package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PSICOLOGOProgresoMenorDTO {
    private Long menorId;
    private String nombreMenor;
    private String diagnosticoActual;
    private Integer citasCompletadas;
    private Double promedioEficaciaInformes; // Promedio de calificación
    private List<PSICOLOGOInformeDTO> ultimosInformes;
}
