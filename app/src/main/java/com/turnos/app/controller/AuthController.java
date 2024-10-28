package com.turnos.app.controller;

import com.turnos.app.models.AuthRequest;
import com.turnos.app.models.AuthResponse;
import com.turnos.app.models.Profesional;
import com.turnos.app.models.Usuario;

import com.turnos.app.service.ProfesionalService;
import com.turnos.app.service.UserDetailsServiceImpl;
import com.turnos.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;
import com.turnos.app.util.JwtUtil;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfesionalService profesionalService;
    
    
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Usuario o contraseña inválidos.");
    }

    final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
    String jwt = jwtUtil.generateToken(userDetails);

    // Primero, intenta encontrar un usuario con el nombre de usuario proporcionado
    Usuario usuario = null;
    Profesional profesional = null;

    try {
        usuario = usuarioService.findByUsername(authRequest.getUsername());
    } catch (Exception e) {
        // Aquí simplemente manejamos la excepción o la ignoramos si no se encuentra un Usuario
    }

    if (usuario != null) {
        AuthResponse authResponse = new AuthResponse(jwt, usuario.getId(), usuario.getRole());
        return ResponseEntity.ok(authResponse);
    }

    // Si no se encuentra un usuario, intenta encontrar un profesional
    try {
        profesional = profesionalService.findByUsername(authRequest.getUsername());
    } catch (Exception e) {
        // Aquí manejamos la excepción o la ignoramos si no se encuentra un Profesional
    }

    if (profesional != null) {
        AuthResponse authResponse = new AuthResponse(jwt, profesional.getId(), profesional.getRole());
        return ResponseEntity.ok(authResponse);
    }

    // Si no se encuentra ni usuario ni profesional, devolver un error
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Usuario o profesional no encontrado.");
}



   @PostMapping("/register/usuario")
public ResponseEntity<String> registerUsuario(@RequestBody Usuario usuario) {
    try {
        usuarioService.saveUsuario(usuario);
        return ResponseEntity.ok("Usuario registrado con éxito");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Error en la entrada de datos: " + e.getMessage());
    } catch (DataIntegrityViolationException e) {
        // Captura errores de integridad de datos, como duplicados en la base de datos
        return ResponseEntity.badRequest().body("Error de integridad de datos: " + e.getMessage());
    } catch (Exception e) {
        // Captura cualquier otra excepción inesperada
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado al registrar el usuario: " + e.getMessage());
    }
}


    @PostMapping("/register/profesional")
    public ResponseEntity<String> registerProfesional(@RequestBody Profesional profesional) {
        try {
            // Guardar el profesional utilizando el servicio
            profesionalService.saveProfesional(profesional);

            return ResponseEntity.ok("Profesional registrado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el profesional: " + e.getMessage());
        }
    }



}

