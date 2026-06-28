from __future__ import annotations

from fastapi import APIRouter, Depends, Request

from app.core.dependencies import get_classifier_service, get_llm_service
from app.core.security import require_internal_token
from app.schemas.common import ApiResponse, ModelStatus, SimpleMessage, build_response
from app.schemas.food import FoodClassifierInfo
from app.utils.timer import elapsed_ms, now

router = APIRouter(dependencies=[Depends(require_internal_token)])


@router.get("/health", response_model=ApiResponse[SimpleMessage])
async def health() -> ApiResponse[SimpleMessage]:
    start = now()
    return build_response(SimpleMessage(message="UP"), elapsed_ms=elapsed_ms(start))


@router.get("/models/status", response_model=ApiResponse[ModelStatus])
async def model_status(request: Request) -> ApiResponse[ModelStatus]:
    start = now()
    classifier = get_classifier_service(request)
    llm = get_llm_service(request)
    data = ModelStatus(
        status="UP" if classifier.is_loaded else "DEGRADED",
        modelLoaded=classifier.is_loaded,
        llmConfigured=llm.status(),
    )
    return build_response(data, elapsed_ms=elapsed_ms(start))


@router.get("/models/food-classifier/info", response_model=ApiResponse[FoodClassifierInfo])
async def food_classifier_info(request: Request) -> ApiResponse[FoodClassifierInfo]:
    start = now()
    classifier = get_classifier_service(request)
    return build_response(classifier.info(), elapsed_ms=elapsed_ms(start))


@router.get("/models/food-classifier/labels")
async def food_classifier_labels(request: Request) -> ApiResponse[list[dict[str, str]]]:
    start = now()
    classifier = get_classifier_service(request)
    return build_response(classifier.labels(), elapsed_ms=elapsed_ms(start))
