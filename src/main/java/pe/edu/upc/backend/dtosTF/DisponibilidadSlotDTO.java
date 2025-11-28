package pe.edu.upc.backend.dtosTF;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para respuesta de disponibilidad de horarios
 * Contiene los slots libres de un psicólogo en una fecha específica
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadSlotDTO {
    
    private Long psicologoId;
    private String nombrePsicologo;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @JsonFormat(pattern = "HH:mm")
    private List<LocalTime> horariosDisponibles;
    
    // Información adicional útil
    private Integer totalSlots;
    private Integer slotsOcupados;
    private Integer slotsDisponibles;
    
    // Constructor simplificado para uso común
    public DisponibilidadSlotDTO(Long psicologoId, LocalDate fecha, List<LocalTime> horariosDisponibles) {
        this.psicologoId = psicologoId;
        this.fecha = fecha;
        this.horariosDisponibles = horariosDisponibles;
        this.slotsDisponibles = horariosDisponibles.size();
    }
}
