package pe.edu.upc.backend.controllersTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.*;
import pe.edu.upc.backend.servicesTF.ADMINPsicologoService;
import pe.edu.upc.backend.servicesTF.PSICOLOGOCitaPsicologoService;
import pe.edu.upc.backend.servicesTF.PSICOLOGODisponibilidadHorariaService;
import pe.edu.upc.backend.servicesTF.PSICOLOGOProgresoPsicologicoService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/psicologos/{psicologoId}")
public class PsicologoController {
    // Inyección de servicios
    @Autowired
    private ADMINPsicologoService psicologoService; // Usado para perfil (CRUD básico heredado del Admin)

    @Autowired
    private PSICOLOGOCitaPsicologoService citaPsicologoService;

    @Autowired
    private PSICOLOGODisponibilidadHorariaService disponibilidadService;

    @Autowired
    private PSICOLOGOProgresoPsicologicoService progresoPsicologoService;


    // ===========================================
    // 1. GESTIÓN DEL PERFIL (Lectura y Actualización)
    // ===========================================

    // GET: Obtener perfil del psicólogo
    @GetMapping("/perfil")
    public ResponseEntity<ADMINPsicologoDTO> obtenerPerfil(@PathVariable Long psicologoId) {
        ADMINPsicologoDTO perfil = psicologoService.obtenerPsicologoPorId(psicologoId);
        return new ResponseEntity<>(perfil, HttpStatus.OK);
    }

    // PUT: Actualizar perfil del psicólogo
    @PutMapping("/perfil")
    public ResponseEntity<ADMINPsicologoDTO> actualizarPerfil(@PathVariable Long psicologoId, @RequestBody ADMINPsicologoDTO psicologoDTO) {
        // Usa el servicio de Admin/Base para actualizar el perfil, validando el ID de la URL
        ADMINPsicologoDTO actualizado = psicologoService.actualizarPsicologo(psicologoId, psicologoDTO);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    // ===========================================
    // 2. GESTIÓN DE DISPONIBILIDAD HORARIA
    // ===========================================

    // GET: Obtener toda la disponibilidad del psicólogo
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<PSICOLOGODisponibilidadDTO>> obtenerDisponibilidad(@PathVariable Long psicologoId) {
        List<PSICOLOGODisponibilidadDTO> disponibilidad = disponibilidadService.obtenerDisponibilidadPorPsicologo(psicologoId);
        return new ResponseEntity<>(disponibilidad, HttpStatus.OK);
    }

    // POST: Crear o actualizar un bloque de disponibilidad (ej. Lunes de 9:00 a 17:00)
    @PostMapping("/disponibilidad")
    public ResponseEntity<PSICOLOGODisponibilidadDTO> crearOActualizarDisponibilidad(
            @PathVariable Long psicologoId,
            @RequestBody PSICOLOGODisponibilidadDTO dto) {

        PSICOLOGODisponibilidadDTO guardada = disponibilidadService.crearOActualizarDisponibilidad(psicologoId, dto);
        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    // DELETE: Eliminar un bloque de disponibilidad específico
    @DeleteMapping("/disponibilidad/{disponibilidadId}")
    public ResponseEntity<Void> eliminarDisponibilidad(
            @PathVariable Long psicologoId,
            @PathVariable Long disponibilidadId) {

        disponibilidadService.eliminarDisponibilidad(disponibilidadId, psicologoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ===========================================
    // 3. GESTIÓN DE CITAS
    // ===========================================

    // GET: Obtener próximas citas (Pendientes/Confirmadas)
    @GetMapping("/citas/proximas")
    public ResponseEntity<List<PSICOLOGOCitaResponseDTO>> obtenerProximasCitas(@PathVariable Long psicologoId) {
        List<PSICOLOGOCitaResponseDTO> citas = citaPsicologoService.obtenerProximasCitas(psicologoId);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // GET: Obtener historial de citas (Finalizadas/Canceladas/Rechazadas)
    @GetMapping("/citas/historial")
    public ResponseEntity<List<PSICOLOGOCitaResponseDTO>> obtenerHistorialCitas(@PathVariable Long psicologoId) {
        List<PSICOLOGOCitaResponseDTO> historial = citaPsicologoService.obtenerHistorialCitas(psicologoId);
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }

    // PATCH: Aprobar o rechazar una cita pendiente
    @PatchMapping("/citas/{citaId}/estado")
    public ResponseEntity<PSICOLOGOCitaResponseDTO> cambiarEstadoCita(
            @PathVariable Long psicologoId,
            @PathVariable Long citaId,
            @RequestParam String estado) { // Usar 'Confirmada' o 'Rechazada'

        PSICOLOGOCitaResponseDTO actualizada = citaPsicologoService.cambiarEstadoCita(citaId, psicologoId, estado);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    // PATCH: Finalizar cita y registrar hallazgos/notas
    @PatchMapping("/citas/{citaId}/finalizar")
    public ResponseEntity<PSICOLOGOCitaResponseDTO> finalizarCita(
            @PathVariable Long psicologoId,
            @PathVariable Long citaId,
            @RequestBody String hallazgos) {

        PSICOLOGOCitaResponseDTO finalizada = citaPsicologoService.finalizarCitaYRegistrarHallazgos(citaId, psicologoId, hallazgos);
        return new ResponseEntity<>(finalizada, HttpStatus.OK);
    }

    // ===========================================
    // 4. GESTIÓN DE PROGRESO E INFORMES
    // ===========================================

    // POST: Crear un nuevo informe para una asignación
    @PostMapping("/asignaciones/{asignacionId}/informes")
    public ResponseEntity<PSICOLOGOInformeDTO> crearInforme(
            @PathVariable Long psicologoId,
            @PathVariable Long asignacionId,
            @RequestBody PSICOLOGOInformeDTO informeDTO) {

        PSICOLOGOInformeDTO nuevoInforme = progresoPsicologoService.crearInforme(asignacionId, psicologoId, informeDTO);
        return new ResponseEntity<>(nuevoInforme, HttpStatus.CREATED);
    }

    // GET: Obtener todos los informes de una asignación específica
    @GetMapping("/asignaciones/{asignacionId}/informes")
    public ResponseEntity<List<PSICOLOGOInformeDTO>> obtenerInformesPorAsignacion(
            @PathVariable Long psicologoId,
            @PathVariable Long asignacionId) {

        List<PSICOLOGOInformeDTO> informes = progresoPsicologoService.obtenerInformesPorAsignacion(asignacionId, psicologoId);
        return new ResponseEntity<>(informes, HttpStatus.OK);
    }

    // GET: Obtener el resumen de progreso de un menor
    @GetMapping("/menores/{menorId}/progreso")
    public ResponseEntity<PSICOLOGOProgresoMenorDTO> obtenerProgresoMenor(
            @PathVariable Long psicologoId,
            @PathVariable Long menorId) {

        PSICOLOGOProgresoMenorDTO progreso = progresoPsicologoService.obtenerProgresoMenor(menorId, psicologoId);
        return new ResponseEntity<>(progreso, HttpStatus.OK);
    }
}
