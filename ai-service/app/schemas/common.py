from __future__ import annotations

from typing import Generic, TypeVar

from pydantic import BaseModel, Field


T = TypeVar("T")


class ApiResponse(BaseModel, Generic[T]):
    success: bool = True
    data: T | None = None
    errorMessage: str | None = None
    elapsedMs: int = 0


class ModelStatus(BaseModel):
    status: str
    modelLoaded: bool
    llmConfigured: bool


class SimpleMessage(BaseModel):
    message: str = Field(default="")


def build_response(data: T | None = None, error_message: str | None = None, elapsed_ms: int = 0) -> ApiResponse[T]:
    return ApiResponse(data=data, success=error_message is None, errorMessage=error_message, elapsedMs=elapsed_ms)
