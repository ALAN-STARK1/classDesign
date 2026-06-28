from __future__ import annotations

import json

from pydantic import ValidationError

from app.schemas.recipe import RecipeParseResult


def parse_recipe_json(raw: str | dict) -> RecipeParseResult:
    if isinstance(raw, str):
        payload = json.loads(raw)
    else:
        payload = raw
    return RecipeParseResult.model_validate(payload)


def validate_recipe_json(raw: str | dict) -> tuple[RecipeParseResult | None, str | None]:
    try:
        return parse_recipe_json(raw), None
    except (json.JSONDecodeError, ValidationError, TypeError, ValueError) as exc:
        return None, str(exc)
