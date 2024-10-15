package com.turnos.app.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "servicios")
public class Servicio{

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private int duracionMinutos;
    private List<ProfesionalDTO> profesionales;

    public Servicio(String nombre, String descripcion, int duracionMinutos, List<ProfesionalDTO> profesionales) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.duracionMinutos = duracionMinutos;
            this.profesionales = profesionales;

    }

    // Getters y setters

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<ProfesionalDTO> getProfesionales() {
        return profesionales;
    }

    public void setProfesionales(List<ProfesionalDTO> profesionales) {
        this.profesionales = profesionales;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
}