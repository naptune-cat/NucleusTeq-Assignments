import pytest

from app.core.exceptions import BadRequestError
from app.schemas.user import UserUpdate
from app.services.user_service import get_user_profile, update_user_profile
from tests.conftest import make_user


def test_get_user_profile_returns_same_user(db_session):
    user = make_user(db_session, email="profile1@gmail.com")
    assert get_user_profile(user) is user


def test_update_user_profile_updates_given_fields(db_session):
    user = make_user(db_session, email="profile2@gmail.com", city="Old City")
    data = UserUpdate(city="New City")

    updated = update_user_profile(db_session, user, data)
    assert updated.city == "New City"


def test_update_user_profile_no_fields_raises(db_session):
    user = make_user(db_session, email="profile3@gmail.com")
    data = UserUpdate()

    with pytest.raises(BadRequestError):
        update_user_profile(db_session, user, data)