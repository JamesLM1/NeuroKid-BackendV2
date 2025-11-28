package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PSICOLOGOInformeDTO {

    private Long informeId;
    private Long asignacionId;
    private Long psicologoId;

    private String titulo;
    private String contenido;
    private Date fechaCreacion;
    private Integer calificacionEficacia; // Puntaje del 1 al 5
    
    // Campo enriquecido para mejor visualización
    private String nombreMenor;
}
