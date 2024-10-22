import React, { useEffect, useState } from 'react';
import { request } from '../services/Api';  // Ajusta la ruta según la estructura de tu proyecto

const UserProfile = () => {
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const userId = window.localStorage.getItem('id');  // Obtén el ID desde localStorage
    
        if (!userId) {
        setError('No user ID found');
        return;
    }
        // Hacer una petición GET al backend
        request('GET', `/api/usuario/${userId}`, null)
            .then(response => {
                setUserData(response.data);  // Guardar los datos del usuario
            })
            .catch(err => {
                setError('Error fetching data');
            });
    }, []);

    return (
        <div>
            {error && <p>{error}</p>}
            {userData ? (
                <div>
                    <h1>Perfil del usuario</h1>
                    <p>Nombre: {userData.nombre}</p>
                    <p>Email: {userData.email}</p>
                    {/* Renderiza más información del usuario */}
                </div>
            ) : (
                <p>Cargando...</p>
            )}
        </div>
    );
};

export default UserProfile;



