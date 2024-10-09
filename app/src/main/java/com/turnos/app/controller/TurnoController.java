package com.turnos.app.controller;

import com.turnos.app.models.*;
import com.turnos.app.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;
    @GetMapping
    public List<TurnoDTO> getAllTurnos() {
        return turnoService.getAllTurnos();
    }
    // Endpoint para crear un turno
    @PostMapping("/crear")
    public ResponseEntity<?> crearTurno(@RequestBody Map<String, Object> request) {
        try {
            // Validar que 'fechaHora' esté presente en el cuerpo de la solicitud
            if (!request.containsKey("fechaHora")) {
                return ResponseEntity.badRequest().body("Parámetro 'fechaHora' es requerido.");
            }

            // Parsear y extraer datos del request
            LocalDateTime fechaHora = LocalDateTime.parse((String) request.get("fechaHora"));
            String estado = (String) request.get("estado");
            String clienteId = (String) request.get("clienteId");
            String profesionalId = (String) request.get("profesionalId");
            String servicioId = (String) request.get("servicioId");

            // Llamar al servicio para guardar el turno
            Turno nuevoTurno = turnoService.saveTurno(fechaHora, EstadoTurno.valueOf(estado), clienteId, profesionalId, servicioId);

            // Responder con el turno creado
            return ResponseEntity.ok(nuevoTurno);
        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas
            return ResponseEntity.badRequest().body("error: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el turno: " + e.getMessage());
        }
    }
    @PostMapping("/crear-disponibles")
    public ResponseEntity<?> crearTurnosDisponibles(@RequestBody Map<String, Object> request) {
        try {
            // Validar que el 'profesionalId' y las fechas estén presentes en el request
            if (!request.containsKey("profesionalId") || !request.containsKey("fechaInicio") || !request.containsKey("fechaFin")) {
                return ResponseEntity.badRequest().body("Parámetros 'profesionalId', 'fechaInicio' y 'fechaFin' son requeridos.");
            }

            // Parsear y extraer datos del request
            LocalDateTime fechaInicio = LocalDateTime.parse((String) request.get("fechaInicio"));
            LocalDateTime fechaFin = LocalDateTime.parse((String) request.get("fechaFin"));
            String profesionalId = (String) request.get("profesionalId");
            String servicioId = request.containsKey("servicioId") ? (String) request.get("servicioId") : null;
            int intervaloMinutos = request.containsKey("intervaloMinutos") ? (int) request.get("intervaloMinutos") : 30; // Default a 30 minutos

            // Llamar al servicio para crear los turnos
            List<Turno> turnosDisponibles = turnoService.crearTurnosDisponibles(fechaInicio, fechaFin, profesionalId, servicioId, intervaloMinutos);

            // Responder con los turnos creados
            return ResponseEntity.ok(turnosDisponibles);
        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear turnos disponibles: " + e.getMessage());
        }
    }

    @PostMapping("/reservar")
    public ResponseEntity<?> reservarTurno(@RequestBody Map<String, String> request) {
        try {
            // Validar que 'turnoId' y 'usuarioId' estén presentes en el cuerpo de la solicitud
            if (!request.containsKey("turnoId") || !request.containsKey("usuarioId")) {
                return ResponseEntity.badRequest().body("Parámetros 'turnoId' y 'usuarioId' son requeridos.");
            }

            // Extraer los datos del request
            String turnoId = request.get("turnoId");
            String usuarioId = request.get("usuarioId");

            // Llamar al servicio para reservar el turno
            Turno turnoReservado = turnoService.reservarTurno(turnoId, usuarioId);

            // Responder con el turno reservado
            return ResponseEntity.ok(turnoReservado);
        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al reservar el turno: " + e.getMessage());
        }
    }
    @PostMapping("/cancelar-profesional")
    public ResponseEntity<?> cancelarTurnoPorProfesional(@RequestBody Map<String, String> request) {
        try {
            // Validar que 'turnoId' y 'profesionalId' estén presentes
            if (!request.containsKey("turnoId") || !request.containsKey("profesionalId")) {
                return ResponseEntity.badRequest().body("Parámetros 'turnoId' y 'profesionalId' son requeridos.");
            }

            // Extraer los datos del request
            String turnoId = request.get("turnoId");
            String profesionalId = request.get("profesionalId");

            // Llamar al servicio para cancelar el turno
            Turno turnoCancelado = turnoService.cancelarTurnoPorProfesional(turnoId, profesionalId);

            // Responder con el turno cancelado
            return ResponseEntity.ok(turnoCancelado);
        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cancelar el turno: " + e.getMessage());
        }
    }

    // Endpoint para que el usuario cancele un turno
    @PostMapping("/cancelar-usuario")
    public ResponseEntity<?> cancelarTurnoPorUsuario(@RequestBody Map<String, String> request) {
        try {
            // Validar que 'turnoId' y 'usuarioId' estén presentes
            if (!request.containsKey("turnoId") || !request.containsKey("usuarioId")) {
                return ResponseEntity.badRequest().body("Parámetros 'turnoId' y 'usuarioId' son requeridos.");
            }

            // Extraer los datos del request
            String turnoId = request.get("turnoId");
            String usuarioId = request.get("usuarioId");

            // Llamar al servicio para cancelar el turno
            ResponseEntity<?> turnoCancelado = turnoService.cancelarTurnoPorUsuario(turnoId, usuarioId);

            // Responder con el turno cancelado
            return ResponseEntity.ok(turnoCancelado);
        } catch (IllegalArgumentException e) {
            // Manejo de excepciones específicas
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otra excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cancelar el turno: " + e.getMessage());
        }
    }
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TurnoDTO>> getTurnosByUsuarioId(@PathVariable String usuarioId) {
        try {
            List<TurnoDTO> turnos = turnoService.getTurnosByUsuarioId(usuarioId);
            return ResponseEntity.ok(turnos);
        } catch (Exception e) {
            System.out.print("error" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint para obtener un turno por ID
    @GetMapping("/{id}")
    public Turno obtenerTurno(@PathVariable String id) {
        return turnoService.obtenerTurnoPorId(id);
    }
}

