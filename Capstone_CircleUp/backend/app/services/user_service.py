from sqlalchemy.orm import Session

from app.core.exceptions import BadRequestError
from app.models.user import User
from app.schemas.user import UserUpdate


def get_user_profile(user: User) -> User:
    # user is already fetched by get_current_user dependency
    # no DB call needed, just returning it
    return user


def update_user_profile(db: Session, user: User, data: UserUpdate) -> User:
    # only update fields that were actually sent
    update_data = data.model_dump(exclude_unset=True)

    if not update_data:
        raise BadRequestError("No fields provided to update")


    for field, value in update_data.items():
        setattr(user, field, value)

    db.commit()
    db.refresh(user)
    return user