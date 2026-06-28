from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

import torch
import torch.nn as nn
from torchvision import models
from torchvision.transforms import InterpolationMode, Normalize, Resize, CenterCrop, ToTensor, Compose


IMAGENET_MEAN = (0.485, 0.456, 0.406)
IMAGENET_STD = (0.229, 0.224, 0.225)


def build_preprocess() -> Compose:
    return Compose(
        [
            Resize(256, interpolation=InterpolationMode.BILINEAR),
            CenterCrop(224),
            ToTensor(),
            Normalize(IMAGENET_MEAN, IMAGENET_STD),
        ]
    )


def build_model(num_classes: int) -> models.ResNet:
    model = models.resnet18(weights=None)
    model.fc = nn.Linear(model.fc.in_features, num_classes)
    return model


@dataclass
class FoodClassifierMetadata:
    model_name: str
    model_version: str
    classes: list[str]
    device: str
    model_path: Path
