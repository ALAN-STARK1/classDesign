# 智能健康膳食管理系统 API DTO 定义文档

## 1. 文档说明

本文档以《智能健康膳食管理系统 Web 后端系统设计文档》中的数据库表设计和接口示例为基准，为每个接口明确 Request DTO 与 Response DTO 的完整字段、数据类型、是否必填和说明，作为前后端联调与后端实现的字段契约。

阅读约定：

- 字段名采用驼峰命名，与前端 JSON 字段保持一致。
- 后端实体使用下划线命名（如 `created_at`），DTO 层统一转换为驼峰（如 `createdAt`）。
- 所有接口返回均包裹在统一响应体 `ApiResponse<T>` 中，本文档「Response」部分只描述 `data` 字段内部结构。
- 「必填」列含义：
  - `是`：请求必须携带，缺失返回 `40001`。
  - `否`：可选字段，不传使用默认值或置空。
  - `响应`：仅用于响应，请求不需要。

## 2. 类型约定

| 文档类型 | Java 类型 | JSON 表现 | 说明 |
| --- | --- | --- | --- |
| `string` | String | 字符串 | 普通文本 |
| `long` | Long | 数字 | 主键、外键 ID |
| `int` | Integer | 数字 | 计数、时长 |
| `decimal` | BigDecimal | 数字 | 营养值、体重、金额类，保留精度 |
| `boolean` | Boolean | true/false | 状态标记 |
| `date` | LocalDate | `yyyy-MM-dd` | 日期 |
| `datetime` | LocalDateTime | `yyyy-MM-dd'T'HH:mm:ss` | 时间戳 |
| `enum` | String | 枚举字符串 | 见第 4 章枚举定义 |
| `array<T>` | List\<T> | 数组 | 列表对象 |
| `object` | 自定义 DTO | 对象 | 嵌套结构 |
| `file` | MultipartFile | multipart | 文件上传字段 |

## 3. 通用 DTO

### 3.1 统一响应体 ApiResponse

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `code` | int | 响应 | 业务码，0 表示成功 |
| `message` | string | 响应 | 提示信息 |
| `data` | object | 响应 | 业务数据，失败时可为错误详情 |
| `requestId` | string | 响应 | 请求追踪 ID |
| `timestamp` | datetime | 响应 | 服务端响应时间 |

### 3.2 分页请求 PageQuery

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | int | 否 | 页码，默认 1 |
| `size` | int | 否 | 每页数量，默认 10，最大 100 |
| `sort` | string | 否 | 排序字段与方向，默认 `createdAt,desc` |

### 3.3 分页响应 PageResult\<T>

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `records` | array\<T> | 响应 | 当前页数据列表 |
| `page` | int | 响应 | 当前页码 |
| `size` | int | 响应 | 每页数量 |
| `total` | long | 响应 | 总记录数 |
| `pages` | int | 响应 | 总页数 |

### 3.4 营养值对象 NutritionVO

被菜谱、膳食记录、计划、营养分析等多处复用。

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `calorie` | decimal | 响应 | 热量 kcal |
| `protein` | decimal | 响应 | 蛋白质 g |
| `fat` | decimal | 响应 | 脂肪 g |
| `carbohydrate` | decimal | 响应 | 碳水 g |
| `sugar` | decimal | 响应 | 糖 g |
| `sodium` | decimal | 响应 | 钠 mg |

### 3.5 文件信息对象 FileVO

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `fileId` | long | 响应 | 文件 ID |
| `originalName` | string | 响应 | 原始文件名 |
| `contentType` | string | 响应 | MIME 类型 |
| `size` | long | 响应 | 文件大小（字节） |
| `url` | string | 响应 | 访问地址 |
| `bizType` | enum | 响应 | 业务类型 |

### 3.6 用户简要对象 UserBriefVO

被关注列表、点赞用户、评论作者等复用。

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 响应 | 用户 ID |
| `username` | string | 响应 | 用户名 |
| `nickname` | string | 响应 | 昵称 |
| `avatarUrl` | string | 响应 | 头像地址 |
| `followed` | boolean | 响应 | 当前登录用户是否已关注 |

## 4. 枚举定义

| 枚举名 | 取值 | 说明 |
| --- | --- | --- |
| `Role` | `USER`, `ADMIN` | 用户角色 |
| `UserStatus` | `ENABLED`, `DISABLED`, `DELETED` | 账号状态 |
| `Gender` | `MALE`, `FEMALE`, `UNKNOWN` | 性别 |
| `ActivityLevel` | `SEDENTARY`, `LIGHT`, `MODERATE`, `ACTIVE`, `VERY_ACTIVE` | 活动水平 |
| `HealthGoal` | `FAT_LOSS`, `MUSCLE_GAIN`, `SUGAR_CONTROL`, `LOW_SODIUM`, `MAINTAIN` | 健康目标 |
| `MealType` | `BREAKFAST`, `LUNCH`, `DINNER`, `SNACK` | 餐次 |
| `IngredientCategory` | `MEAT`, `VEGETABLE`, `FRUIT`, `STAPLE`, `DAIRY`, `BEAN`, `SEAFOOD`, `OTHER` | 食材分类 |
| `RecipeStatus` | `DRAFT`, `PENDING`, `ONLINE`, `OFFLINE` | 菜谱状态 |
| `RecordSourceType` | `MANUAL`, `RECIPE`, `PLAN`, `RECOGNITION` | 膳食记录来源 |
| `RecognitionStatus` | `PENDING`, `SUCCESS`, `FAILED`, `CONFIRMED` | 识别任务状态 |
| `PostStatus` | `PENDING`, `ONLINE`, `OFFLINE`, `REJECTED` | 帖子状态 |
| `PostSourceType` | `MANUAL`, `RECIPE`, `MEAL_RECORD`, `AI_RECIPE`, `RECOGNITION` | 帖子来源 |
| `NotificationType` | `LIKE`, `COMMENT`, `FAVORITE`, `FOLLOW`, `SYSTEM` | 通知类型 |
| `ReportReasonType` | `SPAM`, `PORN`, `ABUSE`, `FALSE_INFO`, `OTHER` | 举报原因 |
| `ReviewStatus` | `APPROVED`, `REJECTED` | 审核结果 |
| `FoodType` | `INGREDIENT`, `RECIPE` | 记录食物类型 |
| `AiScene` | `CHAT`, `DIAGNOSIS`, `RECIPE_GENERATE`, `EXPLAIN` | AI 调用场景 |

## 5. 认证与账号模块 DTO

### 5.1 用户注册 `POST /api/v1/auth/register`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `username` | string | 是 | 用户名，4-50 字符，唯一 |
| `email` | string | 是 | 邮箱，唯一 |
| `password` | string | 是 | 密码，8-32 字符 |
| `confirmPassword` | string | 是 | 确认密码，需与 `password` 一致 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 响应 | 新用户 ID |
| `username` | string | 响应 | 用户名 |
| `email` | string | 响应 | 邮箱 |

### 5.2 用户登录 `POST /api/v1/auth/login`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `account` | string | 是 | 用户名或邮箱 |
| `password` | string | 是 | 密码 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `accessToken` | string | 响应 | 访问令牌 |
| `refreshToken` | string | 响应 | 刷新令牌 |
| `expiresIn` | int | 响应 | 访问令牌有效期（秒） |
| `user` | object | 响应 | 登录用户信息，见下 |

`user` 对象：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 用户 ID |
| `username` | string | 响应 | 用户名 |
| `nickname` | string | 响应 | 昵称 |
| `avatarUrl` | string | 响应 | 头像地址 |
| `roles` | array\<enum> | 响应 | 角色列表 |

### 5.3 刷新 Token `POST /api/v1/auth/refresh-token`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `refreshToken` | string | 是 | 刷新令牌 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `accessToken` | string | 响应 | 新访问令牌 |
| `refreshToken` | string | 响应 | 新刷新令牌 |
| `expiresIn` | int | 响应 | 有效期（秒） |

### 5.4 退出登录 `POST /api/v1/auth/logout`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `refreshToken` | string | 否 | 需失效的刷新令牌，不传则失效当前会话 |

Response：无 `data`。

### 5.5 获取登录用户信息 `GET /api/v1/auth/me`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 用户 ID |
| `username` | string | 响应 | 用户名 |
| `email` | string | 响应 | 邮箱 |
| `nickname` | string | 响应 | 昵称 |
| `avatarUrl` | string | 响应 | 头像地址 |
| `roles` | array\<enum> | 响应 | 角色列表 |
| `status` | enum | 响应 | 账号状态 |

### 5.6 修改密码 `PUT /api/v1/auth/password`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `oldPassword` | string | 是 | 原密码 |
| `newPassword` | string | 是 | 新密码，8-32 字符 |
| `confirmPassword` | string | 是 | 确认新密码 |

Response：无 `data`。

### 5.7 重置密码申请 `POST /api/v1/auth/password/reset-request`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `email` | string | 是 | 注册邮箱 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `resetTokenExpireAt` | datetime | 响应 | 重置令牌过期时间 |

### 5.8 重置密码确认 `POST /api/v1/auth/password/reset-confirm`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `resetToken` | string | 是 | 重置令牌 |
| `newPassword` | string | 是 | 新密码 |

Response：无 `data`。

### 5.9 注销账号 `DELETE /api/v1/auth/account`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `password` | string | 是 | 当前密码二次确认 |

Response：无 `data`。

### 5.10 查询账号安全信息 `GET /api/v1/auth/security`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `lastLoginAt` | datetime | 响应 | 最近登录时间 |
| `passwordUpdatedAt` | datetime | 响应 | 密码更新时间 |
| `status` | enum | 响应 | 账号状态 |
| `email` | string | 响应 | 脱敏邮箱 |

## 6. 用户资料与偏好模块 DTO

### 6.1 查询个人资料 `GET /api/v1/users/me/profile`

Request：无。

Response（ProfileVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 响应 | 用户 ID |
| `nickname` | string | 响应 | 昵称 |
| `avatarUrl` | string | 响应 | 头像地址 |
| `bio` | string | 响应 | 个人简介 |
| `gender` | enum | 响应 | 性别 |
| `birthday` | date | 响应 | 生日 |

### 6.2 修改个人资料 `PUT /api/v1/users/me/profile`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `nickname` | string | 否 | 昵称，2-30 字符 |
| `bio` | string | 否 | 简介，最多 200 字符 |
| `gender` | enum | 否 | 性别 |
| `birthday` | date | 否 | 生日 |

Response：同 6.1 ProfileVO。

### 6.3 上传用户头像 `POST /api/v1/users/me/avatar`

Request（multipart/form-data）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `file` | file | 是 | 头像图片，jpg/png/webp，≤5MB |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `fileId` | long | 响应 | 文件 ID |
| `url` | string | 响应 | 头像访问地址 |

### 6.4 查询用户主页 `GET /api/v1/users/{userId}/homepage`

Request（路径参数）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 是 | 目标用户 ID |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 响应 | 用户 ID |
| `nickname` | string | 响应 | 昵称 |
| `avatarUrl` | string | 响应 | 头像地址 |
| `bio` | string | 响应 | 简介 |
| `postCount` | int | 响应 | 帖子数 |
| `followerCount` | int | 响应 | 粉丝数 |
| `followingCount` | int | 响应 | 关注数 |
| `followed` | boolean | 响应 | 是否已关注 |
| `recentPosts` | array\<object> | 响应 | 最近帖子简要列表 |

### 6.5 查询我的偏好 `GET /api/v1/users/me/preferences`

Request：无。

Response（PreferenceVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `tastePreferences` | array\<string> | 响应 | 口味偏好 |
| `dietHabits` | array\<string> | 响应 | 饮食习惯 |
| `favoriteIngredients` | array\<string> | 响应 | 喜欢的食材 |
| `dislikedIngredients` | array\<string> | 响应 | 不喜欢的食材 |
| `recommendationPriority` | string | 响应 | 推荐优先级，如 BALANCED |

### 6.6 修改我的偏好 `PUT /api/v1/users/me/preferences`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `tastePreferences` | array\<string> | 否 | 口味偏好 |
| `dietHabits` | array\<string> | 否 | 饮食习惯 |
| `favoriteIngredients` | array\<string> | 否 | 喜欢的食材 |
| `dislikedIngredients` | array\<string> | 否 | 不喜欢的食材 |
| `recommendationPriority` | string | 否 | 推荐优先级 |

Response：同 6.5 PreferenceVO。

### 6.7 查询我的数据概览 `GET /api/v1/users/me/overview`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `profileCompleteness` | int | 响应 | 档案完整度百分比 |
| `todayCalorie` | decimal | 响应 | 今日已摄入热量 |
| `todayCalorieTarget` | decimal | 响应 | 今日目标热量 |
| `latestWeightKg` | decimal | 响应 | 最近体重 |
| `postCount` | int | 响应 | 帖子数 |
| `mealRecordCount` | int | 响应 | 膳食记录数 |

### 6.8 查询用户公开信息 `GET /api/v1/users/{userId}/public-profile`

Request：路径参数 `userId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 响应 | 用户 ID |
| `nickname` | string | 响应 | 昵称 |
| `avatarUrl` | string | 响应 | 头像地址 |
| `bio` | string | 响应 | 简介 |

### 6.9 关注用户 `POST /api/v1/users/{userId}/follow`

Request：路径参数 `userId`（long，是）。Response：无 `data`。

### 6.10 取消关注用户 `DELETE /api/v1/users/{userId}/follow`

Request：路径参数 `userId`（long，是）。Response：无 `data`。

### 6.11 查询我的关注列表 `GET /api/v1/users/me/following`

Request：分页参数（PageQuery）。Response：`PageResult<UserBriefVO>`，见 3.6。

### 6.12 查询我的粉丝列表 `GET /api/v1/users/me/followers`

Request：分页参数（PageQuery）。Response：`PageResult<UserBriefVO>`，见 3.6。

## 7. 健康档案模块 DTO

### 7.1 创建健康档案 `POST /api/v1/health-profiles`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `gender` | enum | 是 | 性别 |
| `birthday` | date | 是 | 生日 |
| `heightCm` | decimal | 是 | 身高 cm |
| `weightKg` | decimal | 是 | 当前体重 kg |
| `targetWeightKg` | decimal | 否 | 目标体重 kg |
| `activityLevel` | enum | 是 | 活动水平 |
| `dailyCalorieTarget` | int | 否 | 每日目标热量，不传由系统估算 |

Response：HealthProfileVO，见 7.2。

### 7.2 查询我的健康档案 `GET /api/v1/health-profiles/me`

Request：无。

Response（HealthProfileVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 档案 ID |
| `userId` | long | 响应 | 用户 ID |
| `gender` | enum | 响应 | 性别 |
| `birthday` | date | 响应 | 生日 |
| `age` | int | 响应 | 计算所得年龄 |
| `heightCm` | decimal | 响应 | 身高 |
| `weightKg` | decimal | 响应 | 当前体重 |
| `targetWeightKg` | decimal | 响应 | 目标体重 |
| `activityLevel` | enum | 响应 | 活动水平 |
| `bmi` | decimal | 响应 | BMI |
| `bmiLevel` | string | 响应 | BMI 分级 |
| `dailyCalorieTarget` | int | 响应 | 每日目标热量 |
| `goals` | array\<enum> | 响应 | 健康目标 |
| `allergens` | array\<string> | 响应 | 过敏源编码 |
| `chronicDiseases` | array\<string> | 响应 | 慢性病编码 |
| `dietRestrictions` | array\<string> | 响应 | 饮食禁忌编码 |
| `updatedAt` | datetime | 响应 | 更新时间 |

### 7.3 修改健康档案 `PUT /api/v1/health-profiles/me`

Request：字段同 7.1，均为可选（否），至少传一个。Response：HealthProfileVO。

### 7.4 重新计算 BMI `POST /api/v1/health-profiles/me/bmi/recalculate`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `bmi` | decimal | 响应 | BMI 数值 |
| `bmiLevel` | string | 响应 | BMI 分级 |

### 7.5 查询 BMI 信息 `GET /api/v1/health-profiles/me/bmi`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `bmi` | decimal | 响应 | BMI 数值 |
| `bmiLevel` | string | 响应 | 分级（偏瘦/正常/超重/肥胖） |
| `healthyWeightMin` | decimal | 响应 | 健康体重下限 |
| `healthyWeightMax` | decimal | 响应 | 健康体重上限 |

### 7.6 设置健康目标 `PUT /api/v1/health-profiles/me/goals`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `goals` | array\<enum> | 是 | 健康目标列表 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `goals` | array\<enum> | 响应 | 当前健康目标 |

### 7.7 查询健康目标 `GET /api/v1/health-profiles/me/goals`

Request：无。Response：同 7.6 Response。

### 7.8 设置过敏源 `PUT /api/v1/health-profiles/me/allergens`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `allergenCodes` | array\<string> | 是 | 过敏源编码列表 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `allergens` | array\<object> | 响应 | 过敏源项，含 `code`、`name` |

### 7.9 查询过敏源 `GET /api/v1/health-profiles/me/allergens`

Request：无。Response：同 7.8 Response。

### 7.10 设置慢性病 `PUT /api/v1/health-profiles/me/chronic-diseases`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `diseaseCodes` | array\<string> | 是 | 慢性病编码列表 |

Response：`diseases` 数组，含 `code`、`name`。

### 7.11 查询慢性病 `GET /api/v1/health-profiles/me/chronic-diseases`

Request：无。Response：同 7.10 Response。

### 7.12 设置饮食禁忌 `PUT /api/v1/health-profiles/me/diet-restrictions`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `restrictionCodes` | array\<string> | 是 | 饮食禁忌编码列表 |

Response：`restrictions` 数组，含 `code`、`name`。

### 7.13 查询饮食禁忌 `GET /api/v1/health-profiles/me/diet-restrictions`

Request：无。Response：同 7.12 Response。

### 7.14 查询健康档案摘要 `GET /api/v1/health-profiles/me/summary`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `age` | int | 响应 | 年龄 |
| `gender` | enum | 响应 | 性别 |
| `bmi` | decimal | 响应 | BMI |
| `dailyCalorieTarget` | int | 响应 | 每日目标热量 |
| `goals` | array\<enum> | 响应 | 健康目标 |
| `allergens` | array\<string> | 响应 | 过敏源 |
| `chronicDiseases` | array\<string> | 响应 | 慢性病 |
| `dietRestrictions` | array\<string> | 响应 | 饮食禁忌 |

### 7.15 生成健康风险提示 `GET /api/v1/health-profiles/me/risk-warnings`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `warnings` | array\<object> | 响应 | 风险提示列表 |

`warnings` 项：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `level` | string | 响应 | 风险等级（INFO/WARN/HIGH） |
| `type` | string | 响应 | 风险类型 |
| `message` | string | 响应 | 提示内容 |

## 8. 食材库模块 DTO

### 8.1 新增食材 `POST /api/v1/ingredients`

Request（IngredientCreateDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 食材名称，唯一 |
| `category` | enum | 是 | 食材分类 |
| `unit` | string | 是 | 默认单位，如 g、ml |
| `calorie` | decimal | 是 | 每 100g 热量 kcal |
| `protein` | decimal | 是 | 蛋白质 g |
| `fat` | decimal | 是 | 脂肪 g |
| `carbohydrate` | decimal | 是 | 碳水 g |
| `sugar` | decimal | 否 | 糖 g |
| `sodium` | decimal | 否 | 钠 mg |
| `description` | string | 否 | 描述 |

Response：IngredientVO，见 8.4。

### 8.2 修改食材 `PUT /api/v1/ingredients/{ingredientId}`

Request：路径参数 `ingredientId`（long，是）+ 8.1 字段（均可选）。Response：IngredientVO。

### 8.3 删除食材 `DELETE /api/v1/ingredients/{ingredientId}`

Request：路径参数 `ingredientId`（long，是）。Response：无 `data`。

### 8.4 查询食材详情 `GET /api/v1/ingredients/{ingredientId}`

Request：路径参数 `ingredientId`（long，是）。

Response（IngredientVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 食材 ID |
| `name` | string | 响应 | 名称 |
| `category` | enum | 响应 | 分类 |
| `unit` | string | 响应 | 默认单位 |
| `calorie` | decimal | 响应 | 热量 |
| `protein` | decimal | 响应 | 蛋白质 |
| `fat` | decimal | 响应 | 脂肪 |
| `carbohydrate` | decimal | 响应 | 碳水 |
| `sugar` | decimal | 响应 | 糖 |
| `sodium` | decimal | 响应 | 钠 |
| `description` | string | 响应 | 描述 |
| `status` | enum | 响应 | 状态（ENABLED/DISABLED） |

### 8.5 分页查询食材 `GET /api/v1/ingredients`

Request（查询参数）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 名称关键词 |
| `category` | enum | 否 | 食材分类 |
| `minCalorie` | decimal | 否 | 最小热量 |
| `maxCalorie` | decimal | 否 | 最大热量 |
| `page` | int | 否 | 页码 |
| `size` | int | 否 | 每页数量 |
| `sort` | string | 否 | 排序 |

Response：`PageResult<IngredientVO>`。

### 8.6 搜索食材 `GET /api/v1/ingredients/search`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 是 | 搜索关键词 |
| `limit` | int | 否 | 返回数量，默认 10 |

Response：`array<IngredientBriefVO>`（含 `id`、`name`、`category`、`calorie`）。

### 8.7 查询食材营养成分 `GET /api/v1/ingredients/{ingredientId}/nutrition`

Request：路径参数 `ingredientId`（long，是）。Response：NutritionVO（见 3.4），按每 100g。

### 8.8 高蛋白/低热量/低脂/低糖食材

接口：`GET /api/v1/ingredients/high-protein`、`/low-calorie`、`/low-fat`、`/low-sugar`

Request：分页参数（PageQuery）。Response：`PageResult<IngredientVO>`。

### 8.9 查询适合用户的食材 `GET /api/v1/ingredients/suitable-for-me`

Request：分页参数（PageQuery）；用户健康档案由后端自动注入。Response：`PageResult<IngredientVO>`。

### 8.10 查询不适合用户的食材 `GET /api/v1/ingredients/unsuitable-for-me`

Request：分页参数。

Response：`PageResult<UnsuitableIngredientVO>`，列表项为：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `ingredient` | object | 响应 | IngredientVO |
| `reason` | string | 响应 | 不适合原因（过敏源/慢性病/禁忌） |

### 8.11 批量导入食材 `POST /api/v1/ingredients/import`

Request（multipart/form-data）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `file` | file | 是 | Excel 或 JSON 文件 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `successCount` | int | 响应 | 成功导入数 |
| `failCount` | int | 响应 | 失败数 |
| `errors` | array\<string> | 响应 | 失败行说明 |

### 8.12 导出食材库 `GET /api/v1/ingredients/export`

Request：查询条件同 8.5（可选）。Response：二进制文件流（`Content-Disposition: attachment`）。

### 8.13 启用或停用食材 `PATCH /api/v1/ingredients/{ingredientId}/status`

Request：路径参数 `ingredientId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `status` | enum | 是 | ENABLED 或 DISABLED |

Response：无 `data`。

## 9. 菜谱库模块 DTO

### 9.1 新增菜谱 `POST /api/v1/recipes`

Request（RecipeCreateDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 菜谱名称 |
| `description` | string | 否 | 简介，≤1000 字符 |
| `category` | enum | 是 | 餐次分类（MealType） |
| `coverFileId` | long | 否 | 封面文件 ID |
| `difficulty` | string | 否 | 难度（EASY/MEDIUM/HARD） |
| `cookMinutes` | int | 否 | 烹饪时长（分钟） |
| `servings` | int | 否 | 份数，默认 1 |
| `tagCodes` | array\<string> | 否 | 标签编码 |
| `steps` | array\<object> | 否 | 制作步骤，见 RecipeStepDTO |

RecipeStepDTO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `stepNo` | int | 是 | 步骤序号 |
| `content` | string | 是 | 步骤内容 |
| `imageFileId` | long | 否 | 步骤配图 |

Response：RecipeVO，见 9.4。

### 9.2 修改菜谱 `PUT /api/v1/recipes/{recipeId}`

Request：路径参数 `recipeId`（long，是）+ 9.1 字段（可选）。Response：RecipeVO。

### 9.3 删除菜谱 `DELETE /api/v1/recipes/{recipeId}`

Request：路径参数 `recipeId`（long，是）。Response：无 `data`。

### 9.4 查询菜谱详情 `GET /api/v1/recipes/{recipeId}`

Request：路径参数 `recipeId`（long，是）。

Response（RecipeVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 菜谱 ID |
| `name` | string | 响应 | 名称 |
| `description` | string | 响应 | 简介 |
| `category` | enum | 响应 | 分类 |
| `coverUrl` | string | 响应 | 封面地址 |
| `difficulty` | string | 响应 | 难度 |
| `cookMinutes` | int | 响应 | 烹饪时长 |
| `servings` | int | 响应 | 份数 |
| `totalCalorie` | decimal | 响应 | 总热量 |
| `nutrition` | object | 响应 | NutritionVO |
| `ingredients` | array\<object> | 响应 | 食材组成，见 RecipeIngredientVO |
| `steps` | array\<object> | 响应 | 制作步骤 |
| `tags` | array\<string> | 响应 | 标签 |
| `authorId` | long | 响应 | 作者 ID |
| `avgRating` | decimal | 响应 | 平均评分 |
| `favoriteCount` | int | 响应 | 收藏数 |
| `status` | enum | 响应 | 菜谱状态 |

RecipeIngredientVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `ingredientId` | long | 响应 | 食材 ID |
| `name` | string | 响应 | 食材名称 |
| `amount` | decimal | 响应 | 用量 |
| `unit` | string | 响应 | 单位 |

### 9.5 分页查询菜谱 `GET /api/v1/recipes`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 关键词 |
| `category` | enum | 否 | 分类 |
| `tags` | array\<string> | 否 | 标签筛选 |
| `maxCalorie` | decimal | 否 | 最大热量 |
| `page` | int | 否 | 页码 |
| `size` | int | 否 | 每页数量 |
| `sort` | string | 否 | 排序 |

Response：`PageResult<RecipeVO>`。

### 9.6 搜索菜谱 `GET /api/v1/recipes/search`

Request：`keyword`（string，是）、`limit`（int，否）。Response：`array<RecipeBriefVO>`（`id`、`name`、`coverUrl`、`totalCalorie`、`category`）。

### 9.7 分类菜谱查询

接口：`GET /api/v1/recipes/breakfast`、`/lunch`、`/dinner`、`/snack`

Request：分页参数。Response：`PageResult<RecipeVO>`。

### 9.8 绑定菜谱食材 `POST /api/v1/recipes/{recipeId}/ingredients`

Request：路径参数 `recipeId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `items` | array\<object> | 是 | 食材用量列表 |

`items` 项：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `ingredientId` | long | 是 | 食材 ID |
| `amount` | decimal | 是 | 用量 |
| `unit` | string | 是 | 单位 |

Response：`array<RecipeIngredientVO>`。

### 9.9 修改菜谱食材 `PUT /api/v1/recipes/{recipeId}/ingredients`

Request：同 9.8（全量覆盖）。Response：`array<RecipeIngredientVO>`。

### 9.10 查询菜谱食材 `GET /api/v1/recipes/{recipeId}/ingredients`

Request：路径参数 `recipeId`（long，是）。Response：`array<RecipeIngredientVO>`。

### 9.11 计算菜谱营养 `POST /api/v1/recipes/{recipeId}/nutrition/calculate`

Request：路径参数 `recipeId`（long，是）。Response：NutritionVO + `totalCalorie`。

### 9.12 查询菜谱营养 `GET /api/v1/recipes/{recipeId}/nutrition`

Request：路径参数 `recipeId`（long，是）。Response：NutritionVO。

### 9.13 查询适合我的菜谱 `GET /api/v1/recipes/suitable-for-me`

Request：分页参数。Response：`PageResult<RecipeVO>`。

### 9.14 查询不适合我的菜谱 `GET /api/v1/recipes/unsuitable-for-me`

Request：分页参数。Response：`PageResult` 列表项含 `recipe`（RecipeVO）、`reason`（string）。

### 9.15 收藏菜谱 `POST /api/v1/recipes/{recipeId}/favorite`

Request：路径参数 `recipeId`（long，是）。Response：无 `data`。

### 9.16 取消收藏菜谱 `DELETE /api/v1/recipes/{recipeId}/favorite`

Request：路径参数 `recipeId`（long，是）。Response：无 `data`。

### 9.17 查询我的收藏菜谱 `GET /api/v1/recipes/favorites/me`

Request：分页参数。Response：`PageResult<RecipeVO>`。

### 9.18 菜谱评分 `POST /api/v1/recipes/{recipeId}/rating`

Request：路径参数 `recipeId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `score` | int | 是 | 评分 1-5 |
| `comment` | string | 否 | 评价文字 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `avgRating` | decimal | 响应 | 更新后平均分 |
| `ratingCount` | int | 响应 | 评分人数 |

## 10. 膳食计划模块 DTO

### 10.1 生成膳食计划（今日/指定日期/一周）

接口：
- `POST /api/v1/meal-plans/today/generate`
- `POST /api/v1/meal-plans/generate`
- `POST /api/v1/meal-plans/week/generate`

Request（MealPlanGenerateDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 条件 | 指定日期接口必填；今日接口忽略 |
| `startDate` | date | 条件 | 一周接口必填，周起始日 |
| `target` | enum | 否 | 健康目标，不传取档案目标 |
| `calorieLimit` | int | 否 | 热量上限 |
| `mealTypes` | array\<enum> | 否 | 需生成的餐次，默认全部 |
| `useAi` | boolean | 否 | 是否使用 AI 生成，默认 false |

Response：单日返回 MealPlanVO；一周返回 `array<MealPlanVO>`。

MealPlanVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `planId` | long | 响应 | 计划 ID |
| `date` | date | 响应 | 计划日期 |
| `target` | enum | 响应 | 健康目标 |
| `totalCalorie` | decimal | 响应 | 全天总热量 |
| `nutrition` | object | 响应 | NutritionVO |
| `meals` | array\<object> | 响应 | 餐次列表，见 MealPlanItemVO |

MealPlanItemVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `mealType` | enum | 响应 | 餐次 |
| `recipeId` | long | 响应 | 菜谱 ID |
| `recipeName` | string | 响应 | 菜谱名称 |
| `coverUrl` | string | 响应 | 封面 |
| `calorie` | decimal | 响应 | 该餐热量 |
| `locked` | boolean | 响应 | 是否锁定 |

### 10.2 查询今日膳食计划 `GET /api/v1/meal-plans/today`

Request：无。Response：MealPlanVO。

### 10.3 查询指定日期膳食计划 `GET /api/v1/meal-plans/date/{date}`

Request：路径参数 `date`（date，是）。Response：MealPlanVO。

### 10.4 查询周膳食计划 `GET /api/v1/meal-plans/week`

Request：`startDate`（date，是）。Response：`array<MealPlanVO>`。

### 10.5 修改膳食计划 `PUT /api/v1/meal-plans/{planId}`

Request：路径参数 `planId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `meals` | array\<object> | 是 | 餐次配置，含 `mealType`、`recipeId` |

Response：MealPlanVO。

### 10.6 删除膳食计划 `DELETE /api/v1/meal-plans/{planId}`

Request：路径参数 `planId`（long，是）。Response：无 `data`。

### 10.7 替换某一餐 `PUT /api/v1/meal-plans/{planId}/meals/{mealType}/replace`

Request：路径参数 `planId`（long，是）、`mealType`（enum，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recipeId` | long | 是 | 替换的菜谱 ID |

Response：MealPlanVO。

### 10.8 重新生成某一餐 `POST /api/v1/meal-plans/{planId}/meals/{mealType}/regenerate`

Request：路径参数同上 +

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `excludeRecipeIds` | array\<long> | 否 | 排除的菜谱 ID |

Response：MealPlanItemVO（新生成餐次）。

### 10.9 锁定/解锁某一餐

接口：`PATCH /api/v1/meal-plans/{planId}/meals/{mealType}/lock`、`/unlock`

Request：路径参数 `planId`（long，是）、`mealType`（enum，是）。Response：无 `data`。

### 10.10 查询计划生成原因 `GET /api/v1/meal-plans/{planId}/reason`

Request：路径参数 `planId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `summary` | string | 响应 | 总体说明 |
| `reasons` | array\<object> | 响应 | 按餐次的推荐理由，含 `mealType`、`reason` |

### 10.11 查询计划营养分析 `GET /api/v1/meal-plans/{planId}/nutrition-analysis`

Request：路径参数 `planId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `nutrition` | object | 响应 | NutritionVO |
| `calorieTarget` | decimal | 响应 | 目标热量 |
| `compliant` | boolean | 响应 | 是否达标 |
| `gaps` | array\<string> | 响应 | 缺口项 |
| `excess` | array\<string> | 响应 | 超标项 |

### 10.12 查询计划采购清单 `GET /api/v1/meal-plans/{planId}/shopping-list`

Request：路径参数 `planId`（long，是）。Response：`array<ShoppingItemVO>`（见 18.x），含 `ingredientId`、`name`、`amount`、`unit`。

### 10.13 应用计划为膳食记录 `POST /api/v1/meal-plans/{planId}/apply-to-record`

Request：路径参数 `planId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 否 | 记录日期，默认计划日期 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recordIds` | array\<long> | 响应 | 生成的膳食记录 ID 列表 |

## 11. 膳食记录模块 DTO

### 11.1 新增膳食记录 `POST /api/v1/meal-records`

Request（MealRecordCreateDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recordDate` | date | 是 | 记录日期 |
| `mealType` | enum | 是 | 餐次 |
| `items` | array\<object> | 是 | 食物明细，见 MealRecordItemDTO |
| `remark` | string | 否 | 备注 |
| `imageFileIds` | array\<long> | 否 | 记录配图 |

MealRecordItemDTO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `foodType` | enum | 是 | INGREDIENT 或 RECIPE |
| `foodId` | long | 是 | 食材或菜谱 ID |
| `name` | string | 否 | 名称，便于展示 |
| `amount` | decimal | 是 | 用量 |
| `unit` | string | 是 | 单位 |

Response：MealRecordVO，见 11.4。

### 11.2 修改膳食记录 `PUT /api/v1/meal-records/{recordId}`

Request：路径参数 `recordId`（long，是）+ 11.1 字段（可选）。Response：MealRecordVO。

### 11.3 删除膳食记录 `DELETE /api/v1/meal-records/{recordId}`

Request：路径参数 `recordId`（long，是）。Response：无 `data`。

### 11.4 查询记录详情 `GET /api/v1/meal-records/{recordId}`

Request：路径参数 `recordId`（long，是）。

Response（MealRecordVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 记录 ID |
| `userId` | long | 响应 | 用户 ID |
| `recordDate` | date | 响应 | 记录日期 |
| `mealType` | enum | 响应 | 餐次 |
| `sourceType` | enum | 响应 | 来源 |
| `items` | array\<object> | 响应 | 食物明细（含营养） |
| `nutrition` | object | 响应 | NutritionVO 汇总 |
| `totalCalorie` | decimal | 响应 | 总热量 |
| `abnormalType` | string | 响应 | 异常标记，可空 |
| `remark` | string | 响应 | 备注 |
| `imageUrls` | array\<string> | 响应 | 配图地址 |
| `createdAt` | datetime | 响应 | 创建时间 |

### 11.5 按日期查询记录 `GET /api/v1/meal-records/date/{date}`

Request：路径参数 `date`（date，是）。Response：`array<MealRecordVO>`。

### 11.6 分页查询我的记录 `GET /api/v1/meal-records`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `startDate` | date | 否 | 起始日期 |
| `endDate` | date | 否 | 结束日期 |
| `mealType` | enum | 否 | 餐次筛选 |
| `page` | int | 否 | 页码 |
| `size` | int | 否 | 每页数量 |

Response：`PageResult<MealRecordVO>`。

### 11.7 快速记录餐次

接口：`POST /api/v1/meal-records/breakfast`、`/lunch`、`/dinner`、`/snack`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recordDate` | date | 否 | 默认今天 |
| `items` | array\<object> | 是 | 食物明细（MealRecordItemDTO） |
| `remark` | string | 否 | 备注 |

Response：MealRecordVO。

### 11.8 从菜谱生成记录 `POST /api/v1/meal-records/from-recipe/{recipeId}`

Request：路径参数 `recipeId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recordDate` | date | 否 | 默认今天 |
| `mealType` | enum | 是 | 餐次 |
| `servingRatio` | decimal | 否 | 份数比例，默认 1 |

Response：MealRecordVO。

### 11.9 从膳食计划生成记录 `POST /api/v1/meal-records/from-plan/{planId}`

Request：路径参数 `planId`（long，是）+ `date`（date，否）。Response：`array<MealRecordVO>`。

### 11.10 从图片识别结果生成记录 `POST /api/v1/meal-records/from-food-recognition/{taskId}`

Request：路径参数 `taskId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `mealType` | enum | 是 | 餐次 |
| `recordDate` | date | 否 | 默认今天 |
| `amount` | decimal | 否 | 食用量 g |

Response：MealRecordVO。

### 11.11 查询每日摄入汇总 `GET /api/v1/meal-records/daily-summary`

Request：`date`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 响应 | 日期 |
| `nutrition` | object | 响应 | NutritionVO 汇总 |
| `calorieTarget` | decimal | 响应 | 目标热量 |
| `mealBreakdown` | array\<object> | 响应 | 各餐次热量占比，含 `mealType`、`calorie` |

### 11.12 查询历史摄入统计 `GET /api/v1/meal-records/history-statistics`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `points` | array\<object> | 响应 | 每日点位，含 `date`、`calorie`、`protein` |
| `avgCalorie` | decimal | 响应 | 平均热量 |

### 11.13 标记异常饮食 `PATCH /api/v1/meal-records/{recordId}/abnormal`

Request：路径参数 `recordId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `abnormalType` | string | 是 | 异常类型（OVEREAT/SKIP/NIGHT_SNACK） |
| `remark` | string | 否 | 备注 |

Response：无 `data`。

### 11.14 分享膳食记录到社区 `POST /api/v1/meal-records/{recordId}/share-to-community`

Request：路径参数 `recordId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `title` | string | 是 | 帖子标题 |
| `content` | string | 否 | 帖子正文 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `postId` | long | 响应 | 生成的帖子 ID |

## 12. 营养分析模块 DTO

### 12.1 分析单个菜谱营养 `POST /api/v1/nutrition/analyze/recipe/{recipeId}`

Request：路径参数 `recipeId`（long，是）。Response：NutritionAnalysisVO，见下。

NutritionAnalysisVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `calorie` | decimal | 响应 | 热量 |
| `protein` | decimal | 响应 | 蛋白质 |
| `fat` | decimal | 响应 | 脂肪 |
| `carbohydrate` | decimal | 响应 | 碳水 |
| `sugar` | decimal | 响应 | 糖 |
| `sodium` | decimal | 响应 | 钠 |
| `macroRatio` | object | 响应 | 三大营养素占比，见 MacroRatioVO |
| `score` | int | 响应 | 营养评分 0-100 |
| `warnings` | array\<string> | 响应 | 提醒列表 |
| `suggestions` | array\<string> | 响应 | 建议列表 |

MacroRatioVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `proteinPercent` | decimal | 响应 | 蛋白质占比 % |
| `fatPercent` | decimal | 响应 | 脂肪占比 % |
| `carbohydratePercent` | decimal | 响应 | 碳水占比 % |

### 12.2 分析一餐营养 `POST /api/v1/nutrition/analyze/meal`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `items` | array\<object> | 是 | 食物明细（MealRecordItemDTO） |

Response：NutritionAnalysisVO。

### 12.3 分析一日营养 `POST /api/v1/nutrition/analyze/day`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 否 | 指定日期，与 `items` 二选一 |
| `items` | array\<object> | 否 | 自定义食物明细 |

Response：NutritionAnalysisVO。

### 12.4 分析膳食计划营养 `POST /api/v1/nutrition/analyze/meal-plan/{planId}`

Request：路径参数 `planId`（long，是）。Response：NutritionAnalysisVO。

### 12.5 分析膳食记录营养 `POST /api/v1/nutrition/analyze/meal-record/{recordId}`

Request：路径参数 `recordId`（long，是）。Response：NutritionAnalysisVO。

### 12.6 查询每日/周/月营养摄入

接口：`GET /api/v1/nutrition/daily`、`/weekly`、`/monthly`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 条件 | daily 必填 |
| `startDate` | date | 条件 | weekly 必填 |
| `month` | string | 条件 | monthly 必填，格式 `yyyy-MM` |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `points` | array\<object> | 响应 | 时间点营养，含 `date`、NutritionVO 字段 |
| `average` | object | 响应 | 平均营养 NutritionVO |

### 12.7 膳食指南达标分析 `POST /api/v1/nutrition/standard/compare`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 否 | 指定日期 |
| `nutrition` | object | 否 | 自定义营养值（NutritionVO） |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `items` | array\<object> | 响应 | 各营养素对比 |

`items` 项：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `nutrient` | string | 响应 | 营养素名称 |
| `actual` | decimal | 响应 | 实际摄入 |
| `recommended` | decimal | 响应 | 推荐摄入 |
| `status` | string | 响应 | LOW/OK/HIGH |

### 12.8 查询营养缺口 `GET /api/v1/nutrition/gaps`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `gaps` | array\<object> | 响应 | 缺口项，含 `nutrient`、`gapAmount`、`unit` |

### 12.9 查询营养超标项 `GET /api/v1/nutrition/excess`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `excess` | array\<object> | 响应 | 超标项，含 `nutrient`、`excessAmount`、`unit` |

### 12.10 查询营养评分 `GET /api/v1/nutrition/score`

Request：`date`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `score` | int | 响应 | 评分 0-100 |
| `level` | string | 响应 | 评级（优/良/中/差） |
| `dimensions` | array\<object> | 响应 | 维度得分，含 `name`、`score` |

### 12.11 生成基础营养建议 `GET /api/v1/nutrition/suggestions`

Request：`date`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `suggestions` | array\<string> | 响应 | 规则建议列表 |

### 12.12 重新计算营养统计 `POST /api/v1/nutrition/recalculate`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `startDate` | date | 是 | 起始日期 |
| `endDate` | date | 是 | 结束日期 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recalculatedDays` | int | 响应 | 重新计算的天数 |

## 13. 体重趋势模块 DTO

### 13.1 新增体重记录 `POST /api/v1/weight-records`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recordDate` | date | 是 | 记录日期 |
| `weightKg` | decimal | 是 | 体重 kg |
| `remark` | string | 否 | 备注 |

Response：WeightRecordVO，见 13.4。

### 13.2 修改体重记录 `PUT /api/v1/weight-records/{recordId}`

Request：路径参数 `recordId`（long，是）+ `weightKg`（decimal，否）、`remark`（string，否）。Response：WeightRecordVO。

### 13.3 删除体重记录 `DELETE /api/v1/weight-records/{recordId}`

Request：路径参数 `recordId`（long，是）。Response：无 `data`。

### 13.4 查询体重详情 `GET /api/v1/weight-records/{recordId}`

Request：路径参数 `recordId`（long，是）。

Response（WeightRecordVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 记录 ID |
| `recordDate` | date | 响应 | 记录日期 |
| `weightKg` | decimal | 响应 | 体重 |
| `remark` | string | 响应 | 备注 |
| `createdAt` | datetime | 响应 | 创建时间 |

### 13.5 查询体重列表 `GET /api/v1/weight-records`

Request：`startDate`（date，否）、`endDate`（date，否）、分页参数。Response：`PageResult<WeightRecordVO>`。

### 13.6 查询最近体重 `GET /api/v1/weight-records/latest`

Request：无。Response：WeightRecordVO。

### 13.7 查询体重趋势 `GET /api/v1/weight-records/trend`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `points` | array\<object> | 响应 | 折线点，含 `date`、`weightKg` |

### 13.8 查询体重统计 `GET /api/v1/weight-records/statistics`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `avgWeight` | decimal | 响应 | 平均体重 |
| `maxWeight` | decimal | 响应 | 最大体重 |
| `minWeight` | decimal | 响应 | 最小体重 |
| `changeKg` | decimal | 响应 | 区间变化量 |

### 13.9 查询目标体重进度 `GET /api/v1/weight-records/goal-progress`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `startWeight` | decimal | 响应 | 起始体重 |
| `currentWeight` | decimal | 响应 | 当前体重 |
| `targetWeight` | decimal | 响应 | 目标体重 |
| `progressPercent` | decimal | 响应 | 完成百分比 |

### 13.10 生成体重建议 `GET /api/v1/weight-records/suggestions`

Request：无。Response：`suggestions`（array\<string>）。

## 14. 报表统计模块 DTO

### 14.1 生成周/月营养报表

接口：`POST /api/v1/reports/weekly/generate`、`/monthly/generate`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `weekStartDate` | date | 条件 | 周报必填 |
| `month` | string | 条件 | 月报必填，`yyyy-MM` |

Response：ReportVO，见 14.2。

### 14.2 查询周/月报 `GET /api/v1/reports/weekly`、`/monthly`

Request：`weekStartDate`（date，条件）或 `month`（string，条件）。

Response（ReportVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `reportId` | long | 响应 | 报表 ID |
| `reportType` | string | 响应 | WEEKLY/MONTHLY |
| `periodStart` | date | 响应 | 统计起始 |
| `periodEnd` | date | 响应 | 统计结束 |
| `avgCalorie` | decimal | 响应 | 平均热量 |
| `nutrition` | object | 响应 | 平均 NutritionVO |
| `macroRatio` | object | 响应 | MacroRatioVO |
| `complianceRate` | decimal | 响应 | 达标率 % |
| `summary` | string | 响应 | 文字摘要 |

### 14.3 趋势查询

接口：`GET /api/v1/reports/calorie-trend`、`/protein-trend`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `points` | array\<object> | 响应 | 趋势点，含 `date`、`value` |

### 14.4 查询三大营养素占比 `GET /api/v1/reports/macro-ratio`

Request：`startDate`（date，是）、`endDate`（date，是）。Response：MacroRatioVO。

### 14.5 查询饮食达标率 `GET /api/v1/reports/compliance-rate`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `complianceRate` | decimal | 响应 | 达标率 % |
| `compliantDays` | int | 响应 | 达标天数 |
| `totalDays` | int | 响应 | 总天数 |

### 14.6 查询常吃食物排行 `GET /api/v1/reports/top-foods`

Request：`startDate`（date，是）、`endDate`（date，是）、`limit`（int，否）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `foods` | array\<object> | 响应 | 排行项，含 `name`、`count`、`totalCalorie` |

### 14.7 查询健康目标完成度 `GET /api/v1/reports/goal-progress`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `goal` | enum | 响应 | 健康目标 |
| `progressPercent` | decimal | 响应 | 完成度 |
| `description` | string | 响应 | 说明 |

### 14.8 报表导出

接口：`GET /api/v1/reports/weekly/export/pdf`、`/monthly/export/pdf`、`/export/excel`

Request：对应日期参数（`weekStartDate`/`month`/日期范围）。Response：二进制文件流。

### 14.9 生成报表摘要 `GET /api/v1/reports/summary`

Request：`startDate`（date，是）、`endDate`（date，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `summary` | string | 响应 | 自然语言摘要 |

## 15. 食物图片识别模块 DTO

### 15.1 上传食物图片识别 `POST /api/v1/food-recognition/image`

Request（multipart/form-data）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `image` | file | 是 | 食物图片 |
| `topK` | int | 否 | 返回前 K 个结果，默认 3 |

Response：RecognitionTaskVO，见 15.3。

### 15.2 批量上传图片识别 `POST /api/v1/food-recognition/batch`

Request（multipart/form-data）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `images` | array\<file> | 是 | 多张图片 |
| `topK` | int | 否 | 返回前 K 个结果 |

Response：`array<RecognitionTaskVO>`。

### 15.3 查询识别任务详情 `GET /api/v1/food-recognition/tasks/{taskId}`

Request：路径参数 `taskId`（long，是）。

Response（RecognitionTaskVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `taskId` | long | 响应 | 任务 ID |
| `status` | enum | 响应 | 识别状态 |
| `imageUrl` | string | 响应 | 图片地址 |
| `modelName` | string | 响应 | 模型名称 |
| `modelVersion` | string | 响应 | 模型版本 |
| `results` | array\<object> | 响应 | 识别结果，见 RecognitionResultVO |
| `confirmedLabel` | string | 响应 | 用户确认标签，可空 |
| `errorMessage` | string | 响应 | 失败原因，可空 |
| `createdAt` | datetime | 响应 | 创建时间 |

RecognitionResultVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `label` | string | 响应 | 模型标签 |
| `displayName` | string | 响应 | 中文名称 |
| `confidence` | decimal | 响应 | 置信度 0-1 |
| `matchedIngredientId` | long | 响应 | 匹配食材 ID，可空 |

### 15.4 查询我的识别历史 `GET /api/v1/food-recognition/history`

Request：分页参数。Response：`PageResult<RecognitionTaskVO>`。

### 15.5 确认识别结果 `POST /api/v1/food-recognition/tasks/{taskId}/confirm`

Request：路径参数 `taskId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `label` | string | 是 | 确认的食物标签 |
| `ingredientId` | long | 否 | 关联食材 ID |
| `amount` | decimal | 否 | 食用量 g |

Response：RecognitionTaskVO（status 变为 CONFIRMED）。

### 15.6 重新识别图片 `POST /api/v1/food-recognition/tasks/{taskId}/retry`

Request：路径参数 `taskId`（long，是）+ `topK`（int，否）。Response：RecognitionTaskVO。

### 15.7 查询识别营养分析 `GET /api/v1/food-recognition/tasks/{taskId}/nutrition`

Request：路径参数 `taskId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `foodName` | string | 响应 | 食物名称 |
| `amount` | decimal | 响应 | 估算用量 g |
| `nutrition` | object | 响应 | NutritionVO |

### 15.8 识别结果转膳食记录 `POST /api/v1/food-recognition/tasks/{taskId}/to-meal-record`

Request：路径参数 `taskId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `mealType` | enum | 是 | 餐次 |
| `recordDate` | date | 否 | 默认今天 |
| `amount` | decimal | 否 | 食用量 g |

Response：MealRecordVO（见 11.4）。

### 15.9 识别结果生成 AI 建议 `POST /api/v1/food-recognition/tasks/{taskId}/ai-advice`

Request：路径参数 `taskId`（long，是）+ `question`（string，否）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `advice` | string | 响应 | AI 建议文本 |

### 15.10 分享识别结果到社区 `POST /api/v1/food-recognition/tasks/{taskId}/share-to-community`

Request：路径参数 `taskId`（long，是）+ `title`（string，是）、`content`（string，否）。Response：`postId`（long）。

### 15.11 查询模型支持类别 `GET /api/v1/food-recognition/model/labels`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `labels` | array\<object> | 响应 | 类别项，含 `label`、`displayName` |

### 15.12 查询识别模型信息 `GET /api/v1/food-recognition/model/info`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `modelName` | string | 响应 | 模型名称 |
| `modelVersion` | string | 响应 | 版本 |
| `categoryCount` | int | 响应 | 类别数 |
| `accuracy` | decimal | 响应 | 准确率 |

## 16. AI 智能膳食顾问模块 DTO

### 16.1 AI 膳食问答 `POST /api/v1/ai/advisor/chat`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `conversationId` | long | 否 | 会话 ID，空则新建会话 |
| `message` | string | 是 | 用户提问 |
| `useHealthContext` | boolean | 否 | 是否带入健康上下文，默认 true |

Response（AiChatVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `conversationId` | long | 响应 | 会话 ID |
| `messageId` | long | 响应 | 回复消息 ID |
| `reply` | string | 响应 | AI 回复内容 |
| `model` | string | 响应 | 使用模型 |
| `createdAt` | datetime | 响应 | 回复时间 |

### 16.2 AI 流式问答 `POST /api/v1/ai/advisor/chat/stream`

Request：同 16.1。Response：`text/event-stream`，每个 SSE 事件 `data` 为增量文本片段，结束以 `[DONE]` 标记。

### 16.3 查询 AI 会话列表 `GET /api/v1/ai/advisor/conversations`

Request：分页参数。

Response：`PageResult<ConversationVO>`，列表项：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `conversationId` | long | 响应 | 会话 ID |
| `title` | string | 响应 | 会话标题（首问摘要） |
| `lastMessage` | string | 响应 | 最后一条消息摘要 |
| `messageCount` | int | 响应 | 消息数 |
| `updatedAt` | datetime | 响应 | 更新时间 |

### 16.4 查询 AI 会话详情 `GET /api/v1/ai/advisor/conversations/{conversationId}`

Request：路径参数 `conversationId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `conversationId` | long | 响应 | 会话 ID |
| `messages` | array\<object> | 响应 | 消息列表 |

`messages` 项：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `messageId` | long | 响应 | 消息 ID |
| `role` | string | 响应 | user 或 assistant |
| `content` | string | 响应 | 消息内容 |
| `createdAt` | datetime | 响应 | 时间 |

### 16.5 删除 AI 会话 `DELETE /api/v1/ai/advisor/conversations/{conversationId}`

Request：路径参数 `conversationId`（long，是）。Response：无 `data`。

### 16.6 今日/一周营养诊断

接口：`POST /api/v1/ai/advisor/today-diagnosis`、`/weekly-diagnosis`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 条件 | 今日诊断默认今天 |
| `startDate` | date | 条件 | 一周诊断必填 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `diagnosis` | string | 响应 | 诊断文本 |
| `highlights` | array\<string> | 响应 | 关键提示 |

### 16.7 减脂晚餐推荐 `POST /api/v1/ai/advisor/fat-loss-dinner`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `availableIngredients` | array\<string> | 否 | 现有食材 |
| `calorieLimit` | int | 否 | 热量上限 |

Response：`recommendation`（string）、`recipes`（array\<object>，含 `name`、`calorie`）。

### 16.8 食物替代推荐 `POST /api/v1/ai/advisor/food-alternatives`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `foodName` | string | 是 | 待替代食物 |
| `goal` | enum | 否 | 健康目标 |

Response：`alternatives`（array\<object>，含 `name`、`reason`）。

### 16.9 慢性病饮食建议 `POST /api/v1/ai/advisor/chronic-disease-advice`

Request：`diseaseCode`（string，是）。Response：`advice`（string）、`disclaimer`（string）。

### 16.10 饮食风险提醒 `POST /api/v1/ai/advisor/risk-warning`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 否 | 指定日期 |
| `items` | array\<object> | 否 | 自定义食物明细 |

Response：`warnings`（array\<string>）、`riskLevel`（string）。

### 16.11 解释膳食计划 `POST /api/v1/ai/advisor/explain-meal-plan/{planId}`

Request：路径参数 `planId`（long，是）。Response：`explanation`（string）。

### 16.12 解释营养报表 `POST /api/v1/ai/advisor/explain-report`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `reportType` | string | 是 | WEEKLY/MONTHLY |
| `reportId` | long | 是 | 报表 ID |

Response：`explanation`（string）。

### 16.13 查询 AI 可用模型 `GET /api/v1/ai/models`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `models` | array\<object> | 响应 | 模型项，含 `name`、`displayName`、`enabled` |

### 16.14 AI 服务健康检查 `GET /api/v1/ai/health`

Request：无。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `status` | string | 响应 | UP 或 DOWN |
| `latencyMs` | int | 响应 | 探测延迟 |

## 17. AI 智能菜谱模块 DTO

### 17.1 AI 生成菜谱（通用/减脂/增肌/控糖）

接口：`POST /api/v1/ai/recipes/generate`、`/generate/fat-loss`、`/generate/muscle-gain`、`/generate/sugar-control`

Request（AiRecipeGenerateDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `ingredients` | array\<string> | 否 | 现有食材 |
| `taste` | string | 否 | 口味偏好 |
| `target` | enum | 否 | 健康目标，分类接口可省略 |
| `calorieLimit` | int | 否 | 热量上限 |
| `proteinTarget` | decimal | 否 | 蛋白质目标，增肌用 |
| `servings` | int | 否 | 份数，默认 1 |

Response：AiRecipeVO，见 17.6。

### 17.2 AI 生成一周菜谱 `POST /api/v1/ai/recipes/week-plan`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `startDate` | date | 是 | 周起始日 |
| `goal` | enum | 否 | 健康目标 |
| `calorieTarget` | int | 否 | 每日热量目标 |

Response：`array<AiRecipeVO>`（按天/餐次）。

### 17.3 AI 估算菜谱营养 `POST /api/v1/ai/recipes/nutrition-estimate`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 菜名 |
| `ingredients` | array\<object> | 是 | 食材与用量 |

Response：NutritionVO。

### 17.4 AI 生成制作步骤 `POST /api/v1/ai/recipes/cooking-steps`

Request：`name`（string，是）、`ingredients`（array\<string>，是）。Response：`steps`（array\<object>，含 `stepNo`、`content`）。

### 17.5 AI 生成配图描述/采购清单/海报内容

接口：`POST /api/v1/ai/recipes/image-prompt`、`/shopping-list`、`/week-poster`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recipeInfo` | object | 条件 | image-prompt 用菜谱信息 |
| `recipes` | array\<object> | 条件 | shopping-list 用菜谱列表 |
| `weekPlanId` | long | 条件 | week-poster 用周计划 ID |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `content` | string | 响应 | 生成的文本内容（提示词/海报文案） |
| `items` | array\<object> | 响应 | 采购清单项（shopping-list 返回） |

### 17.6 保存 AI 菜谱 `POST /api/v1/ai/recipes/save`

Request：`aiRecipeId`（long，是）。

Response（保存到正式菜谱库）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `recipeId` | long | 响应 | 正式菜谱 ID |

AiRecipeVO（生成结果通用结构）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `aiRecipeId` | long | 响应 | AI 菜谱记录 ID |
| `name` | string | 响应 | 菜名 |
| `description` | string | 响应 | 简介 |
| `category` | enum | 响应 | 餐次分类 |
| `ingredients` | array\<object> | 响应 | 食材，含 `name`、`amount`、`unit` |
| `steps` | array\<object> | 响应 | 步骤 |
| `nutrition` | object | 响应 | NutritionVO |
| `imagePrompt` | string | 响应 | 配图提示词，可空 |
| `createdAt` | datetime | 响应 | 生成时间 |

### 17.7 查询 AI 菜谱历史 `GET /api/v1/ai/recipes/history`

Request：分页参数。Response：`PageResult<AiRecipeVO>`。

### 17.8 删除 AI 菜谱记录 `DELETE /api/v1/ai/recipes/history/{id}`

Request：路径参数 `id`（long，是）。Response：无 `data`。

### 17.9 AI 菜谱转膳食计划 `POST /api/v1/ai/recipes/{aiRecipeId}/to-meal-plan`

Request：路径参数 `aiRecipeId`（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `date` | date | 是 | 计划日期 |
| `mealType` | enum | 是 | 餐次 |

Response：MealPlanItemVO（见 10.1）。

### 17.10 AI 菜谱转社区帖子 `POST /api/v1/ai/recipes/{aiRecipeId}/share-to-community`

Request：路径参数 `aiRecipeId`（long，是）+ `title`（string，是）、`content`（string，否）。Response：`postId`（long）。

## 18. 社区帖子模块 DTO

### 18.1 发布帖子 `POST /api/v1/community/posts`

Request（PostCreateDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `title` | string | 是 | 标题，≤100 字符 |
| `content` | string | 是 | 正文 |
| `coverFileId` | long | 否 | 封面文件 ID |
| `imageIds` | array\<long> | 否 | 配图文件 ID 列表 |
| `tagIds` | array\<long> | 否 | 标签 ID 列表 |

Response：PostVO，见 18.4。

### 18.2 修改帖子 `PUT /api/v1/community/posts/{postId}`

Request：路径参数 `postId`（long，是）+ 18.1 字段（可选）。Response：PostVO。

### 18.3 删除帖子 `DELETE /api/v1/community/posts/{postId}`

Request：路径参数 `postId`（long，是）。Response：无 `data`。

### 18.4 查询帖子详情 `GET /api/v1/community/posts/{postId}`

Request：路径参数 `postId`（long，是）。

Response（PostVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 帖子 ID |
| `title` | string | 响应 | 标题 |
| `content` | string | 响应 | 正文 |
| `coverUrl` | string | 响应 | 封面地址 |
| `images` | array\<string> | 响应 | 图片地址列表 |
| `tags` | array\<object> | 响应 | 标签，含 `id`、`name` |
| `author` | object | 响应 | UserBriefVO |
| `sourceType` | enum | 响应 | 帖子来源 |
| `sourceId` | long | 响应 | 来源业务 ID，可空 |
| `likeCount` | int | 响应 | 点赞数 |
| `commentCount` | int | 响应 | 评论数 |
| `favoriteCount` | int | 响应 | 收藏数 |
| `viewCount` | int | 响应 | 浏览数 |
| `liked` | boolean | 响应 | 当前用户是否点赞 |
| `favorited` | boolean | 响应 | 当前用户是否收藏 |
| `status` | enum | 响应 | 帖子状态 |
| `createdAt` | datetime | 响应 | 发布时间 |

### 18.5 分页查询帖子 `GET /api/v1/community/posts`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 关键词 |
| `tagId` | long | 否 | 标签筛选 |
| `page` | int | 否 | 页码 |
| `size` | int | 否 | 每页数量 |
| `sort` | string | 否 | 排序 |

Response：`PageResult<PostVO>`。

### 18.6 帖子列表类查询

接口：`GET /api/v1/community/posts/me`、`/recommend`、`/hot`、`/latest`、`/following`、`/users/{userId}/posts`

Request：分页参数；`/users/{userId}/posts` 需路径参数 `userId`（long，是）。Response：`PageResult<PostVO>`。

### 18.7 增加帖子浏览量 `POST /api/v1/community/posts/{postId}/view`

Request：路径参数 `postId`（long，是）。Response：无 `data`。

### 18.8 查询帖子统计 `GET /api/v1/community/posts/{postId}/statistics`

Request：路径参数 `postId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `likeCount` | int | 响应 | 点赞数 |
| `commentCount` | int | 响应 | 评论数 |
| `favoriteCount` | int | 响应 | 收藏数 |
| `viewCount` | int | 响应 | 浏览数 |

### 18.9 从业务对象发布帖子

接口：`POST /api/v1/community/posts/from-recipe/{recipeId}`、`/from-meal-record/{recordId}`、`/from-ai-recipe/{aiRecipeId}`、`/from-food-recognition/{taskId}`

Request：对应路径参数（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `title` | string | 是 | 标题 |
| `content` | string | 否 | 正文 |

Response：`postId`（long）。

### 18.10 查询帖子关联对象

接口：`GET /api/v1/community/posts/{postId}/related-recipe`、`/related-meal-record`、`/related-ai-result`

Request：路径参数 `postId`（long，是）。Response：对应业务 VO（RecipeVO / MealRecordVO / AiRecipeVO）。

## 19. 社区图片、点赞、收藏模块 DTO

### 19.1 上传帖子图片 `POST /api/v1/community/posts/images`

Request（multipart/form-data）：`file`（file，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `imageId` | long | 响应 | 图片 ID |
| `url` | string | 响应 | 图片地址 |

### 19.2 删除帖子图片 `DELETE /api/v1/community/posts/images/{imageId}`

Request：路径参数 `imageId`（long，是）。Response：无 `data`。

### 19.3 查询帖子图片 `GET /api/v1/community/posts/{postId}/images`

Request：路径参数 `postId`（long，是）。Response：`array<object>`（含 `imageId`、`url`、`isCover`）。

### 19.4 设置封面图 `PATCH /api/v1/community/posts/{postId}/cover`

Request：路径参数 `postId`（long，是）+ `imageId`（long，是）。Response：无 `data`。

### 19.5 点赞/取消点赞帖子

接口：`POST` / `DELETE /api/v1/community/posts/{postId}/like`

Request：路径参数 `postId`（long，是）。Response：无 `data`。

### 19.6 查询点赞状态 `GET /api/v1/community/posts/{postId}/like/status`

Request：路径参数 `postId`（long，是）。Response：`liked`（boolean）。

### 19.7 查询点赞用户 `GET /api/v1/community/posts/{postId}/likes`

Request：路径参数 `postId`（long，是）+ 分页参数。Response：`PageResult<UserBriefVO>`。

### 19.8 查询我点赞的帖子 `GET /api/v1/community/posts/liked`

Request：分页参数。Response：`PageResult<PostVO>`。

### 19.9 收藏/取消收藏帖子

接口：`POST` / `DELETE /api/v1/community/posts/{postId}/favorite`

Request：路径参数 `postId`（long，是）。Response：无 `data`。

### 19.10 查询收藏状态 `GET /api/v1/community/posts/{postId}/favorite/status`

Request：路径参数 `postId`（long，是）。Response：`favorited`（boolean）。

### 19.11 查询我收藏的帖子 `GET /api/v1/community/posts/favorites`

Request：分页参数。Response：`PageResult<PostVO>`。

### 19.12 查询收藏用户 `GET /api/v1/community/posts/{postId}/favorites`

Request：路径参数 `postId`（long，是）+ 分页参数。Response：`PageResult<UserBriefVO>`。

## 20. 社区评论模块 DTO

### 20.1 发表评论 `POST /api/v1/community/posts/{postId}/comments`

Request：路径参数 `postId`（long，是）+ `content`（string，是）。Response：CommentVO，见 20.4。

### 20.2 回复评论 `POST /api/v1/community/comments/{commentId}/replies`

Request：路径参数 `commentId`（long，是）+ `content`（string，是）。Response：CommentVO。

### 20.3 删除评论 `DELETE /api/v1/community/comments/{commentId}`

Request：路径参数 `commentId`（long，是）。Response：无 `data`。

### 20.4 查询帖子评论 `GET /api/v1/community/posts/{postId}/comments`

Request：路径参数 `postId`（long，是）+ 分页参数。

Response：`PageResult<CommentVO>`，列表项：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 评论 ID |
| `postId` | long | 响应 | 帖子 ID |
| `parentId` | long | 响应 | 父评论 ID，一级评论为空 |
| `author` | object | 响应 | UserBriefVO |
| `content` | string | 响应 | 评论内容 |
| `likeCount` | int | 响应 | 点赞数 |
| `liked` | boolean | 响应 | 当前用户是否点赞 |
| `replyCount` | int | 响应 | 回复数 |
| `createdAt` | datetime | 响应 | 时间 |

### 20.5 查询评论回复 `GET /api/v1/community/comments/{commentId}/replies`

Request：路径参数 `commentId`（long，是）+ 分页参数。Response：`PageResult<CommentVO>`。

### 20.6 查询评论详情 `GET /api/v1/community/comments/{commentId}`

Request：路径参数 `commentId`（long，是）。Response：CommentVO。

### 20.7 点赞/取消点赞评论

接口：`POST` / `DELETE /api/v1/community/comments/{commentId}/like`

Request：路径参数 `commentId`（long，是）。Response：无 `data`。

### 20.8 查询评论点赞状态 `GET /api/v1/community/comments/{commentId}/like/status`

Request：路径参数 `commentId`（long，是）。Response：`liked`（boolean）。

### 20.9 查询我发表的评论 `GET /api/v1/community/comments/me`

Request：分页参数。Response：`PageResult<CommentVO>`。

### 20.10 查询收到的评论 `GET /api/v1/community/comments/received`

Request：分页参数。Response：`PageResult<CommentVO>`（附带所属帖子摘要）。

## 21. 社区标签、搜索、举报模块 DTO

### 21.1 创建标签 `POST /api/v1/community/tags`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 标签名，唯一 |
| `category` | string | 否 | 标签分类 |

Response：TagVO，见 21.4。

### 21.2 查询标签列表 `GET /api/v1/community/tags`

Request：`keyword`（string，否）、`category`（string，否）。Response：`array<TagVO>`。

### 21.3 查询热门标签 `GET /api/v1/community/tags/hot`

Request：`limit`（int，否）。Response：`array<TagVO>`。

### 21.4 查询标签详情 `GET /api/v1/community/tags/{tagId}`

Request：路径参数 `tagId`（long，是）。

Response（TagVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 标签 ID |
| `name` | string | 响应 | 标签名 |
| `category` | string | 响应 | 分类 |
| `postCount` | int | 响应 | 关联帖子数 |

### 21.5 查询标签下帖子 `GET /api/v1/community/tags/{tagId}/posts`

Request：路径参数 `tagId`（long，是）+ 分页参数。Response：`PageResult<PostVO>`。

### 21.6 修改标签 `PUT /api/v1/community/tags/{tagId}`

Request：路径参数 `tagId`（long，是）+ `name`（string，否）、`category`（string，否）。Response：TagVO。

### 21.7 删除标签 `DELETE /api/v1/community/tags/{tagId}`

Request：路径参数 `tagId`（long，是）。Response：无 `data`。

### 21.8 给帖子绑定标签 `POST /api/v1/community/posts/{postId}/tags`

Request：路径参数 `postId`（long，是）+ `tagIds`（array\<long>，是）。Response：`array<TagVO>`。

### 21.9 移除帖子标签 `DELETE /api/v1/community/posts/{postId}/tags/{tagId}`

Request：路径参数 `postId`（long，是）、`tagId`（long，是）。Response：无 `data`。

### 21.10 搜索接口

接口：`GET /api/v1/community/search/posts`、`/search/users`、`/search/tags`、`/search`

Request：`keyword`（string，是）+ 分页参数（综合搜索 `/search` 可不分页）。

Response：
- `/search/posts`：`PageResult<PostVO>`
- `/search/users`：`PageResult<UserBriefVO>`
- `/search/tags`：`array<TagVO>`
- `/search`：综合对象，含 `posts`、`users`、`tags` 三个数组

### 21.11 举报帖子/评论

接口：`POST /api/v1/community/posts/{postId}/report`、`/comments/{commentId}/report`

Request：对应路径参数（long，是）+

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `reasonType` | enum | 是 | 举报原因类型 |
| `reason` | string | 否 | 补充说明 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `reportId` | long | 响应 | 举报记录 ID |
| `status` | string | 响应 | 处理状态（PENDING） |

### 21.12 查询我的举报 `GET /api/v1/community/reports/me`

Request：分页参数。

Response：`PageResult<ReportVO>`，列表项：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `reportId` | long | 响应 | 举报 ID |
| `targetType` | string | 响应 | POST 或 COMMENT |
| `targetId` | long | 响应 | 被举报对象 ID |
| `reasonType` | enum | 响应 | 举报原因 |
| `status` | string | 响应 | 处理状态 |
| `createdAt` | datetime | 响应 | 举报时间 |

## 22. 采购清单模块 DTO

### 22.1 创建采购清单 `POST /api/v1/shopping-lists`

Request（ShoppingListCreateDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 清单名称 |
| `items` | array\<object> | 否 | 清单明细，见 ShoppingItemDTO |

ShoppingItemDTO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `ingredientId` | long | 否 | 食材 ID，可为自定义项时为空 |
| `name` | string | 是 | 物品名称 |
| `amount` | decimal | 是 | 数量 |
| `unit` | string | 是 | 单位 |

Response：ShoppingListVO，见 22.5。

### 22.2 根据膳食计划生成清单 `POST /api/v1/shopping-lists/from-meal-plan/{planId}`

Request：路径参数 `planId`（long，是）。Response：ShoppingListVO。

### 22.3 根据一周计划生成清单 `POST /api/v1/shopping-lists/from-week-plan`

Request：`startDate`（date，是）。Response：ShoppingListVO。

### 22.4 查询采购清单列表 `GET /api/v1/shopping-lists`

Request：分页参数。Response：`PageResult<ShoppingListVO>`。

### 22.5 查询采购清单详情 `GET /api/v1/shopping-lists/{listId}`

Request：路径参数 `listId`（long，是）。

Response（ShoppingListVO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 清单 ID |
| `name` | string | 响应 | 清单名称 |
| `items` | array\<object> | 响应 | 清单明细，见 ShoppingItemVO |
| `totalCount` | int | 响应 | 物品总数 |
| `checkedCount` | int | 响应 | 已购买数 |
| `createdAt` | datetime | 响应 | 创建时间 |

ShoppingItemVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `itemId` | long | 响应 | 明细 ID |
| `ingredientId` | long | 响应 | 食材 ID，可空 |
| `name` | string | 响应 | 物品名称 |
| `amount` | decimal | 响应 | 数量 |
| `unit` | string | 响应 | 单位 |
| `checked` | boolean | 响应 | 是否已购买 |

### 22.6 修改采购清单 `PUT /api/v1/shopping-lists/{listId}`

Request：路径参数 `listId`（long，是）+ `name`（string，否）、`items`（array\<ShoppingItemDTO>，否）。Response：ShoppingListVO。

### 22.7 删除采购清单 `DELETE /api/v1/shopping-lists/{listId}`

Request：路径参数 `listId`（long，是）。Response：无 `data`。

### 22.8 勾选/取消已购买

接口：`PATCH /api/v1/shopping-lists/{listId}/items/{itemId}/checked`、`/unchecked`

Request：路径参数 `listId`（long，是）、`itemId`（long，是）。Response：无 `data`。

### 22.9 导出采购清单 `GET /api/v1/shopping-lists/{listId}/export`

Request：路径参数 `listId`（long，是）。Response：二进制文件流。

## 23. 文件资源模块 DTO

### 23.1 上传文件类接口

接口：`POST /api/v1/files/upload`、`/avatar`、`/recipe-image`、`/meal-record-image`、`/community-post-image`

Request（multipart/form-data）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `file` | file | 是 | 上传文件 |
| `bizType` | enum | 否 | 业务类型，`/upload` 必填，其余固定 |

Response：FileVO（见 3.5）。

### 23.2 查询文件信息 `GET /api/v1/files/{fileId}`

Request：路径参数 `fileId`（long，是）。Response：FileVO。

### 23.3 删除文件 `DELETE /api/v1/files/{fileId}`

Request：路径参数 `fileId`（long，是）。Response：无 `data`。

### 23.4 查询文件访问地址 `GET /api/v1/files/{fileId}/url`

Request：路径参数 `fileId`（long，是）+ `expireSeconds`（int，否，临时链接有效期）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `url` | string | 响应 | 访问地址 |
| `expireAt` | datetime | 响应 | 过期时间，可空表示长期有效 |

## 24. 系统字典模块 DTO

### 24.1 字典查询接口（统一结构）

接口：`GET /api/v1/dictionaries/genders`、`/meal-types`、`/health-goals`、`/chronic-diseases`、`/allergens`、`/diet-restrictions`、`/ingredient-categories`、`/recipe-categories`、`/nutrients`、`/community-tag-categories`

Request：无。

Response：`array<DictItemVO>`：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `code` | string | 响应 | 字典编码 |
| `name` | string | 响应 | 显示名称 |
| `sortOrder` | int | 响应 | 排序 |
| `extra` | object | 响应 | 额外属性，可空 |

## 25. 通知消息模块 DTO

### 25.1 查询我的通知 `GET /api/v1/notifications`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `type` | enum | 否 | 通知类型筛选 |
| `read` | boolean | 否 | 是否已读筛选 |
| `page` | int | 否 | 页码 |
| `size` | int | 否 | 每页数量 |

Response：`PageResult<NotificationVO>`：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 通知 ID |
| `type` | enum | 响应 | 通知类型 |
| `title` | string | 响应 | 标题 |
| `content` | string | 响应 | 内容 |
| `fromUser` | object | 响应 | 触发用户 UserBriefVO，系统通知为空 |
| `targetType` | string | 响应 | 关联对象类型 |
| `targetId` | long | 响应 | 关联对象 ID |
| `read` | boolean | 响应 | 是否已读 |
| `createdAt` | datetime | 响应 | 时间 |

### 25.2 查询未读通知数 `GET /api/v1/notifications/unread-count`

Request：无。Response：`unreadCount`（int）。

### 25.3 标记通知已读 `PATCH /api/v1/notifications/{notificationId}/read`

Request：路径参数 `notificationId`（long，是）。Response：无 `data`。

### 25.4 全部标记已读 `PATCH /api/v1/notifications/read-all`

Request：`type`（enum，否）。Response：`updatedCount`（int）。

### 25.5 删除通知 `DELETE /api/v1/notifications/{notificationId}`

Request：路径参数 `notificationId`（long，是）。Response：无 `data`。

### 25.6 分类通知查询

接口：`GET /api/v1/notifications/likes`、`/comments`、`/favorites`、`/system`

Request：分页参数。Response：`PageResult<NotificationVO>`。

## 26. 管理后台模块 DTO

### 26.1 查询用户列表 `GET /api/v1/admin/users`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 用户名或邮箱 |
| `status` | enum | 否 | 账号状态 |
| `page` | int | 否 | 页码 |
| `size` | int | 否 | 每页数量 |

Response：`PageResult<AdminUserVO>`：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 用户 ID |
| `username` | string | 响应 | 用户名 |
| `email` | string | 响应 | 脱敏邮箱 |
| `role` | enum | 响应 | 角色 |
| `status` | enum | 响应 | 状态 |
| `lastLoginAt` | datetime | 响应 | 最近登录 |
| `createdAt` | datetime | 响应 | 注册时间 |

### 26.2 查询用户详情 `GET /api/v1/admin/users/{userId}`

Request：路径参数 `userId`（long，是）。Response：AdminUserVO + 统计字段（`postCount`、`mealRecordCount`）。

### 26.3 禁用/启用用户

接口：`PATCH /api/v1/admin/users/{userId}/disable`、`/enable`

Request：路径参数 `userId`（long，是）+ `reason`（string，禁用时建议必填）。Response：无 `data`。

### 26.4 重置用户密码 `PATCH /api/v1/admin/users/{userId}/reset-password`

Request：路径参数 `userId`（long，是）+ `newPassword`（string，否，不传则生成临时密码）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `temporaryPassword` | string | 响应 | 临时密码，自定义密码时为空 |

### 26.5 查询用户健康档案 `GET /api/v1/admin/users/{userId}/health-profile`

Request：路径参数 `userId`（long，是）。Response：HealthProfileVO（见 7.2）。

### 26.6 查询用户膳食记录 `GET /api/v1/admin/users/{userId}/meal-records`

Request：路径参数 `userId`（long，是）+ `startDate`、`endDate`、分页参数。Response：`PageResult<MealRecordVO>`。

### 26.7 食材与菜谱管理

| 接口 | Request | Response |
| --- | --- | --- |
| `GET /api/v1/admin/ingredients` | 查询条件 + 分页 | `PageResult<IngredientVO>` |
| `PATCH /api/v1/admin/ingredients/{ingredientId}/review` | `reviewStatus`（enum，是）、`reason`（string，否） | 无 `data` |
| `GET /api/v1/admin/recipes` | 查询条件 + 分页 | `PageResult<RecipeVO>` |
| `PATCH /api/v1/admin/recipes/{recipeId}/review` | `reviewStatus`（enum，是）、`reason`（string，否） | 无 `data` |
| `PATCH /api/v1/admin/recipes/{recipeId}/offline` | `reason`（string，否） | 无 `data` |
| `PATCH /api/v1/admin/recipes/{recipeId}/online` | 路径参数 | 无 `data` |

### 26.8 社区管理

| 接口 | Request | Response |
| --- | --- | --- |
| `GET /api/v1/admin/community/posts` | 查询条件 + 分页 | `PageResult<PostVO>` |
| `PATCH /api/v1/admin/community/posts/{postId}/review` | `reviewStatus`（enum，是）、`reason`（string，否） | 无 `data` |
| `PATCH /api/v1/admin/community/posts/{postId}/offline` | `reason`（string，否） | 无 `data` |
| `PATCH /api/v1/admin/community/posts/{postId}/online` | 路径参数 | 无 `data` |
| `DELETE /api/v1/admin/community/comments/{commentId}` | `reason`（string，否） | 无 `data` |
| `GET /api/v1/admin/community/reports` | `status`、分页 | `PageResult<AdminReportVO>` |
| `PATCH /api/v1/admin/community/reports/{reportId}/handle` | `handleResult`（string，是）、`remark`（string，否） | 无 `data` |
| `GET /api/v1/admin/community/tags` | 查询条件 | `array<TagVO>` |
| `PUT /api/v1/admin/community/tags/{tagId}` | `name`、`category` | TagVO |
| `DELETE /api/v1/admin/community/tags/{tagId}` | 路径参数 | 无 `data` |

AdminReportVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `reportId` | long | 响应 | 举报 ID |
| `reporter` | object | 响应 | 举报人 UserBriefVO |
| `targetType` | string | 响应 | POST 或 COMMENT |
| `targetId` | long | 响应 | 被举报对象 ID |
| `reasonType` | enum | 响应 | 举报原因 |
| `reason` | string | 响应 | 补充说明 |
| `status` | string | 响应 | 处理状态 |
| `createdAt` | datetime | 响应 | 举报时间 |

### 26.9 AI 管理

| 接口 | Request | Response |
| --- | --- | --- |
| `GET /api/v1/admin/ai/configs` | 无 | `array<AiConfigVO>` |
| `PUT /api/v1/admin/ai/configs` | AiConfigUpdateDTO | `array<AiConfigVO>` |
| `GET /api/v1/admin/ai/logs` | 日期范围、`model`、分页 | `PageResult<AiCallLogVO>` |
| `GET /api/v1/admin/ai/local-models` | 无 | `array<LocalModelVO>` |
| `GET /api/v1/admin/ai/local-models/{modelId}` | 路径参数 | LocalModelVO |
| `PATCH /api/v1/admin/ai/local-models/{modelId}/enable` | 路径参数 | 无 `data` |
| `PATCH /api/v1/admin/ai/local-models/{modelId}/disable` | 路径参数 | 无 `data` |
| `POST /api/v1/admin/ai/local-models/{modelId}/reload` | 路径参数 | `reloadResult`（string） |
| `GET /api/v1/admin/ai/health` | 无 | `status`、`latencyMs` |

AiConfigVO / AiConfigUpdateDTO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `provider` | string | 是 | 提供方，如 DEEPSEEK |
| `baseUrl` | string | 是 | 服务地址 |
| `model` | string | 是 | 模型名称 |
| `apiKeyMasked` | string | 响应 | 脱敏 Key（响应用） |
| `apiKey` | string | 否 | 更新时传入明文 Key |
| `temperature` | decimal | 否 | 采样温度 |
| `maxTokens` | int | 否 | 最大输出 token |
| `enabled` | boolean | 否 | 是否启用 |

AiCallLogVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | long | 响应 | 日志 ID |
| `userId` | long | 响应 | 调用用户 |
| `scene` | enum | 响应 | 调用场景 |
| `model` | string | 响应 | 模型 |
| `promptTokens` | int | 响应 | 输入 token |
| `completionTokens` | int | 响应 | 输出 token |
| `elapsedMs` | int | 响应 | 耗时 |
| `success` | boolean | 响应 | 是否成功 |
| `errorMessage` | string | 响应 | 错误信息 |
| `createdAt` | datetime | 响应 | 调用时间 |

LocalModelVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `modelId` | long | 响应 | 模型 ID |
| `name` | string | 响应 | 模型名称 |
| `version` | string | 响应 | 版本 |
| `categoryCount` | int | 响应 | 类别数 |
| `accuracy` | decimal | 响应 | 准确率 |
| `enabled` | boolean | 响应 | 是否启用 |
| `loaded` | boolean | 响应 | 是否已加载 |

### 26.10 系统管理

| 接口 | Request | Response |
| --- | --- | --- |
| `GET /api/v1/admin/dashboard` | 无 | DashboardVO |
| `GET /api/v1/admin/system/configs` | `keyword`（string，否） | `array<SystemConfigVO>` |
| `PUT /api/v1/admin/system/configs/{configKey}` | `configValue`（string，是） | SystemConfigVO |
| `GET /api/v1/admin/logs/access` | 日期范围、分页 | `PageResult<AccessLogVO>` |
| `GET /api/v1/admin/logs/error` | 日期范围、分页 | `PageResult<ErrorLogVO>` |
| `POST /api/v1/admin/backups` | `remark`（string，否） | BackupVO |
| `GET /api/v1/admin/backups` | 分页 | `PageResult<BackupVO>` |
| `POST /api/v1/admin/backups/{backupId}/restore` | 路径参数 | `restoreResult`（string） |

DashboardVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userCount` | long | 响应 | 用户总数 |
| `postCount` | long | 响应 | 帖子总数 |
| `recipeCount` | long | 响应 | 菜谱总数 |
| `mealRecordCount` | long | 响应 | 膳食记录数 |
| `aiCallCount` | long | 响应 | AI 调用总数 |
| `todayActiveUsers` | long | 响应 | 今日活跃用户 |

SystemConfigVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `configKey` | string | 响应 | 配置键 |
| `configValue` | string | 响应 | 配置值 |
| `description` | string | 响应 | 说明 |
| `updatedAt` | datetime | 响应 | 更新时间 |

BackupVO：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `backupId` | long | 响应 | 备份 ID |
| `fileName` | string | 响应 | 备份文件名 |
| `size` | long | 响应 | 文件大小 |
| `remark` | string | 响应 | 备注 |
| `createdAt` | datetime | 响应 | 备份时间 |

## 27. Spring Boot 内部接口模块 DTO

内部接口必须携带 `X-Internal-Token`，仅供 Python AI 服务或内部任务调用。

### 27.1 查询用户 AI 上下文 `GET /api/internal/v1/users/{userId}/ai-context`

Request：路径参数 `userId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 响应 | 用户 ID |
| `age` | int | 响应 | 年龄 |
| `gender` | enum | 响应 | 性别 |
| `bmi` | decimal | 响应 | BMI |
| `goals` | array\<enum> | 响应 | 健康目标 |
| `allergens` | array\<string> | 响应 | 过敏源 |
| `chronicDiseases` | array\<string> | 响应 | 慢性病 |
| `dietRestrictions` | array\<string> | 响应 | 饮食禁忌 |
| `preferences` | object | 响应 | 偏好（PreferenceVO） |

### 27.2 查询用户今日/一周营养上下文

接口：`GET /api/internal/v1/users/{userId}/nutrition-context/today`、`/week`

Request：路径参数 `userId`（long，是）+ `date`（date，否）或 `startDate`（date，否）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `period` | string | 响应 | TODAY 或 WEEK |
| `nutrition` | object | 响应 | NutritionVO 汇总 |
| `calorieTarget` | decimal | 响应 | 目标热量 |
| `records` | array\<object> | 响应 | 简要饮食记录 |

### 27.3 根据模型标签匹配食材/菜谱

接口：`POST /api/internal/v1/ingredients/match-by-labels`、`/recipes/match-by-labels`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `labels` | array\<string> | 是 | 模型识别标签 |

Response：`array<object>`，匹配项含 `label`、`matchedId`、`name`、`score`。

### 27.4 内部营养计算 `POST /api/internal/v1/nutrition/calculate`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `items` | array\<object> | 是 | 食材用量，含 `ingredientId`、`amount`、`unit` |

Response：NutritionVO。

### 27.5 保存 AI 调用日志 `POST /api/internal/v1/ai/logs`

Request（AiCallLogDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `userId` | long | 否 | 调用用户 |
| `scene` | enum | 是 | 调用场景 |
| `model` | string | 是 | 模型 |
| `promptTokens` | int | 否 | 输入 token |
| `completionTokens` | int | 否 | 输出 token |
| `elapsedMs` | int | 否 | 耗时 |
| `success` | boolean | 是 | 是否成功 |
| `errorMessage` | string | 否 | 错误信息 |

Response：`logId`（long）。

### 27.6 保存食物识别回调 `POST /api/internal/v1/food-recognition/callback`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `taskId` | long | 是 | 识别任务 ID |
| `status` | enum | 是 | SUCCESS 或 FAILED |
| `modelName` | string | 否 | 模型名称 |
| `modelVersion` | string | 否 | 版本 |
| `results` | array\<object> | 否 | 识别结果（label、confidence） |
| `errorMessage` | string | 否 | 失败原因 |

Response：无 `data`。

### 27.7 查询文件内部访问地址 `GET /api/internal/v1/files/{fileId}/access-url`

Request：路径参数 `fileId`（long，是）。

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `accessUrl` | string | 响应 | 内部可访问地址（含本地路径或内网 URL） |

## 28. Python AI 服务接口 DTO

仅供 Spring Boot 调用，路径前缀 `/ai/v1`。

### 28.1 基础与模型接口

| 接口 | Request | Response |
| --- | --- | --- |
| `GET /ai/v1/health` | 无 | `status`（string）、`uptimeSeconds`（int） |
| `GET /ai/v1/models/status` | 无 | `loaded`（boolean）、`device`（string） |
| `GET /ai/v1/models/food-classifier/info` | 无 | `modelName`、`modelVersion`、`categoryCount` |
| `GET /ai/v1/models/food-classifier/labels` | 无 | `labels`（array\<object>，含 `index`、`label`、`displayName`） |
| `POST /ai/v1/models/food-classifier/reload` | 无 | `reloaded`（boolean）、`elapsedMs`（int） |

### 28.2 食物图片分类 `POST /ai/v1/food/classify`

Request（multipart/form-data）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `image` | file | 是 | 食物图片 |
| `topK` | int | 否 | 返回前 K 个，默认 3 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `modelName` | string | 响应 | 模型名称 |
| `modelVersion` | string | 响应 | 版本 |
| `results` | array\<object> | 响应 | 分类结果，含 `label`、`displayName`、`confidence` |
| `elapsedMs` | int | 响应 | 推理耗时 |

### 28.3 批量图片分类 `POST /ai/v1/food/batch-classify`

Request（multipart/form-data）：`images`（array\<file>，是）、`topK`（int，否）。Response：`array`，每项含 `index` 与分类结果结构（同 28.2）。

### 28.4 图片分类并解释 `POST /ai/v1/food/classify-and-explain`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `image` | file | 是 | 食物图片 |
| `userContext` | object | 否 | 用户健康上下文（来自内部接口） |

Response：分类结果（同 28.2）+ `explanation`（string）。

### 28.5 图片识别生成建议 `POST /ai/v1/food/image-meal-advice`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `classifyResults` | array\<object> | 是 | 分类结果 |
| `nutrition` | object | 否 | 营养估算 |
| `healthContext` | object | 否 | 健康上下文 |

Response：`advice`（string）。

### 28.6 DeepSeek 普通问答 `POST /ai/v1/llm/chat`

Request（LlmChatDTO）：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `messages` | array\<object> | 是 | 对话消息，含 `role`、`content` |
| `context` | object | 否 | 结构化健康上下文 |
| `model` | string | 否 | 指定模型，默认配置模型 |
| `temperature` | decimal | 否 | 采样温度 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `reply` | string | 响应 | 回复内容 |
| `model` | string | 响应 | 使用模型 |
| `promptTokens` | int | 响应 | 输入 token |
| `completionTokens` | int | 响应 | 输出 token |

### 28.7 DeepSeek 流式问答 `POST /ai/v1/llm/chat/stream`

Request：同 28.6。Response：`text/event-stream`，逐段返回增量文本，结束以 `[DONE]` 标记。

### 28.8 结构化生成接口

接口：`POST /ai/v1/llm/today-diagnosis`、`/weekly-advice`、`/food-alternatives`、`/recipe-generate`、`/recipe-image-prompt`、`/meal-poster`

Request：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `context` | object | 是 | 场景相关结构化数据（营养上下文、食材、目标等） |
| `model` | string | 否 | 指定模型 |

Response：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `content` | string | 响应 | 文本类结果（诊断/建议/海报/提示词） |
| `data` | object | 响应 | 结构化结果（如 recipe-generate 返回菜谱 JSON） |
| `model` | string | 响应 | 使用模型 |

recipe-generate 的 `data` 结构：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 响应 | 菜名 |
| `description` | string | 响应 | 简介 |
| `ingredients` | array\<object> | 响应 | 食材，含 `name`、`amount`、`unit` |
| `steps` | array\<object> | 响应 | 步骤，含 `stepNo`、`content` |
| `nutrition` | object | 响应 | 营养估算 |

## 29. 附录：DTO 命名约定建议

| 后缀 | 用途 | 示例 |
| --- | --- | --- |
| `XxxCreateDTO` | 创建请求 | `IngredientCreateDTO` |
| `XxxUpdateDTO` | 更新请求 | `AiConfigUpdateDTO` |
| `XxxQuery` | 查询条件 | `PageQuery` |
| `XxxVO` | 响应对象 | `RecipeVO` |
| `XxxBriefVO` | 简要响应对象 | `UserBriefVO` |
| `PageResult<T>` | 分页响应 | `PageResult<PostVO>` |

实现建议：

- 使用 `jakarta.validation` 注解（`@NotNull`、`@NotBlank`、`@Size`、`@Min`、`@Max`、`@Email`）实现「必填」与范围校验。
- 枚举字段在 DTO 中使用枚举类型接收，结合 `@JsonValue` 与全局反序列化器处理非法值返回 `40001`。
- 所有金额、营养类数值统一使用 `BigDecimal`，避免浮点误差。
- 响应统一通过 `ApiResponse.success(data)` 包装，分页统一返回 `PageResult`。
