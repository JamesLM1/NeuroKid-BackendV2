package pe.edu.upc.backend.servicesimplTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINCitaListDTO;
import pe.edu.upc.backend.entitiesTF.Cita;
import pe.edu.upc.backend.repositoriesTF.CitaRepository;
import pe.edu.upc.backend.servicesTF.ADMINCitaService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINCitaServiceImpl implements ADMINCitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<ADMINCitaListDTO> obtenerTodasLasCitas() {
        return citaRepository.findAll().stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ADMINCitaListDTO::getFecha)
                        .thenComparing(ADMINCitaListDTO::getHoraInicio)
                        .reversed()) // Orden descendente: más recientes primero
                .collect(Collectors.toList());
    }

    private ADMINCitaListDTO convertToDTO(Cita cita) {
        ADMINCitaListDTO dto = new ADMINCitaListDTO();
        
        dto.setCitaId(cita.getCitaId());
        dto.setFecha(cita.getFecha());
        dto.setHoraInicio(cita.getHoraInicio());
        dto.setHoraFin(cita.getHoraFin());
        dto.setEstado(cita.getEstado());
        dto.setMotivo(cita.getMotivo());
        
        // Obtener nombres desde la asignación
        if (cita.getAsignacion() != null) {
            // Nombre del Psicólogo
            if (cita.getAsignacion().getPsicologo() != null) {
                dto.setPsicologoId(cita.getAsignacion().getPsicologo().getPsicologoId());
                dto.setNombrePsicologo(
                    cita.getAsignacion().getPsicologo().getNombre() + " " + 
                    cita.getAsignacion().getPsicologo().getApellido()
                );
            }
            
            // Nombre del Menor
            if (cita.getAsignacion().getMenor() != null) {
                dto.setMenorId(cita.getAsignacion().getMenor().getMenorId());
                dto.setNombreMenor(
                    cita.getAsignacion().getMenor().getNombre() + " " + 
                    cita.getAsignacion().getMenor().getApellido()
                );
            }
            
            // Nombre del Padre
            if (cita.getAsignacion().getPadre() != null) {
                dto.setPadreId(cita.getAsignacion().getPadre().getPadreId());
                dto.setNombrePadre(
                    cita.getAsignacion().getPadre().getNombre() + " " + 
                    cita.getAsignacion().getPadre().getApellido()
                );
            }
        }
        
        return dto;
    }
}

