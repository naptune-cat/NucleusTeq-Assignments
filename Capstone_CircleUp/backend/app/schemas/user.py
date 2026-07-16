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
    def name_must_be_valid(cls, value: str) -> str:
        value = value.strip()
        if len(value) < 2:
            raise ValueError("Name must be at least 2 characters long")
        if len(value) > 50:
            raise ValueError("Name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", value):
            raise ValueError("Name can only contain letters and spaces")
        return value

    @field_validator("email")
    @classmethod
    def email_must_be_valid(cls, value: str) -> str:
        value = value.strip().lower()
        if len(value) > 100:
            raise ValueError("Email must be at most 100 characters long")
        allowed_domains = ["gmail.com", "yahoo.com", "outlook.com", "hotmail.com"]
        domain = value.split("@")[-1] if "@" in value else ""
        if domain not in allowed_domains:
            raise ValueError("Email must be from Gmail, Yahoo, Outlook, or Hotmail")
        return value

    @field_validator("phone_number")
    @classmethod
    def phone_must_be_valid(cls, value: str) -> str:
        value = value.strip()
        if not re.match(r"^[6-9]\d{9}$", value):
            raise ValueError("Enter a valid 10-digit Indian mobile number starting with 6-9")
        return value

    @field_validator("city")
    @classmethod
    def city_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) > 50:
            raise ValueError("City name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", value):
            raise ValueError("City name can only contain letters and spaces")
        return value

    @field_validator("bio")
    @classmethod
    def bio_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) > 200:
            raise ValueError("Bio must be at most 200 characters long")
        return value

    @field_validator("password")
    @classmethod
    def password_must_be_strong(cls, value: str) -> str:
        if len(value) < 8:
            raise ValueError("Password must be at least 8 characters long")
        if not any(c.isupper() for c in value):
            raise ValueError("Password must contain at least one uppercase letter")
        if not any(c.isdigit() for c in value):
            raise ValueError("Password must contain at least one number")
        if not any(not c.isalnum() for c in value):
            raise ValueError("Password must contain at least one special character")
        return value


# --- Update profile request (keeping all fields optional) ---
class UserUpdate(BaseModel):
    name: str | None = None
    phone_number: str | None = None
    city: str | None = None
    bio: str | None = None

    @field_validator("name")
    @classmethod
    def name_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) < 2:
            raise ValueError("Name must be at least 2 characters long")
        if len(value) > 50:
            raise ValueError("Name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", value):
            raise ValueError("Name can only contain letters and spaces")
        return value

    @field_validator("phone_number")
    @classmethod
    def phone_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if not re.match(r"^[6-9]\d{9}$", value):
            raise ValueError("Enter a valid 10-digit Indian mobile number starting with 6-9")
        return value

    @field_validator("city")
    @classmethod
    def city_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) > 50:
            raise ValueError("City name must be at most 50 characters long")
        if not re.match(r"^[A-Za-z ]+$", value):
            raise ValueError("City name can only contain letters and spaces")
        return value

    @field_validator("bio")
    @classmethod
    def bio_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) > 200:
            raise ValueError("Bio must be at most 200 characters long")
        return value


class UserOut(UserBase):
    id: int
    model_config = {"from_attributes": True}


class UserContact(BaseModel):
    name: str
    phone_number: str | None = None
    model_config = {"from_attributes": True}
