"""
ai-service 全 API 测试脚本
覆盖所有 7 个端点 + 认证失败场景
用法:  python test_api.py
      或 cd ai-service && source venv/Scripts/activate && python test_api.py
"""

import json
import os
import sys
from pathlib import Path

import requests

# ============================================================
# 配置
# ============================================================
BASE_URL = os.getenv("AI_SERVICE_URL", "http://127.0.0.1:8000")
TOKEN = os.getenv("INTERNAL_TOKEN", "change-me")
HEADERS = {"X-Internal-Token": TOKEN}

# 从 data 目录随机选一张 apple_pie 图片
DATA_DIR = Path(__file__).resolve().parent / "data" / "food-101" / "images" / "apple_pie"
IMAGES = sorted(DATA_DIR.glob("*.jpg"))
TEST_IMAGE = str(IMAGES[0]) if IMAGES else None

PASS = 0
FAIL = 0


def test(name: str, method: str, path: str, **kwargs) -> dict | None:
    """发送请求并打印结果"""
    global PASS, FAIL
    url = f"{BASE_URL}{path}"
    print(f"\n{'='*60}")
    print(f"  {name}")
    print(f"  {method} {url}")
    print(f"{'='*60}")

    try:
        if method == "GET":
            resp = requests.get(url, headers=HEADERS, timeout=30)
        elif method == "POST":
            resp = requests.post(url, headers=HEADERS, timeout=60, **kwargs)
        else:
            raise ValueError(f"Unknown method: {method}")

        print(f"  Status: {resp.status_code}")

        if resp.status_code == 200:
            data = resp.json()
            pretty = json.dumps(data, indent=2, ensure_ascii=False)
            if len(pretty) > 1500:
                pretty = pretty[:1500] + "\n  ... (truncated)"
            print(f"  Body:\n{pretty}")
            PASS += 1
            return data
        else:
            print(f"  Body: {resp.text}")
            FAIL += 1
            return None

    except requests.ConnectionError:
        print(f"  ERROR: 无法连接到 {BASE_URL}，请确认服务已启动")
        FAIL += 1
        return None
    except Exception as e:
        print(f"  ERROR: {e}")
        FAIL += 1
        return None


def main():
    global FAIL

    if TEST_IMAGE is None:
        print("ERROR: data/food-101/images/apple_pie/ 下没有图片，请先下载数据集")
        sys.exit(1)

    print(f"服务地址: {BASE_URL}")
    print(f"测试图片: {TEST_IMAGE}")
    print(f"图片大小: {Path(TEST_IMAGE).stat().st_size / 1024:.1f} KB")

    # ── GET 端点 ──────────────────────────────────────────
    test("1. 健康检查",                  "GET", "/ai/v1/health")
    test("2. 模型状态",                  "GET", "/ai/v1/models/status")
    test("3. 分类器元信息",              "GET", "/ai/v1/models/food-classifier/info")

    labels_result = test("4. 分类器标签列表", "GET", "/ai/v1/models/food-classifier/labels")
    if labels_result and labels_result.get("data"):
        count = len(labels_result["data"])
        print(f"  → 共 {count} 个标签")

    # ── POST /food/classify ──────────────────────────────
    with open(TEST_IMAGE, "rb") as f:
        test("5. 食物图片分类",
             "POST", "/ai/v1/food/classify",
             files={"image": ("apple_pie.jpg", f, "image/jpeg")},
             data={"topK": 5})

    # ── POST /recipes/parse (纯文本) ─────────────────────
    recipe_context = {
        "userId": 1,
        "userInput": "I want to make a classic apple pie",
        "recognitionResults": [
            {"label": "apple_pie", "displayName": "Apple Pie", "confidence": 0.985}
        ],
        "healthProfile": {
            "gender": "male",
            "heightCm": 175,
            "weightKg": 70,
            "dailyCalorieTarget": 2200
        },
        "preferences": {
            "taste": ["sweet"],
            "goal": "maintain"
        },
        "restrictions": {
            "allergens": ["nuts"],
            "dietRestrictions": []
        }
    }
    test("6. 食谱解析(纯文本)",
         "POST", "/ai/v1/recipes/parse",
         json=recipe_context)

    # ── POST /recipes/parse-from-image ──────────────────
    with open(TEST_IMAGE, "rb") as f:
        test("7. 食谱解析(图片)",
             "POST", "/ai/v1/recipes/parse-from-image",
             files={"image": ("apple_pie.jpg", f, "image/jpeg")},
             data={
                 "topK": 3,
                 "context": json.dumps({
                     "userId": 1,
                     "userInput": "How do I cook this?",
                     "healthProfile": {"dailyCalorieTarget": 2000},
                     "preferences": {"taste": ["sweet"], "goal": "maintain"},
                     "restrictions": {"allergens": [], "dietRestrictions": []}
                 })
             })

    # ── 认证失败测试 ─────────────────────────────────────
    print(f"\n{'='*60}")
    print(f"  8. 认证失败测试 (无 Token)")
    print(f"{'='*60}")
    try:
        resp = requests.get(f"{BASE_URL}/ai/v1/health", timeout=10)
        print(f"  Status: {resp.status_code}")
        if resp.status_code == 401:
            print(f"  Body: {resp.text}")
            print("  [OK] 正确拒绝无 Token 请求")
            global PASS
            PASS += 1
        else:
            print(f"  ✗ 预期 401，实际 {resp.status_code}")
            FAIL += 1
    except Exception as e:
        print(f"  ERROR: {e}")
        FAIL += 1

    # ── 汇总 ─────────────────────────────────────────────
    total = PASS + FAIL
    print(f"\n{'='*60}")
    print(f"  测试完成: {PASS}/{total} 通过")
    if FAIL:
        print(f"  {FAIL} 个失败")
    print(f"{'='*60}")

    return 0 if FAIL == 0 else 1


if __name__ == "__main__":
    sys.exit(main())
