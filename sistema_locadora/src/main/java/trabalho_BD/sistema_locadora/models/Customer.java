package trabalho_BD.sistema_locadora.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "cpf",nullable = false)
    private String cpf;

    @Column(name = "phone",nullable = false)
    private String phone;

    @Column(name = "email",nullable = false)
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentals = new ArrayList<>();


}
