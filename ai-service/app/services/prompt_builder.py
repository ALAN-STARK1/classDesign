from __future__ import annotations

import json

from app.schemas.recipe import RecipeParseRequest


SYSTEM_PROMPT = """你是智能健康膳食管理系统的AI菜谱解析助手。
只输出合法JSON，不要输出Markdown、解释、代码块或多余文本。
必须符合以下字段：
recipeName, recognizedFoods, description, suitability, ingredients, nutritionEstimate, cookingSteps, healthTips, warnings
其中：
- recognizedFoods 必须是数组，元素包含 label, displayName, confidence
- suitability 必须包含 score 和 reason
- ingredients 必须是数组，元素包含 name, amount, unit
- nutritionEstimate 必须包含 calories, protein, fat, carbohydrate
- cookingSteps, healthTips, warnings 都必须是数组
- 所有字段都必须可被JSON解析
"""


def build_recipe_prompt(request: RecipeParseRequest) -> str:
    payload = {
        "userId": request.userId,
        "userInput": request.userInput,
        "recognitionResults": [item.model_dump() for item in request.recognitionResults],
        "healthProfile": request.healthProfile.model_dump() if request.healthProfile else None,
        "preferences": request.preferences.model_dump() if request.preferences else None,
        "restrictions": request.restrictions.model_dump() if request.restrictions else None,
    }
    return (
        "请基于下面的用户信息、食物识别结果和补充描述，生成一个中文菜谱JSON。\n"
        f"{json.dumps(payload, ensure_ascii=False, indent=2)}"
    )
