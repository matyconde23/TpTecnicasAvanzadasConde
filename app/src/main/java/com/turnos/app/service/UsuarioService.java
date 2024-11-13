package com.turnos.app.service;


import com.turnos.app.models.Usuario;
import com.turnos.app.repository.UsuarioRepo;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service

public class UsuarioService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<Usuario> getAllUsuarios() {
        return usuarioRepo.findAll();
    }
    public Usuario findByUsername(String username) {
        return usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el nombre de usuario: " + username));
    }

    
    public Optional<Usuario> getUsuarioById(String id) {
        return usuarioRepo.findById(id);
    }


    public Usuario saveUsuario(Usuario usuario) {
    
        // Verificar si el nombre de usuario ya existe
        if (usuarioRepo.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
    
        // Validar el rol del usuario: solo se permiten "USER" o "ADMIN"
        String role = (usuario.getRole() != null) ? usuario.getRole().toUpperCase() : "USER";
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new IllegalArgumentException("Rol inválido. Debe ser 'USER' o 'ADMIN'");
        }
    
        // Verificar si ya existe un administrador en el sistema
        if (role.equals("ADMIN") && usuarioRepo.findByRole("ADMIN").isPresent()) {
            throw new IllegalArgumentException("Ya existe un administrador en el sistema.");
        }
    
        // Configuración final del rol y encriptación de la contraseña
        usuario.setRole(role);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    
        // Guardar el usuario en el repositorio
        return usuarioRepo.save(usuario);
    }
    


    public void deleteUsuario(String id) {
        usuarioRepo.deleteById(id);
    }
}
