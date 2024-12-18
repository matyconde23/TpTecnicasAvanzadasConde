package com.turnos.app.controller;

import com.turnos.app.models.EstadoTurno;
import com.turnos.app.models.Profesional;
import com.turnos.app.models.Turno;
import com.turnos.app.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.turnos.app.models.Servicio;

@RestController
@RequestMapping("/api/servicio")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;


    @GetMapping("/all")
    public List<Servicio> getAllservicios() {
        return servicioService.getAllServicios();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getServicioById(@PathVariable String id) {
        Optional<Servicio> servicio = servicioService.getServicioById(id);
        return servicio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/crear")
    public ResponseEntity<?> crearServicio(@RequestBody Map<String, Object> request) {
        try {
            
            String nombre = (String) request.get("nombre");
            String descripcion = (String) request.get("descripcion");
            Integer duracionMinutos = (Integer) request.get("duracionMinutos"); 

           
            if (nombre == null || descripcion == null || duracionMinutos == null) {
                return ResponseEntity.badRequest().body("El nombre, la descripción y la duración son obligatorios.");
            }

           
            Servicio nuevoServicio = servicioService.saveServicio(nombre, descripcion, new ArrayList<>(), duracionMinutos);

            
            return ResponseEntity.ok(nuevoServicio);
        } catch (IllegalArgumentException e) {
            
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el servicio: " + e.getMessage());
        }
    }


    @PostMapping("/agregar-profesional")
    public ResponseEntity<?> agregarProfesionalaServicio(@RequestBody Map<String, Object> request) {
        String profesionalId = request.containsKey("profesionalId") ? (String) request.get("profesionalId") : null;
        String servicioId = request.containsKey("servicioId") ? (String) request.get("servicioId") : null;

        try {
            
            Servicio actualizado = servicioService.agregarProfesionalaServicio(profesionalId, servicioId);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al agregar el profesional al servicio: " + e.getMessage());
        }
    }

    
    @GetMapping("/{servicioId}/profesionales")
    public ResponseEntity<?> getProfesionalesPorServicio(@PathVariable String servicioId) {
        try {
            
            List<Profesional> profesionales = servicioService.getProfesionalesPorServicio(servicioId);
            return ResponseEntity.ok(profesionales);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.toString());
        }
    }

    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<Servicio>> obtenerServiciosPorProfesional(@PathVariable String profesionalId) {
        List<Servicio> servicios = servicioService.obtenerServiciosPorProfesional(profesionalId);
        return ResponseEntity.ok(servicios);
    }



}
