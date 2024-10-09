package com.turnos.app.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



public class Persona {
    private String apellido;
    private String nombre;
    private String email;
    private String telefono;

    public Persona(String apellido, String nombre, String email, String telefono) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
