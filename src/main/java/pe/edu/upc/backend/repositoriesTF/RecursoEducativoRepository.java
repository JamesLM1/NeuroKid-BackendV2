package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.backend.entitiesTF.RecursoEducativo;

import java.util.List;

public interface RecursoEducativoRepository extends JpaRepository<RecursoEducativo, Long> {
    List<RecursoEducativo> findByTituloContaining(String titulo);
}
