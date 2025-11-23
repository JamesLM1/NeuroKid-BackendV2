package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADREFavoritoDTO {
    private Long favoritoId;
    private Long padreId;
    private Long recursoId;
    private Date fechaMarcado;
}

//(Para Creación)