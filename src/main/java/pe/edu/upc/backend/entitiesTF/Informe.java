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
@Table(name = "informes")
public class Informe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long informeId;

    private Integer Mes;
    private Integer Ano;
    private String Resumen;
    private String titulo;
    private String contenido;
    private Date fechaCreacion;
    private Integer calificacionEficacia;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "asignacion_id")
    private Asignacion asignacion;
}
