# 项目启动任务列表

## 任务 1: 环境检查

- [x] 1.1: 检查 MySQL 服务是否运行
  - 验证 MySQL 服务状态
  - 确认数据库 `tp_music` 存在
  
- [x] 1.2: 检查 Redis 服务是否运行
  - 验证 Redis 服务状态
  - 确认可以连接

## 任务 2: 启动后端服务

- [x] 2.1: 进入 music-server 目录
  - 执行 `cd music-server`
  
- [x] 2.2: 编译后端项目
  - 执行 `mvn clean compile`
  - 确保编译无错误
  
- [x] 2.3: 启动后端服务
  - 执行 `mvn spring-boot:run`
  - 服务应在端口 8888 启动
  - 确认 Flyway 迁移执行成功
  - 确认无异常错误

## 任务 3: 启动前台客户端

- [x] 3.1: 进入 music-client 目录
  - 执行 `cd music-client`
  
- [x] 3.2: 安装依赖（如需要）
  - 执行 `npm install`
  - 确保安装无错误
  
- [x] 3.3: 启动前台服务
  - 执行 `npm run serve`
  - 服务应在端口 8080 启动

## 任务 4: 启动后台管理端

- [x] 4.1: 进入 music-manage 目录
  - 执行 `cd music-manage`
  
- [x] 4.2: 安装依赖（如需要）
  - 执行 `npm install`
  - 确保安装无错误
  
- [x] 4.3: 启动后台服务
  - 执行 `npm run serve`
  - 服务应在端口 8081 启动

## 任务 5: 验证服务状态

- [x] 5.1: 验证后端 API 可访问
  - 访问 http://localhost:8888
  - 确认服务响应正常
  
- [x] 5.2: 验证前台客户端可访问
  - 访问 http://localhost:8080
  - 确认首页正常显示
  
- [x] 5.3: 验证后台管理端可访问
  - 访问 http://localhost:8081
  - 确认登录页正常显示

## Task Dependencies
- [2.x] 依赖 [1.x] 环境检查通过
- [3.x] 依赖 [2.x] 后端服务启动
- [4.x] 依赖 [2.x] 后端服务启动
- [5.x] 依赖 [3.x] 和 [4.x]
