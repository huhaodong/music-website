# Checklist

- [x] 数据库迁移脚本创建正确，添加 nickname 字段
- [x] ConsumerRequest 包含 nickname 字段
- [x] ConsumerServiceImpl.addUser() 正确生成默认昵称（格式：默认用户001）
- [x] ConsumerServiceImpl.updateUserMsg() 可以更新昵称（通过 BeanUtils.copyProperties）
- [x] Personal.vue 模板正确展示昵称（大字）和用户名（小字灰色）
- [x] 旧用户（无昵称）访问个人页面不报错，显示默认昵称（使用 || 运算符兜底）
- [x] 新用户注册后访问个人页面正确显示默认昵称
