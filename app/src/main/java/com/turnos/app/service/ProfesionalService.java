package com.turnos.app.service;

import com.turnos.app.models.Profesional;
import com.turnos.app.models.ProfesionalDisponibilidad;
import com.turnos.app.models.Servicio;
import com.turnos.app.repository.ProfesionalRepo;


import com.turnos.app.repository.ServicioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalService {

    @Autowired
    private ProfesionalRepo profesionalRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<Profesional> getAllProfesionales() {
        return profesionalRepo.findAll();
    }
    
    public Profesional findByUsername(String username) {
        return profesionalRepo.findByUsername(username).orElse(null);
    }

    public Optional<Profesional> getProfesionalById(String id) {
        return profesionalRepo.findById(id);
    }


    public Profesional saveProfesional(Profesional profesional) {

        if (profesionalRepo.existsByUsername(profesional.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }


        if (profesional.getRole() == null) {
            profesional.setRole("PROFESIONAL");
        }


        if (profesional.getDisponibilidad() == null || profesional.getDisponibilidad().isEmpty()) {
            System.out.println("No se recibió disponibilidad personalizada. Generando disponibilidad por defecto.");
            profesional.setDisponibilidad(generarDisponibilidadPorDefecto());
        } else {
            System.out.println("Disponibilidad personalizada recibida:");
            for (ProfesionalDisponibilidad disponibilidad : profesional.getDisponibilidad()) {
                System.out.println(" - Día: " + disponibilidad.getDiaSemana());
                System.out.println("   Hora Inicio: " + disponibilidad.getHoraInicio());
                System.out.println("   Hora Fin: " + disponibilidad.getHoraFin());
            }
        }


        profesional.setPassword(passwordEncoder.encode(profesional.getPassword()));


        return profesionalRepo.save(profesional);
    }



    public List<ProfesionalDisponibilidad> generarDisponibilidadPorDefecto() {
        List<ProfesionalDisponibilidad> disponibilidadPorDefecto = new ArrayList<>();
    
        // Crear un mapeo de los días en español a sus valores correspondientes de DayOfWeek
        DayOfWeek[] diasSemana = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
    
        for (DayOfWeek dia : diasSemana) {
            ProfesionalDisponibilidad disponibilidad = new ProfesionalDisponibilidad(dia, LocalTime.of(9, 0), LocalTime.of(16, 0), null);
            disponibilidadPorDefecto.add(disponibilidad);
        }
    
        return disponibilidadPorDefecto;
    }



    public void deleteProfesional(String id) {
        profesionalRepo.deleteById(id);
    }
}
