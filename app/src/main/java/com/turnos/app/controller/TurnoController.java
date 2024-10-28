package com.turnos.app.controller;

import com.turnos.app.models.*;
import com.turnos.app.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turno")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;
    
    /*@GetMapping
    public List<TurnoDTO> getAllTurnos() {
        return turnoService.getAllTurnos();
    }

     */


    // Endpoint para crear un turno
    @PostMapping("/sacar-turno")
    public ResponseEntity<?> crearTurno(@RequestBody Map<String, Object> request) {
        try {

            if (!request.containsKey("dia") || !request.containsKey("horarioInicio") ||
                    !request.containsKey("servicioId") || !request.containsKey("usuarioId")) {
                return ResponseEntity.badRequest().body("Parámetros 'dia', 'horarioInicio', 'servicioId', y 'usuarioId' son requeridos.");
            }


            LocalDate dia = LocalDate.parse((String) request.get("dia"));
            LocalTime horarioInicio = LocalTime.parse((String) request.get("horarioInicio"));
            String profesionalId = request.containsKey("profesionalId") ? (String) request.get("profesionalId") : null;
            String servicioId = (String) request.get("servicioId");
            String usuarioId = (String) request.get("usuarioId");


            Turno nuevoTurno = turnoService.reservarTurno(dia, horarioInicio, servicioId, profesionalId, usuarioId);


            return ResponseEntity.ok(nuevoTurno);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.toString());
        }
    }





    /*@PostMapping("/crear-disponibles")
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

     */



   /* @PostMapping("/reservar")
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

    */
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
    /*@GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TurnoDTO>> getTurnosByUsuarioId(@PathVariable String usuarioId) {
        try {
            List<TurnoDTO> turnos = turnoService.getTurnosByUsuarioId(usuarioId);
            return ResponseEntity.ok(turnos);
        } catch (Exception e) {
            System.out.print("error" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

     */

    // Endpoint para obtener un turno por ID
    @GetMapping("/{id}")
    public Turno obtenerTurno(@PathVariable String id) {
        return turnoService.obtenerTurnoPorId(id);
    }

    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<Turno>> obtenerTurnosPorProfesional(@PathVariable String profesionalId) {
        List<Turno> turnos = turnoService.obtenerTurnosPorProfesional(profesionalId);
        return ResponseEntity.ok(turnos);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Turno>> obtenerTurnosPorUsuario(@PathVariable String usuarioId) {
    List<Turno> turnos = turnoService.obtenerTurnosPorUsuario(usuarioId);
    return ResponseEntity.ok(turnos);
}
}

