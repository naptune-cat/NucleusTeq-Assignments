from pydantic import BaseModel, EmailStr, field_validator
from typing import Literal

GenderType = Literal["male", "female", "other", "prefer_not_to_say"]


# --- Base (shared fields) ---
class UserBase(BaseModel):
    name: str
    email: EmailStr
    phone_number: str | None = None
    city: str | None = None
    bio: str | None = None
    gender: GenderType | None = None 


# --- Register request body ---
class UserCreate(UserBase):
    password: str

    @field_validator("password")
    @classmethod
    def password_must_be_strong(cls, v: str) -> str:
        if len(v) < 8:
            raise ValueError("Password must be at least 8 characters long")
        if not any(char.isupper() for char in v):
            raise ValueError("Password must contain at least one uppercase letter")
        if not any(char.isdigit() for char in v):
            raise ValueError("Password must contain at least one number")
        if not any(not char.isalnum() for char in v):
            raise ValueError("Password must contain at least one special character")
        return v


# --- Update profile request (all fields optional) ---
class UserUpdate(BaseModel):
    name: str | None = None
    phone_number: str | None = None
    city: str | None = None
    bio: str | None = None
    gender: GenderType | None = None  


# --- Response ---
class UserOut(UserBase):
    id: int
    model_config = {"from_attributes": True} # lets pydantic read SQLAlchemy objects


# --- Contact info — only visible after participation approved ---
class UserContact(BaseModel):
    name: str
    phone_number: str | None = None
    model_config = {"from_attributes": True}