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
public class ProfesionalService {

    @Autowired
    private ProfesionalRepo profesionalRepo;
    @Autowired
    private ServicioRepo servicioRepo;
    // Obtener todos los profesionales
    public List<Profesional> getAllProfesionales() {
        return profesionalRepo.findAll();
    }

    // Obtener un profesional por su ID
    public Optional<Profesional> getProfesionalById(String id) {
        return profesionalRepo.findById(id);
    }

    // Crear o actualizar un profesional
    /*public Profesional saveProfesional(String apellido, String nombre, String email, String telefono, String especialidad, String servicioId) {
        List<Servicio> servicios = new ArrayList<>();

        // Verificar si el servicioId es nulo o está vacío
        if (servicioId != null && !servicioId.isEmpty()) {
            Servicio servicio = servicioRepo.findById(servicioId)
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
            servicios.add(servicio); // Agregar el servicio a la lista si existe
        } else {
            // Si servicioId es nulo, se crea una lista vacía de servicios
            servicios = new ArrayList<>(); // Lista vacía
        }

        // Crear el nuevo profesional con los datos proporcionados y la lista de servicios
        Profesional profesional = new Profesional(apellido, nombre, email, telefono, especialidad, servicios);

        return profesionalRepo.save(profesional);
    }
*/

    // Método para agregar un servicio a un profesional


    public Profesional agregarServicioAProfesional(String profesionalId, String servicioId) {
        // Buscar el profesional por ID
        Profesional profesional = profesionalRepo.findById(profesionalId)
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado"));

        // Buscar el servicio por ID
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        // Obtener la lista de servicios del profesional
        List<Servicio> servicios = profesional.getServicio();
        if (servicios == null) {
            // Si la lista de servicios es nula, inicializarla como una lista vacía
            servicios = new ArrayList<>();
        }

        // Verificar si el servicio ya está en la lista
        boolean servicioExists = servicios.stream()
                .anyMatch(s -> s.getId().equals(servicio.getId())); // Comparar por ID

        if (servicioExists) {
            // Si el servicio ya existe en la lista, lanzar una excepción
            throw new IllegalArgumentException("El servicio ya está agregado al profesional");
        }

        // Agregar el servicio a la lista de servicios del profesional
        servicios.add(servicio);

        // Actualizar la lista de servicios en el profesional
        profesional.setServicio(servicios);

        // Guardar el profesional actualizado en la base de datos
        return profesionalRepo.save(profesional);
    }
    // Eliminar un profesional por su ID
    public void deleteProfesional(String id) {
        profesionalRepo.deleteById(id);
    }
}
