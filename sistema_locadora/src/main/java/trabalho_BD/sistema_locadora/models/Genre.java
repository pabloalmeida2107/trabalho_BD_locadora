package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "genre")
@Setter
@NoArgsConstructor

public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    public Genre(String name, UUID id) {
        this.name = name;
        this.id = id;
    }
}
