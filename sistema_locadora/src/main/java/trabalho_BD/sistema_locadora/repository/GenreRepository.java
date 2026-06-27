package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trabalho_BD.sistema_locadora.models.Genre;

import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {

}
