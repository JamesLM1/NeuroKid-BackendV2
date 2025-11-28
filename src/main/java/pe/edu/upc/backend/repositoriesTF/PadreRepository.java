package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.backend.entitiesTF.Favorito;
import pe.edu.upc.backend.entitiesTF.Padre;

import java.util.List;
import java.util.Optional;

public interface PadreRepository extends JpaRepository<Padre, Long> {

    //CONSULTAS JPQL

    @Query("SELECT p FROM Padre p WHERE p.nombre LIKE %:nombre% OR p.apellido LIKE %:apellido%")
    List<Padre> findPorNombreOApellidoJPQL(String nombre, String apellido);

    // Buscar padre por email (username)
    Optional<Padre> findByEmail(String email);
}
