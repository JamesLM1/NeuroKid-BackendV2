package pe.edu.upc.backend.securityTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upc.backend.entitiesTF.Authority;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthoritySecurity implements GrantedAuthority {

    private Authority authority;

    @Override
    public String getAuthority() {
        return authority.getName(); // Devuelve el nombre del rol (ej: ROLE_ADMIN)
    }
}
