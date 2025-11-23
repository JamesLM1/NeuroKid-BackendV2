package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
