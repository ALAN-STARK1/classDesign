from __future__ import annotations

import json
from typing import Any

import httpx

from app.core.config import Settings
from app.core.errors import AppError
from app.schemas.recipe import RecipeParseRequest, RecipeParseResult
from app.services.json_validator import validate_recipe_json
from app.services.prompt_builder import SYSTEM_PROMPT, build_recipe_prompt


class LLMRecipeService:
    def __init__(self, settings: Settings) -> None:
        self.settings = settings
        self.configured = bool(settings.deepseek_api_key)

    def status(self) -> bool:
        return self.configured

    async def parse_recipe(self, request: RecipeParseRequest) -> RecipeParseResult:
        payload = await self._call_model(build_recipe_prompt(request))
        recipe, error = validate_recipe_json(payload)
        if recipe is not None:
            return recipe

        repair_prompt = (
            "请修复以下内容，使其成为合法且完整的JSON，并且只输出JSON：\n"
            f"{payload}\n"
            f"错误信息：{error}"
        )
        repaired = await self._call_model(repair_prompt)
        recipe, error = validate_recipe_json(repaired)
        if recipe is None:
            raise AppError(f"LLM JSON validation failed: {error}", 502)
        return recipe

    async def _call_model(self, user_prompt: str) -> str:
        if not self.configured:
            raise AppError("DeepSeek API key is not configured", 500)

        url = f"{self.settings.deepseek_base_url}/chat/completions"
        body: dict[str, Any] = {
            "model": self.settings.deepseek_model,
            "messages": [
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": user_prompt},
            ],
            "response_format": {"type": "json_object"},
            "temperature": 0.2,
            "max_tokens": 2048,
        }
        headers = {"Authorization": f"Bearer {self.settings.deepseek_api_key}"}
        timeout = httpx.Timeout(self.settings.deepseek_timeout)
        async with httpx.AsyncClient(timeout=timeout) as client:
            response = await client.post(url, json=body, headers=headers)
        if response.status_code >= 400:
            raise AppError(f"DeepSeek API error: {response.status_code} {response.text}", 502)

        try:
            data = response.json()
        except ValueError as exc:
            raise AppError("DeepSeek API returned non-JSON response", 502) from exc

        try:
            content = data["choices"][0]["message"]["content"]
        except (KeyError, IndexError, TypeError) as exc:
            raise AppError("Unexpected DeepSeek API response format", 502) from exc

        if isinstance(content, dict):
            return json.dumps(content, ensure_ascii=False)
        if not isinstance(content, str):
            return json.dumps(content, ensure_ascii=False)
        return content
