from datetime import datetime

from pydantic import BaseModel

from app.enums.participation import RequestStatus  


class ParticipationRequestOut(BaseModel):
    id: int
    activity_id: int
    requester_id: int
    status: RequestStatus
    requested_at: datetime
    requester_name: str | None = None
    activity_title: str | None = None
    activity_date: datetime | None = None
    activity_status: str | None = None
    host_name: str | None = None
    host_phone: str | None = None

    # Allows Pydantic to read values from SQLAlchemy objects
    model_config = {"from_attributes": True}


class ContactOut(BaseModel):
    name: str
    phone_number: str

    # Allows Pydantic to read values from SQLAlchemy objects
    model_config = {"from_attributes": True}


class ParticipationRequestWithContact(BaseModel):
    id: int
    activity_id: int
    requester_id: int
    status: RequestStatus
    requested_at: datetime
    requester_name: str | None = None
    activity_title: str | None = None
    activity_date: datetime | None = None
    activity_status: str | None = None

    # Contact details are included only after approval,
    # otherwise this field will be None
    contact: ContactOut | None = None

    # Allows Pydantic to read values from SQLAlchemy objects
    model_config = {"from_attributes": True}