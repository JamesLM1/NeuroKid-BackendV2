package pe.edu.upc.backend.dtosTF;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADMINRecursoEducativoDTO {
    private Long recursoId;
    private String titulo;
    private String descripcion;
    private String link;
    private String texto;
    private Date fechaCreacion;
}

//(Recurso)