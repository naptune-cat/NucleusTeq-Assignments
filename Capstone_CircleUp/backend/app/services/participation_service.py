from datetime import datetime, timezone

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core.logger import logger
from app.models.activity import ActivityStatus
from app.models.participation import RequestStatus
from app.models.user import User
from app.repositories.activity_repository import (
    get_activity_by_id as repo_get_activity,
    update_activity as repo_update_activity,
)
from app.repositories.participation_repository import (
    count_approved,
    create_request,
    get_existing_request,
    get_request_by_id,
    get_requests_by_user,
    get_requests_for_activity,
    update_request_status,
)
from app.repositories.user_repository import get_user_by_id


# Create a participation request for an activity
def request_to_join(db: Session, activity_id: int, current_user: User):
    activity = repo_get_activity(db, activity_id)

    # User cannot join their own activity
    if activity.creator_id == current_user.id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="You cannot request to join your own activity",
        )

    # Activity must be open for new requests
    if activity.status != ActivityStatus.open:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Activity is {activity.status.value} and not accepting requests",
        )

    # Allow only female users for female-only activities
    if activity.gender_filter == "female_only" and current_user.gender != "female":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="This activity is for female participants only",
        )

    # Prevent duplicate participation requests
    if get_existing_request(db, activity_id, current_user.id):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="You have already requested to join this activity",
        )

    req = create_request(db, activity_id, current_user.id)
    logger.info(f"User {current_user.id} requested to join activity {activity_id}")
    return req


# Get all participation requests made by the current user
def get_my_requests(db: Session, current_user: User):
    return get_requests_by_user(db, current_user.id)


# Get all participation requests for an activity
def get_activity_requests(db: Session, activity_id: int, current_user: User):
    activity = repo_get_activity(db, activity_id)

    # Only the activity creator can view requests
    if activity.creator_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not the creator of this activity",
        )

    return get_requests_for_activity(db, activity_id)


# Approve a participation request
def approve_request(db: Session, request_id: int, current_user: User):
    req = get_request_by_id(db, request_id)
    if not req:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Request not found")

    activity = repo_get_activity(db, req.activity_id)

    # Only the activity creator can approve requests
    if activity.creator_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not the creator of this activity",
        )

    # Request must be pending
    if req.status != RequestStatus.pending:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Request is already {req.status.value}",
        )

    # Check if the activity has reached its participant limit
    approved_count = count_approved(db, req.activity_id)
    if approved_count >= activity.max_participants:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Activity is already at full capacity",
        )

    req = update_request_status(db, req, RequestStatus.approved)
    logger.info(f"Request {request_id} approved by user {current_user.id}")

    # Mark the activity as full when capacity is reached
    if count_approved(db, req.activity_id) >= activity.max_participants:
        activity.status = ActivityStatus.full
        repo_update_activity(db, activity)
        logger.info(f"Activity {activity.id} automatically set to full")

    return req


# Reject a participation request
def reject_request(db: Session, request_id: int, current_user: User):
    req = get_request_by_id(db, request_id)
    if not req:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Request not found")

    activity = repo_get_activity(db, req.activity_id)

    # Only the activity creator can reject requests
    if activity.creator_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not the creator of this activity",
        )

    # Request must be pending
    if req.status != RequestStatus.pending:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Request is already {req.status.value}",
        )

    req = update_request_status(db, req, RequestStatus.rejected)
    logger.info(f"Request {request_id} rejected by user {current_user.id}")
    return req


# Get contact details after a request is approved
def get_contact(db: Session, request_id: int, current_user: User):
    req = get_request_by_id(db, request_id)
    if not req:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Request not found")

    activity = repo_get_activity(db, req.activity_id)
    is_host = activity.creator_id == current_user.id
    is_requester = req.requester_id == current_user.id

    # Only the host or requester can access contact details
    if not is_host and not is_requester:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")

    # Contact details are available only after approval
    if req.status != RequestStatus.approved:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Contact is only visible after request is approved",
        )

    # Return the other participant's contact details
    if is_host:
        contact_user = get_user_by_id(db, req.requester_id)
    else:
        contact_user = get_user_by_id(db, activity.creator_id)

    if not contact_user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    return contact_user


# Automatically mark expired activities as completed
def lazy_complete_activity(db: Session, activity_id: int):
    activity = repo_get_activity(db, activity_id)
    now = datetime.now(timezone.utc)

    # Only open or full activities can be completed
    if activity.status in (ActivityStatus.open, ActivityStatus.full):
        activity_date = activity.activity_date

        # Convert naive datetime to UTC if needed
        if activity_date.tzinfo is None:
            activity_date = activity_date.replace(tzinfo=timezone.utc)

        # Automatically adding Complete status on the activity if its date has passed
        if activity_date < now:
            try:
                activity.status = ActivityStatus.completed
                repo_update_activity(db, activity)
                logger.info(f"Activity {activity_id} auto-completed")
            except Exception:
                # Roll back changes if an error occurs
                db.rollback()

    return activity