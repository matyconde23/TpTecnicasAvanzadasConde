// TurnosProfesional.js
import React, { useState, useEffect } from 'react';
import { request } from '../services/Api';
import '../css/TurnosProfesional.css';

const TurnosProfesional = ({ profesionalId }) => {
    const [turnos, setTurnos] = useState([]);
    const [error, setError] = useState('');
    const [filtroEstado, setFiltroEstado] = useState(''); // Estado para el filtro de estado

    useEffect(() => {
        const fetchTurnosPorProfesional = async () => {
            try {
                const response = await request('get', `/api/turno/profesional/${profesionalId}`, null);
                setTurnos(response.data);
            } catch (error) {
                setError('Error al obtener los turnos del profesional');
                console.error('Error fetching turnos:', error);
            }
        };

        if (profesionalId) {
            fetchTurnosPorProfesional();
        } else {
            setError('No se encontró el profesionalId');
        }
    }, [profesionalId]);

    // Función para cancelar un turno y actualizar su estado en la lista
    const cancelarTurno = async (turnoId) => {
        try {
            const requestBody = { turnoId, profesionalId };
            await request('post', `/api/turno/cancelar-profesional`, requestBody);

            setTurnos(turnos.map(turno => 
                turno.id === turnoId ? { ...turno, estado: 'CANCELADO_POR_PROFESIONAL' } : turno
            ));

            alert('Turno cancelado con éxito.');
        } catch (error) {
            console.error('Error cancelando el turno:', error);
            alert('Error al cancelar el turno.');
        }
    };

    // Filtrar turnos según el estado seleccionado
    const turnosFiltrados = filtroEstado 
        ? turnos.filter(turno => turno.estado === filtroEstado)
        : turnos;

    // Manejo de errores
    if (error) {
        return <p className="error-message">{error}</p>;
    }

    return (
        <div className="turnos-container">
            {/* Selector de filtro para los estados de turnos */}
            <label htmlFor="filtro-estado">Filtrar por estado:</label>
            <select
                id="filtro-estado"
                value={filtroEstado}
                onChange={(e) => setFiltroEstado(e.target.value)}
                className="filtro-select"
            >
                <option value="">Todos</option>
                <option value="RESERVADO">Reservados</option>
                <option value="CANCELADO_POR_CLIENTE">Cancelados por Cliente</option>
                <option value="CANCELADO_POR_PROFESIONAL">Cancelados por Profesional</option>
            </select>

            {/* Lista de turnos filtrada */}
            {turnosFiltrados.length === 0 ? (
                <p className="no-turnos">No hay turnos disponibles para este profesional en el estado seleccionado.</p>
            ) : (
                <ul className="turnos-list">
                    {turnosFiltrados.map((turno) => (
                        <li key={turno.id} className="turno-item">
                            <p><strong>Día:</strong> {turno.dia}</p>
                            <p><strong>Hora de Inicio:</strong> {turno.fechainicio}</p>
                            <p><strong>Hora de Fin:</strong> {turno.fechaFin}</p>
                            <p><strong>Estado:</strong> {turno.estado}</p>
                            {turno.estado !== 'CANCELADO_POR_PROFESIONAL' && (
                                <button
                                    className="cancelar-button"
                                    onClick={() => cancelarTurno(turno.id)}
                                >
                                    Cancelar Turno
                                </button>
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default TurnosProfesional;
