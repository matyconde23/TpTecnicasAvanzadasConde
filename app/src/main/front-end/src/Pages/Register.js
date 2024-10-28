import React from 'react';
import { useNavigate } from 'react-router-dom';

const RoleSelection = () => {
    const navigate = useNavigate();

    const handlePaciente = () => {
        navigate('/registerPaciente');
    };

    const handleProfesional = () => {
        navigate('/registerProfesional');
    };

    return (
        <div className="container">
            <h2>¿Eres profesional o paciente?</h2>
            <button onClick={handlePaciente}>Paciente</button>
            <button onClick={handleProfesional}>Profesional</button>
        </div>
    );
};

export default RoleSelection;
