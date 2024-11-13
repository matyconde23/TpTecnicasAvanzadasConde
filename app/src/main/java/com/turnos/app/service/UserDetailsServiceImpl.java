package com.turnos.app.service;

import com.turnos.app.models.Profesional;
import com.turnos.app.models.Usuario;
import com.turnos.app.repository.ProfesionalRepo;
import com.turnos.app.repository.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.boot.web.server.Ssl.ClientAuth.map;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepo usuarioRepository;

    @Autowired
    private final ProfesionalRepo profesionalRepo;

    public UserDetailsServiceImpl(UsuarioRepo usuarioRepository, ProfesionalRepo profesionalRepo) {
        this.usuarioRepository = usuarioRepository;
        this.profesionalRepo = profesionalRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent()) {
            
            String role = usuario.get().getRole().toUpperCase();  

            if (!role.equals("ADMIN") && !role.equals("USER")) {
                throw new IllegalArgumentException("Rol no válido: " + role);
            }

            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            return new org.springframework.security.core.userdetails.User(
                    usuario.get().getUsername(),
                    usuario.get().getPassword(),
                    authorities
            );
        }

        
        Optional<Profesional> profesional = profesionalRepo.findByUsername(username);
        if (profesional.isPresent()) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("PROFESIONAL")); // Asignar rol "PROFESIONAL"
            return new org.springframework.security.core.userdetails.User(
                    profesional.get().getUsername(),
                    profesional.get().getPassword(),
                    authorities
            );
        }

        throw new UsernameNotFoundException("Usuario o profesional no encontrado");
    }

    
    public boolean adminExists() {
        return usuarioRepository.findByRole("ADMIN").isPresent();
    }
}

