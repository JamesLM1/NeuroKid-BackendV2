package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PADRECitaRequestDTO;
import pe.edu.upc.backend.dtosTF.PADRECitaResponseDTO;
import pe.edu.upc.backend.entitiesTF.Asignacion;
import pe.edu.upc.backend.entitiesTF.Cita;
import pe.edu.upc.backend.repositoriesTF.AsignacionRepository;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.servicesTF.PADRECitaPadreService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PADRECitaPadreServiceImpl implements PADRECitaPadreService {

    @Autowired private CitaRepository citaRepository;
    @Autowired private AsignacionRepository asignacionRepository;

    // Métodos de conversión completos:
    private PADRECitaResponseDTO convertToDTO(Cita entity) {
        PADRECitaResponseDTO dto = new PADRECitaResponseDTO();

        dto.setCitaId(entity.getCitaId());
        dto.setAsignacionId(entity.getAsignacion().getAsignacionId());

        dto.setNombreMenor(entity.getAsignacion().getMenor().getNombre());
        dto.setNombrePsicologo(entity.getAsignacion().getPsicologo().getNombre());

        dto.setFecha(entity.getFecha());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setMotivo(entity.getMotivo());
        dto.setHallazgos(entity.getHallazgos());
        dto.setEstado(entity.getEstado()); // Mapeo del nuevo campo Estado

        return dto;
    }

    // --- Implementación ---

    @Override
    public PADRECitaResponseDTO solicitarCita(Long padreId, PADRECitaRequestDTO requestDTO) {
        // Validación de existencia de Asignación
        Asignacion asignacion = asignacionRepository.findById(requestDTO.getAsignacionId())
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + requestDTO.getAsignacionId()));

        // **Validación de seguridad:** Asegurar que la asignación pertenezca al padre
        if (!asignacion.getPadre().getPadreId().equals(padreId)) {
            throw new SecurityException("Acceso denegado: La asignación no pertenece a este padre.");
        }

        //Lógica de validación de disponibilidad del Psicologo (usando DISPONIBILIDAD_HORARIA y CITA)
        // CRÍTICO: Aquí DEBE haber lógica para evitar doble reserva.

        Cita nuevaCita = new Cita();
        nuevaCita.setAsignacion(asignacion);

        // Mapeo de campos de la solicitud
        nuevaCita.setFecha(requestDTO.getFecha());
        nuevaCita.setHoraInicio(requestDTO.getHoraInicio());
        nuevaCita.setHoraFin(requestDTO.getHoraFin());
        nuevaCita.setMotivo(requestDTO.getMotivo());

        // **CORRECCIÓN:** El estado inicial de una solicitud SIEMPRE es Pendiente.
        nuevaCita.setEstado("Pendiente");

        Cita guardada = citaRepository.save(nuevaCita);
        return convertToDTO(guardada);
    }

    @Override
    public List<PADRECitaResponseDTO> obtenerHistorialCitas(Long padreId) {
        // Se considera el historial como TODAS las citas del padre
        return citaRepository.findByAsignacion_Padre_PadreId(padreId).stream()
                // Se puede agregar un filtro por estado si se desea solo ver las 'Atendidas' o 'Finalizadas'
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PADRECitaResponseDTO> obtenerProximasCitas(Long padreId) {
        // Se usa el filtro en memoria, idealmente se debe hacer un método en el repositorio
        return citaRepository.findByAsignacion_Padre_PadreId(padreId).stream()
                // Filtra por fecha futura Y estado "Pendiente" (o similar)
                .filter(c -> c.getFecha().after(new Date()) && "Pendiente".equals(c.getEstado()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PADRECitaResponseDTO cancelarCita(Long citaId, Long padreId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + citaId));

        // Validación de seguridad
        if (!cita.getAsignacion().getPadre().getPadreId().equals(padreId)) {
            throw new SecurityException("Acceso denegado: La cita no pertenece a este padre.");
        }

        // **COMPLETADO:** Implementar lógica de cancelación (Ej: cita.setEstado("Cancelada"))
        if (!"Cancelada".equals(cita.getEstado()) && !"Atendida".equals(cita.getEstado())) {
            cita.setEstado("Cancelada");
        } else {
            throw new IllegalStateException("Solo se pueden cancelar citas en estado 'Pendiente'.");
        }

        Cita actualizada = citaRepository.save(cita);
        return convertToDTO(actualizada);
    }
}
