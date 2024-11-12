package com.turnos.app.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.turnos.app.repository.TurnoRepo;
import com.turnos.app.service.TurnoService;
import com.turnos.app.models.EstadoTurno;
import com.turnos.app.models.ProfesionalDTO;
import com.turnos.app.models.ServicioDTO;
import com.turnos.app.models.Turno;
import com.turnos.app.models.UsuarioDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@SpringBootTest
public class TurnoServiceTest {

    @Mock
    private TurnoRepo turnoRepo;

    @InjectMocks
    private TurnoService turnoService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerTurnoPorId_TurnoExiste() {
        Turno mockTurno = new Turno(
            LocalDate.now(),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            EstadoTurno.RESERVADO,
            new UsuarioDTO("usuarioId", "Nombre", "Apellido"),
            new ProfesionalDTO("profesionalId", "Nombre Profesional", "Apellido Profesional"),
            new ServicioDTO("Corte de cabello", 30, "servicioId")
        );
        mockTurno.setId("123");

        when(turnoRepo.findById("123")).thenReturn(Optional.of(mockTurno));

        Turno turno = turnoService.obtenerTurnoPorId("123");
        assertNotNull(turno);
        assertEquals("123", turno.getId());
    }

    @Test
    public void testObtenerTurnoPorId_TurnoNoExiste() {
        when(turnoRepo.findById("123")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            turnoService.obtenerTurnoPorId("123");
        });
    }
}