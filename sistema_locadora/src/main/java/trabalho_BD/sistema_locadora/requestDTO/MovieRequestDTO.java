package trabalho_BD.sistema_locadora.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record MovieRequestDTO(
        @NotBlank(message = "O título do filme é obrigatório.")
        String title,

        @NotBlank(message = "A sinopse é obrigatória.")
        String sinopse,

        @NotNull(message = "O ano de lançamento é obrigatório.")
        @Positive(message = "Ano inválido.")
        Integer releaseYear,

        @NotNull(message = "A duração é obrigatória.")
        @Positive(message = "Duração deve ser maior que zero.")
        Integer durationMin,

        @NotBlank(message = "A classificação indicativa é obrigatória.")
        Integer rating,

        UUID genreId
) {
}
