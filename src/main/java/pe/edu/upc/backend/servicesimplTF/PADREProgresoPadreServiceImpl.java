package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PADREEvaluacionPsicologoDTO;
import pe.edu.upc.backend.dtosTF.PADREInformeDTO;
import pe.edu.upc.backend.entitiesTF.EvaluacionPsicologo;
import pe.edu.upc.backend.entitiesTF.Informe;
import pe.edu.upc.backend.repositoriesTF.*;
import pe.edu.upc.backend.servicesTF.PADREProgresoPadreService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PADREProgresoPadreServiceImpl implements PADREProgresoPadreService {

    @Autowired
    private EvaluacionPsicologoRepository evaluacionRepository;

    @Autowired
    private InformeRepository informeRepository;

    @Autowired
    private MenorRepository menorRepository;

    @Autowired
    private PadreRepository padreRepository;

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private CitaRepository citaRepository;

    // --- Conversión de DTO a Entity para Evaluación (C) ---
    private EvaluacionPsicologo convertToEntity(PADREEvaluacionPsicologoDTO dto) {
        EvaluacionPsicologo entity = new EvaluacionPsicologo();
        entity.setPuntaje(dto.getPuntaje());
        entity.setComentario(dto.getComentario());
        entity.setFechaEvaluacion(LocalDate.now());
        // Las FKs (Padre y Psicologo) se asignan en la función evaluarPsicologo
        return entity;
    }

    // --- Conversión de Entity a DTO para Evaluación (R) ---
    private PADREEvaluacionPsicologoDTO convertToDTO(EvaluacionPsicologo entity) {
        PADREEvaluacionPsicologoDTO dto = new PADREEvaluacionPsicologoDTO();
        dto.setEvaluacionId(entity.getEvaluacionId());
        dto.setPadreId(entity.getPadre().getPadreId());
        dto.setPsicologoId(entity.getPsicologo().getPsicologoId());
        dto.setPuntaje(entity.getPuntaje());
        dto.setComentario(entity.getComentario());
        dto.setFechaEvaluacion(entity.getFechaEvaluacion());
        return dto;
    }

    // --- Conversión de Entity a DTO para Informe (R) ---
    private PADREInformeDTO convertToDTO(Informe entity) {
        PADREInformeDTO dto = new PADREInformeDTO();
        dto.setInformeId(entity.getInformeId());
        dto.setMes(entity.getMes());
        dto.setAnio(entity.getAno());
        dto.setResumen(entity.getResumen());
        dto.setTitulo(entity.getTitulo()); // Agregar título para búsqueda inteligente

        // Mapeo de información del menor desde la Asignación
        dto.setMenorId(entity.getAsignacion().getMenor().getMenorId());
        dto.setNombreMenor(entity.getAsignacion().getMenor().getNombre() + " " + entity.getAsignacion().getMenor().getApellido());
        
        // Enriquecer con información del psicólogo
        dto.setNombrePsicologo(entity.getAsignacion().getPsicologo().getNombre() + " " + entity.getAsignacion().getPsicologo().getApellido());
        
        // Enriquecer con fecha de creación (convertir Date a LocalDate si es necesario, o usar Date directamente)
        if (entity.getFechaCreacion() != null) {
            dto.setFechaCreacion(entity.getFechaCreacion());
        }
        
        return dto;
    }

    // --- Implementación ---

    @Override
    public PADREEvaluacionPsicologoDTO evaluarPsicologo(Long padreId, PADREEvaluacionPsicologoDTO evaluacionDTO) {
        EvaluacionPsicologo evaluacion = convertToEntity(evaluacionDTO);

        // 1. Validar FKs
        evaluacion.setPadre(padreRepository.findById(padreId)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + padreId)));

        evaluacion.setPsicologo(psicologoRepository.findById(evaluacionDTO.getPsicologoId())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + evaluacionDTO.getPsicologoId())));

        EvaluacionPsicologo guardada = evaluacionRepository.save(evaluacion);
        // **COMPLETADO:** Mapeo de vuelta al DTO para retornarlo con el ID de la evaluación
        return convertToDTO(guardada);
    }

    @Override
    public PADREEvaluacionPsicologoDTO evaluarPsicologoPorCita(Long padreId, Long citaId, PADREEvaluacionPsicologoDTO evaluacionDTO) {
        // 1. Buscar la cita
        var cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + citaId));

        // 2. Validar que la cita pertenece a un menor de este padre
        if (!cita.getAsignacion().getMenor().getPadre().getPadreId().equals(padreId)) {
            throw new SecurityException("Acceso denegado: La cita no pertenece a un menor de este padre.");
        }

        // 3. Validar que la cita está finalizada
        if (!"Finalizada".equals(cita.getEstado())) {
            throw new IllegalStateException("Solo se pueden evaluar citas finalizadas.");
        }

        // 4. Obtener el psicólogo de la asignación de la cita
        Long psicologoId = cita.getAsignacion().getPsicologo().getPsicologoId();
        evaluacionDTO.setPsicologoId(psicologoId);

        // 5. Delegar al método existente
        return evaluarPsicologo(padreId, evaluacionDTO);
    }

    @Override
    public List<PADREInformeDTO> obtenerInformesPorMenor(Long menorId, Long padreId) {
        // **Validación de seguridad:** Se mantiene la validación que asegura que el menor pertenezca al padre
        menorRepository.findById(menorId)
                .filter(m -> m.getPadre().getPadreId().equals(padreId))
                .orElseThrow(() -> new SecurityException("Acceso denegado: Menor no encontrado o no pertenece a este padre."));

        // CORREGIDO: Usar el método correcto que busca por menorId, no por asignacionId
        return informeRepository.findPorMenorIdJPQL(menorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
