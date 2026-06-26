package trabalho_BD.sistema_locadora.requestDTO;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RentalRequestDTO(
        @NotNull(message = "O cliente é obrigatório.")
        UUID customerId,

        @NotNull(message = "A cópia física do filme é obrigatória.")
        UUID copyId,

        @NotNull(message = "A data limite de devolução é obrigatória.")
        LocalDate dueDate
) {
}
