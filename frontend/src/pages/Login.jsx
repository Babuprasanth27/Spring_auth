import React from 'react'
import { useState } from "react"
import api from '../api/axios';

const Login = () => {

    const [form,setForm] = useState({
        username: "",
        password: "",
    }); 
    const handleSubmit = async (e) => {
        e.preventDefault(); 
        try {
            const res = await api.post("/auth/login",form);

            localStorage.setItem("accessToken", res.data.accessToken);
            alert("Login successful");
            window.location.href = "/dashboard"; //redirect to home page
        } catch (error) {
            alert(error.response?.data || "Invalid login credentials");
        }
    };
  return (
   <form
  style={{
    display: "flex",
    flexDirection: "column",
    gap: "15px",
    width: "100%",
    maxWidth: "350px",
    margin: "auto",
    padding: "30px",
    borderRadius: "12px",
    boxShadow: "0 8px 20px rgba(0,0,0,0.1)",
    backgroundColor: "#ffffff",
  }}
>
  <input
    placeholder="Username"
    onChange={(e) =>
      setForm({ ...form, username: e.target.value })
    }
    style={{
      padding: "12px",
      borderRadius: "8px",
      border: "1px solid #ccc",
      fontSize: "14px",
      outline: "none",
      transition: "0.3s",
    }}
    onFocus={(e) => (e.target.style.border = "1px solid #4CAF50")}
    onBlur={(e) => (e.target.style.border = "1px solid #ccc")}
  />

  <input
    type="password"
    placeholder="Password"
    onChange={(e) =>
      setForm({ ...form, password: e.target.value })
    }
    style={{
      padding: "12px",
      borderRadius: "8px",
      border: "1px solid #ccc",
      fontSize: "14px",
      outline: "none",
      transition: "0.3s",
    }}
    onFocus={(e) => (e.target.style.border = "1px solid #4CAF50")}
    onBlur={(e) => (e.target.style.border = "1px solid #ccc")}
  />

  <button
    onClick={handleSubmit}
    style={{
      padding: "12px",
      borderRadius: "8px",
      border: "none",
      background: "linear-gradient(135deg, #4CAF50, #2E7D32)",
      color: "#fff",
      fontSize: "16px",
      fontWeight: "bold",
      cursor: "pointer",
      transition: "0.3s",
    }}
    onMouseOver={(e) =>
      (e.target.style.background =
        "linear-gradient(135deg, #45a049, #1b5e20)")
    }
    onMouseOut={(e) =>
      (e.target.style.background =
        "linear-gradient(135deg, #4CAF50, #2E7D32)")
    }
  >
    Login
  </button>
</form>
  )
}

export default Login
