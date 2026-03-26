# Checklist

- [x] admin 表数据已迁移到 consumer 表
- [x] 迁移后数据完整性验证通过
- [x] system:admin:login 权限已添加到 permission 表
- [x] 管理员用户已分配 system:admin:login 权限
- [x] AuthService 修改完成，支持统一登录
- [x] ConsumerServiceImpl 修改完成
- [x] 管理员用户可正常登录后台
- [x] 无权限用户无法登录后台（返回"无后台访问权限"）
- [x] 前台用户登录不受影响
