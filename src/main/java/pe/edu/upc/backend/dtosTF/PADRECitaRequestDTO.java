package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADRECitaRequestDTO { // DTO usado por el Padre para solicitar una cita
    private Long asignacionId; // FK de la asignación activa (Menor + Psicólogo)
    private Date fecha;
    private Time horaInicio;
    private Time horaFin;
    private String motivo;
    private String estado;
}

//(Para Solicitud de Cita)
