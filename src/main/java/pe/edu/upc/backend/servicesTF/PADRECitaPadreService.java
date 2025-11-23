package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PADRECitaRequestDTO;
import pe.edu.upc.backend.dtosTF.PADRECitaResponseDTO;

import java.util.List;

public interface PADRECitaPadreService {
    // C - Crear (Solicitar cita)
    PADRECitaResponseDTO solicitarCita(Long padreId, PADRECitaRequestDTO requestDTO);

    // R - Leer (Historial)
    List<PADRECitaResponseDTO> obtenerHistorialCitas(Long padreId);
    List<PADRECitaResponseDTO> obtenerProximasCitas(Long padreId);

    // U - Actualizar (Cancelar Cita)
    PADRECitaResponseDTO cancelarCita(Long citaId, Long padreId);
}
