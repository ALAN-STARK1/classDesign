from __future__ import annotations

from pydantic import BaseModel, Field

from app.schemas.food import FoodRecognition


class HealthProfile(BaseModel):
    gender: str | None = None
    heightCm: float | None = None
    weightKg: float | None = None
    bmi: float | None = None
    dailyCalorieTarget: float | None = None


class Preferences(BaseModel):
    taste: list[str] = Field(default_factory=list)
    goal: str | None = None


class Restrictions(BaseModel):
    allergens: list[str] = Field(default_factory=list)
    dietRestrictions: list[str] = Field(default_factory=list)


class RecipeIngredient(BaseModel):
    name: str
    amount: float
    unit: str


class NutritionEstimate(BaseModel):
    calories: float
    protein: float
    fat: float
    carbohydrate: float


class Suitability(BaseModel):
    score: int = Field(ge=0, le=100)
    reason: str


class RecipeParseRequest(BaseModel):
    userId: int | None = None
    userInput: str
    recognitionResults: list[FoodRecognition] = Field(default_factory=list)
    healthProfile: HealthProfile | None = None
    preferences: Preferences | None = None
    restrictions: Restrictions | None = None


class RecipeParseResult(BaseModel):
    recipeName: str
    recognizedFoods: list[FoodRecognition] = Field(default_factory=list)
    description: str
    imagePrompt: str = ""
    visualDescription: str = ""
    suitability: Suitability
    ingredients: list[RecipeIngredient] = Field(default_factory=list)
    nutritionEstimate: NutritionEstimate
    cookingSteps: list[str] = Field(default_factory=list)
    healthTips: list[str] = Field(default_factory=list)
    warnings: list[str] = Field(default_factory=list)
    shoppingHints: list[str] = Field(default_factory=list)


class ParsedRecipeResponse(BaseModel):
    result: RecipeParseResult
