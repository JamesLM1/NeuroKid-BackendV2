package pe.edu.upc.backend.entitiesTF;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Time;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long citaId;

    private Date fecha;
    private Time horaInicio;
    private Time horaFin;
    private String motivo;
    private String hallazgos;
    private String tareas;
    private String estado;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "asignacion_id")
    private Asignacion asignacion;
}
