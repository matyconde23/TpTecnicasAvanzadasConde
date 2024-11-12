// UserAppointments.js
import React from 'react';
import { request } from '../services/Api';
import '../css/TurnosUsuario.css';

const TurnosUsuario = ({ turnos, userId, onCancelTurno }) => {
    const handleCancelarTurno = (turnoId) => {
        const requestData = {
            turnoId: turnoId,
            usuarioId: userId
        };

        request('POST', '/api/turno/cancelar-usuario', requestData)
            .then(() => {
                onCancelTurno(turnoId); // Llama a la función para actualizar la lista de turnos
                alert("Turno cancelado con éxito");
            })
            .catch(() => alert("Error al cancelar el turno"));
    };

    // Filtrar solo los turnos con estado "RESERVADO"
    const turnosReservados = turnos.filter(turno => turno.estado === 'RESERVADO');

    return (
        <div className="turnos-container">
            <h2>Mis Turnos Reservados</h2>
            {turnosReservados.length > 0 ? (
                turnosReservados.map((turno) => (
                    <div key={turno.id} className="turno-card">
                        <p className="turno-info">Fecha: {turno.dia}</p>
                        <p className="turno-info">Hora Inicio: {turno.fechainicio}</p>
                        <p className="turno-info">Profesional: {turno.profesional.nombre} {turno.profesional.apellido}</p>
                        <p className="turno-info">Servicio: {turno.servicio.nombre}</p>
                        <button onClick={() => handleCancelarTurno(turno.id)}>Cancelar Turno</button>
                    </div>
                ))
            ) : (
                <p>No tienes turnos reservados.</p>
            )}
        </div>
    );
};

export default TurnosUsuario;



