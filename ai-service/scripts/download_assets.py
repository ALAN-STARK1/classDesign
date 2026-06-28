from __future__ import annotations

import hashlib
from pathlib import Path
import tarfile

import requests
import torch
from torchvision import datasets, models
from torchvision.models import ResNet18_Weights


PROJECT_ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = PROJECT_ROOT / "data"
WEIGHTS_DIR = PROJECT_ROOT / "weights"
FOOD101_DIR = DATA_DIR / "food-101"
FOOD101_ARCHIVE = DATA_DIR / "food-101.tar.gz"
FOOD101_URL = datasets.Food101._URL
FOOD101_MD5 = datasets.Food101._MD5


def ensure_directories() -> None:
    DATA_DIR.mkdir(parents=True, exist_ok=True)
    WEIGHTS_DIR.mkdir(parents=True, exist_ok=True)


def download_resnet18_weights() -> Path:
    weights = ResNet18_Weights.DEFAULT
    model = models.resnet18(weights=weights)
    # Save the pretrained state dict locally so later training/inference can reuse it directly.
    output_path = WEIGHTS_DIR / "resnet18_imagenet_pretrained.pth"
    torch.save(model.state_dict(), output_path)
    return output_path


def compute_md5(path: Path, chunk_size: int = 1024 * 1024) -> str:
    digest = hashlib.md5()
    with path.open("rb") as file_obj:
        while chunk := file_obj.read(chunk_size):
            digest.update(chunk)
    return digest.hexdigest()


def download_with_resume(url: str, destination: Path, chunk_size: int = 1024 * 1024) -> Path:
    existing_size = destination.stat().st_size if destination.exists() else 0
    headers = {}
    mode = "wb"

    if existing_size > 0:
        headers["Range"] = f"bytes={existing_size}-"
        mode = "ab"

    with requests.get(url, headers=headers, stream=True, timeout=60) as response:
        if response.status_code == 416:
            return destination
        if existing_size > 0 and response.status_code == 200:
            existing_size = 0
            mode = "wb"
        response.raise_for_status()
        with destination.open(mode) as file_obj:
            for chunk in response.iter_content(chunk_size=chunk_size):
                if chunk:
                    file_obj.write(chunk)
    return destination


def extract_archive(archive_path: Path, destination_dir: Path) -> None:
    with tarfile.open(archive_path, "r:gz") as tar:
        tar.extractall(path=destination_dir)


def download_food101() -> Path:
    if FOOD101_DIR.exists():
        return FOOD101_DIR

    download_with_resume(FOOD101_URL, FOOD101_ARCHIVE)
    if compute_md5(FOOD101_ARCHIVE) != FOOD101_MD5:
        FOOD101_ARCHIVE.unlink(missing_ok=True)
        raise RuntimeError("Food-101 archive checksum mismatch. Removed partial archive; rerun the script.")

    extract_archive(FOOD101_ARCHIVE, DATA_DIR)
    return FOOD101_DIR


def main() -> None:
    ensure_directories()
    weights_path = download_resnet18_weights()
    food101_path = download_food101()
    print(f"Pretrained weights saved to: {weights_path}")
    print(f"Food-101 dataset ready at: {food101_path}")


if __name__ == "__main__":
    main()
