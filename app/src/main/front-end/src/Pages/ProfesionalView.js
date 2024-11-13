import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { request } from '../services/Api';
import TurnosProfesional from '../Components/TurnosProfesional';
import ServiciosProfesionales from '../Components/ServiciosProfesionales';
import AgregarServicio from '../Components/AgregarServicio';
import ActualizarDisponibilidad from '../Components/ActualizarDisponibilidad';
import '../css/ProfesionalView.css';

const ProfesionalView = () => {
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);
    const [showUserInfo, setShowUserInfo] = useState(true);
    const [showTurnos, setShowTurnos] = useState(false);
    const [showServicios, setShowServicios] = useState(false);
    const [showAgregarServicio, setShowAgregarServicio] = useState(false);
    const [showDisponibilidad, setShowDisponibilidad] = useState(false); // Nuevo estado para ActualizarDisponibilidad
    const id = localStorage.getItem('id');
    const navigate = useNavigate();

    useEffect(() => {
        request('GET', `/api/profesional/${id}`, null)
            .then(response => setUserData(response.data))
            .catch(() => setError('Error al obtener los datos del usuario'));
    }, [id]);

    const toggleTurnos = () => {
        setShowTurnos(!showTurnos);
        setShowServicios(false);
        setShowAgregarServicio(false);
        setShowDisponibilidad(false);
        setShowUserInfo(false);
    };

    const toggleServicios = () => {
        setShowServicios(!showServicios);
        setShowTurnos(false);
        setShowAgregarServicio(false);
        setShowDisponibilidad(false);
        setShowUserInfo(false);
    };

    const toggleAgregarServicio = () => {
        setShowAgregarServicio(!showAgregarServicio);
        setShowTurnos(false);
        setShowServicios(false);
        setShowDisponibilidad(false);
        setShowUserInfo(false);
    };

    const toggleDisponibilidad = () => {
        setShowDisponibilidad(!showDisponibilidad);
        setShowTurnos(false);
        setShowServicios(false);
        setShowAgregarServicio(false);
        setShowUserInfo(false);
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <div className="profesional-view-container">
            <h2 className="title">Vista del Profesional</h2>

            <nav className="navbar">
                <button onClick={toggleTurnos} className="navbar-button">
                    {showTurnos ? 'Ocultar Turnos' : 'Ver Turnos'}
                </button>
                <button onClick={toggleServicios} className="navbar-button">
                    {showServicios ? 'Ocultar Servicios' : 'Ver Servicios'}
                </button>
                <button onClick={toggleAgregarServicio} className="navbar-button">
                    {showAgregarServicio ? 'Ocultar Selección de Servicio' : 'Agregar Servicio'}
                </button>
                <button onClick={toggleDisponibilidad} className="navbar-button">
                    {showDisponibilidad ? 'Ocultar Disponibilidad' : 'Actualizar Disponibilidad'}
                </button>
                <button onClick={handleLogout} className="navbar-button">
                    Cerrar Sesión
                </button>
            </nav>
            {error && <p className="error-message">{error}</p>}

            {showUserInfo && userData && (
                <div className="user-info">
                    <h3>Datos del Profesional</h3>
                    <p><strong>Nombre:</strong> {userData.nombre}</p>
                    <p><strong>Apellido:</strong> {userData.apellido}</p>
                    <p><strong>Email:</strong> {userData.email}</p>
                    <p><strong>Especialidad:</strong> {userData.especialidad}</p>
                </div>
            )}

            <div className="scrollable-content">
                {showTurnos && <TurnosProfesional profesionalId={id} />}
                {showServicios && <ServiciosProfesionales profesionalId={id} />}
                {showAgregarServicio && <AgregarServicio profesionalId={id} />}
                {showDisponibilidad && <ActualizarDisponibilidad profesionalId={id} />}
            </div>
        </div>
    );
};

export default ProfesionalView;
