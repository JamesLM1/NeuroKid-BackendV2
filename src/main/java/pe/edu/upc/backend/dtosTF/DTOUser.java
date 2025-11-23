package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOUser {

    private Long id;
    private String username;
    private String password;
    private String authorities; // Cadena de roles separada por ';' (Ej: "ROLE_ADMIN;ROLE_PADRE")

}
