package trabalho_BD.sistema_locadora.requestDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRequestDTO {

    @NotBlank(message = "O título do filme é obrigatório.")
    private String title;

    @NotBlank(message = "A sinopse é obrigatória.")
    private String sinopse;

    @NotNull(message = "O ano de lançamento é obrigatório.")
    @Positive(message = "Ano inválido.")
    private Integer releaseYear;

    @NotNull(message = "A duração é obrigatória.")
    @Positive(message = "Duração deve ser maior que zero.")
    private Integer durationMin;

    @NotNull(message = "A classificação indicativa é obrigatória.")
    @Positive(message = "A classificação deve ser maior que zero.")
    private Integer rating;

    private UUID genreId;

    // Ignorado na desserialização JSON (endpoint de update).
    // Preenchido pelo binding multipart no endpoint de criação.
    @JsonIgnore
    private MultipartFile coverImage;
}
