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


from sqlalchemy import or_

def browse_activities(
    db: Session,
    category: str | None = None,
    location: str | None = None,
    date_from: datetime | None = None,
    date_to: datetime | None = None,
    search: str | None = None,
    sort_by: str | None = None,
    girls_only: str | None = None,
    current_user_gender: str | None = None,
) -> list[Activity]:
    """
    Search activities with optional filters.
    Non-female users only see activities open to everyone.
    """
    query = db.query(Activity).filter(Activity.status != ActivityStatus.cancelled)

    if search:
        search_pattern = f"%{search}%"
        query = query.filter(
            or_(
                Activity.title.ilike(search_pattern),
                Activity.description.ilike(search_pattern)
            )
        )

    if category:
        query = query.filter(Activity.category.ilike(f"%{category}%"))
    if location:
        query = query.filter(Activity.location.ilike(f"%{location}%"))
    if date_from:
        query = query.filter(Activity.activity_date >= date_from)
    if date_to:
        query = query.filter(Activity.activity_date <= date_to)

    if girls_only == "female_only":
        query = query.filter(Activity.gender_filter == "female_only")
    elif girls_only == "all":
        query = query.filter(Activity.gender_filter == "all")

    # hide female-only activities from non-female users
    if current_user_gender != "female":
        query = query.filter(Activity.gender_filter == "all")

    activities = query.all()

    for a in activities:
        a.participants_count = db.query(ParticipationRequest).filter(
            ParticipationRequest.activity_id == a.id,
            ParticipationRequest.status == RequestStatus.approved,
        ).count()
        
    if sort_by == "popular":
        activities.sort(key=lambda x: (x.max_participants - (x.participants_count or 0)))
    else:
        activities.sort(key=lambda x: x.activity_date)

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