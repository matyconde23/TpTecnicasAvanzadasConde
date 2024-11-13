import React, { useState, useEffect } from 'react';
import { request } from '../services/Api';
import '../css/TurnosProfesional.css'; // Importación del CSS

const TurnosProfesional = ({ profesionalId }) => {
    const [turnos, setTurnos] = useState([]);
    const [error, setError] = useState('');
    const [filtroDia, setFiltroDia] = useState(''); // Estado para el filtro de día de la semana

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

    // Función para obtener el nombre del día de la semana a partir de una fecha
    const obtenerDiaSemana = (fecha) => {
        const diasSemana = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
        return diasSemana[new Date(fecha).getDay()];
    };

    // Filtrar turnos según el día de la semana y el estado `RESERVADO`
    const turnosFiltrados = turnos.filter(turno => 
        turno.estado === 'RESERVADO' && (!filtroDia || obtenerDiaSemana(turno.dia) === filtroDia)
    );

    // Manejo de errores
    if (error) {
        return <p className="error-message">{error}</p>;
    }

    return (
        <div className="turnos-container">
            {/* Selector de filtro para los días de la semana */}
            <label htmlFor="filtro-dia">Filtrar por día de la semana:</label>
            <select
                id="filtro-dia"
                value={filtroDia}
                onChange={(e) => setFiltroDia(e.target.value)}
                className="filtro-select"
            >
                <option value="">Todos</option>
                <option value="Lunes">Lunes</option>
                <option value="Martes">Martes</option>
                <option value="Miércoles">Miércoles</option>
                <option value="Jueves">Jueves</option>
                <option value="Viernes">Viernes</option>
                <option value="Sábado">Sábado</option>
                <option value="Domingo">Domingo</option>
            </select>

            {/* Lista de turnos filtrada */}
            {turnosFiltrados.length === 0 ? (
                <p className="no-turnos">No hay turnos reservados para este profesional en el día seleccionado.</p>
            ) : (
                <ul className="turnos-list">
                    {turnosFiltrados.map((turno) => (
                        <li key={turno.id} className="turno-item">
                            <p><strong>Servicio:</strong> {turno.servicio?.nombre || 'No especificado'}</p> {/* Agregado el nombre del servicio */}
                            <p><strong>Día:</strong> {turno.dia}</p>
                            <p><strong>Hora de Inicio:</strong> {turno.fechainicio}</p>
                            <p><strong>Hora de Fin:</strong> {turno.fechaFin}</p>
                            <p><strong>Estado:</strong> {turno.estado}</p>
                            
                            <button
                                className="cancelar-button"
                                onClick={() => cancelarTurno(turno.id)}
                            >
                                Cancelar Turno
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default TurnosProfesional;
