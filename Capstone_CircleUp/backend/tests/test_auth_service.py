import pytest

from app.core.exceptions import BadRequestError, NotFoundError, UnauthorizedError
from app.schemas.user import UserCreate
from app.services.auth_service import login_user, register_user
from tests.conftest import make_user


def _valid_user_create(**overrides):
    defaults = dict(
        name="Zoya Khanam",
        email="zoya.dev@gmail.com",
        phone_number="9876543210",
        city="Jabalpur",
        bio="developer",
        gender="female",
        password="Passw0rd!",
    )
    defaults.update(overrides)
    return UserCreate(**defaults)


def test_register_user_success(db_session):
    data = _valid_user_create()
    user = register_user(db_session, data)
    assert user.id is not None
    assert user.email == "zoya.dev@gmail.com"
    assert user.hashed_password != "Passw0rd!"


def test_register_user_duplicate_email_raises(db_session):
    make_user(db_session, email="taken@gmail.com")
    data = _valid_user_create(email="taken@gmail.com")
    with pytest.raises(BadRequestError):
        register_user(db_session, data)


def test_login_user_success(db_session):
    data = _valid_user_create(email="loginme@gmail.com")
    register_user(db_session, data)

    token = login_user(db_session, "loginme@gmail.com", "Passw0rd!")
    assert isinstance(token, str) and len(token) > 0


def test_login_user_nonexistent_email_raises(db_session):
    with pytest.raises(NotFoundError):
        login_user(db_session, "ghost@gmail.com", "whatever")


def test_login_user_wrong_password_raises(db_session):
    data = _valid_user_create(email="wrongpass@gmail.com")
    register_user(db_session, data)

    with pytest.raises(UnauthorizedError):
        login_user(db_session, "wrongpass@gmail.com", "IncorrectPass1!")