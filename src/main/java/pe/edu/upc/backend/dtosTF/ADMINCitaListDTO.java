package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADMINCitaListDTO {
    private Long citaId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private String motivo;
    
    // Nombres completos para mostrar en la tabla
    private String nombrePsicologo;
    private String nombreMenor;
    private String nombrePadre;
    
    // IDs para referencia (opcional, pero útil)
    private Long psicologoId;
    private Long menorId;
    private Long padreId;
}

