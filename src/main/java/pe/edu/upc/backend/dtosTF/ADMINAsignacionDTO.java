package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADMINAsignacionDTO {
    private Long asignacionId;
    private Long padreId;      // Solo el ID es suficiente para crear/actualizar
    private Long menorId;      // Solo el ID es suficiente para crear/actualizar
    private Long psicologoId;  // Solo el ID es suficiente para crear/actualizar
    private LocalDate fechaAsignacion;
    private String estado;
    
    // Campos opcionales para nombres completos (enriquecidos desde el backend)
    private String nombrePadre;
    private String nombreMenor;
    private String nombrePsicologo;
}

//(Asignación)