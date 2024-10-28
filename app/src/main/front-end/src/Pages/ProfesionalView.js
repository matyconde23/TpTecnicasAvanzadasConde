// ProfesionalView.js
import React, { useState } from 'react';
import TurnosProfesional from '../Components/TurnosProfesional';
import ServiciosProfesionales from '../Components/ServiciosProfesionales';
import AgregarServicio from '../Components/AgregarServicio'; // Nuevo componente
import '../css/TurnosProfesional.css';

const ProfesionalView = () => {
    const [showTurnos, setShowTurnos] = useState(false);
    const [showServicios, setShowServicios] = useState(false);
    const [showAgregarServicio, setShowAgregarServicio] = useState(false); // Estado para mostrar AgregarServicio
    const id = localStorage.getItem('id'); // ID del profesional

    return (
        <div className="profesional-view-container">
            <h2 className="title">Vista del Profesional</h2>
            
            <div className="button-container">
                <button onClick={() => setShowTurnos(!showTurnos)} className="ver-turnos-button">
                    {showTurnos ? 'Ocultar Turnos' : 'Ver Turnos'}
                </button>
                <button onClick={() => setShowServicios(!showServicios)} className="ver-servicios-button">
                    {showServicios ? 'Ocultar Servicios' : 'Ver Servicios'}
                </button>
                <button onClick={() => setShowAgregarServicio(!showAgregarServicio)} className="agregar-servicio-button">
                    {showAgregarServicio ? 'Ocultar Selección de Servicio' : 'Agregar Servicio'}
                </button>
            </div>

            {showTurnos && <TurnosProfesional profesionalId={id} />}
            {showServicios && <ServiciosProfesionales profesionalId={id} />}
            {showAgregarServicio && <AgregarServicio profesionalId={id} />}
        </div>
    );
};

export default ProfesionalView;
