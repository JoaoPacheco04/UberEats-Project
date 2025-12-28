# 🚀 EduScrum Management System (UberEats-Project)

A high-performance Fullstack application designed to manage Agile methodologies (Scrum) in educational environments. This is my **main project**, featuring a robust **Spring Boot API** and a dynamic **React** frontend.

## 🌟 Project Overview

This platform allows teachers and students to manage the entire Scrum lifecycle, from course enrollment to Sprint execution and performance analytics. It combines complex business logic with a modern user experience.

## 🚀 Key Features

- **🛡️ Secure Authentication:** Full implementation of **Spring Security** with **JWT (JSON Web Tokens)** for stateless authentication.
- **📊 Teacher Analytics:** Advanced dashboard for teachers to monitor team progress, sprint velocity, and student achievements.
- **🏃 Sprint Management:** Complete module for planning, executing, and closing Sprints, including a dynamic **Sprint Board**.
- **📋 User Story Lifecycle:** Manage product backlogs with priority levels, story points, and status tracking (To Do, Doing, Done).
- **🏆 Achievement System:** Gamification engine that awards **Badges** to students based on their performance and milestones.
- **👥 Team Collaboration:** Role-based management (Scrum Master, Product Owner, Development Team) with real-time membership control.

## 🛠️ Technical Stack

### Backend (The Core)
- **Java 17** & **Spring Boot 3**
- **Spring Security & JWT** (Authentication & Authorization)
- **Spring Data JPA** (Persistence layer)
- **MySQL / PostgreSQL** (Relational Database)
- **Maven** (Project Management)
- **JUnit 5 & Mockito** (Unit and Integration Testing)

### Frontend (The Experience)
- **React.js** (Vite-based)
- **Tailwind CSS** (Modern styling)
- **Axios** (API communication)
- **React Router Dom** (Navigation)

## 🏗️ Architecture & Best Practices

- **RESTful API Design:** Clean and predictable endpoints following HATEOAS principles where applicable.
- **Global Exception Handling:** Centralized management of application errors for consistent API responses.
- **DTO Pattern:** Decoupling of internal entities from external API contracts to ensure security and flexibility.
- **Clean Code:** Strong adherence to SOLID principles and DRY (Don't Repeat Yourself).

## 🔧 How to Run

### Backend
1. Configure your database in `src/main/resources/application.properties`.
2. Run `./mvnw spring-boot:run`.
3. The API will be available at `http://localhost:8080`.

### Frontend
1. Navigate to `ubereats-frontend/`.
2. Install dependencies: `npm install`.
3. Start the development server: `npm run dev`.

---
*Note: This project was developed as part of the Computer Engineering degree at UPT.*
