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
        
        if (!dia.isAfter(LocalDate.now())) {
            throw new ReservaTurnoException("La fecha del turno debe ser posterior a la fecha actual.");
        }
    
        
        DayOfWeek diaSemana = dia.getDayOfWeek();
        System.out.println("Fecha solicitada: " + dia);
        System.out.println("Día de la semana (en inglés): " + diaSemana);
    
        
        Servicio servicio = servicioRepo.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + servicioId));
        ServicioDTO servicioDTO = new ServicioDTO(servicio.getNombre(), servicio.getDuracionMinutos(), servicio.getId());
    
        
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getNombre(), usuario.getApellido(), usuario.getId());
    
        Profesional profesional = null;
        ProfesionalDTO profesionalDTO;
    
        if (profesionalId == null || profesionalId.isEmpty()) {
            
            profesionalDTO = encontrarProfesionalDisponible(servicio, dia, diaSemana, horarioInicio);
            if (profesionalDTO == null) {
                throw new ReservaTurnoException("No hay profesionales disponibles para el servicio en la fecha y hora solicitadas.");
            }
    
            
            profesional = convertirDTOAProfesional(profesionalDTO); 
            LocalTime horarioFin = horarioInicio.plusMinutes(servicio.getDuracionMinutos());
    
           
            Turno nuevoTurno = new Turno(dia, horarioInicio, horarioFin, EstadoTurno.RESERVADO, usuarioDTO, profesionalDTO, servicioDTO);
            return turnoRepo.save(nuevoTurno);
        } else {
            
            profesional = profesionalRepo.findById(profesionalId)
                    .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado con ID: " + profesionalId));
            profesionalDTO = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());
        }
    
   
        LocalTime horarioFin = horarioInicio.plusMinutes(servicio.getDuracionMinutos());
    
        
        if (!verificarDisponibilidadAgenda(profesional, diaSemana, horarioInicio, horarioFin)) {
            throw new ReservaTurnoException("El profesional no atiende en el día de la semana o en el horario especificado.");
        }
    
        
        if (!verificarDisponibilidadFechaEspecifica(profesionalDTO, dia, horarioInicio, horarioFin)) {
            throw new ReservaTurnoException("El profesional ya tiene un turno en el horario solicitado en esa fecha.");
        }
    
        Turno nuevoTurno = new Turno(dia, horarioInicio, horarioFin, EstadoTurno.RESERVADO, usuarioDTO, profesionalDTO, servicioDTO);
        return turnoRepo.save(nuevoTurno);
    }
    
    


    
    
    private boolean verificarDisponibilidadAgenda(Profesional profesional, DayOfWeek diaSemana, LocalTime horarioInicio, LocalTime horarioFin) {
       
        for (ProfesionalDisponibilidad disponibilidad : profesional.getDisponibilidad()) {
    
            
            boolean diaCoincide = false;
            DayOfWeek diaSistema = disponibilidad.getDiaSemana();
            DayOfWeek diaSolicitado = diaSemana;
            
            System.out.println("Comparando día de disponibilidad (sistema): " + diaSistema + " con día solicitado: " + diaSolicitado);
    
            if (diaSistema != null && diaSistema.equals(diaSolicitado)) {
                diaCoincide = true;
            }
            System.out.println("Día coincide: " + diaCoincide);
    
            
            boolean horarioInicioCoincide = false;
            if (horarioInicio.equals(disponibilidad.getHoraInicio()) || horarioInicio.isAfter(disponibilidad.getHoraInicio())) {
                horarioInicioCoincide = true;
            }
            System.out.println("Horario de inicio coincide: " + horarioInicioCoincide);
    
            
            boolean horarioFinCoincide = false;
            if (horarioFin.equals(disponibilidad.getHoraFin()) || horarioFin.isBefore(disponibilidad.getHoraFin())) {
                horarioFinCoincide = true;
            }
            System.out.println("Horario de fin coincide: " + horarioFinCoincide);
    
            
            if (diaCoincide && horarioInicioCoincide && horarioFinCoincide) {
                System.out.println("Disponibilidad encontrada en la agenda.");
                return true;
            }
        }
    
        System.out.println("Resultado de la verificación de disponibilidad en la agenda: false");
        return false;
    }
    
    
    
    
    
    private boolean verificarDisponibilidadFechaEspecifica(ProfesionalDTO profesionalDTO, LocalDate dia, LocalTime horarioInicio, LocalTime horarioFin) {
       
    
        
        List<Turno> turnosExistentes = turnoRepo.findByProfesionalAndDia(profesionalDTO, dia);
    
        for (Turno turno : turnosExistentes) {
            LocalTime turnoInicio = turno.getFechainicio();
            LocalTime turnoFin = turno.getFechaFin();
    
            System.out.println("Turno existente en la fecha solicitada - Inicio: " + turnoInicio + ", Fin: " + turnoFin);
    
            
            boolean inicioAntesDeFinTurno = horarioInicio.isBefore(turnoFin);
            boolean finDespuesDeInicioTurno = horarioFin.isAfter(turnoInicio);
    
    
            
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
            
            Profesional profesional = profesionalRepo.findById(profesionalDTO.getId()).orElse(null);
    
            if (profesional == null || profesional.getDisponibilidad() == null) {
                continue;
            }
    
            
            System.out.println("Evaluando disponibilidad para el profesional: " + profesional.getNombre() + " " + profesional.getApellido());
            for (ProfesionalDisponibilidad disponibilidad : profesional.getDisponibilidad()) {
                System.out.println("Disponibilidad - Día: " + disponibilidad.getDiaSemana() +
                                   ", Hora Inicio: " + disponibilidad.getHoraInicio() +
                                   ", Hora Fin: " + disponibilidad.getHoraFin());
            }
    
            
            boolean disponibleEnAgenda = verificarDisponibilidadAgenda(profesional, diaSemana, horarioInicio, horarioInicio.plusMinutes(servicio.getDuracionMinutos()));
            System.out.println("TRUE O FALSE " + disponibleEnAgenda);
            if (disponibleEnAgenda) {
                
                ProfesionalDTO profesionalDTOCompleto = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());
                
                
                boolean disponibleEnFecha = verificarDisponibilidadFechaEspecifica(profesionalDTOCompleto, dia, horarioInicio, horarioInicio.plusMinutes(servicio.getDuracionMinutos()));
                System.out.println("TRUE O FALSE 2 " + disponibleEnFecha);
                if (disponibleEnFecha) {
                    return profesionalDTOCompleto; 
                }
            }
        }
    
        return null; 
    }
    
    
    
    
    
    
    
    private Profesional convertirDTOAProfesional(ProfesionalDTO profesionalDTO) {
       
        return new Profesional(profesionalDTO.getNombre(), profesionalDTO.getApellido(), profesionalDTO.getId() /* otros atributos */, null, null, null, null, null, null);
    }
    
    

    public void eliminarTurnoPorId(String turnoId) {
        turnoRepo.deleteById(turnoId);
    }

    
    

   

    public Turno cancelarTurnoPorProfesional(String turnoId, String profesionalId) {
        try {
            Turno turno = turnoRepo.findById(turnoId)
                    .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
    
            
            if (!turno.getProfesional().getId().equals(profesionalId)) {
                throw new IllegalArgumentException("Este turno no está asignado al profesional proporcionado.");
            }
    
            
            LocalDateTime ahora = LocalDateTime.now();
    
            
            LocalDate fechaTurno = turno.getDia();
            LocalDateTime fechaHoraInicioTurno = LocalDateTime.of(fechaTurno, turno.getFechainicio());
    
            
            Duration diferencia = Duration.between(ahora, fechaHoraInicioTurno);
    
            
            if (diferencia.toHours() < 24) {
                throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que comience.");
            }
    
            
            turno.setEstado(EstadoTurno.CANCELADO_POR_PROFESIONAL);
    
            // Guardar los cambios
            return turnoRepo.save(turno);
    
        } catch (IllegalArgumentException e) {
            throw new CancelarTurnoException("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new CancelarTurnoException("Error al cancelar el turno.");
        }
    }
    

    
    public ResponseEntity<?> cancelarTurnoPorUsuario(String turnoId, String usuarioId) {
        try {
            
            Turno turno = turnoRepo.findById(turnoId)
                    .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

            
            if (!turno.getUsuario().getId().equals(usuarioId)) {
                throw new IllegalArgumentException("Este turno no está asignado al usuario proporcionado.");
            }

            // Obtener la fecha y hora actual
            LocalDateTime ahora = LocalDateTime.now();
            
            LocalDate fechaTurno = turno.getDia();
            
            LocalDateTime fechaHoraInicioTurno = LocalDateTime.of(fechaTurno, turno.getFechainicio());

            
            Duration diferencia = Duration.between(ahora, fechaHoraInicioTurno);

            
            if (diferencia.toHours() < 24) {
                throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que ");
            }

            
            turno.setEstado(EstadoTurno.CANCELADO_POR_CLIENTE);
            turnoRepo.save(turno);

            return ResponseEntity.ok("El turno ha sido cancelado con éxito.");
        } catch (IllegalArgumentException e) {
            throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que ");
        } catch (Exception e) {
            throw new CancelarTurnoException("El turno no se puede cancelar si faltan menos de 24 horas para que ");
        }

    }




    
    
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

