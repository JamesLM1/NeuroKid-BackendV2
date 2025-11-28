package pe.edu.upc.backend.entitiesTF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menores")
public class Menor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menorId;

    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String diagnostico;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "padreId")
    private Padre padre;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "menor", fetch = FetchType.EAGER)
    private List<Asignacion> asignaciones;
}
