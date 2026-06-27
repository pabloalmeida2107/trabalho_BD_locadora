package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trabalho_BD.sistema_locadora.models.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {


    @Query("SELECT c FROM Customer c WHERE c.cpf = :cpf")
    Optional<Customer> findByCpf(@Param("cpf") String cpf);
}
