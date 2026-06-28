from __future__ import annotations

from pathlib import Path

import torch
import torch.nn.functional as F
from PIL import Image

from app.core.errors import AppError
from app.models.food_classifier import FoodClassifierMetadata, build_model, build_preprocess
from app.schemas.food import FoodClassifierInfo, FoodClassifierResult, FoodRecognition


def _pretty_name(label: str) -> str:
    return label.replace("_", " ").strip().title()


class FoodClassifierService:
    def __init__(self, model_path: Path, device: str) -> None:
        self.model_path = model_path
        self.device_name = device
        self.device = self._resolve_device(device)
        self.preprocess = build_preprocess()
        self.metadata: FoodClassifierMetadata | None = None
        self.model: torch.nn.Module | None = None
        self._load()

    def _resolve_device(self, device: str) -> torch.device:
        if device == "cuda" and not torch.cuda.is_available():
            return torch.device("cpu")
        if device == "auto":
            return torch.device("cuda" if torch.cuda.is_available() else "cpu")
        return torch.device(device)

    def _load(self) -> None:
        if not self.model_path.exists():
            raise AppError(f"Model checkpoint not found: {self.model_path}", 500)

        checkpoint = torch.load(self.model_path, map_location="cpu")
        classes = checkpoint.get("classes")
        if not isinstance(classes, list) or not classes:
            raise AppError("Invalid model checkpoint: classes missing", 500)

        model = build_model(len(classes))
        state_dict = checkpoint.get("model_state_dict")
        if not isinstance(state_dict, dict):
            raise AppError("Invalid model checkpoint: model_state_dict missing", 500)
        model.load_state_dict(state_dict)
        model.to(self.device)
        model.eval()

        self.model = model
        self.metadata = FoodClassifierMetadata(
            model_name=str(checkpoint.get("model_name", "resnet18-food101")),
            model_version=str(checkpoint.get("model_version", "1.0.0")),
            classes=classes,
            device=str(self.device),
            model_path=self.model_path,
        )

    @property
    def is_loaded(self) -> bool:
        return self.model is not None and self.metadata is not None

    def info(self) -> FoodClassifierInfo:
        if not self.metadata:
            raise AppError("Model is not loaded", 500)
        return FoodClassifierInfo(
            modelName=self.metadata.model_name,
            modelVersion=self.metadata.model_version,
            numClasses=len(self.metadata.classes),
            device=self.metadata.device,
        )

    def labels(self) -> list[dict[str, str]]:
        if not self.metadata:
            raise AppError("Model is not loaded", 500)
        return [{"label": label, "displayName": _pretty_name(label)} for label in self.metadata.classes]

    @torch.no_grad()
    def predict(self, image: Image.Image, top_k: int = 5) -> FoodClassifierResult:
        if not self.model or not self.metadata:
            raise AppError("Model is not loaded", 500)

        top_k = max(1, min(top_k, 10, len(self.metadata.classes)))
        tensor = self.preprocess(image).unsqueeze(0).to(self.device)
        logits = self.model(tensor)
        probs = F.softmax(logits, dim=1)[0]
        values, indices = torch.topk(probs, k=top_k)
        results = [
            FoodRecognition(
                label=self.metadata.classes[idx.item()],
                displayName=_pretty_name(self.metadata.classes[idx.item()]),
                confidence=float(value.item()),
            )
            for value, idx in zip(values, indices)
        ]
        return FoodClassifierResult(
            modelName=self.metadata.model_name,
            modelVersion=self.metadata.model_version,
            results=results,
        )
