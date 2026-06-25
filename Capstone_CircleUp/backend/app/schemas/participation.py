from datetime import datetime

from pydantic import BaseModel

from app.models.participation import RequestStatus
from app.schemas.user import UserContact


# --- Request to join an activity (activity_id comes from the URL) ---
class ParticipationRequestCreate(BaseModel):
    activity_id: int


# --- Response ---
class ParticipationRequestOut(BaseModel):
    id: int
    activity_id: int
    requester_id: int
    status: RequestStatus
    requested_at: datetime

    model_config = {"from_attributes": True}


# --- Response with contact info visible (only visible if request approved) ---
class ParticipationRequestWithContact(ParticipationRequestOut):
    requester: UserContact  # phone visible only when approved