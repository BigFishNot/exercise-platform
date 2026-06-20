# exercise 项目规则

个人健身运动平台，前后端分离，后端拆成两个独立服务（管理端 + 用户端），各自带独立拦截器与登录态。

# 项目结构

后端 `exercise-java/`（Maven 多模块）：

- `exercise-common`：公共模块，包路径 `com.exercise.campus`。承载 PO / DTO / VO / Query、Service / Mapper、Redis 组件、异常、枚举、工具类。
- `exercise-admin`：管理端服务，包路径 `com.exercise.admin`。承载 Controller、Biz（业务编排）、管理端专用 DTO/VO。独立启动，运行在管理端端口。
- `exercise-web`：用户端服务，包路径 `com.exercise.web`。承载 Controller、Biz、用户端专用 DTO/VO。独立启动，运行在用户端端口。

依赖方向：`admin → common`、`web → common`。common 不允许反向依赖任何端。

前端 `exercise-front/`：

- `exercise-admin`：管理后台前端工程。
- `exercise-web`：用户端前端工程。

> 包名提醒：common 用 `com.exercise.campus`，admin 用 `com.exercise.admin`，web 用 `com.exercise.web`。新增类必须落到正确的包路径，不要混。

# 系统角色

| 角色 | 编码 | 登录入口 | 能力边界 |
| --- | --- | --- | --- |
| 系统管理员 | `ADMIN`（`roleType = 1`） | 管理端 `/admin/login` | 维护用户、运动类型、邮件配置 / 模板、邮件日志；查看统计；手动触发邮件。**不能**修改用户的身体数据 / 运动记录 / 阶段计划。 |
| 普通用户 | `USER`（`roleType = 2`） | 用户端 `/user/login` 或 `/user/register` | 注册账号、维护个人资料、创建阶段计划、选择运动类型完成打卡、查看趋势图、调用 AI 鼓励接口。**不能**访问任何管理端接口。 |

- 角色字段约定在 `UserInfo.roleType`，枚举名 `RoleTypeEnum`。
- 两端登录态相互隔离，token header 不同（`adminToken` / `studentToken`），管理端拦截器仅放行 `roleType = ADMIN`，用户端拦截器仅放行 `roleType = USER`。
- 系统初始化时落一个超管账号（账号 `admin` / 密码在 DDL 初始化脚本里设置，首次登录强制改密）。其余用户全部从用户端注册产生，**管理端不开放新增用户入口**。

# 接口与路径

- 全局上下文：`/api`，由 `server.servlet.context-path` 提供，路径中**不再重复**写 `admin` / `web`。
- 路径形式：`/api/<模块名>/<动作>`，模块名小驼峰；常用动作：`loadDataList`、`add`、`update`、`delete`、`detail`、`getXxxOptions`（下拉用）。
- 管理接口归 admin 模块、用户接口归 web 模块；两端互不复用 Controller。

# 鉴权与权限

- 管理端 token 走 header `adminToken`，由管理端登录拦截器校验，并配合 Redis 登录组件做有效期管理。
- 用户端 token 走 header `studentToken`，由用户端登录拦截器校验。
- 管理端接口必须打权限注解，权限编码与系统菜单表中的编码一一对应；权限不通过则抛业务异常。
- 当前登录用户统一从登录上下文持有器（ThreadLocal）取，禁止在 Controller / Service 里再次手动解析 token。

# 核心业务域

模块名按接口路径使用，统一小驼峰；同一域同时出现在两端时，按下表分配归属。

| 业务域 | 模块名 | 归属端 | 用途 |
| --- | --- | --- | --- |
| 用户与登录 | `userInfo` | web（注册 / 登录 / 个人资料）/ admin（用户列表 / 详情 / 启停） | 用户身份、个人资料、身高体重、BMI |
| 阶段计划 | `exercisePlan` | web（创建 / 修改 / 取消 / 查询当前计划）/ admin（按用户查） | 用户选择一个日期范围制定打卡计划，并设定每日目标时长 |
| 运动类型 | `exerciseType` | admin（CRUD + 启停 + 排序）/ web（下拉选项 + 详情） | 室内运动类型字典：自由运动 / 跳绳 / 俯卧撑 / 平板支撑 / 仰卧起坐 / 深蹲 / 开合跳 / 卷腹 / 拉伸 等 |
| 运动记录 | `exerciseRecord` | web（创建 / 结束 / 放弃 / 当日列表） | 用户在倒计时页发起一次运动并完成落库 |
| 打卡判定 | `exerciseCheckIn` | web（当日打卡状态 + 阶段日历）/ admin（按用户查阶段打卡情况） | 根据当日累计有效时长判定 `NOT_DONE` / `INSUFFICIENT` / `DONE` |
| 减肥目标 | `weightGoal` | web | 用户的目标体重 / 目标体脂率 / 目标日期 / 备注；同一时刻仅一个生效目标 |
| 身体数据 | `bodyData` | web（每日记录 / 历史曲线） | 身高、体重、BMI；BMI = 体重(kg) / 身高(m)² 后端计算并落库 |
| AI 鼓励 | `aiEncourage` | web（身体数据页触发） | 调用外部大模型生成鼓励语；限频 |
| 邮件配置 | `mailConfig` | admin | SMTP 配置 + 每日发送时间点列表（多时间点） |
| 邮件模板 | `mailTemplate` | admin | 模板分两类：`NOT_DONE`（未打卡）、`INSUFFICIENT`（时长不足）；变量占位符 |
| 邮件日志 | `mailLog` | admin | 所有发送记录（定时 + 手动）持久化，可按用户 / 日期 / 模板查询 |
| 手动发送 | `mailSend` | admin | 管理员对单用户 / 多用户选择模板发送，受去重约束 |
| 统计趋势 | `statistics` | web（个人）/ admin（平台总览） | 按周 / 月 / 阶段聚合：体重、BMI、每日运动时长、完成率 |

通用枚举（common 模块）至少包含：`RoleTypeEnum`、`PlanStatusEnum`、`CheckInStatusEnum`、`MailTemplateTypeEnum`、`MailSendStatusEnum`、`ExerciseRecordStatusEnum`、`BodyDataSourceEnum`、`MailTriggerTypeEnum`（定时 / 手动）。

# 业务规则

## 用户与登录

- 注册入口**仅用户端**开放：账号（手机号 / 邮箱）、昵称、密码、性别、出生年月；昵称 2–20 字符，密码 8–32 位且至少含字母与数字，BCrypt 存储。
- 注册成功自动以 `USER` 角色登录并写登录态。
- 用户名 / 邮箱在系统内唯一，重复注册返回业务错误码（如 `USER_ALREADY_EXISTS`）。
- 个人资料字段：头像、昵称、性别、出生年月、身高（cm，0–300）、体重（kg，0–500），用户可随时修改。
- 用户端资料接口对外暴露必要字段即可；管理端用户详情可看注册时间、最近登录时间、累计打卡天数等汇总字段。

## 阶段计划（exercisePlan）

- 字段：开始日期、结束日期、每日目标时长（分钟，整数 1–600）、备注、状态。
- 校验：
  - 开始日期 ≥ 当日；结束日期 > 开始日期；区间跨度 1–60 天。
  - 单用户同一时刻**只能有一个**状态为 `进行中(ONGOING)` 的计划；新建前必须把已有计划 `取消(CANCELED)` 或 `结束(FINISHED)`。
  - 修改仅允许改"每日目标时长"与备注；区间锁定后不允许变更日期。
- 状态机：`ONGOING` → `FINISHED`（到达结束日期且定时任务归档）/ `CANCELED`（用户主动取消）；终态不可逆。
- 阶段结束时同步冻结当日及之后的打卡判定，不再触发未打卡邮件。

## 运动类型（exerciseType）

- 字段：名称、图标、默认倒计时（秒）、可选时长档位（JSON 数组 `[{label, seconds}]`，如 `1min / 3min / 5min / 自定义`）、每分钟消耗卡路里参考值、是否启用、排序权重。
- 仅室内运动，不接户外类目（屏蔽户外标签作为前端筛选兜底）。
- 删除前校验是否被运动记录引用；被引用时只能停用（`status = 0`），不能硬删。
- 用户端只读取 `status = 1` 的类型；管理端列表支持按启用 / 停用筛选。

## 运动记录（exerciseRecord）

- 用户在前端选择运动类型 → 选预设时长档或自定义倒计时（10–7200 秒） → 前端驱动倒计时，到点结束调用"完成"接口。
- 服务端校验：
  - 同一用户同一时刻只能有一条 `进行中(IN_PROGRESS)` 的运动记录。
  - "完成"接口要求 `实际时长 ≥ 计划时长 × 0.95`（允许前后 5% 误差，前端到点时间与本地时钟会有偏差）；不满足则记录为 `放弃(ABANDONED)`，不计入打卡。
  - 倒计时中途用户点"放弃"：直接落 `ABANDONED`。
- 字段：用户 ID、运动类型 ID、计划时长（秒）、实际时长（秒）、消耗卡路里（= 实际时长 / 60 × 每分钟消耗参考）、运动日期（按服务器时区 GMT+8 落到 `yyyy-MM-dd`）、开始时间、结束时间、状态、备注。
- 同一用户同一天允许多次运动记录；当日累计 = `SUM(实际时长)` 仅统计 `DONE` 状态的记录。

## 打卡判定（exerciseCheckIn）

- 判定口径：**当日累计 `DONE` 状态实际时长 ≥ 当日目标时长 → 已打卡 `DONE`**；有运动但不足 → `INSUFFICIENT`；无任何运动 → `NOT_DONE`。
- 触发时机：
  - 用户每完成一条运动记录 → 实时重算当日打卡状态。
  - 每日凌晨定时任务补齐所有 `ONGOING` 阶段计划用户前一日的判定结果，避免跨天后漏判。
- 阶段日历接口返回用户在阶段区间内每天的判定结果 + 当日累计时长 + 完成率。

## 减肥目标（weightGoal）

- 字段：目标体重（kg）、目标体脂率（可选，%）、目标日期、备注、状态。
- 同一时刻只能有一个 `生效中(ACTIVE)` 目标；新建目标时把旧目标置为 `ARCHIVED`。
- 目标日期 ≥ 当日；到达目标日期后由定时任务自动归档，并解锁可新建下一个目标。

## 身体数据（bodyData）

- 用户每天可记录一次体重（kg，精确到 0.1）；身高仅在个人资料修改。
- 落库时由后端按当前身高计算 BMI 并冗余存储，趋势图直接读取落库值，避免每次现算。
- 趋势图维度：阶段内（按日）、最近 7 天、最近 30 天、自定义区间。

## AI 鼓励

- 用户在身体数据页 / 阶段计划页可点"给我一点鼓励"调用接口。
- 限频：每用户每天最多 3 次；后端用 Redis 计数（key：`ai:encourage:{userId}:{yyyyMMdd}`，TTL 到次日凌晨）。
- 调用失败（超时 / 限流 / 第三方错误）时降级到内置静态鼓励语池，**不**抛业务异常；前端不区分失败。
- 不持久化生成内容；纯读接口。

## 邮件配置 / 模板（mailConfig / mailTemplate）

- `mailConfig` 单例配置：SMTP host / port / 用户名 / 密码（密文存储）/ 发件人 / TLS 开关；每日发送时间点（HH:mm，多值，逗号分隔，至少 1 个、至多 6 个）。
- `mailTemplate` 一条记录 = 一个模板，分类枚举 `MailTemplateTypeEnum`：
  - `NOT_DONE`（今日未打卡）
  - `INSUFFICIENT`（今日时长不足）
- 模板字段：标题、内容（富文本，支持占位符 `{nickName}` / `{date}` / `{actualMinutes}` / `{targetMinutes}` / `{planStartDate}` / `{planEndDate}`）、是否启用。
- 修改模板走"保存即生效"；历史已发送邮件不受影响（快照存在 `mailLog.renderedContent`）。

## 定时发送邮件

- 定时任务按 `mailConfig.sendTimePoints` 在每个时间点扫描：
  1. 取所有 `ONGOING` 的阶段计划；
  2. 对每个用户计算今日打卡状态；
  3. 状态为 `NOT_DONE` → 渲染 `NOT_DONE` 模板；状态为 `INSUFFICIENT` → 渲染 `INSUFFICIENT` 模板；
  4. 命中规则后再做去重：若 `mailLog` 中存在 (userId, templateType, sendDate) 的成功记录 → 跳过；
  5. 通过 SMTP 发送，写 `mailLog`（`triggerType = SCHEDULED`，`status = SUCCESS / FAILED`，失败记录异常摘要）。
- 同一用户同一模板**每天最多发送一次**，跨日重置。

## 手动发送邮件

- 管理端选择模板 + 一个或多个用户 + 备注，点击"立即发送"。
- 同样走 `mailSend` Biz，先校验去重再发送；命中"今日已发"的用户在响应里返回被跳过的列表，由前端提示。
- 手动触发也要写 `mailLog`，`triggerType = MANUAL`，并记录操作管理员 ID。

## 统计 / 趋势

- 个人统计：阶段完成率（已打卡天数 / 阶段天数）、日均运动时长、体重 vs 目标体重对比、BMI 走势。
- 平台总览（admin）：注册用户数、活跃用户数（近 7 天有运动记录）、邮件发送成功率、热门运动类型 Top 5。
- 聚合统一在 Service 层用 SQL 完成，避免在内存里循环聚合。

## 接口归属清单

| 端 | 典型接口 |
| --- | --- |
| web  | `userInfo/register`、`userInfo/login`、`userInfo/getProfile`、`userInfo/updateProfile`、`exercisePlan/add`、`exercisePlan/updateDailyTarget`、`exercisePlan/cancel`、`exercisePlan/getCurrent`、`exercisePlan/getCalendar`、`exerciseType/getOptions`、`exerciseType/detail`、`exerciseRecord/start`、`exerciseRecord/finish`、`exerciseRecord/abandon`、`exerciseRecord/listToday`、`exerciseCheckIn/getToday`、`exerciseCheckIn/getCalendar`、`weightGoal/add`、`weightGoal/getActive`、`weightGoal/archive`、`bodyData/add`、`bodyData/getTrend`、`aiEncourage/generate`、`statistics/personalSummary` |
| admin | `userInfo/loadDataList`、`userInfo/detail`、`userInfo/updateStatus`、`exerciseType/loadDataList`、`exerciseType/add`、`exerciseType/update`、`exerciseType/delete`、`exerciseType/getOptions`、`mailConfig/get`、`mailConfig/update`、`mailTemplate/loadDataList`、`mailTemplate/add`、`mailTemplate/update`、`mailTemplate/delete`、`mailLog/loadDataList`、`mailLog/resend`、`mailSend/send`、`statistics/platformOverview` |

# 字段与返回约定

- 后端统一返回 `ResponseVO<T>`，不直接返回 Entity / PO。
- 分页参数继承统一基类，分页结果使用统一的分页结果 VO；前后端分页字段保持一致。
- 数据库字段下划线、Java 字段驼峰，由 MyBatis 配置自动映射；**前端字段保持与后端驼峰一致**，禁止前端起别名。
- 时间字段统一格式化为 `yyyy-MM-dd HH:mm:ss`（GMT+8），禁止返回时间戳。
- 主键：业务实体多为 String UUID（如课程 ID / 章节 ID / 课时 ID / 资源 ID），基础数据多为自增整数。

# 禁止行为

- 禁止前后端字段不一致或私自重命名 PO 字段。
- 禁止生成 mock 数据，联调以真实接口为准。
- 禁止管理端接口不带权限注解。
- 禁止用户端接口暴露管理端字段（创建人、排序权重、审核状态等）。
- 禁止把仅一端使用的类放进 common 模块。
- 禁止 Controller / Biz / Service / Mapper 越层（Controller 不能直接调 Mapper，Mapper 不能反查 Service 等）。

# 子规则索引

- 后端通用：`exercise-java/CLAUDE.md`
- 前端通用：`exercise-front/CLAUDE.md`
- 各子模块 / 前端工程根目录下的 `AGENTS.md` 为补充约束。