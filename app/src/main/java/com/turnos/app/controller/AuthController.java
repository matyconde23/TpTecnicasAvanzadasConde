package com.turnos.app.controller;

import com.turnos.app.models.AuthRequest;
import com.turnos.app.models.AuthResponse;
import com.turnos.app.models.Profesional;
import com.turnos.app.models.Usuario;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.UsuarioRepo;
import com.turnos.app.service.ProfesionalService;
import com.turnos.app.service.UserDetailsServiceImpl;
import com.turnos.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.turnos.app.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Usuario o contraseña inválidos.");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register/usuario")
    public ResponseEntity<String> registerUsuario(@RequestBody Usuario usuario) {
        try {
            usuarioService.saveUsuario(usuario);
            return ResponseEntity.ok("Usuario registrado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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

