from __future__ import annotations

import argparse
import json
import os
import random
import time
from datetime import datetime
from pathlib import Path

import torch
import torch.nn as nn
from torch.utils.data import DataLoader
from torchvision import datasets, models, transforms


PROJECT_ROOT = Path(__file__).resolve().parents[1]
DATA_DIR = PROJECT_ROOT / "data"
WEIGHTS_DIR = PROJECT_ROOT / "weights"
DEFAULT_DATA_ROOT = DATA_DIR
DEFAULT_PRETRAINED_PATH = WEIGHTS_DIR / "resnet18_imagenet_pretrained.pth"
DEFAULT_OUTPUT_PATH = WEIGHTS_DIR / "resnet18_food101.pth"
DEFAULT_HISTORY_PATH = WEIGHTS_DIR / "resnet18_food101_history.json"

IMAGENET_MEAN = (0.485, 0.456, 0.406)
IMAGENET_STD = (0.229, 0.224, 0.225)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Train a ResNet18 Food-101 classifier.")
    parser.add_argument("--data-root", type=Path, default=DEFAULT_DATA_ROOT, help="Directory that contains food-101.")
    parser.add_argument(
        "--pretrained-path",
        type=Path,
        default=DEFAULT_PRETRAINED_PATH,
        help="Local ImageNet pretrained ResNet18 state dict path.",
    )
    parser.add_argument(
        "--output-path",
        type=Path,
        default=DEFAULT_OUTPUT_PATH,
        help="Output checkpoint path for the best Food-101 model.",
    )
    parser.add_argument(
        "--history-path",
        type=Path,
        default=DEFAULT_HISTORY_PATH,
        help="JSON file used to persist per-epoch metrics.",
    )
    parser.add_argument("--batch-size", type=int, default=64, help="Mini-batch size.")
    parser.add_argument("--epochs-head", type=int, default=3, help="Epochs that only train the classifier head.")
    parser.add_argument(
        "--epochs-finetune",
        type=int,
        default=2,
        help="Epochs that unfreeze layer4 and continue fine-tuning.",
    )
    parser.add_argument("--lr-head", type=float, default=1e-3, help="Learning rate for head-only training.")
    parser.add_argument("--lr-finetune", type=float, default=1e-4, help="Learning rate for layer4 fine-tuning.")
    parser.add_argument("--weight-decay", type=float, default=1e-4, help="Optimizer weight decay.")
    parser.add_argument(
        "--num-workers",
        type=int,
        default=min(4, os.cpu_count() or 1),
        help="PyTorch DataLoader workers.",
    )
    parser.add_argument("--seed", type=int, default=42, help="Random seed.")
    parser.add_argument(
        "--device",
        type=str,
        default="auto",
        choices=["auto", "cpu", "cuda"],
        help="Execution device. 'auto' prefers CUDA when available.",
    )
    return parser.parse_args()


def set_seed(seed: int) -> None:
    random.seed(seed)
    torch.manual_seed(seed)
    if torch.cuda.is_available():
        torch.cuda.manual_seed_all(seed)


def resolve_device(device_arg: str) -> torch.device:
    if device_arg == "cuda":
        if not torch.cuda.is_available():
            raise RuntimeError("CUDA was requested but is not available.")
        return torch.device("cuda")
    if device_arg == "cpu":
        return torch.device("cpu")
    return torch.device("cuda" if torch.cuda.is_available() else "cpu")


def build_transforms() -> tuple[transforms.Compose, transforms.Compose]:
    train_transform = transforms.Compose(
        [
            transforms.RandomResizedCrop(224),
            transforms.RandomHorizontalFlip(),
            transforms.ToTensor(),
            transforms.Normalize(IMAGENET_MEAN, IMAGENET_STD),
        ]
    )
    eval_transform = transforms.Compose(
        [
            transforms.Resize(256),
            transforms.CenterCrop(224),
            transforms.ToTensor(),
            transforms.Normalize(IMAGENET_MEAN, IMAGENET_STD),
        ]
    )
    return train_transform, eval_transform


def build_dataloaders(
    data_root: Path, batch_size: int, num_workers: int, pin_memory: bool
) -> tuple[DataLoader, DataLoader, list[str]]:
    train_transform, eval_transform = build_transforms()
    train_dataset = datasets.Food101(root=str(data_root), split="train", transform=train_transform, download=False)
    test_dataset = datasets.Food101(root=str(data_root), split="test", transform=eval_transform, download=False)

    persistent_workers = num_workers > 0
    train_loader = DataLoader(
        train_dataset,
        batch_size=batch_size,
        shuffle=True,
        num_workers=num_workers,
        pin_memory=pin_memory,
        persistent_workers=persistent_workers,
    )
    test_loader = DataLoader(
        test_dataset,
        batch_size=batch_size,
        shuffle=False,
        num_workers=num_workers,
        pin_memory=pin_memory,
        persistent_workers=persistent_workers,
    )
    return train_loader, test_loader, list(train_dataset.classes)


def build_model(pretrained_path: Path, num_classes: int) -> models.ResNet:
    if not pretrained_path.exists():
        raise FileNotFoundError(f"Pretrained weights not found: {pretrained_path}")

    model = models.resnet18(weights=None)
    pretrained_state = torch.load(pretrained_path, map_location="cpu")
    model.load_state_dict(pretrained_state)
    model.fc = nn.Linear(model.fc.in_features, num_classes)
    return model


def freeze_backbone(model: nn.Module) -> None:
    for name, parameter in model.named_parameters():
        parameter.requires_grad = name.startswith("fc.")


def unfreeze_layer4_and_fc(model: nn.Module) -> None:
    for parameter in model.parameters():
        parameter.requires_grad = False
    for parameter in model.layer4.parameters():
        parameter.requires_grad = True
    for parameter in model.fc.parameters():
        parameter.requires_grad = True


def set_frozen_modules_to_eval(model: models.ResNet) -> None:
    model.conv1.eval()
    model.bn1.eval()
    model.relu.eval()
    model.maxpool.eval()
    model.layer1.eval()
    model.layer2.eval()
    model.layer3.eval()


def topk_correct(logits: torch.Tensor, targets: torch.Tensor, k: int) -> int:
    k = min(k, logits.shape[1])
    topk = logits.topk(k, dim=1).indices
    return topk.eq(targets.unsqueeze(1)).any(dim=1).sum().item()


def train_one_epoch(
    model: models.ResNet,
    loader: DataLoader,
    criterion: nn.Module,
    optimizer: torch.optim.Optimizer,
    device: torch.device,
    freeze_feature_extractor: bool,
) -> dict[str, float]:
    model.train()
    if freeze_feature_extractor:
        set_frozen_modules_to_eval(model)

    total_loss = 0.0
    total_samples = 0
    total_top1 = 0
    total_top5 = 0

    for images, targets in loader:
        images = images.to(device, non_blocking=True)
        targets = targets.to(device, non_blocking=True)

        optimizer.zero_grad(set_to_none=True)
        logits = model(images)
        loss = criterion(logits, targets)
        loss.backward()
        optimizer.step()

        batch_size = targets.size(0)
        total_loss += loss.item() * batch_size
        total_samples += batch_size
        total_top1 += topk_correct(logits, targets, 1)
        total_top5 += topk_correct(logits, targets, 5)

    return {
        "loss": total_loss / total_samples,
        "top1": total_top1 / total_samples,
        "top5": total_top5 / total_samples,
    }


@torch.no_grad()
def evaluate(
    model: models.ResNet,
    loader: DataLoader,
    criterion: nn.Module,
    device: torch.device,
) -> dict[str, float]:
    model.eval()

    total_loss = 0.0
    total_samples = 0
    total_top1 = 0
    total_top5 = 0

    for images, targets in loader:
        images = images.to(device, non_blocking=True)
        targets = targets.to(device, non_blocking=True)

        logits = model(images)
        loss = criterion(logits, targets)

        batch_size = targets.size(0)
        total_loss += loss.item() * batch_size
        total_samples += batch_size
        total_top1 += topk_correct(logits, targets, 1)
        total_top5 += topk_correct(logits, targets, 5)

    return {
        "loss": total_loss / total_samples,
        "top1": total_top1 / total_samples,
        "top5": total_top5 / total_samples,
    }


def save_checkpoint(
    output_path: Path,
    model: models.ResNet,
    classes: list[str],
    best_metrics: dict[str, float],
    args: argparse.Namespace,
) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    serializable_args = {
        key: str(value) if isinstance(value, Path) else value for key, value in vars(args).items()
    }
    checkpoint = {
        "model_state_dict": model.state_dict(),
        "classes": classes,
        "model_name": "resnet18-food101",
        "model_version": "1.0.0",
        "best_metrics": best_metrics,
        "training_args": serializable_args,
        "saved_at": datetime.now().isoformat(timespec="seconds"),
    }
    torch.save(checkpoint, output_path)


def save_history(history_path: Path, history: list[dict[str, float | int | str]]) -> None:
    history_path.parent.mkdir(parents=True, exist_ok=True)
    history_path.write_text(json.dumps(history, indent=2), encoding="utf-8")


def run_phase(
    phase_name: str,
    epochs: int,
    model: models.ResNet,
    train_loader: DataLoader,
    test_loader: DataLoader,
    criterion: nn.Module,
    optimizer: torch.optim.Optimizer,
    device: torch.device,
    history: list[dict[str, float | int | str]],
    freeze_feature_extractor: bool,
    args: argparse.Namespace,
    classes: list[str],
    best_state: dict[str, object],
) -> None:
    for epoch in range(1, epochs + 1):
        epoch_start = time.perf_counter()
        train_metrics = train_one_epoch(model, train_loader, criterion, optimizer, device, freeze_feature_extractor)
        eval_metrics = evaluate(model, test_loader, criterion, device)
        epoch_seconds = time.perf_counter() - epoch_start

        record = {
            "phase": phase_name,
            "epoch": epoch,
            "train_loss": round(train_metrics["loss"], 6),
            "train_top1": round(train_metrics["top1"], 6),
            "train_top5": round(train_metrics["top5"], 6),
            "val_loss": round(eval_metrics["loss"], 6),
            "val_top1": round(eval_metrics["top1"], 6),
            "val_top5": round(eval_metrics["top5"], 6),
            "seconds": round(epoch_seconds, 2),
        }
        history.append(record)
        save_history(args.history_path, history)

        print(
            f"[{phase_name}] epoch {epoch}/{epochs} "
            f"train_loss={train_metrics['loss']:.4f} "
            f"train_top1={train_metrics['top1']:.4%} "
            f"train_top5={train_metrics['top5']:.4%} "
            f"val_loss={eval_metrics['loss']:.4f} "
            f"val_top1={eval_metrics['top1']:.4%} "
            f"val_top5={eval_metrics['top5']:.4%} "
            f"time={epoch_seconds:.1f}s"
        )

        if eval_metrics["top1"] > float(best_state["val_top1"]):
            best_state["val_top1"] = eval_metrics["top1"]
            best_state["metrics"] = {
                "phase": phase_name,
                "epoch": epoch,
                "val_loss": eval_metrics["loss"],
                "val_top1": eval_metrics["top1"],
                "val_top5": eval_metrics["top5"],
            }
            best_state["state_dict"] = {k: v.detach().cpu().clone() for k, v in model.state_dict().items()}
            save_checkpoint(args.output_path, model, classes, dict(best_state["metrics"]), args)
            print(f"Saved new best checkpoint to: {args.output_path}")


def main() -> None:
    args = parse_args()
    set_seed(args.seed)

    if not (args.data_root / "food-101").exists():
        raise FileNotFoundError(
            f"Food-101 dataset was not found under {args.data_root}. "
            "Run scripts/download_assets.py first."
        )

    device = resolve_device(args.device)
    pin_memory = device.type == "cuda"
    train_loader, test_loader, classes = build_dataloaders(
        args.data_root, args.batch_size, args.num_workers, pin_memory
    )

    model = build_model(args.pretrained_path, num_classes=len(classes))
    model.to(device)

    criterion = nn.CrossEntropyLoss()
    history: list[dict[str, float | int | str]] = []
    best_state: dict[str, object] = {
        "val_top1": float("-inf"),
        "metrics": {},
        "state_dict": None,
    }

    print(f"Using device: {device}")
    print(f"Train samples: {len(train_loader.dataset)}")
    print(f"Test samples: {len(test_loader.dataset)}")
    print(f"Classes: {len(classes)}")
    print(f"Pretrained weights: {args.pretrained_path}")

    if args.epochs_head > 0:
        freeze_backbone(model)
        head_params = [parameter for parameter in model.parameters() if parameter.requires_grad]
        head_optimizer = torch.optim.AdamW(head_params, lr=args.lr_head, weight_decay=args.weight_decay)
        print(f"Starting head-only training for {args.epochs_head} epoch(s)")
        run_phase(
            phase_name="head",
            epochs=args.epochs_head,
            model=model,
            train_loader=train_loader,
            test_loader=test_loader,
            criterion=criterion,
            optimizer=head_optimizer,
            device=device,
            history=history,
            freeze_feature_extractor=True,
            args=args,
            classes=classes,
            best_state=best_state,
        )

    if args.epochs_finetune > 0:
        unfreeze_layer4_and_fc(model)
        finetune_params = [parameter for parameter in model.parameters() if parameter.requires_grad]
        finetune_optimizer = torch.optim.AdamW(
            finetune_params,
            lr=args.lr_finetune,
            weight_decay=args.weight_decay,
        )
        print(f"Starting layer4 fine-tuning for {args.epochs_finetune} epoch(s)")
        run_phase(
            phase_name="finetune",
            epochs=args.epochs_finetune,
            model=model,
            train_loader=train_loader,
            test_loader=test_loader,
            criterion=criterion,
            optimizer=finetune_optimizer,
            device=device,
            history=history,
            freeze_feature_extractor=False,
            args=args,
            classes=classes,
            best_state=best_state,
        )

    if best_state["state_dict"] is None:
        raise RuntimeError("Training did not run. Increase --epochs-head or --epochs-finetune.")

    model.load_state_dict(best_state["state_dict"])
    final_metrics = evaluate(model, test_loader, criterion, device)
    print(
        "Best checkpoint summary: "
        f"val_loss={final_metrics['loss']:.4f} "
        f"val_top1={final_metrics['top1']:.4%} "
        f"val_top5={final_metrics['top5']:.4%}"
    )
    print(f"Checkpoint saved to: {args.output_path}")
    print(f"History saved to: {args.history_path}")


if __name__ == "__main__":
    main()
