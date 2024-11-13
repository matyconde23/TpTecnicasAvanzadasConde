package com.turnos.app.controller;

import com.turnos.app.excepciones.ReservaTurnoException;
import com.turnos.app.excepciones.CancelarTurnoException;
import com.turnos.app.models.*;
import com.turnos.app.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
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
    
        } catch (ReservaTurnoException e) {
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al reservar el turno: " + e.getMessage());
    
        } catch (IllegalArgumentException e) {
            
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error inesperado al reservar el turno: " + e.getMessage());
        }
    }

    @PostMapping("/cancelar-profesional")
    public ResponseEntity<?> cancelarTurnoPorProfesional(@RequestBody Map<String, String> request) {
        try {
            
            if (!request.containsKey("turnoId") || !request.containsKey("profesionalId")) {
                return ResponseEntity.badRequest().body("Parámetros 'turnoId' y 'profesionalId' son requeridos.");
            }

           
            String turnoId = request.get("turnoId");
            String profesionalId = request.get("profesionalId");

        
            Turno turnoCancelado = turnoService.cancelarTurnoPorProfesional(turnoId, profesionalId);

            
            return ResponseEntity.ok(turnoCancelado);
        } catch (IllegalArgumentException e) {
        
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al cancelar el turno: " + e.getMessage());
    }
    }

    
    @PostMapping("/cancelar-usuario")
    public ResponseEntity<?> cancelarTurnoPorUsuario(@RequestBody Map<String, String> request) throws ReservaTurnoException {
        try {
            
            if (!request.containsKey("turnoId") || !request.containsKey("usuarioId")) {
                return ResponseEntity.badRequest().body("Parámetros 'turnoId' y 'usuarioId' son requeridos.");
            }

           
            String turnoId = request.get("turnoId");
            String usuarioId = request.get("usuarioId");

           
            ResponseEntity<?> turnoCancelado = turnoService.cancelarTurnoPorUsuario(turnoId, usuarioId);

            
            return ResponseEntity.ok(turnoCancelado);
        } catch (IllegalArgumentException e) {
            
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error al cancelar el turno: " + e.getMessage());
        }

    }
    

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

    @DeleteMapping("/eliminar/{turnoId}")
    public ResponseEntity<?> eliminarTurno(@PathVariable String turnoId) {
        try {
            turnoService.eliminarTurnoPorId(turnoId);
            return ResponseEntity.ok("Turno eliminado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el turno: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Turno>> obtenerTodosLosTurnos() {
        List<Turno> turnos = turnoService.obtenerTodosLosTurnos();
        return ResponseEntity.ok(turnos);
    }


}


