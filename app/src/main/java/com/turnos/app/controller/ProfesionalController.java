package com.turnos.app.controller;

import com.turnos.app.models.Servicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.turnos.app.service.ProfesionalService;
import com.turnos.app.models.Profesional;

@RestController
@RequestMapping("/api/profesional")
public class ProfesionalController {

    @Autowired
    private ProfesionalService profesionalService;


    @GetMapping
    public List<Profesional> getAllProfesionales() {
        return profesionalService.getAllProfesionales();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Profesional> getProfesionalById(@PathVariable String id) {
        Optional<Profesional> profesional = profesionalService.getProfesionalById(id);
        if (profesional.isPresent()) {
            return ResponseEntity.ok(profesional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear o actualizar un profesional
    /*@PostMapping("/crear")
    public ResponseEntity<?> crearProfesional(@RequestBody Map<String, Object> request) {
        try {
            // Parsear y extraer datos del request
            String apellido = (String) request.get("apellido");
            String nombre = (String) request.get("nombre");
            String email = (String) request.get("email");
            String telefono = (String) request.get("telefono");
            String especialidad = (String) request.get("especialidad");
            String servicioId = request.containsKey("servicioId") ? (String) request.get("servicioId") : null;


            // Crear un objeto Profesional
            Profesional nuevoProfesional = profesionalService.saveProfesional(apellido, nombre, email, telefono, especialidad, servicioId);

            // Llamar al servicio para guardar el profesional


            // Responder con el profesional creado
            return ResponseEntity.ok(nuevoProfesional);
        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el profesional: " + e.getMessage());
        }
    }

     */







    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesional(@PathVariable String id) {
        profesionalService.deleteProfesional(id);
        return ResponseEntity.noContent().build();
    }
}
