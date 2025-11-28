package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.DTOUser;
import pe.edu.upc.backend.entitiesTF.User;

import java.util.List;

public interface UserService {

    // Método para obtener un usuario por su ID
    User findById(Long id);

    // Método CRUCIAL para Spring Security: Carga el usuario por nombre
    User findByUsername(String username);

    // Método para registrar un nuevo usuario (User)
    DTOUser add(DTOUser userDTO);

    // Método para obtener todos los usuarios (necesario para data seeding)
    List<User> findAll();

}
