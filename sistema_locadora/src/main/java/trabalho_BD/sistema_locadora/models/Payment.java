package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "payment")
@NoArgsConstructor
public class Payment {
    @Id
    @Column(name = "payment_id", length = 36)
    private UUID paymentId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_at", nullable = false)
    private LocalDate paidAt;

    @Column(name = "method", length = 46, nullable = false)
    private String method;

    @Column(name = "description", length = 300)
    private String description;
}
