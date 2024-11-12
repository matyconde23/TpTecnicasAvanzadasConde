// AgregarServicio.js
import React, { useEffect, useState } from 'react';
import { request } from '../services/Api';
import '../css/AgregarServicio.css';

const AgregarServicio = ({ profesionalId }) => {
    const [servicios, setServicios] = useState([]);
    const [selectedServicio, setSelectedServicio] = useState('');
    const [message, setMessage] = useState('');

    // Cargar todos los servicios al montar el componente
    useEffect(() => {
        const fetchServicios = async () => {
            try {
                const response = await request('get', '/api/servicio/all');
                setServicios(response.data);
            } catch (error) {
                setMessage('Error al obtener los servicios');
                console.error('Error fetching servicios:', error);
            }
        };

        fetchServicios();
    }, []);

    // Función para manejar la asociación del profesional con el servicio
    const handleAgregarServicio = async () => {
        if (!selectedServicio) {
            setMessage('Por favor, selecciona un servicio.');
            return;
        }

        const requestBody = {
            profesionalId,
            servicioId: selectedServicio
        };

        try {
            await request('post', '/api/servicio/agregar-profesional', requestBody);
            setMessage('Servicio agregado exitosamente.');
        } catch (error) {
            setMessage('Error al agregar el servicio al profesional.');
            console.error('Error:', error);
        }
    };

    return (
        <div className="agregar-servicio-container">
            <h3>Agregar Servicio al Profesional</h3>
            <select
                value={selectedServicio}
                onChange={(e) => setSelectedServicio(e.target.value)}
                className="servicio-select"
            >
                <option value="">Selecciona un servicio</option>
                {servicios.map(servicio => (
                    <option key={servicio.id} value={servicio.id}>
                        {servicio.nombre}
                    </option>
                ))}
            </select>
            <button onClick={handleAgregarServicio} className="confirmar-agregar-button">
                Agregar Servicio
            </button>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default AgregarServicio;
