package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trabalho_BD.sistema_locadora.models.Rental;

import java.util.List;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<
        Rental, UUID> {


    @Query("SELECT r FROM Rental r JOIN r.customer c WHERE c.id = :customerId ORDER BY r.rentedAt DESC")
    List<Rental> findHistoricoByCustomerId(@Param("customerId") UUID customerId);
}
