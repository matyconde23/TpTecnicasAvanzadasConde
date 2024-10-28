import axios from 'axios';

// Obtener el token del localStorage
export const getAuthToken = () => {
    return localStorage.getItem('auth_token');
};

// Establecer o remover el token en el localStorage
export const setAuthHeader = (token) => {
    if (token !== null) {
        localStorage.setItem("auth_token", token);
    } else {
        localStorage.removeItem("auth_token");
    }
};

// Configuración global de Axios
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.headers.post['Content-Type'] = 'application/json';


// Función para hacer peticiones con el token JWT si está disponible
export const request = (method, url, data) => {
    const token = getAuthToken();
    let headers = {};

    // Solo añade el token si no es una solicitud de registro o login
    if (token !== null && token !== "null" && !url.includes('/api/auth/register') && !url.includes('/api/auth/login')) {
        headers = { 'Authorization': `Bearer ${token}` };
        console.log("Token enviado:", token);  // Verifica que el token se está enviando
    } else {
        console.log("No token sent for this request");
    }

    return axios({
        method: method,
        url: url,
        headers: headers,
        data: data,
        withCredentials: true
    });
};


