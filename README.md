# 🗂️ Real-Time Project & Task Management Tool

A full-stack, real-time project management application (mini Jira/Trello clone) built with **Spring Boot** and **React**. Supports multi-user collaboration with live Kanban boards, drag-and-drop task management, and instant WebSocket-based synchronization across sessions.

## 🚀 Live Demo

- **Frontend (App):** [https://taskmanager-five-mauve.vercel.app](https://taskmanager-five-mauve.vercel.app)
- **Backend (API):** [https://taskmanager-backend-ui72.onrender.com](https://taskmanager-backend-ui72.onrender.com)

> ⚠️ Note: The backend is hosted on Render's free tier, which spins down after periods of inactivity. The **first request after idle time may take 30–60 seconds** to respond while the server wakes up — this is expected behavior, not a bug.

### Try it out
1. Visit the live app link above
2. Register a new account
3. Create a project — a Kanban board (To Do / In Progress / Done) is generated automatically
4. Add tasks, drag them across columns, and open a second browser tab to see real-time sync in action

---

## ✨ Features

- 🔐 **JWT Authentication** — secure register/login flow with token-based session management
- 📋 **Kanban Board** — auto-generated columns (To Do, In Progress, Done) per project
- 🖱️ **Drag-and-Drop** — smooth task reordering and column transitions using `@dnd-kit`
- ⚡ **Real-Time Sync** — WebSocket (STOMP over SockJS) broadcasts task updates instantly across all connected clients
- 💬 **Task Details & Comments** — modal view with comment thread and task deletion
- 👥 **Project Membership & Roles** — Admin / Manager / Member roles per project
- 🔔 **Notifications** — backend support for task-related notifications

---

## 🛠️ Tech Stack

### Backend
- **Java 17** + **Spring Boot 4**
- **Spring Security** with JWT authentication
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** (relational database)
- **WebSocket** (STOMP protocol) for real-time updates
- **Docker** for containerized deployment

### Frontend
- **React** (Vite)
- **Tailwind CSS** for styling
- **Axios** for API communication
- **React Router** for navigation
- **@dnd-kit** for drag-and-drop interactions
- **@stomp/stompjs** + **sockjs-client** for WebSocket connectivity

### Deployment
- **Backend:** Render (Docker container + managed PostgreSQL)
- **Frontend:** Vercel

---

## 📐 Architecture

```
┌─────────────────┐         HTTPS / REST          ┌──────────────────┐
│                  │ ─────────────────────────────>│                  │
│  React Frontend  │                                │  Spring Boot API │
│    (Vercel)      │ <───────────────────────────── │    (Render)      │
│                  │        JWT Auth                │                  │
└─────────────────┘                                 └────────┬─────────┘
        │                                                     │
        │            WebSocket (STOMP/SockJS)                 │
        └─────────────────────────────────────────────────────┤
                          Real-time task updates                │
                                                                 ▼
                                                        ┌──────────────────┐
                                                        │   PostgreSQL DB   │
                                                        │    (Render)       │
                                                        └──────────────────┘
```

---

## 💻 Running Locally

### Prerequisites
- Java 17+
- Node.js v18+ and npm
- PostgreSQL (running locally, or update `application.properties` to point elsewhere)
- Maven (or use the included `mvnw` wrapper)

### Backend Setup

```bash
cd taskmanager

# Configure your local PostgreSQL credentials in
# src/main/resources/application.properties (or set env vars)

./mvnw spring-boot:run
```

The backend will start on `http://localhost:8081`.

### Frontend Setup

```bash
cd frontend
npm install

# Create a .env file with:
# VITE_API_URL=http://localhost:8081/api
# VITE_WS_URL=http://localhost:8081/ws

npm run dev
```

The frontend will start on `http://localhost:5173`.

---

## 📄 Environment Variables

### Backend
| Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/taskmanager_db` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | — |
| `JWT_SECRET` | Secret key for signing JWTs | — |
| `JWT_EXPIRATION` | Token expiry (ms) | `86400000` |
| `PORT` | Server port | `8081` |

### Frontend
| Variable | Description |
|---|---|
| `VITE_API_URL` | Base URL for REST API |
| `VITE_WS_URL` | Base URL for WebSocket endpoint |

---

## 📌 Project Structure

```
taskmanager.Project/
├── taskmanager/        # Spring Boot backend
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
└── frontend/            # React frontend
    ├── src/
    ├── package.json
    └── vercel.json
```

---

## 👤 Author

**Lingraj**
Java Full Stack Developer
