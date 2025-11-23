package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.PSICOLOGOInformeDTO;
import pe.edu.upc.backend.dtosTF.PSICOLOGOProgresoMenorDTO;

import java.util.List;

public interface PSICOLOGOProgresoPsicologicoService {
    // C - Crear un informe mensual para una asignación
    PSICOLOGOInformeDTO crearInforme(Long asignacionId, Long psicologoId, PSICOLOGOInformeDTO PSICOLOGOInformeDTO);

    // R - Obtener los informes creados por el psicólogo para una asignación
    List<PSICOLOGOInformeDTO> obtenerInformesPorAsignacion(Long asignacionId, Long psicologoId);

    // R - Obtener métricas de progreso de un menor
    PSICOLOGOProgresoMenorDTO obtenerProgresoMenor(Long menorId, Long psicologoId);
}
