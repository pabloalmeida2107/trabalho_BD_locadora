package trabalho_BD.sistema_locadora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trabalho_BD.sistema_locadora.models.Copy;

import java.util.UUID;

public interface CopyRepository extends JpaRepository<Copy, UUID> {
}
