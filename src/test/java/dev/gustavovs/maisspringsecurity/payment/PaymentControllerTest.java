package dev.gustavovs.maisspringsecurity.payment;

import dev.gustavovs.maisspringsecurity.exception.GlobalExceptionHandler;
import dev.gustavovs.maisspringsecurity.exception.exceptions.PaymentNotFoundException;
import dev.gustavovs.maisspringsecurity.payment.dto.request.PaymentRequestDTO;
import dev.gustavovs.maisspringsecurity.payment.dto.request.PaymentUpdateRequestDTO;
import dev.gustavovs.maisspringsecurity.payment.dto.response.PaymentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PaymentController(paymentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void createPayment_Success_Returns200() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO("payment_id", BigDecimal.valueOf(15), PaymentType.PIX, true);

        when(paymentService.createPayment(any(String.class), any(PaymentRequestDTO.class)))
                .thenReturn(response);

        String payload = """
                {
                    "value": 15,
                    "paymentType": "PIX",
                    "isDone": true
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .param("appointmentId", "appointment_id"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(BigDecimal.valueOf(15)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentType").value("PIX"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isDone").value(true));

        verify(paymentService).createPayment(any(String.class), any(PaymentRequestDTO.class));
    }

    @Test
    public void updatePayment_Success_Returns201() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO("payment_id", BigDecimal.valueOf(32), PaymentType.PIX, false);

        when(paymentService.updatePayment(any(String.class), any(PaymentUpdateRequestDTO.class)))
                .thenReturn(response);

        String payload = """
                {
                    "value": 32,
                    "paymentType": "PIX",
                    "isDone": false
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/payment/payment_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(BigDecimal.valueOf(32)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentType").value("PIX"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isDone").value(false));

        verify(paymentService).updatePayment(any(String.class), any(PaymentUpdateRequestDTO.class));
    }

    @Test
    public void updatePayment_PaymentId_NotFound_Returns404() throws Exception {
        when(paymentService.updatePayment(any(String.class), any(PaymentUpdateRequestDTO.class)))
                .thenThrow(new PaymentNotFoundException("patient_id"));

        String payload = """
                {
                    "value": 32,
                    "paymentType": "PIX",
                    "isDone": false
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/payment/payment_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Payment not found."));

        verify(paymentService).updatePayment(any(String.class), any(PaymentUpdateRequestDTO.class));
    }

    @Test
    public void deletePayment_Success_Returns204() throws Exception{
        doNothing().when(paymentService).deletePayment(any(String.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/payment/payment_id"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(paymentService).deletePayment(any(String.class));
    }

    @Test
    public void confirmPayment_Success_Returns201() throws Exception{
        doNothing().when(paymentService).confirmPayment(any(String.class));

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/payment/payment_id/confirm"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}