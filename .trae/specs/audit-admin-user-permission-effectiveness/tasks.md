# Tasks

- [x] Task 1: 复现与证据固化“admin 添加用户权限不足”
  - [x] SubTask 1.1: 用 `admin` 账号复现 `/system/user/add` 权限不足并保存请求/响应证据
  - [x] SubTask 1.2: 记录当前运行实例的 profile、数据源与鉴权上下文，排除“连错环境”干扰

- [x] Task 2: 审计角色权限分配链路并输出矩阵
  - [x] SubTask 2.1: 核查 `user_role`、`role_permission`、`permission` 三表关联
  - [x] SubTask 2.2: 输出关键账号（admin/测试账号）“账号-角色-权限码”矩阵
  - [x] SubTask 2.3: 对照接口注解权限码，标注缺失/冲突项

- [x] Task 3: 审计并修复权限生效链路
  - [x] SubTask 3.1: 检查 `PermissionAspect` 的用户类型识别与权限聚合逻辑
  - [x] SubTask 3.2: 修复权限编码不一致或角色解析错误导致的拒绝
  - [x] SubTask 3.3: 确认修复后“有权限放行、无权限拒绝”行为稳定

- [x] Task 4: 编写并执行权限模块测试代码
  - [x] SubTask 4.1: 新增/修复“用户新增权限”自动化测试（正向+反向）
  - [x] SubTask 4.2: 增加“赋权后生效”回归测试（角色变更后重新鉴权）
  - [x] SubTask 4.3: 执行测试并输出失败->修复->通过的结果证据

- [x] Task 5: 全量回归与交付说明
  - [x] SubTask 5.1: 回归组织、角色、用户管理相关受保护接口
  - [x] SubTask 5.2: 提供修复摘要、风险点、后续数据治理建议（权限编码统一）

# Task Dependencies
- Task 2 依赖 Task 1
- Task 3 依赖 Task 2
- Task 4 依赖 Task 3
- Task 5 依赖 Task 4
