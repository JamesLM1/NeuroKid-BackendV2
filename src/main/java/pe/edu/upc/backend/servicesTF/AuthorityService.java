package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.entitiesTF.Authority;

public interface AuthorityService {

    // Metodo para obtener una autoridad por su ID
    Authority findById(Long id);

    // Metodo para obtener una autoridad por su nombre (ej: "ROLE_ADMIN")
    Authority findByName(String name);

    // Metodo para agregar una nueva autoridad (necesario para inicializar roles)
    Authority add(Authority authority);

}
