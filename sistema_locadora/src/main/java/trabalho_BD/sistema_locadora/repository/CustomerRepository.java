package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trabalho_BD.sistema_locadora.models.Customer;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
