# 权限约束性测试 Spec

## Why

当前权限系统已重构为原子权限列表，但用户反馈权限开通和不开通没有变化。需要通过测试验证权限是否真正具有约束能力。

## What Changes

1. **测试所有原子权限的约束性** - 验证每个权限在开通和不开通时的行为差异
2. **检查 PermissionAspect 切面逻辑** - 确认权限校验是否正确执行
3. **验证权限校验流程** - 从请求到响应的完整链路

## Impact

- 测试范围：所有41个原子权限
- 涉及代码：
  - `PermissionAspect.java` - 权限校验切面
  - `RequirePermission.java` - 权限注解
  - `AuthService.java` - 登录时权限加载
  - 各Controller的 `@RequirePermission` 注解

## 测试策略

### 测试方法
1. 创建测试用户（无任何权限）
2. 创建测试用户（开通特定权限）
3. 调用对应API验证权限校验是否生效

### 测试权限清单（按模块分组）

#### 歌曲相关 (8个)
| 权限编码 | 测试场景 |
|----------|----------|
| song:list | 无权限访问 /song/list |
| song:detail | 无权限访问 /song/detail |
| song:add | 无权限访问 /song/add |
| song:edit | 无权限访问 /song/update |
| song:delete | 无权限访问 /song/delete |
| song:download | 无权限访问下载接口 |
| song:listen | 无权限访问试听接口 |
| song:material | 无权限访问物料接口 |

#### 歌手相关 (6个)
| 权限编码 | 测试场景 |
|----------|----------|
| singer:list | 无权限访问 /singer/list |
| singer:detail | 无权限访问 /singer/detail |
| singer:add | 无权限访问 /singer/add |
| singer:edit | 无权限访问 /singer/update |
| singer:delete | 无权限访问 /singer/delete |

#### 歌单相关 (7个)
| 权限编码 | 测试场景 |
|----------|----------|
| songlist:list | 无权限访问 /songlist/list |
| songlist:detail | 无权限访问 /songlist/detail |
| songlist:add | 无权限访问 /songlist/add |
| songlist:edit | 无权限访问 /songlist/update |
| songlist:delete | 无权限访问 /songlist/delete |
| songlist:collect | 无权限访问收藏接口 |

#### 用户相关 (7个)
| 权限编码 | 测试场景 |
|----------|----------|
| user:list | 无权限访问 /user/list |
| user:detail | 无权限访问 /user/detail |
| user:add | 无权限访问 /user/add |
| user:edit | 无权限访问 /user/update |
| user:delete | 无权限访问 /user/delete |
| user:disable | 无权限访问禁用接口 |
| user:assignRoles | 无权限访问分配角色接口 |

#### 角色相关 (7个)
| 权限编码 | 测试场景 |
|----------|----------|
| role:list | 无权限访问 /role/list |
| role:detail | 无权限访问 /role/detail |
| role:add | 无权限访问 /role/add |
| role:edit | 无权限访问 /role/update |
| role:delete | 无权限访问 /role/delete |
| role:assign | 无权限访问分配权限接口 |

#### 组织相关 (6个)
| 权限编码 | 测试场景 |
|----------|----------|
| org:list | 无权限访问 /organization/list |
| org:detail | 无权限访问 /organization/detail |
| org:add | 无权限访问 /organization/add |
| org:edit | 无权限访问 /organization/update |
| org:delete | 无权限访问 /organization/delete |

#### 评论相关 (3个)
| 权限编码 | 测试场景 |
|----------|----------|
| comment:list | 无权限访问 /comment/list |
| comment:add | 无权限访问 /comment/add |
| comment:delete | 无权限访问 /comment/delete |

#### 系统相关 (2个)
| 权限编码 | 测试场景 |
|----------|----------|
| system:config | 无权限访问系统配置接口 |
| system:log | 无权限访问日志接口 |

## 预期结果

### 有权限时
- API 正常返回数据

### 无权限时
- API 返回错误信息（无权限访问）
- HTTP 状态码 403 或业务错误码

## 验证检查点

1. PermissionAspect 是否正确拦截无权限请求
2. 注解 `@RequirePermission` 是否正确解析
3. 用户登录时权限列表是否正确加载
4. 权限变更后是否需要重新登录才生效
