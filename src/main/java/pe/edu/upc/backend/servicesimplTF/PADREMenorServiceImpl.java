package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PADREMenorDTO;
import pe.edu.upc.backend.entitiesTF.Menor;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.repositoriesTF.MenorRepository;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.servicesTF.PADREMenorService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PADREMenorServiceImpl implements PADREMenorService {

    @Autowired private MenorRepository menorRepository;
    @Autowired private PadreRepository padreRepository;

    // Métodos de conversión completos:
    private Menor convertToEntity(PADREMenorDTO dto, Padre padre) {
        Menor entity = new Menor();
        // El ID solo se usa en Update, no en Create
        entity.setMenorId(dto.getMenorId());
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setPadre(padre);
        return entity;
    }

    private PADREMenorDTO convertToDTO(Menor entity) {
        PADREMenorDTO dto = new PADREMenorDTO();
        dto.setMenorId(entity.getMenorId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setPadreId(entity.getPadre().getPadreId());
        return dto;
    }

    // --- Implementación de CRUD ---

    @Override
    public PADREMenorDTO registrarMenor(Long padreId, PADREMenorDTO PADREMenorDTO) {
        Padre padre = padreRepository.findById(padreId)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + padreId));

        Menor menor = convertToEntity(PADREMenorDTO, padre);
        Menor nuevoMenor = menorRepository.save(menor);
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
    public void eliminarMenor(Long menorId, Long padreId) {
        Menor menorExistente = menorRepository.findById(menorId)
                .orElseThrow(() -> new EntityNotFoundException("Menor no encontrado con ID: " + menorId));

        if (!menorExistente.getPadre().getPadreId().equals(padreId)) {
            throw new SecurityException("Acceso denegado.");
        }

        menorRepository.delete(menorExistente);
    }
}
