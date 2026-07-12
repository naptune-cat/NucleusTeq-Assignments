from datetime import datetime, timedelta, timezone

import pytest

from app.core.exceptions import BadRequestError, NotFoundError, PermissionDeniedError
from app.enums.activity import ActivityStatus
from app.enums.participation import RequestStatus
from app.schemas.activity import ActivityCreate, ActivityUpdate
from app.services.activity_service import (
    browse_activities,
    create_activity,
    delete_activity,
    get_activity_by_id,
    get_all_activities,
    get_my_activities,
    update_activity,
)
from tests.conftest import make_activity, make_request, make_user


def _valid_activity_create(**overrides):
    defaults = dict(
        title="Board Games Night",
        description="Casual board games at the cafe",
        category="Social",
        location="Cafe Central",
        max_participants=6,
        activity_date=datetime.now(timezone.utc) + timedelta(days=2),
        gender_filter="all",
    )
    defaults.update(overrides)
    return ActivityCreate(**defaults)


def test_create_activity(db_session):
    creator = make_user(db_session, email="svchost1@gmail.com")
    data = _valid_activity_create()

    activity = create_activity(db_session, data, creator)
    assert activity.id is not None
    assert activity.status == ActivityStatus.open
    assert activity.creator_id == creator.id


def test_get_activity_by_id_not_found_raises(db_session):
    with pytest.raises(NotFoundError):
        get_activity_by_id(db_session, 999999)


def test_get_all_activities(db_session):
    creator = make_user(db_session, email="svchost2@gmail.com")
    make_activity(db_session, creator)
    activities = get_all_activities(db_session)
    assert len(activities) == 1


def test_get_my_activities(db_session):
    creator = make_user(db_session, email="svchost3@gmail.com")
    other = make_user(db_session, email="svchost3b@gmail.com")
    make_activity(db_session, creator, title="Mine")
    make_activity(db_session, other, title="Not mine")

    mine = get_my_activities(db_session, creator)
    assert len(mine) == 1
    assert mine[0].title == "Mine"


def test_browse_activities_uses_current_user_gender(db_session):
    creator = make_user(db_session, email="svchost4@gmail.com")
    female_user = make_user(db_session, email="svchost4b@gmail.com", gender="female")
    make_activity(db_session, creator, title="Ladies only", gender_filter="female_only")

    results = browse_activities(db_session, female_user)
    titles = [a.title for a in results]
    assert "Ladies only" in titles


def test_update_activity_success(db_session):
    creator = make_user(db_session, email="svchost5@gmail.com")
    activity = make_activity(db_session, creator)
    data = ActivityUpdate(title="Renamed Event")

    updated = update_activity(db_session, activity.id, data, creator)
    assert updated.title == "Renamed Event"


def test_update_activity_not_creator_raises(db_session):
    creator = make_user(db_session, email="svchost6@gmail.com")
    stranger = make_user(db_session, email="svchost6b@gmail.com")
    activity = make_activity(db_session, creator)
    data = ActivityUpdate(title="Hijacked")

    with pytest.raises(PermissionDeniedError):
        update_activity(db_session, activity.id, data, stranger)


def test_update_activity_no_fields_raises(db_session):
    creator = make_user(db_session, email="svchost7@gmail.com")
    activity = make_activity(db_session, creator)
    data = ActivityUpdate()

    with pytest.raises(BadRequestError):
        update_activity(db_session, activity.id, data, creator)


def test_update_activity_capacity_below_approved_raises(db_session):
    creator = make_user(db_session, email="svchost8@gmail.com")
    joiner = make_user(db_session, email="svchost8b@gmail.com")
    activity = make_activity(db_session, creator, max_participants=5)
    make_request(db_session, activity, joiner, status=RequestStatus.approved)
    # Try to reduce capacity to 0 equivalent — use 1 which is below the 2 approved? No.
    # We have 1 approved participant. Reduce from 5 → 1 is still fine (1 approved <= 1 cap).
    # We need capacity below approved count: set capacity to LESS than 1 = not possible.
    # So: create 2 approved joiners, then try to reduce to 1 capacity.
    joiner2 = make_user(db_session, email="svchost8c@gmail.com")
    make_request(db_session, activity, joiner2, status=RequestStatus.approved)
    # Now 2 approved; reducing to 1 should fail
    data = ActivityUpdate(max_participants=1)

    with pytest.raises(BadRequestError):
        update_activity(db_session, activity.id, data, creator)



def test_delete_activity_success_cancels_and_rejects_pending(db_session):
    creator = make_user(db_session, email="svchost9@gmail.com")
    joiner = make_user(db_session, email="svchost9b@gmail.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner, status=RequestStatus.pending)

    delete_activity(db_session, activity.id, creator)

    db_session.refresh(activity)
    db_session.refresh(req)
    assert activity.status == ActivityStatus.cancelled
    assert req.status == RequestStatus.rejected


def test_delete_activity_not_creator_raises(db_session):
    creator = make_user(db_session, email="svchost10@gmail.com")
    stranger = make_user(db_session, email="svchost10b@gmail.com")
    activity = make_activity(db_session, creator)

    with pytest.raises(PermissionDeniedError):
        delete_activity(db_session, activity.id, stranger)