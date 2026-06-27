package trabalho_BD.sistema_locadora.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trabalho_BD.sistema_locadora.requestDTO.MovieRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.MovieResponseDTO;
import trabalho_BD.sistema_locadora.service.MovieService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Filmes")
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Cadastrar filme (com imagem de capa opcional)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponseDTO> create(@Valid @ModelAttribute MovieRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(data));
    }

    @Operation(summary = "Listar todos os filmes")
    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> findAll() {
        return ResponseEntity.ok(movieService.findAll());
    }

    @Operation(summary = "Buscar filme por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @Operation(summary = "Pesquisar filmes por título")
    @GetMapping("/search")
    public ResponseEntity<List<MovieResponseDTO>> search(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchMovies(title));
    }

    @Operation(summary = "Substituir imagem de capa do filme")
    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponseDTO> uploadCover(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(movieService.uploadCover(id, file));
    }

    @Operation(summary = "Obter imagem de capa do filme")
    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> getCover(@PathVariable UUID id) {
        byte[] image = movieService.getCoverImage(id);
        String contentType = movieService.getCoverContentType(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }

    @Operation(summary = "Atualizar dados do filme")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieResponseDTO> update(@PathVariable UUID id,
                                                   @Valid @RequestBody MovieRequestDTO data) {
        return ResponseEntity.ok(movieService.updateMovie(id, data));
    }

    @Operation(summary = "Excluir filme")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
