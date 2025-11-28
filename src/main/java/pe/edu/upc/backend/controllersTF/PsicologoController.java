package pe.edu.upc.backend.controllersTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.*;
import pe.edu.upc.backend.servicesTF.ADMINPsicologoService;
import pe.edu.upc.backend.servicesTF.PSICOLOGOCitaPsicologoService;
import pe.edu.upc.backend.servicesTF.PSICOLOGODisponibilidadHorariaService;
import pe.edu.upc.backend.servicesTF.PSICOLOGOProgresoPsicologicoService;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.repositoriesTF.AsignacionRepository;
import pe.edu.upc.backend.repositoriesTF.EvaluacionPsicologoRepository;
import pe.edu.upc.backend.repositoriesTF.PsicologoRepository;
import pe.edu.upc.backend.entitiesTF.Cita;
import pe.edu.upc.backend.entitiesTF.Asignacion;
import pe.edu.upc.backend.entitiesTF.Psicologo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private EvaluacionPsicologoRepository evaluacionPsicologoRepository;
    
    @Autowired
    private PsicologoRepository psicologoRepository;
    
    // ===========================================
    // MÉTODO HELPER: Obtener psicologoId desde el usuario autenticado
    // ===========================================
    /**
     * Obtiene el ID del psicólogo autenticado basándose en su email (username)
     * Esto asegura que siempre se use el ID correcto, independientemente del ID en la URL
     */
    private Long obtenerPsicologoIdAutenticado() {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Buscar el Psicólogo por su email
            Psicologo psicologo = psicologoRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado para el usuario: " + username));
            
            return psicologo.getPsicologoId();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener psicologoId autenticado: " + e.getMessage());
            throw new SecurityException("No se pudo identificar al psicólogo autenticado.");
        }
    }


    // ===========================================
    // 0. DASHBOARD - MÉTRICAS Y RESUMEN
    // ===========================================

    // GET: Obtener métricas del dashboard del psicólogo
    @GetMapping("/dashboard")
    public ResponseEntity<PSICOLOGODashboardDTO> obtenerDashboard(@PathVariable Long psicologoId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            LocalDate hoy = LocalDate.now();

            // 1. Contar citas de hoy
            long citasHoy = citaRepository.countByAsignacion_Psicologo_PsicologoIdAndFecha(psicologoIdReal, hoy);

            // 2. Contar pacientes activos (asignaciones activas)
            long pacientesActivos = asignacionRepository.countByPsicologo_PsicologoIdAndEstado(psicologoIdReal, "Activa");

            // 3. Obtener promedio de calificaciones y total de evaluaciones
            Double calificacionPromedio = evaluacionPsicologoRepository.obtenerPromedioCalificacionPorPsicologo(psicologoIdReal);
            if (calificacionPromedio == null) {
                calificacionPromedio = 0.0;
            }
            Long totalEvaluaciones = evaluacionPsicologoRepository.countByPsicologo_PsicologoId(psicologoIdReal);
            if (totalEvaluaciones == null) {
                totalEvaluaciones = 0L;
            }

            // 4. Obtener lista de citas de hoy
            List<Cita> citasHoyEntities = citaRepository.findByAsignacion_Psicologo_PsicologoIdAndFecha(psicologoIdReal, hoy);
            List<PSICOLOGOCitaResponseDTO> listaCitasHoy = citasHoyEntities.stream()
                    .map(cita -> {
                        PSICOLOGOCitaResponseDTO dto = new PSICOLOGOCitaResponseDTO();
                        
                        // Asignación y entidades relacionadas
                        var asignacion = cita.getAsignacion();
                        var psicologo = asignacion.getPsicologo();
                        var padre = asignacion.getPadre();
                        var menor = asignacion.getMenor();

                        // Datos de Cita
                        dto.setCitaId(cita.getCitaId());
                        dto.setFechaHoraCita(cita.getFecha());
                        dto.setMotivoCita(cita.getMotivo());
                        dto.setEstado(cita.getEstado());
                        dto.setHallazgos(cita.getHallazgos());

                        // Información de Asignación
                        dto.setAsignacionId(asignacion.getAsignacionId());

                        // Información del Psicólogo
                        dto.setPsicologoId(psicologo.getPsicologoId());
                        dto.setNombrePsicologo(psicologo.getNombre() + " " + psicologo.getApellido());

                        // Información del Padre
                        dto.setPadreId(padre.getPadreId());
                        dto.setNombreCompletoPadre(padre.getNombre() + " " + padre.getApellido());
                        dto.setEmailPadre(padre.getEmail());

                        // Información del Menor
                        dto.setMenorId(menor.getMenorId());
                        dto.setNombreCompletoMenor(menor.getNombre() + " " + menor.getApellido());
                        dto.setFechaNacimientoMenor(menor.getFechaNacimiento());

                        return dto;
                    })
                    .collect(Collectors.toList());

            // Crear y retornar el DTO del dashboard
            PSICOLOGODashboardDTO dashboard = new PSICOLOGODashboardDTO();
            dashboard.setCitasHoy(citasHoy);
            dashboard.setPacientesActivos(pacientesActivos);
            dashboard.setCalificacionPromedio(calificacionPromedio);
            dashboard.setTotalEvaluaciones(totalEvaluaciones);
            dashboard.setListaCitasHoy(listaCitasHoy);

            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener dashboard del psicólogo: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 1. GESTIÓN DEL PERFIL (Lectura y Actualización)
    // ===========================================

    // GET: Obtener perfil del psicólogo
    @GetMapping("/perfil")
    public ResponseEntity<ADMINPsicologoDTO> obtenerPerfil(@PathVariable Long psicologoId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            ADMINPsicologoDTO perfil = psicologoService.obtenerPsicologoPorId(psicologoIdReal);
            return new ResponseEntity<>(perfil, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener perfil: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT: Actualizar perfil del psicólogo
    @PutMapping("/perfil")
    public ResponseEntity<ADMINPsicologoDTO> actualizarPerfil(@PathVariable Long psicologoId, @RequestBody ADMINPsicologoDTO psicologoDTO) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            // Usa el servicio de Admin/Base para actualizar el perfil con el ID real
            ADMINPsicologoDTO actualizado = psicologoService.actualizarPsicologo(psicologoIdReal, psicologoDTO);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar perfil: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 2. GESTIÓN DE DISPONIBILIDAD HORARIA
    // ===========================================

    // GET: Obtener toda la disponibilidad del psicólogo
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<PSICOLOGODisponibilidadDTO>> obtenerDisponibilidad(@PathVariable Long psicologoId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            List<PSICOLOGODisponibilidadDTO> disponibilidad = disponibilidadService.obtenerDisponibilidadPorPsicologo(psicologoIdReal);
            return new ResponseEntity<>(disponibilidad, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener disponibilidad: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST: Crear o actualizar un bloque de disponibilidad (ej. Lunes de 9:00 a 17:00)
    @PostMapping("/disponibilidad")
    public ResponseEntity<PSICOLOGODisponibilidadDTO> crearOActualizarDisponibilidad(
            @PathVariable Long psicologoId,
            @RequestBody PSICOLOGODisponibilidadDTO dto) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            PSICOLOGODisponibilidadDTO guardada = disponibilidadService.crearOActualizarDisponibilidad(psicologoIdReal, dto);
            return new ResponseEntity<>(guardada, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error al crear/actualizar disponibilidad: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE: Eliminar un bloque de disponibilidad específico
    @DeleteMapping("/disponibilidad/{disponibilidadId}")
    public ResponseEntity<Void> eliminarDisponibilidad(
            @PathVariable Long psicologoId,
            @PathVariable Long disponibilidadId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            disponibilidadService.eliminarDisponibilidad(disponibilidadId, psicologoIdReal);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar disponibilidad: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 3. GESTIÓN DE CITAS
    // ===========================================

    // GET: Obtener próximas citas (Pendientes/Confirmadas)
    @GetMapping("/citas/proximas")
    public ResponseEntity<List<PSICOLOGOCitaResponseDTO>> obtenerProximasCitas(@PathVariable Long psicologoId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            List<PSICOLOGOCitaResponseDTO> citas = citaPsicologoService.obtenerProximasCitas(psicologoIdReal);
            return new ResponseEntity<>(citas, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener próximas citas: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET: Obtener historial de citas (Finalizadas/Canceladas/Rechazadas)
    @GetMapping("/citas/historial")
    public ResponseEntity<List<PSICOLOGOCitaResponseDTO>> obtenerHistorialCitas(@PathVariable Long psicologoId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            List<PSICOLOGOCitaResponseDTO> historial = citaPsicologoService.obtenerHistorialCitas(psicologoIdReal);
            return new ResponseEntity<>(historial, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener historial de citas: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PATCH: Aprobar o rechazar una cita pendiente
    @PatchMapping("/citas/{citaId}/estado")
    public ResponseEntity<PSICOLOGOCitaResponseDTO> cambiarEstadoCita(
            @PathVariable Long psicologoId,
            @PathVariable Long citaId,
            @RequestParam String estado) { // Usar 'Confirmada' o 'Rechazada'
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            PSICOLOGOCitaResponseDTO actualizada = citaPsicologoService.cambiarEstadoCita(citaId, psicologoIdReal, estado);
            return new ResponseEntity<>(actualizada, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al cambiar estado de cita: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PATCH: Finalizar cita y registrar hallazgos/notas
    @PatchMapping("/citas/{citaId}/finalizar")
    public ResponseEntity<PSICOLOGOCitaResponseDTO> finalizarCita(
            @PathVariable Long psicologoId,
            @PathVariable Long citaId,
            @RequestBody String hallazgos) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            PSICOLOGOCitaResponseDTO finalizada = citaPsicologoService.finalizarCitaYRegistrarHallazgos(citaId, psicologoIdReal, hallazgos);
            return new ResponseEntity<>(finalizada, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al finalizar cita: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 4. GESTIÓN DE ASIGNACIONES
    // ===========================================

    // GET: Obtener todas las asignaciones del psicólogo
    @GetMapping("/asignaciones")
    public ResponseEntity<List<ADMINAsignacionDTO>> obtenerMisAsignaciones(@PathVariable Long psicologoId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            List<Asignacion> asignaciones = asignacionRepository.findByPsicologo_PsicologoId(psicologoIdReal);
            
            List<ADMINAsignacionDTO> asignacionesDTO = asignaciones.stream()
                    .map(asignacion -> {
                        ADMINAsignacionDTO dto = new ADMINAsignacionDTO();
                        dto.setAsignacionId(asignacion.getAsignacionId());
                        dto.setPadreId(asignacion.getPadre().getPadreId());
                        dto.setMenorId(asignacion.getMenor().getMenorId());
                        dto.setPsicologoId(asignacion.getPsicologo().getPsicologoId());
                        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
                        dto.setEstado(asignacion.getEstado());
                        
                        // Enriquecer con nombres si están disponibles
                        if (asignacion.getPadre() != null) {
                            dto.setNombrePadre(asignacion.getPadre().getNombre() + " " + asignacion.getPadre().getApellido());
                        }
                        if (asignacion.getMenor() != null) {
                            dto.setNombreMenor(asignacion.getMenor().getNombre() + " " + asignacion.getMenor().getApellido());
                        }
                        if (asignacion.getPsicologo() != null) {
                            dto.setNombrePsicologo(asignacion.getPsicologo().getNombre() + " " + asignacion.getPsicologo().getApellido());
                        }
                        
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return new ResponseEntity<>(asignacionesDTO, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener asignaciones del psicólogo: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 5. GESTIÓN DE PROGRESO E INFORMES
    // ===========================================

    // POST: Crear un nuevo informe para una asignación
    @PostMapping("/asignaciones/{asignacionId}/informes")
    public ResponseEntity<PSICOLOGOInformeDTO> crearInforme(
            @PathVariable Long psicologoId,
            @PathVariable Long asignacionId,
            @RequestBody PSICOLOGOInformeDTO informeDTO) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            PSICOLOGOInformeDTO nuevoInforme = progresoPsicologoService.crearInforme(asignacionId, psicologoIdReal, informeDTO);
            return new ResponseEntity<>(nuevoInforme, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error al crear informe: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET: Obtener todos los informes de una asignación específica
    @GetMapping("/asignaciones/{asignacionId}/informes")
    public ResponseEntity<List<PSICOLOGOInformeDTO>> obtenerInformesPorAsignacion(
            @PathVariable Long psicologoId,
            @PathVariable Long asignacionId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            List<PSICOLOGOInformeDTO> informes = progresoPsicologoService.obtenerInformesPorAsignacion(asignacionId, psicologoIdReal);
            return new ResponseEntity<>(informes, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener informes por asignación: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET: Obtener el resumen de progreso de un menor
    @GetMapping("/menores/{menorId}/progreso")
    public ResponseEntity<PSICOLOGOProgresoMenorDTO> obtenerProgresoMenor(
            @PathVariable Long psicologoId,
            @PathVariable Long menorId) {
        try {
            // Obtener el ID real del psicólogo autenticado (ignorar el de la URL)
            Long psicologoIdReal = obtenerPsicologoIdAutenticado();
            
            PSICOLOGOProgresoMenorDTO progreso = progresoPsicologoService.obtenerProgresoMenor(menorId, psicologoIdReal);
            return new ResponseEntity<>(progreso, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener progreso del menor: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
