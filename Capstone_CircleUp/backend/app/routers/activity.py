from datetime import datetime
from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.dependencies import get_current_user
from app.models.user import User
from app.schemas.activity import ActivityCreate, ActivityOut, ActivityUpdate
from app.services.activity_service import (
    create_activity,
    get_activity_by_id,
    get_all_activities,
    get_my_activities,
    browse_activities,
    update_activity,
    delete_activity,
)

router = APIRouter()


@router.post("", response_model=ActivityOut, status_code=status.HTTP_201_CREATED)
def create(
    data: ActivityCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return create_activity(db, data, current_user)


@router.get("/browse", response_model=list[ActivityOut])
def browse(
    category: str | None = None,
    location: str | None = None,
    date_from: datetime | None = None,
    date_to: datetime | None = None,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return browse_activities(db, current_user, category, location, date_from, date_to)


@router.get("", response_model=list[ActivityOut])
def list_activities(db: Session = Depends(get_db)):
    return get_all_activities(db)


@router.get("/mine", response_model=list[ActivityOut])
def my_activities(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return get_my_activities(db, current_user)


@router.get("/{activity_id}", response_model=ActivityOut)
def get_one(activity_id: int, db: Session = Depends(get_db)):
    return get_activity_by_id(db, activity_id)


@router.put("/{activity_id}", response_model=ActivityOut)
def update(
    activity_id: int,
    data: ActivityUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return update_activity(db, activity_id, data, current_user)


@router.delete("/{activity_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete(
    activity_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    delete_activity(db, activity_id, current_user)