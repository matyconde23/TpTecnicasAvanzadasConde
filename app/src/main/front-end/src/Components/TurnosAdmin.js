import React, { useState, useEffect } from 'react';
import { request } from '../services/Api';
import '../css/ViewAdmin.css'; // Importación del CSS compartido

const TurnosAdmin = () => {
    const [turnos, setTurnos] = useState([]);
    const [error, setError] = useState('');
    const [filtroEstado, setFiltroEstado] = useState(''); // Estado para el filtro de turnos

    useEffect(() => {
        const fetchTurnos = async () => {
            try {
                const response = await request('GET', '/api/turno/all');
                setTurnos(response.data);
            } catch (error) {
                setError('Error al obtener los turnos');
                console.error('Error fetching turnos:', error);
            }
        };

        fetchTurnos();
    }, []);

    // Filtra los turnos según el estado seleccionado
    const turnosFiltrados = filtroEstado 
        ? turnos.filter(turno => turno.estado === filtroEstado)
        : turnos;

    // Función para eliminar un turno
    const handleDeleteTurno = async (turnoId) => {
        try {
            await request('DELETE', `/api/turno/eliminar/${turnoId}`);
            setTurnos(turnos.filter(turno => turno.id !== turnoId));
            alert('Turno eliminado con éxito.');
        } catch (error) {
            console.error('Error al eliminar el turno:', error);
            setError('Error al eliminar el turno');
        }
    };

    return (
        <div className="turnos-admin-container">
            <h2 className="title">Gestión de Turnos</h2>

            {/* Filtro de turnos por estado */}
            <div className="filter-container">
                <label>Filtrar por estado:</label>
                <select 
                    value={filtroEstado} 
                    onChange={(e) => setFiltroEstado(e.target.value)}
                >
                    <option value="">Todos</option>
                    <option value="RESERVADO">Reservados</option>
                    <option value="CANCELADO_POR_CLIENTE">Cancelados por Cliente</option>
                    <option value="CANCELADO_POR_PROFESIONAL">Cancelados por Profesional</option>
                </select>
            </div>

            {/* Lista de turnos */}
            {turnosFiltrados.length === 0 ? (
                <p className="error-message">No hay turnos disponibles en el estado seleccionado.</p>
            ) : (
                <ul className="turnos-list">
                    {turnosFiltrados.map((turno) => (
                        <li key={turno.id} className="turno-item">
                            <p><strong>Día:</strong> {turno.dia}</p>
                            <p><strong>Hora de Inicio:</strong> {turno.horaInicio}</p>
                            <p><strong>Hora de Fin:</strong> {turno.horaFin}</p>
                            <p><strong>Estado:</strong> {turno.estado}</p>
                            <button 
                                className="delete-button" 
                                onClick={() => handleDeleteTurno(turno.id)}
                            >
                                Eliminar Turno
                            </button>
                        </li>
                    ))}
                </ul>
            )}

            {/* Mensaje de error */}
            {error && <p className="error-message">{error}</p>}
        </div>
    );
};

export default TurnosAdmin;
