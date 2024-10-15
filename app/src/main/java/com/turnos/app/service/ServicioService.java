package com.turnos.app.service;


import com.turnos.app.models.Profesional;
import com.turnos.app.models.ProfesionalDTO;
import com.turnos.app.models.Servicio;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.ServicioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


        ProfesionalDTO profesionalDTO= new ProfesionalDTO(profesional.getId(),profesional.getApellido(), profesional.getNombre());


        if (servicio.getProfesionales() == null) {
            servicio.setProfesionales(new ArrayList<>());
        }


        boolean profesionalYaAgregado = servicio.getProfesionales().stream()
                .anyMatch(p -> p.getNombre().equals(profesionalDTO.getNombre()) && p.getApellido().equals(profesionalDTO.getApellido()));

        if (profesionalYaAgregado) {
            throw new IllegalArgumentException("El profesional ya está agregado a este servicio.");
        }


        servicio.getProfesionales().add(profesionalDTO);


        return servicioRepo.save(servicio);
    }

    public List<Profesional> getProfesionalesPorServicio(String servicioId) {

        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        List<ProfesionalDTO> profesionalesDTO = servicio.getProfesionales();


        List<Profesional> profesionales = new ArrayList<>();
        for (ProfesionalDTO profesionalDTO : profesionalesDTO) {
            Profesional profesional = profesionalRepo.findById(profesionalDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado"));
            profesionales.add(profesional);
        }

        return profesionales;
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
