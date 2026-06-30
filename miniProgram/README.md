# INDRAS 微信小程序

智能健康膳食管理系统移动端，对接 Spring Boot 后端 `/api/v1`，无 Mock。

## 目录结构

```
miniProgram/
├── app.js / app.json / app.wxss
├── config/index.js          # API 地址
├── api/endpoints.js         # 接口路径
├── constants/               # 枚举与中文标签
├── services/                # 业务 API 封装（与 Web 端 adapter 对齐）
├── utils/request.js         # wx.request + JWT
└── pages/                   # 页面
```

## 启动步骤

1. 启动 MySQL，导入 `backend/demo` 的 schema/data
2. 启动后端：`cd backend/demo && mvn spring-boot:run`（端口 8080）
3. 可选：启动 AI 服务（端口 8000，AI 菜谱解析需要）
4. 用微信开发者工具打开 `miniProgram/` 目录
5. 勾选 **详情 → 本地设置 → 不校验合法域名**
6. 演示账号：`alice` / `user123`

## 真机调试

将 `config/index.js` 中 `API_BASE_URL` 的 `localhost` 改为电脑局域网 IP，例如：

```js
const API_BASE_URL = 'http://192.168.1.100:8080/api/v1'
```

## Tab 页面

| Tab | 功能 |
|-----|------|
| 概览 | 今日营养、目标周期、风险预警 |
| 计划 | 生成/查看每日膳食计划 |
| 菜谱 | 搜索浏览菜谱 |
| 我的 | 健康档案、记录、营养、AI、社区等入口 |

## 接口契约

详见项目根目录 `docs/前后端接口契约.md`，字段适配逻辑与 `frontend/src/services/modules/` 保持一致。
