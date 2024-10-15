package com.turnos.app.models;

import java.time.LocalTime;

public class ProfesionalDisponibilidad {

    private String diaSemana;  // Ej: Lunes, Martes, etc.
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Profesional profesional;

    public ProfesionalDisponibilidad(String diaSemana, LocalTime horaInicio, LocalTime horaFin, Profesional profesional) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.profesional = profesional;
    }


    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesional profesional) {
        this.profesional = profesional;
    }
}


