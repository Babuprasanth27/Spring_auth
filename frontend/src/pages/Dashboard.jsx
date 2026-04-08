import React, { useEffect } from 'react';
import api from '../api/axios';

const Dashboard = () => {

    //  DEFINE FUNCTION FIRST
    const fetchData = async () => {
        try {
            const res = await api.get("/api/protected-data");
            console.log("✅ Data:", res.data);
        } catch (err) {
            console.error("❌ Error:", err);
        }
    };

    // CALL ON LOAD (optional)
    useEffect(() => {
        fetchData();
    }, []);

    const logout = async () => {
        await api.post("/auth/logout");
        localStorage.removeItem("accessToken");
        window.location.href = "/login";
    };

    return (
        <>
            <h1>Dashboard - Protected Route</h1>

            {/* 🔥 Button to manually trigger API */}
            <button onClick={fetchData}>Fetch Data</button>

            <br /><br />

            <button onClick={logout}>Logout</button>
        </>
    );
};

export default Dashboard;