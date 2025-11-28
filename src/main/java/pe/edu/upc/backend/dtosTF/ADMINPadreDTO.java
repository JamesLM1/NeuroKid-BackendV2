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
    private String tipoDocumento;
    private String dni; // Ahora funciona como numeroDocumento genérico
    private String email;
    private String telefono;
    private String tipoParentesco;
    private String claveVisible; // Contraseña en texto plano para mostrar al admin
    private Boolean usuarioActivo; // Estado del usuario (enabled/disabled)
    private Date fechaRegistro;
}

//(Para Respuesta y Edición)