package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.backend.dtosTF.PADREMenorDTO;
import pe.edu.upc.backend.entitiesTF.Asignacion;
import pe.edu.upc.backend.entitiesTF.Menor;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.repositoriesTF.AsignacionRepository;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.repositoriesTF.InformeRepository;
import pe.edu.upc.backend.repositoriesTF.MenorRepository;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.servicesTF.PADREMenorService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PADREMenorServiceImpl implements PADREMenorService {

    @Autowired private MenorRepository menorRepository;
    @Autowired private PadreRepository padreRepository;
    @Autowired private AsignacionRepository asignacionRepository;
    @Autowired private CitaRepository citaRepository;
    @Autowired private InformeRepository informeRepository;

    // Métodos de conversión completos:
    private Menor convertToEntity(PADREMenorDTO dto, Padre padre) {
        Menor entity = new Menor();
        // El ID solo se usa en Update, no en Create
        entity.setMenorId(dto.getMenorId());
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        // Asignación directa: ambos usan LocalDate
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setPadre(padre);
        return entity;
    }

    private PADREMenorDTO convertToDTO(Menor entity) {
        PADREMenorDTO dto = new PADREMenorDTO();
        dto.setMenorId(entity.getMenorId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        // Asignación directa: ambos usan LocalDate
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setPadreId(entity.getPadre().getPadreId());
        return dto;
    }

    // --- Implementación de CRUD ---

    @Override
    public PADREMenorDTO registrarMenor(Long padreId, PADREMenorDTO PADREMenorDTO) {
        System.out.println("📝 PADREMenorService.registrarMenor - padreId recibido: " + padreId);
        
        // Validar que el DTO no sea null
        if (PADREMenorDTO == null) {
            throw new IllegalArgumentException("El DTO del menor no puede ser null");
        }
        
        // Validar campos requeridos
        if (PADREMenorDTO.getNombre() == null || PADREMenorDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del menor es requerido");
        }
        if (PADREMenorDTO.getApellido() == null || PADREMenorDTO.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del menor es requerido");
        }
        if (PADREMenorDTO.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento del menor es requerida");
        }
        
        System.out.println("📋 Datos del menor a registrar:");
        System.out.println("   - Nombre: " + PADREMenorDTO.getNombre());
        System.out.println("   - Apellido: " + PADREMenorDTO.getApellido());
        System.out.println("   - Fecha Nacimiento: " + PADREMenorDTO.getFechaNacimiento());
        
        // Buscar el padre por ID
        Padre padre = padreRepository.findById(padreId)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + padreId));
        
        System.out.println("✅ Padre encontrado - ID: " + padre.getPadreId() + ", Nombre: " + padre.getNombre() + " " + padre.getApellido());

        // Convertir DTO a entidad y guardar
        Menor menor = convertToEntity(PADREMenorDTO, padre);
        Menor nuevoMenor = menorRepository.save(menor);
        
        System.out.println("✅ Menor guardado exitosamente - ID: " + nuevoMenor.getMenorId());
        
        return convertToDTO(nuevoMenor);
    }

    @Override
    public PADREMenorDTO obtenerMenorPorId(Long menorId, Long padreId) {
        Menor menor = menorRepository.findById(menorId)
                .orElseThrow(() -> new EntityNotFoundException("Menor no encontrado con ID: " + menorId));

        if (!menor.getPadre().getPadreId().equals(padreId)) {
            throw new SecurityException("Acceso denegado. El menor no pertenece a este padre.");
        }
        return convertToDTO(menor);
    }

    @Override
    public List<PADREMenorDTO> obtenerMenoresPorPadre(Long padreId) {
        return menorRepository.findPorPadreIdJPQL(padreId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PADREMenorDTO actualizarMenor(Long menorId, Long padreId, PADREMenorDTO PADREMenorDTO) {
        Menor menorExistente = menorRepository.findById(menorId)
                .orElseThrow(() -> new EntityNotFoundException("Menor no encontrado con ID: " + menorId));

        if (!menorExistente.getPadre().getPadreId().equals(padreId)) {
            throw new SecurityException("Acceso denegado.");
        }

        // Actualización de campos
        menorExistente.setNombre(PADREMenorDTO.getNombre());
        menorExistente.setApellido(PADREMenorDTO.getApellido());
        menorExistente.setFechaNacimiento(PADREMenorDTO.getFechaNacimiento());

        Menor actualizado = menorRepository.save(menorExistente);
        return convertToDTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminarMenor(Long menorId, Long padreId) {
        Menor menorExistente = menorRepository.findById(menorId)
                .orElseThrow(() -> new EntityNotFoundException("Menor no encontrado con ID: " + menorId));

        if (!menorExistente.getPadre().getPadreId().equals(padreId)) {
            throw new SecurityException("Acceso denegado.");
        }

        // Obtener todas las asignaciones del menor
        List<Asignacion> asignaciones = menorExistente.getAsignaciones();
        
        // Eliminar en cascada: Informes -> Citas -> Asignaciones -> Menor
        for (Asignacion asignacion : asignaciones) {
            // 1. Eliminar todos los Informes de esta asignación
            if (asignacion.getInformes() != null && !asignacion.getInformes().isEmpty()) {
                informeRepository.deleteAll(asignacion.getInformes());
            }
            
            // 2. Eliminar todas las Citas de esta asignación
            if (asignacion.getCitas() != null && !asignacion.getCitas().isEmpty()) {
                citaRepository.deleteAll(asignacion.getCitas());
            }
        }
        
        // 3. Eliminar todas las Asignaciones del menor
        if (!asignaciones.isEmpty()) {
            asignacionRepository.deleteAll(asignaciones);
        }
        
        // 4. Finalmente, eliminar el Menor
        menorRepository.delete(menorExistente);
    }
}
