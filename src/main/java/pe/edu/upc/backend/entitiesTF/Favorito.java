package pe.edu.upc.backend.entitiesTF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favoritos")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoritoId;

    private Date fechaMarcado;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "padreId")
    private Padre padre;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recursoId")
    private RecursoEducativo recurso;
}
