import React, { useState } from 'react';
import TurnosAdmin from '../Components/TurnosAdmin';
import ServicioNuevo from '../Components/ServicioNuevo';
import '../css/ViewAdmin.css'; // Importación del CSS compartido

const ViewAdmin = () => {
    const [showTurnos, setShowTurnos] = useState(false);
    const [showServicioNuevo, setShowServicioNuevo] = useState(false);

    const toggleTurnos = () => {
        setShowTurnos(!showTurnos);
        setShowServicioNuevo(false);
    };

    const toggleServicioNuevo = () => {
        setShowServicioNuevo(!showServicioNuevo);
        setShowTurnos(false);
    };

    return (
        <div className="view-admin-container">
            <h2 className="title">Vista del Administrador</h2>
            {/* Navbar para acceder a los diferentes componentes */}
            <nav className="navbar">
                <button onClick={toggleTurnos} className="navbar-button">
                    {showTurnos ? 'Ocultar Gestión de Turnos' : 'Ver Gestión de Turnos'}
                </button>
                <button onClick={toggleServicioNuevo} className="navbar-button">
                    {showServicioNuevo ? 'Ocultar Crear Nuevo Servicio' : 'Crear Nuevo Servicio'}
                </button>
            </nav>

            {/* Contenido dinámico con scroll */}
            <div className="scrollable-content">
                {showTurnos && <TurnosAdmin />}
                {showServicioNuevo && <ServicioNuevo />}
            </div>
        </div>
    );
};

export default ViewAdmin;