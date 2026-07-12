from datetime import datetime, timezone

from sqlalchemy.orm import Session

from app.core.exceptions import BadRequestError, NotFoundError, PermissionDeniedError
from app.core.logger import logger
from app.enums.activity import ActivityStatus
from app.enums.participation import RequestStatus
from app.models.user import User
from app.repositories.activity_repository import (
    get_activity_by_id as repo_get_activity,
    get_activity_for_update as repo_get_activity_for_update,
    update_activity as repo_update_activity,
)
from app.repositories.participation_repository import (
    count_approved,
    create_request,
    delete_request,
    get_existing_request,
    get_request_by_id,
    get_requests_by_user,
    get_requests_for_activity,
    update_request_status,
)
from app.repositories.user_repository import get_user_by_id


def request_to_join(db: Session, activity_id: int, current_user: User):
    """
    Submit a join request for an activity.

    Checks performed before creating the request:
    - User is not the activity creator
    - Activity is still open (not full or cancelled)
    - Activity allows the user's gender
    - User hasn't already sent a request for this activity
    """
    activity = repo_get_activity(db, activity_id)

    # Creators cannot request to join their own activity
    if activity.creator_id == current_user.id:
        raise BadRequestError("You cannot request to join your own activity")

    # Only open activities accept new requests
    if activity.status != ActivityStatus.open:
        raise BadRequestError(f"Activity is {activity.status.value} and not accepting requests")

    # Enforce the gender restriction for female-only activities
    if activity.gender_filter == "female_only" and current_user.gender != "female":
        raise PermissionDeniedError("This activity is for female participants only")

    # Prevent the user from sending a duplicate request
    if get_existing_request(db, activity_id, current_user.id):
        raise BadRequestError("You have already requested to join this activity")

    req = create_request(db, activity_id, current_user.id)
    logger.info(f"User {current_user.id} requested to join activity {activity_id}")
    return req


def get_my_requests(db: Session, current_user: User):
    """Return all participation requests made by the current user."""
    return get_requests_by_user(db, current_user.id)


def get_activity_requests(db: Session, activity_id: int, current_user: User):
    """
    Return all participation requests for an activity.
    Only the activity creator is allowed to see this list.
    """
    activity = repo_get_activity(db, activity_id)

    if activity.creator_id != current_user.id:
        raise PermissionDeniedError("You are not the creator of this activity")

    return get_requests_for_activity(db, activity_id)


def approve_request(db: Session, request_id: int, current_user: User):
    """
    Approve a request.
    Uses a database lock so two people can't be approved at the exact same time
    if there's only one spot left.
    """
    req = get_request_by_id(db, request_id)
    if not req:
        raise NotFoundError("Request not found")

    # Lock the row so no one else can approve right now
    activity = repo_get_activity_for_update(db, req.activity_id)
    if not activity:
        raise NotFoundError("Activity not found")

    # Only the activity creator can approve requests
    if activity.creator_id != current_user.id:
        raise PermissionDeniedError("You are not the creator of this activity")

    # Request must still be pending — not already approved or rejected
    if req.status != RequestStatus.pending:
        raise BadRequestError(f"Request is already {req.status.value}")

    # Check capacity again while locked to be safe
    approved_count = count_approved(db, req.activity_id)
    if approved_count >= activity.max_participants:
        raise BadRequestError("Activity is already at full capacity")

    # Approve the request — this commits the transaction and releases the lock
    req = update_request_status(db, req, RequestStatus.approved)
    logger.info(f"Request {request_id} approved by user {current_user.id}")

    # After committing, check if the activity is now full and mark it accordingly
    if count_approved(db, req.activity_id) >= activity.max_participants:
        activity.status = ActivityStatus.full
        repo_update_activity(db, activity)
        logger.info(
            f"Activity {activity.id} automatically marked as full "
            f"({activity.max_participants}/{activity.max_participants} spots taken)"
        )

    return req


def reject_request(db: Session, request_id: int, current_user: User):
    """
    Reject a pending participation request.
    Only the activity creator is allowed to do this.
    """
    req = get_request_by_id(db, request_id)
    if not req:
        raise NotFoundError("Request not found")

    activity = repo_get_activity(db, req.activity_id)

    if activity.creator_id != current_user.id:
        raise PermissionDeniedError("You are not the creator of this activity")

    # Only pending requests can be rejected
    if req.status != RequestStatus.pending:
        raise BadRequestError(f"Request is already {req.status.value}")

    req = update_request_status(db, req, RequestStatus.rejected)
    logger.info(f"Request {request_id} rejected by user {current_user.id}")
    return req


def get_contact(db: Session, request_id: int, current_user: User):
    """
    Return the contact details of the other party once a request is approved.
    - If the current user is the host, return the requester's contact.
    - If the current user is the requester, return the host's contact.
    Contact details are only available after the request has been approved.
    """
    req = get_request_by_id(db, request_id)
    if not req:
        raise NotFoundError("Request not found")

    activity = repo_get_activity(db, req.activity_id)
    is_host      = activity.creator_id == current_user.id
    is_requester = req.requester_id    == current_user.id

    # Only the host or the requester can access contact details
    if not is_host and not is_requester:
        raise PermissionDeniedError("Access denied")

    # Contact info is gated behind approval — prevents exposure on rejected requests
    if req.status != RequestStatus.approved:
        raise PermissionDeniedError("Contact is only visible after request is approved")

    # Return the other participant's contact details
    contact_user_id = req.requester_id if is_host else activity.creator_id
    contact_user    = get_user_by_id(db, contact_user_id)

    if not contact_user:
        raise NotFoundError("User not found")

    return contact_user


def lazy_complete_activity(db: Session, activity_id: int):
    """
    Automatically mark an activity as completed if its date has already passed.

    This is called lazily (on-read) rather than on a scheduled job.
    Only open or full activities can transition to completed — cancelled
    activities are left as-is.
    """
    activity = repo_get_activity(db, activity_id)
    now = datetime.now(timezone.utc)

    # Only transition activities that are still in an active state
    if activity.status in (ActivityStatus.open, ActivityStatus.full):
        activity_date = activity.activity_date

        # Normalise naive datetimes to UTC so the comparison is always valid
        if activity_date.tzinfo is None:
            activity_date = activity_date.replace(tzinfo=timezone.utc)

        if activity_date < now:
            try:
                activity.status = ActivityStatus.completed
                repo_update_activity(db, activity)
                logger.info(
                    f"Activity {activity_id} automatically marked as completed "
                    f"(its date {activity_date.date()} has passed)"
                )
            except Exception:
                # If something goes wrong, roll back so we don't leave dirty state
                db.rollback()
                logger.warning(
                    f"Failed to auto-complete activity {activity_id} — rolled back"
                )

    return activity


def cancel_request(db: Session, request_id: int, current_user: User):
    """
    Cancel a participation request.
    Only the requester can cancel their request.
    If the request was approved and the activity status was full, we mark it as open again.
    """
    req = get_request_by_id(db, request_id)
    if not req:
        raise NotFoundError("Request not found")

    if req.requester_id != current_user.id:
        raise PermissionDeniedError("You are not authorized to cancel this request")

    activity_id = req.activity_id
    was_approved = req.status == RequestStatus.approved

    delete_request(db, req)
    logger.info(f"Participation request {request_id} deleted/cancelled by user {current_user.id}")

    if was_approved:
        # Load activity
        activity = repo_get_activity(db, activity_id)
        if activity and activity.status == ActivityStatus.full:
            # Check current approved count
            current_approved = count_approved(db, activity_id)
            if current_approved < activity.max_participants:
                activity.status = ActivityStatus.open
                repo_update_activity(db, activity)
                logger.info(f"Activity {activity_id} automatically marked back as open because approved request was cancelled")

    return {"detail": "Request cancelled successfully"}