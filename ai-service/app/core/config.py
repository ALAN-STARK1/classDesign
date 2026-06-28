from __future__ import annotations

import os
from dataclasses import dataclass
from functools import lru_cache
from pathlib import Path


@dataclass(frozen=True)
class Settings:
    host: str = "0.0.0.0"
    port: int = 8000
    internal_token: str = "change-me"
    food_model_path: Path = Path("weights/resnet18_food101.pth")
    device: str = "cpu"
    deepseek_api_key: str = ""
    deepseek_base_url: str = "https://api.deepseek.com"
    deepseek_model: str = "deepseek-v4-flash"
    deepseek_timeout: float = 60.0
    max_image_bytes: int = 10 * 1024 * 1024

    @property
    def project_root(self) -> Path:
        return Path(__file__).resolve().parents[2]

    @property
    def resolved_food_model_path(self) -> Path:
        path = self.food_model_path
        return path if path.is_absolute() else self.project_root / path


@lru_cache(maxsize=1)
def get_settings() -> Settings:
    return Settings(
        host=os.getenv("AI_SERVICE_HOST", "0.0.0.0"),
        port=int(os.getenv("AI_SERVICE_PORT", "8000")),
        internal_token=os.getenv("INTERNAL_TOKEN", "change-me"),
        food_model_path=Path(os.getenv("FOOD_MODEL_PATH", "weights/resnet18_food101.pth")),
        device=os.getenv("DEVICE", "cpu"),
        deepseek_api_key=os.getenv("DEEPSEEK_API_KEY", ""),
        deepseek_base_url=os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com").rstrip("/"),
        deepseek_model=os.getenv("DEEPSEEK_MODEL", "deepseek-v4-flash"),
        deepseek_timeout=float(os.getenv("DEEPSEEK_TIMEOUT", "60")),
        max_image_bytes=int(os.getenv("MAX_IMAGE_BYTES", str(10 * 1024 * 1024))),
    )
