package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PSICOLOGODisponibilidadDTO {

    private Long disponibilidadId;
    private Long psicologoId;
    private String diaSemana; // Ej: "LUNES", "MARTES"
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
