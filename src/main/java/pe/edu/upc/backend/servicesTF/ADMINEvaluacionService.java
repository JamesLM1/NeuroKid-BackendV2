package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.ADMINEvaluacionListDTO;

import java.util.List;

public interface ADMINEvaluacionService {
    List<ADMINEvaluacionListDTO> obtenerTodasLasEvaluaciones();
    void eliminarEvaluacion(Long id);
}

