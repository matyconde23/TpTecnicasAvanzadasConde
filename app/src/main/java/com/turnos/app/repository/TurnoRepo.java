package com.turnos.app.repository;

import com.turnos.app.models.Turno;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TurnoRepo extends MongoRepository<Turno, String> {

    List<Turno> findByUsuarioId(String usuarioId);
}
