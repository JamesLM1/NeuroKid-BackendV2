package pe.edu.upc.backend.dtosTF;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADRECitaResponseDTO { // DTO de respuesta que incluye el estado y hallazgos
    private Long citaId;
    private Long asignacionId;
    private String nombreMenor; // Dato extraído para el dashboard
    private String nombrePsicologo; // Dato extraído para el dashboard
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;
    
    private String motivo;
    private String hallazgos; // Llenado por el Psicólogo
    private String estado; // Estado de la cita (Pendiente, Atendida, Cancelada)
}

//(Para Historial/Próximas Citas)