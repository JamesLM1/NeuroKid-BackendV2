package pe.edu.upc.backend.servicesimplTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.DTOUser;
import pe.edu.upc.backend.entitiesTF.Authority;
import pe.edu.upc.backend.entitiesTF.User;
import pe.edu.upc.backend.repositoriesTF.UserRepository;
import pe.edu.upc.backend.servicesTF.AuthorityService;
import pe.edu.upc.backend.servicesTF.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityService authorityService;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        // Usa el metodo del repositorio para buscar
        return userRepository.findByUsername(username);
    }

    /**
     * Convierte una cadena de roles (ej: "ROLE_ADMIN;ROLE_PADRE") en una lista de entidades Authority.
     */
    private List<Authority> authoritiesFromString(String authorities){

        List<Authority> authorityList = new ArrayList<>();
        // El DTO del profesor usa ';' para separar roles.
        List<String> authorityStringList = Arrays.stream(authorities.split(";")).toList();

        for(String authorityString: authorityStringList) {
            // Busca la Authority en la DB
            Authority authority = authorityService.findByName(authorityString);
            if (authority!=null) {
                authorityList.add(authority);
            }
        }
        return authorityList;
    }

    @Override
    public DTOUser add(DTOUser userDTO) {
        // 1. Obtiene las entidades Authority de la DB
        List<Authority> authorityList = authoritiesFromString(userDTO.getAuthorities());

        // 2. Codifica la contraseña y crea la nueva entidad User
        User newUser = new User(null, userDTO.getUsername(),
                new BCryptPasswordEncoder().encode(userDTO.getPassword()),
                true, authorityList); // 'true' = cuenta habilitada

        // 3. Guarda el usuario en la DB
        newUser = userRepository.save(newUser);

        // 4. Mapea de vuelta el ID para retornarlo en el DTO de respuesta
        userDTO.setId(newUser.getId());
        return userDTO;
    }
}
