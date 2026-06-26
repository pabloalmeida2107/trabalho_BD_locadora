package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trabalho_BD.sistema_locadora.models.Rental;

import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, UUID> {
}
