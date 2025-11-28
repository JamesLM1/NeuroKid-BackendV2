package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADREEvaluacionPsicologoDTO {
    private Long evaluacionId;
    private Long padreId;      // ID del padre que evalúa
    private Long psicologoId;  // ID del psicólogo evaluado
    private Integer puntaje;      // 1 a 5
    private String comentario;
    private LocalDate fechaEvaluacion;
}

//(Para Creación)