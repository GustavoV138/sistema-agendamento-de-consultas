package dev.gustavovs.maisspringsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitAndPayloadFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_SIZE = 10 * 1024; // 10 KB

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final int generalLimit;
    private final int authLimit;

    private final Map<String, RequestCounter> counters = new ConcurrentHashMap<>();

    public RateLimitAndPayloadFilter(
            @Value("${rate-limit.general:60}") int generalLimit,
            @Value("${rate-limit.auth:10}") int authLimit) {
        this.generalLimit = generalLimit;
        this.authLimit = authLimit;
    }

    private static class RequestCounter {
        final long windowStart;
        final AtomicInteger count;

        RequestCounter(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. /health e OPTIONS ficam isentos
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(method) || "/health".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Limite de Payload (10 KB)
        long contentLength = request.getContentLengthLong();
        if (contentLength > MAX_PAYLOAD_SIZE) {
            sendProblemDetail(response, HttpStatus.PAYLOAD_TOO_LARGE, "Payload Too Large",
                    "Request payload exceeds the maximum limit of 10 KB", path, null);
            return;
        }

        // Para requisições sem Content-Length definido ou chunked com body
        HttpServletRequest requestToUse = request;
        if (("POST".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))) {
            try {
                BoundedRequestWrapper wrapper = new BoundedRequestWrapper(request, MAX_PAYLOAD_SIZE);
                requestToUse = wrapper;
            } catch (PayloadTooLargeException e) {
                sendProblemDetail(response, HttpStatus.PAYLOAD_TOO_LARGE, "Payload Too Large",
                        "Request payload exceeds the maximum limit of 10 KB", path, null);
                return;
            }
        }

        // 3. Rate Limiting por IP
        String clientIp = extractClientIp(request);
        boolean isAuthEndpoint = path.equals("/auth/login") || path.equals("/auth/register");
        int limit = isAuthEndpoint ? authLimit : generalLimit;
        String key = (isAuthEndpoint ? "auth:" : "gen:") + clientIp;

        long now = System.currentTimeMillis();
        RequestCounter counter = counters.compute(key, (k, existing) -> {
            if (existing == null || now - existing.windowStart >= 60000) {
                return new RequestCounter(now, new AtomicInteger(1));
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (counter.count.get() > limit) {
            long elapsed = now - counter.windowStart;
            long retryAfterSec = Math.max(1, (60000 - elapsed) / 1000);
            response.setHeader("Retry-After", String.valueOf(retryAfterSec));
            sendProblemDetail(response, HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests",
                    "Rate limit exceeded. Please try again later.", path, retryAfterSec);
            return;
        }

        filterChain.doFilter(requestToUse, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendProblemDetail(HttpServletResponse response, HttpStatus status, String title, String detail, String path, Long retryAfter)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setInstance(URI.create(path));

        response.getWriter().write(objectMapper.writeValueAsString(pd));
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupOldCounters() {
        long now = System.currentTimeMillis();
        counters.entrySet().removeIf(entry -> now - entry.getValue().windowStart > 120000);
    }

    private static class PayloadTooLargeException extends IOException {
        PayloadTooLargeException(String msg) {
            super(msg);
        }
    }

    private static class BoundedRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] body;

        BoundedRequestWrapper(HttpServletRequest request, int maxBytes) throws IOException {
            super(request);
            InputStream is = request.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            int total = 0;
            while ((read = is.read(buffer)) != -1) {
                total += read;
                if (total > maxBytes) {
                    throw new PayloadTooLargeException("Payload exceeds " + maxBytes + " bytes");
                }
                baos.write(buffer, 0, read);
            }
            this.body = baos.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return bais.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {}

                @Override
                public int read() {
                    return bais.read();
                }
            };
        }
    }
}
