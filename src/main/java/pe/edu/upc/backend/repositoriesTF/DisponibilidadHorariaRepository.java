package pe.edu.upc.backend.repositoriesTF;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.backend.entitiesTF.DisponibilidadHoraria;

import java.util.List;
import java.util.Optional;

public interface DisponibilidadHorariaRepository extends JpaRepository<DisponibilidadHoraria, Long> {

    //SQL NATIVAS

    // Encuentra toda la disponibilidad de un psicólogo.
    @Query(value = "SELECT * FROM disponibilidades_horarias dh WHERE dh.psicologo_id = :psicologoId", nativeQuery = true)
    List<DisponibilidadHoraria> findByPsicologoIdSQL(Long psicologoId);

    // Encuentra una disponibilidad por psicólogo y día de la semana (asumiendo que 'diaSemana' es un String o Enum).
    @Query(value = "SELECT * FROM disponibilidades_horarias dh WHERE dh.psicologo_id = :psicologoId AND dh.dia_semana = :diaSemana", nativeQuery = true)
    Optional<DisponibilidadHoraria> findByPsicologoIdAndDiaSemanaSQL(Long psicologoId, String diaSemana);

}
