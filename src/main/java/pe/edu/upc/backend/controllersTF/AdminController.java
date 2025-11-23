package pe.edu.upc.backend.controllersTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.ADMINAsignacionDTO;
import pe.edu.upc.backend.dtosTF.ADMINPadreDTO;
import pe.edu.upc.backend.dtosTF.ADMINPsicologoDTO;
import pe.edu.upc.backend.dtosTF.ADMINRecursoEducativoDTO;
import pe.edu.upc.backend.servicesTF.ADMINAsignacionService;
import pe.edu.upc.backend.servicesTF.ADMINPadreService;
import pe.edu.upc.backend.servicesTF.ADMINPsicologoService;
import pe.edu.upc.backend.servicesTF.ADMINRecursoEducativoService;

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

    // DELETE: Eliminar Padre por ID (D)
    @DeleteMapping("/padres/{id}")
    public ResponseEntity<Void> eliminarPadre(@PathVariable Long id) {
        ADMINPadreService.eliminarPadre(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    // DELETE: Eliminar Psicólogo por ID (D)
    @DeleteMapping("/psicologos/{id}")
    public ResponseEntity<Void> eliminarPsicologo(@PathVariable Long id) {
        ADMINPsicologoService.eliminarPsicologo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
}
