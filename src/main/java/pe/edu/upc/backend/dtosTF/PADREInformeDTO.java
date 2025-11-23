package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADREInformeDTO {
    private Long informeId;
    private Long menorId;
    private String nombreMenor;
    private Integer mes;
    private Integer anio;
    private String resumen;
}

//(Para Lectura de Progreso)