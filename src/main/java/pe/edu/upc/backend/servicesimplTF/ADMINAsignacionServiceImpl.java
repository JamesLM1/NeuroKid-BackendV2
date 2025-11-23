package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINAsignacionDTO;
import pe.edu.upc.backend.entitiesTF.Asignacion;
import pe.edu.upc.backend.entitiesTF.Menor;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.entitiesTF.Psicologo;
import pe.edu.upc.backend.repositoriesTF.AsignacionRepository;
import pe.edu.upc.backend.repositoriesTF.MenorRepository;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.repositoriesTF.PsicologoRepository;
import pe.edu.upc.backend.servicesTF.ADMINAsignacionService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINAsignacionServiceImpl implements ADMINAsignacionService {

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private PadreRepository padreRepository;

    @Autowired
    private MenorRepository menorRepository;

    @Autowired
    private PsicologoRepository psicologoRepository;

    // --- Conversión Entity <-> DTO (COMPLETADO) ---
    private ADMINAsignacionDTO convertToDTO(Asignacion entity) {
        ADMINAsignacionDTO dto = new ADMINAsignacionDTO();

        dto.setAsignacionId(entity.getAsignacionId());

        // Mapeo de FKs
        dto.setPadreId(entity.getPadre().getPadreId());
        dto.setMenorId(entity.getMenor().getMenorId());
        dto.setPsicologoId(entity.getPsicologo().getPsicologoId());

        dto.setFechaAsignacion(entity.getFechaAsignacion());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    // --- Implementación de CRUD ---

    @Override
    public ADMINAsignacionDTO crearAsignacion(ADMINAsignacionDTO dto) {
        // 1. Validar existencia de las FKs
        Padre padre = padreRepository.findById(dto.getPadreId())
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + dto.getPadreId()));
        Menor menor = menorRepository.findById(dto.getMenorId())
                .orElseThrow(() -> new EntityNotFoundException("Menor no encontrado con ID: " + dto.getMenorId()));
        Psicologo psicologo = psicologoRepository.findById(dto.getPsicologoId())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + dto.getPsicologoId()));

        // 2. Crear Asignacion
        Asignacion asignacion = new Asignacion();
        asignacion.setPadre(padre);
        asignacion.setMenor(menor);
        asignacion.setPsicologo(psicologo);
        asignacion.setFechaAsignacion(new Date());
        asignacion.setEstado("Activa"); // Estado inicial

        Asignacion nuevaAsignacion = asignacionRepository.save(asignacion);
        return convertToDTO(nuevaAsignacion);
    }

    @Override
    public ADMINAsignacionDTO obtenerAsignacionPorId(Long id) {
        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + id));
        return convertToDTO(asignacion);
    }

    @Override
    public List<ADMINAsignacionDTO> obtenerTodasLasAsignaciones() {
        return asignacionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ADMINAsignacionDTO actualizarAsignacion(Long id, ADMINAsignacionDTO ADMINAsignacionDTO) {
        Asignacion asignacionExistente = asignacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + id));

        // Lógica de actualización completa (Admin puede cambiar Psicólogo o Estado)

        // 1. Actualizar Psicólogo si se proporciona un ID diferente
        if (ADMINAsignacionDTO.getPsicologoId() != null && !asignacionExistente.getPsicologo().getPsicologoId().equals(ADMINAsignacionDTO.getPsicologoId())) {
            Psicologo nuevoPsicologo = psicologoRepository.findById(ADMINAsignacionDTO.getPsicologoId())
                    .orElseThrow(() -> new EntityNotFoundException("Psicólogo nuevo no encontrado."));
            asignacionExistente.setPsicologo(nuevoPsicologo);
        }

        // 2. Actualizar Estado
        if (ADMINAsignacionDTO.getEstado() != null) {
            asignacionExistente.setEstado(ADMINAsignacionDTO.getEstado());
        }

        Asignacion actualizado = asignacionRepository.save(asignacionExistente);
        return convertToDTO(actualizado);
    }

    @Override
    public ADMINAsignacionDTO cambiarEstadoAsignacion(Long id, String nuevoEstado) {
        Asignacion asignacionExistente = asignacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + id));

        if (!List.of("Activa", "Pausada", "Finalizada").contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido. Los estados permitidos son: Activa, Pausada, Finalizada.");
        }
        asignacionExistente.setEstado(nuevoEstado);
        Asignacion actualizado = asignacionRepository.save(asignacionExistente);
        return convertToDTO(actualizado);
    }

    @Override
    public void eliminarAsignacion(Long id) {
        asignacionRepository.deleteById(id);
    }

}
