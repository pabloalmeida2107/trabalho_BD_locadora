package trabalho_BD.sistema_locadora.responseDTO;

import trabalho_BD.sistema_locadora.Enum.AvailabilityStatus;
import trabalho_BD.sistema_locadora.Enum.Format;

import java.util.UUID;

public record CopyResponseDTO(
        UUID id,
        UUID movieId,
        String movieTitle,
        AvailabilityStatus availabilityStatus,
        Format format
) {
}
