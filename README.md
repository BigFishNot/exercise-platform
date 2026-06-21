# vibe-coding

个人项目工作空间。仓库下分多个独立子项目，按目录树组织。

## 📁 目录

```
vibe-coding/
├── exercise/                       # 个人健身运动平台（主项目）
│   ├── CLAUDE.md                   # 项目级 AI 协作规范
│   ├── exercise-java/              # 后端（Spring Boot 3 多模块）
│   └── exercise-front/             # 前端（两个独立 Vite 工程）
└── README.md                       # 本文件
```

> 更多子项目请按需新建目录，并各自维护 `CLAUDE.md` 与 `README.md`。

---

## 🏃 exercise：个人健身运动平台

> 详细规范见 [exercise/CLAUDE.md](exercise/CLAUDE.md)。
> 前后端分离，**后端双服务**（管理端 + 用户端），**前端双工程**（互不共享代码）。

### 技术栈

| 层 | 选型 |
| --- | --- |
| 后端 | Spring Boot 3.x · MyBatis（自定义 Mapper）· MySQL 8 · Redis |
| 前端 | Vue 3 · JavaScript · Vite · Pinia · Vue Router · Ant Design Vue 4 |

### 模块组成

```
exercise/
├── exercise-java/                  # 后端（端口 8081 admin / 8082 web）
│   ├── exercise-common/            # PO/DTO/VO/Query、Service/Mapper、Redis、异常、枚举
│   ├── exercise-admin/             # 管理端（端口 8081）
│   ├── exercise-web/               # 用户端（端口 8082）
│   ├── ddl/exercise.sql            # 数据库初始化
│   └── pom.xml                     # Maven 父 POM
└── exercise-front/                 # 前端
    ├── exercise-front-admin/       # 管理后台（端口 5174）
    └── exercise-front-web/         # 用户端（端口 5175）
```

### 接口规范

- 统一上下文：`/api`（由 `server.servlet.context-path` 提供）
- 路径形式：`/api/<模块名>/<动作>`，如 `/api/userInfo/login`
- 统一返回 `ResponseVO<T>`：`{ status, code, info, data }`
- 管理端 token header：`adminToken`
- 用户端 token header：`studentToken`

### 启动

```bash
# 1. 初始化数据库
mysql -uroot -p < exercise/exercise-java/ddl/exercise.sql

# 2. 启动后端（两个独立进程）
cd exercise/exercise-java
mvn -pl exercise-admin -am spring-boot:run   # 8081
mvn -pl exercise-web   -am spring-boot:run   # 8082

# 3. 启动前端（两个独立工程）
cd exercise/exercise-front/exercise-front-admin
npm install && npm run dev                    # http://localhost:5174

cd exercise/exercise-front/exercise-front-web
npm install && npm run dev                    # http://localhost:5175
```

### 默认账号

| 端 | 账号 | 密码 | 入口 |
| --- | --- | --- | --- |
| 管理后台 | `admin` | `admin123` | http://localhost:5174/login |
| 用户端 | 注册产生 | 注册时设置 | http://localhost:5175/register |

---

## 🤝 协作约定

- **AI 协作**：每个子项目根目录放 `CLAUDE.md`，AI 工具启动时优先读取
- **代码风格**：见各子项目 `CLAUDE.md` 的"禁止行为"与"开发原则"
- **提交规范**：见下方
- **环境配置**：`application-dev.yml` 等本地配置可提交，生产环境 `application-prod.yml` 永远不入库

## 📝 Git 提交规范

> 后续业务代码按"**一功能一提交**"。

格式（参考 Conventional Commits）：

```
<type>(<scope>): <subject>

<body>

<footer>
```

| type | 用途 | 示例 |
| --- | --- | --- |
| `feat` | 新功能 | `feat(exercisePlan): 新增阶段计划创建 / 取消 / 当前计划查询` |
| `fix` | 修复 bug | `fix(login): 修复用户端登录态过期后未跳转登录页` |
| `refactor` | 重构 | `refactor(common): 提取 ResponseCodeEnum 错误码集中管理` |
| `style` | 样式 / UI | `style(admin): 用户列表 KPI 卡 + 表格升级` |
| `docs` | 文档 | `docs: 补充邮件模块业务规则` |
| `chore` | 杂项 / 脚手架 | `chore: 初始化前后端工程骨架` |
| `test` | 测试 | `test(user): 补充注册参数校验单元测试` |

scope（可选）建议：模块名（`userInfo` / `exercisePlan` / `exerciseType` / `mailConfig` …）或子工程名（`admin` / `web` / `front-admin` / `front-web` / `common`）。

> **不要**把多个互不相关的功能塞进一次提交；每次只做"一件事"。

## 📄 License

Private & Internal Use Only.
