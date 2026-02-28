


# 📗 Backend Repo README (Spring Boot)

markdown
# 💼 Job Portal – Backend

RESTful API backend for a full-stack job portal built using Spring Boot.  
Provides authentication, job management, and role-based access control.

🔗 **Live API:** https://job-portal-backend-aagn.onrender.com

---

## 🚀 Features

- JWT Authentication & Authorization
- Role-Based Access Control (Job Seeker / Recruiter)
- Job CRUD operations
- User profile management
- Secure REST APIs

---

## 🛠 Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- MySQL
- Maven

---

## 🏗 Architecture

Client (React) → REST API (Spring Boot) → MySQL Database

---

## 📊 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /users | Register user |
| POST | /users/login | Login user |
| GET | /jobs | Get all jobs |
| POST | /jobs | Create job |
| PUT | /jobs/{id} | Update job |
| DELETE | /jobs/{id} | Delete job |

---

## 🗄 Database Schema (Simplified)

**Users**
- id
- name
- email
- password
- role

**Jobs**
- id
- title
- description
- company
- location
