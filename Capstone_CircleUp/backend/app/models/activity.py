from __future__ import annotations

import enum
from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import BigInteger, DateTime, Enum, ForeignKey, Integer, String, Text, CheckConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base

if TYPE_CHECKING:
    from app.models.user import User
    from app.models.participation import ParticipationRequest


class ActivityStatus(str, enum.Enum):
    open      = "open"
    full      = "full"
    cancelled = "cancelled"


class Activity(Base):
    __tablename__ = "activities"

    id:               Mapped[int]            = mapped_column(BigInteger, primary_key=True, index=True)
    title:            Mapped[str]            = mapped_column(String(200))
    description:      Mapped[str]            = mapped_column(Text)
    category:         Mapped[str]            = mapped_column(String(100))
    location:         Mapped[str]            = mapped_column(String(200))
    max_participants: Mapped[int]            = mapped_column(Integer)
    activity_date:    Mapped[datetime]       = mapped_column(DateTime(timezone=True))
    status:           Mapped[ActivityStatus] = mapped_column(
        Enum(ActivityStatus), default=ActivityStatus.open, server_default="open"
    )
    gender_filter:    Mapped[str]            = mapped_column(String(50), default="all", server_default="all")
    creator_id:       Mapped[int]            = mapped_column(BigInteger, ForeignKey("users.id"), index=True)

    __table_args__ = (
        CheckConstraint("gender_filter IN ('all', 'female_only')", name="ck_gender_filter"),
    )

    creator:                Mapped[User]                      = relationship("User", back_populates="created_activities")
    participation_requests: Mapped[list[ParticipationRequest]] = relationship("ParticipationRequest", back_populates="activity")

    @property
    def creator_name(self) -> str | None:
        return self.creator.name if self.creator else None