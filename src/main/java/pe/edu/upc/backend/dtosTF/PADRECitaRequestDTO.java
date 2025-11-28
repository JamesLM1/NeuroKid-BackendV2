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
public class PADRECitaRequestDTO { // DTO usado por el Padre para solicitar una cita
    private Long asignacionId; // FK de la asignación activa (Menor + Psicólogo)
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;
    private String motivo;
    private String estado;
}

//(Para Solicitud de Cita)
