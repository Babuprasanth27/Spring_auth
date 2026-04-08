import React from 'react'
import {BrowserRouter as Router, Routes, Route, Navigate, BrowserRouter} from "react-router-dom"
import Register from './pages/Register';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';

const ProctedRoute = ({ children }) => {
  const token = localStorage.getItem("accessToken");
 
  return token ? children : <Navigate to="/login" />;
};
const App = () => {
  return (
    <BrowserRouter>
      <Routes>
      {/* //Public routes */}
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        
        {/* //Protected route */}
        <Route path="/dashboard" element={
          <ProctedRoute>
            <Dashboard />
          </ProctedRoute>
        } />

        {/* //Default Redirect */}
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
