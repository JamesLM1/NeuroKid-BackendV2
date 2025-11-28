package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.Asignacion;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    Optional<Asignacion> findByMenor_MenorIdAndPsicologo_PsicologoIdAndEstado(Long menorId, Long psicologoId, String estado);
    
    // Método para buscar asignación activa entre menor y psicólogo (sin importar el estado específico)
    Optional<Asignacion> findByMenor_MenorIdAndPsicologo_PsicologoId(Long menorId, Long psicologoId);
    
    // Método para buscar asignaciones por padre (para validaciones de seguridad)
    List<Asignacion> findByPadre_PadreId(Long padreId);
    
    // Método para contar asignaciones activas de un psicólogo
    long countByPsicologo_PsicologoIdAndEstado(Long psicologoId, String estado);
    
    // Método para obtener todas las asignaciones de un psicólogo
    List<Asignacion> findByPsicologo_PsicologoId(Long psicologoId);
}
