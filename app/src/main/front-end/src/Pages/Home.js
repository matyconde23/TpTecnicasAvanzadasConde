import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Home.css'; // Asegúrate de crear este archivo para aplicar los estilos

const Home = () => {
  return (
    <div className="home-container">
      <h2 className="home-title">Bienvenido</h2>
      <p className="home-subtitle">Elige una opción:</p>
      <div className="button-container">
        <Link to="/login" className="home-button">Iniciar Sesión</Link>
        <Link to="/register" className="home-button">Registrarse</Link>
      </div>
    </div>
  );
};

export default Home;
