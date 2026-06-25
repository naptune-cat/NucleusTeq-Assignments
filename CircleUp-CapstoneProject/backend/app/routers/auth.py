from fastapi import APIRouter, Depends
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.schemas.token import Token
from app.schemas.user import UserCreate, UserOut
from app.services.auth_service import register_user, login_user

router = APIRouter()


@router.post("/register", response_model=UserOut, status_code=201)
def register(data: UserCreate, db: Session = Depends(get_db)):
    return register_user(db, data)


# OAuth2PasswordRequestForm is a FastAPI built-in that gives us
# username + password from a form body — Swagger's Authorize button uses this
@router.post("/login", response_model=Token)
def login(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db),
):
    token = login_user(db, form_data.username, form_data.password)
    return {"access_token": token, "token_type": "bearer"}