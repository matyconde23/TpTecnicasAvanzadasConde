package com.turnos.app.repository;

import com.turnos.app.models.Profesional;
import com.turnos.app.models.Turno;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface TurnoRepo extends MongoRepository<Turno, String> {

    List<Turno> findByUsuarioId(String usuarioId);
    List<Turno> findByProfesionalAndDia(Profesional profesional, LocalDate dia);
    List<Turno> findByProfesional_Id(String profesionalId);
    List<Turno> findByUsuario_Id(String usuarioId);
}
