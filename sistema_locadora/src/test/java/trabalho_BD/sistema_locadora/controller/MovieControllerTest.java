package trabalho_BD.sistema_locadora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import trabalho_BD.sistema_locadora.config.SecurityConfig;
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.GlobalExceptionHandler;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.requestDTO.MovieRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.MovieResponseDTO;
import trabalho_BD.sistema_locadora.service.MovieService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class MovieControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MovieService movieService;

    private UUID id;
    private UUID genreId;
    private MovieResponseDTO responseDTO;
    private MovieResponseDTO responseDTOComCapa;
    private MovieRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        genreId = UUID.randomUUID();

        responseDTO = new MovieResponseDTO(id, "Matrix", "Sinopse aqui", 1999, 136, 14, "Ficção Científica", null);

        responseDTOComCapa = new MovieResponseDTO(id, "Matrix", "Sinopse aqui", 1999, 136, 14, "Ficção Científica",
                "/api/movies/" + id + "/cover");

        updateRequestDTO = MovieRequestDTO.builder()
                .title("Matrix")
                .sinopse("Sinopse aqui")
                .releaseYear(1999)
                .durationMin(136)
                .rating(14)
                .genreId(genreId)
                .build();
    }

    // ── Criação (multipart/form-data) ─────────────────────────────────────────

    @Test
    void create_deveRetornar201_semImagem() throws Exception {
        when(movieService.createMovie(any())).thenReturn(responseDTO);

        mockMvc.perform(multipart("/api/movies")
                        .param("title", "Matrix")
                        .param("sinopse", "Sinopse aqui")
                        .param("releaseYear", "1999")
                        .param("durationMin", "136")
                        .param("rating", "14")
                        .param("genreId", genreId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Matrix"))
                .andExpect(jsonPath("$.coverImageUrl").doesNotExist());
    }

    @Test
    void create_deveRetornar201_comImagem() throws Exception {
        when(movieService.createMovie(any())).thenReturn(responseDTOComCapa);

        var imagem = new MockMultipartFile("coverImage", "capa.jpg", "image/jpeg", "bytes-fake".getBytes());

        mockMvc.perform(multipart("/api/movies")
                        .file(imagem)
                        .param("title", "Matrix")
                        .param("sinopse", "Sinopse aqui")
                        .param("releaseYear", "1999")
                        .param("durationMin", "136")
                        .param("rating", "14")
                        .param("genreId", genreId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.coverImageUrl").value("/api/movies/" + id + "/cover"));
    }

    @Test
    void create_deveRetornar400_quandoTituloVazio() throws Exception {
        mockMvc.perform(multipart("/api/movies")
                        .param("title", "")
                        .param("sinopse", "Sinopse")
                        .param("releaseYear", "1999")
                        .param("durationMin", "136")
                        .param("rating", "14")
                        .param("genreId", genreId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar400_quandoFormatoImagemInvalido() throws Exception {
        when(movieService.createMovie(any()))
                .thenThrow(new BusinessException("Formato inválido. Envie uma imagem JPEG, PNG, GIF ou WebP."));

        var pdf = new MockMultipartFile("coverImage", "doc.pdf", "application/pdf", "bytes".getBytes());

        mockMvc.perform(multipart("/api/movies")
                        .file(pdf)
                        .param("title", "Matrix")
                        .param("sinopse", "Sinopse")
                        .param("releaseYear", "1999")
                        .param("durationMin", "136")
                        .param("rating", "14")
                        .param("genreId", genreId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Formato inválido. Envie uma imagem JPEG, PNG, GIF ou WebP."));
    }

    @Test
    void create_deveRetornar404_quandoGenreNaoEncontrado() throws Exception {
        when(movieService.createMovie(any())).thenThrow(new ResourceNotFoundException("Gênero não encontrado!"));

        mockMvc.perform(multipart("/api/movies")
                        .param("title", "Matrix")
                        .param("sinopse", "Sinopse")
                        .param("releaseYear", "1999")
                        .param("durationMin", "136")
                        .param("rating", "14")
                        .param("genreId", genreId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Gênero não encontrado!"));
    }

    // ── Leitura ───────────────────────────────────────────────────────────────

    @Test
    void findAll_deveRetornar200_comLista() throws Exception {
        when(movieService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Matrix"));
    }

    @Test
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(movieService.getMovieById(id)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Matrix"))
                .andExpect(jsonPath("$.releaseYear").value(1999));
    }

    @Test
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(movieService.getMovieById(id)).thenThrow(new ResourceNotFoundException("Filme não encontrado!"));

        mockMvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void search_deveRetornar200_comResultados() throws Exception {
        when(movieService.searchMovies("Matrix")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/movies/search").param("title", "Matrix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Matrix"));
    }

    @Test
    void search_deveRetornar200_comListaVazia_quandoNaoEncontrado() throws Exception {
        when(movieService.searchMovies("XYZ")).thenReturn(List.of());

        mockMvc.perform(get("/api/movies/search").param("title", "XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── Capa ─────────────────────────────────────────────────────────────────

    @Test
    void uploadCover_deveRetornar200_comUrlDaCapa() throws Exception {
        when(movieService.uploadCover(eq(id), any())).thenReturn(responseDTOComCapa);
        var file = new MockMultipartFile("file", "capa.jpg", "image/jpeg", "bytes".getBytes());

        mockMvc.perform(multipart("/api/movies/{id}/cover", id).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coverImageUrl").value("/api/movies/" + id + "/cover"));
    }

    @Test
    void uploadCover_deveRetornar400_quandoFormatoInvalido() throws Exception {
        when(movieService.uploadCover(eq(id), any()))
                .thenThrow(new BusinessException("Formato inválido. Envie uma imagem JPEG, PNG, GIF ou WebP."));
        var file = new MockMultipartFile("file", "doc.pdf", "application/pdf", "bytes".getBytes());

        mockMvc.perform(multipart("/api/movies/{id}/cover", id).file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Formato inválido. Envie uma imagem JPEG, PNG, GIF ou WebP."));
    }

    @Test
    void uploadCover_deveRetornar404_quandoFilmeNaoEncontrado() throws Exception {
        when(movieService.uploadCover(eq(id), any()))
                .thenThrow(new ResourceNotFoundException("Filme não encontrado!"));
        var file = new MockMultipartFile("file", "capa.jpg", "image/jpeg", "bytes".getBytes());

        mockMvc.perform(multipart("/api/movies/{id}/cover", id).file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCover_deveRetornar200_comBytesEContentType() throws Exception {
        byte[] imageBytes = "imagem-fake".getBytes();
        when(movieService.getCoverImage(id)).thenReturn(imageBytes);
        when(movieService.getCoverContentType(id)).thenReturn("image/jpeg");

        mockMvc.perform(get("/api/movies/{id}/cover", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void getCover_deveRetornar404_quandoFilmeSemCapa() throws Exception {
        when(movieService.getCoverImage(id))
                .thenThrow(new ResourceNotFoundException("Este filme não possui imagem de capa."));

        mockMvc.perform(get("/api/movies/{id}/cover", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Este filme não possui imagem de capa."));
    }

    // ── Update (JSON) ─────────────────────────────────────────────────────────

    @Test
    void update_deveRetornar200_comDadosAtualizados() throws Exception {
        var atualizado = new MovieResponseDTO(id, "Matrix Reloaded", "Sinopse 2", 2003, 138, 14, "Ficção Científica", null);
        when(movieService.updateMovie(eq(id), any())).thenReturn(atualizado);

        mockMvc.perform(put("/api/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Matrix Reloaded"));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_deveRetornar204_quandoDeletado() throws Exception {
        doNothing().when(movieService).deleteMovie(id);

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("Filme não encontrado!")).when(movieService).deleteMovie(id);

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().isNotFound());
    }
}
