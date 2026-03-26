# Checklist

## 数据库迁移
- [ ] Flyway配置已添加到application.properties
- [ ] V3_create_organization_table.sql迁移可正常执行
- [ ] V4_create_role_permission_tables.sql迁移可正常执行
- [ ] role, permission, user_role, role_permission, organization表已创建

## API测试
- [ ] UserManage.test.ts测试通过
- [ ] RoleManage.test.ts测试通过
- [ ] PermissionManage.test.ts测试通过
- [ ] OrganizationManage.test.ts测试通过

## 代码修复
- [ ] SystemManager API路径与后端一致
- [ ] 前端页面可正常加载数据
