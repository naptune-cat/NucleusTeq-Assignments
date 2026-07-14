from datetime import datetime

from sqlalchemy.exc import IntegrityError, SQLAlchemyError
from sqlalchemy.orm import Session

from app.enums.activity import ActivityStatus
from app.enums.participation import RequestStatus
from app.models.activity import Activity
from app.models.participation import ParticipationRequest


def get_activity_by_id(db: Session, activity_id: int) -> Activity | None:
    """Get an activity by ID. Returns None if not found."""
    activity = db.query(Activity).filter(Activity.id == activity_id).first()
    if activity:
        # attach current approved participant count
        activity.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == activity_id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
    return activity


def get_activity_for_update(db: Session, activity_id: int) -> Activity | None:
    """
    I'm locking the activity row first so two approvals can't happen at the same time and exceed the activity capacity.
    """
    activity = (
        db.query(Activity)
        .filter(Activity.id == activity_id)
        .with_for_update()  # row-level lock (SELECT FOR UPDATE)
        .first()
    )
    if activity:
        # attach current approved participant count while we hold the lock
        activity.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == activity_id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
    return activity


def get_all_activities(db: Session) -> list[Activity]:
    """Get all non-cancelled activities, ordered by date."""
    activities = db.query(Activity).filter(
        Activity.status != ActivityStatus.cancelled
    ).order_by(Activity.activity_date).all()
   
    for a in activities:
        a.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == a.id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()



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

    for a in activities:
        a.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == a.id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
    return activities


def get_activities_by_creator(db: Session, creator_id: int) -> list[Activity]:
    """Get all activities created by a specific user."""
    activities = db.query(Activity).filter(
        Activity.creator_id == creator_id
    ).order_by(Activity.activity_date.desc()).all()

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
    """
    Search activities with optional filters.
    Non-female users only see activities open to everyone.
    """
    query = db.query(Activity).filter(Activity.status != ActivityStatus.cancelled)

    if category:
        query = query.filter(Activity.category.ilike(f"%{category}%"))
    if location:
        query = query.filter(Activity.location.ilike(f"%{location}%"))
    if date_from:
        query = query.filter(Activity.activity_date >= date_from)
    if date_to:
        query = query.filter(Activity.activity_date <= date_to)

    # hide female-only activities from non-female users
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
    """Save a new activity to the database."""
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
    """Commit changes made to an activity object."""
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
    """This is a soft-delete (status=cancelled) on activity to keep track of activity history"""
    try:
        db.commit()
    except IntegrityError as e:
        db.rollback()
        raise e
    except SQLAlchemyError as e:
        db.rollback()
        raise e