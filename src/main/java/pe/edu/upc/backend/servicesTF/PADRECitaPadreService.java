package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PADRECitaRequestDTO;
import pe.edu.upc.backend.dtosTF.PADRECitaResponseDTO;
import pe.edu.upc.backend.dtosTF.PADRESolicitudCitaDTO;

import java.util.List;

public interface PADRECitaPadreService {
    // C - Crear (Solicitar cita) - MÉTODO ORIGINAL
    PADRECitaResponseDTO solicitarCita(Long padreId, PADRECitaRequestDTO requestDTO);
    
    // C - Crear (Solicitar cita directa) - NUEVO MÉTODO AUTOSERVICIO
    PADRECitaResponseDTO solicitarCitaDirecta(Long padreId, PADRESolicitudCitaDTO solicitudDTO);

    // R - Leer (Historial)
    List<PADRECitaResponseDTO> obtenerHistorialCitas(Long padreId);
    List<PADRECitaResponseDTO> obtenerProximasCitas(Long padreId);

    // U - Actualizar (Cancelar Cita)
    PADRECitaResponseDTO cancelarCita(Long citaId, Long padreId);
}
