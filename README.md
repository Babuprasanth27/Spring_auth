# 🔐 Spring Boot JWT Authentication with Refresh Token (Full Stack)

A production-ready authentication system built using **Spring Boot + React (Vite)** implementing:

* JWT-based authentication
* Refresh token mechanism
* HttpOnly cookie security
* Token rotation
* Auto token refresh (frontend interceptor)

---

## 🚀 Tech Stack

### Backend

* Java + Spring Boot
* Spring Security
* JWT (JJWT)
* MySQL
* JPA / Hibernate

### Frontend

* React (Vite)
* Axios
* React Router

---

## 🔑 Features

* ✅ User Registration with Roles (USER / ADMIN)
* ✅ Secure Login with JWT Access Token
* ✅ Refresh Token stored in **HttpOnly Cookies**
* ✅ Automatic Access Token Refresh
* ✅ Refresh Token Rotation (New token issued each refresh)
* ✅ Protected Routes (Frontend)
* ✅ Role-based Authorization
* ✅ Logout with Cookie Cleanup
* ✅ CORS Configuration for frontend-backend communication

---

## 🧠 Authentication Flow

```text
Login →
   Access Token (short-lived)
   Refresh Token (long-lived, HttpOnly cookie)

API Request →
   Access Token sent in header

If Access Token expires →
   Backend returns 401 →
   Axios interceptor triggers /auth/refresh →
   New Access Token generated →
   Original request retried

If Refresh Token expires →
   User redirected to login
```

---

## 🔐 Token Strategy

| Token Type    | Storage         | Expiry            | Purpose                    |
| ------------- | --------------- | ----------------- | -------------------------- |
| Access Token  | LocalStorage    | Short (1 min)  | API authentication         |
| Refresh Token | HttpOnly Cookie | Longer (5 min) | Generate new access tokens |

---

## 📁 Project Structure

### Backend (Spring Boot)

```text
backend/
├── config/
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
├── controllers/
│   ├── AuthController.java
│   ├── UserController.java
├── dto/
│   ├── RegisterRequest.java
│   ├── AuthResponse.java
├── entities/
│   ├── User.java
│   ├── Role.java
│   ├── RefreshToken.java
├── repositories/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── RefreshTokenRepository.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAuthenticationEntryPoint.java
├── services/
│   ├── CustomUserDetailsService.java
│   ├── RefreshTokenService.java
```

---

### Frontend (React)

```text
frontend/
├── src/
│   ├── api/
│   │   └── axios.js
│   ├── pages/
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── Dashboard.jsx
│   ├── App.jsx
```

---

## 🔧 API Endpoints

### Auth APIs

| Method | Endpoint         | Description          |
| ------ | ---------------- | -------------------- |
| POST   | `/auth/register` | Register new user    |
| POST   | `/auth/login`    | Login and get tokens |
| POST   | `/auth/refresh`  | Refresh access token |
| POST   | `/auth/logout`   | Logout user          |

---

### Protected APIs

| Endpoint              | Access              |
| --------------------- | ------------------- |
| `/api/protected-data` | Authenticated users |
| `/admin/**`           | ADMIN role          |
| `/user/**`            | USER role           |

---

## 🔁 Refresh Token Flow

* Refresh token stored in **HttpOnly cookie**
* Automatically sent by browser
* Backend validates and rotates token
* New access token issued without user interaction

---

## ⚙️ Setup Instructions

---

### 🔹 Backend Setup

1. Clone repository


2. Configure MySQL in `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

3. Run Spring Boot app

```bash
mvn spring-boot:run
```

---

### 🔹 Frontend Setup

1. Navigate to frontend folder

```bash
cd frontend
```

2. Install dependencies

```bash
npm install
```

3. Run app

```bash
npm run dev
```

---

## 🌐 Environment URLs

| Service  | URL                   |
| -------- | --------------------- |
| Frontend | http://localhost:5173 |
| Backend  | http://localhost:8081 |

---

## ⚠️ Important Configurations

### CORS (Backend)

* Allow frontend origin
* Enable credentials for cookies

### Axios (Frontend)

```javascript
withCredentials: true
```

---

## 🧪 Testing Flow

1. Register user
2. Login
3. Access dashboard
4. Wait for token expiry
5. Trigger API → auto refresh
6. Logout

---

## 📌 Conclusion

This project demonstrates a **production-level authentication system** using:

* Secure token handling
* Automatic session management
* Scalable backend architecture

---

## ⭐ If you like this project

Give it a ⭐ on GitHub!

---
