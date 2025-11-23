package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PSICOLOGOCitaResponseDTO;
import pe.edu.upc.backend.entitiesTF.*;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.servicesTF.PSICOLOGOCitaPsicologoService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PSICOLOGOCitaPsicologoServiceImpl implements PSICOLOGOCitaPsicologoService {
    @Autowired
    private CitaRepository citaRepository;

    // --- Conversión Entity -> DTO (COMPLETADO) ---
    private PSICOLOGOCitaResponseDTO convertToDTO(Cita entity) {
        PSICOLOGOCitaResponseDTO dto = new PSICOLOGOCitaResponseDTO();

        // Asignación y entidades relacionadas
        Asignacion asignacion = entity.getAsignacion();
        Psicologo psicologo = asignacion.getPsicologo();
        Padre padre = asignacion.getPadre();
        Menor menor = asignacion.getMenor();

        // 1. Datos de Cita
        dto.setCitaId(entity.getCitaId());
        dto.setFechaHoraCita(entity.getFecha());
        dto.setMotivoCita(entity.getMotivo());
        dto.setEstado(entity.getEstado());
        dto.setHallazgos(entity.getHallazgos()); // Notas del psicólogo al finalizar

        // 2. Información de Asignación
        dto.setAsignacionId(asignacion.getAsignacionId());

        // 3. Información del Psicólogo (propio, para consistencia)
        dto.setPsicologoId(psicologo.getPsicologoId());
        dto.setNombrePsicologo(psicologo.getNombre() + " " + psicologo.getApellido());

        // 4. Información del Padre
        dto.setPadreId(padre.getPadreId());
        dto.setNombreCompletoPadre(padre.getNombre() + " " + padre.getApellido());
        dto.setEmailPadre(padre.getEmail());

        // 5. Información del Menor
        dto.setMenorId(menor.getMenorId());
        dto.setNombreCompletoMenor(menor.getNombre() + " " + menor.getApellido());
        dto.setFechaNacimientoMenor(menor.getFechaNacimiento());

        return dto;
    }

    // --- Lógica de Seguridad ---
    private void validarPertenencia(Cita cita, Long psicologoId) {
        if (!cita.getAsignacion().getPsicologo().getPsicologoId().equals(psicologoId)) {
            throw new SecurityException("Acceso denegado: La cita no está asignada a este psicólogo.");
        }
    }

    // --- Implementación de Servicio ---

    @Override
    public List<PSICOLOGOCitaResponseDTO> obtenerProximasCitas(Long psicologoId) {

        return citaRepository.findByAsignacion_Psicologo_PsicologoIdAndFechaAfter(psicologoId, new Date()).stream()
                .filter(c -> c.getEstado().equals("Pendiente") || c.getEstado().equals("Confirmada"))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PSICOLOGOCitaResponseDTO> obtenerHistorialCitas(Long psicologoId) {
        // Se asume un metodo findByAsignacion_Psicologo_PsicologoId en el repositorio
        return citaRepository.findByAsignacion_Psicologo_PsicologoId(psicologoId).stream()
                .filter(c -> c.getEstado().equals("Finalizada") || c.getEstado().equals("Cancelada") || c.getEstado().equals("Rechazada")) // Incluimos Rechazada en historial
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PSICOLOGOCitaResponseDTO cambiarEstadoCita(Long citaId, Long psicologoId, String nuevoEstado) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada."));

        validarPertenencia(cita, psicologoId);

        if (!List.of("Confirmada", "Rechazada").contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido para la acción. Use 'Confirmada' o 'Rechazada'.");
        }

        // Solo se puede cambiar si está pendiente
        if (!cita.getEstado().equals("Pendiente")) {
            throw new IllegalStateException("Solo se pueden confirmar o rechazar citas en estado 'Pendiente'.");
        }

        cita.setEstado(nuevoEstado);
        Cita actualizada = citaRepository.save(cita);
        return convertToDTO(actualizada);
    }

    @Override
    public PSICOLOGOCitaResponseDTO finalizarCitaYRegistrarHallazgos(Long citaId, Long psicologoId, String hallazgos) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada."));

        validarPertenencia(cita, psicologoId);

        // Lógica de validación
        if (!cita.getEstado().equals("Confirmada")) {
            throw new IllegalStateException("Solo se pueden finalizar citas en estado 'Confirmada'.");
        }

        cita.setHallazgos(hallazgos);
        cita.setEstado("Finalizada");

        Cita actualizada = citaRepository.save(cita);
        return convertToDTO(actualizada);
    }
}
