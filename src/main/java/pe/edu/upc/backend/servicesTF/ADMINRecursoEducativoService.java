package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.ADMINRecursoEducativoDTO;

import java.util.List;

public interface ADMINRecursoEducativoService {
    // C - Create
    ADMINRecursoEducativoDTO crearRecurso(ADMINRecursoEducativoDTO recursoDTO);

    // R - Read
    ADMINRecursoEducativoDTO obtenerRecursoPorId(Long id);
    List<ADMINRecursoEducativoDTO> obtenerTodosLosRecursos();
    List<ADMINRecursoEducativoDTO> buscarRecursosPorTitulo(String termino);

    // U - Update
    ADMINRecursoEducativoDTO actualizarRecurso(Long id, ADMINRecursoEducativoDTO recursoDTO);

    // D - Delete
    void eliminarRecurso(Long id);
}
