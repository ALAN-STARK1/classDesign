from __future__ import annotations

from fastapi import APIRouter, Depends, File, Form, Request, UploadFile

from app.core.config import get_settings
from app.core.dependencies import get_classifier_service
from app.core.security import require_internal_token
from app.schemas.common import ApiResponse, build_response
from app.schemas.food import FoodClassifierResult
from app.utils.image import image_from_upload_file
from app.utils.timer import elapsed_ms, now

router = APIRouter(prefix="/food", dependencies=[Depends(require_internal_token)])


@router.post("/classify", response_model=ApiResponse[FoodClassifierResult])
async def classify_food(
    request: Request,
    image: UploadFile = File(...),
    topK: int = Form(default=5),
) -> ApiResponse[FoodClassifierResult]:
    start = now()
    settings = get_settings()
    classifier = get_classifier_service(request)
    pil_image = await image_from_upload_file(image, settings.max_image_bytes)
    result = classifier.predict(pil_image, top_k=topK)
    return build_response(result, elapsed_ms=elapsed_ms(start))
