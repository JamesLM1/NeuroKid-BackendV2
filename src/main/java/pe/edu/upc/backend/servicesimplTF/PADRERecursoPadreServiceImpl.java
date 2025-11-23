package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.PADREFavoritoDTO;
import pe.edu.upc.backend.dtosTF.ADMINRecursoEducativoDTO;
import pe.edu.upc.backend.entitiesTF.Favorito;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.entitiesTF.RecursoEducativo;
import pe.edu.upc.backend.repositoriesTF.FavoritoRepository;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.repositoriesTF.RecursoEducativoRepository;
import pe.edu.upc.backend.servicesTF.PADRERecursoPadreService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PADRERecursoPadreServiceImpl implements PADRERecursoPadreService {

    @Autowired
    private RecursoEducativoRepository recursoRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private PadreRepository padreRepository;

    // --- Métodos de conversión ---
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

    // --- Implementación ---

    @Override
    public List<ADMINRecursoEducativoDTO> buscarRecursos(String termino) {
        return recursoRepository.findByTituloContaining(termino).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PADREFavoritoDTO marcarFavorito(Long padreId, Long recursoId) {
        Padre padre = padreRepository.findById(padreId)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + padreId));
        RecursoEducativo recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new EntityNotFoundException("Recurso no encontrado con ID: " + recursoId));

        // **CORRECCIÓN:** Se asume que el repositorio devuelve 'Favorito' (o null) en lugar de 'Optional'
        Favorito existente = favoritoRepository.findByPadreIdAndRecursoIdSQL(padreId, recursoId);

        if (existente != null) { // Se verifica contra null
            throw new IllegalStateException("El recurso ya está marcado como favorito por este padre.");
        }

        Favorito favorito = new Favorito();
        favorito.setPadre(padre);
        favorito.setRecurso(recurso);
        favorito.setFechaMarcado(new Date());

        Favorito guardado = favoritoRepository.save(favorito);

        PADREFavoritoDTO dto = new PADREFavoritoDTO();
        dto.setFavoritoId(guardado.getFavoritoId());
        dto.setPadreId(padreId);
        dto.setRecursoId(recursoId);
        dto.setFechaMarcado(guardado.getFechaMarcado());
        return dto;
    }

    @Override
    @Transactional
    public void desmarcarFavorito(Long padreId, Long recursoId) {
        // **CORRECCIÓN:** Se asume que el repositorio devuelve 'Favorito' (o null) en lugar de 'Optional'
        Favorito favorito = favoritoRepository.findByPadreIdAndRecursoIdSQL(padreId, recursoId);

        if (favorito == null) {
            throw new EntityNotFoundException("El favorito no existe o no fue marcado por este padre.");
        }

        // Se elimina el objeto de la base de datos
        favoritoRepository.delete(favorito);
    }

    @Override
    public List<ADMINRecursoEducativoDTO> obtenerRecursosFavoritos(Long padreId) {
        // NOTA: findByPadre_PadreId debe devolver una List<Favorito> para que funcione el .stream()
        return favoritoRepository.findByPadreIdSQL(padreId).stream()
                .map(Favorito::getRecurso)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
