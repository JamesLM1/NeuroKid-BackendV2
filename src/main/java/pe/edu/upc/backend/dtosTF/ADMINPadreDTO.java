package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADMINPadreDTO {
    private Long padreId;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String tipoParentesco;
    private Date fechaRegistro;
}

//(Para Respuesta y Edición)