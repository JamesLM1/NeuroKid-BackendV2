package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(String name);
}
