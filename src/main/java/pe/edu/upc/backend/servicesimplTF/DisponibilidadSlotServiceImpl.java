package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.DisponibilidadSlotDTO;
import pe.edu.upc.backend.entitiesTF.Cita;
import pe.edu.upc.backend.entitiesTF.Psicologo;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.repositoriesTF.PsicologoRepository;
import pe.edu.upc.backend.servicesTF.DisponibilidadSlotService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisponibilidadSlotServiceImpl implements DisponibilidadSlotService {

    @Autowired
    private CitaRepository citaRepository;
    
    @Autowired
    private PsicologoRepository psicologoRepository;

    // CONFIGURACIÓN DE HORARIOS (Hardcoded según requerimientos)
    private static final LocalTime HORA_INICIO_LABORAL = LocalTime.of(9, 0);  // 09:00
    private static final LocalTime HORA_FIN_LABORAL = LocalTime.of(18, 0);    // 18:00
    private static final LocalTime INICIO_REFRIGERIO = LocalTime.of(13, 0);   // 13:00
    private static final LocalTime FIN_REFRIGERIO = LocalTime.of(14, 0);      // 14:00
    private static final int DURACION_CITA_HORAS = 1; // 1 hora por cita

    @Override
    public DisponibilidadSlotDTO listarHorariosDisponibles(Long psicologoId, LocalDate fecha) {
        System.out.println("🕐 DisponibilidadSlotService.listarHorariosDisponibles - psicologoId: " + psicologoId + ", fecha: " + fecha);
        
        // 1. Validar que el psicólogo existe
        Psicologo psicologo = psicologoRepository.findById(psicologoId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + psicologoId));
        
        System.out.println("✅ Psicólogo encontrado: " + psicologo.getNombre() + " " + psicologo.getApellido());
        
        // 2. Generar todos los slots posibles del día
        List<LocalTime> todosLosSlots = generarSlotsPosibles();
        System.out.println("📋 Slots totales generados: " + todosLosSlots.size() + " - " + todosLosSlots);
        
        // 3. Buscar citas ocupadas (excluyendo canceladas)
        List<Cita> citasOcupadas = citaRepository.findByAsignacion_Psicologo_PsicologoIdAndFechaAndEstadoNot(
                psicologoId, fecha, "Cancelada");
        
        System.out.println("🚫 Citas ocupadas encontradas: " + citasOcupadas.size());
        
        // 4. Extraer horarios ocupados
        List<LocalTime> horariosOcupados = citasOcupadas.stream()
                .map(Cita::getHoraInicio)
                .collect(Collectors.toList());
        
        System.out.println("⏰ Horarios ocupados: " + horariosOcupados);
        
        // 5. Filtrar slots disponibles (eliminar ocupados)
        List<LocalTime> horariosDisponibles = todosLosSlots.stream()
                .filter(slot -> !horariosOcupados.contains(slot))
                .collect(Collectors.toList());
        
        System.out.println("✅ Horarios disponibles: " + horariosDisponibles);
        
        // 6. Construir respuesta
        DisponibilidadSlotDTO respuesta = new DisponibilidadSlotDTO();
        respuesta.setPsicologoId(psicologoId);
        respuesta.setNombrePsicologo(psicologo.getNombre() + " " + psicologo.getApellido());
        respuesta.setFecha(fecha);
        respuesta.setHorariosDisponibles(horariosDisponibles);
        respuesta.setTotalSlots(todosLosSlots.size());
        respuesta.setSlotsOcupados(horariosOcupados.size());
        respuesta.setSlotsDisponibles(horariosDisponibles.size());
        
        System.out.println("📊 Resumen - Total: " + todosLosSlots.size() + 
                          ", Ocupados: " + horariosOcupados.size() + 
                          ", Disponibles: " + horariosDisponibles.size());
        
        return respuesta;
    }

    /**
     * Genera todos los slots posibles de un día laboral
     * Reglas de negocio:
     * - Horario: 09:00 a 18:00
     * - Duración: 1 hora por slot
     * - Refrigerio: 13:00 a 14:00 (no disponible)
     */
    private List<LocalTime> generarSlotsPosibles() {
        List<LocalTime> slots = new ArrayList<>();
        
        LocalTime horaActual = HORA_INICIO_LABORAL;
        
        while (horaActual.isBefore(HORA_FIN_LABORAL)) {
            // Saltar el horario de refrigerio
            if (horaActual.equals(INICIO_REFRIGERIO)) {
                horaActual = FIN_REFRIGERIO;
                continue;
            }
            
            slots.add(horaActual);
            horaActual = horaActual.plusHours(DURACION_CITA_HORAS);
        }
        
        return slots;
    }
}
