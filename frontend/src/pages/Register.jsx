import { useState } from "react";
import api from "../api/axios";

export default function Register() {

    const [form,setForm] = useState({
        username: "",
        password: "",
        roles: ["USER"]
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.post("/auth/register", form);
            alert("Registered Successfully");
        } catch (error) {
            alert(error.response?.data || "Error");
        }
    };

    return(

       <form
  onSubmit={handleSubmit}
  style={{
    display: "flex",
    flexDirection: "column",
    gap: "16px",
    width: "100%",
    maxWidth: "380px",
    margin: "40px auto",
    padding: "35px 25px",
    borderRadius: "14px",
    boxShadow: "0 10px 25px rgba(0,0,0,0.12)",
    background: "#ffffff",
  }}
>
  <h2 style={{ textAlign: "center", marginBottom: "10px" }}>
    Register
  </h2>

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
    onFocus={(e) => (e.target.style.border = "1px solid #2196F3")}
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
    onFocus={(e) => (e.target.style.border = "1px solid #2196F3")}
    onBlur={(e) => (e.target.style.border = "1px solid #ccc")}
  />

  <button
    type="submit"
    style={{
      padding: "12px",
      borderRadius: "8px",
      border: "none",
      background: "linear-gradient(135deg, #2196F3, #0D47A1)",
      color: "#fff",
      fontSize: "16px",
      fontWeight: "bold",
      cursor: "pointer",
      transition: "0.3s",
    }}
    onMouseOver={(e) =>
      (e.target.style.background =
        "linear-gradient(135deg, #1976D2, #0B3C91)")
    }
    onMouseOut={(e) =>
      (e.target.style.background =
        "linear-gradient(135deg, #2196F3, #0D47A1)")
    }
  >
    Register
  </button>
</form>
    );
}