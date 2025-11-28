package pe.edu.upc.backend.dtosTF;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimientoMenor;

    // Detalles de la Cita
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaHoraCita;
    private String motivoCita;
    private String estado; // Pendiente, Confirmada, Rechazada, Cancelada, Finalizada

    // Campos generados por el Psicólogo
    private String hallazgos; // Notas o hallazgos registrados al finalizar la cita
}
