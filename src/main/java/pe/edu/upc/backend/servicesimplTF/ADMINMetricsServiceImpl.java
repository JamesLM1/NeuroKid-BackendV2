package pe.edu.upc.backend.servicesimplTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.DashboardMetricsDTO;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.repositoriesTF.EvaluacionPsicologoRepository;
import pe.edu.upc.backend.repositoriesTF.MenorRepository;
import pe.edu.upc.backend.servicesTF.ADMINMetricsService;

@Service
public class ADMINMetricsServiceImpl implements ADMINMetricsService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private EvaluacionPsicologoRepository evaluacionRepository;

    @Autowired
    private MenorRepository menorRepository;

    private static final double PRECIO_BASE_CITA = 80.0; // Precio por cita finalizada

    @Override
    public DashboardMetricsDTO obtenerMetricasDashboard() {
        DashboardMetricsDTO metrics = new DashboardMetricsDTO();

        // 1. INGRESOS ESTIMADOS: Citas finalizadas * precio base
        long citasFinalizadas = citaRepository.countByEstado("Finalizada");
        metrics.setIngresosEstimados(citasFinalizadas * PRECIO_BASE_CITA);

        // 2. CALIDAD PROMEDIO: Promedio de puntajes en evaluaciones
        Double promedioCalificacion = evaluacionRepository.obtenerPromedioCalificacion();
        if (promedioCalificacion == null) {
            metrics.setCalidadPromedio(0.0);
        } else {
            metrics.setCalidadPromedio(promedioCalificacion);
        }

        // 3. VOLUMEN TOTAL: Total de citas
        metrics.setTotalCitas(citaRepository.count());

        // 4. TOTAL PACIENTES: Total de menores registrados
        metrics.setTotalPacientes(menorRepository.count());

        return metrics;
    }
}

