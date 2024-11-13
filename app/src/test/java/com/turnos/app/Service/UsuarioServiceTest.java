package com.turnos.app.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.turnos.app.models.Usuario;
import com.turnos.app.repository.UsuarioRepo;
import com.turnos.app.service.UsuarioService;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepo usuarioRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsuarios() {
        List<Usuario> usuarios = List.of(new Usuario("Carlos", "Perez", "carlos", "password123", "USER", null, null));
        when(usuarioRepo.findAll()).thenReturn(usuarios);

        List<Usuario> result = usuarioService.getAllUsuarios();

        assertEquals(usuarios, result);
        verify(usuarioRepo).findAll();
    }

    @Test
    void testFindByUsername() {
        String username = "carlos";
        Usuario usuario = new Usuario("Carlos", "Perez", username, "password123", "USER", username, username);
        when(usuarioRepo.findByUsername(username)).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.findByUsername(username);

        assertEquals(usuario, result);
        verify(usuarioRepo).findByUsername(username);
    }

    @Test
    void testFindByUsernameNotFound() {
        String username = "notfound";
        when(usuarioRepo.findByUsername(username)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.findByUsername(username);
        });

        assertEquals("Usuario no encontrado con el nombre de usuario: " + username, exception.getMessage());
        verify(usuarioRepo).findByUsername(username);
    }

    @Test
    void testGetUsuarioById() {
        String userId = "user123";
        Usuario usuario = new Usuario("Carlos", "Perez", "carlos", "password123", "USER", userId, userId);
        when(usuarioRepo.findById(userId)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.getUsuarioById(userId);

        assertTrue(result.isPresent());
        assertEquals(usuario, result.get());
        verify(usuarioRepo).findById(userId);
    }

    @Test
    void testSaveUsuarioWithValidRoleUser() {
        Usuario usuario = new Usuario("Carlos", "Perez", "carlos", "password123", "USER", null, null);

        when(usuarioRepo.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(usuario.getPassword())).thenReturn("encodedPassword");
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.saveUsuario(usuario);

        assertEquals("USER", result.getRole());
        assertEquals("encodedPassword", result.getPassword());
        verify(usuarioRepo).existsByUsername(usuario.getUsername());
        verify(usuarioRepo).save(usuario);
    }

    @Test
    void testSaveUsuarioWithAdminRoleWhenAdminExists() {
        Usuario usuario = new Usuario("Carlos", "Perez", "carlos", "password123", "ADMIN", "hola", "ADMIN");

        when(usuarioRepo.existsByUsername(usuario.getUsername())).thenReturn(false);
        when(usuarioRepo.findByRole("ADMIN")).thenReturn(Optional.of(new Usuario(null, null, null, null, null, null, null)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.saveUsuario(usuario);
        });

        assertEquals("Ya existe un administrador en el sistema.", exception.getMessage());
        verify(usuarioRepo).existsByUsername(usuario.getUsername());
        verify(usuarioRepo).findByRole("ADMIN");
    }

    @Test 
    void testSaveUsuarioWithInvalidRole() {
        // Crear un usuario con rol "INVALID", que debería ser rechazado
        Usuario usuario = new Usuario("Carlos", "Perez", "carlos", "password123", "password", "HOLA", "INVALID");
    
        // Configurar mock para simular que el nombre de usuario no está en uso
        when(usuarioRepo.existsByUsername(usuario.getUsername())).thenReturn(false);
    
        // Ejecutar el método y verificar que lanza IllegalArgumentException con el mensaje adecuado
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.saveUsuario(usuario);
        });
    
        // Verificar que el mensaje de la excepción sea el esperado
        assertEquals("Rol inválido. Debe ser 'USER' o 'ADMIN'", exception.getMessage());
    
        // Confirmar que se verificó la existencia del nombre de usuario
        verify(usuarioRepo).existsByUsername(usuario.getUsername());
    
        // Verificar que no se intentó guardar el usuario debido a la excepción
        verify(usuarioRepo, never()).save(any(Usuario.class));
    }

    @Test
    void testSaveUsuarioWithUsernameAlreadyExists() {
        Usuario usuario = new Usuario("Carlos", "Perez", "carlos", "password123", "USER", null, null);

        when(usuarioRepo.existsByUsername(usuario.getUsername())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.saveUsuario(usuario);
        });

        assertEquals("El nombre de usuario ya está en uso.", exception.getMessage());
        verify(usuarioRepo).existsByUsername(usuario.getUsername());
    }

    @Test
    void testDeleteUsuario() {
        String userId = "user123";

        usuarioService.deleteUsuario(userId);

        verify(usuarioRepo).deleteById(userId);
    }
}