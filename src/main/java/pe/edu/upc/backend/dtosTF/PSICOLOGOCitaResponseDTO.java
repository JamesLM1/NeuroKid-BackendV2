package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PSICOLOGOCitaResponseDTO {

    private Long citaId;

    // Información de la Asignación
    private Long asignacionId;

    // Información del Psicólogo (opcional, pero útil para coherencia)
    private Long psicologoId;
    private String nombrePsicologo;

    // Información del Padre que solicitó la cita
    private Long padreId;
    private String nombreCompletoPadre;
    private String emailPadre;

    // Información del Menor
    private Long menorId;
    private String nombreCompletoMenor;
    private Date fechaNacimientoMenor;

    // Detalles de la Cita
    private Date fechaHoraCita; // Usamos LocalDateTime para precisión
    private String motivoCita;
    private String estado; // Pendiente, Confirmada, Rechazada, Cancelada, Finalizada

    // Campos generados por el Psicólogo
    private String hallazgos; // Notas o hallazgos registrados al finalizar la cita
}
