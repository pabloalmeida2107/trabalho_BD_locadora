package trabalho_BD.sistema_locadora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import trabalho_BD.sistema_locadora.config.SecurityConfig;
import trabalho_BD.sistema_locadora.exception.GlobalExceptionHandler;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.requestDTO.PaymentRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.PaymentResponseDTO;
import trabalho_BD.sistema_locadora.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PaymentService paymentService;

    private UUID paymentId;
    private UUID rentalId;
    private PaymentResponseDTO responseDTO;
    private PaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        rentalId = UUID.randomUUID();

        responseDTO = new PaymentResponseDTO(
                paymentId,
                rentalId,
                new BigDecimal("15.00"),
                LocalDate.of(2025, 1, 12),
                "PIX",
                "Pagamento do aluguel"
        );

        requestDTO = new PaymentRequestDTO(rentalId, new BigDecimal("15.00"), "PIX", "Pagamento do aluguel");
    }

    @Test
    void create_deveRetornar201_quandoRequestValida() throws Exception {
        when(paymentService.create(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.amount").value(15.00))
                .andExpect(jsonPath("$.method").value("PIX"));
    }

    @Test
    void create_deveRetornar400_quandoValorNulo() throws Exception {
        var invalido = new PaymentRequestDTO(rentalId, null, "PIX", null);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar400_quandoValorNegativo() throws Exception {
        var invalido = new PaymentRequestDTO(rentalId, new BigDecimal("-5.00"), "PIX", null);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar400_quandoMetodoVazio() throws Exception {
        var invalido = new PaymentRequestDTO(rentalId, new BigDecimal("15.00"), "", null);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar404_quandoAluguelNaoEncontrado() throws Exception {
        when(paymentService.create(any())).thenThrow(new ResourceNotFoundException("Aluguel não encontrado!"));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Aluguel não encontrado!"));
    }

    @Test
    void findAll_deveRetornar200_comLista() throws Exception {
        when(paymentService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].method").value("PIX"));
    }

    @Test
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(paymentService.findById(paymentId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(15.00))
                .andExpect(jsonPath("$.paidAt").value("2025-01-12"));
    }

    @Test
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(paymentService.findById(paymentId))
                .thenThrow(new ResourceNotFoundException("Pagamento não encontrado!"));

        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pagamento não encontrado!"));
    }

    @Test
    void findByRental_deveRetornar200_comListaDoPagamentos() throws Exception {
        when(paymentService.findByRentalId(rentalId)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/payments/rental/{rentalId}", rentalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].method").value("PIX"));
    }

    @Test
    void findByRental_deveRetornar404_quandoAluguelNaoEncontrado() throws Exception {
        when(paymentService.findByRentalId(rentalId))
                .thenThrow(new ResourceNotFoundException("Aluguel não encontrado!"));

        mockMvc.perform(get("/api/payments/rental/{rentalId}", rentalId))
                .andExpect(status().isNotFound());
    }

    @Test
    void totalByRental_deveRetornar200_comSoma() throws Exception {
        when(paymentService.sumAmountByRentalId(rentalId)).thenReturn(new BigDecimal("30.00"));

        mockMvc.perform(get("/api/payments/rental/{rentalId}/total", rentalId))
                .andExpect(status().isOk())
                .andExpect(content().string("30.00"));
    }

    @Test
    void update_deveRetornar200_comDadosAtualizados() throws Exception {
        var atualizado = new PaymentResponseDTO(paymentId, rentalId, new BigDecimal("20.00"),
                LocalDate.of(2025, 1, 12), "CARTÃO", "Atualizado");
        when(paymentService.update(eq(paymentId), any())).thenReturn(atualizado);

        mockMvc.perform(put("/api/payments/{id}", paymentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("CARTÃO"))
                .andExpect(jsonPath("$.amount").value(20.00));
    }

    @Test
    void delete_deveRetornar204_quandoDeletado() throws Exception {
        doNothing().when(paymentService).delete(paymentId);

        mockMvc.perform(delete("/api/payments/{id}", paymentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("Pagamento não encontrado!")).when(paymentService).delete(paymentId);

        mockMvc.perform(delete("/api/payments/{id}", paymentId))
                .andExpect(status().isNotFound());
    }
}
