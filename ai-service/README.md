# ai-service

## Asset Setup

Run the existing virtual environment Python to create local asset directories and download:

```powershell
venv\Scripts\python.exe scripts\download_assets.py
```

This prepares:

- `data/food-101`
- `weights/resnet18_imagenet_pretrained.pth`

## Run

```powershell
Copy-Item .env.example .env
venv\Scripts\python.exe -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```
