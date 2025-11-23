package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PADREFavoritoDTO;
import pe.edu.upc.backend.dtosTF.ADMINRecursoEducativoDTO;

import java.util.List;

public interface PADRERecursoPadreService {
    // R - Leer Recursos
    List<ADMINRecursoEducativoDTO> buscarRecursos(String termino);

    // C/D - Marcar/Desmarcar Favorito
    PADREFavoritoDTO marcarFavorito(Long padreId, Long recursoId);
    void desmarcarFavorito(Long padreId, Long recursoId);

    // R - Leer Favoritos
    List<ADMINRecursoEducativoDTO> obtenerRecursosFavoritos(Long padreId);
}
