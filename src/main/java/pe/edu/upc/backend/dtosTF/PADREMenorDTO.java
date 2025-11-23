package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADREMenorDTO {
    private Long menorId;
    private Long padreId; // FK del padre que lo crea
    private String nombre;
    private String apellido;
    private Date fechaNacimiento;
}

//(Para Creación/Respuesta)
