package pe.edu.upc.backend.dtosTF;

import java.time.LocalDate;

/**
 * DTO para solicitar citas directamente eligiendo menor y psicólogo
 * Reemplaza el modelo anterior que dependía de asignaciones pre-existentes
 */
public class PADRESolicitudCitaDTO {
    
    private Long menorId;
    private Long psicologoId;
    private LocalDate fecha;
    private String horaInicio;
    private String horaFin;
    private String motivo;
    private String estado;

    // Constructors
    public PADRESolicitudCitaDTO() {}

    public PADRESolicitudCitaDTO(Long menorId, Long psicologoId, LocalDate fecha, 
                                String horaInicio, String horaFin, String motivo, String estado) {
        this.menorId = menorId;
        this.psicologoId = psicologoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.motivo = motivo;
        this.estado = estado;
    }

    // Getters and Setters
    public Long getMenorId() {
        return menorId;
    }

    public void setMenorId(Long menorId) {
        this.menorId = menorId;
    }

    public Long getPsicologoId() {
        return psicologoId;
    }

    public void setPsicologoId(Long psicologoId) {
        this.psicologoId = psicologoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "PADRESolicitudCitaDTO{" +
                "menorId=" + menorId +
                ", psicologoId=" + psicologoId +
                ", fecha=" + fecha +
                ", horaInicio='" + horaInicio + '\'' +
                ", horaFin='" + horaFin + '\'' +
                ", motivo='" + motivo + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
