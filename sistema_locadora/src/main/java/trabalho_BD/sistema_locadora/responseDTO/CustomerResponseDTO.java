package trabalho_BD.sistema_locadora.responseDTO;

import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String name,
        String cpf,
        String phone,
        String email
) {
}
