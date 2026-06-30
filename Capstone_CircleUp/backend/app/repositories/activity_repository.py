from fastapi import HTTPException, status
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
    return db.query(Activity).filter(
        Activity.status != ActivityStatus.cancelled
    ).order_by(Activity.activity_date).all()


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