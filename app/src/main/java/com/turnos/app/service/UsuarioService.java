package com.turnos.app.service;


import com.turnos.app.models.Usuario;
import com.turnos.app.repository.UsuarioRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener todos los usuarios
    public List<Usuario> getAllUsuarios() {
        return usuarioRepo.findAll();
    }

    // Obtener un usuario por su ID
    public Optional<Usuario> getUsuarioById(String id) {
        return usuarioRepo.findById(id);
    }

    // Crear o actualizar un usuario
    public Usuario saveUsuario(Usuario usuario) {
        // Verifica si el nombre de usuario ya existe
        if (usuarioRepo.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        // Codifica la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guarda el usuario
        return usuarioRepo.save(usuario);

    }
    public UsuarioService(UsuarioRepo usuarioRepo, @Lazy PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }



    // Eliminar un usuario por su ID
    public void deleteUsuario(String id) {
        usuarioRepo.deleteById(id);
    }
}
