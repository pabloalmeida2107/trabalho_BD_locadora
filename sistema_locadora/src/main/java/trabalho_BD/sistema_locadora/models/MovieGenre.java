package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "movie_genre")
public class MovieGenre {

    @EmbeddedId
    private MovieGenreId id = new MovieGenreId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreId")
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public MovieGenre(Movie movie, Genre genre) {
        this.movie = movie;
        this.genre = genre;
        this.id = new MovieGenreId(movie.getId(), genre.getId());
    }
}