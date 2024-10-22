import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './Pages/Home';
import Login from './Pages/Login';
import Register from './Pages/Register';
import UserProfile from './Pages/UserProfile'; 
import ProfesionalView from './Pages/ProfesionalView';// Import UsuarioView component

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/userProfile" element={<UserProfile />} />  {/* Add the user view */}
                <Route path="/profesionalView" element={<ProfesionalView />} /> {/* Nueva ruta */}
            </Routes>
        </Router>
    );
};

export default App;

