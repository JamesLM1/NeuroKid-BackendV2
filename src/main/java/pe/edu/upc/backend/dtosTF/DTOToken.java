package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOToken {

    private String jwtToken;
    private Long id; // ID de la entidad User
    private String authorities; // Los roles del usuario

}
