package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String sinopse;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    private Integer durationMin;

    @Column(nullable = false)
    private Integer rating;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Copy> copies = new ArrayList<>();

    public Movie(String title, String sinopse, Integer releaseYear, Integer durationMin, Integer rating) {

        this.title = title;
        this.sinopse = sinopse;
        this.releaseYear = releaseYear;
        this.durationMin = durationMin;
        this.rating = rating;

    }
}
