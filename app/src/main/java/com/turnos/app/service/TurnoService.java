package com.turnos.app.service;

import com.turnos.app.excepciones.CancelarTurnoException;
import com.turnos.app.excepciones.ReservaTurnoException;
import com.turnos.app.models.*;
import com.turnos.app.repository.TurnoRepo;
import com.turnos.app.repository.UsuarioRepo;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.ServicioRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;

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

    public List<Turno> obtenerTodosLosTurnos() {
        return turnoRepo.findAll();
    }

    public Turno reservarTurno(LocalDate dia, LocalTime horarioInicio, String servicioId, String profesionalId, String usuarioId) {
        // Validar que la fecha solicitada no sea hoy ni en días anteriores
        if (!dia.isAfter(LocalDate.now())) {
            throw new ReservaTurnoException("La fecha del turno debe ser posterior a la fecha actual.");
        }
    
        // Obtener el día de la semana de la fecha solicitada
        DayOfWeek diaSemana = dia.getDayOfWeek();
        System.out.println("Fecha solicitada: " + dia);
        System.out.println("Día de la semana (en inglés): " + diaSemana);
    
        // Buscar servicio
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + servicioId));
        ServicioDTO servicioDTO = new ServicioDTO(servicio.getNombre(), servicio.getDuracionMinutos(), servicio.getId());
    
        // Buscar usuario
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getNombre(), usuario.getApellido(), usuario.getId());
    
        Profesional profesional = null;
        ProfesionalDTO profesionalDTO;
    
        if (profesionalId == null || profesionalId.isEmpty()) {
            // Buscar un profesional disponible en base a la fecha y el horario
            profesionalDTO = encontrarProfesionalDisponible(servicio, dia, diaSemana, horarioInicio);
            if (profesionalDTO == null) {
                throw new ReservaTurnoException("No hay profesionales disponibles para el servicio en la fecha y hora solicitadas.");
            }
    
            // Si se encuentra un profesional disponible, convertirlo y guardar el turno directamente
            profesional = convertirDTOAProfesional(profesionalDTO); 
            LocalTime horarioFin = horarioInicio.plusMinutes(servicio.getDuracionMinutos());
    
            // Crear y guardar el turno sin verificaciones adicionales
            Turno nuevoTurno = new Turno(dia, horarioInicio, horarioFin, EstadoTurno.RESERVADO, usuarioDTO, profesionalDTO, servicioDTO);
            return turnoRepo.save(nuevoTurno);
        } else {
            // Buscar el profesional por su ID
            profesional = profesionalRepo.findById(profesionalId)
                    .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado con ID: " + profesionalId));
            profesionalDTO = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());
        }
    
        // Calcular horario de fin del turno
        LocalTime horarioFin = horarioInicio.plusMinutes(servicio.getDuracionMinutos());
    
        // Verificar disponibilidad en la agenda
        if (!verificarDisponibilidadAgenda(profesional, diaSemana, horarioInicio, horarioFin)) {
            throw new ReservaTurnoException("El profesional no atiende en el día de la semana o en el horario especificado.");
        }
    
        // Verificar disponibilidad en la fecha específica
        if (!verificarDisponibilidadFechaEspecifica(profesionalDTO, dia, horarioInicio, horarioFin)) {
            throw new ReservaTurnoException("El profesional ya tiene un turno en el horario solicitado en esa fecha.");
        }
    
        // Crear y guardar turno
        Turno nuevoTurno = new Turno(dia, horarioInicio, horarioFin, EstadoTurno.RESERVADO, usuarioDTO, profesionalDTO, servicioDTO);
        return turnoRepo.save(nuevoTurno);
    }
    
    


    
    
    private boolean verificarDisponibilidadAgenda(Profesional profesional, DayOfWeek diaSemana, LocalTime horarioInicio, LocalTime horarioFin) {
       
        for (ProfesionalDisponibilidad disponibilidad : profesional.getDisponibilidad()) {
    
            // Comprobar si el día coincide y mostrar los valores que se comparan
            boolean diaCoincide = false;
            DayOfWeek diaSistema = disponibilidad.getDiaSemana();
            DayOfWeek diaSolicitado = diaSemana;
            
            System.out.println("Comparando día de disponibilidad (sistema): " + diaSistema + " con día solicitado: " + diaSolicitado);
    
            if (diaSistema != null && diaSistema.equals(diaSolicitado)) {
                diaCoincide = true;
            }
            System.out.println("Día coincide: " + diaCoincide);
    
            // Comprobar si el horario de inicio coincide o está dentro del rango
            boolean horarioInicioCoincide = false;
            if (horarioInicio.equals(disponibilidad.getHoraInicio()) || horarioInicio.isAfter(disponibilidad.getHoraInicio())) {
                horarioInicioCoincide = true;
            }
            System.out.println("Horario de inicio coincide: " + horarioInicioCoincide);
    
            // Comprobar si el horario de fin coincide o está dentro del rango
            boolean horarioFinCoincide = false;
            if (horarioFin.equals(disponibilidad.getHoraFin()) || horarioFin.isBefore(disponibilidad.getHoraFin())) {
                horarioFinCoincide = true;
            }
            System.out.println("Horario de fin coincide: " + horarioFinCoincide);
    
            // Si todas las condiciones son verdaderas, devolver verdadero
            if (diaCoincide && horarioInicioCoincide && horarioFinCoincide) {
                System.out.println("Disponibilidad encontrada en la agenda.");
                return true;
            }
        }
    
        System.out.println("Resultado de la verificación de disponibilidad en la agenda: false");
        return false;
    }
    
    
    
    
    
    private boolean verificarDisponibilidadFechaEspecifica(ProfesionalDTO profesionalDTO, LocalDate dia, LocalTime horarioInicio, LocalTime horarioFin) {
       
    
        // Obtener todos los turnos del profesional en la fecha solicitada
        List<Turno> turnosExistentes = turnoRepo.findByProfesionalAndDia(profesionalDTO, dia);
    
        for (Turno turno : turnosExistentes) {
            LocalTime turnoInicio = turno.getFechainicio();
            LocalTime turnoFin = turno.getFechaFin();
    
            System.out.println("Turno existente en la fecha solicitada - Inicio: " + turnoInicio + ", Fin: " + turnoFin);
    
            // Verificación detallada de solapamiento de horarios
            boolean inicioAntesDeFinTurno = horarioInicio.isBefore(turnoFin);
            boolean finDespuesDeInicioTurno = horarioFin.isAfter(turnoInicio);
    
    
            // Comprobar solapamiento total
            boolean solapamiento = inicioAntesDeFinTurno && finDespuesDeInicioTurno;
            System.out.println("¿Existe solapamiento? " + solapamiento);
    
            if (solapamiento) {
                System.err.println("El profesional ya tiene un turno en este rango de horario.");
                return false;
            }
        }
    
        System.out.println("El profesional está disponible en la fecha y horario solicitado.");
        return true;
    }
    
    
    
    

    private ProfesionalDTO encontrarProfesionalDisponible(Servicio servicio, LocalDate dia, DayOfWeek diaSemana, LocalTime horarioInicio) {
        List<ProfesionalDTO> profesionalesDTO = servicio.getProfesionales(); // Obtener los profesionales desde el servicio
    
        for (ProfesionalDTO profesionalDTO : profesionalesDTO) {
            // Obtener el Profesional completo del repositorio utilizando el ID del DTO
            Profesional profesional = profesionalRepo.findById(profesionalDTO.getId()).orElse(null);
    
            // Si el profesional no existe o su disponibilidad es nula, omitirlo
            if (profesional == null || profesional.getDisponibilidad() == null) {
                continue;
            }
    
            // Imprimir la disponibilidad del profesional
            System.out.println("Evaluando disponibilidad para el profesional: " + profesional.getNombre() + " " + profesional.getApellido());
            for (ProfesionalDisponibilidad disponibilidad : profesional.getDisponibilidad()) {
                System.out.println("Disponibilidad - Día: " + disponibilidad.getDiaSemana() +
                                   ", Hora Inicio: " + disponibilidad.getHoraInicio() +
                                   ", Hora Fin: " + disponibilidad.getHoraFin());
            }
    
            // Verificar disponibilidad en la agenda (día y hora)
            boolean disponibleEnAgenda = verificarDisponibilidadAgenda(profesional, diaSemana, horarioInicio, horarioInicio.plusMinutes(servicio.getDuracionMinutos()));
            System.out.println("TRUE O FALSE " + disponibleEnAgenda);
            if (disponibleEnAgenda) {
                // Convertir a ProfesionalDTO para verificar disponibilidad en fecha específica
                ProfesionalDTO profesionalDTOCompleto = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());
                
                // Verificar si el profesional no tiene turnos en la fecha específica y horario solicitado
                boolean disponibleEnFecha = verificarDisponibilidadFechaEspecifica(profesionalDTOCompleto, dia, horarioInicio, horarioInicio.plusMinutes(servicio.getDuracionMinutos()));
                System.out.println("TRUE O FALSE 2 " + disponibleEnFecha);
                if (disponibleEnFecha) {
                    return profesionalDTOCompleto; // Retorna el primer profesional disponible
                }
            }
        }
    
        return null; // Retorna null si no encuentra ningún profesional disponible
    }
    
    
    
    
    
    
    // Método auxiliar para convertir de ProfesionalDTO a Profesional (si tienes la lógica para realizar la conversión)
    private Profesional convertirDTOAProfesional(ProfesionalDTO profesionalDTO) {
        // Implementa la conversión de DTO a Profesional según tu modelo de datos
        return new Profesional(profesionalDTO.getNombre(), profesionalDTO.getApellido(), profesionalDTO.getId() /* otros atributos */, null, null, null, null, null, null);
    }
    
    

    public void eliminarTurnoPorId(String turnoId) {
        turnoRepo.deleteById(turnoId);
    }

    
    

   

    public Turno cancelarTurnoPorProfesional(String turnoId, String profesionalId) {
        try {
            Turno turno = turnoRepo.findById(turnoId)
                    .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
    
            // Verificar que el turno esté asociado al profesional correcto
            if (!turno.getProfesional().getId().equals(profesionalId)) {
                throw new IllegalArgumentException("Este turno no está asignado al profesional proporcionado.");
            }
    
            // Obtener la fecha y hora actual
            LocalDateTime ahora = LocalDateTime.now();
    
            // Combinar el 'dia' y 'horaInicio' del turno para obtener el instante del inicio del turno
            LocalDate fechaTurno = turno.getDia();
            LocalDateTime fechaHoraInicioTurno = LocalDateTime.of(fechaTurno, turno.getFechainicio());
    
            // Calcular la diferencia entre la fecha del turno y la fecha actual
            Duration diferencia = Duration.between(ahora, fechaHoraInicioTurno);
    
            // Verificar si faltan menos de 24 horas
            if (diferencia.toHours() < 24) {
                throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que comience.");
            }
    
            // Cambiar el estado del turno a 'Cancelado por Profesional'
            turno.setEstado(EstadoTurno.CANCELADO_POR_PROFESIONAL);
    
            // Guardar los cambios
            return turnoRepo.save(turno);
    
        } catch (IllegalArgumentException e) {
            throw new CancelarTurnoException("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new CancelarTurnoException("Error al cancelar el turno.");
        }
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
            
            LocalDate fechaTurno = turno.getDia();
            // Combinar el 'dia' y 'horaInicio' del turno para obtener el instante del inicio del turno
            LocalDateTime fechaHoraInicioTurno = LocalDateTime.of(fechaTurno, turno.getFechainicio());

            // Calcular la diferencia entre la fecha del turno y la fecha actual
            Duration diferencia = Duration.between(ahora, fechaHoraInicioTurno);

            // Verificar si faltan menos de 24 horas
            if (diferencia.toHours() < 24) {
                throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que ");
            }

            // Actualizar el estado del turno a 'Cancelado'
            turno.setEstado(EstadoTurno.CANCELADO_POR_CLIENTE);
            turnoRepo.save(turno);

            return ResponseEntity.ok("El turno ha sido cancelado con éxito.");
        } catch (IllegalArgumentException e) {
            throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que ");
        } catch (Exception e) {
            throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que ");
        }

    }




    
    // Obtener un turno por ID y devolverlo con los nombres de las entidades relacionadas
    public Turno obtenerTurnoPorId(String turnoId) {
        Optional<Turno> turnoOpt = turnoRepo.findById(turnoId);
        if (!turnoOpt.isPresent()) {
            throw new IllegalArgumentException("Turno no encontrado");
        }
        return turnoOpt.get();
    }

    public List<Turno> obtenerTurnosPorProfesional(String profesionalId) {
        return turnoRepo.findByProfesional_Id(profesionalId);
    }
    public List<Turno> obtenerTurnosPorUsuario(String usuarioId) {
        return turnoRepo.findByUsuario_Id(usuarioId);
    }


    

}

