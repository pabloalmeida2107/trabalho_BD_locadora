package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import trabalho_BD.sistema_locadora.Enum.AvailabilityStatus;
import trabalho_BD.sistema_locadora.Enum.Format;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "copy")
@Getter
@Setter
@NoArgsConstructor
public class Copy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JoinColumn(name = "movie_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private Format format;

    @OneToMany(mappedBy = "copy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentals = new ArrayList<>();

}
