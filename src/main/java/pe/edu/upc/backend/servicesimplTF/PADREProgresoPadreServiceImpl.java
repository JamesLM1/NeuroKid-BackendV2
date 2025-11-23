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

import java.util.Date;
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

    // --- Conversión de DTO a Entity para Evaluación (C) ---
    private EvaluacionPsicologo convertToEntity(PADREEvaluacionPsicologoDTO dto) {
        EvaluacionPsicologo entity = new EvaluacionPsicologo();
        entity.setPuntaje(dto.getPuntaje());
        entity.setComentario(dto.getComentario());
        entity.setFechaEvaluacion(new Date());
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

        // Mapeo de información del menor desde la Asignación
        dto.setMenorId(entity.getAsignacion().getMenor().getMenorId());
        dto.setNombreMenor(entity.getAsignacion().getMenor().getNombre());
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
    public List<PADREInformeDTO> obtenerInformesPorMenor(Long menorId, Long padreId) {
        // **Validación de seguridad:** Se mantiene la validación que asegura que el menor pertenezca al padre
        menorRepository.findById(menorId)
                .filter(m -> m.getPadre().getPadreId().equals(padreId))
                .orElseThrow(() -> new SecurityException("Acceso denegado: Menor no encontrado o no pertenece a este padre."));

        // Se usa la función convertToDTO completada
        return informeRepository.findPorAsignacionIdJPQL(menorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
