package com.turnos.app.Service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.turnos.app.models.Profesional;
import com.turnos.app.models.ProfesionalDisponibilidad;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.service.ProfesionalService;

class ProfesionalServiceTest {

    @InjectMocks
    private ProfesionalService profesionalService;

    @Mock
    private ProfesionalRepo profesionalRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProfesionales() {
        List<Profesional> profesionales = List.of(
            new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", null)
        );
        when(profesionalRepo.findAll()).thenReturn(profesionales);

        List<Profesional> result = profesionalService.getAllProfesionales();

        assertEquals(profesionales, result);
        verify(profesionalRepo).findAll();
    }

    @Test
    void testFindByUsername() {
        Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", null);
        when(profesionalRepo.findByUsername("testuser")).thenReturn(Optional.of(profesional));

        Profesional result = profesionalService.findByUsername("testuser");

        assertEquals(profesional, result);
        verify(profesionalRepo).findByUsername("testuser");
    }

    @Test
    void testGetProfesionalById() {
        Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", null);
        when(profesionalRepo.findById("123")).thenReturn(Optional.of(profesional));

        Optional<Profesional> result = profesionalService.getProfesionalById("123");

        assertTrue(result.isPresent());
        assertEquals(profesional, result.get());
        verify(profesionalRepo).findById("123");
    }

    @Test
    void testSaveProfesional() {
        Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", null, null);

        when(profesionalRepo.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(profesionalRepo.save(any(Profesional.class))).thenReturn(profesional);

        Profesional result = profesionalService.saveProfesional(profesional);

        assertEquals("PROFESIONAL", result.getRole());
        assertEquals("encodedPassword", result.getPassword());
        assertNotNull(result.getDisponibilidad());
        verify(profesionalRepo).existsByUsername("testuser");
        verify(profesionalRepo).save(profesional);
    }

    @Test
    void testGenerarDisponibilidadPorDefecto() {
        List<ProfesionalDisponibilidad> disponibilidad = profesionalService.generarDisponibilidadPorDefecto();

        assertEquals(5, disponibilidad.size());
        assertEquals(DayOfWeek.MONDAY, disponibilidad.get(0).getDiaSemana());
        assertEquals(LocalTime.of(9, 0), disponibilidad.get(0).getHoraInicio());
        assertEquals(LocalTime.of(16, 0), disponibilidad.get(0).getHoraFin());
    }

    @Test
    void testUpdateDisponibilidadProfesional() {
        Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", null);
        profesional.setId("123");
        List<ProfesionalDisponibilidad> nuevaDisponibilidad = new ArrayList<>();
        nuevaDisponibilidad.add(new ProfesionalDisponibilidad(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(18, 0), null));
        
        when(profesionalRepo.findById("123")).thenReturn(Optional.of(profesional));
        when(profesionalRepo.save(profesional)).thenReturn(profesional);

        Profesional result = profesionalService.updateDisponibilidadProfesional("123", nuevaDisponibilidad);

        assertEquals(nuevaDisponibilidad, result.getDisponibilidad());
        verify(profesionalRepo).findById("123");
        verify(profesionalRepo).save(profesional);
    }

    @Test
    void testObtenerDisponibilidad() {
        List<ProfesionalDisponibilidad> disponibilidad = new ArrayList<>();
        disponibilidad.add(new ProfesionalDisponibilidad(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(16, 0), null));
        Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", disponibilidad);
        
        when(profesionalRepo.findById("123")).thenReturn(Optional.of(profesional));

        List<ProfesionalDisponibilidad> result = profesionalService.obtenerDisponibilidad("123");

        assertEquals(disponibilidad, result);
        verify(profesionalRepo).findById("123");
    }

    @Test
    void testDeleteProfesional() {
        profesionalService.deleteProfesional("123");

        verify(profesionalRepo).deleteById("123");
    }
}
