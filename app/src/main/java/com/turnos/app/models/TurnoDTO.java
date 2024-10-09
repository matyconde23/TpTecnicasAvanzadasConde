package com.turnos.app.models;

import java.time.LocalDateTime;

public class TurnoDTO {
    private LocalDateTime fechaHora;
    private EstadoTurno estado;
    private String usuarioNombre;
    private String profesionalNombre;
    private String servicioNombre;

    public TurnoDTO(LocalDateTime fechaHora, EstadoTurno estado, String usuarioNombre, String profesionalNombre, String servicioNombre) {
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.usuarioNombre = usuarioNombre;
        this.profesionalNombre = profesionalNombre;
        this.servicioNombre = servicioNombre;
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getProfesionalNombre() {
        return profesionalNombre;
    }

    public void setProfesionalNombre(String profesionalNombre) {
        this.profesionalNombre = profesionalNombre;
    }

    public String getServicioNombre() {
        return servicioNombre;
    }

    public void setServicioNombre(String servicioNombre) {
        this.servicioNombre = servicioNombre;
    }

    // Constructor, getters y setters
}
