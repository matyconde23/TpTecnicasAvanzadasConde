package com.turnos.app.models;


import com.turnos.app.models.Profesional;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "servicios")
public class Servicio{

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private List<Profesional> profesional;

    public Servicio(String nombre, String descripcion, List<Profesional> profesional) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.profesional = profesional;
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


    public List<Profesional> getProfesional() {
        return profesional;
    }

    public void setProfesional(List<Profesional> profesional) {
        this.profesional = profesional;
    }
}