import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './Pages/Home';
import Login from './Pages/Login';
import Register from './Pages/Register';
import UserProfile from './Pages/UserProfile'; 
import ProfesionalView from './Pages/ProfesionalView';// Import UsuarioView component
import ViewAdmin from './Pages/ViewAdmin';
import RegisterPaciente from './Pages/RegisterPaciente';
import RegisterProfesional from './Pages/RegisterProfesional';


const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/userProfile" element={<UserProfile />} />  {/* Add the user view */}
                <Route path="/profesionalView" element={<ProfesionalView />} /> {/* Nueva ruta */}
                <Route path="/viewAdmin" element={<ViewAdmin />}/>
                <Route path='/registerProfesional' element={<RegisterProfesional/>}/>
                <Route path='/registerPaciente' element={<RegisterPaciente/>}/>
            </Routes>
        </Router>
    );
};

export default App;

