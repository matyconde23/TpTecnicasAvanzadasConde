package com.turnos.app.repository;

import com.turnos.app.models.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepo extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
    Optional<Usuario>   findByRole(String role);
}
