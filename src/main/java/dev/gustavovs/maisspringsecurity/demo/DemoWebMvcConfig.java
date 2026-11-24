package dev.gustavovs.maisspringsecurity.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("demo")
public class DemoWebMvcConfig implements WebMvcConfigurer {

    private final DemoTableLimitInterceptor demoTableLimitInterceptor;

    public DemoWebMvcConfig(DemoTableLimitInterceptor demoTableLimitInterceptor) {
        this.demoTableLimitInterceptor = demoTableLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(demoTableLimitInterceptor);
    }
}
