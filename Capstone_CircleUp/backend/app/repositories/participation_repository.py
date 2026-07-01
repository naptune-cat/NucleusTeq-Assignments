from sqlalchemy.orm import Session

from app.models.participation import ParticipationRequest, RequestStatus


# Get a participation request by its ID
def get_request_by_id(db: Session, request_id: int) -> ParticipationRequest | None:
    return db.query(ParticipationRequest).filter(ParticipationRequest.id == request_id).first()


# Check if the user has already requested to join the activity
def get_existing_request(db: Session, activity_id: int, requester_id: int) -> ParticipationRequest | None:
    return db.query(ParticipationRequest).filter(
        ParticipationRequest.activity_id == activity_id,
        ParticipationRequest.requester_id == requester_id,
    ).first()


# Get all participation requests for an activity
def get_requests_for_activity(db: Session, activity_id: int) -> list[ParticipationRequest]:
    return db.query(ParticipationRequest).filter(
        ParticipationRequest.activity_id == activity_id
    ).order_by(ParticipationRequest.requested_at).all()


# Get all participation requests made by a user
def get_requests_by_user(db: Session, user_id: int) -> list[ParticipationRequest]:
    return db.query(ParticipationRequest).filter(
        ParticipationRequest.requester_id == user_id
    ).order_by(ParticipationRequest.requested_at.desc()).all()


# Count the number of approved requests for an activity
def count_approved(db: Session, activity_id: int) -> int:
    return db.query(ParticipationRequest).filter(
        ParticipationRequest.activity_id == activity_id,
        ParticipationRequest.status == RequestStatus.approved,
    ).count()


# Create a new participation request
def create_request(db: Session, activity_id: int, requester_id: int) -> ParticipationRequest:
    try:
        req = ParticipationRequest(
            activity_id=activity_id,
            requester_id=requester_id,
            status=RequestStatus.pending,
        )
        db.add(req)
        db.commit()
        db.refresh(req)
        return req
    except Exception as e:
        db.rollback()
        raise e


# Update the status of a participation request
def update_request_status(
    db: Session,
    req: ParticipationRequest,
    status: RequestStatus,
) -> ParticipationRequest:
    try:
        req.status = status
        db.commit()
        db.refresh(req)
        return req
    except Exception as e:
        # Rolling back changes if an error occurs
        db.rollback()
        raise e