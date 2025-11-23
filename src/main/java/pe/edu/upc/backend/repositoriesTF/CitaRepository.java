package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.Cita;

import java.util.Date;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByAsignacion_Psicologo_PsicologoIdAndFechaAfter(Long psicologoId, Date fecha);
    List<Cita> findByAsignacion_Padre_PadreId(Long padreId);
    List<Cita> findByAsignacion_Psicologo_PsicologoId(Long asignacionId);
    Integer countByAsignacion_AsignacionIdAndEstado(Long asignacionId, String estado);
}
