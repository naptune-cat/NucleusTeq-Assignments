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
    def title_must_be_valid(cls, v: str) -> str:
        v = v.strip()
        if len(v) < 3:
            raise ValueError("Title must be at least 3 characters long")
        if len(v) > 200:
            raise ValueError("Title must be at most 200 characters long")
        return v

    @field_validator("description")
    @classmethod
    def description_must_be_valid(cls, v: str) -> str:
        v = v.strip()
        if len(v) < 10:
            raise ValueError("Description must be at least 10 characters long")
        return v

    @field_validator("category")
    @classmethod
    def category_must_be_valid(cls, v: str) -> str:
        v = v.strip()
        allowed = ["Sports", "Art", "Wellness", "Food", "Outdoors", "Learning", "Social", "Music"]
        if v not in allowed:
            raise ValueError(f"Category must be one of: {', '.join(allowed)}")
        return v

    @field_validator("location")
    @classmethod
    def location_must_be_valid(cls, v: str) -> str:
        v = v.strip()
        if len(v) < 2:
            raise ValueError("Location must be at least 2 characters long")
        if len(v) > 200:
            raise ValueError("Location must be at most 200 characters long")
        return v

    @field_validator("max_participants")
    @classmethod
    def must_be_positive(cls, v: int) -> int:
        if v <= 0:
            raise ValueError("max_participants must be greater than zero")
        if v > 1000:
            raise ValueError("max_participants cannot exceed 1000")
        return v

    @field_validator("activity_date")
    @classmethod
    def must_be_future(cls, v: datetime) -> datetime:
        if v <= datetime.now(v.tzinfo):
            raise ValueError("Activity must be scheduled for a future date and time")
        return v


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
    def title_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) < 3:
            raise ValueError("Title must be at least 3 characters long")
        if len(v) > 200:
            raise ValueError("Title must be at most 200 characters long")
        return v

    @field_validator("description")
    @classmethod
    def description_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) < 10:
            raise ValueError("Description must be at least 10 characters long")
        return v

    @field_validator("category")
    @classmethod
    def category_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        allowed = ["Sports", "Art", "Wellness", "Food", "Outdoors", "Learning", "Social", "Music"]
        if v not in allowed:
            raise ValueError(f"Category must be one of: {', '.join(allowed)}")
        return v

    @field_validator("location")
    @classmethod
    def location_must_be_valid(cls, v: str | None) -> str | None:
        if v is None:
            return v
        v = v.strip()
        if len(v) < 2:
            raise ValueError("Location must be at least 2 characters long")
        if len(v) > 200:
            raise ValueError("Location must be at most 200 characters long")
        return v

    @field_validator("max_participants")
    @classmethod
    def must_be_positive(cls, v: int | None) -> int | None:
        if v is not None and v <= 0:
            raise ValueError("max_participants must be greater than zero")
        if v is not None and v > 1000:
            raise ValueError("max_participants cannot exceed 1000")
        return v

    @field_validator("activity_date")
    @classmethod
    def must_be_future(cls, v: datetime | None) -> datetime | None:
        if v is not None and v <= datetime.now(v.tzinfo):
            raise ValueError("Activity must be scheduled for a future date and time")
        return v


class ActivityOut(ActivityBase):
    id: int
    status: ActivityStatus
    creator_id: int
    participants_count: int = 0

    model_config = {"from_attributes": True}