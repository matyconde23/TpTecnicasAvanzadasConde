import React, { useState } from 'react';
import { request } from '../services/Api';
import '../css/RegisterProfesional.css'; // Asegúrate de importar los estilos

const RegisterProfesional = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        role: 'PROFESIONAL',
        apellido: '',
        nombre: '',
        email: '',
        telefono: '',
        especialidad: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [useDefaultDisponibilidad, setUseDefaultDisponibilidad] = useState(true);
    const [disponibilidad, setDisponibilidad] = useState([{ dia: '', horaInicio: '', horaFin: '' }]);
    const [selectedDays, setSelectedDays] = useState([]); // Controlar días seleccionados

    const diasSemana = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes"];

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleDisponibilidadChange = (index, field, value) => {
        const newDisponibilidad = [...disponibilidad];
        newDisponibilidad[index][field] = value;
        setDisponibilidad(newDisponibilidad);
    };

    const addDisponibilidad = () => {
        setDisponibilidad([...disponibilidad, { dia: '', horaInicio: '', horaFin: '' }]);
    };

    const handleDayChange = (index, value) => {
        const newDisponibilidad = [...disponibilidad];
        newDisponibilidad[index].dia = value;
        setDisponibilidad(newDisponibilidad);

        // Actualizar días seleccionados
        const newSelectedDays = disponibilidad
            .map((disp) => disp.dia)
            .filter((dia) => dia); // Filtrar días vacíos
        setSelectedDays(newSelectedDays);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const disponibilidadData = useDefaultDisponibilidad ? null : disponibilidad;
            await request('POST', '/api/auth/register/profesional', { ...formData, disponibilidad: disponibilidadData });
            setSuccess('Profesional registrado con éxito');
            setError('');
            setFormData({
                username: '',
                password: '',
                role: 'PROFESIONAL',
                apellido: '',
                nombre: '',
                email: '',
                telefono: '',
                especialidad: ''
            });
            setDisponibilidad([{ dia: '', horaInicio: '', horaFin: '' }]);
            setUseDefaultDisponibilidad(true);
            setSelectedDays([]); // Restablecer días seleccionados
        } catch (error) {
            const errorMsg = error.response?.data || 'Error al registrar el profesional';
            setError(errorMsg);
        }
    };

    return (
        <div className="register-profesional-container">
            <h2>Registrar Profesional</h2>
            <form onSubmit={handleSubmit} className="register-form">
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
                <input
                    type="text"
                    name="especialidad"
                    placeholder="Especialidad"
                    value={formData.especialidad}
                    onChange={handleChange}
                    required
                />

                <h3>Disponibilidad</h3>
                <div className="disponibilidad-options">
                    <label>
                        <input
                            type="radio"
                            name="disponibilidad"
                            checked={useDefaultDisponibilidad}
                            onChange={() => setUseDefaultDisponibilidad(true)}
                        />
                        Usar disponibilidad por defecto
                    </label>
                    <label>
                        <input
                            type="radio"
                            name="disponibilidad"
                            checked={!useDefaultDisponibilidad}
                            onChange={() => setUseDefaultDisponibilidad(false)}
                        />
                        Usar disponibilidad personalizada
                    </label>
                </div>

                {!useDefaultDisponibilidad && (
                    <div className="disponibilidad-form">
                        {disponibilidad.map((disp, index) => (
                            <div key={index} className="disponibilidad-entry">
                                <label>Día:</label>
                                <select
                                    value={disp.dia}
                                    onChange={(e) => handleDayChange(index, e.target.value)}
                                    required
                                >
                                    <option value="">Selecciona un día</option>
                                    {diasSemana
                                        .filter((dia) => !selectedDays.includes(dia) || dia === disp.dia)
                                        .map((dia) => (
                                            <option key={dia} value={dia}>
                                                {dia}
                                            </option>
                                        ))}
                                </select>
                                <label>Hora Inicio:</label>
                                <input
                                    type="time"
                                    placeholder="Hora Inicio"
                                    value={disp.horaInicio}
                                    onChange={(e) => handleDisponibilidadChange(index, 'horaInicio', e.target.value)}
                                    required
                                />
                                <label>Hora Fin:</label>
                                <input
                                    type="time"
                                    placeholder="Hora Fin"
                                    value={disp.horaFin}
                                    onChange={(e) => handleDisponibilidadChange(index, 'horaFin', e.target.value)}
                                    required
                                />
                            </div>
                        ))}
                        <button type="button" onClick={addDisponibilidad} className="add-button">Agregar Disponibilidad</button>
                    </div>
                )}

                <button type="submit" className="submit-button">Registrar Profesional</button>
            </form>
            {error && <p className="error-message">{error}</p>}
            {success && <p className="success-message">{success}</p>}
        </div>
    );
};

export default RegisterProfesional;
