package com.turnos.app.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "turnos")
public class Turno {
    @Id
    private String id;
    private LocalDateTime fechaHora;
    private EstadoTurno estado;
    @JsonBackReference
    private Usuario usuario;
    @JsonBackReference
    private Profesional profesional;
    private Servicio servicio;

    public Turno(LocalDateTime fechaHora, EstadoTurno estado, Usuario usuario, Profesional profesional, Servicio servicio) {
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.usuario = usuario;
        this.profesional = profesional;
        this.servicio = servicio;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Profesional getProfesional() { return profesional; }
    public void setProfesional(Profesional profesional) { this.profesional = profesional; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
}
