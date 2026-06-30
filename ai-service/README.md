# ai-service

Python AI 服务（食物图像识别 + 菜谱解析 + 营养顾问 LLM），默认端口 **8000**。

> **注意**：必须在 `ai-service` 目录下启动，不要在项目根目录 `foodManagement` 运行 uvicorn。
> 根目录没有 `requirements.txt` 和 `app` 模块，会报 `No module named 'app'`。

## 一键启动（Windows）

在项目根目录双击或执行：

```bat
start-ai-service.bat
```

或在 `ai-service` 目录执行：

```bat
start.bat
```

## 手动启动

```powershell
cd ai-service

# 首次：创建虚拟环境并安装依赖
py -3.12 -m venv venv
venv\Scripts\python.exe -m pip install -r requirements.txt

# 首次：配置环境变量
Copy-Item .env.example .env
# 编辑 .env，填写 DEEPSEEK_API_KEY，INTERNAL_TOKEN 需与后端 application.yml 一致

# 启动
venv\Scripts\python.exe -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```

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
