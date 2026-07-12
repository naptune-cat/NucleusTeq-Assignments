"""
Shared pytest fixtures and factory helpers for the CircleUp test suite.

Uses an in-memory SQLite database so tests are fast and self-contained —
no PostgreSQL connection is required.
"""

import os

# ---------------------------------------------------------------------------
# Setting env vars before importing any app modules.
# pydantic-settings respects env vars over the .env file by default.
# ---------------------------------------------------------------------------
os.environ["DATABASE_URL"] = "sqlite:///:memory:"
os.environ.setdefault("SECRET_KEY", "test-secret-key-for-unit-tests-only")

from datetime import datetime, timedelta, timezone

import pytest
from sqlalchemy import create_engine, event
from sqlalchemy.dialects.sqlite.base import SQLiteTypeCompiler
from sqlalchemy.orm import sessionmaker

# -----------------------------------------------------------------------
# SQLite compatibility patch
# SQLite requires the PRIMARY KEY column to be declared as INTEGER (not
# BIGINT) so that it can act as an alias for the internal rowid and
# auto-increment on INSERT.  Our models use BigInteger everywhere, which
# SQLAlchemy maps to BIGINT — breaking auto-increment in SQLite.
# Patching the compiler makes BigInteger render as INTEGER for SQLite only.
# -----------------------------------------------------------------------
SQLiteTypeCompiler.visit_big_integer = lambda self, type_, **kwargs: "INTEGER"
#  importing after env override
from app.core.database import Base 
from app.enums.activity import ActivityStatus
from app.enums.participation import RequestStatus
from app.models.activity import Activity
from app.models.participation import ParticipationRequest
from app.models.user import User
from app.core.security import hash_password


# ---------------------------------------------------------------------------
# Database fixture
# ---------------------------------------------------------------------------

SQLITE_URL = "sqlite:///:memory:"


@pytest.fixture(scope="session")
def engine():
    """Create a single in-memory SQLite engine for the whole test session."""
    _engine = create_engine(
        SQLITE_URL,
        connect_args={"check_same_thread": False},
    )

    @event.listens_for(_engine, "connect")
    def set_sqlite_pragma(dbapi_conn, _):
        """Enable foreign-key enforcement for every new connection."""
        cursor = dbapi_conn.cursor()
        cursor.execute("PRAGMA foreign_keys=ON")
        cursor.close()

    Base.metadata.create_all(bind=_engine)
    yield _engine
    Base.metadata.drop_all(bind=_engine)


@pytest.fixture()
def db_session(engine):
    """
    Provide a test database session.

    Each test gets a fresh connection.  We use a nested SAVEPOINT so that
    any explicit commit() calls inside service functions don't permanently
    write data — everything is rolled back when the test finishes.
    """
    connection = engine.connect()
    # Start the outer transaction
    transaction = connection.begin()
    # Create a SAVEPOINT so inner commits don't escape
    connection.begin_nested()

    SessionFactory = sessionmaker(bind=connection, autoflush=False, autocommit=False)
    session = SessionFactory()

    # Every time SQLAlchemy would commit, restart the savepoint instead
    @event.listens_for(session, "after_transaction_end")
    def restart_savepoint(session, trans):
        if trans.nested and not trans._parent.nested:
            session.begin_nested()

    yield session

    session.close()
    transaction.rollback()
    connection.close()


# ---------------------------------------------------------------------------
# Factory helpers (importable by test modules)
# ---------------------------------------------------------------------------

_user_counter = 0


def make_user(
    db,
    *,
    email: str = None,
    name: str = "Test User",
    password: str = "Passw0rd!",
    phone_number: str = "9000000000",
    city: str = "Testville",
    bio: str = "A test user",
    gender: str = "male",
) -> User:
    """Create and persist a User in *db*."""
    global _user_counter
    _user_counter += 1
    if email is None:
        email = f"testuser{_user_counter}@example.com"

    user = User(
        name=name,
        email=email,
        hashed_password=hash_password(password),
        phone_number=phone_number,
        city=city,
        bio=bio,
        gender=gender,
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def make_activity(
    db,
    creator: User,
    *,
    title: str = "Test Activity",
    description: str = "A test activity",
    category: str = "Social",
    location: str = "Test Location",
    max_participants: int = 5,
    activity_date: datetime = None,
    gender_filter: str = "all",
    status: ActivityStatus = ActivityStatus.open,
) -> Activity:
    """Create and persist an Activity in *db*."""
    if activity_date is None:
        activity_date = datetime.now(timezone.utc) + timedelta(days=7)

    activity = Activity(
        title=title,
        description=description,
        category=category,
        location=location,
        max_participants=max_participants,
        activity_date=activity_date,
        gender_filter=gender_filter,
        creator_id=creator.id,
        status=status,
    )
    db.add(activity)
    db.commit()
    db.refresh(activity)
    activity.participants_count = 0
    return activity


def make_request(
    db,
    activity: Activity,
    requester: User,
    *,
    status: RequestStatus = RequestStatus.pending,
) -> ParticipationRequest:
    """Create and persist a ParticipationRequest in *db*."""
    req = ParticipationRequest(
        activity_id=activity.id,
        requester_id=requester.id,
        status=status,
    )
    db.add(req)
    db.commit()
    db.refresh(req)
    return req
