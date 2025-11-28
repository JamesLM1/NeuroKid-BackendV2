package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PSICOLOGODashboardDTO {
    private Long citasHoy;
    private Long pacientesActivos;
    private Double calificacionPromedio;
    private Long totalEvaluaciones;
    private List<PSICOLOGOCitaResponseDTO> listaCitasHoy;
}

