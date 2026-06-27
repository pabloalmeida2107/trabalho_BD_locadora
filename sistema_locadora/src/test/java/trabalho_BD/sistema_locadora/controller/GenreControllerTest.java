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
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.GlobalExceptionHandler;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.requestDTO.GenreRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.GenreResponseDTO;
import trabalho_BD.sistema_locadora.service.GenreService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GenreController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class GenreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    GenreService genreService;

    private UUID id;
    private GenreResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        responseDTO = new GenreResponseDTO(id, "Ação");
    }

    @Test
    void create_deveRetornar201_quandoRequestValida() throws Exception {
        when(genreService.create(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GenreRequestDTO("Ação"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Ação"));
    }

    @Test
    void create_deveRetornar400_quandoNomeVazio() throws Exception {
        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GenreRequestDTO(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar400_quandoGenreDuplicado() throws Exception {
        when(genreService.create(any())).thenThrow(new BusinessException("Já existe um gênero com o nome 'Ação'."));

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GenreRequestDTO("Ação"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Já existe um gênero com o nome 'Ação'."));
    }

    @Test
    void findAll_deveRetornar200_comLista() throws Exception {
        when(genreService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Ação"));
    }

    @Test
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(genreService.findById(id)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/genres/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Ação"));
    }

    @Test
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(genreService.findById(id)).thenThrow(new ResourceNotFoundException("Gênero não encontrado!"));

        mockMvc.perform(get("/api/genres/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Gênero não encontrado!"));
    }

    @Test
    void update_deveRetornar200_comDadosAtualizados() throws Exception {
        var atualizado = new GenreResponseDTO(id, "Drama");
        when(genreService.update(eq(id), any())).thenReturn(atualizado);

        mockMvc.perform(put("/api/genres/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GenreRequestDTO("Drama"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drama"));
    }

    @Test
    void delete_deveRetornar204_quandoDeletado() throws Exception {
        doNothing().when(genreService).delete(id);

        mockMvc.perform(delete("/api/genres/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Gênero não encontrado!"))
                .when(genreService).delete(id);

        mockMvc.perform(delete("/api/genres/{id}", id))
                .andExpect(status().isNotFound());
    }
}
