package trabalho_BD.sistema_locadora.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trabalho_BD.sistema_locadora.requestDTO.GenreRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.GenreResponseDTO;
import trabalho_BD.sistema_locadora.service.GenreService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Gêneros")
@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @Operation(summary = "Cadastrar gênero")
    @PostMapping
    public ResponseEntity<GenreResponseDTO> create(@Valid @RequestBody GenreRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(data));
    }

    @Operation(summary = "Listar todos os gêneros")
    @GetMapping
    public ResponseEntity<List<GenreResponseDTO>> findAll() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @Operation(summary = "Buscar gênero por ID")
    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(genreService.findById(id));
    }

    @Operation(summary = "Atualizar gênero")
    @PutMapping("/{id}")
    public ResponseEntity<GenreResponseDTO> update(@PathVariable UUID id,
                                                   @Valid @RequestBody GenreRequestDTO data) {
        return ResponseEntity.ok(genreService.update(id, data));
    }

    @Operation(summary = "Excluir gênero")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
