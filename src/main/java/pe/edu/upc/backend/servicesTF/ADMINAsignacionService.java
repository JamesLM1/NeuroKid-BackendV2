package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.ADMINAsignacionDTO;

import java.util.List;

public interface ADMINAsignacionService {
    // C - Create0
    ADMINAsignacionDTO crearAsignacion(ADMINAsignacionDTO ADMINAsignacionDTO);

    // R - Read
    ADMINAsignacionDTO obtenerAsignacionPorId(Long id);
    List<ADMINAsignacionDTO> obtenerTodasLasAsignaciones();

    // U - Update (Incluye Pausar/Reactivar)
    ADMINAsignacionDTO actualizarAsignacion(Long id, ADMINAsignacionDTO ADMINAsignacionDTO);
    ADMINAsignacionDTO cambiarEstadoAsignacion(Long id, String nuevoEstado);

    // D - Delete (Opcional, pero se incluye por completitud, aunque se recomienda usar "estado")
    void eliminarAsignacion(Long id);

}
