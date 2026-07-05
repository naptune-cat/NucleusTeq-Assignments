from datetime import datetime
from sqlalchemy.orm import Session

from app.core.exceptions import BadRequestError, NotFoundError, PermissionDeniedError
from app.core.logger import logger
from app.enums.activity import ActivityStatus
from app.enums.participation import RequestStatus
from app.models.activity import Activity
from app.models.user import User
from app.schemas.activity import ActivityCreate, ActivityUpdate
from app.repositories.activity_repository import (
    get_activity_by_id as repo_get_activity,
    get_all_activities as repo_get_all_activities,
    get_activities_by_creator as repo_get_activities_by_creator,
    browse_activities as repo_browse_activities,
    create_activity as repo_create_activity,
    update_activity as repo_update_activity,
    delete_activity as repo_delete_activity,
)
from app.models.participation import ParticipationRequest


def create_activity(db: Session, data: ActivityCreate, current_user: User) -> Activity:
    """Create a new activity and save it to the database."""
    activity = Activity(
        title=data.title,
        description=data.description,
        category=data.category,
        location=data.location,
        max_participants=data.max_participants,
        activity_date=data.activity_date,
        gender_filter=data.gender_filter,
        creator_id=current_user.id,
        status=ActivityStatus.open,  # all new activities start as open
    )
    created_activity = repo_create_activity(db, activity)
    logger.info(f"Activity created: '{created_activity.title}' by user {current_user.id}")
    return created_activity


def get_activity_by_id(db: Session, activity_id: int) -> Activity:
    """Fetch a single activity by ID. Raises NotFoundError if not found."""
    activity = repo_get_activity(db, activity_id)
    if not activity:
        raise NotFoundError("Activity not found")
    return activity


def get_all_activities(db: Session) -> list[Activity]:
    """Return all non-cancelled activities."""
    return repo_get_all_activities(db)


def get_my_activities(db: Session, current_user: User) -> list[Activity]:
    """Return activities created by the current user."""
    return repo_get_activities_by_creator(db, current_user.id)


def browse_activities(
    db: Session,
    current_user: User,
    category: str | None = None,
    location: str | None = None,
    date_from: datetime | None = None,
    date_to: datetime | None = None,
) -> list[Activity]:
    """Return filtered activities visible to the current user based on their gender."""
    return repo_browse_activities(
        db,
        category=category,
        location=location,
        date_from=date_from,
        date_to=date_to,
        current_user_gender=current_user.gender,
    )


def update_activity(
    db: Session, activity_id: int, data: ActivityUpdate, current_user: User
) -> Activity:
    """Update an activity's fields. Only the activity creator is allowed to do this."""
    activity = get_activity_by_id(db, activity_id)

    if activity.creator_id != current_user.id:
        raise PermissionDeniedError("You are not the creator of this activity")

    update_data = data.model_dump(exclude_unset=True)
    if not update_data:
        raise BadRequestError("No fields provided to update")

    if "max_participants" in update_data and update_data["max_participants"] is not None:
        from app.repositories.participation_repository import count_approved
        approved_count = count_approved(db, activity_id)
        if update_data["max_participants"] < approved_count:
            raise BadRequestError(f"Cannot reduce capacity below currently approved participant count ({approved_count})")

    for field, value in update_data.items():
        setattr(activity, field, value)

    updated_activity = repo_update_activity(db, activity)
    logger.info(f"Activity {activity_id} updated by user {current_user.id}")
    return updated_activity


def delete_activity(db: Session, activity_id: int, current_user: User) -> None:
    """
    Soft-delete an activity by marking it as cancelled.
    All pending requests are automatically rejected when this happens.
    Only the activity creator can delete their own activity.
    """


    activity = get_activity_by_id(db, activity_id)

    if activity.creator_id != current_user.id:
        raise PermissionDeniedError("You are not the creator of this activity")

    # Reject all pending participation requests before cancelling
    requests = db.query(ParticipationRequest).filter(
        ParticipationRequest.activity_id == activity_id
    ).all()
    for req in requests:
        req.status = RequestStatus.rejected

    activity.status = ActivityStatus.cancelled
    repo_delete_activity(db, activity)
    logger.info(f"Activity {activity_id} cancelled by user {current_user.id}")