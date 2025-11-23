package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINRecursoEducativoDTO;
import pe.edu.upc.backend.entitiesTF.RecursoEducativo;
import pe.edu.upc.backend.repositoriesTF.RecursoEducativoRepository;
import pe.edu.upc.backend.servicesTF.ADMINRecursoEducativoService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINRecursoEducativoServiceImpl implements ADMINRecursoEducativoService {
    @Autowired
    private RecursoEducativoRepository recursoRepository;

    // --- Conversión Entity <-> DTO (COMPLETADO) ---

    private ADMINRecursoEducativoDTO convertToDTO(RecursoEducativo entity) {
        ADMINRecursoEducativoDTO dto = new ADMINRecursoEducativoDTO();

        dto.setRecursoId(entity.getRecursoId());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setLink(entity.getLink());
        dto.setTexto(entity.getTexto());
        dto.setFechaCreacion(entity.getFechaCreacion());
        return dto;
    }

    private RecursoEducativo convertToEntity(ADMINRecursoEducativoDTO dto) {
        RecursoEducativo entity = new RecursoEducativo();

        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setLink(dto.getLink());
        entity.setTexto(dto.getTexto());
        // FechaCreacion se establece en crearRecurso
        return entity;
    }

    // --- Implementación de CRUD ---

    @Override
    public ADMINRecursoEducativoDTO crearRecurso(ADMINRecursoEducativoDTO recursoDTO) {
        RecursoEducativo recurso = convertToEntity(recursoDTO);
        recurso.setFechaCreacion(new Date());
        RecursoEducativo nuevoRecurso = recursoRepository.save(recurso);
        return convertToDTO(nuevoRecurso);
    }

    @Override
    public ADMINRecursoEducativoDTO obtenerRecursoPorId(Long id) {
        RecursoEducativo recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso no encontrado con ID: " + id));
        return convertToDTO(recurso);
    }

    @Override
    public List<ADMINRecursoEducativoDTO> obtenerTodosLosRecursos() {
        return recursoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ADMINRecursoEducativoDTO> buscarRecursosPorTitulo(String termino) {
        return recursoRepository.findByTituloContaining(termino).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ADMINRecursoEducativoDTO actualizarRecurso(Long id, ADMINRecursoEducativoDTO recursoDTO) {
        // 1. Buscar la entidad existente o lanzar una excepción si no la encuentra.
        RecursoEducativo recursoExistente = recursoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso no encontrado con ID: " + id));

        // 2. Actualizar campos solo si los nuevos valores no son nulos ni están en blanco.

        // Título
        if (recursoDTO.getTitulo() != null && !recursoDTO.getTitulo().isBlank()) {
            recursoExistente.setTitulo(recursoDTO.getTitulo());
        }

        // Descripción
        if (recursoDTO.getDescripcion() != null && !recursoDTO.getDescripcion().isBlank()) {
            recursoExistente.setDescripcion(recursoDTO.getDescripcion());
        }

        // Link
        if (recursoDTO.getLink() != null && !recursoDTO.getLink().isBlank()) {
            recursoExistente.setLink(recursoDTO.getLink());
        }

        // Texto
        if (recursoDTO.getTexto() != null && !recursoDTO.getTexto().isBlank()) {
            recursoExistente.setTexto(recursoDTO.getTexto());
        }

        // 3. Guardar la entidad con los campos actualizados.
        RecursoEducativo actualizado = recursoRepository.save(recursoExistente);

        // 4. Convertir la entidad guardada a DTO y retornarla.
        return convertToDTO(actualizado);
    }

    @Override
    public void eliminarRecurso(Long id) {
        recursoRepository.deleteById(id);
    }
}
