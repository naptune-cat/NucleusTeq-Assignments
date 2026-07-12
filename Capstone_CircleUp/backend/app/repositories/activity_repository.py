from datetime import datetime

from sqlalchemy.exc import IntegrityError, SQLAlchemyError
from sqlalchemy.orm import Session
from app.models.activity import Activity, ActivityStatus
from app.models.participation import ParticipationRequest, RequestStatus


def get_activity_by_id(db: Session, activity_id: int) -> Activity | None:
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if activity:
        activity.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == activity_id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
    return activity


def get_all_activities(db: Session) -> list[Activity]:
    activities = db.query(Activity).filter(
        Activity.status != ActivityStatus.cancelled
    ).order_by(Activity.activity_date).all()
    return db.query(Activity).filter(
        Activity.status != ActivityStatus.cancelled
    ).order_by(Activity.activity_date).all()
    for a in activities:
        a.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == a.id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
    return activities


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

    if current_user_gender != "female":
        query = query.filter(Activity.gender_filter == "all")

    activities = query.order_by(Activity.activity_date).all()
    for a in activities:
        a.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == a.id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
    return activities


def create_activity(db: Session, activity: Activity) -> Activity:
    try:
        db.add(activity)
        db.commit()
        db.refresh(activity)
        activity.participants_count = 0
        return activity
    except IntegrityError as e:
        db.rollback()
        raise e
    except SQLAlchemyError as e:
        db.rollback()
        raise e


def update_activity(db: Session, activity: Activity) -> Activity:
    try:
        db.commit()
        db.refresh(activity)
        return activity
    except IntegrityError as e:
        db.rollback()
        raise e
    except SQLAlchemyError as e:
        db.rollback()
        raise e


def delete_activity(db: Session, activity: Activity) -> None:
    try:
        db.commit()
    except IntegrityError as e:
        db.rollback()
        raise e
    except SQLAlchemyError as e:
        db.rollback()
        raise e