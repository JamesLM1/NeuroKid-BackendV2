package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINPadreDTO;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.servicesTF.ADMINPadreService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINPadreServiceImpl implements ADMINPadreService {

    @Autowired
    private PadreRepository padreRepository;

    // --- Conversión Entity <-> DTO (COMPLETADO) ---

    private Padre convertToEntity(ADMINPadreDTO dto) {
        Padre entity = new Padre();
        // Nota: padreId solo se usa en Update
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setDni(dto.getDni());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setTipoParentesco(dto.getTipoParentesco());
        return entity;
    }

    private ADMINPadreDTO convertToDTO(Padre entity) {
        ADMINPadreDTO dto = new ADMINPadreDTO();
        dto.setPadreId(entity.getPadreId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setDni(entity.getDni());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setTipoParentesco(entity.getTipoParentesco());
        dto.setFechaRegistro(entity.getFechaRegistro());
        return dto;
    }

    // --- Implementación de CRUD ---

    @Override
    public ADMINPadreDTO crearPadre(ADMINPadreDTO ADMINPadreDTO) {
        Padre padre = convertToEntity(ADMINPadreDTO);
        padre.setFechaRegistro(new Date()); // Se añade la fecha de registro
        Padre nuevoPadre = padreRepository.save(padre);
        return convertToDTO(nuevoPadre);
    }

    @Override
    public ADMINPadreDTO obtenerPadrePorId(Long id) {
        Padre padre = padreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + id));
        return convertToDTO(padre);
    }

    @Override
    public List<ADMINPadreDTO> obtenerTodosLosPadres() {
        return padreRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ADMINPadreDTO> buscarPadresPorNombre(String termino) {
        // Asumiendo que el repositorio tiene un metodo que acepta dos parámetros para OR.
        return padreRepository.findPorNombreOApellidoJPQL(termino, termino).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ADMINPadreDTO actualizarPadre(Long id, ADMINPadreDTO ADMINPadreDTO) {
        // 1. Buscar la entidad existente o lanzar excepción si no se encuentra
        Padre padreExistente = padreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + id));

        if (ADMINPadreDTO.getNombre() != null && !ADMINPadreDTO.getNombre().isBlank()) {

            padreExistente.setNombre(ADMINPadreDTO.getNombre());
        }

        // **Apellido:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getApellido() != null && !ADMINPadreDTO.getApellido().isBlank()) {
            padreExistente.setApellido(ADMINPadreDTO.getApellido());
        }

        // **DNI:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getDni() != null && !ADMINPadreDTO.getDni().isBlank()) {
            padreExistente.setDni(ADMINPadreDTO.getDni());
        }

        // **Email:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getEmail() != null && !ADMINPadreDTO.getEmail().isBlank()) {
            padreExistente.setEmail(ADMINPadreDTO.getEmail());
        }

        // **Teléfono:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getTelefono() != null && !ADMINPadreDTO.getTelefono().isBlank()) {
            padreExistente.setTelefono(ADMINPadreDTO.getTelefono());
        }

        // **Tipo Parentesco:** Solo verificar no null (asumiendo que es un enum u objeto que no tiene 'isBlank')
        // Si fuera un String, se usaría la verificación completa como en los demás campos.
        if (ADMINPadreDTO.getTipoParentesco() != null) {
            padreExistente.setTipoParentesco(ADMINPadreDTO.getTipoParentesco());
        }

        // 3. Guardar la entidad actualizada
        Padre actualizado = padreRepository.save(padreExistente);

        // 4. Convertir a DTO y retornar
        return convertToDTO(actualizado);
    }

    @Override
    public void eliminarPadre(Long id) {
        padreRepository.deleteById(id);
    }

}
