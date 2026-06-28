from __future__ import annotations

import io

from fastapi import UploadFile
from PIL import Image, UnidentifiedImageError

from app.core.errors import AppError


def image_from_bytes(content: bytes) -> Image.Image:
    if not content:
        raise AppError("Empty image payload", 400)
    try:
        with Image.open(io.BytesIO(content)) as img:
            return img.convert("RGB")
    except UnidentifiedImageError as exc:
        raise AppError("Invalid image format", 400) from exc


async def image_from_upload_file(file: UploadFile, max_bytes: int) -> Image.Image:
    content = await file.read()
    if len(content) > max_bytes:
        raise AppError("Image file too large", 400)
    return image_from_bytes(content)
