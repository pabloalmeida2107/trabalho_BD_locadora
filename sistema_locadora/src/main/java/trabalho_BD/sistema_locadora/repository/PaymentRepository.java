package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trabalho_BD.sistema_locadora.models.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {


    @Query("SELECT p FROM Payment p WHERE p.rental.id = :rentalId ORDER BY p.paidAt DESC")
    List<Payment> findByRentalId(@Param("rentalId") UUID rentalId);


    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.rental.id = :rentalId")
    BigDecimal sumAmountByRentalId(@Param("rentalId") UUID rentalId);
}
