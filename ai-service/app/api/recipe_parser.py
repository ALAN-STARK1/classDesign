from __future__ import annotations

from fastapi import APIRouter, Depends, File, Form, Request, UploadFile
from pydantic import ValidationError

from app.core.config import get_settings
from app.core.dependencies import get_classifier_service, get_llm_service
from app.core.errors import AppError
from app.core.security import require_internal_token
from app.schemas.common import ApiResponse, build_response
from app.schemas.recipe import RecipeParseRequest, RecipeParseResult
from app.utils.image import image_from_upload_file
from app.utils.timer import elapsed_ms, now

router = APIRouter(prefix="/recipes", dependencies=[Depends(require_internal_token)])


@router.post("/parse", response_model=ApiResponse[RecipeParseResult])
async def parse_recipe(request: RecipeParseRequest, req: Request) -> ApiResponse[RecipeParseResult]:
    start = now()
    llm = get_llm_service(req)
    result = await llm.parse_recipe(request)
    return build_response(result, elapsed_ms=elapsed_ms(start))


@router.post("/parse-from-image", response_model=ApiResponse[RecipeParseResult])
async def parse_recipe_from_image(
    req: Request,
    image: UploadFile = File(...),
    context: str = Form(...),
    topK: int = Form(default=5),
) -> ApiResponse[RecipeParseResult]:
    start = now()
    settings = get_settings()
    classifier = get_classifier_service(req)
    llm = get_llm_service(req)
    pil_image = await image_from_upload_file(image, settings.max_image_bytes)
    recognition = classifier.predict(pil_image, top_k=topK)
    try:
        payload = RecipeParseRequest.model_validate_json(context)
    except ValidationError as exc:
        raise AppError(f"Invalid context JSON: {exc}", 400) from exc
    request = payload.model_copy(update={"recognitionResults": recognition.results})
    result = await llm.parse_recipe(request)
    return build_response(result, elapsed_ms=elapsed_ms(start))
