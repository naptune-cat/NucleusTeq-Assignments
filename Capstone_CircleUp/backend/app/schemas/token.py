from pydantic import BaseModel


# --- What login returns ---
class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"


# --- What's inside the JWT payload ---
class TokenData(BaseModel):
    user_id: int | None = None