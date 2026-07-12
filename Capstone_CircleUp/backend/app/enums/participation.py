import enum


class RequestStatus(str, enum.Enum):
    """
    Tracks the state of a user's request to join an activity.

    - pending:  The request has been submitted and is awaiting a decision.
    - approved: The activity creator has accepted the request.
    - rejected: The activity creator has declined the request.
    """

    pending  = "pending"
    approved = "approved"
    rejected = "rejected"
