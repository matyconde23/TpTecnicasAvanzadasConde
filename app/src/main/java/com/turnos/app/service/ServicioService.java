package com.turnos.app.service;


import com.turnos.app.models.Profesional;
import com.turnos.app.models.ProfesionalDTO;
import com.turnos.app.models.Servicio;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.ServicioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepo servicioRepo;
    
    @Autowired
    private ProfesionalRepo profesionalRepo;



    public List<Servicio> getAllServicios() {
        return servicioRepo.findAll();
    }


    public Optional<Servicio> getServicioById(String id) {
        return servicioRepo.findById(id);
    }


    public Servicio saveServicio(String nombre, String descripcion, List<ProfesionalDTO> profesionales, int duracionMinutos) {

        if (profesionales == null) {
            profesionales = new ArrayList<>();
        }


        Servicio servicio = new Servicio(nombre, descripcion, duracionMinutos, profesionales);


        return servicioRepo.save(servicio);
    }


    public Servicio agregarProfesionalaServicio(String profesionalId, String servicioId) {

        if (profesionalId == null || servicioId == null) {
            throw new IllegalArgumentException("El ID del profesional y el ID del servicio son obligatorios.");
        }

    
        Profesional profesional = profesionalRepo.findById(profesionalId)
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado con el ID: " + profesionalId));

    
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con el ID: " + servicioId));

    
        ProfesionalDTO profesionalDTO = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());

        if (servicio.getProfesionales() == null) {
            servicio.setProfesionales(new ArrayList<>());
        }

    
        boolean profesionalYaAgregado = servicio.getProfesionales().stream()
                .anyMatch(p -> p.getId().equals(profesionalDTO.getId())); // Comprobación basada en el ID único

        if (profesionalYaAgregado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El profesional ya está agregado a este servicio.");
        }

    
        servicio.getProfesionales().add(profesionalDTO);

    
        return servicioRepo.save(servicio);
}


public List<Profesional> getProfesionalesPorServicio(String servicioId) {

    Servicio servicio = servicioRepo.findById(servicioId)
            .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

    List<ProfesionalDTO> profesionalesDTO = servicio.getProfesionales();

    
    if (profesionalesDTO == null || profesionalesDTO.isEmpty()) {
        return Collections.emptyList();
    }

    List<Profesional> profesionales = new ArrayList<>();
    for (ProfesionalDTO profesionalDTO : profesionalesDTO) {
        profesionalRepo.findById(profesionalDTO.getId()).ifPresent(profesionales::add);
    }

    return profesionales;
}



public List<Servicio> obtenerServiciosPorProfesional(String profesionalId) {
    return servicioRepo.findByProfesionales_Id(profesionalId);
    }
}

