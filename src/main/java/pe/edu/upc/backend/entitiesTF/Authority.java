package pe.edu.upc.backend.entitiesTF;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ejemplo: ROLE_ADMIN, ROLE_PSICOLOGO, ROLE_PADRE

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities")
    private List<User> users = new ArrayList<>();
}




