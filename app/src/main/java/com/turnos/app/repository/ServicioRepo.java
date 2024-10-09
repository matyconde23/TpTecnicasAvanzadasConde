package com.turnos.app.repository;

import com.turnos.app.models.Servicio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ServicioRepo extends MongoRepository<Servicio, String> {

}
