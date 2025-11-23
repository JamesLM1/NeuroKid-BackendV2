package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADMINAsignacionDTO {
    private Long asignacionId;
    private Long padreId;      // Solo el ID es suficiente para crear/actualizar
    private Long menorId;      // Solo el ID es suficiente para crear/actualizar
    private Long psicologoId;  // Solo el ID es suficiente para crear/actualizar
    private Date fechaAsignacion;
    private String estado;
}

//(Asignación)