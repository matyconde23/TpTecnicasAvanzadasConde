package com.turnos.app.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "turnos")
public class Turno {
    @Id
    private String id;
    private LocalDate dia;
    private LocalTime fechainicio;
    private LocalTime fechaFin;
    private EstadoTurno estado;
    @JsonBackReference
    private UsuarioDTO usuario;
    private ProfesionalDTO profesional;
    private ServicioDTO servicio;

    public Turno(LocalDate dia, LocalTime fechainicio, LocalTime fechaFin, EstadoTurno estado, UsuarioDTO usuario, ProfesionalDTO profesional, ServicioDTO servicio) {
        this.dia = dia;
        this.fechainicio = fechainicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.usuario = usuario;
        this.profesional = profesional;
        this.servicio = servicio;
    }

    
 
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }


    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public LocalTime getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(LocalTime fechainicio) {
        this.fechainicio = fechainicio;
    }

    public LocalTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public ProfesionalDTO getProfesional() {
        return profesional;
    }

    public void setProfesional(ProfesionalDTO profesional) {
        this.profesional = profesional;
    }

    public ServicioDTO getServicio() {
        return servicio;
    }

    public void setServicio(ServicioDTO servicio) {
        this.servicio = servicio;
    }
}
