package pe.edu.upc.backend.servicesimplTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.entitiesTF.User;
import pe.edu.upc.backend.securityTF.UserSecurity;
import pe.edu.upc.backend.servicesTF.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca la entidad User en la DB usando el servicio
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con username: " + username);
        }

        // 2. Envuelve la entidad en el adaptador de seguridad requerido por Spring
        return new UserSecurity(user);
    }
}
