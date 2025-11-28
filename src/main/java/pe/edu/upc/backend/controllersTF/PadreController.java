package pe.edu.upc.backend.controllersTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.*;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.servicesTF.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/padres/{padreId}")
public class PadreController {
    // Inyección de servicios relevantes para el rol Padre
    @Autowired
    private ADMINPadreService ADMINPadreService;

    @Autowired
    private PADREMenorService PADREMenorService;

    @Autowired
    private PADRECitaPadreService PADRECitaPadreService;

    @Autowired
    private PADREProgresoPadreService PADREProgresoPadreService;

    @Autowired
    private PADRERecursoPadreService PADRERecursoPadreService;

    @Autowired
    private PadreRepository padreRepository;

    // ===========================================
    // 1. GESTIÓN DEL PERFIL DEL PADRE (Manejo de PadreService)
    // ===========================================

    @GetMapping("/perfil")
    public ResponseEntity<ADMINPadreDTO> obtenerPerfil(@PathVariable Long padreId) {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Buscar el Padre por su email
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            // Obtener el perfil del padre real
            ADMINPadreDTO perfil = ADMINPadreService.obtenerPadrePorId(padre.getPadreId());
            return new ResponseEntity<>(perfil, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error al obtener perfil: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error general al obtener perfil: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<ADMINPadreDTO> actualizarPerfil(@PathVariable Long padreId, @RequestBody ADMINPadreDTO ADMINPadreDTO) {
        ADMINPadreDTO actualizado = ADMINPadreService.actualizarPadre(padreId, ADMINPadreDTO);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    // ===========================================
    // 2. GESTIÓN DE MENORES ASOCIADOS (MenorService)
    // ===========================================

    // Obtener la lista de menores asociados a este padre
    @GetMapping("/menores")
    public ResponseEntity<List<PADREMenorDTO>> obtenerMenoresPorPadre(@PathVariable Long padreId) {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Buscar el Padre por su email
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            // Obtener los menores del padre real
            List<PADREMenorDTO> menores = PADREMenorService.obtenerMenoresPorPadre(padre.getPadreId());
            return new ResponseEntity<>(menores, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error al obtener menores: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error general al obtener menores: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener las asignaciones activas del padre (para solicitar citas)
    @Autowired
    private ADMINAsignacionService asignacionService;

    @GetMapping("/asignaciones")
    public ResponseEntity<List<ADMINAsignacionDTO>> obtenerMisAsignaciones(@PathVariable Long padreId) {
        try {
            // Obtener el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Buscar el Padre por su email
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            // Obtener las asignaciones del padre real
            List<ADMINAsignacionDTO> todasLasAsignaciones = asignacionService.obtenerTodasLasAsignaciones();
            List<ADMINAsignacionDTO> misAsignaciones = todasLasAsignaciones.stream()
                    .filter(a -> a.getPadreId().equals(padre.getPadreId()) && "Activa".equals(a.getEstado()))
                    .collect(java.util.stream.Collectors.toList());
            return new ResponseEntity<>(misAsignaciones, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error al obtener asignaciones: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error general al obtener asignaciones: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Registrar un nuevo menor (IGNORA el @PathVariable padreId de la URL por seguridad)
    @PostMapping("/menores")
    public ResponseEntity<PADREMenorDTO> registrarMenor(@PathVariable Long padreId, @RequestBody PADREMenorDTO PADREMenorDTO) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Validar que el DTO tenga la fecha correctamente
            if (PADREMenorDTO.getFechaNacimiento() == null) {
                System.err.println("❌ Error: fechaNacimiento es null en el DTO");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            System.out.println("📅 Fecha recibida: " + PADREMenorDTO.getFechaNacimiento());

            // Registrar el menor con el ID correcto del Padre
            PADREMenorDTO nuevoMenor = PADREMenorService.registrarMenor(padreIdReal, PADREMenorDTO);
            
            System.out.println("✅ Menor registrado exitosamente - ID: " + nuevoMenor.getMenorId());
            return new ResponseEntity<>(nuevoMenor, HttpStatus.CREATED);

        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error IllegalArgumentException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("❌ Error General al registrar menor: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar datos de un menor asociado a este padre
    @PutMapping("/menores/{menorId}")
    public ResponseEntity<PADREMenorDTO> actualizarMenor(
            @PathVariable Long padreId,
            @PathVariable Long menorId,
            @RequestBody PADREMenorDTO PADREMenorDTO) {
        try {
            // Obtener el padre real desde el token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado"));

            PADREMenorDTO menorActualizado = PADREMenorService.actualizarMenor(menorId, padre.getPadreId(), PADREMenorDTO);
            return new ResponseEntity<>(menorActualizado, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error al actualizar menor: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error de seguridad: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar un menor asociado al padre
    @DeleteMapping("/menores/{menorId}")
    public ResponseEntity<Void> eliminarMenor(
            @PathVariable Long padreId,
            @PathVariable Long menorId) {
        try {
            // Obtener el padre real desde el token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado"));

            PADREMenorService.eliminarMenor(menorId, padre.getPadreId());
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error al eliminar menor: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error de seguridad al eliminar menor: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.err.println("❌ Error general al eliminar menor: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ===========================================
    // 2.5. ENDPOINTS PARA AUTOSERVICIO DE CITAS
    // ===========================================

    @GetMapping("/menores-disponibles")
    public ResponseEntity<List<PADREMenorDTO>> obtenerMenoresDisponibles(@PathVariable Long padreId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            // Usar el ID REAL del Padre de la BD
            Long padreIdReal = padre.getPadreId();
            
            // Obtener los menores del padre
            List<PADREMenorDTO> menores = PADREMenorService.obtenerMenoresPorPadre(padreIdReal);
            
            System.out.println("✅ Menores disponibles obtenidos - Cantidad: " + menores.size());
            return new ResponseEntity<>(menores, HttpStatus.OK);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error General al obtener menores: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 3. GESTIÓN DE CITAS (CitaPadreService)
    // ===========================================

    // NUEVO ENDPOINT: Solicitar cita directa (modelo autoservicio)
    @PostMapping("/citas/solicitar-directa")
    public ResponseEntity<PADRECitaResponseDTO> solicitarCitaDirecta(@PathVariable Long padreId, @RequestBody PADRESolicitudCitaDTO solicitudDTO) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Solicitar la cita directa con el ID correcto del Padre
            PADRECitaResponseDTO nuevaCita = PADRECitaPadreService.solicitarCitaDirecta(padreIdReal, solicitudDTO);
            
            System.out.println("✅ Cita directa solicitada exitosamente - ID: " + nuevaCita.getCitaId());
            return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error SecurityException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error IllegalArgumentException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("❌ Error General al solicitar cita directa: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ENDPOINT ORIGINAL: Solicitar cita con asignación previa
    @PostMapping("/citas/solicitar")
    public ResponseEntity<PADRECitaResponseDTO> solicitarCita(@PathVariable Long padreId, @RequestBody PADRECitaRequestDTO requestDTO) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Solicitar la cita con el ID correcto del Padre
            PADRECitaResponseDTO nuevaCita = PADRECitaPadreService.solicitarCita(padreIdReal, requestDTO);
            
            System.out.println("✅ Cita solicitada exitosamente - ID: " + nuevaCita.getCitaId());
            return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error SecurityException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error IllegalArgumentException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("❌ Error General al solicitar cita: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/citas/proximas")
    public ResponseEntity<List<PADRECitaResponseDTO>> obtenerProximasCitas(@PathVariable Long padreId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Obtener las próximas citas con el ID correcto del Padre
            List<PADRECitaResponseDTO> citas = PADRECitaPadreService.obtenerProximasCitas(padreIdReal);
            
            System.out.println("✅ Próximas citas obtenidas - Cantidad: " + citas.size());
            citas.forEach(c -> System.out.println("   - Cita ID: " + c.getCitaId() + ", Estado: " + c.getEstado() + ", Fecha: " + c.getFecha()));
            
            return new ResponseEntity<>(citas, HttpStatus.OK);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error General al obtener próximas citas: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/citas/historial")
    public ResponseEntity<List<PADRECitaResponseDTO>> obtenerHistorialCitas(@PathVariable Long padreId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Obtener el historial de citas con el ID correcto del Padre
            List<PADRECitaResponseDTO> historial = PADRECitaPadreService.obtenerHistorialCitas(padreIdReal);
            
            return new ResponseEntity<>(historial, HttpStatus.OK);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error General al obtener historial de citas: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/citas/{citaId}")
    public ResponseEntity<PADRECitaResponseDTO> cancelarCita(@PathVariable Long padreId, @PathVariable Long citaId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);
            System.out.println("📋 Cita ID a cancelar: " + citaId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Cancelar la cita con el ID correcto del Padre
            PADRECitaResponseDTO citaCancelada = PADRECitaPadreService.cancelarCita(citaId, padreIdReal);
            
            System.out.println("✅ Cita cancelada exitosamente - ID: " + citaCancelada.getCitaId());
            return new ResponseEntity<>(citaCancelada, HttpStatus.OK);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error SecurityException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            System.err.println("❌ Error IllegalStateException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("❌ Error General al cancelar cita: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ===========================================
    // 4. CONSULTA DE PROGRESO E INFORMES (ProgresoPadreService)
    // ===========================================

    @GetMapping("/menores/{menorId}/informes")
    public ResponseEntity<List<PADREInformeDTO>> obtenerInformes(
            @PathVariable Long padreId,
            @PathVariable Long menorId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);
            System.out.println("👶 Menor ID solicitado: " + menorId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Obtener los informes del menor con el ID correcto del Padre
            List<PADREInformeDTO> informes = PADREProgresoPadreService.obtenerInformesPorMenor(menorId, padreIdReal);
            
            System.out.println("✅ Informes obtenidos exitosamente - Cantidad: " + informes.size());
            return new ResponseEntity<>(informes, HttpStatus.OK);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error SecurityException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.err.println("❌ Error General al obtener informes: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/psicologos/{psicologoId}/evaluar")
    public ResponseEntity<PADREEvaluacionPsicologoDTO> evaluarPsicologo(
            @PathVariable Long padreId,
            @PathVariable Long psicologoId,
            @RequestBody PADREEvaluacionPsicologoDTO evaluacionDTO) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);
            System.out.println("👨‍⚕️ Psicólogo ID a evaluar: " + psicologoId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Se establece el PsicologoId y el PadreId desde la URL y el DTO
            evaluacionDTO.setPsicologoId(psicologoId);
            PADREEvaluacionPsicologoDTO resultado = PADREProgresoPadreService.evaluarPsicologo(padreIdReal, evaluacionDTO);
            
            System.out.println("✅ Evaluación creada exitosamente - ID: " + resultado.getEvaluacionId());
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error SecurityException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            System.err.println("❌ Error General al evaluar psicólogo: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/citas/{citaId}/evaluar")
    public ResponseEntity<PADREEvaluacionPsicologoDTO> evaluarPsicologoPorCita(
            @PathVariable Long padreId,
            @PathVariable Long citaId,
            @RequestBody PADREEvaluacionPsicologoDTO evaluacionDTO) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);
            System.out.println("📋 Cita ID para evaluación: " + citaId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Delegar al servicio para obtener el psicólogo de la cita y crear la evaluación
            PADREEvaluacionPsicologoDTO resultado = PADREProgresoPadreService.evaluarPsicologoPorCita(padreIdReal, citaId, evaluacionDTO);
            
            System.out.println("✅ Evaluación por cita creada exitosamente - ID: " + resultado.getEvaluacionId());
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            System.err.println("❌ Error SecurityException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            System.err.println("❌ Error IllegalStateException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("❌ Error General al evaluar psicólogo por cita: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ===========================================
    // 5. GESTIÓN DE RECURSOS Y FAVORITOS (RecursoPadreService)
    // ===========================================

    @GetMapping("/recursos/buscar")
    public ResponseEntity<List<ADMINRecursoEducativoDTO>> buscarRecursos(@RequestParam String termino) {
        List<ADMINRecursoEducativoDTO> recursos = PADRERecursoPadreService.buscarRecursos(termino);
        return new ResponseEntity<>(recursos, HttpStatus.OK);
    }

    @GetMapping("/recursos/favoritos")
    public ResponseEntity<List<ADMINRecursoEducativoDTO>> obtenerFavoritos(@PathVariable Long padreId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Obtener los favoritos con el ID correcto del Padre
            List<ADMINRecursoEducativoDTO> favoritos = PADRERecursoPadreService.obtenerRecursosFavoritos(padreIdReal);
            
            return new ResponseEntity<>(favoritos, HttpStatus.OK);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error General al obtener favoritos: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/recursos/{recursoId}/favorito")
    public ResponseEntity<PADREFavoritoDTO> marcarFavorito(@PathVariable Long padreId, @PathVariable Long recursoId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);
            System.out.println("📚 Recurso ID a marcar como favorito: " + recursoId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Marcar como favorito con el ID correcto del Padre
            PADREFavoritoDTO favorito = PADRERecursoPadreService.marcarFavorito(padreIdReal, recursoId);
            
            System.out.println("✅ Recurso marcado como favorito exitosamente - Recurso ID: " + recursoId);
            return new ResponseEntity<>(favorito, HttpStatus.CREATED);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error General al marcar favorito: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/recursos/{recursoId}/favorito")
    public ResponseEntity<Void> desmarcarFavorito(@PathVariable Long padreId, @PathVariable Long recursoId) {
        try {
            // Obtener el usuario autenticado del SecurityContext (email del JWT)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            System.out.println("🔐 Usuario autenticado (email): " + username);
            System.out.println("⚠️  ID recibido en URL (ignorado): " + padreId);
            System.out.println("📚 Recurso ID a desmarcar como favorito: " + recursoId);

            // Buscar el Padre por su email (ignorar completamente el padreId de la URL)
            Padre padre = padreRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado para el usuario: " + username));

            System.out.println("✅ Padre encontrado - ID real: " + padre.getPadreId() + ", Email: " + padre.getEmail());

            // Usar el ID REAL del Padre de la BD (ignorar el padreId de la URL)
            Long padreIdReal = padre.getPadreId();
            
            // Desmarcar como favorito con el ID correcto del Padre
            PADRERecursoPadreService.desmarcarFavorito(padreIdReal, recursoId);
            
            System.out.println("✅ Recurso desmarcado como favorito exitosamente - Recurso ID: " + recursoId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error EntityNotFoundException: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("❌ Error General al desmarcar favorito: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
