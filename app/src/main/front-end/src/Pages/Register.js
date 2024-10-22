import React, { useState } from 'react';
import { request } from '../services/Api'; // Importa la función request desde api.js

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        role: 'USER',
        apellido: '',
        nombre: '',
        email: '',
        telefono: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Usamos la función request de api.js para hacer el POST
            const response = await request('POST', '/api/auth/register/usuario', formData);
            setSuccess('Usuario registrado con éxito');
            setError('');
            setFormData({
                username: '',
                password: '',
                role: 'USER',
                apellido: '',
                nombre: '',
                email: '',
                telefono: ''
            });
        } catch (error) {
            const errorMsg = error.response?.data || 'Error al registrar el usuario';
            setError(errorMsg);
        }
    };

    return (
        <div>
            <h2>Registrar Usuario</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="username"
                    placeholder="Usuario"
                    value={formData.username}
                    onChange={handleChange}
                />
                <input
                    type="password"
                    name="password"
                    placeholder="Contraseña"
                    value={formData.password}
                    onChange={handleChange}
                />
                <input
                    type="text"
                    name="apellido"
                    placeholder="Apellido"
                    value={formData.apellido}
                    onChange={handleChange}
                />
                <input
                    type="text"
                    name="nombre"
                    placeholder="Nombre"
                    value={formData.nombre}
                    onChange={handleChange}
                />
                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={formData.email}
                    onChange={handleChange}
                />
                <input
                    type="text"
                    name="telefono"
                    placeholder="Teléfono"
                    value={formData.telefono}
                    onChange={handleChange}
                />
                <button type="submit">Registrar</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {success && <p style={{ color: 'green' }}>{success}</p>}
        </div>
    );
};

export default Register;
