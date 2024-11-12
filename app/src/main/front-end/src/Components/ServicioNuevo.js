import React, { useState } from 'react';
import { request } from '../services/Api';
import '../css/ViewAdmin.css'; // Importación del CSS

const ServicioNuevo = () => {
    const [nombre, setNombre] = useState('');
    const [descripcion, setDescripcion] = useState('');
    const [duracionMinutos, setDuracionMinutos] = useState('');
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (!nombre || !descripcion || !duracionMinutos) {
                setError("Todos los campos son obligatorios");
                return;
            }

            const requestData = {
                nombre,
                descripcion,
                duracionMinutos: parseInt(duracionMinutos),
            };

            const response = await request('POST', '/api/servicio/crear', requestData);
            
            setSuccessMessage(`Servicio "${response.data.nombre}" creado exitosamente.`);
            setError('');
            setNombre('');
            setDescripcion('');
            setDuracionMinutos('');
        } catch (error) {
            console.error("Error al crear el servicio:", error.response);
            setError(error.response?.data || 'Error al crear el servicio');
        }
    };

    return (
        <div className="servicio-nuevo-container">
            <h3 className="title">Crear Nuevo Servicio</h3>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Nombre del Servicio:</label>
                    <input
                        type="text"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                        className="form-control"
                    />
                </div>

                <div className="form-group">
                    <label>Descripción:</label>
                    <textarea
                        value={descripcion}
                        onChange={(e) => setDescripcion(e.target.value)}
                        className="form-control"
                    />
                </div>

                <div className="form-group">
                    <label>Duración (minutos):</label>
                    <input
                        type="number"
                        value={duracionMinutos}
                        onChange={(e) => setDuracionMinutos(e.target.value)}
                        className="form-control"
                    />
                </div>

                <button type="submit" className="submit-button">Crear Servicio</button>

                {error && <p className="error-message">{error}</p>}
                {successMessage && <p className="success-message">{successMessage}</p>}
            </form>
        </div>
    );
};

export default ServicioNuevo;
