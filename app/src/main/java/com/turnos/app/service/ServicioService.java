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

    // Buscar al profesional en la base de datos
        Profesional profesional = profesionalRepo.findById(profesionalId)
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado con el ID: " + profesionalId));

    // Buscar el servicio en la base de datos
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con el ID: " + servicioId));

    // Crear un DTO del profesional
        ProfesionalDTO profesionalDTO = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());

    // Inicializar la lista de profesionales si está vacía
        if (servicio.getProfesionales() == null) {
            servicio.setProfesionales(new ArrayList<>());
        }

    // Validar si el profesional ya está asociado al servicio
        boolean profesionalYaAgregado = servicio.getProfesionales().stream()
                .anyMatch(p -> p.getId().equals(profesionalDTO.getId())); // Comprobación basada en el ID único

        if (profesionalYaAgregado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El profesional ya está agregado a este servicio.");
        }

    // Agregar el profesional al servicio
        servicio.getProfesionales().add(profesionalDTO);

    // Guardar y devolver el servicio actualizado
        return servicioRepo.save(servicio);
}


public List<Profesional> getProfesionalesPorServicio(String servicioId) {

    Servicio servicio = servicioRepo.findById(servicioId)
            .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

    List<ProfesionalDTO> profesionalesDTO = servicio.getProfesionales();

    // Si la lista de profesionalesDTO está vacía, retornar una lista vacía
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

    // Eliminar un profesional por su ID
    /*public void eliminarServicio(String servicioId) {

        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));


        List<Profesional> profesionales = profesionalRepo.findAll();

        for (Profesional profesional : profesionales) {
            // Obtener la lista de servicios del profesional
            List<Servicio> servicios = profesional.getServicio();

            if (servicios != null && servicios.contains(servicio)) {
                // Eliminar el servicio de la lista
                servicios.remove(servicio);
                profesional.setServicio(servicios); // Actualizar la lista en el profesional
                profesionalRepo.save(profesional); // Guardar los cambios en la base de datos
            }
        }


        servicioRepo.delete(servicio);
    }

     */
