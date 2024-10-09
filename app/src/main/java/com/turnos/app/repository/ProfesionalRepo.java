package com.turnos.app.repository;
import com.turnos.app.models.Profesional;
import com.turnos.app.models.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfesionalRepo extends MongoRepository<Profesional, String> {

    Optional<Profesional> findByUsername(String username);

    boolean existsByUsername(String username);
}
