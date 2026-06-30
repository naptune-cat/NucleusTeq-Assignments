from datetime import datetime
from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.activity import Activity, ActivityStatus
from app.models.user import User
from app.schemas.activity import ActivityCreate, ActivityUpdate
from app.core.logger import logger
from app.repositories.activity_repository import (
    get_activity_by_id as repo_get_activity,
    get_all_activities as repo_get_all_activities,
    browse_activities as repo_browse_activities,
    create_activity as repo_create_activity,
    update_activity as repo_update_activity,
    delete_activity as repo_delete_activity,
)


def create_activity(db: Session, data: ActivityCreate, current_user: User) -> Activity:
    activity = Activity(
        title=data.title,
        description=data.description,
        category=data.category,
        location=data.location,
        max_participants=data.max_participants,
        activity_date=data.activity_date,
        gender_filter=data.gender_filter,
        creator_id=current_user.id,
        status=ActivityStatus.open,
    )
    created_activity = repo_create_activity(db, activity)
    logger.info(f"Activity created: '{created_activity.title}' by user {current_user.id}")
    return created_activity


def get_activity_by_id(db: Session, activity_id: int) -> Activity:
    return repo_get_activity(db, activity_id)


def get_all_activities(db: Session) -> list[Activity]:
    return repo_get_all_activities(db)


def browse_activities(
    db: Session,
    current_user: User,
    category: str | None = None,
    location: str | None = None,
    date_from: datetime | None = None,
    date_to: datetime | None = None,
) -> list[Activity]:
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
    activity = repo_get_activity(db, activity_id)

    if activity.creator_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not the creator of this activity",
        )

    update_data = data.model_dump(exclude_unset=True)
    if not update_data:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No fields provided to update",
        )

    for field, value in update_data.items():
        setattr(activity, field, value)

    updated_activity = repo_update_activity(db, activity)
    logger.info(f"Activity {activity_id} updated by user {current_user.id}")
    return updated_activity


def delete_activity(db: Session, activity_id: int, current_user: User) -> None:
    activity = repo_get_activity(db, activity_id)

    if activity.creator_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not the creator of this activity",
        )

    activity.status = ActivityStatus.cancelled
    repo_delete_activity(db, activity)
    logger.info(f"Activity {activity_id} cancelled by user {current_user.id}")