import re
from typing import Literal

from pydantic import BaseModel, EmailStr, field_validator

GenderType = Literal["male", "female", "other", "prefer_not_to_say"]

class UserBase(BaseModel):
    name: str
    email: EmailStr
    phone_number: str
    city: str | None = None
    bio: str | None = None
    gender: GenderType


class UserCreate(UserBase):
    password: str

    @field_validator("name")
    @classmethod
    def name_must_be_valid(cls, v: str) -> str:
        v = v.strip()
        if len(v) < 2:
            raise ValueError("Name must be at least 2 characters long")
        if len(v) > 50:
            raise ValueError("Name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", v):
            raise ValueError("Name can only contain letters and spaces")
        return v

    @field_validator("email")
    @classmethod
    def email_must_be_valid(cls, v: str) -> str:
        v = v.strip().lower()
        if len(v) > 100:
            raise ValueError("Email must be at most 100 characters long")
        allowed_domains = ["gmail.com", "yahoo.com", "outlook.com", "hotmail.com"]
        domain = v.split("@")[-1] if "@" in v else ""
        if domain not in allowed_domains:
            raise ValueError("Email must be from Gmail, Yahoo, Outlook, or Hotmail")
        return v

    @field_validator("phone_number")
    @classmethod
    def phone_must_be_valid(cls, v: str) -> str:
        v = v.strip()
        if not re.match(r"^[6-9]\d{9}$", v):
            raise ValueError("Enter a valid 10-digit Indian mobile number starting with 6-9")
        return v

    @field_validator("city")
    @classmethod
    def city_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) > 50:
            raise ValueError("City name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", v):
            raise ValueError("City name can only contain letters and spaces")
        return v

    @field_validator("bio")
    @classmethod
    def bio_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) > 200:
            raise ValueError("Bio must be at most 200 characters long")
        return v

    @field_validator("password")
    @classmethod
    def password_must_be_strong(cls, v: str) -> str:
        if len(v) < 8:
            raise ValueError("Password must be at least 8 characters long")
        if not any(c.isupper() for c in v):
            raise ValueError("Password must contain at least one uppercase letter")
        if not any(c.isdigit() for c in v):
            raise ValueError("Password must contain at least one number")
        if not any(not c.isalnum() for c in v):
            raise ValueError("Password must contain at least one special character")
        return v


# --- Update profile request (keeping all fields optional) ---
class UserUpdate(BaseModel):
    name: str | None = None
    phone_number: str | None = None
    city: str | None = None
    bio: str | None = None
    gender: GenderType | None = None

    @field_validator("name")
    @classmethod
    def name_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) < 2:
            raise ValueError("Name must be at least 2 characters long")
        if len(v) > 50:
            raise ValueError("Name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", v):
            raise ValueError("Name can only contain letters and spaces")
        return v

    @field_validator("phone_number")
    @classmethod
    def phone_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if not re.match(r"^[6-9]\d{9}$", v):
            raise ValueError("Enter a valid 10-digit Indian mobile number starting with 6-9")
        return v

    @field_validator("city")
    @classmethod
    def city_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) > 50:
            raise ValueError("City name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", v):
            raise ValueError("City name can only contain letters and spaces")
        return v

    @field_validator("bio")
    @classmethod
    def bio_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) > 200:
            raise ValueError("Bio must be at most 200 characters long")
        return v


class UserOut(UserBase):
    id: int
    model_config = {"from_attributes": True}


class UserContact(BaseModel):
    name: str
    phone_number: str | None = None
    model_config = {"from_attributes": True}
