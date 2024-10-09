package com.turnos.app.controller;

import com.turnos.app.models.AuthRequest;
import com.turnos.app.models.AuthResponse;
import com.turnos.app.models.Profesional;
import com.turnos.app.models.Usuario;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.UsuarioRepo;
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
    private UsuarioRepo usuarioRepository;

    @Autowired
    private ProfesionalRepo profesionalRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Usuario o contraseña inválidos.");
        }

        // Si la autenticación es exitosa
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register/usuario")
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
        // Si el rol no se proporciona, establecerlo automáticamente como "USER"
        String role = (usuario.getRole() != null) ? usuario.getRole().toUpperCase() : "USER";

        // Verificar si el nombre de usuario ya existe
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso");
        }

        // Verificar el rol que se envía en la solicitud
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            return ResponseEntity.badRequest().body("Rol inválido. Debe ser 'USER' o 'ADMIN'");
        }

        // Si se solicita registrar un ADMIN, verificar si ya existe uno
        if (role.equals("ADMIN")) {
            boolean adminExists = userDetailsService.adminExists();
            if (adminExists) {
                return ResponseEntity.badRequest().body("Ya existe un administrador en el sistema");
            }
        }

        // Codificar la contraseña y guardar el usuario
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole(role);  // Asignar el rol que se definió o el valor por defecto "USER"
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario registrado con éxito");
    }
    @PostMapping("/register/profesional")
    public String registerProfesional(@RequestBody Profesional profesional) {
        profesional.setPassword(passwordEncoder.encode(profesional.getPassword()));
        profesionalRepo.save(profesional);
        return "profesional registrado con éxito";
    }

}
