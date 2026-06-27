package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trabalho_BD.sistema_locadora.models.Movie;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie,UUID> {
    Optional<Movie> findMovieByID(UUID id);

    Optional<Movie> findMovieByTitle(String title);

    Optional<Movie> findByTitleContainingIgnoreCase(String title);
}
