package pe.edu.upc.backend.entitiesTF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recursos_educativos")
public class RecursoEducativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recursoId;

    private String titulo;
    private String Descripcion;
    private String Link;
    private String Texto;
    private Date fechaCreacion;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "recurso", fetch = FetchType.EAGER)
    private List<Favorito> favoritos;

    public RecursoEducativo(Object o, String guíaDeJuegoSensorial, String actividadesParaIntegración, String url, String textoSensorial, Date date) {
    }
}
