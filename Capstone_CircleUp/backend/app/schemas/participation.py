from datetime import datetime

from pydantic import BaseModel

from app.models.participation import RequestStatus


class ParticipationRequestOut(BaseModel):
    id: int
    activity_id: int
    requester_id: int
    status: RequestStatus
    requested_at: datetime

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

    # Contact details are included only after approval
    contact: ContactOut | None = None

    # Allows Pydantic to read values from SQLAlchemy objects
    model_config = {"from_attributes": True}