# CircleUp Capstone Project

CircleUp is a platform that helps users discover and organize social activities. Users can create activity invitations, browse activities created by others, request participation, and connect with participants once approved.

## Technology Stack

- **Backend**: Python, FastAPI
- **Frontend**: HTML, CSS, JavaScript (Vanilla)
- **Database**: PostgreSQL
- **API Documentation**: Swagger / OpenAPI

## Features

- **User Management**: Register, Login, Profile Management. Authentication via JWT.
- **Activity Management**: Create, View, Edit (own), Cancel (own).
- **Activity Discovery**: Browse and filter activities by Category, Location, Date.
- **Participation**: Request to join, Approve/Reject requests.
- **Capacity Management**: Automatic full status, prevent over-capacity.

## Setup Instructions

### Backend Setup

1. **Navigate to the backend directory**:
   ```bash
   cd backend
   ```

2. **Create a virtual environment**:
   ```bash
   python -m venv .venv
   ```

3. **Activate the virtual environment**:
   - On Windows:
     ```bash
     .venv\Scripts\activate
     ```
   - On macOS/Linux:
     ```bash
     source .venv/bin/activate
     ```

4. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

5. **Database Configuration**:
   - Ensure you have PostgreSQL installed and running.
   - Update the `.env` file with your database credentials:
     ```env
     DATABASE_URL=postgresql://user:password@localhost/circleup
     ```

6. **Run Database Migrations**:
   ```bash
   alembic upgrade head
   ```

7. **Start the API Server**:
   ```bash
   uvicorn app.main:app --reload
   ```

### Frontend Setup

1. Open the "http://localhost:8000/frontend/component/index.html" page in your browser.

## API Documentation

Once the backend server is running, the Swagger UI API documentation is available at:
- `http://localhost:8000/docs`

## Testing

Unit tests are included for validation rules, capacity logic, and authentication checks.

To run the tests with coverage, ensure your virtual environment is active and run:
```bash
cd backend
pytest --cov
```

##  Documentation

The project documentation is available in the repository:


-  [Project Presentation](docs/MCP-2026_CircleUp-ZoyaKhanam.pptx)
-  [Swagger API Documentation](docs/swaggerDocs.pdf)
-  [Database Schema](database_schema.md)
-  [System Architecture](architecture.md)
