package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.ADMINPadreDTO;

import java.util.List;

public interface ADMINPadreService {
    // C - Create
    ADMINPadreDTO crearPadre(ADMINPadreDTO ADMINPadreDTO);

    // R - Read
    ADMINPadreDTO obtenerPadrePorId(Long id);
    List<ADMINPadreDTO> obtenerTodosLosPadres();
    List<ADMINPadreDTO> buscarPadresPorNombre(String termino);

    // U - Update
    ADMINPadreDTO actualizarPadre(Long id, ADMINPadreDTO ADMINPadreDTO);

    // D - Delete
    void eliminarPadre(Long id);
}
