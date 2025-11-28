package pe.edu.upc.backend.servicesTF;

import pe.edu.upc.backend.dtosTF.DisponibilidadSlotDTO;

import java.time.LocalDate;

public interface DisponibilidadSlotService {
    
    /**
     * Obtiene los horarios disponibles de un psicólogo para una fecha específica
     * @param psicologoId ID del psicólogo
     * @param fecha Fecha para consultar disponibilidad
     * @return DTO con los slots disponibles
     */
    DisponibilidadSlotDTO listarHorariosDisponibles(Long psicologoId, LocalDate fecha);
}
