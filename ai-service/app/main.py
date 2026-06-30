from __future__ import annotations

import logging
from contextlib import asynccontextmanager

from pathlib import Path

from dotenv import load_dotenv

load_dotenv(Path(__file__).resolve().parents[1] / ".env")

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.api.food_classifier import router as food_router
from app.api.health import router as health_router
from app.api.llm_chat import router as llm_router
from app.api.recipe_parser import router as recipe_router
from app.core.config import get_settings
from app.core.errors import AppError
from app.schemas.common import ApiResponse
from app.services.classifier_service import FoodClassifierService
from app.services.llm_chat_service import LLMChatService
from app.services.llm_recipe_service import LLMRecipeService
from app.utils.timer import elapsed_ms, now


logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s %(message)s")
logger = logging.getLogger("ai-service")


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = get_settings()
    app.state.settings = settings
    app.state.classifier_service = FoodClassifierService(settings.resolved_food_model_path, settings.device)
    app.state.llm_service = LLMRecipeService(settings)
    app.state.chat_service = LLMChatService(settings)
    yield


app = FastAPI(title="ai-service", version="1.0.0", lifespan=lifespan)
app.include_router(health_router, prefix="/ai/v1")
app.include_router(food_router, prefix="/ai/v1")
app.include_router(recipe_router, prefix="/ai/v1")
app.include_router(llm_router, prefix="/ai/v1")


@app.exception_handler(AppError)
async def app_error_handler(_: Request, exc: AppError) -> JSONResponse:
    return JSONResponse(
        status_code=exc.status_code,
        content=ApiResponse(success=False, errorMessage=exc.message, elapsedMs=0).model_dump(),
    )


@app.exception_handler(RequestValidationError)
async def validation_error_handler(_: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(
        status_code=422,
        content=ApiResponse(success=False, errorMessage=str(exc), elapsedMs=0).model_dump(),
    )


@app.middleware("http")
async def timing_middleware(request: Request, call_next):
    start = now()
    try:
        response = await call_next(request)
    except Exception:
        raise
    response.headers["X-Elapsed-Ms"] = str(elapsed_ms(start))
    return response
