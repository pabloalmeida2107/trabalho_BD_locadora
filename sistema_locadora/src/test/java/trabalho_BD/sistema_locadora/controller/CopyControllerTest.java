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
import trabalho_BD.sistema_locadora.Enum.AvailabilityStatus;
import trabalho_BD.sistema_locadora.Enum.Format;
import trabalho_BD.sistema_locadora.config.SecurityConfig;
import trabalho_BD.sistema_locadora.exception.GlobalExceptionHandler;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.requestDTO.CopyRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.CopyResponseDTO;
import trabalho_BD.sistema_locadora.service.CopyService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CopyController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class CopyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CopyService copyService;

    private UUID id;
    private UUID movieId;
    private CopyResponseDTO responseDTO;
    private CopyRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        movieId = UUID.randomUUID();
        responseDTO = new CopyResponseDTO(id, movieId, "Matrix", AvailabilityStatus.AVAILABLE, Format.DVD);
        requestDTO = new CopyRequestDTO(movieId, AvailabilityStatus.AVAILABLE, Format.DVD);
    }

    @Test
    void create_deveRetornar201_quandoRequestValida() throws Exception {
        when(copyService.create(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.movieTitle").value("Matrix"))
                .andExpect(jsonPath("$.availabilityStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$.format").value("DVD"));
    }

    @Test
    void create_deveRetornar400_quandoMovieIdNulo() throws Exception {
        var invalido = new CopyRequestDTO(null, AvailabilityStatus.AVAILABLE, Format.DVD);

        mockMvc.perform(post("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar404_quandoFilmeNaoEncontrado() throws Exception {
        when(copyService.create(any())).thenThrow(new ResourceNotFoundException("Filme não encontrado!"));

        mockMvc.perform(post("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_deveRetornar200_comLista() throws Exception {
        when(copyService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/copies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].movieTitle").value("Matrix"));
    }

    @Test
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(copyService.findById(id)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/copies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.format").value("DVD"));
    }

    @Test
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(copyService.findById(id)).thenThrow(new ResourceNotFoundException("Cópia não encontrada!"));

        mockMvc.perform(get("/api/copies/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cópia não encontrada!"));
    }

    @Test
    void update_deveRetornar200_comDadosAtualizados() throws Exception {
        var atualizado = new CopyResponseDTO(id, movieId, "Matrix", AvailabilityStatus.RENTED, Format.BLU_RAY);
        when(copyService.update(eq(id), any())).thenReturn(atualizado);

        mockMvc.perform(put("/api/copies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityStatus").value("RENTED"))
                .andExpect(jsonPath("$.format").value("BLU_RAY"));
    }

    @Test
    void countAvailable_deveRetornar200_comContagem() throws Exception {
        when(copyService.countAvailableCopiesByMovie(movieId)).thenReturn(3L);

        mockMvc.perform(get("/api/copies/available-count/{movieId}", movieId))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void countAvailable_deveRetornar404_quandoFilmeNaoEncontrado() throws Exception {
        when(copyService.countAvailableCopiesByMovie(movieId))
                .thenThrow(new ResourceNotFoundException("Filme não encontrado!"));

        mockMvc.perform(get("/api/copies/available-count/{movieId}", movieId))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_deveRetornar204_quandoDeletado() throws Exception {
        doNothing().when(copyService).delete(id);

        mockMvc.perform(delete("/api/copies/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("Cópia não encontrada!")).when(copyService).delete(id);

        mockMvc.perform(delete("/api/copies/{id}", id))
                .andExpect(status().isNotFound());
    }
}
