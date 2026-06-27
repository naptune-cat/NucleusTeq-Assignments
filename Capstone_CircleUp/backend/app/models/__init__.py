# Importing modules ensures all models are registered with Base
# before create_all or Alembic autogenerate runs
from app.models.user import User
from app.models.activity import Activity
from app.models.participation import ParticipationRequest

__all__ = ["User", "Activity", "ParticipationRequest"]