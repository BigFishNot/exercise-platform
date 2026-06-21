# exercise-java

个人健身运动平台后端，Spring Boot 3 + MyBatis + MySQL 8 + Redis。

## 模块

| 模块            | 包路径                  | 职责                                                         | 端口  |
| --------------- | ----------------------- | ------------------------------------------------------------ | ----- |
| exercise-common | `com.exercise.campus.*` | PO / DTO / VO / Query、Service / Mapper、Redis 组件、异常、枚举、工具类 | —     |
| exercise-admin  | `com.exercise.admin.*`  | 管理端 Controller / Biz                                      | 8081  |
| exercise-web    | `com.exercise.web.*`    | 用户端 Controller / Biz                                      | 8082  |

## 运行

```bash
# 1. 准备 MySQL / Redis
# 2. 执行 ddl/exercise.sql 初始化数据库
# 3. 启动
mvn -pl exercise-admin -am spring-boot:run
mvn -pl exercise-web   -am spring-boot:run
```

## 路径规范

- 全局上下文：`/api`（server.servlet.context-path）
- 模块路径：`/api/<模块名>/<动作>`，如 `/api/userInfo/login`
- 管理端 token header：`adminToken`
- 用户端 token header：`studentToken`