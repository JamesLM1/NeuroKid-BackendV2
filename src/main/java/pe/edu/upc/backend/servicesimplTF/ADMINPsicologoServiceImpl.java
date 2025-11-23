package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINPsicologoDTO;
import pe.edu.upc.backend.entitiesTF.Psicologo;
import pe.edu.upc.backend.repositoriesTF.PsicologoRepository;
import pe.edu.upc.backend.servicesTF.ADMINPsicologoService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINPsicologoServiceImpl implements ADMINPsicologoService {

    @Autowired
    private PsicologoRepository psicologoRepository;

    // --- Conversión Entity <-> DTO (COMPLETADO) ---

    private Psicologo convertToEntity(ADMINPsicologoDTO dto) {
        Psicologo entity = new Psicologo();
        // Mapeo manual de campos del DTO a la Entidad
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setDni(dto.getDni());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setEspecialidad(dto.getEspecialidad());
        // El ID no se mapea en la creación
        return entity;
    }

    private ADMINPsicologoDTO convertToDTO(Psicologo entity) {
        ADMINPsicologoDTO dto = new ADMINPsicologoDTO();
        // Mapeo manual de campos de la Entidad al DTO
        dto.setPsicologoId(entity.getPsicologoId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setDni(entity.getDni());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setEspecialidad(entity.getEspecialidad());
        dto.setFechaRegistro(entity.getFechaRegistro()); // Asumido
        return dto;
    }

    // --- Implementación de CRUD ---

    @Override
    public ADMINPsicologoDTO crearPsicologo(ADMINPsicologoDTO ADMINPsicologoDTO) {
        Psicologo psicologo = convertToEntity(ADMINPsicologoDTO);
        psicologo.setFechaRegistro(new Date()); // Se añade la fecha de registro
        Psicologo nuevoPsicologo = psicologoRepository.save(psicologo);
        return convertToDTO(nuevoPsicologo);
    }

    @Override
    public ADMINPsicologoDTO obtenerPsicologoPorId(Long id) {
        Psicologo psicologo = psicologoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + id));
        return convertToDTO(psicologo);
    }

    @Override
    public List<ADMINPsicologoDTO> obtenerTodosLosPsicologos() {
        return psicologoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ADMINPsicologoDTO> buscarPsicologosPorEspecialidad(String especialidad) {
        return psicologoRepository.findByEspecialidad(especialidad).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ADMINPsicologoDTO actualizarPsicologo(Long id, ADMINPsicologoDTO ADMINPsicologoDTO) {
        // 1. Buscar la entidad existente o lanzar excepción si no se encuentra
        Psicologo psicologoExistente = psicologoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + id));

        // 2. Aplicar las verificaciones condicionales (if) para actualizar solo los campos
        //    que no son nulos y no están en blanco en el DTO.

        // Nombre
        if (ADMINPsicologoDTO.getNombre() != null && !ADMINPsicologoDTO.getNombre().isBlank()) {
            psicologoExistente.setNombre(ADMINPsicologoDTO.getNombre());
        }

        // Apellido
        if (ADMINPsicologoDTO.getApellido() != null && !ADMINPsicologoDTO.getApellido().isBlank()) {
            psicologoExistente.setApellido(ADMINPsicologoDTO.getApellido());
        }

        // DNI
        if (ADMINPsicologoDTO.getDni() != null && !ADMINPsicologoDTO.getDni().isBlank()) {
            psicologoExistente.setDni(ADMINPsicologoDTO.getDni());
        }

        // Email
        if (ADMINPsicologoDTO.getEmail() != null && !ADMINPsicologoDTO.getEmail().isBlank()) {
            psicologoExistente.setEmail(ADMINPsicologoDTO.getEmail());
        }

        // Teléfono
        if (ADMINPsicologoDTO.getTelefono() != null && !ADMINPsicologoDTO.getTelefono().isBlank()) {
            psicologoExistente.setTelefono(ADMINPsicologoDTO.getTelefono());
        }

        // Especialidad
        // Usamos solo la verificación de 'null' si Especialidad es un objeto/enum.
        // Si fuera un String simple, se recomienda usar también !.isBlank().
        if (ADMINPsicologoDTO.getEspecialidad() != null && !ADMINPsicologoDTO.getEspecialidad().isBlank()) {
            psicologoExistente.setEspecialidad(ADMINPsicologoDTO.getEspecialidad());
        }

        // 3. Guardar la entidad actualizada
        Psicologo actualizado = psicologoRepository.save(psicologoExistente);

        // 4. Convertir a DTO y retornar
        return convertToDTO(actualizado);
    }

    @Override
    public void eliminarPsicologo(Long id) {
        psicologoRepository.deleteById(id);
    }
}
