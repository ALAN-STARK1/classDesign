# 智能健康膳食管理系统 ai-service 设计文档

## 1. 文档说明

本文档描述智能健康膳食管理系统中独立 AI 服务 `ai-service` 的设计。该服务由 Python + FastAPI 实现，面向 Spring Boot 业务后端提供食物图片识别和 AI 解析菜谱能力。

`ai-service` 不直接面向前端用户开放，不负责用户登录、权限控制、菜谱正式发布、膳食记录落库等业务能力。前端请求统一进入 Spring Boot 后端，由 Spring Boot 完成鉴权、用户数据查询、文件保存和业务落库，再通过内部接口调用 `ai-service`。

## 2. 建设目标

- 使用 ResNet18 + Food-101 构建本地食物图片分类能力，支持上传图片后返回 Top-K 食物识别结果。
- 使用迁移学习方式训练模型：自动下载 ResNet18 ImageNet 预训练权重，自动下载 Food-101 数据集，微调得到 `resnet18_food101.pth`。
- 使用 DeepSeek OpenAI 兼容接口实现结构化菜谱解析，要求 LLM 返回可被后端解析和展示的 JSON。
- 将图片识别结果、用户健康档案、饮食偏好、过敏源、禁忌和用户补充描述组合为 Prompt，生成适合用户的菜谱建议。
- 提供健康检查、模型状态、图片分类、AI 解析菜谱等内部接口。
- 记录模型推理耗时、LLM 调用耗时、成功状态和错误信息，便于课程答辩演示和问题定位。

## 3. 非目标说明

- 不实现医学诊断，只提供饮食参考建议。
- 不在 `ai-service` 中保存用户密码、JWT、完整用户资料等敏感信息。
- 不由 `ai-service` 直接写入 MySQL 业务表，业务数据持久化由 Spring Boot 完成。
- 不要求在线实时训练模型，模型训练属于离线阶段，运行阶段只加载训练后的权重文件。

## 4. 技术选型

| 层次     | 技术                           | 说明                                 |
| -------- | ------------------------------ | ------------------------------------ |
| Web 服务 | FastAPI                        | 提供内部 HTTP API                    |
| 模型框架 | PyTorch                        | 训练和推理 ResNet18                  |
| 图像处理 | Pillow, torchvision.transforms | 图片读取、缩放、归一化               |
| 分类模型 | ResNet18                       | 使用 ImageNet 预训练权重进行迁移学习 |
| 数据集   | Food-101                       | 食物分类数据集，共 101 类            |
| LLM      | DeepSeek OpenAI 兼容接口       | 生成结构化菜谱 JSON                  |
| 配置管理 | `.env` / 环境变量            | 管理 API Key、模型路径、服务端口     |

## 5. 总体架构

```text
Vue3 前端
  |
  | HTTP / JSON / Multipart
  v
Spring Boot 业务后端
  |-- 鉴权、文件保存、用户健康上下文查询
  |-- 食材库 / 菜谱库匹配
  |-- 业务落库和统一响应
  |
  | 内部 HTTP 调用，携带 X-Internal-Token
  v
Python FastAPI ai-service
  |-- FoodClassifier: ResNet18 + Food-101 图片分类
  |-- RecipeParser: LLM 结构化菜谱解析
  |-- PromptBuilder: 用户上下文和识别结果组装
  |-- JsonValidator: LLM JSON 输出校验
```

## 6. 推荐目录结构

```text
ai-service/
  app/
    main.py
    api/
      health.py
      food_classifier.py
      recipe_parser.py
    core/
      config.py
      security.py
    schemas/
      food.py
      recipe.py
      common.py
    services/
      classifier_service.py
      llm_recipe_service.py
      prompt_builder.py
      json_validator.py
    models/
      food_classifier.py
    utils/
      image.py
      timer.py
  scripts/
    train_food101.py
    evaluate_food101.py
  data/
    food-101/
  weights/
    resnet18_food101.pth
  requirements.txt
  .env.example
  README.md
```

## 7. 模型训练设计

### 7.1 训练目标

训练一个基于 ResNet18 的 Food-101 食物分类模型。模型输入为食物图片，输出 101 个 Food-101 类别的概率分布，服务接口返回置信度最高的 Top-K 结果。

### 7.2 权重与数据下载方式

项目不要求手动去网页下载模型和数据集，训练脚本可自动完成以下下载：

- `torchvision.models.resnet18(weights=ResNet18_Weights.DEFAULT)` 自动下载 ResNet18 的 ImageNet 预训练权重。
- `torchvision.datasets.Food101(download=True)` 自动下载 Food-101 数据集。

需要注意：torchvision 官方提供的是 ImageNet 预训练 ResNet18，不是已经在 Food-101 上训练好的 ResNet18。系统应通过迁移学习微调得到自己的 `resnet18_food101.pth`。

### 7.3 迁移学习流程

```text
加载 ImageNet 预训练 ResNet18
  -> 替换最后的全连接层，输出类别数改为 101
  -> 加载 Food-101 训练集和测试集
  -> 图片 resize 到 224x224，并按 ImageNet 均值方差归一化
  -> 可选：冻结主干网络，仅训练最后一层
  -> 可选：解冻部分层，进行少量 epoch 微调
  -> 保存权重文件和类别映射
```

### 7.4 训练脚本核心逻辑

```python
import torch
import torch.nn as nn
from torchvision import models
from torchvision.models import ResNet18_Weights

num_classes = 101

weights = ResNet18_Weights.DEFAULT
model = models.resnet18(weights=weights)
model.fc = nn.Linear(model.fc.in_features, num_classes)

torch.save({
    "model_state_dict": model.state_dict(),
    "classes": train_dataset.classes,
    "model_name": "resnet18-food101",
    "model_version": "1.0.0"
}, "weights/resnet18_food101.pth")
```

### 7.5 课程项目训练策略

为了控制开发成本，课程项目可以采用轻量训练策略：

| 策略         | 说明                                                  |
| ------------ | ----------------------------------------------------- |
| 快速演示策略 | 冻结 ResNet18 主干，只训练最后一层，训练 1-3 个 epoch |
| 较完整策略   | 先训练最后一层，再解冻`layer4` 微调 3-5 个 epoch    |
| 答辩说明     | 说明训练阶段离线完成，部署阶段只加载本地权重推理      |

## 8. 模型推理设计

### 8.1 启动加载

FastAPI 启动时加载 `weights/resnet18_food101.pth`，初始化模型和类别映射。运行阶段不再下载 Food-101 数据集，也不执行训练逻辑。

```text
读取权重文件
  -> 创建 ResNet18 结构
  -> 替换 fc 为 101 类输出
  -> 加载 state_dict
  -> 设置 model.eval()
  -> 保存 classes 标签列表
```

### 8.2 图片预处理

```text
接收 Multipart 图片
  -> Pillow 打开图片并转为 RGB
  -> Resize 到 224x224
  -> ToTensor
  -> Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
  -> 增加 batch 维度
```

### 8.3 Top-K 输出

模型推理后使用 softmax 得到概率，取置信度最高的 K 个类别。返回英文标签、中文展示名、置信度、模型名称、模型版本和耗时。

## 9. AI 解析菜谱功能设计

### 9.1 功能定位

AI 解析菜谱功能是本项目的核心智能能力。它不是简单的聊天接口，而是一个“图片识别 + 用户健康上下文 + LLM 结构化生成”的组合流程。

```text
用户上传食物图片或输入想吃的食物
  -> Spring Boot 保存图片并查询用户健康上下文
  -> ai-service 使用 ResNet18 识别图片 Top-K 食物
  -> ai-service 将识别结果、用户上下文、用户补充描述传入 LLM
  -> LLM 返回结构化菜谱 JSON
  -> ai-service 校验 JSON 结构
  -> Spring Boot 保存 AI 菜谱结果
  -> 前端展示菜谱、营养估算、适配原因和做菜步骤
```

### 9.2 输入上下文

| 数据         | 来源        | 说明                                |
| ------------ | ----------- | ----------------------------------- |
| 图片识别结果 | ResNet18    | Top-K 食物标签和置信度              |
| 健康档案     | Spring Boot | 身高、体重、BMI、目标体重、活动水平 |
| 饮食目标     | Spring Boot | 减脂、增肌、控糖、低盐等            |
| 饮食偏好     | Spring Boot | 口味、餐次偏好、常用食材            |
| 过敏源和禁忌 | Spring Boot | 过敏食材、慢性病饮食限制            |
| 用户补充描述 | 前端        | 例如“想做晚餐”“希望简单一点”    |

### 9.3 LLM 输出 JSON

LLM 必须返回合法 JSON，禁止返回 Markdown。建议输出结构如下：

```json
{
  "recipeName": "番茄鸡胸肉意面",
  "recognizedFoods": [
    {
      "label": "spaghetti_bolognese",
      "displayName": "意面",
      "confidence": 0.87
    }
  ],
  "description": "适合高蛋白、控制热量的一餐。",
  "suitability": {
    "score": 86,
    "reason": "蛋白质较高，脂肪适中，适合减脂期午餐。"
  },
  "ingredients": [
    {
      "name": "鸡胸肉",
      "amount": 120,
      "unit": "g"
    }
  ],
  "nutritionEstimate": {
    "calories": 520,
    "protein": 38,
    "fat": 12,
    "carbohydrate": 62
  },
  "cookingSteps": [
    "鸡胸肉切片，用少量盐和黑胡椒腌制。",
    "番茄切块，下锅炒出汁水。",
    "加入鸡胸肉翻炒至变色。",
    "加入煮好的意面拌匀，调整口味后出锅。"
  ],
  "healthTips": [
    "如果正在控糖，可减少意面用量。"
  ],
  "warnings": [
    "营养数据为估算值，仅供饮食参考。"
  ]
}
```

### 9.4 Prompt 模板

```text
你是智能健康膳食管理系统的 AI 菜谱解析助手。
请根据用户健康档案、食物图片识别结果和用户输入，生成一个中文菜谱 JSON。
必须只输出合法 JSON，不要输出 Markdown，不要输出解释性前后缀。

用户健康档案：
{healthProfile}

饮食偏好：
{preferences}

过敏源和禁忌：
{restrictions}

图片识别 Top-K：
{recognitionResults}

用户补充描述：
{userInput}

请输出字段：
recipeName, recognizedFoods, description, suitability, ingredients,
nutritionEstimate, cookingSteps, healthTips, warnings。

要求：
1. cookingSteps 必须是字符串数组。
2. nutritionEstimate 使用估算值。
3. 不要给出医疗诊断。
4. 如果涉及慢性病，只给饮食参考建议。
5. 过敏源和禁忌食材不得出现在 ingredients 中。
```

### 9.5 JSON 校验

LLM 输出后，`ai-service` 应先进行 JSON 解析和字段校验：

- 校验是否为合法 JSON。
- 校验必填字段是否存在。
- 校验 `ingredients`、`cookingSteps`、`healthTips`、`warnings` 是否为数组。
- 校验 `nutritionEstimate` 中热量、蛋白质、脂肪、碳水是否为数字。
- 如果 JSON 不合法，可进行一次修复请求；仍失败则返回错误给 Spring Boot。

## 10. 接口设计

### 10.1 通用要求

- 所有内部接口建议携带 `X-Internal-Token`。
- 响应统一包含 `success`、`data`、`errorMessage`、`elapsedMs`。
- 图片上传接口使用 `multipart/form-data`。
- JSON 接口使用 `application/json`。

### 10.2 健康检查

| 接口         | 方法与路径                                   | 说明                       |
| ------------ | -------------------------------------------- | -------------------------- |
| 健康检查     | `GET /ai/v1/health`                        | 检查服务是否可用           |
| 模型状态     | `GET /ai/v1/models/status`                 | 查看模型是否加载成功       |
| 分类模型信息 | `GET /ai/v1/models/food-classifier/info`   | 查看模型名称、版本、类别数 |
| 分类标签列表 | `GET /ai/v1/models/food-classifier/labels` | 查看 Food-101 标签         |

健康检查响应：

```json
{
  "success": true,
  "data": {
    "status": "UP",
    "modelLoaded": true,
    "llmConfigured": true
  },
  "elapsedMs": 3
}
```

### 10.3 食物图片分类接口

```text
POST /ai/v1/food/classify
Content-Type: multipart/form-data
```

请求参数：

| 参数      | 类型 | 必填 | 说明              |
| --------- | ---- | ---- | ----------------- |
| `image` | file | 是   | 食物图片          |
| `topK`  | int  | 否   | 默认 5，范围 1-10 |

响应示例：

```json
{
  "success": true,
  "data": {
    "modelName": "resnet18-food101",
    "modelVersion": "1.0.0",
    "results": [
      {
        "label": "greek_salad",
        "displayName": "希腊沙拉",
        "confidence": 0.89
      }
    ]
  },
  "elapsedMs": 230
}
```

### 10.4 AI 解析菜谱接口

```text
POST /ai/v1/recipes/parse
Content-Type: application/json
```

请求示例：

```json
{
  "userId": 10001,
  "userInput": "想做一份适合减脂晚餐的菜，步骤简单一点",
  "recognitionResults": [
    {
      "label": "grilled_chicken",
      "displayName": "烤鸡肉",
      "confidence": 0.82
    }
  ],
  "healthProfile": {
    "gender": "FEMALE",
    "heightCm": 165,
    "weightKg": 62,
    "bmi": 22.8,
    "dailyCalorieTarget": 1500
  },
  "preferences": {
    "taste": ["清淡", "少油"],
    "goal": "减脂"
  },
  "restrictions": {
    "allergens": ["花生"],
    "dietRestrictions": ["低糖"]
  }
}
```

响应示例：

```json
{
  "success": true,
  "data": {
    "recipeName": "低脂鸡肉蔬菜碗",
    "recognizedFoods": [
      {
        "label": "grilled_chicken",
        "displayName": "烤鸡肉",
        "confidence": 0.82
      }
    ],
    "description": "适合减脂期晚餐的高蛋白轻食。",
    "suitability": {
      "score": 88,
      "reason": "蛋白质充足，油脂较低，整体热量适中。"
    },
    "ingredients": [
      {
        "name": "鸡胸肉",
        "amount": 120,
        "unit": "g"
      },
      {
        "name": "西兰花",
        "amount": 150,
        "unit": "g"
      }
    ],
    "nutritionEstimate": {
      "calories": 360,
      "protein": 36,
      "fat": 8,
      "carbohydrate": 32
    },
    "cookingSteps": [
      "鸡胸肉切片，加入少量盐和黑胡椒腌制。",
      "西兰花焯水 1 分钟后捞出。",
      "平底锅少油煎熟鸡胸肉。",
      "将鸡胸肉和蔬菜装盘，可搭配少量糙米。"
    ],
    "healthTips": [
      "晚餐可减少主食量，但不建议完全不吃碳水。"
    ],
    "warnings": [
      "营养数据为估算值，仅供饮食参考。"
    ]
  },
  "elapsedMs": 1420
}
```

### 10.5 图片识别并解析菜谱接口

```text
POST /ai/v1/recipes/parse-from-image
Content-Type: multipart/form-data
```

该接口适合简化 Spring Boot 调用流程：一次请求同时完成图片分类和 LLM 菜谱解析。Spring Boot 传入图片、用户上下文和用户描述，`ai-service` 返回识别结果和菜谱 JSON。

请求参数：

| 参数        | 类型   | 必填 | 说明                                            |
| ----------- | ------ | ---- | ----------------------------------------------- |
| `image`   | file   | 是   | 食物图片                                        |
| `context` | string | 是   | JSON 字符串，包含健康档案、偏好、禁忌、用户输入 |
| `topK`    | int    | 否   | 默认 5                                          |

## 11. 与 Spring Boot 的职责边界

| 能力             | Spring Boot | ai-service       |
| ---------------- | ----------- | ---------------- |
| 用户认证鉴权     | 是          | 否               |
| 图片文件保存     | 是          | 否，或仅临时读取 |
| 用户健康档案查询 | 是          | 否               |
| 食物图片分类     | 调用服务    | 是               |
| Prompt 构造      | 可参与      | 是               |
| LLM 调用         | 调用服务    | 是               |
| JSON 结果校验    | 是          | 是               |
| AI 菜谱落库      | 是          | 否               |
| 前端统一响应     | 是          | 否               |

## 12. 配置项

| 配置                  | 说明                | 示例                             |
| --------------------- | ------------------- | -------------------------------- |
| `AI_SERVICE_HOST`   | 服务监听地址        | `0.0.0.0`                      |
| `AI_SERVICE_PORT`   | 服务端口            | `8000`                         |
| `INTERNAL_TOKEN`    | 内部调用令牌        | `change-me`                    |
| `FOOD_MODEL_PATH`   | Food-101 权重路径   | `weights/resnet18_food101.pth` |
| `DEVICE`            | 推理设备            | `cpu` 或 `cuda`              |
| `DEEPSEEK_API_KEY`  | DeepSeek API Key    | 从环境变量读取                   |
| `DEEPSEEK_BASE_URL` | OpenAI 兼容接口地址 | `https://api.deepseek.com`     |
| `DEEPSEEK_MODEL`    | 默认模型            | `deepseek-chat`                |

## 13. 异常处理

| 场景            | 处理方式                               |
| --------------- | -------------------------------------- |
| 模型权重不存在  | 启动时标记模型未加载，分类接口返回错误 |
| 图片格式错误    | 返回参数错误                           |
| 图片过大        | 返回文件大小错误                       |
| 模型推理失败    | 返回 AI 推理异常                       |
| LLM 超时        | 返回 LLM 调用超时                      |
| LLM JSON 不合法 | 尝试一次修复，失败后返回结构化错误     |
| 内部 Token 错误 | 返回 401 或 403                        |

## 14. 日志设计

每次调用建议记录以下信息：

| 字段              | 说明                                  |
| ----------------- | ------------------------------------- |
| `request_id`    | 请求 ID                               |
| `scene`         | `FOOD_CLASSIFY` 或 `RECIPE_PARSE` |
| `model_name`    | 本地模型或 LLM 名称                   |
| `elapsed_ms`    | 耗时                                  |
| `success`       | 是否成功                              |
| `error_message` | 错误原因                              |
| `top_labels`    | 图片分类 Top-K 标签摘要               |

日志中不得记录 DeepSeek API Key、JWT、密码等敏感信息。用户健康档案建议只记录摘要，不记录完整 Prompt。

## 15. 部署与启动

### 15.1 开发环境启动

```text
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
python scripts/train_food101.py
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### 15.2 启动顺序

```text
首次训练阶段：
安装依赖
  -> 运行 train_food101.py
  -> 自动下载 ResNet18 权重和 Food-101 数据集
  -> 保存 weights/resnet18_food101.pth

系统运行阶段：
启动 MySQL
  -> 启动 ai-service，加载本地权重
  -> 启动 Spring Boot
  -> 启动 Vue3 前端
```

## 16. 答辩说明建议

答辩时可以按以下顺序介绍本模块：

1. 本项目没有只做简单大模型聊天，而是设计了独立 `ai-service`。
2. 图像识别部分使用 ResNet18 + Food-101，通过迁移学习得到本地食物分类模型。
3. 模型训练阶段自动下载 ImageNet 预训练权重和 Food-101 数据集，训练完成后保存本地权重。
4. 系统运行阶段由 FastAPI 加载权重，对用户上传的食物图片进行 Top-K 分类。
5. 分类结果会和用户健康档案、饮食目标、过敏源、禁忌一起传入 LLM。
6. LLM 按固定 JSON Schema 输出菜谱，包括食材、营养估算、适配理由、做菜步骤数组和健康提示。
7. Spring Boot 对结果进行保存和展示，前端将菜谱结构化展示给用户。

推荐表述：

```text
本系统的 AI 解析菜谱功能采用“本地图像识别 + 大语言模型结构化生成”的组合方案。
首先使用 ResNet18 对 Food-101 食物图片进行迁移学习训练，得到本地食物分类模型；
然后将图片识别结果与用户健康档案、饮食偏好和禁忌信息共同构造成 Prompt；
最后调用大语言模型生成结构化菜谱 JSON，便于后端保存和前端展示。
```
