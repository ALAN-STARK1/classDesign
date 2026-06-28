 **智能健康膳食管理系统 Web 后端接口方案**。

技术架构默认如下：

```text
前端：Vue3
业务后端：Spring Boot 3.5.x + Java 17
数据库：MySQL 8.4 LTS
AI 服务：Python FastAPI
AI 能力：本地食物图片分类模型 + DeepSeek OpenAI 兼容接口
可以使用的数据集： Food-101 Food-101 是公开食物图像数据集，包含 101 个食物类别、101000 张图片，每类有 750 张训练图和 250 张测试图，很适合做食物识别课程项目。 模型：  ResNet18 
认证方式：JWT
接口风格：RESTful API
业务接口前缀：/api/v1
管理端接口前缀：/api/v1/admin
内部接口前缀：/api/internal/v1
Python AI 服务前缀：/ai/v1
```

整体架构是：

```text
Vue3
  ↓
Spring Boot 业务后端
  ↓
MySQL

Spring Boot 业务后端
  ↓
Python AI 服务
  ├── 本地食物图片分类模型
  └── DeepSeek 大模型调用
```

---

# 一、系统模块总览

系统接口可以分为 18 个模块：

```text
1. 认证与账号模块
2. 用户资料与偏好模块
3. 健康档案模块
4. 食材库模块
5. 菜谱库模块
6. 膳食计划模块
7. 膳食记录模块
8. 营养分析模块
9. 体重趋势模块
10. 报表统计模块
11. 食物图片识别模块
12. AI 智能膳食顾问模块
13. AI 智能菜谱模块
14. 社区帖子模块
15. 点赞、评论、收藏模块
16. 标签、搜索、举报模块
17. 文件资源模块
18. 管理后台模块
```

---

# 二、认证与账号模块

| 接口名称     | 接口路径                                       | 接口功能                |
| -------- | ------------------------------------------ | ------------------- |
| 用户注册     | `POST /api/v1/auth/register`               | 用户创建账号              |
| 用户登录     | `POST /api/v1/auth/login`                  | 用户登录并获取 Token       |
| 刷新 Token | `POST /api/v1/auth/refresh-token`          | 使用刷新令牌换取新的访问令牌      |
| 退出登录     | `POST /api/v1/auth/logout`                 | 当前用户退出登录            |
| 获取登录用户信息 | `GET /api/v1/auth/me`                      | 获取当前登录用户基础信息        |
| 修改密码     | `PUT /api/v1/auth/password`                | 用户修改登录密码            |
| 重置密码申请   | `POST /api/v1/auth/password/reset-request` | 用户申请重置密码            |
| 重置密码确认   | `POST /api/v1/auth/password/reset-confirm` | 用户完成密码重置            |
| 注销账号     | `DELETE /api/v1/auth/account`              | 用户主动注销账号            |
| 查询账号安全信息 | `GET /api/v1/auth/security`                | 查询登录时间、密码更新时间、账号状态等 |

---

# 三、用户资料与偏好模块

| 接口名称     | 接口路径                                        | 接口功能                 |
| -------- | ------------------------------------------- | -------------------- |
| 查询个人资料   | `GET /api/v1/users/me/profile`              | 查询昵称、头像、简介等资料        |
| 修改个人资料   | `PUT /api/v1/users/me/profile`              | 修改用户基础资料             |
| 上传用户头像   | `POST /api/v1/users/me/avatar`              | 上传或更换头像              |
| 查询用户主页   | `GET /api/v1/users/{userId}/homepage`       | 查询某个用户的主页信息          |
| 查询我的偏好   | `GET /api/v1/users/me/preferences`          | 查询口味、饮食习惯、推荐偏好       |
| 修改我的偏好   | `PUT /api/v1/users/me/preferences`          | 修改口味偏好、目标偏好等         |
| 查询我的数据概览 | `GET /api/v1/users/me/overview`             | 查询健康档案、膳食记录、体重、帖子等概览 |
| 查询用户公开信息 | `GET /api/v1/users/{userId}/public-profile` | 查询其他用户公开信息           |
| 关注用户     | `POST /api/v1/users/{userId}/follow`        | 关注其他用户               |
| 取消关注用户   | `DELETE /api/v1/users/{userId}/follow`      | 取消关注                 |
| 查询我的关注列表 | `GET /api/v1/users/me/following`            | 查询我关注的用户             |
| 查询我的粉丝列表 | `GET /api/v1/users/me/followers`            | 查询关注我的用户             |

---

# 四、健康档案模块

| 接口名称      | 接口路径                                               | 接口功能                |
| --------- | -------------------------------------------------- | ------------------- |
| 创建健康档案    | `POST /api/v1/health-profiles`                     | 首次创建用户健康档案          |
| 查询我的健康档案  | `GET /api/v1/health-profiles/me`                   | 查询当前用户完整健康档案        |
| 修改健康档案    | `PUT /api/v1/health-profiles/me`                   | 修改身高、体重、年龄、性别等      |
| 重新计算 BMI  | `POST /api/v1/health-profiles/me/bmi/recalculate`  | 根据身高体重重新计算 BMI      |
| 查询 BMI 信息 | `GET /api/v1/health-profiles/me/bmi`               | 查询 BMI 数值和身体状态      |
| 设置健康目标    | `PUT /api/v1/health-profiles/me/goals`             | 设置减脂、增肌、控糖、低盐等目标    |
| 查询健康目标    | `GET /api/v1/health-profiles/me/goals`             | 查询当前健康目标            |
| 设置过敏源     | `PUT /api/v1/health-profiles/me/allergens`         | 设置花生、海鲜、牛奶等过敏源      |
| 查询过敏源     | `GET /api/v1/health-profiles/me/allergens`         | 查询用户过敏源             |
| 设置慢性病     | `PUT /api/v1/health-profiles/me/chronic-diseases`  | 设置高血压、糖尿病、高血脂等      |
| 查询慢性病     | `GET /api/v1/health-profiles/me/chronic-diseases`  | 查询慢性病信息             |
| 设置饮食禁忌    | `PUT /api/v1/health-profiles/me/diet-restrictions` | 设置素食、不吃辣、低嘌呤等       |
| 查询饮食禁忌    | `GET /api/v1/health-profiles/me/diet-restrictions` | 查询饮食禁忌              |
| 查询健康档案摘要  | `GET /api/v1/health-profiles/me/summary`           | 生成供膳食计划和 AI 使用的健康摘要 |
| 生成健康风险提示  | `GET /api/v1/health-profiles/me/risk-warnings`     | 根据健康档案生成基础风险提醒      |

---

# 五、食材库模块

| 接口名称       | 接口路径                                               | 接口功能              |
| ---------- | -------------------------------------------------- | ----------------- |
| 新增食材       | `POST /api/v1/ingredients`                         | 新增食材及营养成分         |
| 修改食材       | `PUT /api/v1/ingredients/{ingredientId}`           | 修改食材信息            |
| 删除食材       | `DELETE /api/v1/ingredients/{ingredientId}`        | 删除或停用食材           |
| 查询食材详情     | `GET /api/v1/ingredients/{ingredientId}`           | 查询单个食材详情          |
| 分页查询食材     | `GET /api/v1/ingredients`                          | 根据名称、分类、热量等查询食材   |
| 搜索食材       | `GET /api/v1/ingredients/search`                   | 模糊搜索食材            |
| 查询食材营养成分   | `GET /api/v1/ingredients/{ingredientId}/nutrition` | 查询热量、蛋白质、脂肪、碳水等   |
| 查询高蛋白食材    | `GET /api/v1/ingredients/high-protein`             | 查询高蛋白食材           |
| 查询低热量食材    | `GET /api/v1/ingredients/low-calorie`              | 查询低热量食材           |
| 查询低脂食材     | `GET /api/v1/ingredients/low-fat`                  | 查询低脂食材            |
| 查询低糖食材     | `GET /api/v1/ingredients/low-sugar`                | 查询适合控糖的食材         |
| 查询适合用户的食材  | `GET /api/v1/ingredients/suitable-for-me`          | 根据用户健康档案推荐食材      |
| 查询不适合用户的食材 | `GET /api/v1/ingredients/unsuitable-for-me`        | 根据过敏源、慢性病、禁忌过滤食材  |
| 批量导入食材     | `POST /api/v1/ingredients/import`                  | Excel 或 JSON 批量导入 |
| 导出食材库      | `GET /api/v1/ingredients/export`                   | 导出食材数据            |
| 启用或停用食材    | `PATCH /api/v1/ingredients/{ingredientId}/status`  | 修改食材可用状态          |

---

# 六、菜谱库模块

| 接口名称      | 接口路径                                                  | 接口功能           |
| --------- | ----------------------------------------------------- | -------------- |
| 新增菜谱      | `POST /api/v1/recipes`                                | 新增菜谱           |
| 修改菜谱      | `PUT /api/v1/recipes/{recipeId}`                      | 修改菜谱基础信息       |
| 删除菜谱      | `DELETE /api/v1/recipes/{recipeId}`                   | 删除或停用菜谱        |
| 查询菜谱详情    | `GET /api/v1/recipes/{recipeId}`                      | 查询菜谱、步骤、食材、营养  |
| 分页查询菜谱    | `GET /api/v1/recipes`                                 | 按分类、标签、热量、目标查询 |
| 搜索菜谱      | `GET /api/v1/recipes/search`                          | 根据关键词搜索菜谱      |
| 查询早餐菜谱    | `GET /api/v1/recipes/breakfast`                       | 查询早餐分类菜谱       |
| 查询午餐菜谱    | `GET /api/v1/recipes/lunch`                           | 查询午餐分类菜谱       |
| 查询晚餐菜谱    | `GET /api/v1/recipes/dinner`                          | 查询晚餐分类菜谱       |
| 查询加餐菜谱    | `GET /api/v1/recipes/snack`                           | 查询加餐分类菜谱       |
| 绑定菜谱食材    | `POST /api/v1/recipes/{recipeId}/ingredients`         | 为菜谱添加食材和用量     |
| 修改菜谱食材    | `PUT /api/v1/recipes/{recipeId}/ingredients`          | 修改菜谱食材组成       |
| 查询菜谱食材    | `GET /api/v1/recipes/{recipeId}/ingredients`          | 查询菜谱所需食材       |
| 计算菜谱营养    | `POST /api/v1/recipes/{recipeId}/nutrition/calculate` | 根据食材用量计算营养     |
| 查询菜谱营养    | `GET /api/v1/recipes/{recipeId}/nutrition`            | 查询菜谱营养数据       |
| 查询适合我的菜谱  | `GET /api/v1/recipes/suitable-for-me`                 | 根据健康档案推荐菜谱     |
| 查询不适合我的菜谱 | `GET /api/v1/recipes/unsuitable-for-me`               | 查询可能不适合当前用户的菜谱 |
| 收藏菜谱      | `POST /api/v1/recipes/{recipeId}/favorite`            | 收藏菜谱           |
| 取消收藏菜谱    | `DELETE /api/v1/recipes/{recipeId}/favorite`          | 取消收藏           |
| 查询我的收藏菜谱  | `GET /api/v1/recipes/favorites/me`                    | 查询用户收藏菜谱       |
| 菜谱评分      | `POST /api/v1/recipes/{recipeId}/rating`              | 用户给菜谱评分        |

---

# 七、膳食计划模块

| 接口名称       | 接口路径                                                           | 接口功能          |
| ---------- | -------------------------------------------------------------- | ------------- |
| 生成今日膳食计划   | `POST /api/v1/meal-plans/today/generate`                       | 自动生成今日三餐和加餐   |
| 生成指定日期膳食计划 | `POST /api/v1/meal-plans/generate`                             | 生成某天膳食计划      |
| 生成一周膳食计划   | `POST /api/v1/meal-plans/week/generate`                        | 生成一周膳食安排      |
| 查询今日膳食计划   | `GET /api/v1/meal-plans/today`                                 | 查询今天的计划       |
| 查询指定日期膳食计划 | `GET /api/v1/meal-plans/date/{date}`                           | 查询指定日期计划      |
| 查询周膳食计划    | `GET /api/v1/meal-plans/week`                                  | 查询某一周计划       |
| 修改膳食计划     | `PUT /api/v1/meal-plans/{planId}`                              | 修改计划内容        |
| 删除膳食计划     | `DELETE /api/v1/meal-plans/{planId}`                           | 删除计划          |
| 替换某一餐      | `PUT /api/v1/meal-plans/{planId}/meals/{mealType}/replace`     | 替换早餐、午餐、晚餐或加餐 |
| 重新生成某一餐    | `POST /api/v1/meal-plans/{planId}/meals/{mealType}/regenerate` | 单独重新生成某餐      |
| 锁定某一餐      | `PATCH /api/v1/meal-plans/{planId}/meals/{mealType}/lock`      | 锁定某餐不被重新生成    |
| 解锁某一餐      | `PATCH /api/v1/meal-plans/{planId}/meals/{mealType}/unlock`    | 取消锁定          |
| 查询计划生成原因   | `GET /api/v1/meal-plans/{planId}/reason`                       | 说明为什么推荐这些菜谱   |
| 查询计划营养分析   | `GET /api/v1/meal-plans/{planId}/nutrition-analysis`           | 查询该计划营养是否达标   |
| 查询计划采购清单   | `GET /api/v1/meal-plans/{planId}/shopping-list`                | 根据计划生成采购清单    |
| 应用计划为膳食记录  | `POST /api/v1/meal-plans/{planId}/apply-to-record`             | 将计划转为实际摄入记录   |

---

# 八、膳食记录模块

| 接口名称        | 接口路径                                                       | 接口功能          |
| ----------- | ---------------------------------------------------------- | ------------- |
| 新增膳食记录      | `POST /api/v1/meal-records`                                | 用户记录实际摄入食物    |
| 修改膳食记录      | `PUT /api/v1/meal-records/{recordId}`                      | 修改记录          |
| 删除膳食记录      | `DELETE /api/v1/meal-records/{recordId}`                   | 删除记录          |
| 查询记录详情      | `GET /api/v1/meal-records/{recordId}`                      | 查询单条膳食记录      |
| 按日期查询记录     | `GET /api/v1/meal-records/date/{date}`                     | 查询某天所有记录      |
| 分页查询我的记录    | `GET /api/v1/meal-records`                                 | 查询历史记录        |
| 新增早餐记录      | `POST /api/v1/meal-records/breakfast`                      | 快速记录早餐        |
| 新增午餐记录      | `POST /api/v1/meal-records/lunch`                          | 快速记录午餐        |
| 新增晚餐记录      | `POST /api/v1/meal-records/dinner`                         | 快速记录晚餐        |
| 新增加餐记录      | `POST /api/v1/meal-records/snack`                          | 快速记录加餐        |
| 从菜谱生成记录     | `POST /api/v1/meal-records/from-recipe/{recipeId}`         | 将菜谱转为实际记录     |
| 从膳食计划生成记录   | `POST /api/v1/meal-records/from-plan/{planId}`             | 将计划转为实际记录     |
| 从图片识别结果生成记录 | `POST /api/v1/meal-records/from-food-recognition/{taskId}` | 将食物识别结果转为膳食记录 |
| 查询每日摄入汇总    | `GET /api/v1/meal-records/daily-summary`                   | 汇总某天摄入        |
| 查询历史摄入统计    | `GET /api/v1/meal-records/history-statistics`              | 查询历史摄入趋势      |
| 标记异常饮食      | `PATCH /api/v1/meal-records/{recordId}/abnormal`           | 标记暴食、漏餐、夜宵等   |
| 分享膳食记录到社区   | `POST /api/v1/meal-records/{recordId}/share-to-community`  | 将记录生成社区帖子     |

---

# 九、营养分析模块

| 接口名称     | 接口路径                                                    | 接口功能           |
| -------- | ------------------------------------------------------- | -------------- |
| 分析单个菜谱营养 | `POST /api/v1/nutrition/analyze/recipe/{recipeId}`      | 分析菜谱营养         |
| 分析一餐营养   | `POST /api/v1/nutrition/analyze/meal`                   | 分析一餐摄入         |
| 分析一日营养   | `POST /api/v1/nutrition/analyze/day`                    | 分析一天摄入         |
| 分析膳食计划营养 | `POST /api/v1/nutrition/analyze/meal-plan/{planId}`     | 分析计划营养         |
| 分析膳食记录营养 | `POST /api/v1/nutrition/analyze/meal-record/{recordId}` | 分析实际记录营养       |
| 查询每日营养摄入 | `GET /api/v1/nutrition/daily`                           | 查询每日营养         |
| 查询周营养摄入  | `GET /api/v1/nutrition/weekly`                          | 查询周营养趋势        |
| 查询月营养摄入  | `GET /api/v1/nutrition/monthly`                         | 查询月营养趋势        |
| 膳食指南达标分析 | `POST /api/v1/nutrition/standard/compare`               | 对比推荐摄入标准       |
| 查询营养缺口   | `GET /api/v1/nutrition/gaps`                            | 查询缺少哪些营养       |
| 查询营养超标项  | `GET /api/v1/nutrition/excess`                          | 查询热量、脂肪、糖等是否超标 |
| 查询营养评分   | `GET /api/v1/nutrition/score`                           | 生成营养均衡评分       |
| 生成基础营养建议 | `GET /api/v1/nutrition/suggestions`                     | 根据营养分析生成规则建议   |
| 重新计算营养统计 | `POST /api/v1/nutrition/recalculate`                    | 重新计算营养缓存数据     |

---

# 十、体重趋势模块

| 接口名称     | 接口路径                                       | 接口功能         |
| -------- | ------------------------------------------ | ------------ |
| 新增体重记录   | `POST /api/v1/weight-records`              | 记录体重         |
| 修改体重记录   | `PUT /api/v1/weight-records/{recordId}`    | 修改体重         |
| 删除体重记录   | `DELETE /api/v1/weight-records/{recordId}` | 删除体重记录       |
| 查询体重详情   | `GET /api/v1/weight-records/{recordId}`    | 查询单条体重记录     |
| 查询体重列表   | `GET /api/v1/weight-records`               | 按日期范围查询      |
| 查询最近体重   | `GET /api/v1/weight-records/latest`        | 查询最新体重       |
| 查询体重趋势   | `GET /api/v1/weight-records/trend`         | 获取折线图数据      |
| 查询体重统计   | `GET /api/v1/weight-records/statistics`    | 查询平均、最大、最小体重 |
| 查询目标体重进度 | `GET /api/v1/weight-records/goal-progress` | 查询距离目标体重的进度  |
| 生成体重建议   | `GET /api/v1/weight-records/suggestions`   | 根据趋势生成建议     |

---

# 十一、报表统计模块

| 接口名称       | 接口路径                                     | 接口功能           |
| ---------- | ---------------------------------------- | -------------- |
| 生成周营养报表    | `POST /api/v1/reports/weekly/generate`   | 生成周报           |
| 生成月营养报表    | `POST /api/v1/reports/monthly/generate`  | 生成月报           |
| 查询周报       | `GET /api/v1/reports/weekly`             | 查询周报           |
| 查询月报       | `GET /api/v1/reports/monthly`            | 查询月报           |
| 查询热量趋势     | `GET /api/v1/reports/calorie-trend`      | 返回热量趋势         |
| 查询蛋白质趋势    | `GET /api/v1/reports/protein-trend`      | 返回蛋白质趋势        |
| 查询三大营养素占比  | `GET /api/v1/reports/macro-ratio`        | 查询蛋白质、脂肪、碳水占比  |
| 查询饮食达标率    | `GET /api/v1/reports/compliance-rate`    | 查询饮食达标情况       |
| 查询常吃食物排行   | `GET /api/v1/reports/top-foods`          | 统计常吃食物         |
| 查询健康目标完成度  | `GET /api/v1/reports/goal-progress`      | 查询减脂、增肌等目标完成情况 |
| 导出周报 PDF   | `GET /api/v1/reports/weekly/export/pdf`  | 导出周报 PDF       |
| 导出月报 PDF   | `GET /api/v1/reports/monthly/export/pdf` | 导出月报 PDF       |
| 导出报表 Excel | `GET /api/v1/reports/export/excel`       | 导出 Excel       |
| 生成报表摘要     | `GET /api/v1/reports/summary`            | 生成自然语言摘要       |

---

# 十二、食物图片识别模块

这个模块由 Spring Boot 对前端暴露接口，实际识别由 Python AI 服务完成。

| 接口名称         | 接口路径                                                              | 接口功能                |
| ------------ | ----------------------------------------------------------------- | ------------------- |
| 上传食物图片识别     | `POST /api/v1/food-recognition/image`                             | 用户上传食物图片，系统调用本地分类模型 |
| 批量上传图片识别     | `POST /api/v1/food-recognition/batch`                             | 批量识别多张食物图片          |
| 查询识别任务详情     | `GET /api/v1/food-recognition/tasks/{taskId}`                     | 查询识别结果              |
| 查询我的识别历史     | `GET /api/v1/food-recognition/history`                            | 查询历史识别记录            |
| 确认识别结果       | `POST /api/v1/food-recognition/tasks/{taskId}/confirm`            | 用户确认或修正识别结果         |
| 重新识别图片       | `POST /api/v1/food-recognition/tasks/{taskId}/retry`              | 重新调用模型识别            |
| 查询识别营养分析     | `GET /api/v1/food-recognition/tasks/{taskId}/nutrition`           | 查询识别食物的营养估算         |
| 识别结果转膳食记录    | `POST /api/v1/food-recognition/tasks/{taskId}/to-meal-record`     | 将识别结果保存为膳食记录        |
| 识别结果生成 AI 建议 | `POST /api/v1/food-recognition/tasks/{taskId}/ai-advice`          | 结合健康档案生成 AI 建议      |
| 分享识别结果到社区    | `POST /api/v1/food-recognition/tasks/{taskId}/share-to-community` | 将识别图片和营养分析发成帖子      |
| 查询模型支持类别     | `GET /api/v1/food-recognition/model/labels`                       | 查询当前模型可识别的食物类别      |
| 查询识别模型信息     | `GET /api/v1/food-recognition/model/info`                         | 查询模型名称、版本、准确率等      |

---

# 十三、AI 智能膳食顾问模块

Spring Boot 对外提供接口，内部调用 Python AI 服务中的 DeepSeek 模块。

| 接口名称       | 接口路径                                                       | 接口功能                |
| ---------- | ---------------------------------------------------------- | ------------------- |
| AI 膳食问答    | `POST /api/v1/ai/advisor/chat`                             | 用户自然语言咨询            |
| AI 流式问答    | `POST /api/v1/ai/advisor/chat/stream`                      | 流式返回 AI 回复          |
| 查询 AI 会话列表 | `GET /api/v1/ai/advisor/conversations`                     | 查询历史会话              |
| 查询 AI 会话详情 | `GET /api/v1/ai/advisor/conversations/{conversationId}`    | 查询单次对话详情            |
| 删除 AI 会话   | `DELETE /api/v1/ai/advisor/conversations/{conversationId}` | 删除会话                |
| 今日营养诊断     | `POST /api/v1/ai/advisor/today-diagnosis`                  | 根据今日摄入生成诊断          |
| 一周饮食诊断     | `POST /api/v1/ai/advisor/weekly-diagnosis`                 | 根据一周记录生成诊断          |
| 减脂晚餐推荐     | `POST /api/v1/ai/advisor/fat-loss-dinner`                  | 生成减脂晚餐建议            |
| 食物替代推荐     | `POST /api/v1/ai/advisor/food-alternatives`                | 推荐替代食物              |
| 慢性病饮食建议    | `POST /api/v1/ai/advisor/chronic-disease-advice`           | 根据慢性病生成建议           |
| 饮食风险提醒     | `POST /api/v1/ai/advisor/risk-warning`                     | 判断饮食风险              |
| 解释膳食计划     | `POST /api/v1/ai/advisor/explain-meal-plan/{planId}`       | 解释计划为什么这样安排         |
| 解释营养报表     | `POST /api/v1/ai/advisor/explain-report`                   | 对周报或月报生成解释          |
| 查询 AI 可用模型 | `GET /api/v1/ai/models`                                    | 查询 DeepSeek 模型配置    |
| AI 服务健康检查  | `GET /api/v1/ai/health`                                    | 检查 Python AI 服务是否可用 |

---

# 十四、AI 智能菜谱模块

| 接口名称          | 接口路径                                                      | 接口功能            |
| ------------- | --------------------------------------------------------- | --------------- |
| AI 生成原创菜谱     | `POST /api/v1/ai/recipes/generate`                        | 根据食材、口味、目标生成菜谱  |
| AI 生成减脂菜谱     | `POST /api/v1/ai/recipes/generate/fat-loss`               | 生成减脂菜谱          |
| AI 生成增肌菜谱     | `POST /api/v1/ai/recipes/generate/muscle-gain`            | 生成增肌菜谱          |
| AI 生成控糖菜谱     | `POST /api/v1/ai/recipes/generate/sugar-control`          | 生成控糖菜谱          |
| AI 生成一周菜谱     | `POST /api/v1/ai/recipes/week-plan`                       | 生成一周菜谱          |
| AI 估算菜谱营养     | `POST /api/v1/ai/recipes/nutrition-estimate`              | 估算 AI 菜谱营养      |
| AI 生成制作步骤     | `POST /api/v1/ai/recipes/cooking-steps`                   | 生成菜谱步骤          |
| AI 生成菜谱配图描述   | `POST /api/v1/ai/recipes/image-prompt`                    | 生成配图提示词         |
| AI 生成采购清单     | `POST /api/v1/ai/recipes/shopping-list`                   | 生成采购清单          |
| AI 生成一周膳食海报内容 | `POST /api/v1/ai/recipes/week-poster`                     | 生成海报内容          |
| 保存 AI 菜谱      | `POST /api/v1/ai/recipes/save`                            | 将 AI 菜谱保存到正式菜谱库 |
| 查询 AI 菜谱历史    | `GET /api/v1/ai/recipes/history`                          | 查询历史生成记录        |
| 删除 AI 菜谱记录    | `DELETE /api/v1/ai/recipes/history/{id}`                  | 删除生成记录          |
| AI 菜谱转膳食计划    | `POST /api/v1/ai/recipes/{aiRecipeId}/to-meal-plan`       | 加入膳食计划          |
| AI 菜谱转社区帖子    | `POST /api/v1/ai/recipes/{aiRecipeId}/share-to-community` | 分享到社区           |

---

# 十五、社区帖子模块

接口前缀：

```text
/api/v1/community
```

| 接口名称         | 接口路径                                                          | 接口功能           |
| ------------ | ------------------------------------------------------------- | -------------- |
| 发布帖子         | `POST /api/v1/community/posts`                                | 发布图文帖子         |
| 修改帖子         | `PUT /api/v1/community/posts/{postId}`                        | 修改自己的帖子        |
| 删除帖子         | `DELETE /api/v1/community/posts/{postId}`                     | 删除自己的帖子        |
| 查询帖子详情       | `GET /api/v1/community/posts/{postId}`                        | 查询帖子详情         |
| 分页查询帖子       | `GET /api/v1/community/posts`                                 | 查询社区帖子流        |
| 查询我的帖子       | `GET /api/v1/community/posts/me`                              | 查询我发布的帖子       |
| 查询用户帖子       | `GET /api/v1/community/users/{userId}/posts`                  | 查询某个用户发布的帖子    |
| 查询推荐帖子       | `GET /api/v1/community/posts/recommend`                       | 查询推荐帖子         |
| 查询热门帖子       | `GET /api/v1/community/posts/hot`                             | 查询热门帖子         |
| 查询最新帖子       | `GET /api/v1/community/posts/latest`                          | 查询最新帖子         |
| 查询关注用户帖子     | `GET /api/v1/community/posts/following`                       | 查询关注用户的帖子      |
| 增加帖子浏览量      | `POST /api/v1/community/posts/{postId}/view`                  | 记录浏览行为         |
| 查询帖子统计       | `GET /api/v1/community/posts/{postId}/statistics`             | 查询点赞、评论、收藏、浏览数 |
| 从菜谱发布帖子      | `POST /api/v1/community/posts/from-recipe/{recipeId}`         | 将菜谱分享为帖子       |
| 从膳食记录发布帖子    | `POST /api/v1/community/posts/from-meal-record/{recordId}`    | 将饮食记录分享为帖子     |
| 从 AI 菜谱发布帖子  | `POST /api/v1/community/posts/from-ai-recipe/{aiRecipeId}`    | 将 AI 菜谱分享为帖子   |
| 从图片识别结果发布帖子  | `POST /api/v1/community/posts/from-food-recognition/{taskId}` | 将识别结果分享为帖子     |
| 查询帖子关联菜谱     | `GET /api/v1/community/posts/{postId}/related-recipe`         | 查询帖子关联菜谱       |
| 查询帖子关联膳食记录   | `GET /api/v1/community/posts/{postId}/related-meal-record`    | 查询帖子关联膳食记录     |
| 查询帖子关联 AI 结果 | `GET /api/v1/community/posts/{postId}/related-ai-result`      | 查询帖子关联 AI 生成内容 |

---

# 十六、社区图片、点赞、收藏模块

## 1. 帖子图片接口

| 接口名称   | 接口路径                                              | 接口功能     |
| ------ | ------------------------------------------------- | -------- |
| 上传帖子图片 | `POST /api/v1/community/posts/images`             | 上传帖子配图   |
| 删除帖子图片 | `DELETE /api/v1/community/posts/images/{imageId}` | 删除图片     |
| 查询帖子图片 | `GET /api/v1/community/posts/{postId}/images`     | 查询帖子图片列表 |
| 设置封面图  | `PATCH /api/v1/community/posts/{postId}/cover`    | 设置帖子封面   |

## 2. 帖子点赞接口

| 接口名称     | 接口路径                                               | 接口功能      |
| -------- | -------------------------------------------------- | --------- |
| 点赞帖子     | `POST /api/v1/community/posts/{postId}/like`       | 点赞帖子      |
| 取消点赞     | `DELETE /api/v1/community/posts/{postId}/like`     | 取消点赞      |
| 查询点赞状态   | `GET /api/v1/community/posts/{postId}/like/status` | 查询我是否点赞   |
| 查询点赞用户   | `GET /api/v1/community/posts/{postId}/likes`       | 查询点赞用户列表  |
| 查询我点赞的帖子 | `GET /api/v1/community/posts/liked`                | 查询我点赞过的帖子 |

## 3. 帖子收藏接口

| 接口名称     | 接口路径                                                   | 接口功能       |
| -------- | ------------------------------------------------------ | ---------- |
| 收藏帖子     | `POST /api/v1/community/posts/{postId}/favorite`       | 收藏帖子       |
| 取消收藏     | `DELETE /api/v1/community/posts/{postId}/favorite`     | 取消收藏       |
| 查询收藏状态   | `GET /api/v1/community/posts/{postId}/favorite/status` | 查询我是否收藏    |
| 查询我收藏的帖子 | `GET /api/v1/community/posts/favorites`                | 查询收藏列表     |
| 查询收藏用户   | `GET /api/v1/community/posts/{postId}/favorites`       | 查询收藏该帖子的用户 |

---

# 十七、社区评论模块

评论建议只做两层：一级评论 + 二级回复。

| 接口名称     | 接口路径                                                     | 接口功能         |
| -------- | -------------------------------------------------------- | ------------ |
| 发表评论     | `POST /api/v1/community/posts/{postId}/comments`         | 对帖子发表评论      |
| 回复评论     | `POST /api/v1/community/comments/{commentId}/replies`    | 回复某条评论       |
| 删除评论     | `DELETE /api/v1/community/comments/{commentId}`          | 删除自己的评论      |
| 查询帖子评论   | `GET /api/v1/community/posts/{postId}/comments`          | 查询一级评论       |
| 查询评论回复   | `GET /api/v1/community/comments/{commentId}/replies`     | 查询二级回复       |
| 查询评论详情   | `GET /api/v1/community/comments/{commentId}`             | 查询评论详情       |
| 点赞评论     | `POST /api/v1/community/comments/{commentId}/like`       | 点赞评论         |
| 取消点赞评论   | `DELETE /api/v1/community/comments/{commentId}/like`     | 取消点赞         |
| 查询评论点赞状态 | `GET /api/v1/community/comments/{commentId}/like/status` | 查询是否点赞评论     |
| 查询我发表的评论 | `GET /api/v1/community/comments/me`                      | 查询我的评论       |
| 查询收到的评论  | `GET /api/v1/community/comments/received`                | 查询别人给我的评论和回复 |

---

# 十八、社区标签、搜索、举报模块

## 1. 标签接口

| 接口名称    | 接口路径                                                   | 接口功能      |
| ------- | ------------------------------------------------------ | --------- |
| 创建标签    | `POST /api/v1/community/tags`                          | 创建社区标签    |
| 查询标签列表  | `GET /api/v1/community/tags`                           | 查询标签      |
| 查询热门标签  | `GET /api/v1/community/tags/hot`                       | 查询热门标签    |
| 查询标签详情  | `GET /api/v1/community/tags/{tagId}`                   | 查询标签信息    |
| 查询标签下帖子 | `GET /api/v1/community/tags/{tagId}/posts`             | 查询某标签下的帖子 |
| 修改标签    | `PUT /api/v1/community/tags/{tagId}`                   | 修改标签      |
| 删除标签    | `DELETE /api/v1/community/tags/{tagId}`                | 删除标签      |
| 给帖子绑定标签 | `POST /api/v1/community/posts/{postId}/tags`           | 给帖子添加标签   |
| 移除帖子标签  | `DELETE /api/v1/community/posts/{postId}/tags/{tagId}` | 移除标签      |

## 2. 搜索接口

| 接口名称 | 接口路径                                 | 接口功能         |
| ---- | ------------------------------------ | ------------ |
| 搜索帖子 | `GET /api/v1/community/search/posts` | 搜索帖子标题、正文、标签 |
| 搜索用户 | `GET /api/v1/community/search/users` | 搜索社区用户       |
| 搜索标签 | `GET /api/v1/community/search/tags`  | 搜索标签         |
| 综合搜索 | `GET /api/v1/community/search`       | 综合搜索帖子、用户、标签 |

## 3. 举报接口

| 接口名称   | 接口路径                                                 | 接口功能     |
| ------ | ---------------------------------------------------- | -------- |
| 举报帖子   | `POST /api/v1/community/posts/{postId}/report`       | 举报违规帖子   |
| 举报评论   | `POST /api/v1/community/comments/{commentId}/report` | 举报违规评论   |
| 查询我的举报 | `GET /api/v1/community/reports/me`                   | 查询我的举报记录 |

---

# 十九、采购清单模块

| 接口名称       | 接口路径                                                             | 接口功能       |
| ---------- | ---------------------------------------------------------------- | ---------- |
| 创建采购清单     | `POST /api/v1/shopping-lists`                                    | 手动创建采购清单   |
| 根据膳食计划生成清单 | `POST /api/v1/shopping-lists/from-meal-plan/{planId}`            | 根据单日计划生成清单 |
| 根据一周计划生成清单 | `POST /api/v1/shopping-lists/from-week-plan`                     | 根据一周计划生成清单 |
| 查询采购清单列表   | `GET /api/v1/shopping-lists`                                     | 查询我的采购清单   |
| 查询采购清单详情   | `GET /api/v1/shopping-lists/{listId}`                            | 查询清单详情     |
| 修改采购清单     | `PUT /api/v1/shopping-lists/{listId}`                            | 修改清单       |
| 删除采购清单     | `DELETE /api/v1/shopping-lists/{listId}`                         | 删除清单       |
| 勾选已购买      | `PATCH /api/v1/shopping-lists/{listId}/items/{itemId}/checked`   | 标记已购买      |
| 取消已购买      | `PATCH /api/v1/shopping-lists/{listId}/items/{itemId}/unchecked` | 取消标记       |
| 导出采购清单     | `GET /api/v1/shopping-lists/{listId}/export`                     | 导出清单       |

---

# 二十、文件资源模块

| 接口名称     | 接口路径                                      | 接口功能       |
| -------- | ----------------------------------------- | ---------- |
| 上传通用文件   | `POST /api/v1/files/upload`               | 上传通用文件     |
| 上传用户头像   | `POST /api/v1/files/avatar`               | 上传头像       |
| 上传菜谱图片   | `POST /api/v1/files/recipe-image`         | 上传菜谱图片     |
| 上传膳食记录图片 | `POST /api/v1/files/meal-record-image`    | 上传饮食图片     |
| 上传帖子图片   | `POST /api/v1/files/community-post-image` | 上传社区帖子图片   |
| 查询文件信息   | `GET /api/v1/files/{fileId}`              | 查询文件信息     |
| 删除文件     | `DELETE /api/v1/files/{fileId}`           | 删除文件       |
| 查询文件访问地址 | `GET /api/v1/files/{fileId}/url`          | 获取文件访问 URL |

---

# 二十一、系统字典模块

| 接口名称     | 接口路径                                                | 接口功能          |
| -------- | --------------------------------------------------- | ------------- |
| 查询性别字典   | `GET /api/v1/dictionaries/genders`                  | 查询性别选项        |
| 查询餐次字典   | `GET /api/v1/dictionaries/meal-types`               | 查询早餐、午餐、晚餐、加餐 |
| 查询健康目标字典 | `GET /api/v1/dictionaries/health-goals`             | 查询减脂、增肌、控糖等   |
| 查询慢性病字典  | `GET /api/v1/dictionaries/chronic-diseases`         | 查询慢性病标签       |
| 查询过敏源字典  | `GET /api/v1/dictionaries/allergens`                | 查询常见过敏源       |
| 查询饮食禁忌字典 | `GET /api/v1/dictionaries/diet-restrictions`        | 查询饮食禁忌        |
| 查询食材分类字典 | `GET /api/v1/dictionaries/ingredient-categories`    | 查询食材分类        |
| 查询菜谱分类字典 | `GET /api/v1/dictionaries/recipe-categories`        | 查询菜谱分类        |
| 查询营养素字典  | `GET /api/v1/dictionaries/nutrients`                | 查询营养素         |
| 查询社区标签分类 | `GET /api/v1/dictionaries/community-tag-categories` | 查询社区标签分类      |

---

# 二十二、通知消息模块

这个模块用于社区点赞、评论、收藏、系统审核结果提醒。

| 接口名称    | 接口路径                                                | 接口功能    |
| ------- | --------------------------------------------------- | ------- |
| 查询我的通知  | `GET /api/v1/notifications`                         | 查询通知列表  |
| 查询未读通知数 | `GET /api/v1/notifications/unread-count`            | 查询未读数量  |
| 标记通知已读  | `PATCH /api/v1/notifications/{notificationId}/read` | 标记单条已读  |
| 全部标记已读  | `PATCH /api/v1/notifications/read-all`              | 全部已读    |
| 删除通知    | `DELETE /api/v1/notifications/{notificationId}`     | 删除通知    |
| 查询点赞通知  | `GET /api/v1/notifications/likes`                   | 查询点赞类通知 |
| 查询评论通知  | `GET /api/v1/notifications/comments`                | 查询评论类通知 |
| 查询收藏通知  | `GET /api/v1/notifications/favorites`               | 查询收藏类通知 |
| 查询系统通知  | `GET /api/v1/notifications/system`                  | 查询系统消息  |

---

# 二十三、管理后台接口

## 1. 用户管理

| 接口名称     | 接口路径                                                | 接口功能        |
| -------- | --------------------------------------------------- | ----------- |
| 查询用户列表   | `GET /api/v1/admin/users`                           | 管理员分页查询用户   |
| 查询用户详情   | `GET /api/v1/admin/users/{userId}`                  | 查询用户详情      |
| 禁用用户     | `PATCH /api/v1/admin/users/{userId}/disable`        | 禁用账号        |
| 启用用户     | `PATCH /api/v1/admin/users/{userId}/enable`         | 启用账号        |
| 重置用户密码   | `PATCH /api/v1/admin/users/{userId}/reset-password` | 管理员重置密码     |
| 查询用户健康档案 | `GET /api/v1/admin/users/{userId}/health-profile`   | 管理员查看用户健康档案 |
| 查询用户膳食记录 | `GET /api/v1/admin/users/{userId}/meal-records`     | 管理员查看用户饮食记录 |

## 2. 食材与菜谱管理

| 接口名称    | 接口路径                                                    | 接口功能   |
| ------- | ------------------------------------------------------- | ------ |
| 管理员查询食材 | `GET /api/v1/admin/ingredients`                         | 管理食材库  |
| 管理员审核食材 | `PATCH /api/v1/admin/ingredients/{ingredientId}/review` | 审核新增食材 |
| 管理员查询菜谱 | `GET /api/v1/admin/recipes`                             | 管理菜谱   |
| 管理员审核菜谱 | `PATCH /api/v1/admin/recipes/{recipeId}/review`         | 审核菜谱   |
| 管理员下架菜谱 | `PATCH /api/v1/admin/recipes/{recipeId}/offline`        | 下架菜谱   |
| 管理员恢复菜谱 | `PATCH /api/v1/admin/recipes/{recipeId}/online`         | 恢复菜谱   |

## 3. 社区管理

| 接口名称    | 接口路径                                                      | 接口功能   |
| ------- | --------------------------------------------------------- | ------ |
| 管理员查询帖子 | `GET /api/v1/admin/community/posts`                       | 查询所有帖子 |
| 管理员审核帖子 | `PATCH /api/v1/admin/community/posts/{postId}/review`     | 审核帖子   |
| 管理员下架帖子 | `PATCH /api/v1/admin/community/posts/{postId}/offline`    | 下架违规帖子 |
| 管理员恢复帖子 | `PATCH /api/v1/admin/community/posts/{postId}/online`     | 恢复帖子   |
| 管理员删除评论 | `DELETE /api/v1/admin/community/comments/{commentId}`     | 删除违规评论 |
| 管理员查询举报 | `GET /api/v1/admin/community/reports`                     | 查询举报列表 |
| 管理员处理举报 | `PATCH /api/v1/admin/community/reports/{reportId}/handle` | 处理举报   |
| 管理员管理标签 | `GET /api/v1/admin/community/tags`                        | 查询社区标签 |
| 管理员修改标签 | `PUT /api/v1/admin/community/tags/{tagId}`                | 修改标签   |
| 管理员删除标签 | `DELETE /api/v1/admin/community/tags/{tagId}`             | 删除标签   |

## 4. AI 管理

| 接口名称       | 接口路径                                                    | 接口功能                |
| ---------- | ------------------------------------------------------- | ------------------- |
| 查询 AI 配置   | `GET /api/v1/admin/ai/configs`                          | 查询 DeepSeek 和本地模型配置 |
| 修改 AI 配置   | `PUT /api/v1/admin/ai/configs`                          | 修改 AI 配置            |
| 查询 AI 调用日志 | `GET /api/v1/admin/ai/logs`                             | 查询 AI 调用记录          |
| 查询本地模型列表   | `GET /api/v1/admin/ai/local-models`                     | 查询食物分类模型            |
| 查询本地模型详情   | `GET /api/v1/admin/ai/local-models/{modelId}`           | 查询模型信息              |
| 启用本地模型     | `PATCH /api/v1/admin/ai/local-models/{modelId}/enable`  | 启用模型                |
| 停用本地模型     | `PATCH /api/v1/admin/ai/local-models/{modelId}/disable` | 停用模型                |
| 重新加载本地模型   | `POST /api/v1/admin/ai/local-models/{modelId}/reload`   | 重新加载模型              |
| 查询 AI 服务状态 | `GET /api/v1/admin/ai/health`                           | 检查 Python AI 服务状态   |

## 5. 系统管理

| 接口名称     | 接口路径                                            | 接口功能                  |
| -------- | ----------------------------------------------- | --------------------- |
| 查询后台首页数据 | `GET /api/v1/admin/dashboard`                   | 查询用户数、帖子数、菜谱数、AI 调用数等 |
| 查询系统配置   | `GET /api/v1/admin/system/configs`              | 查询系统配置                |
| 修改系统配置   | `PUT /api/v1/admin/system/configs/{configKey}`  | 修改系统参数                |
| 查询访问日志   | `GET /api/v1/admin/logs/access`                 | 查询访问日志                |
| 查询异常日志   | `GET /api/v1/admin/logs/error`                  | 查询异常日志                |
| 创建数据备份   | `POST /api/v1/admin/backups`                    | 创建数据库备份               |
| 查询备份记录   | `GET /api/v1/admin/backups`                     | 查询备份列表                |
| 恢复备份     | `POST /api/v1/admin/backups/{backupId}/restore` | 恢复备份                  |

---

# 二十四、Spring Boot 内部接口

这些接口不直接给前端使用，主要给 Python AI 服务或系统内部调用。

| 接口名称        | 接口路径                                                          | 接口功能                         |
| ----------- | ------------------------------------------------------------- | ---------------------------- |
| 查询用户 AI 上下文 | `GET /api/internal/v1/users/{userId}/ai-context`              | 查询健康档案、目标、禁忌、偏好              |
| 查询用户今日营养上下文 | `GET /api/internal/v1/users/{userId}/nutrition-context/today` | 查询今日饮食和营养统计                  |
| 查询用户一周营养上下文 | `GET /api/internal/v1/users/{userId}/nutrition-context/week`  | 查询一周饮食统计                     |
| 根据模型标签匹配食材  | `POST /api/internal/v1/ingredients/match-by-labels`           | 根据模型识别标签匹配食材库                |
| 根据模型标签匹配菜谱  | `POST /api/internal/v1/recipes/match-by-labels`               | 根据模型识别标签匹配菜谱                 |
| 内部营养计算      | `POST /api/internal/v1/nutrition/calculate`                   | 为 AI 服务提供营养计算                |
| 保存 AI 调用日志  | `POST /api/internal/v1/ai/logs`                               | 记录 AI 调用情况                   |
| 保存食物识别回调    | `POST /api/internal/v1/food-recognition/callback`             | Python 异步识别完成后回调 Spring Boot |
| 查询文件内部访问地址  | `GET /api/internal/v1/files/{fileId}/access-url`              | 给 Python 服务读取图片使用            |

---

# 二十五、Python AI 服务接口

Python AI 服务不直接暴露给前端，只给 Spring Boot 调用。

## 1. 基础与模型接口

| 接口名称       | 接口路径                                        | 接口功能             |
| ---------- | ------------------------------------------- | ---------------- |
| AI 服务健康检查  | `GET /ai/v1/health`                         | 检查 Python 服务是否可用 |
| 查询模型状态     | `GET /ai/v1/models/status`                  | 查询当前加载模型状态       |
| 查询食物分类模型信息 | `GET /ai/v1/models/food-classifier/info`    | 查询模型名称、版本、类别数    |
| 查询食物类别标签   | `GET /ai/v1/models/food-classifier/labels`  | 查询模型支持类别         |
| 重新加载分类模型   | `POST /ai/v1/models/food-classifier/reload` | 重新加载模型           |

## 2. 食物图片分类接口

| 接口名称     | 接口路径                                    | 接口功能              |
| -------- | --------------------------------------- | ----------------- |
| 食物图片分类   | `POST /ai/v1/food/classify`             | 接收图片并返回 Top-K 分类  |
| 批量图片分类   | `POST /ai/v1/food/batch-classify`       | 批量识别图片            |
| 图片分类并解释  | `POST /ai/v1/food/classify-and-explain` | 分类后调用 DeepSeek 解释 |
| 图片识别生成建议 | `POST /ai/v1/food/image-meal-advice`    | 分类、营养、健康档案综合建议    |

## 3. DeepSeek 调用接口

| 接口名称          | 接口路径                                  | 接口功能             |
| ------------- | ------------------------------------- | ---------------- |
| DeepSeek 普通问答 | `POST /ai/v1/llm/chat`                | 调用 DeepSeek 生成回复 |
| DeepSeek 流式问答 | `POST /ai/v1/llm/chat/stream`         | 流式生成回复           |
| 生成今日营养诊断      | `POST /ai/v1/llm/today-diagnosis`     | 根据结构化数据生成诊断      |
| 生成一周饮食建议      | `POST /ai/v1/llm/weekly-advice`       | 生成一周饮食建议         |
| 生成替代食物建议      | `POST /ai/v1/llm/food-alternatives`   | 生成替代食物           |
| 生成菜谱          | `POST /ai/v1/llm/recipe-generate`     | AI 生成菜谱          |
| 生成菜谱配图描述      | `POST /ai/v1/llm/recipe-image-prompt` | 生成图片描述           |
| 生成膳食海报文案      | `POST /ai/v1/llm/meal-poster`         | 生成海报内容           |

---

# 二十六、核心业务闭环

这套接口最终形成 5 条主线。

## 1. 健康档案到膳食计划

```text
用户注册登录
↓
填写健康档案
↓
设置目标、过敏源、慢性病、禁忌
↓
生成膳食计划
↓
营养分析
↓
生成建议
```

## 2. 食物图片识别到膳食记录

```text
用户上传食物图片
↓
Spring Boot 调用 Python 本地分类模型
↓
识别食物类别
↓
匹配食材库 / 菜谱库
↓
计算营养
↓
用户确认
↓
保存为膳食记录
```

## 3. 膳食记录到报表

```text
用户每日记录饮食
↓
系统计算每日营养
↓
生成周报 / 月报
↓
绘制趋势图
↓
AI 解释报表
```

## 4. AI 顾问闭环

```text
用户提问
↓
Spring Boot 查询健康档案和饮食记录
↓
构造结构化上下文
↓
Python 调用 DeepSeek
↓
返回个性化建议
↓
保存 AI 会话和调用日志
```

## 5. 社区分享闭环

```text
菜谱 / 膳食记录 / AI 菜谱 / 图片识别结果
↓
生成社区帖子
↓
用户点赞、评论、收藏
↓
形成健康饮食社区内容
↓
管理员审核和举报处理
```

---

# 二十七、推荐 Controller 划分

Spring Boot 后端建议这样划分 Controller：

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

---

# 二十八、权限设计建议

```text
无需登录：
- 注册
- 登录
- 刷新 Token
- 查询公开帖子
- 查询公开菜谱
- 查询公开用户主页

普通用户：
- 健康档案
- 膳食计划
- 膳食记录
- 食物图片识别
- AI 顾问
- AI 菜谱生成
- 发布帖子
- 点赞评论收藏
- 关注用户
- 查看个人报表

管理员：
- 用户管理
- 食材库管理
- 菜谱审核
- 社区内容审核
- 举报处理
- AI 配置管理
- 模型管理
- 系统配置
- 日志查看
- 数据备份
```

---