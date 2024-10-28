// UserAppointments.js
import React from 'react';
import { request } from '../services/Api';

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

    return (
        <div>
            <h2>Mis Turnos</h2>
            {turnos.length > 0 ? (
                turnos.map((turno) => (
                    <div key={turno.id} style={{ border: '1px solid #ddd', padding: '10px', margin: '10px 0' }}>
                        <p>Fecha: {turno.dia}</p>
                        <p>Hora Inicio: {turno.fechainicio}</p>
                        <p>Profesional: {turno.profesional.nombre} {turno.profesional.apellido}</p>
                        <p>Servicio: {turno.servicio.nombre}</p>
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
