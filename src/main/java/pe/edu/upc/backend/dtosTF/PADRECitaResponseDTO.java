package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADRECitaResponseDTO { // DTO de respuesta que incluye el estado y hallazgos
    private Long citaId;
    private Long asignacionId;
    private String nombreMenor; // Dato extraído para el dashboard
    private String nombrePsicologo; // Dato extraído para el dashboard
    private Date fecha;
    private Time horaInicio;
    private String motivo;
    private String hallazgos; // Llenado por el Psicólogo
    private String estado; // Estado de la cita (Pendiente, Atendida, Cancelada)
}

//(Para Historial/Próximas Citas)