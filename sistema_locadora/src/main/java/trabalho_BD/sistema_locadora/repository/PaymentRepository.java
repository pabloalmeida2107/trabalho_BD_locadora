package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trabalho_BD.sistema_locadora.models.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
