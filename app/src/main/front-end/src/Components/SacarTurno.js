import React, { useState, useEffect } from 'react';
import { request } from '../services/Api';
import '../css/SacarTurno.css';

const SacarTurno = ({ userId, servicios, onTurnoConfirmado }) => {
    const [dia, setDia] = useState('');
    const [horarioInicio, setHorarioInicio] = useState('');
    const [servicioId, setServicioId] = useState('');
    const [profesionales, setProfesionales] = useState([]);
    const [profesionalId, setProfesionalId] = useState('');
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false); // Indicador de carga

    useEffect(() => {
        if (servicioId) {
            setIsLoading(true);
            request('GET', `/api/servicio/${servicioId}/profesionales`, null)
                .then(response => {
                    setProfesionales(response.data);
                    setError(null);
                })
                .catch(() => setError('Error al obtener los profesionales para el servicio seleccionado.'))
                .finally(() => setIsLoading(false));
        } else {
            setProfesionales([]);
        }
    }, [servicioId]);

    const handleReservarTurno = () => {
        if (!dia || !horarioInicio || !servicioId || !userId) {
            setError('Por favor, completa todos los campos obligatorios.');
            return;
        }

        setIsLoading(true);
        const requestData = {
            dia,
            horarioInicio,
            servicioId,
            profesionalId: profesionalId || null,
            usuarioId: userId,
        };

        request('POST', '/api/turno/sacar-turno', requestData)
            .then(response => {
                onTurnoConfirmado(response.data);
                setError(null);
            })
            .catch(error => {
                const errorMessage = error.response?.data?.message || 'Error al reservar el turno';
                setError(errorMessage);
            })
            .finally(() => setIsLoading(false));
    };

    const handleInputChange = (setter) => (event) => {
        setter(event.target.value);
        setError(null); // Restablecer el error al cambiar el campo
    };

    return (
        <div className="sacar-turno-container">
            <h2>Reservar Turno</h2>
            {error && <p>{error}</p>}
            {isLoading && <p>Cargando...</p>}

            <label>Fecha: </label>
            <input type="date" value={dia} onChange={handleInputChange(setDia)} required />

            <label>Hora de inicio: </label>
            <input type="time" value={horarioInicio} onChange={handleInputChange(setHorarioInicio)} required />

            <label>Servicio: </label>
            <select value={servicioId} onChange={handleInputChange(setServicioId)} required>
                <option value="">Selecciona un servicio</option>
                {servicios.map((servicio) => (
                    <option key={servicio.id} value={servicio.id}>{servicio.nombre}</option>
                ))}
            </select>

            <label>Profesional (opcional): </label>
            <select value={profesionalId} onChange={handleInputChange(setProfesionalId)}>
                <option value="">Selecciona un profesional</option>
                {profesionales.map((profesional) => (
                    <option key={profesional.id} value={profesional.id}>{profesional.nombre} {profesional.apellido}</option>
                ))}
            </select>

            <button onClick={handleReservarTurno} disabled={isLoading}>Sacar Turno</button>
        </div>
    );
};

export default SacarTurno;
