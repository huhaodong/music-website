# Tasks

- [x] Task 1: 创建统一用户管理页面 UnifiedUserPage.vue
  - [x] SubTask 1.1: 从 ConsumerPage.vue 复制表格列结构（ID、头像、用户名、性别、手机、邮箱、生日、签名、地区）
  - [x] SubTask 1.2: 从 UserManage.vue 复制昵称、角色、状态、创建时间列
  - [x] SubTask 1.3: 从 UserManage.vue 复制添加/编辑用户功能（对话框、表单、验证）
  - [x] SubTask 1.4: 从 UserManage.vue 复制角色分配功能
  - [x] SubTask 1.5: 从 UserManage.vue 复制搜索和角色筛选功能
  - [x] SubTask 1.6: 从 ConsumerPage.vue 复制头像更新功能（上传组件）
  - [x] SubTask 1.7: 从 ConsumerPage.vue 复制收藏跳转功能
  - [x] SubTask 1.8: 整合批量删除和单个删除功能

- [x] Task 2: 修改路由配置 router/index.ts
  - [x] SubTask 2.1: 将 UserManage.vue 路由指向新的统一页面
  - [x] SubTask 2.2: 移除 /Consumer 路由（或保留重定向）

- [x] Task 3: 修改侧边栏 YinAside.vue
  - [x] SubTask 3.1: 将 /Consumer 菜单项移除
  - [x] SubTask 3.2: 确保系统管理下的用户管理指向 /Home/user

- [x] Task 4: 删除 ConsumerPage.vue
  - [x] SubTask 4.1: 确认功能已迁移后删除文件

# Task Dependencies
- Task 2 依赖 Task 1（统一页面创建后才能修改路由）
- Task 3 依赖 Task 1
- Task 4 依赖 Task 1、Task 2、Task 3
