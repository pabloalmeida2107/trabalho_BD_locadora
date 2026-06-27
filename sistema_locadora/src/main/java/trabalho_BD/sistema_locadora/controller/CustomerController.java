package trabalho_BD.sistema_locadora.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trabalho_BD.sistema_locadora.requestDTO.CustomerRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.CustomerResponseDTO;
import trabalho_BD.sistema_locadora.service.CustomerService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Clientes")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Cadastrar cliente")
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(data));
    }

    @Operation(summary = "Listar todos os clientes")
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @Operation(summary = "Buscar cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Operation(summary = "Buscar cliente por CPF")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<CustomerResponseDTO> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(customerService.findByCpf(cpf));
    }

    @Operation(summary = "Atualizar dados do cliente")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable UUID id,
                                                      @Valid @RequestBody CustomerRequestDTO data) {
        return ResponseEntity.ok(customerService.update(id, data));
    }

    @Operation(summary = "Excluir cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
