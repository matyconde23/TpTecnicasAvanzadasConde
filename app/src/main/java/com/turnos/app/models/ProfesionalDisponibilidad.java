package com.turnos.app.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfesionalDisponibilidad {
    @JsonProperty("dia")
    private DayOfWeek diaSemana; 
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Profesional profesional;

    public ProfesionalDisponibilidad(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin, Profesional profesional) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.profesional = profesional;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
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
