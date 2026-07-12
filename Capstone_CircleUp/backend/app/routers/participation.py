from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.dependencies import get_current_user
from app.models.user import User
from app.schemas.participation import ContactOut, ParticipationRequestOut
from app.services.participation_service import (
    approve_request,
    get_activity_requests,
    get_contact,
    get_my_requests,
    lazy_complete_activity,
    reject_request,
    request_to_join,
)

# Router for participation-related endpoints
router = APIRouter()


# Send a request to join an activity
@router.post("/{activity_id}/request", response_model=ParticipationRequestOut, status_code=status.HTTP_201_CREATED)
def join_request(
    activity_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return request_to_join(db, activity_id, current_user)


# Get all participation requests made by the current user
@router.get("/mine", response_model=list[ParticipationRequestOut])
def my_requests(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return get_my_requests(db, current_user)


# Get all participation requests for an activity
@router.get("/activity/{activity_id}", response_model=list[ParticipationRequestOut])
def activity_requests(
    activity_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    # Update activity status if it has already ended
    lazy_complete_activity(db, activity_id)
    return get_activity_requests(db, activity_id, current_user)


# Approve a participation request
@router.put("/{request_id}/approve", response_model=ParticipationRequestOut)
def approve(
    request_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return approve_request(db, request_id, current_user)


# Reject a participation request
@router.put("/{request_id}/reject", response_model=ParticipationRequestOut)
def reject(
    request_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return reject_request(db, request_id, current_user)


# Get contact details after request approval
@router.get("/{request_id}/contact", response_model=ContactOut)
def contact(
    request_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    user = get_contact(db, request_id, current_user)
    return ContactOut(name=user.name, phone_number=user.phone_number)