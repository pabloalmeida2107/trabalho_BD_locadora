package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trabalho_BD.sistema_locadora.models.Copy;

import java.util.UUID;

public interface CopyRepository extends JpaRepository<Copy, UUID> {


    @Query("SELECT COUNT(c) FROM Copy c WHERE c.movie.id = :movieId AND c.availabilityStatus = trabalho_BD.sistema_locadora.Enum.AvailabilityStatus.AVAILABLE")
    Long countAvailableCopiesByMovie(@Param("movieId") UUID movieId);
}
