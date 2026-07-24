# CircleUp Capstone Project

CircleUp is a platform that helps users discover and organize social activities. Users can create activity invitations, browse activities created by others, request participation, and connect with participants once approved.

## Technology Stack

- **Backend:** Python, FastAPI
- **Frontend:** HTML, CSS, JavaScript (Vanilla)
- **Database:** PostgreSQL
- **Containerization:** Docker & Docker Compose
- **API Documentation:** Swagger / OpenAPI

---

## Features

- User Registration & JWT Authentication
- User Profile Management
- Create, Edit, and Cancel Activities
- Browse and Filter Activities
- Participation Requests
- Approve/Reject Participation Requests
- Capacity Management
- PostgreSQL Database Integration
- Dockerized Deployment

---

# Running the Project with Docker (Recommended)

## Prerequisites

- Docker Desktop
- Docker Compose

## Start the Application

From the project root, run:

```bash
docker compose up --build
```

This will:

- Build the FastAPI application image
- Start a PostgreSQL container
- Start the CircleUp backend
- Create the required Docker network
- Mount persistent database storage using Docker Volumes

## Stop the Application

```bash
docker compose down
```

To also remove the database volume:

```bash
docker compose down -v
```

---

## Application URLs

| Service | URL |
|---------|-----|
| API | http://localhost:8000 |
| Swagger Docs | http://localhost:8000/docs |
| ReDoc | http://localhost:8000/redoc |

---

# Running Without Docker

## Backend Setup

1. Navigate to the backend directory

```bash
cd backend
```

2. Create a virtual environment

```bash
python -m venv .venv
```

3. Activate it

Windows

```bash
.venv\Scripts\activate
```

macOS/Linux

```bash
source .venv/bin/activate
```

4. Install dependencies

```bash
pip install -r requirements.txt
```

5. Configure the database

Update the `.env` file:

```env
DATABASE_URL=postgresql://user:password@localhost/circleup
```

6. Run migrations

```bash
alembic upgrade head
```

7. Start the application

```bash
uvicorn app.main:app --reload
```

---

## Frontend

Open:

```
http://localhost:8000/frontend/component/index.html
```

---

# Testing

Run tests from the backend folder:

```bash
cd backend
pytest --cov
```

---

# Project Structure

```
CircleUp/
│
├── backend/
├── frontend/
├── docs/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── architecture.md
├── database_schema.md
└── README.md
```

---

# Documentation

- Project Presentation → `docs/MCP-2026_CircleUp-ZoyaKhanam.pdf`
- Swagger API Documentation → `docs/swaggerDocs.pdf`
- Database Schema → `database_schema.md`
- System Architecture → `architecture.md`

