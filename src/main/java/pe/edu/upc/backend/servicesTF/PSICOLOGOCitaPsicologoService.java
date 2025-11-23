package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PSICOLOGOCitaResponseDTO;

import java.util.List;

public interface PSICOLOGOCitaPsicologoService {
    // R - Obtener próximas citas del psicólogo
    List<PSICOLOGOCitaResponseDTO> obtenerProximasCitas(Long psicologoId);

    // R - Obtener historial de citas atendidas o canceladas
    List<PSICOLOGOCitaResponseDTO> obtenerHistorialCitas(Long psicologoId);

    // U - Aceptar o Rechazar una cita pendiente
    PSICOLOGOCitaResponseDTO cambiarEstadoCita(Long citaId, Long psicologoId, String nuevoEstado);

    // U - Finalizar una cita y agregar hallazgos (completar cita)
    PSICOLOGOCitaResponseDTO finalizarCitaYRegistrarHallazgos(Long citaId, Long psicologoId, String hallazgos);
}
