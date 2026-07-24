import re
from datetime import datetime
from typing import Literal

from pydantic import BaseModel, field_validator

from app.enums.activity import ActivityStatus  

GenderFilter = Literal["all", "female_only"]


class ActivityBase(BaseModel):
    title: str
    description: str
    category: str
    location: str
    max_participants: int
    activity_date: datetime
    gender_filter: GenderFilter = "all"


class ActivityCreate(ActivityBase):

    @field_validator("title")
    @classmethod
    def title_must_be_valid(cls, value: str) -> str:
        value = value.strip()
        if len(value) < 3:
            raise ValueError("Title must be at least 3 characters long")
        if len(value) > 200:
            raise ValueError("Title must be at most 200 characters long")
        return value

    @field_validator("description")
    @classmethod
    def description_must_be_valid(cls, value: str) -> str:
        value = value.strip()
        if len(value) < 10:
            raise ValueError("Description must be at least 10 characters long")
        return value

    @field_validator("category")
    @classmethod
    def category_must_be_valid(cls, value: str) -> str:
        value = value.strip()
        allowed = ["Sports", "Art", "Wellness", "Food", "Outdoors", "Learning", "Social", "Music"]
        if value not in allowed:
            raise ValueError(f"Category must be one of: {', '.join(allowed)}")
        return value

    @field_validator("location")
    @classmethod
    def location_must_be_valid(cls, value: str) -> str:
        value = value.strip()
        if len(value) < 2:
            raise ValueError("Location must be at least 2 characters long")
        if len(value) > 200:
            raise ValueError("Location must be at most 200 characters long")
        return value

    @field_validator("max_participants")
    @classmethod
    def must_be_positive(cls, value: int) -> int:
        if value <= 0:
            raise ValueError("max_participants must be greater than zero")
        if value > 1000:
            raise ValueError("max_participants cannot exceed 1000")
        return value

    @field_validator("activity_date")
    @classmethod
    def must_be_future(cls, value: datetime) -> datetime:
        if value <= datetime.now(value.tzinfo):
            raise ValueError("Activity must be scheduled for a future date and time")
        return value


class ActivityUpdate(BaseModel):
    title: str | None = None
    description: str | None = None
    category: str | None = None
    location: str | None = None
    max_participants: int | None = None
    activity_date: datetime | None = None
    gender_filter: GenderFilter | None = None

    @field_validator("title")
    @classmethod
    def title_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) < 3:
            raise ValueError("Title must be at least 3 characters long")
        if len(value) > 200:
            raise ValueError("Title must be at most 200 characters long")
        return value

    @field_validator("description")
    @classmethod
    def description_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) < 10:
            raise ValueError("Description must be at least 10 characters long")
        return value

    @field_validator("category")
    @classmethod
    def category_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        allowed = ["Sports", "Art", "Wellness", "Food", "Outdoors", "Learning", "Social", "Music"]
        if value not in allowed:
            raise ValueError(f"Category must be one of: {', '.join(allowed)}")
        return value

    @field_validator("location")
    @classmethod
    def location_must_be_valid(cls, value: str | None) -> str | None:
        if value is None:
            return value
        value = value.strip()
        if len(value) < 2:
            raise ValueError("Location must be at least 2 characters long")
        if len(value) > 200:
            raise ValueError("Location must be at most 200 characters long")
        return value

    @field_validator("max_participants")
    @classmethod
    def must_be_positive(cls, value: int | None) -> int | None:
        if value is not None and value <= 0:
            raise ValueError("max_participants must be greater than zero")
        if value is not None and value > 1000:
            raise ValueError("max_participants cannot exceed 1000")
        return value

    @field_validator("activity_date")
    @classmethod
    def must_be_future(cls, value: datetime | None) -> datetime | None:
        if value is not None and value <= datetime.now(value.tzinfo):
            raise ValueError("Activity must be scheduled for a future date and time")
        return value


class ActivityOut(ActivityBase):
    id: int
    status: ActivityStatus
    creator_id: int
    creator_name: str | None = None
    participants_count: int = 0

    model_config = {"from_attributes": True}