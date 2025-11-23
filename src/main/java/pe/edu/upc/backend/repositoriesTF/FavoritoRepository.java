package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.backend.entitiesTF.Favorito;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito,Long> {

    //SQL NATIVAS

    @Query(value = "SELECT * FROM favoritos f WHERE f.padre_id = :padreId", nativeQuery = true)
    List<Favorito> findByPadreIdSQL(Long padreId);

    @Query(value = "SELECT * FROM favoritos f WHERE f.padre_id = :padreId AND f.recurso_id = :recursoId", nativeQuery = true)
    Favorito findByPadreIdAndRecursoIdSQL(Long padreId, Long recursoId);
}
