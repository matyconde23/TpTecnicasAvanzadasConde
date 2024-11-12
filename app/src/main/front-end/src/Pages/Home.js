import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Home.css'; // Importa el archivo de CSS

const Home = () => {
  return (
    <div className="home-container">
      <h1 className="home-title">Bienvenido a Serenity Spa</h1>
      <p className="home-subtitle">
        Inicia tu sesión aquí o regístrate para ver qué ofrecemos.
      </p>
      <div className="button-container">
        <Link to="/login" className="home-button">
          Iniciar Sesión
        </Link>
        <Link to="/register" className="home-button">
          Registrarse
        </Link>
      </div>
    </div>
  );
};

export default Home;
