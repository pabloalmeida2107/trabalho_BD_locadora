package trabalho_BD.sistema_locadora.requestDTO;

import jakarta.validation.constraints.NotNull;
import trabalho_BD.sistema_locadora.Enum.AvailabilityStatus;
import trabalho_BD.sistema_locadora.Enum.Format;

import java.util.UUID;

public record CopyRequestDTO(
        @NotNull(message = "O ID do filme associado é obrigatório.")
        UUID movieId,

        @NotNull(message = "O status de disponibilidade é obrigatório.")
        AvailabilityStatus availabilityStatus,

        @NotNull(message = "O formato da mídia física é obrigatório.")
        Format format
) {
}
