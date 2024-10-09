package com.turnos.app.controller;

import com.turnos.app.models.EstadoTurno;
import com.turnos.app.models.Turno;
import com.turnos.app.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.turnos.app.models.Servicio;

@RestController
@RequestMapping("/api/servicio")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    // Obtener todos los profesionales
    @GetMapping
    public List<Servicio> getAllservicios() {
        return servicioService.getAllServicios();
    }

    // Obtener un profesional por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getServicioById(@PathVariable String id) {
        Optional<Servicio> servicio = servicioService.getServicioById(id);
        return servicio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear o actualizar un profesional
    @PostMapping("/crear")
    public ResponseEntity<?> crearServicio(@RequestBody Map<String, Object> request) {
        try {
            // Parsear y extraer datos del request
            String nombre = (String) request.get("nombre");
            String descripcion = (String) request.get("descripcion"); // Corrección del nombre de la variable
            String profesionalId = request.containsKey("profesionalId") ? (String) request.get("profesionalId") : null;

            // Llamar al servicio para guardar el servicio (profesionalId puede ser null)
            Servicio nuevoServicio = servicioService.saveServicio(nombre, descripcion, profesionalId);

            // Responder con el servicio creado
            return ResponseEntity.ok(nuevoServicio);
        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el servicio: " + e.getMessage());
        }
    }

    // Eliminar un profesional por su ID
    @DeleteMapping("/{servicioId}")
    public ResponseEntity<?> eliminarServicio(@PathVariable String servicioId) {
        try {
            servicioService.eliminarServicio(servicioId);
            return ResponseEntity.ok("Servicio eliminado y de los profesionales.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el servicio: " + e.getMessage());
        }
    }
}
