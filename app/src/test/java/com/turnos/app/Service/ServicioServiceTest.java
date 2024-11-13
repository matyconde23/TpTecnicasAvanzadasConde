package com.turnos.app.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.turnos.app.models.Profesional;
import com.turnos.app.models.ProfesionalDTO;
import com.turnos.app.models.Servicio;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.ServicioRepo;
import com.turnos.app.service.ServicioService;

class ServicioServiceTest {

    @InjectMocks
    private ServicioService servicioService;

    @Mock
    private ServicioRepo servicioRepo;

    @Mock
    private ProfesionalRepo profesionalRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   

    @Test
    void testGetAllServicios() {
        List<Servicio> servicios = List.of(new Servicio("Servicio1", "Descripcion1", 60, null));
        when(servicioRepo.findAll()).thenReturn(servicios);

        List<Servicio> result = servicioService.getAllServicios();

        assertEquals(servicios, result);
        verify(servicioRepo).findAll();
    }

    @Test
    void testGetServicioById() {
        Servicio servicio = new Servicio("Servicio1", "Descripcion1", 60, null);
        when(servicioRepo.findById("123")).thenReturn(Optional.of(servicio));

        Optional<Servicio> result = servicioService.getServicioById("123");

        assertTrue(result.isPresent());
        assertEquals(servicio, result.get());
        verify(servicioRepo).findById("123");
    }

    @Test
    void testSaveServicio() {
        List<ProfesionalDTO> profesionales = new ArrayList<>();
        Servicio servicio = new Servicio("Servicio1", "Descripcion1", 60, profesionales);
        
        when(servicioRepo.save(any(Servicio.class))).thenReturn(servicio);

        Servicio result = servicioService.saveServicio("Servicio1", "Descripcion1", profesionales, 60);

        assertEquals("Servicio1", result.getNombre());
        assertEquals("Descripcion1", result.getDescripcion());
        assertEquals(60, result.getDuracionMinutos());
        assertEquals(profesionales, result.getProfesionales());
        verify(servicioRepo).save(any(Servicio.class));
    }

   
    @Test
    void testAgregarProfesionalaServicio() {
    Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", null);
    profesional.setId("profesional123");
    ProfesionalDTO profesionalDTO = new ProfesionalDTO("Juan", "Gomez", "profesional123");

    Servicio servicio = new Servicio("Servicio1", "Descripcion1", 60, new ArrayList<>());
    servicio.setId("servicio123");

    when(profesionalRepo.findById("profesional123")).thenReturn(Optional.of(profesional));
    when(servicioRepo.findById("servicio123")).thenReturn(Optional.of(servicio));
    when(servicioRepo.save(any(Servicio.class))).thenReturn(servicio);

    Servicio result = servicioService.agregarProfesionalaServicio("profesional123", "servicio123");

    // Verificar si el id del profesional agregado está en la lista de profesionales del servicio
    boolean profesionalEncontrado = result.getProfesionales().stream()
        .anyMatch(p -> p.getId().equals("profesional123"));

    assertTrue(profesionalEncontrado, "El profesional debería estar en el servicio");
}


    @Test
    void testAgregarProfesionalaServicioYaAgregado() {
        Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", null);
        profesional.setId("profesional123");
        ProfesionalDTO profesionalDTO = new ProfesionalDTO("Juan", "Gomez", "profesional123");

        Servicio servicio = new Servicio("Servicio1", "Descripcion1", 60, new ArrayList<>(List.of(profesionalDTO)));
        servicio.setId("servicio123");

        when(profesionalRepo.findById("profesional123")).thenReturn(Optional.of(profesional));
        when(servicioRepo.findById("servicio123")).thenReturn(Optional.of(servicio));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            servicioService.agregarProfesionalaServicio("profesional123", "servicio123")
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("El profesional ya está agregado a este servicio.", exception.getReason());
    }

    @Test
    void testGetProfesionalesPorServicio() {
        Servicio servicio = new Servicio("Servicio1", "Descripcion1", 60, new ArrayList<>());
        servicio.setId("servicio123");

        ProfesionalDTO profesionalDTO = new ProfesionalDTO("Juan", "Gomez", "profesional123");
        servicio.getProfesionales().add(profesionalDTO);

        Profesional profesional = new Profesional("Gomez", "Juan", "juan@example.com", "123456789", "Cardiología", "password", "testuser", "PROFESIONAL", null);
        profesional.setId("profesional123");

        when(servicioRepo.findById("servicio123")).thenReturn(Optional.of(servicio));
        when(profesionalRepo.findById("profesional123")).thenReturn(Optional.of(profesional));

        List<Profesional> result = servicioService.getProfesionalesPorServicio("servicio123");

        assertEquals(1, result.size());
        assertEquals(profesional, result.get(0));
    }

    @Test
    void testObtenerServiciosPorProfesional() {
        Servicio servicio = new Servicio("Servicio1", "Descripcion1", 60, new ArrayList<>());
        servicio.setId("servicio123");

        when(servicioRepo.findByProfesionales_Id("profesional123")).thenReturn(List.of(servicio));

        List<Servicio> result = servicioService.obtenerServiciosPorProfesional("profesional123");

        assertEquals(1, result.size());
        assertEquals(servicio, result.get(0));
        verify(servicioRepo).findByProfesionales_Id("profesional123");
    }
}

