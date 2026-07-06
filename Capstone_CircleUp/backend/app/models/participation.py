from __future__ import annotations

from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import BigInteger, DateTime, Enum, ForeignKey, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.sql import func

from app.core.database import Base
from app.enums.participation import RequestStatus 

if TYPE_CHECKING:
    from app.models.user import User
    from app.models.activity import Activity


class ParticipationRequest(Base):
    __tablename__ = "participation_requests"

    id:           Mapped[int]           = mapped_column(BigInteger, primary_key=True, index=True)
    requester_id: Mapped[int]           = mapped_column(BigInteger, ForeignKey("users.id"), index=True)
    activity_id:  Mapped[int]           = mapped_column(BigInteger, ForeignKey("activities.id"), index=True)
    status:       Mapped[RequestStatus] = mapped_column(
        Enum(RequestStatus), default=RequestStatus.pending, server_default="pending"
    )
    requested_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())

    requester: Mapped[User]     = relationship("User", back_populates="participation_requests")
    activity:  Mapped[Activity] = relationship("Activity", back_populates="participation_requests")

    __table_args__ = (
        UniqueConstraint("requester_id", "activity_id", name="uq_user_activity_request"),
    )

    @property
    def requester_name(self) -> str | None:
        return self.requester.name if self.requester else None

    @property
    def activity_title(self) -> str | None:
        return self.activity.title if self.activity else None

    @property
    def activity_date(self) -> datetime | None:
        return self.activity.activity_date if self.activity else None

    @property
    def activity_status(self) -> str | None:
        return self.activity.status.value if self.activity else None

    @property
    def host_name(self) -> str | None:
        return self.activity.creator.name if self.activity and self.activity.creator else None

    @property
    def host_phone(self) -> str | None:
        return self.activity.creator.phone_number if self.activity and self.activity.creator else None