from datetime import datetime
from fastapi import HTTPException, status
from sqlalchemy import and_
from sqlalchemy.orm import Session
from app.models.activity import Activity, ActivityStatus


def get_activity_by_id(db: Session, activity_id: int) -> Activity:
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if not activity:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Activity not found",
        )
    return activity


def get_all_activities(db: Session) -> list[Activity]:
<<<<<<< Updated upstream
    return db.query(Activity).filter(
        Activity.status != ActivityStatus.cancelled
    ).order_by(Activity.activity_date).all()
=======
    activities = db.query(Activity).order_by(Activity.activity_date).all()
    for a in activities:
        a.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == a.id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
    return activities
>>>>>>> Stashed changes


def browse_activities(
    db: Session,
    category: str | None = None,
    location: str | None = None,
    date_from: datetime | None = None,
    date_to: datetime | None = None,
    gender_filter: str | None = None,
    current_user_gender: str | None = None,
) -> list[Activity]:
    query = db.query(Activity).filter(Activity.status != ActivityStatus.cancelled)

    if category:
        query = query.filter(Activity.category.ilike(f"%{category}%"))

    if location:
        query = query.filter(Activity.location.ilike(f"%{location}%"))

    if date_from:
        query = query.filter(Activity.activity_date >= date_from)

    if date_to:
        query = query.filter(Activity.activity_date <= date_to)

    # female_only activities only visible to females
    if current_user_gender == "female":
        # females see both "all" and "female_only"
        pass
    else:
        # males/others see only "all" activities
        query = query.filter(Activity.gender_filter == "all")

    return query.order_by(Activity.activity_date).all()


def browse_activities(
    db: Session,
    category: str | None = None,
    location: str | None = None,
    date_from: datetime | None = None,
    date_to: datetime | None = None,
    gender_filter: str | None = None,
    current_user_gender: str | None = None,
) -> list[Activity]:
    query = db.query(Activity).filter(Activity.status != ActivityStatus.cancelled)

    if category:
        query = query.filter(Activity.category.ilike(f"%{category}%"))

    if location:
        query = query.filter(Activity.location.ilike(f"%{location}%"))

    if date_from:
        query = query.filter(Activity.activity_date >= date_from)

    if date_to:
        query = query.filter(Activity.activity_date <= date_to)

    # female_only activities only visible to females
    if current_user_gender == "female":
        # females see both "all" and "female_only"
        pass
    else:
        # males/others see only "all" activities
        query = query.filter(Activity.gender_filter == "all")

    return query.order_by(Activity.activity_date).all()


def create_activity(db: Session, activity: Activity) -> Activity:
    try:
        db.add(activity)
        db.commit()
        db.refresh(activity)
        return activity
    except Exception as e:
        db.rollback()
        raise e


def update_activity(db: Session, activity: Activity) -> Activity:
    try:
        db.commit()
        db.refresh(activity)
        return activity
    except Exception as e:
        db.rollback()
        raise e


def delete_activity(db: Session, activity: Activity) -> None:
    try:
        db.commit()
    except Exception as e:
        db.rollback()
        raise e