package pe.edu.upc.backend.servicesimplTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINEvaluacionListDTO;
import pe.edu.upc.backend.entitiesTF.EvaluacionPsicologo;
import pe.edu.upc.backend.repositoriesTF.EvaluacionPsicologoRepository;
import pe.edu.upc.backend.servicesTF.ADMINEvaluacionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINEvaluacionServiceImpl implements ADMINEvaluacionService {

    @Autowired
    private EvaluacionPsicologoRepository evaluacionRepository;

    private ADMINEvaluacionListDTO convertToDTO(EvaluacionPsicologo entity) {
        ADMINEvaluacionListDTO dto = new ADMINEvaluacionListDTO();
        dto.setEvaluacionId(entity.getEvaluacionId());
        dto.setFechaEvaluacion(entity.getFechaEvaluacion());
        dto.setPuntaje(entity.getPuntaje());
        dto.setComentario(entity.getComentario());

        // Nombres completos de entidades relacionadas
        if (entity.getPsicologo() != null) {
            dto.setNombrePsicologo(entity.getPsicologo().getNombre() + " " + entity.getPsicologo().getApellido());
        }
        if (entity.getPadre() != null) {
            dto.setNombrePadre(entity.getPadre().getNombre() + " " + entity.getPadre().getApellido());
        }

        return dto;
    }

    @Override
    public List<ADMINEvaluacionListDTO> obtenerTodasLasEvaluaciones() {
        // Ordenar por fecha descendente (más recientes primero)
        Sort sort = Sort.by(Sort.Direction.DESC, "fechaEvaluacion");
        return evaluacionRepository.findAll(sort).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarEvaluacion(Long id) {
        if (!evaluacionRepository.existsById(id)) {
            throw new RuntimeException("Evaluación no encontrada con ID: " + id);
        }
        evaluacionRepository.deleteById(id);
    }
}

