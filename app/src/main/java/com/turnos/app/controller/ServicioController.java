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


    @GetMapping
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
            // Parsear y extraer datos del request
            String nombre = (String) request.get("nombre");
            String descripcion = (String) request.get("descripcion");
            Integer duracionMinutos = (Integer) request.get("duracionMinutos"); // Duración del servicio

            // Verificar que los campos obligatorios estén presentes
            if (nombre == null || descripcion == null || duracionMinutos == null) {
                return ResponseEntity.badRequest().body("El nombre, la descripción y la duración son obligatorios.");
            }

            // Crear un nuevo servicio con la lista de profesionales vacía
            Servicio nuevoServicio = servicioService.saveServicio(nombre, descripcion, new ArrayList<>(), duracionMinutos);

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


    @PostMapping("/agregar-profesional")
    public ResponseEntity<?> agregarProfesionalaServicio(@RequestBody Map<String, Object> request) {
        String profesionalId = request.containsKey("profesionalId") ? (String) request.get("profesionalId") : null;
        String servicioId = request.containsKey("servicioId") ? (String) request.get("servicioId") : null;

        try {
            // Llamar al servicio para agregar el profesional al servicio
            Servicio actualizado = servicioService.agregarProfesionalaServicio(profesionalId, servicioId);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar el profesional al servicio: " + e.getMessage());
        }
    }
    @GetMapping("/{servicioId}/profesionales")
    public ResponseEntity<?> getProfesionalesPorServicio(@PathVariable String servicioId) {
        try {
            // Llamar al servicio para obtener la lista de profesionales
            List<Profesional> profesionales = servicioService.getProfesionalesPorServicio(servicioId);
            return ResponseEntity.ok(profesionales);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.toString());
        }
    }


   /* @DeleteMapping("/{servicioId}")
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

    */
}
