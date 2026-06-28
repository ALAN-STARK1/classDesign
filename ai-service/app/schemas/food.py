from __future__ import annotations

from pydantic import BaseModel, Field


class FoodRecognition(BaseModel):
    label: str
    displayName: str
    confidence: float = Field(ge=0.0, le=1.0)


class FoodClassifierInfo(BaseModel):
    modelName: str
    modelVersion: str
    numClasses: int
    device: str


class FoodClassifierResult(BaseModel):
    modelName: str
    modelVersion: str
    results: list[FoodRecognition]
