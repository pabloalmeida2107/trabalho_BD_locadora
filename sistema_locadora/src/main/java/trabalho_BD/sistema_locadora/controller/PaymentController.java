package trabalho_BD.sistema_locadora.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trabalho_BD.sistema_locadora.requestDTO.PaymentRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.PaymentResponseDTO;
import trabalho_BD.sistema_locadora.service.PaymentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Pagamentos")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Registrar pagamento")
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(data));
    }

    @Operation(summary = "Listar todos os pagamentos")
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @Operation(summary = "Buscar pagamento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @Operation(summary = "Listar pagamentos de um aluguel")
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<List<PaymentResponseDTO>> findByRental(@PathVariable UUID rentalId) {
        return ResponseEntity.ok(paymentService.findByRentalId(rentalId));
    }

    @Operation(summary = "Total pago em um aluguel")
    @GetMapping("/rental/{rentalId}/total")
    public ResponseEntity<BigDecimal> totalByRental(@PathVariable UUID rentalId) {
        return ResponseEntity.ok(paymentService.sumAmountByRentalId(rentalId));
    }

    @Operation(summary = "Atualizar pagamento")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> update(@PathVariable UUID id,
                                                     @Valid @RequestBody PaymentRequestDTO data) {
        return ResponseEntity.ok(paymentService.update(id, data));
    }

    @Operation(summary = "Excluir pagamento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
