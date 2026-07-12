from contextlib import asynccontextmanager
import os

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles

from app.core.database import Base, engine
from app.core.exceptions import CircleUpError
from app.core.logger import setup_logging
from app.routers import auth, users, activity, participation
from app.core.logger import logger

# ---lifcycle of app---
@asynccontextmanager
async def lifespan(app: FastAPI):
    setup_logging()
    logger.info("Starting up CircleUp API...")
    Base.metadata.create_all(bind=engine)
    yield
    logger.info("Shutting down CircleUp API...")
    engine.dispose()
    logger.info("Database connection pool closed.")

# Initialize the FastAPI application
app = FastAPI(
    title="CircleUp API",
    description="Discover and organize social activities",
    version="1.0.0",
    lifespan=lifespan,
)


# ---exception handling---
@app.exception_handler(CircleUpError)
async def circleup_exception_handler(request: Request, exc: CircleUpError):
    return JSONResponse(
        status_code=exc.status_code,
        content={"detail": exc.message},
    )


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ---routes---
app.include_router(auth.router, prefix="/auth", tags=["Auth"])
app.include_router(users.router, prefix="/users", tags=["Users"])
app.include_router(activity.router, prefix="/activities", tags=["Activities"])
app.include_router(participation.router, prefix="/participation", tags=["Participation"])

# static file for loading frontend from same FASTAPI server and port
BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
FRONTEND_DIR = os.path.join(BASE_DIR, "frontend")
app.mount("/frontend", StaticFiles(directory=FRONTEND_DIR), name="frontend")


@app.get("/", tags=["Health"])
def health_check():
    return {"status": "ok", "app": "CircleUp"}