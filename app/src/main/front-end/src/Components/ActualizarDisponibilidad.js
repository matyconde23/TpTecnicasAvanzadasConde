import React, { useEffect, useState } from 'react';
import { request } from '../services/Api';
import '../css/ActualizarDisponibilidad.css';

const diasSemana = [
    { name: 'Lunes', value: 'MONDAY' },
    { name: 'Martes', value: 'TUESDAY' },
    { name: 'Miércoles', value: 'WEDNESDAY' },
    { name: 'Jueves', value: 'THURSDAY' },
    { name: 'Viernes', value: 'FRIDAY' },
];

const ActualizarDisponibilidad = ({ profesionalId }) => {
    const [disponibilidad, setDisponibilidad] = useState([]);
    const [mensaje, setMensaje] = useState(null);

    useEffect(() => {
        // Cargar disponibilidad actual del profesional
        request('GET', `/api/profesional/${profesionalId}/disponibilidad`)
            .then(response => {
                const disponibilidadData = response.data.map(dia => ({
                    diaSemana: dia.dia, // Usar el campo correcto `dia`
                    horaInicio: dia.horaInicio,
                    horaFin: dia.horaFin,
                }));
                setDisponibilidad(disponibilidadData);
            })
            .catch(() => setMensaje('Error al cargar la disponibilidad actual'));
    }, [profesionalId]);

    const handleChange = (index, field, value) => {
        const nuevaDisponibilidad = [...disponibilidad];
        nuevaDisponibilidad[index][field] = value;
        setDisponibilidad(nuevaDisponibilidad);
    };

    const agregarDia = () => {
        setDisponibilidad([...disponibilidad, { diaSemana: 'MONDAY', horaInicio: '', horaFin: '' }]);
    };

    const eliminarDia = (index) => {
        const nuevaDisponibilidad = disponibilidad.filter((_, i) => i !== index);
        setDisponibilidad(nuevaDisponibilidad);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        request('PUT', `/api/profesional/actualizar/${profesionalId}/disponibilidad`, disponibilidad)
            .then(() => setMensaje('Disponibilidad actualizada correctamente'))
            .catch(() => setMensaje('Error al actualizar la disponibilidad'));
    };

    return (
        <div className="actualizar-disponibilidad-container">
            <h3>Actualizar Disponibilidad</h3>

            {/* Aviso informativo */}
            <p className="aviso">
                Nota: Si se modifica la disponibilidad, los turnos que ya han sido reservados deberán ser atendidos según la disponibilidad anterior.
            </p>

            {mensaje && <p className="mensaje">{mensaje}</p>}
            <form onSubmit={handleSubmit}>
                {disponibilidad.map((dia, index) => (
                    <div key={index} className="dia-disponibilidad">
                        <select
                            value={dia.diaSemana || ''}
                            onChange={(e) => handleChange(index, 'diaSemana', e.target.value)}
                        >
                            {diasSemana.map((d) => (
                                <option key={d.value} value={d.value}>
                                    {d.name}
                                </option>
                            ))}
                        </select>
                        <input
                            type="time"
                            value={dia.horaInicio || ''}
                            onChange={(e) => handleChange(index, 'horaInicio', e.target.value)}
                        />
                        <input
                            type="time"
                            value={dia.horaFin || ''}
                            onChange={(e) => handleChange(index, 'horaFin', e.target.value)}
                        />
                        <button type="button" onClick={() => eliminarDia(index)}>Eliminar</button>
                    </div>
                ))}
                <button type="button" onClick={agregarDia}>Agregar Día</button>
                <button type="submit">Guardar Disponibilidad</button>
            </form>
        </div>
    );
};

export default ActualizarDisponibilidad;
