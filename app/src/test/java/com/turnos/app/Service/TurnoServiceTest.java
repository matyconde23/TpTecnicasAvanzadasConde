package com.turnos.app.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.turnos.app.excepciones.CancelarTurnoException;
import com.turnos.app.excepciones.ReservaTurnoException;
import com.turnos.app.models.*;
import com.turnos.app.repository.*;
import com.turnos.app.service.TurnoService;

class TurnoServiceTest {

    @InjectMocks
    private TurnoService turnoService;

    @Mock
    private TurnoRepo turnoRepo;

    @Mock
    private UsuarioRepo usuarioRepo;

    @Mock
    private ProfesionalRepo profesionalRepo;

    @Mock
    private ServicioRepo servicioRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerTodosLosTurnos() {
        List<Turno> turnos = List.of(new Turno(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), EstadoTurno.RESERVADO, null, null, null));
        when(turnoRepo.findAll()).thenReturn(turnos);

        List<Turno> result = turnoService.obtenerTodosLosTurnos();

        assertEquals(turnos, result);
        verify(turnoRepo).findAll();
    }

    @Test
void testReservarTurnoExitoso() {
    LocalDate dia = LocalDate.now().plusDays(1); // Fecha futura
    DayOfWeek diaSemana = dia.getDayOfWeek(); // Obtener el día de la semana para configurar la disponibilidad
    LocalTime horarioInicio = LocalTime.of(10, 0);
    String servicioId = "servicio123";
    String profesionalId = "profesional123";
    String usuarioId = "usuario123";

    // Creación de entidades y DTOs con datos específicos
    Servicio servicio = new Servicio("Consulta General", "Descripción del servicio", 60, null);
    Usuario usuario = new Usuario("Carlos", "Perez", "carlos.perez@example.com", "123456789", usuarioId, null, null);
    Profesional profesional = new Profesional("Juan", "Gomez", "juan.gomez@example.com", "987654321", "Cardiología", "password123", "juangomez", "PROFESIONAL", null);

    // Ajuste de la disponibilidad para coincidir con el día y hora solicitados en el test
    profesional.setDisponibilidad(Collections.singletonList(
        new ProfesionalDisponibilidad(diaSemana, LocalTime.of(9, 0), LocalTime.of(16, 0), null)
    ));

    // Creación de DTOs
    UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getNombre(), usuario.getApellido(), usuario.getId());
    ProfesionalDTO profesionalDTO = new ProfesionalDTO(profesional.getNombre(), profesional.getApellido(), profesional.getId());
    ServicioDTO servicioDTO = new ServicioDTO(servicio.getNombre(), servicio.getDuracionMinutos(), servicio.getId());

    when(servicioRepo.findById(servicioId)).thenReturn(Optional.of(servicio));
    when(usuarioRepo.findById(usuarioId)).thenReturn(Optional.of(usuario));
    when(profesionalRepo.findById(profesionalId)).thenReturn(Optional.of(profesional));
    when(turnoRepo.save(any(Turno.class))).thenReturn(new Turno(dia, horarioInicio, horarioInicio.plusMinutes(servicio.getDuracionMinutos()), EstadoTurno.RESERVADO, usuarioDTO, profesionalDTO, servicioDTO));

    Turno result = turnoService.reservarTurno(dia, horarioInicio, servicioId, profesionalId, usuarioId);

    assertNotNull(result);
    verify(turnoRepo).save(any(Turno.class));
}

    @Test
    void testReservarTurnoFechaPasada() {
        LocalDate dia = LocalDate.now().minusDays(1);
        LocalTime horarioInicio = LocalTime.of(10, 0);
        String servicioId = "servicio123";
        String profesionalId = "profesional123";
        String usuarioId = "usuario123";

        ReservaTurnoException exception = assertThrows(ReservaTurnoException.class, () ->
            turnoService.reservarTurno(dia, horarioInicio, servicioId, profesionalId, usuarioId)
        );

        assertEquals("La fecha del turno debe ser posterior a la fecha actual.", exception.getMessage());
    }

    @Test
    void testCancelarTurnoPorProfesional() {
        String turnoId = "turno123";
        String profesionalId = "profesional123";

        // Creación de DTO para el profesional
        ProfesionalDTO profesionalDTO = new ProfesionalDTO("Juan", "Gomez", profesionalId);
        Turno turno = new Turno(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(11, 0), EstadoTurno.RESERVADO, null, profesionalDTO, null);

        when(turnoRepo.findById(turnoId)).thenReturn(Optional.of(turno));
        when(turnoRepo.save(any(Turno.class))).thenReturn(turno);

        Turno result = turnoService.cancelarTurnoPorProfesional(turnoId, profesionalId);

        assertEquals(EstadoTurno.CANCELADO_POR_PROFESIONAL, result.getEstado());
        verify(turnoRepo).save(turno);
    }

    @Test
    void testCancelarTurnoPorUsuario() {
        String turnoId = "turno123";
        String usuarioId = "usuario123";

        // Creación de DTO para el usuario
        UsuarioDTO usuarioDTO = new UsuarioDTO("Carlos", "Perez", usuarioId);
        Turno turno = new Turno(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(11, 0), EstadoTurno.RESERVADO, usuarioDTO, null, null);

        when(turnoRepo.findById(turnoId)).thenReturn(Optional.of(turno));
        when(turnoRepo.save(any(Turno.class))).thenReturn(turno);

        ResponseEntity<?> response = turnoService.cancelarTurnoPorUsuario(turnoId, usuarioId);

        assertEquals("El turno ha sido cancelado con éxito.", response.getBody());
        verify(turnoRepo).save(turno);
    }

    @Test
    void testObtenerTurnoPorId() {
        Turno turno = new Turno(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), EstadoTurno.RESERVADO, null, null, null);
        when(turnoRepo.findById("turno123")).thenReturn(Optional.of(turno));

        Turno result = turnoService.obtenerTurnoPorId("turno123");

        assertEquals(turno, result);
        verify(turnoRepo).findById("turno123");
    }

    @Test
    void testObtenerTurnosPorProfesional() {
        String profesionalId = "profesional123";

        // Creación de DTO para el profesional
        ProfesionalDTO profesionalDTO = new ProfesionalDTO("Juan", "Gomez", profesionalId);
        List<Turno> turnos = List.of(new Turno(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), EstadoTurno.RESERVADO, null, profesionalDTO, null));

        when(turnoRepo.findByProfesional_Id(profesionalId)).thenReturn(turnos);

        List<Turno> result = turnoService.obtenerTurnosPorProfesional(profesionalId);

        assertEquals(turnos, result);
        verify(turnoRepo).findByProfesional_Id(profesionalId);
    }

    @Test
    void testObtenerTurnosPorUsuario() {
        String usuarioId = "usuario123";

        // Creación de DTO para el usuario
        UsuarioDTO usuarioDTO = new UsuarioDTO("Carlos", "Perez", usuarioId);
        List<Turno> turnos = List.of(new Turno(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), EstadoTurno.RESERVADO, usuarioDTO, null, null));

        when(turnoRepo.findByUsuario_Id(usuarioId)).thenReturn(turnos);

        List<Turno> result = turnoService.obtenerTurnosPorUsuario(usuarioId);

        assertEquals(turnos, result);
        verify(turnoRepo).findByUsuario_Id(usuarioId);
    }
}
