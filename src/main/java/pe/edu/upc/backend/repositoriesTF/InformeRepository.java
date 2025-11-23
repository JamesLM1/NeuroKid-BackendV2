package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.backend.entitiesTF.Informe;

import java.util.List;

public interface InformeRepository extends JpaRepository<Informe, Long> {

    //CONSULTAS JPQL

    @Query("SELECT i FROM Informe i WHERE i.asignacion.menor.menorId = :menorId")
    List<Informe> findPorMenorIdJPQL(Long menorId);

    @Query("SELECT i FROM Informe i WHERE i.asignacion.asignacionId = :asignacionId")
    List<Informe> findPorAsignacionIdJPQL(Long asignacionId);
}
