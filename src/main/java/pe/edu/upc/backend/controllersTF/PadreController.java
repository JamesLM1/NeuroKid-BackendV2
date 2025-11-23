package pe.edu.upc.backend.controllersTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.*;
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

    // ===========================================
    // 1. GESTIÓN DEL PERFIL DEL PADRE (Manejo de PadreService)
    // ===========================================

    @GetMapping("/perfil")
    public ResponseEntity<ADMINPadreDTO> obtenerPerfil(@PathVariable Long padreId) {
        ADMINPadreDTO perfil = ADMINPadreService.obtenerPadrePorId(padreId);
        return new ResponseEntity<>(perfil, HttpStatus.OK);
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
        List<PADREMenorDTO> menores = PADREMenorService.obtenerMenoresPorPadre(padreId);
        return new ResponseEntity<>(menores, HttpStatus.OK);
    }

    // Registrar un nuevo menor (Asociación automática al padre por el {padreId} en la URL)
    @PostMapping("/menores")
    public ResponseEntity<PADREMenorDTO> registrarMenor(@PathVariable Long padreId, @RequestBody PADREMenorDTO PADREMenorDTO) {
        PADREMenorDTO nuevoMenor = PADREMenorService.registrarMenor(padreId, PADREMenorDTO);
        return new ResponseEntity<>(nuevoMenor, HttpStatus.CREATED);
    }

    // Actualizar datos de un menor asociado a este padre
    @PutMapping("/menores/{menorId}")
    public ResponseEntity<PADREMenorDTO> actualizarMenor(
            @PathVariable Long padreId,
            @PathVariable Long menorId,
            @RequestBody PADREMenorDTO PADREMenorDTO) {
        try {
            PADREMenorDTO menorActualizado = PADREMenorService.actualizarMenor(menorId, padreId, PADREMenorDTO);
            return new ResponseEntity<>(menorActualizado, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar un menor asociado al padre
    @DeleteMapping("/menores/{menorId}")
    public ResponseEntity<Void> eliminarMenor(
            @PathVariable Long padreId,
            @PathVariable Long menorId) {
        PADREMenorService.eliminarMenor(menorId, padreId);
        return ResponseEntity.noContent().build();
    }


    // ===========================================
    // 3. GESTIÓN DE CITAS (CitaPadreService)
    // ===========================================

    @PostMapping("/citas/solicitar")
    public ResponseEntity<PADRECitaResponseDTO> solicitarCita(@PathVariable Long padreId, @RequestBody PADRECitaRequestDTO requestDTO) {
        PADRECitaResponseDTO nuevaCita = PADRECitaPadreService.solicitarCita(padreId, requestDTO);
        return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);
    }

    @GetMapping("/citas/proximas")
    public ResponseEntity<List<PADRECitaResponseDTO>> obtenerProximasCitas(@PathVariable Long padreId) {
        List<PADRECitaResponseDTO> citas = PADRECitaPadreService.obtenerProximasCitas(padreId);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    @GetMapping("/citas/historial")
    public ResponseEntity<List<PADRECitaResponseDTO>> obtenerHistorialCitas(@PathVariable Long padreId) {
        List<PADRECitaResponseDTO> historial = PADRECitaPadreService.obtenerHistorialCitas(padreId);
        return new ResponseEntity<>(historial, HttpStatus.OK);
    }

    @DeleteMapping("/citas/{citaId}")
    public ResponseEntity<PADRECitaResponseDTO> cancelarCita(@PathVariable Long padreId, @PathVariable Long citaId) {
        PADRECitaResponseDTO citaCancelada = PADRECitaPadreService.cancelarCita(citaId, padreId);
        return new ResponseEntity<>(citaCancelada, HttpStatus.OK);
    }

    // ===========================================
    // 4. CONSULTA DE PROGRESO E INFORMES (ProgresoPadreService)
    // ===========================================

    @GetMapping("/menores/{menorId}/informes")
    public ResponseEntity<List<PADREInformeDTO>> obtenerInformes(
            @PathVariable Long padreId,
            @PathVariable Long menorId) {
        List<PADREInformeDTO> informes = PADREProgresoPadreService.obtenerInformesPorMenor(menorId, padreId);
        return new ResponseEntity<>(informes, HttpStatus.OK);
    }

    @PostMapping("/psicologos/{psicologoId}/evaluar")
    public ResponseEntity<PADREEvaluacionPsicologoDTO> evaluarPsicologo(
            @PathVariable Long padreId,
            @PathVariable Long psicologoId,
            @RequestBody PADREEvaluacionPsicologoDTO evaluacionDTO) {

        // Se establece el PsicologoId y el PadreId desde la URL y el DTO
        evaluacionDTO.setPsicologoId(psicologoId);
        PADREEvaluacionPsicologoDTO resultado = PADREProgresoPadreService.evaluarPsicologo(padreId, evaluacionDTO);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
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
        List<ADMINRecursoEducativoDTO> favoritos = PADRERecursoPadreService.obtenerRecursosFavoritos(padreId);
        return new ResponseEntity<>(favoritos, HttpStatus.OK);
    }

    @PostMapping("/recursos/{recursoId}/favorito")
    public ResponseEntity<PADREFavoritoDTO> marcarFavorito(@PathVariable Long padreId, @PathVariable Long recursoId) {
        PADREFavoritoDTO favorito = PADRERecursoPadreService.marcarFavorito(padreId, recursoId);
        return new ResponseEntity<>(favorito, HttpStatus.CREATED);
    }

    @DeleteMapping("/recursos/{recursoId}/favorito")
    public ResponseEntity<Void> desmarcarFavorito(@PathVariable Long padreId, @PathVariable Long recursoId) {
        PADRERecursoPadreService.desmarcarFavorito(padreId, recursoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
