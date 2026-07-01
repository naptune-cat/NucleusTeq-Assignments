from contextlib import asynccontextmanager
import os

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from app.core.database import Base, engine
from app.core.logger import setup_logging
from app.routers import auth, users, activity, participation


# Run startup tasks when the application starts
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Configure application logging
    setup_logging()

    # Create database tables if they do not exist
    Base.metadata.create_all(bind=engine)

    yield


# Create the FastAPI application
app = FastAPI(
    title="CircleUp API",
    description="Discover and organize social activities",
    version="1.0.0",
    lifespan=lifespan,
)


# Configure Cross-Origin Resource Sharing (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Register application routers
app.include_router(auth.router, prefix="/auth", tags=["Auth"])
app.include_router(users.router, prefix="/users", tags=["Users"])
app.include_router(activity.router, prefix="/activities", tags=["Activities"])
app.include_router(participation.router, prefix="/participation", tags=["Participation"])


# Path to the frontend directory
BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
FRONTEND_DIR = os.path.join(BASE_DIR, "frontend")

# Serve frontend static files
app.mount("/frontend", StaticFiles(directory=FRONTEND_DIR), name="frontend")


# Health check endpoint
@app.get("/", tags=["Health"])
def health_check():
    return {"status": "ok", "app": "CircleUp"}