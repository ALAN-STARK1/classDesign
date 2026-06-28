from __future__ import annotations

from dataclasses import dataclass


@dataclass
class AppError(Exception):
    message: str
    status_code: int = 400
    code: str | None = None
