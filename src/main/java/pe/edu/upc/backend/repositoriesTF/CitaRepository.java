package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upc.backend.entitiesTF.Cita;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByAsignacion_Psicologo_PsicologoIdAndFechaAfter(Long psicologoId, LocalDate fecha);
    
    // QUERY SIMPLIFICADA: Buscar citas activas (no finalizadas) para el psicólogo
    // Incluye citas desde la fecha indicada O citas pendientes (incluso si están en el pasado)
    // Excluye estados finalizados: Finalizada, Cancelada, Rechazada
    // Ordena por fecha y hora de inicio
    @Query("SELECT c FROM Cita c WHERE c.asignacion.psicologo.psicologoId = :psicologoId " +
           "AND (c.fecha >= :fechaDesde OR c.estado = 'Pendiente') " +
           "AND c.estado NOT IN ('Finalizada', 'Cancelada', 'Rechazada') " +
           "ORDER BY c.fecha ASC, c.horaInicio ASC")
    List<Cita> findProximasCitasByPsicologo(
        @Param("psicologoId") Long psicologoId,
        @Param("fechaDesde") LocalDate fechaDesde
    );
    
    List<Cita> findByAsignacion_Padre_PadreId(Long padreId);
    List<Cita> findByAsignacion_Psicologo_PsicologoId(Long asignacionId);
    Integer countByAsignacion_AsignacionIdAndEstado(Long asignacionId, String estado);
    
    // NUEVO MÉTODO: Buscar citas ocupadas para calcular disponibilidad
    List<Cita> findByAsignacion_Psicologo_PsicologoIdAndFechaAndEstadoNot(Long psicologoId, LocalDate fecha, String estadoCancelado);
    
    // Método para contar citas por estado
    long countByEstado(String estado);
    
    // Método para contar citas de un psicólogo en una fecha específica
    long countByAsignacion_Psicologo_PsicologoIdAndFecha(Long psicologoId, LocalDate fecha);
    
    // Método para obtener citas de un psicólogo en una fecha específica
    List<Cita> findByAsignacion_Psicologo_PsicologoIdAndFecha(Long psicologoId, LocalDate fecha);
}
