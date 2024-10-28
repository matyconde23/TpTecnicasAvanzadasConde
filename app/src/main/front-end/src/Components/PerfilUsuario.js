// UserProfileInfo.js
import React from 'react';

const PerfilUsuario = ({ userData }) => {
    return (
        <div>
            <h1>Perfil del usuario</h1>
            <p>Nombre: {userData.nombre}</p>
            <p>Email: {userData.email}</p>
        </div>
    );
};

export default PerfilUsuario;
