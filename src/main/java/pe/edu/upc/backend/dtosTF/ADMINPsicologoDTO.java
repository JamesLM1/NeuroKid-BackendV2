package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADMINPsicologoDTO {
    private Long psicologoId;
    private String nombre;
    private String apellido;
    private String dni;
    private String especialidad;
    private String email;
    private String telefono;
    private Date FechaRegistro;
}

//(Para Respuesta y Edición)