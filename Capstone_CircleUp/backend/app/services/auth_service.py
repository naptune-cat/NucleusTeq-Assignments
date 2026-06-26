from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core.security import hash_password, verify_password, create_access_token
from app.core.logger import logger
from app.models.user import User
from app.schemas.user import UserCreate


def get_user_by_email(db: Session, email: str) -> User | None:
    return db.query(User).filter(User.email == email).first()


def get_user_by_id(db: Session, user_id: int) -> User | None:
    return db.query(User).filter(User.id == user_id).first()


def register_user(db: Session, data: UserCreate) -> User:
    # email must be unique
    if get_user_by_email(db, data.email):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email already registered",
        )

    user = User(
        name=data.name,
        email=data.email,
        hashed_password=hash_password(data.password),
        phone_number=data.phone_number,
        city=data.city,
        bio=data.bio,
        gender=data.gender,
    )
    db.add(user)
    db.commit()
    db.refresh(user)  # populates user.id from DB
    logger.info(f"New user registered: {user.email}")
    return user


def login_user(db: Session, email: str, password: str) -> str:
    user = get_user_by_email(db, email)

    # I am deliberately returning vague error message for security reasons
    if not user or not verify_password(password, user.hashed_password):
        logger.warning(f"Failed login attempt for email: {email}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password",
        )

    # sub = subject — it is a JWT claim, we store user id as string
    token = create_access_token({"sub": str(user.id)})
    logger.info(f"User logged in: {user.email}")
    return token
