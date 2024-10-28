import React, { useState } from 'react';
import { request } from '../services/Api'; // Importa la función de solicitud configurada con token

const ViewAdmin = () => {
    const [nombre, setNombre] = useState('');
    const [descripcion, setDescripcion] = useState('');
    const [duracionMinutos, setDuracionMinutos] = useState('');
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Validar que los campos no estén vacíos
            if (!nombre || !descripcion || !duracionMinutos) {
                setError("Todos los campos son obligatorios");
                return;
            }

            const requestData = {
                nombre,
                descripcion,
                duracionMinutos: parseInt(duracionMinutos),
            };

            // Realizar la solicitud para crear el servicio
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
        <div className="container">
            <h2>Crear Nuevo Servicio</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Nombre del Servicio:</label>
                    <input
                        type="text"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                    />
                </div>

                <div>
                    <label>Descripción:</label>
                    <textarea
                        value={descripcion}
                        onChange={(e) => setDescripcion(e.target.value)}
                    />
                </div>

                <div>
                    <label>Duración (minutos):</label>
                    <input
                        type="number"
                        value={duracionMinutos}
                        onChange={(e) => setDuracionMinutos(e.target.value)}
                    />
                </div>

                <button type="submit">Crear Servicio</button>
            </form>

            {error && <p className="error">{error}</p>}
            {successMessage && <p className="success">{successMessage}</p>}
        </div>
    );
};

export default ViewAdmin;
