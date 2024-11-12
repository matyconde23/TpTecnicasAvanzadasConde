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



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesional(@PathVariable String id) {
        profesionalService.deleteProfesional(id);
        return ResponseEntity.noContent().build();
    }
}
