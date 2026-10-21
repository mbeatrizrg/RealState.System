import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Le agregamos ": any" a config y a error para que TypeScript no marque error
api.interceptors.request.use((config: any) => {
  const token = localStorage.getItem('token_crm');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error: any) => {
  return Promise.reject(error);
});

export default api;