import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../css/Login.css'; // Importación del archivo CSS

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                username,
                password,
            });

            const token = response.data.token;
            const role = response.data.role || 'USER';
            const id = response.data.id;

            localStorage.removeItem('auth_token'); // Elimina el token viejo
            localStorage.setItem('auth_token', token); // Almacena el nuevo token
            localStorage.removeItem('id');
            localStorage.setItem('id', id);
            localStorage.setItem('role', role);

            if (role === 'USER') {
                navigate(`/userProfile`);
            } else if (role === 'PROFESIONAL') {
                navigate('/profesionalView');
            } else if (role === "ADMIN"){
                navigate('/viewAdmin');
            }

        } catch (error) {
            console.error("Error details:", error.response);
            setError(error.response?.data?.message || 'Usuario o contraseña incorrectos');
        }
    };

    const handleRegisterRedirect = () => {
        navigate('/register'); // Redirige a la página de registro
    };

    return (
        <div className="container">
            <h2>Iniciar Sesión</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Usuario"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit">Login</button>
            </form>
            {error && <p className="error">{error}</p>}

            {/* Mensaje y botón de registro */}
            <p>¿Aún no te has registrado?</p>
            <button onClick={handleRegisterRedirect} className="register-button">Registrarse</button>
        </div>
    );
};

export default Login;
