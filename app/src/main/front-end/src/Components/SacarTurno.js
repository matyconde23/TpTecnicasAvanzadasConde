// BookAppointment.js
import React, { useState, useEffect } from 'react';
import { request } from '../services/Api';

const SacarTurno = ({ userId, servicios, onTurnoConfirmado }) => {
    const [dia, setDia] = useState('');
    const [horarioInicio, setHorarioInicio] = useState('');
    const [servicioId, setServicioId] = useState('');
    const [profesionales, setProfesionales] = useState([]);
    const [profesionalId, setProfesionalId] = useState('');
    const [error, setError] = useState(null);

    useEffect(() => {
        if (servicioId) {
            // Obtener los profesionales para el servicio seleccionado
            request('GET', `/api/servicio/${servicioId}/profesionales`, null)
                .then(response => setProfesionales(response.data))
                .catch(() => setError('Error fetching professionals for selected service'));
        }
    }, [servicioId]);

    const handleReservarTurno = () => {
        if (!dia || !horarioInicio || !servicioId || !userId) {
            setError('Por favor, completa todos los campos obligatorios.');
            return;
        }

        const requestData = {
            dia,
            horarioInicio,
            servicioId,
            profesionalId: profesionalId || null,
            usuarioId: userId,
        };

        request('POST', '/api/turno/sacar-turno', requestData)
            .then(response => onTurnoConfirmado(response.data))
            .catch(() => setError('Error al reservar el turno'));
    };

    return (
        <div>
            <h2>Reservar Turno</h2>
            {error && <p>{error}</p>}
            <label>Fecha: </label>
            <input type="date" value={dia} onChange={(e) => setDia(e.target.value)} required />

            <label>Hora de inicio: </label>
            <input type="time" value={horarioInicio} onChange={(e) => setHorarioInicio(e.target.value)} required />

            <label>Servicio: </label>
            <select value={servicioId} onChange={(e) => setServicioId(e.target.value)} required>
                <option value="">Selecciona un servicio</option>
                {servicios.map((servicio) => (
                    <option key={servicio.id} value={servicio.id}>{servicio.nombre}</option>
                ))}
            </select>

            <label>Profesional (opcional): </label>
            <select value={profesionalId} onChange={(e) => setProfesionalId(e.target.value)}>
                <option value="">Selecciona un profesional</option>
                {profesionales.map((profesional) => (
                    <option key={profesional.id} value={profesional.id}>{profesional.nombre} {profesional.apellido}</option>
                ))}
            </select>

            <button onClick={handleReservarTurno}>Sacar Turno</button>
        </div>
    );
};

export default SacarTurno;
