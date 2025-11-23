package pe.edu.upc.backend.entitiesTF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long asignacionId;

    private Date fechaAsignacion;
    private String estado;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "padre_id")
    private Padre padre;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "menor_id")
    private Menor menor;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "asignacion", fetch = FetchType.EAGER)
    private List<Cita> citas;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "asignacion", fetch = FetchType.EAGER)
    private List<Informe> informes;


    public Asignacion(Object o, Padre padre1, Menor menor1, Psicologo psicologo1, Date date, String activa) {
    }
}
