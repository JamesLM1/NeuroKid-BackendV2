package pe.edu.upc.backend.dtosTF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PADREInformeDTO {
    private Long informeId;
    private Long menorId;
    private String nombreMenor;
    private Integer mes;
    private Integer anio;
    private String resumen;
    
    // Campos enriquecidos para mejor visualización
    private Date fechaCreacion;
    private String nombrePsicologo;
    private String titulo; // Título del informe (para búsqueda inteligente)
}

//(Para Lectura de Progreso)