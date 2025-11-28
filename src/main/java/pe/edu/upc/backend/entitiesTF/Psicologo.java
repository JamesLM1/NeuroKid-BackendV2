package pe.edu.upc.backend.entitiesTF;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "psicologos")
public class Psicologo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long psicologoId;

    private String nombre;
    private String apellido;
    private String tipoDocumento; // Tipo de documento (DNI, Pasaporte, CE)
    private String dni; // Número de documento genérico
    private String especialidad;
    private String email;
    private String telefono;
    private String claveVisible; // Contraseña en texto plano para mostrar al admin
    private Date FechaRegistro;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "psicologo", fetch = FetchType.EAGER)
    private List<DisponibilidadHoraria> disponibilidadeshorarias;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "psicologo", fetch = FetchType.EAGER)
    private List<Asignacion> asignaciones;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "psicologo", fetch = FetchType.EAGER)
    private List<EvaluacionPsicologo> evaluaciones;
}
