# CircleUp Architecture Walkthrough

## 1. System Design & Core Components

CircleUp is designed using a classic client-server architecture with three main tiers:

- **Presentation Layer (Frontend)**: Developed with vanilla HTML, CSS, and JavaScript. It communicates with the backend asynchronously using the Fetch API.
- **Application Layer (Backend)**: Built with Python and FastAPI. It serves RESTful endpoints, handles business logic, authorization, and data validation.
- **Data Layer (Database)**: PostgreSQL is used as the relational database, accessed through SQLAlchemy ORM with Alembic for migrations.

## 2. Technical Strategy

### Backend
- **Framework**: FastAPI was chosen for its high performance, automatic Swagger UI documentation generation, and built-in type validation via Pydantic.
- **Authentication**: JSON Web Tokens (JWT) are used for stateless authentication. Passwords are encrypted via argon2.
- **Dependency Injection**: Used heavily to provide database sessions and current authenticated users to route handlers securely and efficiently.
- **Capacity Management**: Capacity checks are handled within atomic database transactions or lock-safe implementations to prevent over-subscription of participants in near-simultaneous approval attempts.

### Frontend
- **Vanilla Setup**: Built without any frameworks to ensure a low learning curve and simplicity, aligning with project requirements.
- **Modularity**: Logic is split across multiple JavaScript files (`auth.js`, `profile.js`, `browse.js`) to maintain clean code and separation of concerns.
- **Session Management**: JWTs are stored in LocalStorage. A global fetch interceptor is implemented to detect 401 Unauthorized responses and automatically redirect the user to the login screen, effectively handling session expiration.
