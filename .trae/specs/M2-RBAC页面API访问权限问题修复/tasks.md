# Tasks

## 1. [Done] 检查并修复前端 API 请求配置
- [x] 1.1: 检查 `request.ts` 中的 baseURL 配置是否以 "/" 结尾
- [x] 1.2: 检查 `index.ts` 中的 API 路径是否以 "/" 开头
- [x] 1.3: 确保组合后的 URL 不会出现 "//" 双斜杠

## 2. [Done] 检查后端权限注解配置
- [x] 2.1: 检查 UserController 的权限注解是否正确
- [x] 2.2: 检查 RoleController 的权限注解是否正确
- [x] 2.3: 检查 OrganizationController 的权限注解是否正确
- [x] 2.4: 检查 PermissionController 的权限注解是否正确

## 3. [Done] 修复 JwtAuthenticationFilter
- [x] 3.1: 在 JWT 验证成功时设置 session 属性
- [x] 3.2: 确保 PermissionAspect 能正确识别用户已登录

## 4. [Pending] 验证修复效果
- [ ] 4.1: 登录后测试用户管理页面是否正常
- [ ] 4.2: 登录后测试角色管理页面是否正常
- [ ] 4.3: 登录后测试组织管理页面是否正常
- [ ] 4.4: 登录后测试权限管理页面是否正常

# Task Dependencies
- Task 2-3 依赖 Task 1 完成
- Task 4 依赖 Task 1-3 完成
