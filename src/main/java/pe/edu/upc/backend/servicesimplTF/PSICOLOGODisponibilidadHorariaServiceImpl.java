package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PSICOLOGODisponibilidadDTO;
import pe.edu.upc.backend.entitiesTF.DisponibilidadHoraria;
import pe.edu.upc.backend.entitiesTF.Psicologo;
import pe.edu.upc.backend.repositoriesTF.DisponibilidadHorariaRepository;
import pe.edu.upc.backend.repositoriesTF.PsicologoRepository;
import pe.edu.upc.backend.servicesTF.PSICOLOGODisponibilidadHorariaService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PSICOLOGODisponibilidadHorariaServiceImpl implements PSICOLOGODisponibilidadHorariaService {
    @Autowired
    private DisponibilidadHorariaRepository disponibilidadRepository;
    @Autowired private PsicologoRepository psicologoRepository;

    private PSICOLOGODisponibilidadDTO convertToDTO(DisponibilidadHoraria entity) {
        PSICOLOGODisponibilidadDTO dto = new PSICOLOGODisponibilidadDTO();
        dto.setDisponibilidadId(entity.getDisponibilidadId());
        dto.setPsicologoId(entity.getPsicologo().getPsicologoId());
        dto.setDiaSemana(entity.getDiaSemana());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFin(entity.getHoraFin());
        return dto;
    }

    @Override
    public PSICOLOGODisponibilidadDTO crearOActualizarDisponibilidad(Long psicologoId, PSICOLOGODisponibilidadDTO dto) {
        Psicologo psicologo = psicologoRepository.findById(psicologoId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado."));

        // Busca si ya existe una disponibilidad para ese día y ese psicólogo
        DisponibilidadHoraria disponibilidadExistente = disponibilidadRepository
                .findByPsicologoIdAndDiaSemanaSQL(psicologoId, dto.getDiaSemana())
                .orElse(new DisponibilidadHoraria());

        // Si es nuevo, establece el psicólogo
        if(disponibilidadExistente.getDisponibilidadId() == null) {
            disponibilidadExistente.setPsicologo(psicologo);
            disponibilidadExistente.setDiaSemana(dto.getDiaSemana());
        }

        // Actualiza las horas
        disponibilidadExistente.setHoraInicio(dto.getHoraInicio());
        disponibilidadExistente.setHoraFin(dto.getHoraFin());

        DisponibilidadHoraria guardada = disponibilidadRepository.save(disponibilidadExistente);
        return convertToDTO(guardada);
    }

    @Override
    public List<PSICOLOGODisponibilidadDTO> obtenerDisponibilidadPorPsicologo(Long psicologoId) {
        // Se valida existencia del psicólogo internamente si se requiere, pero el repositorio ya filtra
        return disponibilidadRepository.findByPsicologoIdSQL(psicologoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarDisponibilidad(Long disponibilidadId, Long psicologoId) {
        DisponibilidadHoraria disp = disponibilidadRepository.findById(disponibilidadId)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilidad no encontrada."));

        // Validación de seguridad: solo el psicólogo dueño puede eliminar su disponibilidad
        if (!disp.getPsicologo().getPsicologoId().equals(psicologoId)) {
            throw new SecurityException("Acceso denegado: El registro de disponibilidad no pertenece a este psicólogo.");
        }
        disponibilidadRepository.deleteById(disponibilidadId);
    }
}
