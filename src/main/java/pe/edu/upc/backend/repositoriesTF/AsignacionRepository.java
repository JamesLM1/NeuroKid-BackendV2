package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.Asignacion;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    Optional<Asignacion> findByMenor_MenorIdAndPsicologo_PsicologoIdAndEstado(Long menorId, Long psicologoId, String estado);
}
