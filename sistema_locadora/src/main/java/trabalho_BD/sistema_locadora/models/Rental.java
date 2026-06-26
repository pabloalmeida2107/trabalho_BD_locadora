package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import trabalho_BD.sistema_locadora.Enum.RentalStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "rental")
@NoArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JoinColumn(name = "customer_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @JoinColumn(name = "copy_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Copy copy;

    @Column(name = "rented_at", nullable = false)
    private LocalDate rentedAt;

    @Column(name = "returned_at")
    private LocalDate returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalStatus rentalStatus;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

}
