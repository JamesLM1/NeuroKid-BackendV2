package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.ADMINCitaListDTO;

import java.util.List;

public interface ADMINCitaService {
    // R - Read: Obtener todas las citas ordenadas por fecha descendente
    List<ADMINCitaListDTO> obtenerTodasLasCitas();
}

