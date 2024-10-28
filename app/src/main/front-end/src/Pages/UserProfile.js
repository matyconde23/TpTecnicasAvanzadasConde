// UserProfile.js
import React, { useEffect, useState } from 'react';
import { request } from '../services/Api';
import PerfilUsuario from '../Components/PerfilUsuario';
import TurnosUsuario from '../Components/TurnosUsuario';
import SacarTurno from '../Components/SacarTurno';

const UserProfile = () => {
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);
    const [turnos, setTurnos] = useState([]);
    const [servicios, setServicios] = useState([]);
    const [turnoConfirmado, setTurnoConfirmado] = useState(null);
    const [view, setView] = useState('perfil'); // Controla la vista actual

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
        request('GET', '/api/servicio', null)
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

    return (
        <div>
            {error && <p>{error}</p>}

            {/* Botones de navegación */}
            <div style={{ marginBottom: '20px' }}>
                <button onClick={() => setView('perfil')}>Ver Datos Personales</button>
                <button onClick={() => setView('viewTurnos')}>Ver Turnos</button>
                <button onClick={() => setView('viewSacarTurno')}>Sacar Turno</button>
            </div>

            {/* Vista actual basada en el estado view */}
            {view === 'perfil' && userData && <PerfilUsuario userData={userData} />}
            {view === 'viewTurnos' && (
                <TurnosUsuario turnos={turnos} userId={userData?.id} onCancelTurno={handleCancelTurno} />
            )}
            {view === 'viewSacarTurno' && (
                <SacarTurno
                    userId={userData?.id}
                    servicios={servicios}
                    onTurnoConfirmado={handleTurnoConfirmado}
                />
            )}

            {/* Mensaje de confirmación de turno */}
            {turnoConfirmado && (
                <div>
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
