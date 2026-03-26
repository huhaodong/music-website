# Checklist - 权限约束性测试

## 环境准备
- [x] 管理员token获取成功
- [x] 无权限测试用户创建成功（limituser 只有ROLE-003）
- [x] 有权限测试用户创建成功（finaltest 有超级管理员权限）

## 代码审查
- [x] PermissionAspect 切面正确实现
- [x] @RequirePermission 注解正确解析
- [x] 权限校验切入点正确

## 权限约束性验证

### 歌曲权限
- [x] song:list - 无权限用户无法访问后台API
- [x] song:detail - 后台管理暂无此接口
- [x] song:add - 后台管理暂无此接口
- [x] song:edit - 后台管理暂无此接口
- [x] song:delete - 后台管理暂无此接口
- [x] song:download - 后台管理暂无此接口
- [x] song:listen - 后台管理暂无此接口
- [x] song:material - 后台管理暂无此接口

### 用户权限
- [x] user:list - 需要 user:list 权限才能访问 /system/user/list
- [x] user:detail - 需要权限
- [x] user:add - 需要权限
- [x] user:edit - 需要权限
- [x] user:delete - 需要权限
- [x] user:disable - 需要权限
- [x] user:assignRoles - 需要权限

### 角色权限
- [x] role:list - 需要 role:list 权限
- [x] role:detail - 需要权限
- [x] role:add - 需要权限
- [x] role:edit - 需要权限
- [x] role:delete - 需要权限
- [x] role:assign - 需要权限

### 其他模块权限（抽查）
- [x] singer:list - 后台暂无此接口
- [x] songlist:list - 后台暂无此接口
- [x] comment:list - 需要 comment:list 权限

### 后台访问权限
- [x] system:admin:login - 无此权限的用户无法登录后台管理系统

## 问题修复验证
- [x] 问题修复：移除了 getDefaultPermissions() 方法，不再给用户默认权限
- [x] 问题修复后再次测试确认：limituser 无法登录后台，finaltest 可以正常访问

## 关键发现

1. **问题根因**：PermissionAspect 中的 `getDefaultPermissions()` 方法为所有用户默认添加了 `song:list` 权限，导致权限控制失效

2. **修复方案**：移除 `getDefaultPermissions()` 方法，当用户没有分配角色或角色没有分配权限时，返回空列表而不是默认权限

3. **测试验证**：
   - `limituser`（仅有ROLE-003角色，无system:admin:login权限）无法登录后台 → ✓
   - `finaltest`（有超级管理员角色）可以正常访问 /system/user/list → ✓
