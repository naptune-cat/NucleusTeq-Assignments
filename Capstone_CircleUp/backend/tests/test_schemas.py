from datetime import datetime, timedelta, timezone
import pytest
from pydantic import ValidationError
from app.schemas.activity import ActivityCreate, ActivityUpdate

def test_activity_create_valid():
    ActivityCreate(
        title="Valid Title",
        description="Valid description goes here.",
        category="Sports",
        location="Some Location",
        max_participants=5,
        activity_date=datetime.now(timezone.utc) + timedelta(days=1)
    )

def test_activity_create_invalid_title():
    with pytest.raises(ValidationError):
        ActivityCreate(
            title="ab",
            description="Valid description goes here.",
            category="Sports",
            location="Some Location",
            max_participants=5,
            activity_date=datetime.now(timezone.utc) + timedelta(days=1)
        )

def test_activity_create_invalid_description():
    with pytest.raises(ValidationError):
        ActivityCreate(
            title="Valid Title",
            description="short",
            category="Sports",
            location="Some Location",
            max_participants=5,
            activity_date=datetime.now(timezone.utc) + timedelta(days=1)
        )

def test_activity_create_invalid_category():
    with pytest.raises(ValidationError):
        ActivityCreate(
            title="Valid Title",
            description="Valid description goes here.",
            category="InvalidCategory",
            location="Some Location",
            max_participants=5,
            activity_date=datetime.now(timezone.utc) + timedelta(days=1)
        )

def test_activity_create_invalid_max_participants():
    with pytest.raises(ValidationError):
        ActivityCreate(
            title="Valid Title",
            description="Valid description goes here.",
            category="Sports",
            location="Some Location",
            max_participants=0,
            activity_date=datetime.now(timezone.utc) + timedelta(days=1)
        )

def test_activity_create_invalid_date():
    with pytest.raises(ValidationError):
        ActivityCreate(
            title="Valid Title",
            description="Valid description goes here.",
            category="Sports",
            location="Some Location",
            max_participants=5,
            activity_date=datetime.now(timezone.utc) - timedelta(days=1)
        )

def test_activity_update_invalid_max_participants():
    with pytest.raises(ValidationError):
        ActivityUpdate(max_participants=0)

def test_activity_update_invalid_date():
    with pytest.raises(ValidationError):
        ActivityUpdate(activity_date=datetime.now(timezone.utc) - timedelta(days=1))
