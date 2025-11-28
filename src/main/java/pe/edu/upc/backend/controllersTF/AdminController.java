package pe.edu.upc.backend.controllersTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.ADMINAsignacionDTO;
import pe.edu.upc.backend.dtosTF.ADMINCitaListDTO;
import pe.edu.upc.backend.dtosTF.ADMINEvaluacionListDTO;
import pe.edu.upc.backend.dtosTF.ADMINPadreDTO;
import pe.edu.upc.backend.dtosTF.ADMINPsicologoDTO;
import pe.edu.upc.backend.dtosTF.ADMINRecursoEducativoDTO;
import pe.edu.upc.backend.dtosTF.DashboardMetricsDTO;
import pe.edu.upc.backend.dtosTF.PADREMenorDTO;
import pe.edu.upc.backend.servicesTF.ADMINAsignacionService;
import pe.edu.upc.backend.servicesTF.ADMINCitaService;
import pe.edu.upc.backend.servicesTF.ADMINEvaluacionService;
import pe.edu.upc.backend.servicesTF.ADMINMetricsService;
import pe.edu.upc.backend.servicesTF.ADMINPadreService;
import pe.edu.upc.backend.servicesTF.ADMINPsicologoService;
import pe.edu.upc.backend.servicesTF.ADMINRecursoEducativoService;
import pe.edu.upc.backend.servicesTF.PADREMenorService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    // Inyección de todos los servicios del rol Administrador
    @Autowired
    private ADMINAsignacionService ADMINAsignacionService;

    @Autowired
    private ADMINPadreService ADMINPadreService;

    @Autowired
    private ADMINPsicologoService ADMINPsicologoService;

    @Autowired
    private ADMINRecursoEducativoService ADMINRecursoEducativoService;

    @Autowired
    private PADREMenorService PADREMenorService;

    @Autowired
    private ADMINCitaService ADMINCitaService;

    @Autowired
    private ADMINMetricsService ADMINMetricsService;

    @Autowired
    private ADMINEvaluacionService ADMINEvaluacionService;

    // ===========================================
    // 1. GESTIÓN DE ASIGNACIONES (Ruta: /api/admin/asignaciones)
    // ===========================================

    // POST: Crear nueva Asignación (C)
    @PostMapping("/asignaciones")
    public ResponseEntity<ADMINAsignacionDTO> crearAsignacion(@RequestBody ADMINAsignacionDTO ADMINAsignacionDTO) {
        ADMINAsignacionDTO nuevaAsignacion = ADMINAsignacionService.crearAsignacion(ADMINAsignacionDTO);
        return new ResponseEntity<>(nuevaAsignacion, HttpStatus.CREATED);
    }

    // GET: Obtener todas las Asignaciones (R)
    @GetMapping("/asignaciones")
    public ResponseEntity<List<ADMINAsignacionDTO>> obtenerTodasLasAsignaciones() {
        List<ADMINAsignacionDTO> asignaciones = ADMINAsignacionService.obtenerTodasLasAsignaciones();
        return new ResponseEntity<>(asignaciones, HttpStatus.OK);
    }

    // GET: Obtener Asignación por ID (R)
    @GetMapping("/asignaciones/{id}")
    public ResponseEntity<ADMINAsignacionDTO> obtenerAsignacionPorId(@PathVariable Long id) {
        ADMINAsignacionDTO asignacion = ADMINAsignacionService.obtenerAsignacionPorId(id);
        return new ResponseEntity<>(asignacion, HttpStatus.OK);
    }

    // PATCH: Cambiar el estado de la Asignación (U - Pausar/Reactivar/Finalizar)
    @PatchMapping("/asignaciones/{id}/estado")
    public ResponseEntity<ADMINAsignacionDTO> cambiarEstadoAsignacion(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        ADMINAsignacionDTO asignacionActualizada = ADMINAsignacionService.cambiarEstadoAsignacion(id, nuevoEstado);
        return new ResponseEntity<>(asignacionActualizada, HttpStatus.OK);
    }

    // PUT: Actualizar Asignación completa (U)
    @PutMapping("/asignaciones/{id}")
    public ResponseEntity<ADMINAsignacionDTO> actualizarAsignacion(@PathVariable Long id, @RequestBody ADMINAsignacionDTO ADMINAsignacionDTO) {
        ADMINAsignacionDTO asignacionActualizada = ADMINAsignacionService.actualizarAsignacion(id, ADMINAsignacionDTO);
        return new ResponseEntity<>(asignacionActualizada, HttpStatus.OK);
    }

    // DELETE: Eliminar Asignación (D)
    @DeleteMapping("/asignaciones/{id}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id) {
        ADMINAsignacionService.eliminarAsignacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ===========================================
    // 2. GESTIÓN DE PADRES (Ruta: /api/admin/padres)
    // ===========================================

    // POST: Crear nuevo Padre (C)
    @PostMapping("/padres")
    public ResponseEntity<ADMINPadreDTO> crearPadre(@RequestBody ADMINPadreDTO ADMINPadreDTO) {
        ADMINPadreDTO nuevoPadre = ADMINPadreService.crearPadre(ADMINPadreDTO);
        return new ResponseEntity<>(nuevoPadre, HttpStatus.CREATED);
    }

    // GET: Obtener todos los Padres (R)
    @GetMapping("/padres")
    public ResponseEntity<List<ADMINPadreDTO>> obtenerTodosLosPadres() {
        List<ADMINPadreDTO> padres = ADMINPadreService.obtenerTodosLosPadres();
        return new ResponseEntity<>(padres, HttpStatus.OK);
    }

    // GET: Obtener Padre por ID (R)
    @GetMapping("/padres/{id}")
    public ResponseEntity<ADMINPadreDTO> obtenerPadrePorId(@PathVariable Long id) {
        ADMINPadreDTO padre = ADMINPadreService.obtenerPadrePorId(id);
        return new ResponseEntity<>(padre, HttpStatus.OK);
    }

    // GET: Buscar Padres por nombre/apellido (R)
    @GetMapping("/padres/buscar")
    public ResponseEntity<List<ADMINPadreDTO>> buscarPadresPorNombre(@RequestParam String termino) {
        List<ADMINPadreDTO> padresEncontrados = ADMINPadreService.buscarPadresPorNombre(termino);
        return new ResponseEntity<>(padresEncontrados, HttpStatus.OK);
    }

    // PUT: Actualizar Padre por ID (U)
    @PutMapping("/padres/{id}")
    public ResponseEntity<ADMINPadreDTO> actualizarPadre(@PathVariable Long id, @RequestBody ADMINPadreDTO ADMINPadreDTO) {
        ADMINPadreDTO padreActualizado = ADMINPadreService.actualizarPadre(id, ADMINPadreDTO);
        return new ResponseEntity<>(padreActualizado, HttpStatus.OK);
    }

    // PATCH: Toggle estado del Padre (Activar/Desactivar)
    @PatchMapping("/padres/{id}/toggle")
    public ResponseEntity<ADMINPadreDTO> toggleEstadoPadre(@PathVariable Long id) {
        ADMINPadreDTO padreActualizado = ADMINPadreService.toggleEstadoPadre(id);
        return new ResponseEntity<>(padreActualizado, HttpStatus.OK);
    }

    // PATCH: Desactivar Padre por ID (Soft Delete) - Compatibilidad
    @PatchMapping("/padres/{id}/desactivar")
    public ResponseEntity<ADMINPadreDTO> desactivarPadre(@PathVariable Long id) {
        ADMINPadreDTO padreActualizado = ADMINPadreService.toggleEstadoPadre(id);
        return new ResponseEntity<>(padreActualizado, HttpStatus.OK);
    }

    // DELETE: Mantener compatibilidad con frontend existente (Toggle)
    @DeleteMapping("/padres/{id}")
    public ResponseEntity<ADMINPadreDTO> eliminarPadre(@PathVariable Long id) {
        ADMINPadreDTO padreActualizado = ADMINPadreService.toggleEstadoPadre(id);
        return new ResponseEntity<>(padreActualizado, HttpStatus.OK);
    }

    // GET: Obtener menores de un padre específico (para Admin)
    @GetMapping("/padres/{padreId}/menores")
    public ResponseEntity<List<PADREMenorDTO>> obtenerMenoresPorPadre(@PathVariable Long padreId) {
        try {
            List<PADREMenorDTO> menores = PADREMenorService.obtenerMenoresPorPadre(padreId);
            return new ResponseEntity<>(menores, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener menores del padre " + padreId + ": " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 3. GESTIÓN DE PSICÓLOGOS (Ruta: /api/admin/psicologos)
    // ===========================================

    @PostMapping("/psicologos")
    public ResponseEntity<ADMINPsicologoDTO> crearPsicologo(@RequestBody ADMINPsicologoDTO ADMINPsicologoDTO) {
        ADMINPsicologoDTO nuevoPsicologo = ADMINPsicologoService.crearPsicologo(ADMINPsicologoDTO);
        return new ResponseEntity<>(nuevoPsicologo, HttpStatus.CREATED);
    }

    // GET: Obtener todos los Psicólogos (R)
    @GetMapping("/psicologos")
    public ResponseEntity<List<ADMINPsicologoDTO>> obtenerTodosLosPsicologos() {
        List<ADMINPsicologoDTO> psicologos = ADMINPsicologoService.obtenerTodosLosPsicologos();
        return new ResponseEntity<>(psicologos, HttpStatus.OK);
    }

    // GET: Obtener Psicólogo por ID (R)
    @GetMapping("/psicologos/{id}")
    public ResponseEntity<ADMINPsicologoDTO> obtenerPsicologoPorId(@PathVariable Long id) {
        ADMINPsicologoDTO psicologo = ADMINPsicologoService.obtenerPsicologoPorId(id);
        return new ResponseEntity<>(psicologo, HttpStatus.OK);
    }

    // GET: Buscar Psicólogos por especialidad (R)
    @GetMapping("/psicologos/especialidad")
    public ResponseEntity<List<ADMINPsicologoDTO>> buscarPsicologosPorEspecialidad(@RequestParam String especialidad) {
        List<ADMINPsicologoDTO> psicologosEncontrados = ADMINPsicologoService.buscarPsicologosPorEspecialidad(especialidad);
        return new ResponseEntity<>(psicologosEncontrados, HttpStatus.OK);
    }

    // PUT: Actualizar Psicólogo por ID (U)
    @PutMapping("/psicologos/{id}")
    public ResponseEntity<ADMINPsicologoDTO> actualizarPsicologo(@PathVariable Long id, @RequestBody ADMINPsicologoDTO ADMINPsicologoDTO) {
        ADMINPsicologoDTO psicologoActualizado = ADMINPsicologoService.actualizarPsicologo(id, ADMINPsicologoDTO);
        return new ResponseEntity<>(psicologoActualizado, HttpStatus.OK);
    }

    // PATCH: Toggle estado del Psicólogo (Activar/Desactivar)
    @PatchMapping("/psicologos/{id}/toggle")
    public ResponseEntity<ADMINPsicologoDTO> toggleEstadoPsicologo(@PathVariable Long id) {
        ADMINPsicologoDTO psicologoActualizado = ADMINPsicologoService.toggleEstadoPsicologo(id);
        return new ResponseEntity<>(psicologoActualizado, HttpStatus.OK);
    }

    // PATCH: Desactivar Psicólogo por ID (Soft Delete) - Compatibilidad
    @PatchMapping("/psicologos/{id}/desactivar")
    public ResponseEntity<ADMINPsicologoDTO> desactivarPsicologo(@PathVariable Long id) {
        ADMINPsicologoDTO psicologoActualizado = ADMINPsicologoService.toggleEstadoPsicologo(id);
        return new ResponseEntity<>(psicologoActualizado, HttpStatus.OK);
    }

    // DELETE: Mantener compatibilidad con frontend existente (Toggle)
    @DeleteMapping("/psicologos/{id}")
    public ResponseEntity<ADMINPsicologoDTO> eliminarPsicologo(@PathVariable Long id) {
        ADMINPsicologoDTO psicologoActualizado = ADMINPsicologoService.toggleEstadoPsicologo(id);
        return new ResponseEntity<>(psicologoActualizado, HttpStatus.OK);
    }

    // ===========================================
    // 4. GESTIÓN DE RECURSOS (Ruta: /api/admin/recursos)
    // ===========================================

    // POST: Crear nuevo Recurso (C)
    @PostMapping("/recursos")
    public ResponseEntity<ADMINRecursoEducativoDTO> crearRecurso(@RequestBody ADMINRecursoEducativoDTO recursoDTO) {
        ADMINRecursoEducativoDTO nuevoRecurso = ADMINRecursoEducativoService.crearRecurso(recursoDTO);
        return new ResponseEntity<>(nuevoRecurso, HttpStatus.CREATED);
    }

    // GET: Obtener todos los Recursos (R)
    @GetMapping("/recursos")
    public ResponseEntity<List<ADMINRecursoEducativoDTO>> obtenerTodosLosRecursos() {
        List<ADMINRecursoEducativoDTO> recursos = ADMINRecursoEducativoService.obtenerTodosLosRecursos();
        // Uso explícito de HttpStatus.OK (200)
        return new ResponseEntity<>(recursos, HttpStatus.OK);
    }

    // GET: Obtener Recurso por ID (R)
    @GetMapping("/recursos/{id}")
    public ResponseEntity<ADMINRecursoEducativoDTO> obtenerRecursoPorId(@PathVariable Long id) {
        ADMINRecursoEducativoDTO recurso = ADMINRecursoEducativoService.obtenerRecursoPorId(id);
        // Uso explícito de HttpStatus.OK (200)
        return new ResponseEntity<>(recurso, HttpStatus.OK);
    }

    // GET: Buscar Recursos por título (R)
    @GetMapping("/recursos/buscar")
    public ResponseEntity<List<ADMINRecursoEducativoDTO>> buscarRecursosPorTitulo(@RequestParam String termino) {
        List<ADMINRecursoEducativoDTO> recursosEncontrados = ADMINRecursoEducativoService.buscarRecursosPorTitulo(termino);
        // Uso explícito de HttpStatus.OK (200)
        return new ResponseEntity<>(recursosEncontrados, HttpStatus.OK);
    }

    // PUT: Actualizar Recurso por ID (U)
    @PutMapping("/recursos/{id}")
    public ResponseEntity<ADMINRecursoEducativoDTO> actualizarRecurso(@PathVariable Long id, @RequestBody ADMINRecursoEducativoDTO recursoDTO) {
        ADMINRecursoEducativoDTO recursoActualizado = ADMINRecursoEducativoService.actualizarRecurso(id, recursoDTO);
        // Uso explícito de HttpStatus.OK (200)
        return new ResponseEntity<>(recursoActualizado, HttpStatus.OK);
    }

    // DELETE: Eliminar Recurso por ID (D)
    @DeleteMapping("/recursos/{id}")
    public ResponseEntity<Void> eliminarRecurso(@PathVariable Long id) {
        ADMINRecursoEducativoService.eliminarRecurso(id);
        // Uso explícito de HttpStatus.NO_CONTENT (204)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ===========================================
    // 5. MONITOREO DE CITAS (Ruta: /api/admin/citas)
    // ===========================================

    // GET: Obtener todas las Citas ordenadas por fecha descendente (R)
    @GetMapping("/citas")
    public ResponseEntity<List<ADMINCitaListDTO>> obtenerTodasLasCitas() {
        List<ADMINCitaListDTO> citas = ADMINCitaService.obtenerTodasLasCitas();
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    // ===========================================
    // 6. MÉTRICAS DEL DASHBOARD (Ruta: /api/admin/metrics)
    // ===========================================

    // GET: Obtener métricas del dashboard (R)
    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> obtenerMetricasDashboard() {
        DashboardMetricsDTO metrics = ADMINMetricsService.obtenerMetricasDashboard();
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }

    // ===========================================
    // 7. GESTIÓN DE EVALUACIONES (Ruta: /api/admin/evaluaciones)
    // ===========================================

    // GET: Obtener todas las Evaluaciones ordenadas por fecha descendente (R)
    @GetMapping("/evaluaciones")
    public ResponseEntity<List<ADMINEvaluacionListDTO>> obtenerTodasLasEvaluaciones() {
        List<ADMINEvaluacionListDTO> evaluaciones = ADMINEvaluacionService.obtenerTodasLasEvaluaciones();
        return new ResponseEntity<>(evaluaciones, HttpStatus.OK);
    }

    // DELETE: Eliminar Evaluación por ID (D - Moderación)
    @DeleteMapping("/evaluaciones/{id}")
    public ResponseEntity<Void> eliminarEvaluacion(@PathVariable Long id) {
        ADMINEvaluacionService.eliminarEvaluacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
