// ServiciosProfesionales.js
import React, { useEffect, useState } from 'react';
import { request } from '../services/Api'; // Asegúrate de ajustar la ruta si es necesario
import '../css/ServiciosProfesionales.css';

const ServiciosProfesionales = ({ profesionalId }) => {
    const [servicios, setServicios] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchServiciosPorProfesional = async () => {
            try {
                const response = await request('get', `/api/servicio/profesional/${profesionalId}`);
                setServicios(response.data);
            } catch (error) {
                setError('Error al obtener los servicios del profesional');
                console.error('Error fetching servicios:', error);
            }
        };

        if (profesionalId) {
            fetchServiciosPorProfesional();
        }
    }, [profesionalId]);

    if (error) {
        return <p className="error-message">{error}</p>;
    }

    return (
        <div className="servicios-container">
            <h3>Servicios que brinda el Profesional</h3>
            {servicios.length === 0 ? (
                <p>No se encontraron servicios para este profesional.</p>
            ) : (
                <ul className="servicios-list">
                    {servicios.map((servicio) => (
                        <li key={servicio.id} className="servicio-item">
                            <p><strong>Nombre:</strong> {servicio.nombre}</p>
                            <p><strong>Descripción:</strong> {servicio.descripcion}</p>
                            <p><strong>Duración:</strong> {servicio.duracionMinutos} minutos</p>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ServiciosProfesionales;
