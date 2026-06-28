from __future__ import annotations

from fastapi import Request

from app.services.classifier_service import FoodClassifierService
from app.services.llm_recipe_service import LLMRecipeService


def get_classifier_service(request: Request) -> FoodClassifierService:
    return request.app.state.classifier_service


def get_llm_service(request: Request) -> LLMRecipeService:
    return request.app.state.llm_service
