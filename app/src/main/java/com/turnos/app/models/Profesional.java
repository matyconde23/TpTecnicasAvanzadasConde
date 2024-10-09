package com.turnos.app.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profesional extends Persona
    {
    private String id;
    private String especialidad;
    private List<Servicio> servicio;
    private String password;
    private String username;

    public Profesional(String apellido, String nombre, String email, String telefono, String especialidad, List<Servicio> servicio, String password, String username) {
            super(apellido, nombre, email, telefono);
            this.especialidad = especialidad;
            this.servicio = servicio;
            this.password = password;
            this.username = username;
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

        public List<Servicio> getServicio() {
            return servicio;
        }

        public void setServicio(List<Servicio> servicios) {
            this.servicio = servicios;
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
    }
