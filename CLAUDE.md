# Workspace Rules

前后端分离项目工作空间

# 技术规范

后端统一：

- SpringBoot 3.x
- MySQL 8
- Redis

前端统一：

- Vue3
- JavaScript
- Vite
- Pinia
- VueRouter
- Ant Design Vue

# 接口规范

- 统一接口风格
- 接口名称必须带模块前缀
- 示例：exerciseInfo/loadExerciseInfoList、exerciseInfo/getExerciseInfo

# 输出要求

- 所有代码必须可直接运行
- 禁止伪代码
- 禁止省略 import/require
- 优先复用现有模块、工具、枚举
- 新增文件必须明确标注完整路径

# 目录规则

- admin：后台管理接口
- web：用户端接口
- common：公共模块
- front-admin：管理后台前端
- front-web：用户端前端

# 开发原则

- 保持与现有代码风格完全一致
- 保持命名规范统一
- 严格分离 PO / DTO / VO
- 统一返回 ResponseVO<T>
ResponseVO<T> 统一返回结构:
{
    "status":"success",
    "code": 200,
    "info": "成功",
    "data": T
}
- 全局统一异常管理
# 禁止行为

- 禁止修改无关代码
- 禁止随意升级依赖
- 禁止破坏现有接口
- 禁止覆盖我手动修改的代码