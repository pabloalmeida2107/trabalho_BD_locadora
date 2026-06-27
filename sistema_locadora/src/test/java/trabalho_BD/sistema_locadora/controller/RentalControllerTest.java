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
import trabalho_BD.sistema_locadora.Enum.RentalStatus;
import trabalho_BD.sistema_locadora.config.SecurityConfig;
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.GlobalExceptionHandler;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.requestDTO.RentalRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.RentalResponseDTO;
import trabalho_BD.sistema_locadora.service.RentalService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RentalController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class RentalControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RentalService rentalService;

    private UUID rentalId;
    private UUID customerId;
    private UUID copyId;
    private RentalResponseDTO responseDTO;
    private RentalRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        rentalId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        copyId = UUID.randomUUID();

        responseDTO = new RentalResponseDTO(
                rentalId,
                customerId,
                "João Silva",
                copyId,
                "Matrix",
                LocalDate.of(2025, 1, 10),
                null,
                LocalDate.of(2025, 1, 17),
                RentalStatus.IN_PROGRESS,
                List.of()
        );

        requestDTO = new RentalRequestDTO(customerId, copyId, LocalDate.of(2025, 1, 17));
    }

    @Test
    void create_deveRetornar201_quandoRequestValida() throws Exception {
        when(rentalService.createRental(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(rentalId.toString()))
                .andExpect(jsonPath("$.customerName").value("João Silva"))
                .andExpect(jsonPath("$.movieTitle").value("Matrix"))
                .andExpect(jsonPath("$.rentalStatus").value("IN_PROGRESS"));
    }

    @Test
    void create_deveRetornar400_quandoClienteIdNulo() throws Exception {
        var invalido = new RentalRequestDTO(null, copyId, LocalDate.of(2025, 1, 17));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar400_quandoCopiaIndisponivel() throws Exception {
        when(rentalService.createRental(any()))
                .thenThrow(new BusinessException("Esta cópia não está disponível para aluguel!"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Esta cópia não está disponível para aluguel!"));
    }

    @Test
    void findAll_deveRetornar200_comLista() throws Exception {
        when(rentalService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].movieTitle").value("Matrix"));
    }

    @Test
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(rentalService.findById(rentalId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/rentals/{id}", rentalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalStatus").value("IN_PROGRESS"));
    }

    @Test
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(rentalService.findById(rentalId))
                .thenThrow(new ResourceNotFoundException("Aluguel não encontrado!"));

        mockMvc.perform(get("/api/rentals/{id}", rentalId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Aluguel não encontrado!"));
    }

    @Test
    void returnMovie_deveRetornar200_comStatusAtualizado() throws Exception {
        var devolvido = new RentalResponseDTO(
                rentalId, customerId, "João Silva", copyId, "Matrix",
                LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 1, 17), RentalStatus.RETURNED, List.of()
        );
        when(rentalService.returnMovie(rentalId)).thenReturn(devolvido);

        mockMvc.perform(patch("/api/rentals/{id}/return", rentalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalStatus").value("RETURNED"))
                .andExpect(jsonPath("$.returnedAt").value("2025-01-15"));
    }

    @Test
    void returnMovie_deveRetornar400_quandoJaDevolvido() throws Exception {
        when(rentalService.returnMovie(rentalId))
                .thenThrow(new BusinessException("Este aluguel já foi encerrado!"));

        mockMvc.perform(patch("/api/rentals/{id}/return", rentalId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Este aluguel já foi encerrado!"));
    }

    @Test
    void findHistorico_deveRetornar200_comListaDoCliente() throws Exception {
        when(rentalService.findHistoricoByCustomerId(customerId)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/rentals/historico/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerName").value("João Silva"));
    }

    @Test
    void findHistorico_deveRetornar404_quandoClienteNaoEncontrado() throws Exception {
        when(rentalService.findHistoricoByCustomerId(customerId))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado!"));

        mockMvc.perform(get("/api/rentals/historico/{customerId}", customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_deveRetornar204_quandoCancelado() throws Exception {
        doNothing().when(rentalService).delete(rentalId);

        mockMvc.perform(delete("/api/rentals/{id}", rentalId))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("Aluguel não encontrado!")).when(rentalService).delete(rentalId);

        mockMvc.perform(delete("/api/rentals/{id}", rentalId))
                .andExpect(status().isNotFound());
    }
}
