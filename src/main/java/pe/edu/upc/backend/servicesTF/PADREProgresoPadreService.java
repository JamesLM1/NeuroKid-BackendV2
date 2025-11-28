package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PADREEvaluacionPsicologoDTO;
import pe.edu.upc.backend.dtosTF.PADREInformeDTO;

import java.util.List;

public interface PADREProgresoPadreService {
    // C - Crear Evaluación
    PADREEvaluacionPsicologoDTO evaluarPsicologo(Long padreId, PADREEvaluacionPsicologoDTO evaluacionDTO);

    // C - Crear Evaluación por Cita
    PADREEvaluacionPsicologoDTO evaluarPsicologoPorCita(Long padreId, Long citaId, PADREEvaluacionPsicologoDTO evaluacionDTO);

    // R - Leer Informes
    List<PADREInformeDTO> obtenerInformesPorMenor(Long menorId, Long padreId);
}
