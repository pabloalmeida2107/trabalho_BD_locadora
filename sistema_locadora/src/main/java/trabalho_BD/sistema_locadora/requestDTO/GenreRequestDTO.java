package trabalho_BD.sistema_locadora.requestDTO;

import jakarta.validation.constraints.NotBlank;

public record GenreRequestDTO(

        @NotBlank(message = "O nome do gênero é obrigatório.")
        String name

) {
}
