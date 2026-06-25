from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.core.database import Base, engine
from app.core.logger import setup_logging
import app.models  # this one line loads ALL models via __init__.py


@asynccontextmanager
async def lifespan(app: FastAPI):
    setup_logging()
    Base.metadata.create_all(bind=engine)
    yield


app = FastAPI(
    title="CircleUp API",
    description="Discover and organize social activities",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/", tags=["Health"])
def health_check():
    return {"status": "ok", "app": "CircleUp"}