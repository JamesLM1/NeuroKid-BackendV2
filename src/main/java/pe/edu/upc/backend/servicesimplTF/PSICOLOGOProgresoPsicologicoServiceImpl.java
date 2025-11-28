package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PSICOLOGOInformeDTO;
import pe.edu.upc.backend.dtosTF.PSICOLOGOProgresoMenorDTO;
import pe.edu.upc.backend.entitiesTF.Asignacion;
import pe.edu.upc.backend.entitiesTF.Informe;
import pe.edu.upc.backend.entitiesTF.Menor;
import pe.edu.upc.backend.repositoriesTF.AsignacionRepository;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.repositoriesTF.InformeRepository;
import pe.edu.upc.backend.repositoriesTF.MenorRepository;
import pe.edu.upc.backend.servicesTF.PSICOLOGOProgresoPsicologicoService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PSICOLOGOProgresoPsicologicoServiceImpl implements PSICOLOGOProgresoPsicologicoService {

    @Autowired
    private InformeRepository informeRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private MenorRepository menorRepository;

    @Autowired
    private CitaRepository citaRepository;

    // --- Mapeo a InformeDTO ---
    private PSICOLOGOInformeDTO convertToDTO(Informe entity) {
        PSICOLOGOInformeDTO dto = new PSICOLOGOInformeDTO();
        dto.setInformeId(entity.getInformeId());
        dto.setAsignacionId(entity.getAsignacion().getAsignacionId());
        dto.setPsicologoId(entity.getAsignacion().getPsicologo().getPsicologoId()); // Lo obtenemos de la Asignación
        dto.setTitulo(entity.getTitulo());
        dto.setContenido(entity.getContenido());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setCalificacionEficacia(entity.getCalificacionEficacia());
        
        // Enriquecer con nombre del menor desde la Asignación
        dto.setNombreMenor(entity.getAsignacion().getMenor().getNombre() + " " + entity.getAsignacion().getMenor().getApellido());
        
        return dto;
    }

    // --- Mapeo a ProgresoMenorDTO (FINALIZADO) ---
    private PSICOLOGOProgresoMenorDTO convertToProgresoDTO(Menor menor, List<Informe> informes, Long asignacionId) {
        PSICOLOGOProgresoMenorDTO dto = new PSICOLOGOProgresoMenorDTO();
        dto.setMenorId(menor.getMenorId());
        dto.setNombreMenor(menor.getNombre() + " " + menor.getApellido());
        dto.setDiagnosticoActual(menor.getDiagnostico());

        // **Cálculo de métricas:**

        // 1. Citas Completadas
        // Consultamos el repositorio de Citas, filtrando por la asignación y el estado "Finalizada"
        Integer citasCompletadas = citaRepository.countByAsignacion_AsignacionIdAndEstado(asignacionId, "Finalizada");
        dto.setCitasCompletadas(citasCompletadas != null ? citasCompletadas : 0);

        // 2. Promedio de Eficacia
        double promedio = informes.stream()
                .filter(i -> i.getCalificacionEficacia() != null)
                .mapToDouble(Informe::getCalificacionEficacia)
                .average()
                .orElse(0.0);

        dto.setPromedioEficaciaInformes(Math.round(promedio * 100.0) / 100.0); // Redondeo a 2 decimales

        // Mapeo de los últimos informes
        dto.setUltimosInformes(informes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // --- Lógica de Seguridad y Pertenencia ---
    private Asignacion validarAsignacion(Long asignacionId, Long psicologoId) {
        Asignacion asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada."));

        if (!asignacion.getPsicologo().getPsicologoId().equals(psicologoId)) {
            throw new SecurityException("Acceso denegado: La asignación no pertenece a este psicólogo.");
        }
        return asignacion;
    }

    // ===============================================
    // MÉTODOS DE LA INTERFAZ ProgresoPsicologoService
    // ===============================================

    @Override
    public PSICOLOGOInformeDTO crearInforme(Long asignacionId, Long psicologoId, PSICOLOGOInformeDTO PSICOLOGOInformeDTO) {
        Asignacion asignacion = validarAsignacion(asignacionId, psicologoId);

        Informe informe = new Informe();
        informe.setAsignacion(asignacion);
        informe.setTitulo(PSICOLOGOInformeDTO.getTitulo());
        informe.setContenido(PSICOLOGOInformeDTO.getContenido());
        informe.setCalificacionEficacia(PSICOLOGOInformeDTO.getCalificacionEficacia());
        informe.setFechaCreacion(new Date()); // Establecer la fecha de creación
        
        // CORREGIDO: Asegurar que el Resumen nunca esté vacío
        // Si el DTO no tiene resumen, usar el título como resumen
        String resumen = PSICOLOGOInformeDTO.getTitulo() != null && !PSICOLOGOInformeDTO.getTitulo().isEmpty() 
            ? PSICOLOGOInformeDTO.getTitulo() 
            : (PSICOLOGOInformeDTO.getContenido() != null && !PSICOLOGOInformeDTO.getContenido().isEmpty()
                ? PSICOLOGOInformeDTO.getContenido().substring(0, Math.min(100, PSICOLOGOInformeDTO.getContenido().length()))
                : "Sin resumen");
        informe.setResumen(resumen);

        Informe nuevoInforme = informeRepository.save(informe);
        return convertToDTO(nuevoInforme);
    }

    @Override
    public List<PSICOLOGOInformeDTO> obtenerInformesPorAsignacion(Long asignacionId, Long psicologoId) {
        validarAsignacion(asignacionId, psicologoId); // Valida la existencia y pertenencia

        // Asumiendo un metodo findByAsignacion_AsignacionId
        return informeRepository.findPorAsignacionIdJPQL(asignacionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PSICOLOGOProgresoMenorDTO obtenerProgresoMenor(Long menorId, Long psicologoId) {
        Menor menor = menorRepository.findById(menorId)
                .orElseThrow(() -> new EntityNotFoundException("Menor no encontrado."));

        // Asumimos que el estado de asignación activa es "ACTIVA".
        Asignacion asignacionActiva = asignacionRepository
                .findByMenor_MenorIdAndPsicologo_PsicologoIdAndEstado(menorId, psicologoId, "ACTIVA")
                .orElseThrow(() -> new SecurityException("Acceso denegado: El menor no está asignado activamente a este psicólogo o la asignación no está ACTIVA."));

        // Obtener todos los informes relacionados con esta asignación activa
        List<Informe> informes = informeRepository.findPorAsignacionIdJPQL(asignacionActiva.getAsignacionId());

        // CORREGIDO: Pasando el tercer argumento requerido (asignacionId)
        return convertToProgresoDTO(menor, informes, asignacionActiva.getAsignacionId());
    }
}
