# 智能健康膳食管理系统 Web 后端系统设计文档

## 1. 文档说明

本文档面向课程大作业开发与答辩，描述“智能健康膳食管理系统”的后端系统设计。系统面向普通用户、管理员两类角色，提供健康档案管理、食材菜谱管理、膳食计划、膳食记录、营养分析、体重趋势、报表统计、食物图片识别、AI 膳食顾问、AI 菜谱生成、社区互动、文件资源、后台管理等能力。

原始接口方案偏向接口目录，本文档在此基础上补充模块职责、核心数据、接口请求与响应结构、权限边界、关键业务流程和实现建议，便于后续按 Spring Boot + MySQL + FastAPI 落地开发。

## 2. 项目背景与目标

### 2.1 项目背景

随着健康饮食意识增强，用户希望系统能够根据个人身体情况、健康目标、口味偏好和每日实际摄入，提供更个性化的饮食管理服务。本项目结合传统 Web 后端、营养规则计算、食物图片识别模型和大语言模型能力，完成一个可演示、可扩展的智能健康膳食管理平台。

### 2.2 建设目标

- 支持用户注册登录、健康档案、饮食偏好、体重记录等基础健康数据管理。
- 支持食材库、菜谱库、膳食计划、膳食记录与营养分析。
- 支持上传食物图片，通过本地 ResNet18 食物分类模型识别食物类别。
- 支持调用 DeepSeek OpenAI 兼容接口，实现智能问答、饮食诊断、菜谱生成。
- 支持社区分享、点赞、评论、收藏、关注、举报与后台审核。
- 支持管理员进行用户、食材、菜谱、社区、AI 配置和系统配置管理。
- 提供结构清晰、权限明确、响应统一的 RESTful API，方便 Vue3 前端调用。

### 2.3 非目标说明

- 不实现医疗诊断结论，只提供健康饮食参考建议。
- 不直接存储银行卡、身份证等敏感金融或实名信息。
- 课程项目阶段可使用本地文件存储；如后续上线，可替换为对象存储。

## 3. 技术架构设计

### 3.1 技术选型

| 层次 | 技术 | 说明 |
| --- | --- | --- |
| 前端 | Vue3 | 负责页面展示、表单提交、图表展示、AI 会话交互 |
| 业务后端 | Spring Boot 3.5.x + Java 17 | 提供业务接口、认证鉴权、数据访问、服务编排 |
| 数据库 | MySQL 8.4 LTS | 存储用户、健康档案、菜谱、记录、社区、日志等数据 |
| AI 服务 | Python FastAPI | 提供图片分类、本地模型加载、DeepSeek 调用封装 |
| 图片识别模型 | ResNet18 + Food-101 | 识别食物图片类别，返回 Top-K 预测结果 |
| 大模型能力 | DeepSeek OpenAI 兼容接口 | 生成膳食建议、问答、菜谱、报表解释 |
| 认证方式 | JWT | 前后端分离认证，支持访问令牌与刷新令牌 |
| 接口风格 | RESTful API | 统一路径、状态码、响应结构 |

### 3.2 系统总体架构

```text
Vue3 前端
  |
  | HTTP / JSON / Multipart
  v
Spring Boot 业务后端
  |-- Auth / User / Health / Meal / Community / Admin
  |-- Nutrition Rule Engine
  |-- File Storage Adapter
  |
  | JDBC / MyBatis 或 JPA
  v
MySQL 8.4

Spring Boot 业务后端
  |
  | HTTP 内部调用
  v
Python FastAPI AI 服务
  |-- ResNet18 Food Classifier
  |-- DeepSeek Client
```

### 3.3 后端分层结构

```text
controller
  接收请求、参数校验、返回统一响应
service
  业务编排、权限判断、事务控制、规则计算
repository / mapper
  数据库读写
domain / entity
  数据库实体与领域对象
dto
  请求对象、响应对象、分页对象
client
  调用 Python AI 服务、DeepSeek 服务、文件服务
config
  安全、跨域、Swagger、事务、文件上传等配置
common
  统一响应、异常处理、枚举、工具类
```

### 3.4 推荐包结构

```text
com.example.diet
  ├── auth
  ├── user
  ├── health
  ├── ingredient
  ├── recipe
  ├── mealplan
  ├── mealrecord
  ├── nutrition
  ├── weight
  ├── report
  ├── recognition
  ├── ai
  ├── community
  ├── shopping
  ├── file
  ├── dictionary
  ├── notification
  ├── admin
  ├── internal
  └── common
```

## 4. 角色与权限设计

### 4.1 用户角色

| 角色 | 标识 | 权限范围 |
| --- | --- | --- |
| 游客 | ANONYMOUS | 注册、登录、查看公开菜谱、公开帖子、公开用户主页 |
| 普通用户 | USER | 管理个人健康数据、膳食计划、膳食记录、AI 问答、社区互动 |
| 管理员 | ADMIN | 用户管理、食材菜谱审核、社区审核、举报处理、AI 配置、系统配置 |
| 内部服务 | INTERNAL | Spring Boot 与 Python AI 服务之间的内部接口调用 |

### 4.2 权限原则

- 用户只能访问和修改自己的健康档案、膳食记录、体重记录、报表和 AI 会话。
- 用户可以查看公开菜谱、公开帖子和公开用户主页。
- 用户只能修改或删除自己发布的帖子、评论、采购清单和上传文件。
- 管理员可以查看和处理全站内容，但敏感字段需要脱敏展示。
- 内部接口不对前端开放，需使用内部服务令牌或内网访问控制。

### 4.3 JWT 设计

访问令牌建议有效期 2 小时，刷新令牌建议有效期 7 天。JWT Payload 示例：

```json
{
  "sub": "10001",
  "username": "alice",
  "roles": ["USER"],
  "tokenType": "ACCESS",
  "iat": 1710000000,
  "exp": 1710007200
}
```

## 5. 通用接口规范

### 5.1 接口前缀

| 类型 | 前缀 | 说明 |
| --- | --- | --- |
| 业务接口 | `/api/v1` | Vue3 前端直接调用 |
| 管理端接口 | `/api/v1/admin` | 管理后台调用 |
| 内部接口 | `/api/internal/v1` | Spring Boot 内部或 Python AI 服务调用 |
| Python AI 服务 | `/ai/v1` | 仅 Spring Boot 调用 |

### 5.2 请求头规范

| 请求头 | 是否必需 | 说明 |
| --- | --- | --- |
| `Authorization: Bearer <token>` | 登录后必需 | JWT 访问令牌 |
| `Content-Type: application/json` | JSON 请求必需 | 普通业务请求 |
| `Content-Type: multipart/form-data` | 文件上传必需 | 上传图片、头像、导入文件 |
| `X-Request-Id` | 可选 | 前端生成的请求追踪 ID |
| `X-Internal-Token` | 内部接口必需 | 服务间调用令牌 |

### 5.3 统一响应结构

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "requestId": "req-202606240001",
  "timestamp": "2026-06-24T12:00:00+08:00"
}
```

失败响应：

```json
{
  "code": 40001,
  "message": "参数校验失败",
  "data": {
    "field": "email",
    "reason": "邮箱格式不正确"
  },
  "requestId": "req-202606240002",
  "timestamp": "2026-06-24T12:00:00+08:00"
}
```

分页响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "records": [],
    "page": 1,
    "size": 10,
    "total": 128,
    "pages": 13
  }
}
```

### 5.4 通用错误码

| 错误码 | 含义 | 使用场景 |
| --- | --- | --- |
| 0 | 成功 | 请求处理成功 |
| 40000 | 请求错误 | 参数缺失、格式错误 |
| 40001 | 参数校验失败 | 字段长度、范围、格式不合法 |
| 40100 | 未登录 | 未携带 Token 或 Token 失效 |
| 40300 | 无权限 | 访问他人资源或管理员接口 |
| 40400 | 资源不存在 | ID 不存在或资源已删除 |
| 40900 | 资源冲突 | 重复注册、重复收藏、重复点赞 |
| 42900 | 请求过于频繁 | AI 调用、上传、登录限流 |
| 50000 | 系统异常 | 未预期服务端错误 |
| 50200 | AI 服务异常 | Python AI 服务不可用 |

### 5.5 通用分页参数

| 参数 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `page` | integer | 1 | 页码，从 1 开始 |
| `size` | integer | 10 | 每页数量，最大建议 100 |
| `sort` | string | `createdAt,desc` | 排序字段与方向 |

## 6. 数据库总体设计

### 6.1 数据表分组

| 分组 | 主要表 |
| --- | --- |
| 账号用户 | `sys_user`, `user_profile`, `user_preference`, `user_follow`, `refresh_token` |
| 健康档案 | `health_profile`, `health_goal`, `user_allergen`, `user_chronic_disease`, `user_diet_restriction` |
| 食材菜谱 | `ingredient`, `recipe`, `recipe_ingredient`, `recipe_step`, `recipe_tag`, `recipe_favorite`, `recipe_rating` |
| 膳食业务 | `meal_plan`, `meal_plan_item`, `meal_record`, `meal_record_item`, `shopping_list`, `shopping_list_item` |
| 营养报表 | `nutrition_daily_stat`, `nutrition_report`, `weight_record` |
| AI 与识别 | `food_recognition_task`, `food_recognition_result`, `ai_conversation`, `ai_message`, `ai_recipe`, `ai_call_log` |
| 社区互动 | `community_post`, `community_post_image`, `community_comment`, `community_like`, `community_favorite`, `community_tag`, `community_post_tag`, `community_report` |
| 系统基础 | `file_resource`, `dictionary_item`, `notification`, `system_config`, `access_log`, `error_log` |

### 6.2 基础审计字段

所有核心业务表建议包含以下字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 主键 |
| `created_at` | datetime | 创建时间 |
| `updated_at` | datetime | 更新时间 |
| `created_by` | bigint | 创建人 ID，可为空 |
| `updated_by` | bigint | 更新人 ID，可为空 |
| `deleted` | tinyint | 逻辑删除标记，0 未删除，1 已删除 |

### 6.3 核心表设计摘要

#### 6.3.1 用户表 `sys_user`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 用户 ID |
| `username` | varchar(50) | 用户名，唯一 |
| `email` | varchar(100) | 邮箱，唯一 |
| `phone` | varchar(20) | 手机号，可选 |
| `password_hash` | varchar(255) | 加密后的密码 |
| `role` | varchar(20) | USER 或 ADMIN |
| `status` | varchar(20) | ENABLED、DISABLED、DELETED |
| `last_login_at` | datetime | 最近登录时间 |
| `password_updated_at` | datetime | 密码更新时间 |

#### 6.3.2 健康档案表 `health_profile`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 档案 ID |
| `user_id` | bigint | 用户 ID，唯一 |
| `gender` | varchar(20) | 性别 |
| `birthday` | date | 生日 |
| `height_cm` | decimal(5,2) | 身高 |
| `weight_kg` | decimal(5,2) | 当前体重 |
| `target_weight_kg` | decimal(5,2) | 目标体重 |
| `activity_level` | varchar(30) | 活动水平 |
| `bmi` | decimal(5,2) | BMI |
| `daily_calorie_target` | int | 每日目标热量 |
| `remark` | varchar(500) | 备注 |

#### 6.3.3 食材表 `ingredient`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 食材 ID |
| `name` | varchar(100) | 食材名称 |
| `category` | varchar(50) | 食材分类 |
| `unit` | varchar(20) | 默认单位 |
| `calorie` | decimal(8,2) | 每 100g 热量 kcal |
| `protein` | decimal(8,2) | 蛋白质 g |
| `fat` | decimal(8,2) | 脂肪 g |
| `carbohydrate` | decimal(8,2) | 碳水 g |
| `sugar` | decimal(8,2) | 糖 g |
| `sodium` | decimal(8,2) | 钠 mg |
| `status` | varchar(20) | ENABLED、DISABLED |

#### 6.3.4 菜谱表 `recipe`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 菜谱 ID |
| `name` | varchar(100) | 菜谱名称 |
| `description` | varchar(1000) | 简介 |
| `category` | varchar(50) | 早餐、午餐、晚餐、加餐等 |
| `cover_file_id` | bigint | 封面文件 ID |
| `difficulty` | varchar(20) | 难度 |
| `cook_minutes` | int | 烹饪时长 |
| `servings` | int | 份数 |
| `total_calorie` | decimal(8,2) | 总热量 |
| `status` | varchar(20) | DRAFT、PENDING、ONLINE、OFFLINE |

#### 6.3.5 膳食记录表 `meal_record`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 记录 ID |
| `user_id` | bigint | 用户 ID |
| `record_date` | date | 记录日期 |
| `meal_type` | varchar(20) | 早餐、午餐、晚餐、加餐 |
| `source_type` | varchar(30) | 手动、菜谱、计划、图片识别 |
| `total_calorie` | decimal(8,2) | 总热量 |
| `total_protein` | decimal(8,2) | 总蛋白质 |
| `total_fat` | decimal(8,2) | 总脂肪 |
| `total_carbohydrate` | decimal(8,2) | 总碳水 |
| `remark` | varchar(500) | 备注 |

#### 6.3.6 食物识别任务表 `food_recognition_task`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 任务 ID |
| `user_id` | bigint | 用户 ID |
| `image_file_id` | bigint | 图片文件 ID |
| `status` | varchar(20) | PENDING、SUCCESS、FAILED、CONFIRMED |
| `model_name` | varchar(100) | 模型名称 |
| `model_version` | varchar(50) | 模型版本 |
| `confirmed_label` | varchar(100) | 用户确认后的食物标签 |
| `error_message` | varchar(500) | 失败原因 |

#### 6.3.7 社区帖子表 `community_post`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | bigint | 帖子 ID |
| `user_id` | bigint | 发布人 |
| `title` | varchar(100) | 标题 |
| `content` | text | 正文 |
| `cover_file_id` | bigint | 封面图 |
| `source_type` | varchar(30) | 手动、菜谱、膳食记录、AI 菜谱、识别结果 |
| `source_id` | bigint | 来源业务 ID |
| `status` | varchar(20) | PENDING、ONLINE、OFFLINE、REJECTED |
| `like_count` | int | 点赞数 |
| `comment_count` | int | 评论数 |
| `favorite_count` | int | 收藏数 |
| `view_count` | int | 浏览数 |

## 7. 核心业务流程设计

### 7.1 注册登录流程

```text
前端提交注册信息
  -> 后端校验用户名 / 邮箱是否重复
  -> BCrypt 加密密码
  -> 创建 sys_user 与 user_profile
  -> 用户登录
  -> 生成 accessToken 与 refreshToken
  -> 前端保存 Token 并调用业务接口
```

### 7.2 健康档案到膳食计划流程

```text
用户填写健康档案
  -> 设置目标、过敏源、慢性病、饮食禁忌
  -> 系统计算 BMI 和每日热量目标
  -> 生成膳食计划
  -> 根据健康目标过滤不适合菜谱
  -> 按早餐 / 午餐 / 晚餐 / 加餐组合菜谱
  -> 计算计划营养
  -> 返回推荐原因和营养达标情况
```

### 7.3 图片识别到膳食记录流程

```text
用户上传食物图片
  -> Spring Boot 保存图片文件
  -> 创建 food_recognition_task
  -> 调用 Python /ai/v1/food/classify
  -> Python 返回 Top-K 标签与置信度
  -> Spring Boot 匹配食材库 / 菜谱库
  -> 估算营养
  -> 用户确认或修正结果
  -> 转换为 meal_record
```

### 7.4 AI 顾问流程

```text
用户输入问题
  -> Spring Boot 查询健康档案、偏好、近期饮食记录
  -> 构造结构化 Prompt 上下文
  -> 调用 Python AI 服务
  -> Python 调用 DeepSeek
  -> 返回建议内容
  -> Spring Boot 保存会话、消息、调用日志
```

### 7.5 社区分享流程

```text
用户从菜谱 / 膳食记录 / AI 菜谱 / 图片识别结果发帖
  -> 生成社区帖子草稿
  -> 上传或关联图片
  -> 绑定标签
  -> 发布后进入 ONLINE 或 PENDING 状态
  -> 其他用户点赞、评论、收藏
  -> 产生通知
  -> 管理员处理举报和违规内容
```

## 8. 模块设计与详细接口设计

本章按模块描述职责、核心数据和主要接口。所有接口默认返回统一响应结构；除特别说明外，登录用户接口均需携带 JWT。

### 8.1 认证与账号模块

#### 模块职责

负责用户注册、登录、Token 刷新、退出登录、密码修改、密码重置、账号注销和当前登录用户信息查询。

#### 核心表

`sys_user`, `refresh_token`, `user_profile`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 用户注册 | `POST /api/v1/auth/register` | 游客 | `username`, `email`, `password`, `confirmPassword` | `userId`, `username`, `email` |
| 用户登录 | `POST /api/v1/auth/login` | 游客 | `account`, `password` | `accessToken`, `refreshToken`, `expiresIn`, `user` |
| 刷新 Token | `POST /api/v1/auth/refresh-token` | 游客 | `refreshToken` | 新 `accessToken`, 新 `refreshToken` |
| 退出登录 | `POST /api/v1/auth/logout` | 用户 | 无或 `refreshToken` | 空 |
| 获取登录用户信息 | `GET /api/v1/auth/me` | 用户 | 无 | 当前用户基础信息与角色 |
| 修改密码 | `PUT /api/v1/auth/password` | 用户 | `oldPassword`, `newPassword`, `confirmPassword` | 空 |
| 重置密码申请 | `POST /api/v1/auth/password/reset-request` | 游客 | `email` | `resetTokenExpireAt` |
| 重置密码确认 | `POST /api/v1/auth/password/reset-confirm` | 游客 | `resetToken`, `newPassword` | 空 |
| 注销账号 | `DELETE /api/v1/auth/account` | 用户 | `password` | 空 |
| 查询账号安全信息 | `GET /api/v1/auth/security` | 用户 | 无 | 登录时间、密码更新时间、账号状态 |

登录请求示例：

```json
{
  "account": "alice@example.com",
  "password": "Password123"
}
```

登录响应 `data` 示例：

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token",
  "expiresIn": 7200,
  "user": {
    "id": 10001,
    "username": "alice",
    "nickname": "Alice",
    "avatarUrl": "https://example.com/avatar.png",
    "roles": ["USER"]
  }
}
```

### 8.2 用户资料与偏好模块

#### 模块职责

管理用户基础资料、头像、公开主页、饮食偏好、关注关系和个人数据概览。

#### 核心表

`user_profile`, `user_preference`, `user_follow`, `file_resource`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 查询个人资料 | `GET /api/v1/users/me/profile` | 用户 | 无 | 昵称、头像、简介、性别、生日 |
| 修改个人资料 | `PUT /api/v1/users/me/profile` | 用户 | `nickname`, `bio`, `gender`, `birthday` | 修改后的资料 |
| 上传用户头像 | `POST /api/v1/users/me/avatar` | 用户 | `file` | `fileId`, `url` |
| 查询用户主页 | `GET /api/v1/users/{userId}/homepage` | 游客 | `userId` | 公开资料、统计、最近帖子 |
| 查询我的偏好 | `GET /api/v1/users/me/preferences` | 用户 | 无 | 口味、饮食习惯、推荐偏好 |
| 修改我的偏好 | `PUT /api/v1/users/me/preferences` | 用户 | 偏好对象 | 修改后的偏好 |
| 查询我的数据概览 | `GET /api/v1/users/me/overview` | 用户 | 无 | 档案完整度、今日摄入、体重、帖子数 |
| 查询用户公开信息 | `GET /api/v1/users/{userId}/public-profile` | 游客 | `userId` | 公开资料 |
| 关注用户 | `POST /api/v1/users/{userId}/follow` | 用户 | `userId` | 空 |
| 取消关注用户 | `DELETE /api/v1/users/{userId}/follow` | 用户 | `userId` | 空 |
| 查询我的关注列表 | `GET /api/v1/users/me/following` | 用户 | 分页参数 | 分页用户列表 |
| 查询我的粉丝列表 | `GET /api/v1/users/me/followers` | 用户 | 分页参数 | 分页用户列表 |

偏好请求示例：

```json
{
  "tastePreferences": ["LIGHT", "LOW_SPICY"],
  "dietHabits": ["REGULAR_BREAKFAST", "LESS_SUGAR"],
  "favoriteIngredients": ["鸡胸肉", "西兰花"],
  "dislikedIngredients": ["香菜"],
  "recommendationPriority": "BALANCED"
}
```

### 8.3 健康档案模块

#### 模块职责

维护用户身高、体重、年龄、性别、活动水平、健康目标、过敏源、慢性病、饮食禁忌，并为膳食推荐和 AI 服务提供结构化上下文。

#### 核心表

`health_profile`, `health_goal`, `user_allergen`, `user_chronic_disease`, `user_diet_restriction`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 创建健康档案 | `POST /api/v1/health-profiles` | 用户 | 档案基础字段 | 健康档案详情 |
| 查询我的健康档案 | `GET /api/v1/health-profiles/me` | 用户 | 无 | 完整健康档案 |
| 修改健康档案 | `PUT /api/v1/health-profiles/me` | 用户 | 档案基础字段 | 修改后的健康档案 |
| 重新计算 BMI | `POST /api/v1/health-profiles/me/bmi/recalculate` | 用户 | 无 | `bmi`, `bmiLevel` |
| 查询 BMI 信息 | `GET /api/v1/health-profiles/me/bmi` | 用户 | 无 | BMI 和身体状态 |
| 设置健康目标 | `PUT /api/v1/health-profiles/me/goals` | 用户 | `goals` 数组 | 目标列表 |
| 查询健康目标 | `GET /api/v1/health-profiles/me/goals` | 用户 | 无 | 目标列表 |
| 设置过敏源 | `PUT /api/v1/health-profiles/me/allergens` | 用户 | `allergenCodes` | 过敏源列表 |
| 查询过敏源 | `GET /api/v1/health-profiles/me/allergens` | 用户 | 无 | 过敏源列表 |
| 设置慢性病 | `PUT /api/v1/health-profiles/me/chronic-diseases` | 用户 | `diseaseCodes` | 慢性病列表 |
| 查询慢性病 | `GET /api/v1/health-profiles/me/chronic-diseases` | 用户 | 无 | 慢性病列表 |
| 设置饮食禁忌 | `PUT /api/v1/health-profiles/me/diet-restrictions` | 用户 | `restrictionCodes` | 禁忌列表 |
| 查询饮食禁忌 | `GET /api/v1/health-profiles/me/diet-restrictions` | 用户 | 无 | 禁忌列表 |
| 查询健康档案摘要 | `GET /api/v1/health-profiles/me/summary` | 用户 | 无 | AI 和推荐用摘要 |
| 生成健康风险提示 | `GET /api/v1/health-profiles/me/risk-warnings` | 用户 | 无 | 风险提示列表 |

创建健康档案请求示例：

```json
{
  "gender": "FEMALE",
  "birthday": "2003-05-10",
  "heightCm": 165.0,
  "weightKg": 58.5,
  "targetWeightKg": 55.0,
  "activityLevel": "LIGHT",
  "dailyCalorieTarget": 1600
}
```

### 8.4 食材库模块

#### 模块职责

维护食材基础信息和营养成分，为菜谱计算、膳食记录、营养分析、图片识别匹配和推荐过滤提供基础数据。

#### 核心表

`ingredient`, `dictionary_item`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 新增食材 | `POST /api/v1/ingredients` | 管理员或授权用户 | 食材信息与营养值 | 食材详情 |
| 修改食材 | `PUT /api/v1/ingredients/{ingredientId}` | 管理员 | 食材信息 | 食材详情 |
| 删除食材 | `DELETE /api/v1/ingredients/{ingredientId}` | 管理员 | `ingredientId` | 空 |
| 查询食材详情 | `GET /api/v1/ingredients/{ingredientId}` | 游客 | `ingredientId` | 食材详情 |
| 分页查询食材 | `GET /api/v1/ingredients` | 游客 | `keyword`, `category`, `minCalorie`, `maxCalorie`, 分页参数 | 分页食材 |
| 搜索食材 | `GET /api/v1/ingredients/search` | 游客 | `keyword`, `limit` | 简要食材列表 |
| 查询食材营养成分 | `GET /api/v1/ingredients/{ingredientId}/nutrition` | 游客 | `ingredientId` | 营养成分 |
| 查询高蛋白食材 | `GET /api/v1/ingredients/high-protein` | 游客 | 分页参数 | 食材列表 |
| 查询低热量食材 | `GET /api/v1/ingredients/low-calorie` | 游客 | 分页参数 | 食材列表 |
| 查询低脂食材 | `GET /api/v1/ingredients/low-fat` | 游客 | 分页参数 | 食材列表 |
| 查询低糖食材 | `GET /api/v1/ingredients/low-sugar` | 游客 | 分页参数 | 食材列表 |
| 查询适合用户的食材 | `GET /api/v1/ingredients/suitable-for-me` | 用户 | 健康档案自动参与 | 食材列表 |
| 查询不适合用户的食材 | `GET /api/v1/ingredients/unsuitable-for-me` | 用户 | 健康档案自动参与 | 食材列表与原因 |
| 批量导入食材 | `POST /api/v1/ingredients/import` | 管理员 | Excel 或 JSON 文件 | 导入成功数、失败数 |
| 导出食材库 | `GET /api/v1/ingredients/export` | 管理员 | 查询条件 | 文件下载 |
| 启用或停用食材 | `PATCH /api/v1/ingredients/{ingredientId}/status` | 管理员 | `status` | 空 |

新增食材请求示例：

```json
{
  "name": "鸡胸肉",
  "category": "MEAT",
  "unit": "g",
  "calorie": 133,
  "protein": 24.6,
  "fat": 3.1,
  "carbohydrate": 0,
  "sugar": 0,
  "sodium": 47,
  "description": "高蛋白、低脂肪食材"
}
```

### 8.5 菜谱库模块

#### 模块职责

管理菜谱基础信息、食材组成、制作步骤、营养计算、收藏评分和适配推荐。

#### 核心表

`recipe`, `recipe_ingredient`, `recipe_step`, `recipe_tag`, `recipe_favorite`, `recipe_rating`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 新增菜谱 | `POST /api/v1/recipes` | 用户或管理员 | 菜谱基础信息 | 菜谱详情 |
| 修改菜谱 | `PUT /api/v1/recipes/{recipeId}` | 作者或管理员 | 菜谱基础信息 | 菜谱详情 |
| 删除菜谱 | `DELETE /api/v1/recipes/{recipeId}` | 作者或管理员 | `recipeId` | 空 |
| 查询菜谱详情 | `GET /api/v1/recipes/{recipeId}` | 游客 | `recipeId` | 菜谱、步骤、食材、营养 |
| 分页查询菜谱 | `GET /api/v1/recipes` | 游客 | `keyword`, `category`, `tags`, `maxCalorie`, 分页参数 | 分页菜谱 |
| 搜索菜谱 | `GET /api/v1/recipes/search` | 游客 | `keyword`, `limit` | 简要菜谱列表 |
| 查询早餐菜谱 | `GET /api/v1/recipes/breakfast` | 游客 | 分页参数 | 菜谱列表 |
| 查询午餐菜谱 | `GET /api/v1/recipes/lunch` | 游客 | 分页参数 | 菜谱列表 |
| 查询晚餐菜谱 | `GET /api/v1/recipes/dinner` | 游客 | 分页参数 | 菜谱列表 |
| 查询加餐菜谱 | `GET /api/v1/recipes/snack` | 游客 | 分页参数 | 菜谱列表 |
| 绑定菜谱食材 | `POST /api/v1/recipes/{recipeId}/ingredients` | 作者或管理员 | 食材用量列表 | 食材组成 |
| 修改菜谱食材 | `PUT /api/v1/recipes/{recipeId}/ingredients` | 作者或管理员 | 食材用量列表 | 食材组成 |
| 查询菜谱食材 | `GET /api/v1/recipes/{recipeId}/ingredients` | 游客 | `recipeId` | 食材组成 |
| 计算菜谱营养 | `POST /api/v1/recipes/{recipeId}/nutrition/calculate` | 作者或管理员 | 无 | 营养结果 |
| 查询菜谱营养 | `GET /api/v1/recipes/{recipeId}/nutrition` | 游客 | `recipeId` | 营养结果 |
| 查询适合我的菜谱 | `GET /api/v1/recipes/suitable-for-me` | 用户 | 健康档案自动参与 | 菜谱列表 |
| 查询不适合我的菜谱 | `GET /api/v1/recipes/unsuitable-for-me` | 用户 | 健康档案自动参与 | 菜谱列表与原因 |
| 收藏菜谱 | `POST /api/v1/recipes/{recipeId}/favorite` | 用户 | `recipeId` | 空 |
| 取消收藏菜谱 | `DELETE /api/v1/recipes/{recipeId}/favorite` | 用户 | `recipeId` | 空 |
| 查询我的收藏菜谱 | `GET /api/v1/recipes/favorites/me` | 用户 | 分页参数 | 分页菜谱 |
| 菜谱评分 | `POST /api/v1/recipes/{recipeId}/rating` | 用户 | `score`, `comment` | 评分结果 |

新增菜谱请求示例：

```json
{
  "name": "低脂鸡胸肉沙拉",
  "description": "适合减脂期的高蛋白菜谱",
  "category": "LUNCH",
  "difficulty": "EASY",
  "cookMinutes": 20,
  "servings": 1,
  "tagCodes": ["FAT_LOSS", "HIGH_PROTEIN"]
}
```

菜谱食材请求示例：

```json
{
  "items": [
    {
      "ingredientId": 1,
      "amount": 150,
      "unit": "g"
    },
    {
      "ingredientId": 2,
      "amount": 100,
      "unit": "g"
    }
  ]
}
```

### 8.6 膳食计划模块

#### 模块职责

根据用户健康档案、饮食目标、过敏源、偏好和菜谱库，生成单日或一周膳食计划，并支持用户修改、替换、锁定、解释和生成采购清单。

#### 核心表

`meal_plan`, `meal_plan_item`, `recipe`, `health_profile`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 生成今日膳食计划 | `POST /api/v1/meal-plans/today/generate` | 用户 | `target`, `calorieLimit`, `useAi` | 计划详情 |
| 生成指定日期膳食计划 | `POST /api/v1/meal-plans/generate` | 用户 | `date`, `target`, `useAi` | 计划详情 |
| 生成一周膳食计划 | `POST /api/v1/meal-plans/week/generate` | 用户 | `startDate`, `target`, `useAi` | 周计划 |
| 查询今日膳食计划 | `GET /api/v1/meal-plans/today` | 用户 | 无 | 今日计划 |
| 查询指定日期膳食计划 | `GET /api/v1/meal-plans/date/{date}` | 用户 | `date` | 指定日期计划 |
| 查询周膳食计划 | `GET /api/v1/meal-plans/week` | 用户 | `startDate` | 周计划 |
| 修改膳食计划 | `PUT /api/v1/meal-plans/{planId}` | 用户 | 计划内容 | 计划详情 |
| 删除膳食计划 | `DELETE /api/v1/meal-plans/{planId}` | 用户 | `planId` | 空 |
| 替换某一餐 | `PUT /api/v1/meal-plans/{planId}/meals/{mealType}/replace` | 用户 | `recipeId` | 计划详情 |
| 重新生成某一餐 | `POST /api/v1/meal-plans/{planId}/meals/{mealType}/regenerate` | 用户 | `excludeRecipeIds` | 新餐次 |
| 锁定某一餐 | `PATCH /api/v1/meal-plans/{planId}/meals/{mealType}/lock` | 用户 | 无 | 空 |
| 解锁某一餐 | `PATCH /api/v1/meal-plans/{planId}/meals/{mealType}/unlock` | 用户 | 无 | 空 |
| 查询计划生成原因 | `GET /api/v1/meal-plans/{planId}/reason` | 用户 | `planId` | 推荐原因 |
| 查询计划营养分析 | `GET /api/v1/meal-plans/{planId}/nutrition-analysis` | 用户 | `planId` | 营养分析 |
| 查询计划采购清单 | `GET /api/v1/meal-plans/{planId}/shopping-list` | 用户 | `planId` | 采购清单 |
| 应用计划为膳食记录 | `POST /api/v1/meal-plans/{planId}/apply-to-record` | 用户 | `date` | 生成的记录 ID 列表 |

生成计划请求示例：

```json
{
  "date": "2026-06-24",
  "target": "FAT_LOSS",
  "calorieLimit": 1600,
  "mealTypes": ["BREAKFAST", "LUNCH", "DINNER", "SNACK"],
  "useAi": false
}
```

### 8.7 膳食记录模块

#### 模块职责

记录用户实际摄入，包括手动记录、从菜谱生成、从计划生成、从图片识别生成，并汇总每日和历史摄入趋势。

#### 核心表

`meal_record`, `meal_record_item`, `nutrition_daily_stat`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 新增膳食记录 | `POST /api/v1/meal-records` | 用户 | 记录基础信息和食物明细 | 记录详情 |
| 修改膳食记录 | `PUT /api/v1/meal-records/{recordId}` | 用户 | 记录内容 | 记录详情 |
| 删除膳食记录 | `DELETE /api/v1/meal-records/{recordId}` | 用户 | `recordId` | 空 |
| 查询记录详情 | `GET /api/v1/meal-records/{recordId}` | 用户 | `recordId` | 记录详情 |
| 按日期查询记录 | `GET /api/v1/meal-records/date/{date}` | 用户 | `date` | 当天记录列表 |
| 分页查询我的记录 | `GET /api/v1/meal-records` | 用户 | `startDate`, `endDate`, `mealType`, 分页参数 | 分页记录 |
| 新增早餐记录 | `POST /api/v1/meal-records/breakfast` | 用户 | 食物明细 | 记录详情 |
| 新增午餐记录 | `POST /api/v1/meal-records/lunch` | 用户 | 食物明细 | 记录详情 |
| 新增晚餐记录 | `POST /api/v1/meal-records/dinner` | 用户 | 食物明细 | 记录详情 |
| 新增加餐记录 | `POST /api/v1/meal-records/snack` | 用户 | 食物明细 | 记录详情 |
| 从菜谱生成记录 | `POST /api/v1/meal-records/from-recipe/{recipeId}` | 用户 | `date`, `mealType`, `servingRatio` | 记录详情 |
| 从膳食计划生成记录 | `POST /api/v1/meal-records/from-plan/{planId}` | 用户 | `date` | 记录列表 |
| 从图片识别结果生成记录 | `POST /api/v1/meal-records/from-food-recognition/{taskId}` | 用户 | `mealType`, `amount` | 记录详情 |
| 查询每日摄入汇总 | `GET /api/v1/meal-records/daily-summary` | 用户 | `date` | 每日汇总 |
| 查询历史摄入统计 | `GET /api/v1/meal-records/history-statistics` | 用户 | `startDate`, `endDate` | 趋势统计 |
| 标记异常饮食 | `PATCH /api/v1/meal-records/{recordId}/abnormal` | 用户 | `abnormalType`, `remark` | 空 |
| 分享膳食记录到社区 | `POST /api/v1/meal-records/{recordId}/share-to-community` | 用户 | `title`, `content` | 帖子 ID |

新增记录请求示例：

```json
{
  "recordDate": "2026-06-24",
  "mealType": "LUNCH",
  "items": [
    {
      "foodType": "INGREDIENT",
      "foodId": 1,
      "name": "鸡胸肉",
      "amount": 150,
      "unit": "g"
    }
  ],
  "remark": "午餐"
}
```

### 8.8 营养分析模块

#### 模块职责

根据食材、菜谱、膳食计划和实际记录计算热量、蛋白质、脂肪、碳水等营养摄入，并生成缺口、超标项、评分和基础建议。

#### 核心表

`ingredient`, `recipe_ingredient`, `meal_record_item`, `nutrition_daily_stat`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 分析单个菜谱营养 | `POST /api/v1/nutrition/analyze/recipe/{recipeId}` | 游客 | `recipeId` | 营养分析 |
| 分析一餐营养 | `POST /api/v1/nutrition/analyze/meal` | 用户 | 食物明细 | 营养分析 |
| 分析一日营养 | `POST /api/v1/nutrition/analyze/day` | 用户 | `date` 或食物明细 | 一日分析 |
| 分析膳食计划营养 | `POST /api/v1/nutrition/analyze/meal-plan/{planId}` | 用户 | `planId` | 计划分析 |
| 分析膳食记录营养 | `POST /api/v1/nutrition/analyze/meal-record/{recordId}` | 用户 | `recordId` | 记录分析 |
| 查询每日营养摄入 | `GET /api/v1/nutrition/daily` | 用户 | `date` | 每日营养 |
| 查询周营养摄入 | `GET /api/v1/nutrition/weekly` | 用户 | `startDate` | 周趋势 |
| 查询月营养摄入 | `GET /api/v1/nutrition/monthly` | 用户 | `month` | 月趋势 |
| 膳食指南达标分析 | `POST /api/v1/nutrition/standard/compare` | 用户 | 营养值或日期 | 达标结果 |
| 查询营养缺口 | `GET /api/v1/nutrition/gaps` | 用户 | `startDate`, `endDate` | 缺口列表 |
| 查询营养超标项 | `GET /api/v1/nutrition/excess` | 用户 | `startDate`, `endDate` | 超标列表 |
| 查询营养评分 | `GET /api/v1/nutrition/score` | 用户 | `date` | 评分 |
| 生成基础营养建议 | `GET /api/v1/nutrition/suggestions` | 用户 | `date` | 规则建议 |
| 重新计算营养统计 | `POST /api/v1/nutrition/recalculate` | 用户 | `startDate`, `endDate` | 重新计算结果 |

营养分析响应 `data` 示例：

```json
{
  "calorie": 520,
  "protein": 35.5,
  "fat": 18.2,
  "carbohydrate": 54.0,
  "macroRatio": {
    "proteinPercent": 27.3,
    "fatPercent": 31.5,
    "carbohydratePercent": 41.2
  },
  "score": 86,
  "warnings": ["晚餐脂肪占比略高"],
  "suggestions": ["晚餐可减少油脂摄入，增加蔬菜"]
}
```

### 8.9 体重趋势模块

#### 模块职责

维护用户体重历史，提供趋势图、统计值、目标进度和体重建议。

#### 核心表

`weight_record`, `health_profile`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 新增体重记录 | `POST /api/v1/weight-records` | 用户 | `recordDate`, `weightKg`, `remark` | 体重记录 |
| 修改体重记录 | `PUT /api/v1/weight-records/{recordId}` | 用户 | `weightKg`, `remark` | 体重记录 |
| 删除体重记录 | `DELETE /api/v1/weight-records/{recordId}` | 用户 | `recordId` | 空 |
| 查询体重详情 | `GET /api/v1/weight-records/{recordId}` | 用户 | `recordId` | 体重记录 |
| 查询体重列表 | `GET /api/v1/weight-records` | 用户 | `startDate`, `endDate`, 分页参数 | 分页记录 |
| 查询最近体重 | `GET /api/v1/weight-records/latest` | 用户 | 无 | 最近记录 |
| 查询体重趋势 | `GET /api/v1/weight-records/trend` | 用户 | `startDate`, `endDate` | 折线图数据 |
| 查询体重统计 | `GET /api/v1/weight-records/statistics` | 用户 | `startDate`, `endDate` | 平均、最大、最小 |
| 查询目标体重进度 | `GET /api/v1/weight-records/goal-progress` | 用户 | 无 | 当前进度 |
| 生成体重建议 | `GET /api/v1/weight-records/suggestions` | 用户 | 无 | 规则建议 |

### 8.10 报表统计模块

#### 模块职责

基于膳食记录和营养统计生成周报、月报、趋势图、目标达成率、常吃食物排行，并支持导出。

#### 核心表

`nutrition_daily_stat`, `nutrition_report`, `meal_record`, `meal_record_item`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 生成周营养报表 | `POST /api/v1/reports/weekly/generate` | 用户 | `weekStartDate` | 周报 |
| 生成月营养报表 | `POST /api/v1/reports/monthly/generate` | 用户 | `month` | 月报 |
| 查询周报 | `GET /api/v1/reports/weekly` | 用户 | `weekStartDate` | 周报 |
| 查询月报 | `GET /api/v1/reports/monthly` | 用户 | `month` | 月报 |
| 查询热量趋势 | `GET /api/v1/reports/calorie-trend` | 用户 | 日期范围 | 趋势数据 |
| 查询蛋白质趋势 | `GET /api/v1/reports/protein-trend` | 用户 | 日期范围 | 趋势数据 |
| 查询三大营养素占比 | `GET /api/v1/reports/macro-ratio` | 用户 | 日期范围 | 占比数据 |
| 查询饮食达标率 | `GET /api/v1/reports/compliance-rate` | 用户 | 日期范围 | 达标率 |
| 查询常吃食物排行 | `GET /api/v1/reports/top-foods` | 用户 | 日期范围、`limit` | 排行榜 |
| 查询健康目标完成度 | `GET /api/v1/reports/goal-progress` | 用户 | 日期范围 | 完成度 |
| 导出周报 PDF | `GET /api/v1/reports/weekly/export/pdf` | 用户 | `weekStartDate` | PDF 文件 |
| 导出月报 PDF | `GET /api/v1/reports/monthly/export/pdf` | 用户 | `month` | PDF 文件 |
| 导出报表 Excel | `GET /api/v1/reports/export/excel` | 用户 | 日期范围 | Excel 文件 |
| 生成报表摘要 | `GET /api/v1/reports/summary` | 用户 | 日期范围 | 自然语言摘要 |

### 8.11 食物图片识别模块

#### 模块职责

接收前端上传的食物图片，调用 Python AI 服务完成图片分类，保存识别任务和结果，并支持用户确认、修正、转膳食记录和生成 AI 建议。

#### 核心表

`food_recognition_task`, `food_recognition_result`, `file_resource`, `ingredient`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 上传食物图片识别 | `POST /api/v1/food-recognition/image` | 用户 | `image`, `topK` | 任务和识别结果 |
| 批量上传图片识别 | `POST /api/v1/food-recognition/batch` | 用户 | `images`, `topK` | 任务列表 |
| 查询识别任务详情 | `GET /api/v1/food-recognition/tasks/{taskId}` | 用户 | `taskId` | 任务详情 |
| 查询我的识别历史 | `GET /api/v1/food-recognition/history` | 用户 | 分页参数 | 识别历史 |
| 确认识别结果 | `POST /api/v1/food-recognition/tasks/{taskId}/confirm` | 用户 | `label`, `ingredientId`, `amount` | 确认结果 |
| 重新识别图片 | `POST /api/v1/food-recognition/tasks/{taskId}/retry` | 用户 | `topK` | 新识别结果 |
| 查询识别营养分析 | `GET /api/v1/food-recognition/tasks/{taskId}/nutrition` | 用户 | `taskId` | 营养估算 |
| 识别结果转膳食记录 | `POST /api/v1/food-recognition/tasks/{taskId}/to-meal-record` | 用户 | `mealType`, `recordDate`, `amount` | 记录详情 |
| 识别结果生成 AI 建议 | `POST /api/v1/food-recognition/tasks/{taskId}/ai-advice` | 用户 | 可选问题 | AI 建议 |
| 分享识别结果到社区 | `POST /api/v1/food-recognition/tasks/{taskId}/share-to-community` | 用户 | `title`, `content` | 帖子 ID |
| 查询模型支持类别 | `GET /api/v1/food-recognition/model/labels` | 游客 | 无 | 标签列表 |
| 查询识别模型信息 | `GET /api/v1/food-recognition/model/info` | 游客 | 无 | 模型信息 |

识别响应 `data` 示例：

```json
{
  "taskId": 90001,
  "status": "SUCCESS",
  "imageUrl": "https://example.com/food.jpg",
  "results": [
    {
      "label": "chicken_wings",
      "displayName": "鸡翅",
      "confidence": 0.91,
      "matchedIngredientId": 12
    }
  ]
}
```

### 8.12 AI 智能膳食顾问模块

#### 模块职责

提供自然语言膳食问答、流式问答、今日和一周饮食诊断、食物替代、慢性病饮食建议、风险提醒、计划解释和报表解释。

#### 核心表

`ai_conversation`, `ai_message`, `ai_call_log`, `health_profile`, `meal_record`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| AI 膳食问答 | `POST /api/v1/ai/advisor/chat` | 用户 | `conversationId`, `message`, `useHealthContext` | AI 回复 |
| AI 流式问答 | `POST /api/v1/ai/advisor/chat/stream` | 用户 | 同普通问答 | SSE 流 |
| 查询 AI 会话列表 | `GET /api/v1/ai/advisor/conversations` | 用户 | 分页参数 | 会话列表 |
| 查询 AI 会话详情 | `GET /api/v1/ai/advisor/conversations/{conversationId}` | 用户 | `conversationId` | 消息列表 |
| 删除 AI 会话 | `DELETE /api/v1/ai/advisor/conversations/{conversationId}` | 用户 | `conversationId` | 空 |
| 今日营养诊断 | `POST /api/v1/ai/advisor/today-diagnosis` | 用户 | `date` | 诊断文本 |
| 一周饮食诊断 | `POST /api/v1/ai/advisor/weekly-diagnosis` | 用户 | `startDate` | 诊断文本 |
| 减脂晚餐推荐 | `POST /api/v1/ai/advisor/fat-loss-dinner` | 用户 | `availableIngredients` | 推荐内容 |
| 食物替代推荐 | `POST /api/v1/ai/advisor/food-alternatives` | 用户 | `foodName`, `goal` | 替代建议 |
| 慢性病饮食建议 | `POST /api/v1/ai/advisor/chronic-disease-advice` | 用户 | `diseaseCode` | 建议 |
| 饮食风险提醒 | `POST /api/v1/ai/advisor/risk-warning` | 用户 | 日期或食物信息 | 风险提醒 |
| 解释膳食计划 | `POST /api/v1/ai/advisor/explain-meal-plan/{planId}` | 用户 | `planId` | 解释文本 |
| 解释营养报表 | `POST /api/v1/ai/advisor/explain-report` | 用户 | `reportType`, `reportId` | 解释文本 |
| 查询 AI 可用模型 | `GET /api/v1/ai/models` | 用户 | 无 | 模型配置 |
| AI 服务健康检查 | `GET /api/v1/ai/health` | 用户 | 无 | 健康状态 |

聊天请求示例：

```json
{
  "conversationId": null,
  "message": "我今天晚餐想减脂，家里有鸡胸肉和西兰花，怎么搭配？",
  "useHealthContext": true
}
```

### 8.13 AI 智能菜谱模块

#### 模块职责

调用 DeepSeek 生成原创菜谱、减脂菜谱、增肌菜谱、控糖菜谱、一周菜谱、制作步骤、采购清单，并支持保存到正式菜谱库或分享到社区。

#### 核心表

`ai_recipe`, `ai_call_log`, `recipe`, `recipe_ingredient`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| AI 生成原创菜谱 | `POST /api/v1/ai/recipes/generate` | 用户 | 食材、口味、目标 | AI 菜谱 |
| AI 生成减脂菜谱 | `POST /api/v1/ai/recipes/generate/fat-loss` | 用户 | 食材、热量限制 | AI 菜谱 |
| AI 生成增肌菜谱 | `POST /api/v1/ai/recipes/generate/muscle-gain` | 用户 | 食材、蛋白目标 | AI 菜谱 |
| AI 生成控糖菜谱 | `POST /api/v1/ai/recipes/generate/sugar-control` | 用户 | 食材、控糖要求 | AI 菜谱 |
| AI 生成一周菜谱 | `POST /api/v1/ai/recipes/week-plan` | 用户 | `startDate`, `goal`, `calorieTarget` | 一周菜谱 |
| AI 估算菜谱营养 | `POST /api/v1/ai/recipes/nutrition-estimate` | 用户 | 菜谱结构 | 营养估算 |
| AI 生成制作步骤 | `POST /api/v1/ai/recipes/cooking-steps` | 用户 | 菜名和食材 | 步骤列表 |
| AI 生成菜谱配图描述 | `POST /api/v1/ai/recipes/image-prompt` | 用户 | 菜谱信息 | 图片提示词 |
| AI 生成采购清单 | `POST /api/v1/ai/recipes/shopping-list` | 用户 | 菜谱列表 | 采购清单 |
| AI 生成一周膳食海报内容 | `POST /api/v1/ai/recipes/week-poster` | 用户 | 周计划 ID | 海报文案 |
| 保存 AI 菜谱 | `POST /api/v1/ai/recipes/save` | 用户 | `aiRecipeId` | 正式菜谱 ID |
| 查询 AI 菜谱历史 | `GET /api/v1/ai/recipes/history` | 用户 | 分页参数 | 历史列表 |
| 删除 AI 菜谱记录 | `DELETE /api/v1/ai/recipes/history/{id}` | 用户 | `id` | 空 |
| AI 菜谱转膳食计划 | `POST /api/v1/ai/recipes/{aiRecipeId}/to-meal-plan` | 用户 | 日期、餐次 | 计划项 |
| AI 菜谱转社区帖子 | `POST /api/v1/ai/recipes/{aiRecipeId}/share-to-community` | 用户 | 标题、正文 | 帖子 ID |

### 8.14 社区帖子模块

#### 模块职责

支持用户发布图文帖子，展示帖子流、推荐帖子、热门帖子、关注用户帖子，以及从菜谱、膳食记录、AI 菜谱、图片识别结果生成帖子。

#### 核心表

`community_post`, `community_post_image`, `community_post_tag`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 发布帖子 | `POST /api/v1/community/posts` | 用户 | `title`, `content`, `imageIds`, `tagIds` | 帖子详情 |
| 修改帖子 | `PUT /api/v1/community/posts/{postId}` | 作者 | 帖子内容 | 帖子详情 |
| 删除帖子 | `DELETE /api/v1/community/posts/{postId}` | 作者或管理员 | `postId` | 空 |
| 查询帖子详情 | `GET /api/v1/community/posts/{postId}` | 游客 | `postId` | 帖子详情 |
| 分页查询帖子 | `GET /api/v1/community/posts` | 游客 | `keyword`, `tagId`, 分页参数 | 帖子列表 |
| 查询我的帖子 | `GET /api/v1/community/posts/me` | 用户 | 分页参数 | 帖子列表 |
| 查询用户帖子 | `GET /api/v1/community/users/{userId}/posts` | 游客 | `userId`, 分页参数 | 帖子列表 |
| 查询推荐帖子 | `GET /api/v1/community/posts/recommend` | 用户 | 分页参数 | 推荐帖子 |
| 查询热门帖子 | `GET /api/v1/community/posts/hot` | 游客 | 分页参数 | 热门帖子 |
| 查询最新帖子 | `GET /api/v1/community/posts/latest` | 游客 | 分页参数 | 最新帖子 |
| 查询关注用户帖子 | `GET /api/v1/community/posts/following` | 用户 | 分页参数 | 关注流 |
| 增加帖子浏览量 | `POST /api/v1/community/posts/{postId}/view` | 游客 | `postId` | 空 |
| 查询帖子统计 | `GET /api/v1/community/posts/{postId}/statistics` | 游客 | `postId` | 互动统计 |
| 从菜谱发布帖子 | `POST /api/v1/community/posts/from-recipe/{recipeId}` | 用户 | 标题、正文 | 帖子 ID |
| 从膳食记录发布帖子 | `POST /api/v1/community/posts/from-meal-record/{recordId}` | 用户 | 标题、正文 | 帖子 ID |
| 从 AI 菜谱发布帖子 | `POST /api/v1/community/posts/from-ai-recipe/{aiRecipeId}` | 用户 | 标题、正文 | 帖子 ID |
| 从图片识别结果发布帖子 | `POST /api/v1/community/posts/from-food-recognition/{taskId}` | 用户 | 标题、正文 | 帖子 ID |
| 查询帖子关联菜谱 | `GET /api/v1/community/posts/{postId}/related-recipe` | 游客 | `postId` | 菜谱详情 |
| 查询帖子关联膳食记录 | `GET /api/v1/community/posts/{postId}/related-meal-record` | 作者或管理员 | `postId` | 膳食记录 |
| 查询帖子关联 AI 结果 | `GET /api/v1/community/posts/{postId}/related-ai-result` | 作者或管理员 | `postId` | AI 结果 |

### 8.15 社区图片、点赞、收藏模块

#### 模块职责

管理帖子图片、帖子点赞、帖子收藏及对应状态查询。

#### 核心表

`community_post_image`, `community_like`, `community_favorite`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 上传帖子图片 | `POST /api/v1/community/posts/images` | 用户 | `file` | 图片信息 |
| 删除帖子图片 | `DELETE /api/v1/community/posts/images/{imageId}` | 上传者 | `imageId` | 空 |
| 查询帖子图片 | `GET /api/v1/community/posts/{postId}/images` | 游客 | `postId` | 图片列表 |
| 设置封面图 | `PATCH /api/v1/community/posts/{postId}/cover` | 作者 | `imageId` | 空 |
| 点赞帖子 | `POST /api/v1/community/posts/{postId}/like` | 用户 | `postId` | 空 |
| 取消点赞 | `DELETE /api/v1/community/posts/{postId}/like` | 用户 | `postId` | 空 |
| 查询点赞状态 | `GET /api/v1/community/posts/{postId}/like/status` | 用户 | `postId` | `liked` |
| 查询点赞用户 | `GET /api/v1/community/posts/{postId}/likes` | 游客 | 分页参数 | 用户列表 |
| 查询我点赞的帖子 | `GET /api/v1/community/posts/liked` | 用户 | 分页参数 | 帖子列表 |
| 收藏帖子 | `POST /api/v1/community/posts/{postId}/favorite` | 用户 | `postId` | 空 |
| 取消收藏 | `DELETE /api/v1/community/posts/{postId}/favorite` | 用户 | `postId` | 空 |
| 查询收藏状态 | `GET /api/v1/community/posts/{postId}/favorite/status` | 用户 | `postId` | `favorited` |
| 查询我收藏的帖子 | `GET /api/v1/community/posts/favorites` | 用户 | 分页参数 | 帖子列表 |
| 查询收藏用户 | `GET /api/v1/community/posts/{postId}/favorites` | 作者或管理员 | 分页参数 | 用户列表 |

### 8.16 社区评论模块

#### 模块职责

支持一级评论和二级回复，支持评论点赞、删除、查询我的评论和收到的评论通知。

#### 核心表

`community_comment`, `community_like`, `notification`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 发表评论 | `POST /api/v1/community/posts/{postId}/comments` | 用户 | `content` | 评论详情 |
| 回复评论 | `POST /api/v1/community/comments/{commentId}/replies` | 用户 | `content` | 回复详情 |
| 删除评论 | `DELETE /api/v1/community/comments/{commentId}` | 作者或管理员 | `commentId` | 空 |
| 查询帖子评论 | `GET /api/v1/community/posts/{postId}/comments` | 游客 | 分页参数 | 一级评论 |
| 查询评论回复 | `GET /api/v1/community/comments/{commentId}/replies` | 游客 | 分页参数 | 二级回复 |
| 查询评论详情 | `GET /api/v1/community/comments/{commentId}` | 游客 | `commentId` | 评论详情 |
| 点赞评论 | `POST /api/v1/community/comments/{commentId}/like` | 用户 | `commentId` | 空 |
| 取消点赞评论 | `DELETE /api/v1/community/comments/{commentId}/like` | 用户 | `commentId` | 空 |
| 查询评论点赞状态 | `GET /api/v1/community/comments/{commentId}/like/status` | 用户 | `commentId` | `liked` |
| 查询我发表的评论 | `GET /api/v1/community/comments/me` | 用户 | 分页参数 | 评论列表 |
| 查询收到的评论 | `GET /api/v1/community/comments/received` | 用户 | 分页参数 | 评论和回复列表 |

### 8.17 社区标签、搜索、举报模块

#### 模块职责

管理社区标签、社区搜索和内容举报。

#### 核心表

`community_tag`, `community_post_tag`, `community_report`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 创建标签 | `POST /api/v1/community/tags` | 管理员或用户 | `name`, `category` | 标签详情 |
| 查询标签列表 | `GET /api/v1/community/tags` | 游客 | `keyword`, `category` | 标签列表 |
| 查询热门标签 | `GET /api/v1/community/tags/hot` | 游客 | `limit` | 热门标签 |
| 查询标签详情 | `GET /api/v1/community/tags/{tagId}` | 游客 | `tagId` | 标签详情 |
| 查询标签下帖子 | `GET /api/v1/community/tags/{tagId}/posts` | 游客 | 分页参数 | 帖子列表 |
| 修改标签 | `PUT /api/v1/community/tags/{tagId}` | 管理员 | 标签信息 | 标签详情 |
| 删除标签 | `DELETE /api/v1/community/tags/{tagId}` | 管理员 | `tagId` | 空 |
| 给帖子绑定标签 | `POST /api/v1/community/posts/{postId}/tags` | 作者 | `tagIds` | 标签列表 |
| 移除帖子标签 | `DELETE /api/v1/community/posts/{postId}/tags/{tagId}` | 作者 | `tagId` | 空 |
| 搜索帖子 | `GET /api/v1/community/search/posts` | 游客 | `keyword`, 分页参数 | 帖子列表 |
| 搜索用户 | `GET /api/v1/community/search/users` | 游客 | `keyword`, 分页参数 | 用户列表 |
| 搜索标签 | `GET /api/v1/community/search/tags` | 游客 | `keyword` | 标签列表 |
| 综合搜索 | `GET /api/v1/community/search` | 游客 | `keyword` | 帖子、用户、标签 |
| 举报帖子 | `POST /api/v1/community/posts/{postId}/report` | 用户 | `reasonType`, `reason` | 举报记录 |
| 举报评论 | `POST /api/v1/community/comments/{commentId}/report` | 用户 | `reasonType`, `reason` | 举报记录 |
| 查询我的举报 | `GET /api/v1/community/reports/me` | 用户 | 分页参数 | 举报列表 |

### 8.18 采购清单模块

#### 模块职责

支持手动创建采购清单，也可以根据单日或一周膳食计划自动汇总食材采购量。

#### 核心表

`shopping_list`, `shopping_list_item`, `meal_plan`, `recipe_ingredient`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 创建采购清单 | `POST /api/v1/shopping-lists` | 用户 | 清单名称、明细 | 清单详情 |
| 根据膳食计划生成清单 | `POST /api/v1/shopping-lists/from-meal-plan/{planId}` | 用户 | `planId` | 清单详情 |
| 根据一周计划生成清单 | `POST /api/v1/shopping-lists/from-week-plan` | 用户 | `startDate` | 清单详情 |
| 查询采购清单列表 | `GET /api/v1/shopping-lists` | 用户 | 分页参数 | 清单列表 |
| 查询采购清单详情 | `GET /api/v1/shopping-lists/{listId}` | 用户 | `listId` | 清单详情 |
| 修改采购清单 | `PUT /api/v1/shopping-lists/{listId}` | 用户 | 清单内容 | 清单详情 |
| 删除采购清单 | `DELETE /api/v1/shopping-lists/{listId}` | 用户 | `listId` | 空 |
| 勾选已购买 | `PATCH /api/v1/shopping-lists/{listId}/items/{itemId}/checked` | 用户 | `itemId` | 空 |
| 取消已购买 | `PATCH /api/v1/shopping-lists/{listId}/items/{itemId}/unchecked` | 用户 | `itemId` | 空 |
| 导出采购清单 | `GET /api/v1/shopping-lists/{listId}/export` | 用户 | `listId` | 文件下载 |

### 8.19 文件资源模块

#### 模块职责

统一管理头像、菜谱图、膳食记录图、社区帖子图、识别图片和导出文件。

#### 核心表

`file_resource`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 上传通用文件 | `POST /api/v1/files/upload` | 用户 | `file`, `bizType` | 文件信息 |
| 上传用户头像 | `POST /api/v1/files/avatar` | 用户 | `file` | 文件信息 |
| 上传菜谱图片 | `POST /api/v1/files/recipe-image` | 用户 | `file` | 文件信息 |
| 上传膳食记录图片 | `POST /api/v1/files/meal-record-image` | 用户 | `file` | 文件信息 |
| 上传帖子图片 | `POST /api/v1/files/community-post-image` | 用户 | `file` | 文件信息 |
| 查询文件信息 | `GET /api/v1/files/{fileId}` | 用户 | `fileId` | 文件元数据 |
| 删除文件 | `DELETE /api/v1/files/{fileId}` | 上传者或管理员 | `fileId` | 空 |
| 查询文件访问地址 | `GET /api/v1/files/{fileId}/url` | 用户 | `fileId` | 临时或公开 URL |

文件响应 `data` 示例：

```json
{
  "fileId": 30001,
  "originalName": "food.jpg",
  "contentType": "image/jpeg",
  "size": 524288,
  "url": "https://example.com/files/30001",
  "bizType": "FOOD_RECOGNITION"
}
```

### 8.20 系统字典模块

#### 模块职责

提供前端表单下拉选项、筛选项和标签枚举，减少前后端硬编码。

#### 核心表

`dictionary_item`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 查询性别字典 | `GET /api/v1/dictionaries/genders` | 游客 | 无 | 字典项 |
| 查询餐次字典 | `GET /api/v1/dictionaries/meal-types` | 游客 | 无 | 字典项 |
| 查询健康目标字典 | `GET /api/v1/dictionaries/health-goals` | 游客 | 无 | 字典项 |
| 查询慢性病字典 | `GET /api/v1/dictionaries/chronic-diseases` | 游客 | 无 | 字典项 |
| 查询过敏源字典 | `GET /api/v1/dictionaries/allergens` | 游客 | 无 | 字典项 |
| 查询饮食禁忌字典 | `GET /api/v1/dictionaries/diet-restrictions` | 游客 | 无 | 字典项 |
| 查询食材分类字典 | `GET /api/v1/dictionaries/ingredient-categories` | 游客 | 无 | 字典项 |
| 查询菜谱分类字典 | `GET /api/v1/dictionaries/recipe-categories` | 游客 | 无 | 字典项 |
| 查询营养素字典 | `GET /api/v1/dictionaries/nutrients` | 游客 | 无 | 字典项 |
| 查询社区标签分类 | `GET /api/v1/dictionaries/community-tag-categories` | 游客 | 无 | 字典项 |

### 8.21 通知消息模块

#### 模块职责

为社区点赞、评论、收藏、关注、系统审核结果和举报处理结果生成通知。

#### 核心表

`notification`

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 查询我的通知 | `GET /api/v1/notifications` | 用户 | `type`, `read`, 分页参数 | 通知列表 |
| 查询未读通知数 | `GET /api/v1/notifications/unread-count` | 用户 | 无 | 未读数量 |
| 标记通知已读 | `PATCH /api/v1/notifications/{notificationId}/read` | 用户 | `notificationId` | 空 |
| 全部标记已读 | `PATCH /api/v1/notifications/read-all` | 用户 | 可选 `type` | 空 |
| 删除通知 | `DELETE /api/v1/notifications/{notificationId}` | 用户 | `notificationId` | 空 |
| 查询点赞通知 | `GET /api/v1/notifications/likes` | 用户 | 分页参数 | 通知列表 |
| 查询评论通知 | `GET /api/v1/notifications/comments` | 用户 | 分页参数 | 通知列表 |
| 查询收藏通知 | `GET /api/v1/notifications/favorites` | 用户 | 分页参数 | 通知列表 |
| 查询系统通知 | `GET /api/v1/notifications/system` | 用户 | 分页参数 | 通知列表 |

### 8.22 管理后台模块

#### 模块职责

管理员统一管理用户、食材、菜谱、社区内容、举报、AI 配置、模型状态、系统配置、日志和备份。

#### 核心表

所有业务表、`system_config`, `access_log`, `error_log`, `ai_call_log`

#### 用户管理接口

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 查询用户列表 | `GET /api/v1/admin/users` | 管理员 | `keyword`, `status`, 分页参数 | 用户列表 |
| 查询用户详情 | `GET /api/v1/admin/users/{userId}` | 管理员 | `userId` | 用户详情 |
| 禁用用户 | `PATCH /api/v1/admin/users/{userId}/disable` | 管理员 | `reason` | 空 |
| 启用用户 | `PATCH /api/v1/admin/users/{userId}/enable` | 管理员 | `userId` | 空 |
| 重置用户密码 | `PATCH /api/v1/admin/users/{userId}/reset-password` | 管理员 | 可选新密码 | 临时密码 |
| 查询用户健康档案 | `GET /api/v1/admin/users/{userId}/health-profile` | 管理员 | `userId` | 健康档案 |
| 查询用户膳食记录 | `GET /api/v1/admin/users/{userId}/meal-records` | 管理员 | 日期范围、分页参数 | 记录列表 |

#### 食材与菜谱管理接口

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 管理员查询食材 | `GET /api/v1/admin/ingredients` | 管理员 | 查询条件 | 食材列表 |
| 管理员审核食材 | `PATCH /api/v1/admin/ingredients/{ingredientId}/review` | 管理员 | `reviewStatus`, `reason` | 空 |
| 管理员查询菜谱 | `GET /api/v1/admin/recipes` | 管理员 | 查询条件 | 菜谱列表 |
| 管理员审核菜谱 | `PATCH /api/v1/admin/recipes/{recipeId}/review` | 管理员 | `reviewStatus`, `reason` | 空 |
| 管理员下架菜谱 | `PATCH /api/v1/admin/recipes/{recipeId}/offline` | 管理员 | `reason` | 空 |
| 管理员恢复菜谱 | `PATCH /api/v1/admin/recipes/{recipeId}/online` | 管理员 | `recipeId` | 空 |

#### 社区管理接口

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 管理员查询帖子 | `GET /api/v1/admin/community/posts` | 管理员 | 查询条件 | 帖子列表 |
| 管理员审核帖子 | `PATCH /api/v1/admin/community/posts/{postId}/review` | 管理员 | `reviewStatus`, `reason` | 空 |
| 管理员下架帖子 | `PATCH /api/v1/admin/community/posts/{postId}/offline` | 管理员 | `reason` | 空 |
| 管理员恢复帖子 | `PATCH /api/v1/admin/community/posts/{postId}/online` | 管理员 | `postId` | 空 |
| 管理员删除评论 | `DELETE /api/v1/admin/community/comments/{commentId}` | 管理员 | `commentId`, `reason` | 空 |
| 管理员查询举报 | `GET /api/v1/admin/community/reports` | 管理员 | `status`, 分页参数 | 举报列表 |
| 管理员处理举报 | `PATCH /api/v1/admin/community/reports/{reportId}/handle` | 管理员 | `handleResult`, `remark` | 空 |
| 管理员管理标签 | `GET /api/v1/admin/community/tags` | 管理员 | 查询条件 | 标签列表 |
| 管理员修改标签 | `PUT /api/v1/admin/community/tags/{tagId}` | 管理员 | 标签信息 | 标签详情 |
| 管理员删除标签 | `DELETE /api/v1/admin/community/tags/{tagId}` | 管理员 | `tagId` | 空 |

#### AI 管理接口

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 查询 AI 配置 | `GET /api/v1/admin/ai/configs` | 管理员 | 无 | 配置列表 |
| 修改 AI 配置 | `PUT /api/v1/admin/ai/configs` | 管理员 | DeepSeek、模型配置 | 配置列表 |
| 查询 AI 调用日志 | `GET /api/v1/admin/ai/logs` | 管理员 | 日期范围、模型、分页参数 | 调用日志 |
| 查询本地模型列表 | `GET /api/v1/admin/ai/local-models` | 管理员 | 无 | 模型列表 |
| 查询本地模型详情 | `GET /api/v1/admin/ai/local-models/{modelId}` | 管理员 | `modelId` | 模型详情 |
| 启用本地模型 | `PATCH /api/v1/admin/ai/local-models/{modelId}/enable` | 管理员 | `modelId` | 空 |
| 停用本地模型 | `PATCH /api/v1/admin/ai/local-models/{modelId}/disable` | 管理员 | `modelId` | 空 |
| 重新加载本地模型 | `POST /api/v1/admin/ai/local-models/{modelId}/reload` | 管理员 | `modelId` | 加载结果 |
| 查询 AI 服务状态 | `GET /api/v1/admin/ai/health` | 管理员 | 无 | 健康状态 |

#### 系统管理接口

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 查询后台首页数据 | `GET /api/v1/admin/dashboard` | 管理员 | 无 | 用户数、帖子数、菜谱数、AI 调用数 |
| 查询系统配置 | `GET /api/v1/admin/system/configs` | 管理员 | 查询条件 | 配置列表 |
| 修改系统配置 | `PUT /api/v1/admin/system/configs/{configKey}` | 管理员 | `configValue` | 配置详情 |
| 查询访问日志 | `GET /api/v1/admin/logs/access` | 管理员 | 日期范围、分页参数 | 日志列表 |
| 查询异常日志 | `GET /api/v1/admin/logs/error` | 管理员 | 日期范围、分页参数 | 日志列表 |
| 创建数据备份 | `POST /api/v1/admin/backups` | 管理员 | `remark` | 备份记录 |
| 查询备份记录 | `GET /api/v1/admin/backups` | 管理员 | 分页参数 | 备份列表 |
| 恢复备份 | `POST /api/v1/admin/backups/{backupId}/restore` | 管理员 | `backupId` | 恢复结果 |

### 8.23 Spring Boot 内部接口模块

#### 模块职责

为 Python AI 服务或系统内部任务提供受保护的数据查询、营养计算、日志记录和回调入口。

#### 接口设计

| 接口 | 方法与路径 | 权限 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 查询用户 AI 上下文 | `GET /api/internal/v1/users/{userId}/ai-context` | 内部服务 | `userId` | 健康档案、偏好、禁忌 |
| 查询用户今日营养上下文 | `GET /api/internal/v1/users/{userId}/nutrition-context/today` | 内部服务 | `userId`, `date` | 今日营养 |
| 查询用户一周营养上下文 | `GET /api/internal/v1/users/{userId}/nutrition-context/week` | 内部服务 | `userId`, `startDate` | 周营养 |
| 根据模型标签匹配食材 | `POST /api/internal/v1/ingredients/match-by-labels` | 内部服务 | `labels` | 匹配食材 |
| 根据模型标签匹配菜谱 | `POST /api/internal/v1/recipes/match-by-labels` | 内部服务 | `labels` | 匹配菜谱 |
| 内部营养计算 | `POST /api/internal/v1/nutrition/calculate` | 内部服务 | 食材用量 | 营养结果 |
| 保存 AI 调用日志 | `POST /api/internal/v1/ai/logs` | 内部服务 | 调用日志 | 日志 ID |
| 保存食物识别回调 | `POST /api/internal/v1/food-recognition/callback` | 内部服务 | 任务 ID、识别结果 | 空 |
| 查询文件内部访问地址 | `GET /api/internal/v1/files/{fileId}/access-url` | 内部服务 | `fileId` | 内部 URL |

内部接口安全要求：

- 必须携带 `X-Internal-Token`。
- 不允许公网直接访问。
- 记录调用来源、耗时和异常信息。

### 8.24 Python AI 服务接口模块

#### 模块职责

封装本地食物分类模型和 DeepSeek 调用，向 Spring Boot 提供稳定的 AI 能力接口。

#### 基础与模型接口

| 接口 | 方法与路径 | 调用方 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| AI 服务健康检查 | `GET /ai/v1/health` | Spring Boot | 无 | 健康状态 |
| 查询模型状态 | `GET /ai/v1/models/status` | Spring Boot | 无 | 模型加载状态 |
| 查询食物分类模型信息 | `GET /ai/v1/models/food-classifier/info` | Spring Boot | 无 | 模型名称、版本、类别数 |
| 查询食物类别标签 | `GET /ai/v1/models/food-classifier/labels` | Spring Boot | 无 | 标签列表 |
| 重新加载分类模型 | `POST /ai/v1/models/food-classifier/reload` | Spring Boot | 无 | 加载结果 |

#### 食物图片分类接口

| 接口 | 方法与路径 | 调用方 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| 食物图片分类 | `POST /ai/v1/food/classify` | Spring Boot | `image`, `topK` | Top-K 分类 |
| 批量图片分类 | `POST /ai/v1/food/batch-classify` | Spring Boot | `images`, `topK` | 批量分类结果 |
| 图片分类并解释 | `POST /ai/v1/food/classify-and-explain` | Spring Boot | 图片和用户上下文 | 分类与解释 |
| 图片识别生成建议 | `POST /ai/v1/food/image-meal-advice` | Spring Boot | 分类结果、健康上下文 | 饮食建议 |

分类响应示例：

```json
{
  "modelName": "resnet18-food101",
  "modelVersion": "1.0.0",
  "results": [
    {
      "label": "greek_salad",
      "displayName": "希腊沙拉",
      "confidence": 0.89
    }
  ],
  "elapsedMs": 230
}
```

#### DeepSeek 调用接口

| 接口 | 方法与路径 | 调用方 | 请求参数 | 响应数据 |
| --- | --- | --- | --- | --- |
| DeepSeek 普通问答 | `POST /ai/v1/llm/chat` | Spring Boot | `messages`, `context`, `model` | 回复文本 |
| DeepSeek 流式问答 | `POST /ai/v1/llm/chat/stream` | Spring Boot | 同普通问答 | SSE 流 |
| 生成今日营养诊断 | `POST /ai/v1/llm/today-diagnosis` | Spring Boot | 今日营养上下文 | 诊断文本 |
| 生成一周饮食建议 | `POST /ai/v1/llm/weekly-advice` | Spring Boot | 一周上下文 | 建议文本 |
| 生成替代食物建议 | `POST /ai/v1/llm/food-alternatives` | Spring Boot | 食物和目标 | 替代建议 |
| 生成菜谱 | `POST /ai/v1/llm/recipe-generate` | Spring Boot | 食材、目标、偏好 | 菜谱 JSON |
| 生成菜谱配图描述 | `POST /ai/v1/llm/recipe-image-prompt` | Spring Boot | 菜谱信息 | 图片提示词 |
| 生成膳食海报文案 | `POST /ai/v1/llm/meal-poster` | Spring Boot | 周计划 | 海报文案 |

## 9. 营养计算与推荐规则设计

### 9.1 BMI 计算

```text
BMI = 体重 kg / (身高 m * 身高 m)
```

BMI 分级可按常见标准设计：

| BMI 范围 | 状态 |
| --- | --- |
| `< 18.5` | 偏瘦 |
| `18.5 - 23.9` | 正常 |
| `24.0 - 27.9` | 超重 |
| `>= 28.0` | 肥胖 |

### 9.2 菜谱营养计算

菜谱营养由食材营养按用量折算：

```text
某营养素总量 = Σ(食材每 100g 营养素值 * 食材用量 g / 100)
```

### 9.3 膳食推荐过滤规则

生成膳食计划时建议按以下顺序过滤：

1. 过滤用户过敏源相关食材。
2. 过滤用户饮食禁忌不允许的菜谱。
3. 根据慢性病限制高糖、高盐、高脂或高嘌呤食物。
4. 根据健康目标选择减脂、增肌、控糖、低盐等标签菜谱。
5. 根据用户偏好提高匹配分。
6. 根据热量目标控制全天总热量。

### 9.4 推荐评分示例

```text
推荐分 = 目标匹配分 * 0.35
       + 营养均衡分 * 0.30
       + 偏好匹配分 * 0.20
       + 历史多样性分 * 0.15
```

## 10. AI 服务设计

### 10.1 图片识别模型设计

- 使用 Food-101 数据集训练 ResNet18 分类模型。
- Python 服务启动时加载模型权重和标签映射。
- 接收图片后进行尺寸调整、归一化、模型推理。
- 返回 Top-K 标签、置信度、模型版本和耗时。
- Spring Boot 根据标签匹配系统食材库，避免模型英文标签直接暴露给用户。

### 10.2 DeepSeek Prompt 设计原则

- Prompt 中传入结构化健康上下文，避免直接拼接无序文本。
- 明确要求输出为中文，风格简洁，避免医疗诊断承诺。
- 对慢性病相关建议添加“仅供饮食参考，不替代医生建议”的提示。
- 菜谱生成接口优先要求输出 JSON，便于后端保存和前端展示。

### 10.3 AI 调用日志

每次 AI 调用建议记录：

| 字段 | 说明 |
| --- | --- |
| `user_id` | 调用用户 |
| `scene` | 调用场景，如 CHAT、RECIPE_GENERATE |
| `model` | 模型名称 |
| `prompt_tokens` | 输入 token 数 |
| `completion_tokens` | 输出 token 数 |
| `elapsed_ms` | 耗时 |
| `success` | 是否成功 |
| `error_message` | 错误原因 |

## 11. 文件存储设计

课程项目可使用本地文件存储，目录建议：

```text
uploads/
  avatar/
  recipe/
  meal-record/
  community/
  recognition/
  export/
```

文件访问建议通过后端接口代理，避免直接暴露服务器真实路径。文件上传限制：

| 类型 | 限制 |
| --- | --- |
| 图片格式 | jpg、jpeg、png、webp |
| 单张图片大小 | 最大 5MB |
| 导入文件 | xlsx、json |
| 导出文件 | pdf、xlsx |

## 12. 安全设计

### 12.1 认证安全

- 密码使用 BCrypt 加密存储。
- 登录失败超过限制后短时间锁定或要求验证码。
- JWT 设置合理过期时间，刷新令牌落库便于失效控制。
- 修改密码、注销账号等敏感操作需要再次校验密码。

### 12.2 权限安全

- Controller 层使用注解限制角色，例如 `@PreAuthorize("hasRole('ADMIN')")`。
- Service 层校验资源归属，例如膳食记录只能由本人访问。
- 管理员接口统一放在 `/api/v1/admin`。
- 内部接口使用 `X-Internal-Token`，并限制网络来源。

### 12.3 数据安全

- 日志中不打印密码、Token、DeepSeek API Key。
- 用户邮箱、手机号在管理端可脱敏显示。
- 文件上传需校验后缀、MIME 类型和大小。
- AI Prompt 中尽量不传递不必要的个人敏感信息。

## 13. 异常处理设计

建议使用全局异常处理器统一捕获：

| 异常类型 | HTTP 状态码 | 业务码 |
| --- | --- | --- |
| 参数校验异常 | 400 | 40001 |
| 未登录异常 | 401 | 40100 |
| 权限不足异常 | 403 | 40300 |
| 资源不存在异常 | 404 | 40400 |
| 业务冲突异常 | 409 | 40900 |
| AI 服务异常 | 502 | 50200 |
| 系统异常 | 500 | 50000 |

## 14. 日志与审计设计

### 14.1 访问日志

记录请求路径、请求方法、用户 ID、IP、User-Agent、耗时、响应状态码和请求 ID。

### 14.2 业务日志

重点记录登录、修改密码、生成膳食计划、AI 调用、管理员审核、举报处理、备份恢复等操作。

### 14.3 异常日志

记录异常类型、异常堆栈、请求上下文和用户 ID，便于课程演示时定位问题。

## 15. 部署设计

### 15.1 本地开发部署

```text
Vue3 前端: http://localhost:5173
Spring Boot 后端: http://localhost:8080
Python AI 服务: http://localhost:8000
MySQL: localhost:3306
```

### 15.2 启动顺序

1. 启动 MySQL，创建数据库和表。
2. 启动 Python AI 服务，加载 ResNet18 模型。
3. 启动 Spring Boot 后端，检查数据库和 AI 服务连接。
4. 启动 Vue3 前端，调用 `/api/v1/ai/health` 和业务接口验证。

### 15.3 配置项建议

| 配置 | 说明 |
| --- | --- |
| `spring.datasource.*` | MySQL 连接配置 |
| `jwt.secret` | JWT 密钥 |
| `jwt.access-token-expire-seconds` | 访问令牌过期时间 |
| `file.upload-dir` | 文件上传目录 |
| `ai.base-url` | Python AI 服务地址 |
| `ai.internal-token` | 内部调用令牌 |
| `deepseek.api-key` | DeepSeek API Key，由 Python 服务读取 |

## 16. 开发优先级建议

为了保证课程大作业按时完成，建议分阶段实现：

### 16.1 第一阶段：基础闭环

- 注册登录、JWT 鉴权。
- 用户资料、健康档案。
- 食材库、菜谱库。
- 膳食记录、营养计算。

### 16.2 第二阶段：核心智能能力

- 膳食计划生成。
- 食物图片识别。
- AI 膳食问答。
- AI 菜谱生成。

### 16.3 第三阶段：展示与扩展

- 报表统计和体重趋势。
- 社区帖子、点赞、评论、收藏。
- 通知消息。
- 管理后台。

### 16.4 第四阶段：完善与答辩

- Swagger 接口文档。
- 演示数据初始化。
- 异常处理、权限校验、导出功能。
- 项目部署说明和系统截图。

## 17. 推荐 Controller 划分

```text
AuthController
UserController
HealthProfileController
IngredientController
RecipeController
MealPlanController
MealRecordController
NutritionController
WeightRecordController
ReportController
FoodRecognitionController
AiAdvisorController
AiRecipeController
ShoppingListController
CommunityPostController
CommunityImageController
CommunityLikeController
CommunityFavoriteController
CommunityCommentController
CommunityTagController
CommunitySearchController
CommunityReportController
NotificationController
FileController
DictionaryController

AdminUserController
AdminIngredientController
AdminRecipeController
AdminCommunityController
AdminAiController
AdminSystemController

InternalUserContextController
InternalNutritionController
InternalAiCallbackController
InternalFileController
```

## 18. 课程答辩展示建议

答辩时建议重点展示以下主线：

1. 用户注册登录后填写健康档案，系统计算 BMI。
2. 用户生成今日膳食计划，查看推荐原因和营养分析。
3. 用户上传食物图片，系统识别食物并转为膳食记录。
4. 用户查看每日或周营养报表，获得 AI 解释。
5. 用户将菜谱或膳食记录分享到社区，其他用户点赞评论。
6. 管理员登录后台，审核菜谱、帖子和举报。

## 19. 总结

本系统以后端业务服务为核心，结合 MySQL 数据持久化、Python AI 服务、本地食物分类模型和 DeepSeek 大模型能力，形成“健康档案 -> 膳食计划 -> 膳食记录 -> 营养分析 -> AI 建议 -> 社区分享”的完整业务闭环。

系统模块划分清晰，接口路径统一，权限边界明确，既能满足课程大作业的完整性要求，也方便后续按模块分阶段开发和演示。
