package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.ADMINPsicologoDTO;

import java.util.List;

public interface ADMINPsicologoService {
    // C - Create
    ADMINPsicologoDTO crearPsicologo(ADMINPsicologoDTO ADMINPsicologoDTO);

    // R - Read
    ADMINPsicologoDTO obtenerPsicologoPorId(Long id);
    List<ADMINPsicologoDTO> obtenerTodosLosPsicologos();
    List<ADMINPsicologoDTO> buscarPsicologosPorEspecialidad(String especialidad);

    // U - Update
    ADMINPsicologoDTO actualizarPsicologo(Long id, ADMINPsicologoDTO ADMINPsicologoDTO);

    // D - Delete
    void eliminarPsicologo(Long id);
    
    // Toggle estado del usuario
    ADMINPsicologoDTO toggleEstadoPsicologo(Long id);
}
