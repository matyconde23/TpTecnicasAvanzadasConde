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
import com.turnos.app.models.ProfesionalDisponibilidad;

@RestController
@RequestMapping("/api/profesional")
public class ProfesionalController {

    @Autowired
    private ProfesionalService profesionalService;

    
    @GetMapping("/all")
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

     @PutMapping("/actualizar/{profesionalId}/disponibilidad")
    public ResponseEntity<?> updateDisponibilidad(@PathVariable String profesionalId, @RequestBody List<ProfesionalDisponibilidad> nuevaDisponibilidad) {
        try {
            Profesional profesionalActualizado = profesionalService.updateDisponibilidadProfesional(profesionalId, nuevaDisponibilidad);
            return ResponseEntity.ok(profesionalActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.toString());
        }
    }

    @GetMapping("/{profesionalId}/disponibilidad")
    public ResponseEntity<?> obtenerDisponibilidad(@PathVariable String profesionalId) {
        try {
            List<ProfesionalDisponibilidad> disponibilidad = profesionalService.obtenerDisponibilidad(profesionalId);
            return ResponseEntity.ok(disponibilidad);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.toString());
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesional(@PathVariable String id) {
        profesionalService.deleteProfesional(id);
        return ResponseEntity.noContent().build();
    }
}
