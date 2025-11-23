package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.Psicologo;

import java.util.List;

public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    List<Psicologo> findByEspecialidad(String especialidad);
}
