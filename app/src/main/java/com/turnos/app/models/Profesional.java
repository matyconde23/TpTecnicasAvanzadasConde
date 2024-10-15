package com.turnos.app.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class Profesional extends Persona {

    private String id;
    private String especialidad;
    private String password;
    private String username;
    private String role;
    private List<ProfesionalDisponibilidad> disponibilidad;


    public Profesional(String apellido, String nombre, String email, String telefono, String especialidad, String password, String username, String role, List<ProfesionalDisponibilidad> disponibilidad) {
        super(apellido, nombre, email, telefono);
        this.especialidad = especialidad;
        this.password = password;
        this.username = username;
        this.role = role;
        this.disponibilidad = disponibilidad;
    }


    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<ProfesionalDisponibilidad> getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(List<ProfesionalDisponibilidad> disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}

