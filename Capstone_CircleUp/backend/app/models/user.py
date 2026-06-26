from __future__ import annotations

from typing import TYPE_CHECKING

from sqlalchemy import BigInteger, String, Text, CheckConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base

if TYPE_CHECKING:
    from app.models.activity import Activity
    from app.models.participation import ParticipationRequest


class User(Base):
    __tablename__ = "users"

    id:              Mapped[int]        = mapped_column(BigInteger, primary_key=True, index=True)
    name:            Mapped[str]        = mapped_column(String(100))
    email:           Mapped[str]        = mapped_column(String(255), unique=True, index=True)
    hashed_password: Mapped[str]
    phone_number:    Mapped[str | None] = mapped_column(String(20))
    city:            Mapped[str | None] = mapped_column(String(100))
    bio:             Mapped[str | None] = mapped_column(Text)
    gender:          Mapped[str | None] = mapped_column(String(20))  

    __table_args__ = (
        CheckConstraint(
            "gender IN ('male', 'female', 'other', 'prefer_not_to_say')",
            name="ck_users_gender"
        ),
    )

    created_activities:     Mapped[list[Activity]]             = relationship("Activity", back_populates="creator")
    participation_requests: Mapped[list[ParticipationRequest]] = relationship("ParticipationRequest", back_populates="requester")