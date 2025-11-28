package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.backend.entitiesTF.EvaluacionPsicologo;

import java.util.List;

public interface EvaluacionPsicologoRepository extends JpaRepository<EvaluacionPsicologo, Long> {

    // Query para obtener el promedio de calificaciones (puntaje)
    @Query("SELECT AVG(e.Puntaje) FROM EvaluacionPsicologo e WHERE e.Puntaje IS NOT NULL")
    Double obtenerPromedioCalificacion();
    
    // Query para obtener el promedio de calificaciones de un psicólogo específico
    @Query("SELECT AVG(e.Puntaje) FROM EvaluacionPsicologo e WHERE e.psicologo.psicologoId = :psicologoId AND e.Puntaje IS NOT NULL")
    Double obtenerPromedioCalificacionPorPsicologo(Long psicologoId);
    
    // Query para contar evaluaciones de un psicólogo
    @Query("SELECT COUNT(e) FROM EvaluacionPsicologo e WHERE e.psicologo.psicologoId = :psicologoId")
    Long countByPsicologo_PsicologoId(Long psicologoId);
}
