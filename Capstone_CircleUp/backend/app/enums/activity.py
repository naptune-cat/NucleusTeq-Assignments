import enum


class ActivityStatus(str, enum.Enum):
    """
    Tracks the lifecycle of an activity from creation to completion.

    - open:      The activity is accepting new join requests.
    - full:      The activity has reached its maximum participant limit.
    - cancelled: The creator has cancelled the activity.
    - completed: The activity date has passed and it is now over.
    """

    open      = "open"
    full      = "full"
    cancelled = "cancelled"
    completed = "completed"  # this is set automatically once the activity date has passed
