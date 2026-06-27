package trabalho_BD.sistema_locadora.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trabalho_BD.sistema_locadora.requestDTO.RentalRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.RentalResponseDTO;
import trabalho_BD.sistema_locadora.service.RentalService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Aluguéis")
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @Operation(summary = "Registrar novo aluguel")
    @PostMapping
    public ResponseEntity<RentalResponseDTO> create(@Valid @RequestBody RentalRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createRental(data));
    }

    @Operation(summary = "Listar todos os aluguéis")
    @GetMapping
    public ResponseEntity<List<RentalResponseDTO>> findAll() {
        return ResponseEntity.ok(rentalService.findAll());
    }

    @Operation(summary = "Buscar aluguel por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(rentalService.findById(id));
    }

    @Operation(summary = "Registrar devolução do filme")
    @PatchMapping("/{id}/return")
    public ResponseEntity<RentalResponseDTO> returnMovie(@PathVariable UUID id) {
        return ResponseEntity.ok(rentalService.returnMovie(id));
    }

    @Operation(summary = "Histórico de aluguéis de um cliente")
    @GetMapping("/historico/{customerId}")
    public ResponseEntity<List<RentalResponseDTO>> findHistorico(@PathVariable UUID customerId) {
        return ResponseEntity.ok(rentalService.findHistoricoByCustomerId(customerId));
    }

    @Operation(summary = "Cancelar aluguel")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        rentalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
