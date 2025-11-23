package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PADREMenorDTO;

import java.util.List;

public interface PADREMenorService {
    // C - Crear
    PADREMenorDTO registrarMenor(Long padreId, PADREMenorDTO PADREMenorDTO);

    // R - Leer
    PADREMenorDTO obtenerMenorPorId(Long menorId, Long padreId); // Validar pertenencia
    List<PADREMenorDTO> obtenerMenoresPorPadre(Long padreId);

    // U - Actualizar
    PADREMenorDTO actualizarMenor(Long menorId, Long padreId, PADREMenorDTO PADREMenorDTO);

    // D - Eliminar
    void eliminarMenor(Long menorId, Long padreId);
}
