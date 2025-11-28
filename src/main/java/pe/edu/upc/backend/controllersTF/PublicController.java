package pe.edu.upc.backend.controllersTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.ADMINPsicologoDTO;
import pe.edu.upc.backend.dtosTF.DisponibilidadSlotDTO;
import pe.edu.upc.backend.servicesTF.ADMINPsicologoService;
import pe.edu.upc.backend.servicesTF.DisponibilidadSlotService;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private ADMINPsicologoService psicologoService;
    
    @Autowired
    private DisponibilidadSlotService disponibilidadSlotService;

    /**
     * Endpoint público para obtener la lista de psicólogos disponibles
     * Usado por los padres para seleccionar psicólogo al solicitar citas
     */
    @GetMapping("/psicologos")
    public ResponseEntity<List<ADMINPsicologoDTO>> obtenerPsicologosDisponibles() {
        try {
            List<ADMINPsicologoDTO> psicologos = psicologoService.obtenerTodosLosPsicologos();
            
            // Filtrar solo psicólogos activos (opcional)
            List<ADMINPsicologoDTO> psicologosActivos = psicologos.stream()
                    .filter(p -> p.getUsuarioActivo() == null || p.getUsuarioActivo()) // null o true = activo
                    .toList();
            
            System.out.println("✅ Psicólogos disponibles obtenidos - Cantidad: " + psicologosActivos.size());
            return new ResponseEntity<>(psicologosActivos, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("❌ Error al obtener psicólogos: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint público para consultar disponibilidad de horarios
     * Usado por los padres para ver slots libres antes de solicitar cita
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<DisponibilidadSlotDTO> consultarDisponibilidad(
            @RequestParam Long psicologoId,
            @RequestParam String fecha) { // Formato: YYYY-MM-DD
        try {
            LocalDate fechaConsulta = LocalDate.parse(fecha);
            
            // Validación: no consultar fechas pasadas
            if (fechaConsulta.isBefore(LocalDate.now())) {
                System.err.println("❌ Error: Se intentó consultar disponibilidad en fecha pasada: " + fechaConsulta);
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            DisponibilidadSlotDTO disponibilidad = disponibilidadSlotService.listarHorariosDisponibles(psicologoId, fechaConsulta);
            
            System.out.println("✅ Disponibilidad consultada - Psicólogo ID: " + psicologoId + 
                              ", Fecha: " + fechaConsulta + 
                              ", Slots disponibles: " + disponibilidad.getSlotsDisponibles());
            
            return new ResponseEntity<>(disponibilidad, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("❌ Error al consultar disponibilidad: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
