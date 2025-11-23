package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.backend.entitiesTF.Menor;

import java.util.List;

public interface MenorRepository extends JpaRepository<Menor, Long> {

    //CONSULTA JPQL

    // Recuperar todos los menores de un padre
    @Query("SELECT m FROM Menor m WHERE m.padre.padreId = :padreId")
    List<Menor> findPorPadreIdJPQL(Long padreId);
}
