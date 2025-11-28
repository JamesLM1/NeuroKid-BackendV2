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
    private String tipoDocumento; // Tipo de documento (DNI, Pasaporte, CE)
    private String dni; // Número de documento genérico
    private String especialidad;
    private String email;
    private String telefono;
    private String claveVisible; // Contraseña en texto plano para mostrar al admin
    private Boolean usuarioActivo; // Estado del usuario (enabled/disabled)
    private Date FechaRegistro;
}

//(Para Respuesta y Edición)