package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMetricsDTO {
    private Double ingresosEstimados;  // Citas finalizadas * precio base (80)
    private Double calidadPromedio;     // Promedio de estrellas en Evaluaciones (0-5)
    private Long totalCitas;            // Volumen total de citas
    private Long totalPacientes;        // Total de Menores registrados
}

