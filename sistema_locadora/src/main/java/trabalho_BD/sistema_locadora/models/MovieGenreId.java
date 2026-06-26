package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieGenreId implements Serializable {

    private UUID movieId;
    private UUID genreId;
}
