package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PSICOLOGODisponibilidadDTO;

import java.util.List;

public interface PSICOLOGODisponibilidadHorariaService {
    // C - Crear nueva disponibilidad o actualizar una existente para el día
    PSICOLOGODisponibilidadDTO crearOActualizarDisponibilidad(Long psicologoId, PSICOLOGODisponibilidadDTO dto);

    // R - Obtener la disponibilidad de un psicólogo específico
    List<PSICOLOGODisponibilidadDTO> obtenerDisponibilidadPorPsicologo(Long psicologoId);

    // D - Eliminar un bloque de disponibilidad
    void eliminarDisponibilidad(Long disponibilidadId, Long psicologoId);
}
