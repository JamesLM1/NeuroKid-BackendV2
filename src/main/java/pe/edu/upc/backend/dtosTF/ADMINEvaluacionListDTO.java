package pe.edu.upc.backend.dtosTF;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADMINEvaluacionListDTO {
    private Long evaluacionId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaEvaluacion;
    
    private Integer puntaje;
    private String comentario;
    
    // Nombres completos para la vista de lista
    private String nombrePsicologo;
    private String nombrePadre;
}

