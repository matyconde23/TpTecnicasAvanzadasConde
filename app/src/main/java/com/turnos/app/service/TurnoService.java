package com.turnos.app.service;

import com.turnos.app.models.*;
import com.turnos.app.repository.TurnoRepo;
import com.turnos.app.repository.UsuarioRepo;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.ServicioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepo turnoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private ProfesionalRepo profesionalRepo;

    @Autowired
    private ServicioRepo servicioRepo;

    /*public List<TurnoDTO> getAllTurnos() {
        List<Turno> turnos = turnoRepo.findAll();
        List<TurnoDTO> turnoDTOs = new ArrayList<>();

        for (Turno turno : turnos) {
            Usuario usuario = turno.getUsuario() != null ? usuarioRepo.findById(turno.getUsuario().getId()).orElse(null) : null;
            Profesional profesional = turno.getProfesional() != null ? profesionalRepo.findById(turno.getProfesional().getId()).orElse(null) : null;
            Servicio servicio = turno.getServicio() != null ? servicioRepo.findById(turno.getServicio().getId()).orElse(null) : null;

            TurnoDTO dto = new TurnoDTO(
                    turno.getFechaHora(),
                    turno.getEstado(),
                    usuario != null ? usuario.getNombre() : null,
                    profesional != null ? profesional.getNombre() : null,
                    servicio != null ? servicio.getNombre() : null
            );
            turnoDTOs.add(dto);
        }

        return turnoDTOs;
    }
    /*
     */

    public Turno reservarTurno(LocalDate dia, LocalTime horarioInicio, String servicioId, String profesionalId, String usuarioId) {

        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        ServicioDTO servicioDTO = new ServicioDTO(servicio.getNombre(), servicio.getDuracionMinutos(), servicio.getId());


        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getNombre(), usuario.getApellido(), usuario.getId());


        Profesional profesional = null;
        ProfesionalDTO profesionalDTO = null;

        if (profesionalId == null || profesionalId.isEmpty()) {

            profesional = encontrarProfesionalDisponible(servicio, dia, horarioInicio);
            if (profesional == null) {
                throw new IllegalArgumentException("No hay profesionales disponibles para este servicio en la fecha y hora solicitadas.");
            }
            profesionalDTO = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());
        } else {

            profesional = profesionalRepo.findById(profesionalId)
                    .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado"));
            profesionalDTO = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());
        }


        int duracionMinutos = servicio.getDuracionMinutos();
        LocalTime horarioFin = horarioInicio.plusMinutes(duracionMinutos);


        boolean profesionalDisponible = verificarDisponibilidad(profesional, dia, horarioInicio, horarioFin);

        if (!profesionalDisponible) {
            throw new IllegalArgumentException("El profesional no está disponible en la fecha y horario solicitados.");
        }


        Turno nuevoTurno = new Turno(dia, horarioInicio, horarioFin, EstadoTurno.RESERVADO, usuarioDTO, profesionalDTO, servicioDTO);


        return turnoRepo.save(nuevoTurno);
    }


    private Profesional encontrarProfesionalDisponible(Servicio servicio, LocalDate dia, LocalTime horarioInicio) {

        List<ProfesionalDTO> profesionalesDelServicio = servicio.getProfesionales();

        for (ProfesionalDTO profesionalDTO: profesionalesDelServicio) {
            Profesional profesional = profesionalRepo.findById(profesionalDTO.getId())
                    .orElse(null);
            if (profesional != null && verificarDisponibilidad(profesional, dia, horarioInicio, horarioInicio.plusMinutes(servicio.getDuracionMinutos()))) {
                return profesional;  // Retorna el primer profesional disponible
            }
        }
        return null;
    }





    private boolean verificarDisponibilidad(Profesional profesional, LocalDate dia, LocalTime horarioInicio, LocalTime horarioFin) {
        // Buscar los turnos existentes del profesional en esa fecha
        List<Turno> turnosExistentes = turnoRepo.findByProfesionalAndDia(profesional, dia);

        for (Turno turno : turnosExistentes) {
            // Verificar si el horario del nuevo turno se solapa con los turnos existentes
            if (horarioInicio.isBefore(turno.getFechaFin()) && horarioFin.isAfter(turno.getFechainicio())) {
                return false;  // El profesional ya está ocupado en este rango de horario
            }
        }

        return true;  // El profesional está disponible
    }

   /* public List<Turno> crearTurnosDisponibles(LocalDateTime fechaInicio, LocalDateTime fechaFin, String profesionalId, String servicioId, int intervaloMinutos) {
        // Validar que el profesional exista
        Profesional profesional = profesionalRepo.findById(profesionalId)
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado"));

        // Validar que el servicio exista
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        // Verificar que el profesional esté asociado al servicio usando ProfesionalSimple (comparando por apellido)
        boolean servicioIncluyeProfesional = servicio.getProfesionales().stream()
                .anyMatch(profesionalSimple -> {
                    // Depuración: imprime los apellidos para verificar
                    System.out.println("Comparando apellidos: " + profesionalSimple.getApellido() + " y " + profesional.getApellido());

                    // Verificar que no sean null
                    if (profesionalSimple.getApellido() != null && profesional.getApellido() != null) {
                        return profesionalSimple.getApellido().trim().equalsIgnoreCase(profesional.getApellido().trim());
                    }
                    return false;
                });

        if (!servicioIncluyeProfesional) {
            throw new IllegalArgumentException("El profesional con apellido " + profesional.getApellido() + " no está asociado a este servicio.");
        }

        // Crear una lista de turnos disponibles
        List<Turno> turnosDisponibles = new ArrayList<>();
        LocalDateTime fechaActual = fechaInicio;

        // Generar turnos en intervalos de tiempo hasta la fecha de fin
        while (fechaActual.isBefore(fechaFin) || fechaActual.isEqual(fechaFin)) {
            Turno nuevoTurno = new Turno(fechaActual, EstadoTurno.DISPONIBLE, null, profesional, servicio);
            turnosDisponibles.add(nuevoTurno);
            fechaActual = fechaActual.plusMinutes(intervaloMinutos); // Crear turnos cada X minutos
        }

        // Guardar todos los turnos creados
        return turnoRepo.saveAll(turnosDisponibles);
    }

    */


   /* public Turno reservarTurno(String turnoId, String usuarioId) {
        // Obtener el turno por su ID
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

        // Verificar que el turno esté disponible
        if (turno.getEstado() != EstadoTurno.DISPONIBLE) {
            throw new IllegalArgumentException("El turno no está disponible.");
        }

        // Obtener el usuario que quiere reservar el turno
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Asignar el usuario al turno y cambiar el estado a "Reservado"
        turno.setUsuario(usuario);
        turno.setEstado(EstadoTurno.RESERVADO);

        // Guardar los cambios en la base de datos
        return turnoRepo.save(turno);
    }

    */

    public Turno cancelarTurnoPorProfesional(String turnoId, String profesionalId) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

        // Verificar que el turno esté asociado al profesional correcto
        if (!turno.getProfesional().getId().equals(profesionalId)) {
            throw new IllegalArgumentException("Este turno no está asignado al profesional proporcionado.");
        }

        // Cambiar el estado del turno a 'Cancelado'
        turno.setEstado(EstadoTurno.CANCELADO_POR_PROFESIONAL);

        // Guardar los cambios
        return turnoRepo.save(turno);
    }

    // Método para que el usuario cancele un turno
    public ResponseEntity<?> cancelarTurnoPorUsuario(String turnoId, String usuarioId) {
        try {
            // Buscar el turno por su ID
            Turno turno = turnoRepo.findById(turnoId)
                    .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

            // Verificar que el turno está asociado con el usuario
            if (!turno.getUsuario().getId().equals(usuarioId)) {
                throw new IllegalArgumentException("Este turno no está asignado al usuario proporcionado.");
            }

            // Obtener la fecha y hora actual
            LocalDateTime ahora = LocalDateTime.now();

            // Combinar el 'dia' y 'horaInicio' del turno para obtener el instante del inicio del turno
            LocalDateTime fechaHoraInicioTurno = LocalDateTime.of(turno.getDia(), turno.getFechainicio());

            // Calcular la diferencia entre la fecha del turno y la fecha actual
            Duration diferencia = Duration.between(ahora, fechaHoraInicioTurno);

            // Verificar si faltan menos de 24 horas
            if (diferencia.toHours() < 24) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se puede cancelar el turno con menos de 24 horas de antelación.");
            }

            // Actualizar el estado del turno a 'Cancelado'
            turno.setEstado(EstadoTurno.CANCELADO_POR_CLIENTE);
            turnoRepo.save(turno);

            return ResponseEntity.ok("El turno ha sido cancelado con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cancelar el turno: " + e.getMessage());
        }
    }




    /*public List<TurnoDTO> getTurnosByUsuarioId(String usuarioId) {
        // Busca todos los turnos asociados al usuario
        List<Turno> turnos = turnoRepo.findByUsuarioId(usuarioId);

        // Mapea los turnos a DTOs
        List<TurnoDTO> turnoDTOs = new ArrayList<>();
        for (Turno turno : turnos) {
            Usuario usuario = turno.getUsuario() != null ? usuarioRepo.findById(turno.getUsuario().getId()).orElse(null) : null;
            Profesional profesional = turno.getProfesional() != null ? profesionalRepo.findById(turno.getProfesional().getId()).orElse(null) : null;
            Servicio servicio = turno.getServicio() != null ? servicioRepo.findById(turno.getServicio().getId()).orElse(null) : null;
            TurnoDTO dto = new TurnoDTO(
                    turno.getFechaHora(),
                    turno.getEstado(),
                    turno.getUsuario().getNombre(),
                    profesional != null ? profesional.getNombre() : null,
                    servicio != null ? servicio.getNombre() : null
            );
            turnoDTOs.add(dto);
        }

        return turnoDTOs;
    }

     */

    // Obtener un turno por ID y devolverlo con los nombres de las entidades relacionadas
    public Turno obtenerTurnoPorId(String turnoId) {
        Optional<Turno> turnoOpt = turnoRepo.findById(turnoId);
        if (!turnoOpt.isPresent()) {
            throw new IllegalArgumentException("Turno no encontrado");
        }
        return turnoOpt.get();
    }
}

