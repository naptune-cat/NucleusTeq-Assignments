class CircleUpError(Exception):
    """Base exception for all CircleUp application errors."""
    message: str = "An unexpected error occurred"
    status_code: int = 500

    def __init__(self, message: str | None = None):
        if message is not None:
            self.message = message
        super().__init__(self.message)


class NotFoundError(CircleUpError):
    message = "Resource not found"
    status_code = 404


class PermissionDeniedError(CircleUpError):
    message = "Permission denied"
    status_code = 403


class BadRequestError(CircleUpError):
    message = "Bad request"
    status_code = 400


class UnauthorizedError(CircleUpError):
    message = "Unauthorized"
    status_code = 401


class ConflictError(CircleUpError):
    message = "Conflict"
    status_code = 409
