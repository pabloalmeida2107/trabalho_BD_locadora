package trabalho_BD.sistema_locadora.responseDTO;

import java.util.List;
import java.util.UUID;

public record MovieResponseDTO(
        UUID id,
        String title,
        String sinopse,
        Integer releaseYear,
        Integer durationMin,
        Integer rating,
        String genre

) {
}
