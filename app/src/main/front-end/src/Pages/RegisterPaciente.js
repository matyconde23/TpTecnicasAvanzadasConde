import React, { useState } from 'react';
import { request } from '../services/Api';
import '../css/RegisterPaciente.css';

const RegisterPaciente = () => {
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
            await request('POST', '/api/auth/register/usuario', formData);
            setSuccess('Paciente registrado con éxito');
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
            const errorMsg = error.response?.data || 'Error al registrar el paciente';
            setError(errorMsg);
        }
    };

    return (
        <div className="container">
            <h2>Registrar Paciente</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="username"
                    placeholder="Usuario"
                    value={formData.username}
                    onChange={handleChange}
                    required
                />
                <input
                    type="password"
                    name="password"
                    placeholder="Contraseña"
                    value={formData.password}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="apellido"
                    placeholder="Apellido"
                    value={formData.apellido}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="nombre"
                    placeholder="Nombre"
                    value={formData.nombre}
                    onChange={handleChange}
                    required
                />
                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="telefono"
                    placeholder="Teléfono"
                    value={formData.telefono}
                    onChange={handleChange}
                    required
                />
                <button type="submit">Registrar Paciente</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {success && <p style={{ color: 'green' }}>{success}</p>}
        </div>
    );
};

export default RegisterPaciente;
