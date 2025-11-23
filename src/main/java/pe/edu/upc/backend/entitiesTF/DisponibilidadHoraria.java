package pe.edu.upc.backend.entitiesTF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "disponibilidades_horarias")
public class DisponibilidadHoraria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disponibilidadId;

    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;
}
