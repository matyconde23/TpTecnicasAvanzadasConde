package com.turnos.app.models;

public class ServicioDTO {
    private String nombre;
    private int duracionMinutos;
    private String id;


    public ServicioDTO(String nombre, int duracionMinutos, String id) {
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

