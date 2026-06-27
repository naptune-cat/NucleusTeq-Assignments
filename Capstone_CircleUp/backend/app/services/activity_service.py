from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models.activity import Activity, ActivityStatus
from app.models.user import User
from app.schemas.activity import ActivityCreate, ActivityUpdate
from app.core.logger import logger


def create_activity(db: Session, data: ActivityCreate, current_user: User) -> Activity:
    activity = Activity(
        title=data.title,
        description=data.description,
        category=data.category,
        location=data.location,
        max_participants=data.max_participants,
        activity_date=data.activity_date,
        creator_id=current_user.id,
        status=ActivityStatus.open,
    )
    db.add(activity)
    db.commit()
    db.refresh(activity)
    logger.info(f"Activity created: '{activity.title}' by user {current_user.id}")
    return activity


def get_activity_by_id(db: Session, activity_id: int) -> Activity:
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if not activity:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Activity not found",
        )
    return activity


def get_all_activities(db: Session) -> list[Activity]:
    return db.query(Activity).filter(
        Activity.status != ActivityStatus.cancelled
    ).order_by(Activity.activity_date).all()


def update_activity(
    db: Session, activity_id: int, data: ActivityUpdate, current_user: User
) -> Activity:
    activity = get_activity_by_id(db, activity_id)

    # checking to make sure only creator can edit
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

    db.commit()
    db.refresh(activity)
    logger.info(f"Activity {activity_id} updated by user {current_user.id}")
    return activity


def delete_activity(db: Session, activity_id: int, current_user: User) -> None:
    activity = get_activity_by_id(db, activity_id)

    # only creator can delete the activiy
    if activity.creator_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not the creator of this activity",
        )

    # marking as cancelled instead of actually deleting
    activity.status = ActivityStatus.cancelled
    db.commit()
    logger.info(f"Activity {activity_id} cancelled by user {current_user.id}")