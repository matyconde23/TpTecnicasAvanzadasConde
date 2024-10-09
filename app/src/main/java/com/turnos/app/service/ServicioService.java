package com.turnos.app.service;


import com.turnos.app.models.Profesional;
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
    // Obtener todos los profesionales
    public List<Servicio> getAllServicios() {
        return servicioRepo.findAll();
    }

    // Obtener un profesional por su ID
    public Optional<Servicio> getServicioById(String id) {
        return servicioRepo.findById(id);
    }

    // Crear o actualizar un profesional
    public Servicio saveServicio(String nombre, String descripcion, String profesionalId) {
        List<Profesional> profesionales = new ArrayList<>();

        // Verificar si el profesionalId es nulo o está vacío
        if (profesionalId != null && !profesionalId.isEmpty()) {
            Profesional profesional = profesionalRepo.findById(profesionalId)
                    .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado"));
            profesionales.add(profesional); // Agregar el profesional a la lista si existe
        }

        // Crear el nuevo servicio con o sin profesionales
        Servicio servicio = new Servicio(nombre, descripcion, profesionales);

        return servicioRepo.save(servicio);
    }


    // Eliminar un profesional por su ID
    public void eliminarServicio(String servicioId) {
        // Buscar el servicio por ID
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        // Obtener todos los profesionales
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

        // Eliminar el servicio de la base de datos
        servicioRepo.delete(servicio);
    }
}