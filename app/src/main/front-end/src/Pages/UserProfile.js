// UserProfile.js
import React, { useEffect, useState } from 'react';
import { request } from '../services/Api';
import { useNavigate } from 'react-router-dom';
import PerfilUsuario from '../Components/PerfilUsuario';
import TurnosUsuario from '../Components/TurnosUsuario';
import SacarTurno from '../Components/SacarTurno';
import '../css/UserProfile.css';

const UserProfile = () => {
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);
    const [turnos, setTurnos] = useState([]);
    const [servicios, setServicios] = useState([]);
    const [turnoConfirmado, setTurnoConfirmado] = useState(null);
    const [view, setView] = useState('perfil'); // Controla la vista actual
    const navigate = useNavigate();

    useEffect(() => {
        const userId = window.localStorage.getItem('id');
        if (!userId) {
            setError('No user ID found');
            return;
        }

        // Obtener datos del usuario
        request('GET', `/api/usuario/${userId}`, null)
            .then(response => setUserData(response.data))
            .catch(() => setError('Error fetching user data'));

        // Obtener la lista de servicios
        request('GET', '/api/servicio/all', null)
            .then(response => setServicios(response.data))
            .catch(() => setError('Error fetching services'));
        
        // Obtener turnos asociados al usuario
        request('GET', `/api/turno/usuario/${userId}`, null)
            .then(response => setTurnos(response.data))
            .catch(() => setError('Error fetching user appointments'));
    }, []);

    const handleTurnoConfirmado = (turno) => {
        setTurnoConfirmado(turno);
        setView('viewTurnos');
    };

    // Función para manejar la cancelación de un turno
    const handleCancelTurno = (turnoId) => {
        setTurnos(turnos.filter(turno => turno.id !== turnoId)); // Actualiza la lista de turnos
    };
    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <div className="user-profile-container">
            {error && <p>{error}</p>}

            {/* Navbar de navegación */}
            <div className="navbar">
                <button onClick={() => setView('perfil')} className="navbar-button">
                    Ver Datos Personales
                </button>
                <button onClick={() => setView('viewTurnos')} className="navbar-button">
                    Ver Turnos
                </button>
                <button onClick={() => setView('viewSacarTurno')} className="navbar-button">
                    Sacar Turno
                </button>
                <button onClick={handleLogout} className="navbar-button">
                    Cerrar Sesión
                </button>
            </div>

            {/* Vista actual basada en el estado view */}
            {view === 'perfil' && userData && <PerfilUsuario userData={userData} />}
            {view === 'viewTurnos' && (
                <TurnosUsuario turnos={turnos} userId={userData?.id} onCancelTurno={handleCancelTurno} />
            )}
            {view === 'viewSacarTurno' && (
                <div className="reservar-turno">
                    <SacarTurno
                        userId={userData?.id}
                        servicios={servicios}
                        onTurnoConfirmado={handleTurnoConfirmado}
                    />
                </div>
            )}

            {/* Mensaje de confirmación de turno */}
            {turnoConfirmado && (
                <div className="turno-confirmado">
                    <h3>Turno Confirmado</h3>
                    <p>Fecha: {turnoConfirmado.dia}</p>
                    <p>Hora Inicio: {turnoConfirmado.fechainicio}</p>
                    <p>Profesional: {turnoConfirmado.profesional.nombre} {turnoConfirmado.profesional.apellido}</p>
                    <p>Servicio: {turnoConfirmado.servicio.nombre}</p>
                </div>
            )}
        </div>
    );
};

export default UserProfile;
