package pe.edu.upc.backend.entitiesTF;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "padres")
public class Padre{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long padreId;

    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String dni; // Ahora funciona como numeroDocumento genérico
    private String email;
    private String telefono;
    private String tipoParentesco;
    private String claveVisible; // Contraseña en texto plano para mostrar al admin
    private Date fechaRegistro;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "padre",  fetch = FetchType.EAGER)
    private List<Menor> menores;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "padre", fetch = FetchType.EAGER)
    private List<Favorito> favoritos;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "padre", fetch = FetchType.EAGER)
    private List<Asignacion> asignaciones;

}
