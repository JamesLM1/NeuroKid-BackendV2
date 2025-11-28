package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.Psicologo;

import java.util.List;
import java.util.Optional;

public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    List<Psicologo> findByEspecialidad(String especialidad);
    
    // Buscar psicólogo por email (username)
    Optional<Psicologo> findByEmail(String email);
}
