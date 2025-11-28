package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PADRECitaRequestDTO;
import pe.edu.upc.backend.dtosTF.PADRECitaResponseDTO;
import pe.edu.upc.backend.dtosTF.PADRESolicitudCitaDTO;
import pe.edu.upc.backend.entitiesTF.Asignacion;
import pe.edu.upc.backend.entitiesTF.Cita;
import pe.edu.upc.backend.entitiesTF.Menor;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.entitiesTF.Psicologo;
import pe.edu.upc.backend.repositoriesTF.AsignacionRepository;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.repositoriesTF.MenorRepository;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.repositoriesTF.PsicologoRepository;
import pe.edu.upc.backend.servicesTF.PADRECitaPadreService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PADRECitaPadreServiceImpl implements PADRECitaPadreService {

    @Autowired private CitaRepository citaRepository;
    @Autowired private AsignacionRepository asignacionRepository;
    @Autowired private MenorRepository menorRepository;
    @Autowired private PadreRepository padreRepository;
    @Autowired private PsicologoRepository psicologoRepository;

    // Métodos de conversión completos:
    private PADRECitaResponseDTO convertToDTO(Cita entity) {
        PADRECitaResponseDTO dto = new PADRECitaResponseDTO();

        dto.setCitaId(entity.getCitaId());
        dto.setAsignacionId(entity.getAsignacion().getAsignacionId());

        dto.setNombreMenor(entity.getAsignacion().getMenor().getNombre());
        dto.setNombrePsicologo(entity.getAsignacion().getPsicologo().getNombre());

        // Asignación directa: ambos usan LocalDate y LocalTime
        dto.setFecha(entity.getFecha());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFin(entity.getHoraFin());
        dto.setMotivo(entity.getMotivo());
        dto.setHallazgos(entity.getHallazgos());
        dto.setEstado(entity.getEstado()); // Mapeo del nuevo campo Estado

        return dto;
    }

    // --- Implementación ---

    @Override
    public PADRECitaResponseDTO solicitarCita(Long padreId, PADRECitaRequestDTO requestDTO) {
        System.out.println("📅 PADRECitaPadreService.solicitarCita - padreId recibido: " + padreId);
        System.out.println("📋 Asignación ID solicitada: " + requestDTO.getAsignacionId());
        
        // Validación de fecha: no se pueden agendar citas en fechas pasadas
        if (requestDTO.getFecha() != null && requestDTO.getFecha().isBefore(LocalDate.now())) {
            System.err.println("❌ Error: Se intentó agendar una cita en fecha pasada: " + requestDTO.getFecha());
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas.");
        }
        
        // Validación de existencia de Asignación
        Asignacion asignacion = asignacionRepository.findById(requestDTO.getAsignacionId())
                .orElseThrow(() -> new EntityNotFoundException("Asignación no encontrada con ID: " + requestDTO.getAsignacionId()));

        System.out.println("✅ Asignación encontrada:");
        System.out.println("   - Asignación ID: " + asignacion.getAsignacionId());
        System.out.println("   - Padre ID en asignación: " + asignacion.getPadre().getPadreId());
        System.out.println("   - Padre ID recibido: " + padreId);

        // **Validación de seguridad:** Asegurar que la asignación pertenezca al padre
        // Comparar IDs usando Long.equals() para evitar problemas de comparación de objetos
        Long asignacionPadreId = asignacion.getPadre().getPadreId();
        if (asignacionPadreId == null || !asignacionPadreId.equals(padreId)) {
            System.err.println("❌ Error de seguridad: La asignación (Padre ID: " + asignacionPadreId + ") no pertenece al padre autenticado (ID: " + padreId + ")");
            throw new SecurityException("Acceso denegado: La asignación no pertenece a este padre.");
        }
        
        System.out.println("✅ Validación de seguridad exitosa - La asignación pertenece al padre");

        //Lógica de validación de disponibilidad del Psicologo (usando DISPONIBILIDAD_HORARIA y CITA)
        // CRÍTICO: Aquí DEBE haber lógica para evitar doble reserva.

        Cita nuevaCita = new Cita();
        nuevaCita.setAsignacion(asignacion);

        // Mapeo de campos de la solicitud
        // FECHA: Asignación directa (ambos usan LocalDate)
        nuevaCita.setFecha(requestDTO.getFecha());
        
        // HORA: Asignación directa (ambos usan LocalTime)
        nuevaCita.setHoraInicio(requestDTO.getHoraInicio());
        nuevaCita.setHoraFin(requestDTO.getHoraFin());
        
        nuevaCita.setMotivo(requestDTO.getMotivo());

        // **CRÍTICO:** El estado inicial de una solicitud SIEMPRE debe ser "Pendiente"
        // Esto permite que el psicólogo apruebe o rechace la cita
        nuevaCita.setEstado("Pendiente");
        
        // Validación explícita antes de guardar
        if (!"Pendiente".equals(nuevaCita.getEstado())) {
            System.err.println("❌ ERROR CRÍTICO: El estado de la cita no es 'Pendiente' antes de guardar: " + nuevaCita.getEstado());
            throw new IllegalStateException("El estado inicial de una cita debe ser 'Pendiente'.");
        }

        Cita guardada = citaRepository.save(nuevaCita);
        
        // Validación después de guardar para asegurar que el estado se mantuvo
        if (!"Pendiente".equals(guardada.getEstado())) {
            System.err.println("❌ ERROR CRÍTICO: El estado de la cita cambió después de guardar. Esperado: 'Pendiente', Actual: " + guardada.getEstado());
            throw new IllegalStateException("El estado de la cita cambió inesperadamente después de guardar.");
        }
        
        System.out.println("✅ Cita creada con estado 'Pendiente' - ID: " + guardada.getCitaId());
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
        System.out.println("📅 PADRECitaPadreService.obtenerProximasCitas - padreId: " + padreId);
        
        // Obtener todas las citas del padre
        List<Cita> todasLasCitas = citaRepository.findByAsignacion_Padre_PadreId(padreId);
        System.out.println("📋 Total de citas encontradas: " + todasLasCitas.size());
        
        // Usar LocalDate para comparar fechas sin problemas de timezone
        LocalDate hoy = LocalDate.now();
        
        List<PADRECitaResponseDTO> proximasCitas = todasLasCitas.stream()
                // Filtra por fecha futura o igual a hoy Y estados: Pendiente, Programada, Confirmada
                .filter(c -> {
                    // Comparar LocalDate directamente (sin necesidad de limpiar horas)
                    boolean fechaFutura = !c.getFecha().isBefore(hoy); // >= hoy
                    String estado = c.getEstado();
                    boolean estadoValido = "Pendiente".equals(estado) || 
                                          "Programada".equals(estado) || 
                                          "Confirmada".equals(estado);
                    
                    if (fechaFutura && estadoValido) {
                        System.out.println("✅ Cita incluida - ID: " + c.getCitaId() + ", Estado: " + estado + ", Fecha: " + c.getFecha());
                    }
                    
                    return fechaFutura && estadoValido;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        System.out.println("✅ Próximas citas filtradas: " + proximasCitas.size());
        return proximasCitas;
    }

    @Override
    public PADRECitaResponseDTO cancelarCita(Long citaId, Long padreId) {
        System.out.println("🚫 PADRECitaPadreService.cancelarCita - citaId: " + citaId + ", padreId: " + padreId);
        
        // Buscar la cita
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con ID: " + citaId));

        System.out.println("✅ Cita encontrada:");
        System.out.println("   - Cita ID: " + cita.getCitaId());
        System.out.println("   - Estado actual: " + cita.getEstado());
        
        // Validación de seguridad: comparar IDs usando Long.equals() con null check
        Long citaPadreId = cita.getAsignacion().getPadre().getPadreId();
        System.out.println("   - Padre ID en cita: " + citaPadreId);
        System.out.println("   - Padre ID recibido: " + padreId);
        
        if (citaPadreId == null || !citaPadreId.equals(padreId)) {
            System.err.println("❌ Error de seguridad: La cita (Padre ID: " + citaPadreId + ") no pertenece al padre autenticado (ID: " + padreId + ")");
            throw new SecurityException("Acceso denegado: La cita no pertenece a este padre.");
        }
        
        System.out.println("✅ Validación de seguridad exitosa - La cita pertenece al padre");

        // Lógica de cancelación: solo se pueden cancelar citas en estados Pendiente, Programada o Confirmada
        String estadoActual = cita.getEstado();
        if ("Finalizada".equals(estadoActual) || "Cancelada".equals(estadoActual)) {
            throw new IllegalStateException("No se pueden cancelar citas que ya están " + estadoActual + ".");
        }
        
        // Cambiar el estado a Cancelada
        cita.setEstado("Cancelada");
        System.out.println("✅ Estado de cita cambiado a: Cancelada");

        Cita actualizada = citaRepository.save(cita);
        return convertToDTO(actualizada);
    }

    /**
     * NUEVO MÉTODO: Solicitar cita directamente eligiendo menor y psicólogo
     * Implementa el modelo autoservicio sin dependencia de asignaciones manuales
     */
    public PADRECitaResponseDTO solicitarCitaDirecta(Long padreId, PADRESolicitudCitaDTO solicitudDTO) {
        System.out.println("🆕 PADRECitaPadreService.solicitarCitaDirecta - padreId: " + padreId);
        System.out.println("📋 Solicitud: " + solicitudDTO);
        
        // Validación de fecha: no se pueden agendar citas en fechas pasadas
        if (solicitudDTO.getFecha() != null && solicitudDTO.getFecha().isBefore(LocalDate.now())) {
            System.err.println("❌ Error: Se intentó agendar una cita en fecha pasada: " + solicitudDTO.getFecha());
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas.");
        }
        
        // 1. Validar que el menor pertenezca al padre autenticado
        Menor menor = menorRepository.findById(solicitudDTO.getMenorId())
                .orElseThrow(() -> new EntityNotFoundException("Menor no encontrado con ID: " + solicitudDTO.getMenorId()));
        
        if (!menor.getPadre().getPadreId().equals(padreId)) {
            System.err.println("❌ Error de seguridad: El menor (Padre ID: " + menor.getPadre().getPadreId() + ") no pertenece al padre autenticado (ID: " + padreId + ")");
            throw new SecurityException("Acceso denegado: El menor no pertenece a este padre.");
        }
        
        System.out.println("✅ Menor validado - ID: " + menor.getMenorId() + ", Nombre: " + menor.getNombre());
        
        // 2. Validar que el psicólogo existe
        Psicologo psicologo = psicologoRepository.findById(solicitudDTO.getPsicologoId())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + solicitudDTO.getPsicologoId()));
        
        System.out.println("✅ Psicólogo validado - ID: " + psicologo.getPsicologoId() + ", Nombre: " + psicologo.getNombre());
        
        // 3. Buscar si ya existe una asignación entre este menor y psicólogo
        Asignacion asignacion = asignacionRepository.findByMenor_MenorIdAndPsicologo_PsicologoId(
                solicitudDTO.getMenorId(), solicitudDTO.getPsicologoId())
                .orElse(null);
        
        // 4. Si no existe asignación, crearla automáticamente
        if (asignacion == null) {
            System.out.println("📋 No existe asignación previa - Creando nueva asignación automáticamente");
            
            Padre padre = padreRepository.findById(padreId)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + padreId));
            
            asignacion = new Asignacion();
            asignacion.setPadre(padre);
            asignacion.setMenor(menor);
            asignacion.setPsicologo(psicologo);
            asignacion.setEstado("Activa"); // Estado por defecto para asignaciones automáticas
            asignacion.setFechaAsignacion(LocalDate.now());
            
            asignacion = asignacionRepository.save(asignacion);
            System.out.println("✅ Nueva asignación creada - ID: " + asignacion.getAsignacionId());
        } else {
            System.out.println("✅ Asignación existente encontrada - ID: " + asignacion.getAsignacionId() + ", Estado: " + asignacion.getEstado());
            
            // Si la asignación existe pero está inactiva, reactivarla
            if (!"Activa".equals(asignacion.getEstado())) {
                asignacion.setEstado("Activa");
                asignacion = asignacionRepository.save(asignacion);
                System.out.println("✅ Asignación reactivada");
            }
        }
        
        // 5. Crear la cita vinculada a la asignación
        Cita nuevaCita = new Cita();
        nuevaCita.setAsignacion(asignacion);
        
        // FECHA: Asignación directa (ambos son LocalDate)
        nuevaCita.setFecha(solicitudDTO.getFecha());
        
        // HORA: Parsear String a LocalTime (formato "HH:mm")
        nuevaCita.setHoraInicio(LocalTime.parse(solicitudDTO.getHoraInicio()));
        nuevaCita.setHoraFin(LocalTime.parse(solicitudDTO.getHoraFin()));
        
        nuevaCita.setMotivo(solicitudDTO.getMotivo());
        
        // **CRÍTICO:** El estado inicial de una solicitud SIEMPRE debe ser "Pendiente"
        // Esto permite que el psicólogo apruebe o rechace la cita
        nuevaCita.setEstado("Pendiente");
        
        // Validación explícita antes de guardar
        if (!"Pendiente".equals(nuevaCita.getEstado())) {
            System.err.println("❌ ERROR CRÍTICO: El estado de la cita no es 'Pendiente' antes de guardar: " + nuevaCita.getEstado());
            throw new IllegalStateException("El estado inicial de una cita debe ser 'Pendiente'.");
        }
        
        Cita citaGuardada = citaRepository.save(nuevaCita);
        
        // Validación después de guardar para asegurar que el estado se mantuvo
        if (!"Pendiente".equals(citaGuardada.getEstado())) {
            System.err.println("❌ ERROR CRÍTICO: El estado de la cita cambió después de guardar. Esperado: 'Pendiente', Actual: " + citaGuardada.getEstado());
            throw new IllegalStateException("El estado de la cita cambió inesperadamente después de guardar.");
        }
        
        System.out.println("✅ Cita creada exitosamente con estado 'Pendiente' - ID: " + citaGuardada.getCitaId());
        return convertToDTO(citaGuardada);
    }
}
