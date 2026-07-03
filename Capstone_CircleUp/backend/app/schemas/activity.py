from datetime import datetime
from typing import Literal

from pydantic import BaseModel, field_validator

from app.models.activity import ActivityStatus

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

    @field_validator("max_participants")
    @classmethod
    def must_be_positive(cls, v: int) -> int:
        if v <= 0:
            raise ValueError("max_participants must be greater than zero")
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

    @field_validator("max_participants")
    @classmethod
    def must_be_positive(cls, v: int | None) -> int | None:
        if v is not None and v <= 0:
            raise ValueError("max_participants must be greater than zero")
        return v


class ActivityOut(ActivityBase):
    id: int
    status: ActivityStatus
    creator_id: int
<<<<<<< Updated upstream
=======
    creator_name: str | None = None
    participants_count: int = 0
>>>>>>> Stashed changes

    model_config = {"from_attributes": True}