from datetime import datetime

from pydantic import BaseModel

from app.enums.participation import RequestStatus  


# Response schema for all participation requests
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

    # Allows Pydantic to read values from SQLAlchemy objects
    model_config = {"from_attributes": True}


# Response schema containing the requester's contact details
# Returned only when the participation request has been approved
class ContactOut(BaseModel):
    name: str
    phone_number: str

    # Allows Pydantic to read values from SQLAlchemy objects
    model_config = {"from_attributes": True}


# Response schema for a participation request along with
# the requester's contact information (if available)
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