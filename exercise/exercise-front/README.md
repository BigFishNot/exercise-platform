# exercise-front

个人健身运动平台前端，两个独立工程，互不共享代码。

| 工程                    | 端口  | 后端代理   | token header  | 说明     |
| ----------------------- | ----- | ---------- | ------------- | -------- |
| `exercise-front-admin`  | 5174  | :8081/api  | `adminToken`  | 管理后台 |
| `exercise-front-web`    | 5175  | :8082/api  | `studentToken` | 用户端   |

## 启动

```bash
# 1. 启动后端 admin (8081) / web (8082)
# 2. 进入任一前端工程安装依赖并启动
cd exercise-front-admin
npm install
npm run dev

cd ../exercise-front-web
npm install
npm run dev
```

## 规范

- 不使用 TypeScript
- 接口请求统一走 `src/utils/request.js`，禁止页面 / 组件直接 import axios
- token 走 header 注入；登录态用 localStorage + Pinia
- 路径形式 `/<模块名>/<动作>`，baseURL `/api`
- 管理后台：搜索区 → 表格 → 分页三段式，弹窗独立组件，二次确认，状态 Tag
- 用户端：卡片 / 列表布局，禁用后台表格 UI