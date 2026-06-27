package trabalho_BD.sistema_locadora.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trabalho_BD.sistema_locadora.requestDTO.CopyRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.CopyResponseDTO;
import trabalho_BD.sistema_locadora.service.CopyService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Cópias")
@RestController
@RequestMapping("/api/copies")
@RequiredArgsConstructor
public class CopyController {

    private final CopyService copyService;

    @Operation(summary = "Cadastrar cópia física")
    @PostMapping
    public ResponseEntity<CopyResponseDTO> create(@Valid @RequestBody CopyRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(copyService.create(data));
    }

    @Operation(summary = "Listar todas as cópias")
    @GetMapping
    public ResponseEntity<List<CopyResponseDTO>> findAll() {
        return ResponseEntity.ok(copyService.findAll());
    }

    @Operation(summary = "Buscar cópia por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CopyResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(copyService.findById(id));
    }

    @Operation(summary = "Atualizar dados da cópia")
    @PutMapping("/{id}")
    public ResponseEntity<CopyResponseDTO> update(@PathVariable UUID id,
                                                  @Valid @RequestBody CopyRequestDTO data) {
        return ResponseEntity.ok(copyService.update(id, data));
    }

    @Operation(summary = "Contar cópias disponíveis de um filme")
    @GetMapping("/available-count/{movieId}")
    public ResponseEntity<Long> countAvailable(@PathVariable UUID movieId) {
        return ResponseEntity.ok(copyService.countAvailableCopiesByMovie(movieId));
    }

    @Operation(summary = "Excluir cópia")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        copyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
