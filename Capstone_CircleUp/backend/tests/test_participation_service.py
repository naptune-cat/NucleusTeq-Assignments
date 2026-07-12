"""
Unit tests for participation_service.py

Covers:
- request_to_join         (success + all guard conditions)
- get_my_requests
- get_activity_requests   (success + permission guard)
- approve_request         (success + all guard conditions + auto-full marking)
- reject_request          (success + all guard conditions)
- get_contact             (success + all guard conditions)
- lazy_complete_activity  (past date → completed, future date → no change, cancelled → no change)
- cancel_request          (success + permission guard + activity re-opened when was full)
"""

from datetime import datetime, timedelta, timezone

import pytest

from app.core.exceptions import BadRequestError, NotFoundError, PermissionDeniedError
from app.enums.activity import ActivityStatus
from app.enums.participation import RequestStatus
from app.services.participation_service import (
    approve_request,
    cancel_request,
    get_activity_requests,
    get_contact,
    get_my_requests,
    lazy_complete_activity,
    reject_request,
    request_to_join,
)
from tests.conftest import make_activity, make_request, make_user


# =============================================================================
# request_to_join
# =============================================================================


def test_request_to_join_success(db_session):
    creator = make_user(db_session, email="join1@example.com")
    joiner = make_user(db_session, email="join2@example.com")
    activity = make_activity(db_session, creator)

    req = request_to_join(db_session, activity.id, joiner)

    assert req.id is not None
    assert req.requester_id == joiner.id
    assert req.activity_id == activity.id
    assert req.status == RequestStatus.pending


def test_request_to_join_creator_cannot_join_own_activity(db_session):
    creator = make_user(db_session, email="join3@example.com")
    activity = make_activity(db_session, creator)

    with pytest.raises(BadRequestError, match="own activity"):
        request_to_join(db_session, activity.id, creator)


def test_request_to_join_non_open_activity_raises(db_session):
    creator = make_user(db_session, email="join4@example.com")
    joiner = make_user(db_session, email="join5@example.com")
    activity = make_activity(db_session, creator, status=ActivityStatus.full)

    with pytest.raises(BadRequestError):
        request_to_join(db_session, activity.id, joiner)


def test_request_to_join_cancelled_activity_raises(db_session):
    creator = make_user(db_session, email="join6@example.com")
    joiner = make_user(db_session, email="join7@example.com")
    activity = make_activity(db_session, creator, status=ActivityStatus.cancelled)

    with pytest.raises(BadRequestError):
        request_to_join(db_session, activity.id, joiner)


def test_request_to_join_female_only_blocks_non_female(db_session):
    creator = make_user(db_session, email="join8@example.com", gender="female")
    male_joiner = make_user(db_session, email="join9@example.com", gender="male")
    activity = make_activity(db_session, creator, gender_filter="female_only")

    with pytest.raises(PermissionDeniedError, match="female"):
        request_to_join(db_session, activity.id, male_joiner)


def test_request_to_join_female_only_allows_female(db_session):
    creator = make_user(db_session, email="join10@example.com", gender="female")
    female_joiner = make_user(db_session, email="join11@example.com", gender="female")
    activity = make_activity(db_session, creator, gender_filter="female_only")

    req = request_to_join(db_session, activity.id, female_joiner)
    assert req.status == RequestStatus.pending


def test_request_to_join_duplicate_raises(db_session):
    creator = make_user(db_session, email="join12@example.com")
    joiner = make_user(db_session, email="join13@example.com")
    activity = make_activity(db_session, creator)
    make_request(db_session, activity, joiner)

    with pytest.raises(BadRequestError, match="already requested"):
        request_to_join(db_session, activity.id, joiner)


# =============================================================================
# get_my_requests
# =============================================================================


def test_get_my_requests_returns_only_current_user_requests(db_session):
    user_a = make_user(db_session, email="myrq1@example.com")
    user_b = make_user(db_session, email="myrq2@example.com")
    creator = make_user(db_session, email="myrqc@example.com")
    act1 = make_activity(db_session, creator, title="Act A")
    act2 = make_activity(db_session, creator, title="Act B")
    make_request(db_session, act1, user_a)
    make_request(db_session, act2, user_b)

    results = get_my_requests(db_session, user_a)
    assert len(results) == 1
    assert results[0].requester_id == user_a.id


def test_get_my_requests_empty_when_no_requests(db_session):
    user = make_user(db_session, email="myrq3@example.com")
    assert get_my_requests(db_session, user) == []


# =============================================================================
# get_activity_requests
# =============================================================================


def test_get_activity_requests_returns_all_requests(db_session):
    creator = make_user(db_session, email="actrq1@example.com")
    j1 = make_user(db_session, email="actrq2@example.com")
    j2 = make_user(db_session, email="actrq3@example.com")
    activity = make_activity(db_session, creator)
    make_request(db_session, activity, j1)
    make_request(db_session, activity, j2)

    reqs = get_activity_requests(db_session, activity.id, creator)
    assert len(reqs) == 2


def test_get_activity_requests_non_creator_raises(db_session):
    creator = make_user(db_session, email="actrq4@example.com")
    stranger = make_user(db_session, email="actrq5@example.com")
    activity = make_activity(db_session, creator)

    with pytest.raises(PermissionDeniedError):
        get_activity_requests(db_session, activity.id, stranger)


# =============================================================================
# approve_request
# =============================================================================


def test_approve_request_success(db_session):
    creator = make_user(db_session, email="apr1@example.com")
    joiner = make_user(db_session, email="apr2@example.com")
    activity = make_activity(db_session, creator, max_participants=3)
    req = make_request(db_session, activity, joiner)

    result = approve_request(db_session, req.id, creator)
    assert result.status == RequestStatus.approved


def test_approve_request_not_found_raises(db_session):
    creator = make_user(db_session, email="apr3@example.com")
    with pytest.raises(NotFoundError):
        approve_request(db_session, 999999, creator)


def test_approve_request_non_creator_raises(db_session):
    creator = make_user(db_session, email="apr4@example.com")
    joiner = make_user(db_session, email="apr5@example.com")
    stranger = make_user(db_session, email="apr6@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner)

    with pytest.raises(PermissionDeniedError):
        approve_request(db_session, req.id, stranger)


def test_approve_already_approved_request_raises(db_session):
    creator = make_user(db_session, email="apr7@example.com")
    joiner = make_user(db_session, email="apr8@example.com")
    activity = make_activity(db_session, creator, max_participants=5)
    req = make_request(db_session, activity, joiner, status=RequestStatus.approved)

    with pytest.raises(BadRequestError, match="already"):
        approve_request(db_session, req.id, creator)


def test_approve_request_at_full_capacity_raises(db_session):
    creator = make_user(db_session, email="apr9@example.com")
    j1 = make_user(db_session, email="apr10@example.com")
    j2 = make_user(db_session, email="apr11@example.com")
    activity = make_activity(db_session, creator, max_participants=1)
    # j1 already approved (capacity is 1)
    make_request(db_session, activity, j1, status=RequestStatus.approved)
    req2 = make_request(db_session, activity, j2)

    with pytest.raises(BadRequestError, match="capacity"):
        approve_request(db_session, req2.id, creator)


def test_approve_request_marks_activity_full_when_capacity_reached(db_session):
    creator = make_user(db_session, email="apr12@example.com")
    joiner = make_user(db_session, email="apr13@example.com")
    activity = make_activity(db_session, creator, max_participants=1)
    req = make_request(db_session, activity, joiner)

    approve_request(db_session, req.id, creator)

    db_session.refresh(activity)
    assert activity.status == ActivityStatus.full


# =============================================================================
# reject_request
# =============================================================================


def test_reject_request_success(db_session):
    creator = make_user(db_session, email="rej1@example.com")
    joiner = make_user(db_session, email="rej2@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner)

    result = reject_request(db_session, req.id, creator)
    assert result.status == RequestStatus.rejected


def test_reject_request_not_found_raises(db_session):
    creator = make_user(db_session, email="rej3@example.com")
    with pytest.raises(NotFoundError):
        reject_request(db_session, 999999, creator)


def test_reject_request_non_creator_raises(db_session):
    creator = make_user(db_session, email="rej4@example.com")
    joiner = make_user(db_session, email="rej5@example.com")
    stranger = make_user(db_session, email="rej6@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner)

    with pytest.raises(PermissionDeniedError):
        reject_request(db_session, req.id, stranger)


def test_reject_already_rejected_request_raises(db_session):
    creator = make_user(db_session, email="rej7@example.com")
    joiner = make_user(db_session, email="rej8@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner, status=RequestStatus.rejected)

    with pytest.raises(BadRequestError, match="already"):
        reject_request(db_session, req.id, creator)


# =============================================================================
# get_contact
# =============================================================================


def test_get_contact_host_gets_requester_details(db_session):
    creator = make_user(db_session, email="con1@example.com")
    joiner = make_user(db_session, email="con2@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner, status=RequestStatus.approved)

    contact = get_contact(db_session, req.id, creator)
    assert contact.id == joiner.id


def test_get_contact_requester_gets_host_details(db_session):
    creator = make_user(db_session, email="con3@example.com")
    joiner = make_user(db_session, email="con4@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner, status=RequestStatus.approved)

    contact = get_contact(db_session, req.id, joiner)
    assert contact.id == creator.id


def test_get_contact_not_found_raises(db_session):
    user = make_user(db_session, email="con5@example.com")
    with pytest.raises(NotFoundError):
        get_contact(db_session, 999999, user)


def test_get_contact_stranger_raises(db_session):
    creator = make_user(db_session, email="con6@example.com")
    joiner = make_user(db_session, email="con7@example.com")
    stranger = make_user(db_session, email="con8@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner, status=RequestStatus.approved)

    with pytest.raises(PermissionDeniedError, match="Access denied"):
        get_contact(db_session, req.id, stranger)


def test_get_contact_pending_request_raises(db_session):
    creator = make_user(db_session, email="con9@example.com")
    joiner = make_user(db_session, email="con10@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner, status=RequestStatus.pending)

    with pytest.raises(PermissionDeniedError, match="approved"):
        get_contact(db_session, req.id, creator)


# =============================================================================
# lazy_complete_activity
# =============================================================================


def test_lazy_complete_activity_past_date_marks_completed(db_session):
    creator = make_user(db_session, email="lc1@example.com")
    past_date = datetime.now(timezone.utc) - timedelta(days=1)
    activity = make_activity(db_session, creator, activity_date=past_date, status=ActivityStatus.open)

    result = lazy_complete_activity(db_session, activity.id)
    assert result.status == ActivityStatus.completed


def test_lazy_complete_activity_full_past_date_marks_completed(db_session):
    creator = make_user(db_session, email="lc2@example.com")
    past_date = datetime.now(timezone.utc) - timedelta(days=1)
    activity = make_activity(db_session, creator, activity_date=past_date, status=ActivityStatus.full)

    result = lazy_complete_activity(db_session, activity.id)
    assert result.status == ActivityStatus.completed


def test_lazy_complete_activity_future_date_no_change(db_session):
    creator = make_user(db_session, email="lc3@example.com")
    future_date = datetime.now(timezone.utc) + timedelta(days=5)
    activity = make_activity(db_session, creator, activity_date=future_date)

    result = lazy_complete_activity(db_session, activity.id)
    assert result.status == ActivityStatus.open


def test_lazy_complete_activity_cancelled_no_change(db_session):
    creator = make_user(db_session, email="lc4@example.com")
    past_date = datetime.now(timezone.utc) - timedelta(days=1)
    activity = make_activity(db_session, creator, activity_date=past_date, status=ActivityStatus.cancelled)

    result = lazy_complete_activity(db_session, activity.id)
    assert result.status == ActivityStatus.cancelled


def test_lazy_complete_activity_naive_datetime_handled(db_session):
    """Naive datetimes (no tzinfo) in the DB should still be compared correctly."""
    creator = make_user(db_session, email="lc5@example.com")
    naive_past = datetime.now() - timedelta(days=1)  # no tzinfo
    activity = make_activity(db_session, creator, activity_date=naive_past, status=ActivityStatus.open)

    result = lazy_complete_activity(db_session, activity.id)
    assert result.status == ActivityStatus.completed


# =============================================================================
# cancel_request
# =============================================================================


def test_cancel_request_success(db_session):
    creator = make_user(db_session, email="can1@example.com")
    joiner = make_user(db_session, email="can2@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner)

    result = cancel_request(db_session, req.id, joiner)
    assert result["detail"] == "Request cancelled successfully"


def test_cancel_request_not_found_raises(db_session):
    user = make_user(db_session, email="can3@example.com")
    with pytest.raises(NotFoundError):
        cancel_request(db_session, 999999, user)


def test_cancel_request_non_requester_raises(db_session):
    creator = make_user(db_session, email="can4@example.com")
    joiner = make_user(db_session, email="can5@example.com")
    stranger = make_user(db_session, email="can6@example.com")
    activity = make_activity(db_session, creator)
    req = make_request(db_session, activity, joiner)

    with pytest.raises(PermissionDeniedError):
        cancel_request(db_session, req.id, stranger)


def test_cancel_approved_request_reopens_full_activity(db_session):
    creator = make_user(db_session, email="can7@example.com")
    joiner = make_user(db_session, email="can8@example.com")
    activity = make_activity(db_session, creator, max_participants=1, status=ActivityStatus.full)
    req = make_request(db_session, activity, joiner, status=RequestStatus.approved)

    cancel_request(db_session, req.id, joiner)

    db_session.refresh(activity)
    assert activity.status == ActivityStatus.open


def test_cancel_approved_request_does_not_reopen_if_still_full(db_session):
    creator = make_user(db_session, email="can9@example.com")
    j1 = make_user(db_session, email="can10@example.com")
    j2 = make_user(db_session, email="can11@example.com")
    # 2 spots, both approved; cancel one → still 1 approved, but max is 1 still full? No.
    # max=2, 2 approved, cancel one → 1 approved < 2 → re-open
    # For "still full": max=2, 2 approved, cancel one → 1 approved < 2 → opens
    # For truly still full: max=1, 2 approved is impossible (unique constraint)
    # So just test the "was approved but activity was NOT full" branch
    activity = make_activity(db_session, creator, max_participants=5, status=ActivityStatus.open)
    req = make_request(db_session, activity, j1, status=RequestStatus.approved)

    cancel_request(db_session, req.id, j1)
    db_session.refresh(activity)
    # Activity stays open since status was already open (not full)
    assert activity.status == ActivityStatus.open

import threading

def test_concurrency_capacity_logic(db_session, engine):
    from sqlalchemy.orm import Session
    
    creator = make_user(db_session, email="conc1@example.com")
    j1 = make_user(db_session, email="conc2@example.com")
    j2 = make_user(db_session, email="conc3@example.com")
    
    activity = make_activity(db_session, creator, max_participants=1)
    req1 = make_request(db_session, activity, j1)
    req2 = make_request(db_session, activity, j2)
    db_session.commit()
    
    req1_id = req1.id
    req2_id = req2.id
    creator_id = creator.id
    
    exceptions = []
    
    def approve_in_thread(req_id):
        with Session(engine) as session:
            try:
                from app.models.user import User
                thread_creator = session.get(User, creator_id)
                approve_request(session, req_id, thread_creator)
                session.commit()
            except Exception as e:
                session.rollback()
                exceptions.append(e)
                
    t1 = threading.Thread(target=approve_in_thread, args=(req1_id,))
    t2 = threading.Thread(target=approve_in_thread, args=(req2_id,))
    
    t1.start()
    t2.start()
    t1.join()
    t2.join()
    
    db_session.refresh(activity)
    assert activity.participants_count <= 1
    assert len(exceptions) >= 1
