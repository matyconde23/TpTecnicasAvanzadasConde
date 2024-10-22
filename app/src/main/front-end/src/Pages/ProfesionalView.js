import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ProfesionalViewAll = () => {
    const [profesionales, setProfesionales] = useState([]);
    const [error, setError] = useState('');

    // Función para obtener los profesionales desde la API
    useEffect(() => {
        const fetchProfesionales = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/profesional/all'); // Asegúrate de que esta URL sea la correcta
                setProfesionales(response.data);
            } catch (error) {
                setError('Error al obtener la lista de profesionales');
                console.error('Error fetching profesionales:', error);
            }
        };

        fetchProfesionales();
    }, []);

    // Mostrar un mensaje si hay un error
    if (error) {
        return <p>{error}</p>;
    }

    // Mostrar la lista de profesionales si se obtuvieron correctamente
    return (
        <div>
            <h2>Lista de Profesionales</h2>
            <ul>
                {profesionales.map((profesional) => (
                    <li key={profesional.id}>
                        <p><strong>Nombre:</strong> {profesional.nombre} {profesional.apellido}</p>
                        <p><strong>Email:</strong> {profesional.email}</p>
                        <p><strong>Especialidad:</strong> {profesional.especialidad}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ProfesionalViewAll;

