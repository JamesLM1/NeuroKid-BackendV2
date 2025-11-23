package pe.edu.upc.backend.entitiesTF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "evaluaciones_psicologos")

public class EvaluacionPsicologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluacionId;

    private Integer Puntaje;
    private String Comentario;
    private Date FechaEvaluacion;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "padre_id")
    private Padre padre;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;
}
