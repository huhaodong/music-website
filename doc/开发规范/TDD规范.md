# TDD 开发流程规范

## 1. TDD 概念说明

### 1.1 什么是 TDD

TDD（Test-Driven Development，测试驱动开发）是一种软件开发方法论，其核心思想是在编写功能代码之前先编写测试用例，通过测试来驱动设计的完善和功能的实现。

### 1.2 红-绿-蓝（Red-Green-Refactor）循环

TDD 的核心开发循环分为三个阶段：

| 阶段 | 颜色 | 描述 |
|------|------|------|
| Red（红） | 红灯 | 编写一个失败的测试用例，明确当前需要实现的功能 |
| Green（绿） | 绿灯 | 编写最简代码使测试通过，不追求完美，只求通过 |
| Refactor（蓝） | 蓝灯 | 重构代码，消除重复，提升代码质量，保持测试通过 |

```
┌─────────────────────────────────────────────────────────────┐
│                     TDD 开发循环                             │
│                                                             │
│    ┌─────────┐    编写测试    ┌─────────┐                   │
│    │   RED   │ ────────────→ │  GREEN  │                   │
│    │  失败测试 │               │  通过测试 │                   │
│    └─────────┘               └─────────┘                   │
│         ↑                          │                        │
│         │                          ▼                        │
│         │                   ┌─────────┐                     │
│         └─────────────────  │ REFACTOR│                     │
│              重构代码         │  重构优化  │                     │
│                              └─────────┘                     │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 TDD 优势

- **更早发现缺陷**：测试先行，问题在引入之初就被捕获
- **更好的设计**：由于需要先写测试，代码设计必须考虑可测试性
- **更可靠的回归测试**：每次修改后自动验证现有功能是否被破坏
- **更清晰的文档**：测试用例本身就是最好的功能文档
- **更快的迭代**：明确的目标，减少过度设计和返工

## 2. TDD 工作流程

### 2.1 整体开发流程

```
┌─────────────────────────────────────────────────────────────────┐
│                      TDD 整体工作流程                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 需求分析 ──→ 2. 编写测试 ──→ 3. 运行测试 ──→ 4. 编写代码     │
│       │              │              │             │            │
│       ▼              ▼              ▼             ▼            │
│  理解业务需求     RED: 测试失败   确认失败原因    GREEN: 最小实现 │
│                                                                 │
│  5. 重构优化 ──→ 6. 运行测试 ──→ 7. 提交代码 ──→ 8. 循环迭代     │
│       │              │              │             │            │
│       ▼              ▼              ▼             ▼            │
│  BLUE: 代码优化    确认全部通过    代码质量检查    进入下一功能   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 具体步骤

**步骤 1：理解需求**
- 仔细阅读需求文档和验收标准
- 明确输入、输出和边界条件
- 识别需要测试的场景

**步骤 2：编写测试（RED）**
- 为一个具体的小功能编写测试
- 测试必须能够编译和运行
- 验证测试在当前代码下失败（预期行为）

**步骤 3：运行测试确认失败**
- 执行测试，确认失败原因
- 失败信息应清晰指出问题所在
- 如果测试通过，说明功能已存在或实现不正确

**步骤 4：编写最小实现（GREEN）**
- 只编写让测试通过的最少代码
- 不追求完美，不添加额外功能
- 可以使用硬编码等临时方案

**步骤 5：重构代码（REFACTOR）**
- 消除重复代码
- 提升代码的可读性和可维护性
- 确保所有测试仍然通过

**步骤 6：循环迭代**
- 重复以上步骤，直到功能完成
- 逐步增加测试覆盖的场景

**步骤 7：提交代码**
- 运行完整的测试套件
- 检查代码覆盖率
- 提交代码

## 3. 后端 TDD 规范

### 3.1 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 2.6.2 |
| 测试框架 | JUnit 4 + Spring Boot Test | 4.13.1 |
| 断言库 | AssertJ | 内置 |
| Mock 框架 | Mockito | Spring Boot 内置 |
| 内存数据库 | H2 | 2.1.214 |
| 覆盖率工具 | Jacoco | 0.8.8 |
| 构建工具 | Maven | - |

### 3.2 测试命名规范

后端测试类和方法命名遵循以下规范：

**测试类命名**
```
[模块]ServiceTest        // Service 层测试
[模块]ControllerTest     // Controller 层测试（集成测试）
[模块]MapperTest         // Mapper 层测试
```

**测试方法命名**
```
[方法名]_should[预期行为]_when[条件]

示例：
- songOfId_shouldReturnSong_whenIdExists
- deleteSong_shouldReturnError_whenIdNotFound
- updateSongMsg_shouldSuccess_whenValidRequest
```

**常用命名模式**
| 模式 | 说明 | 示例 |
|------|------|------|
| shouldReturnXWhenY | 条件Y下应返回X | shouldReturnSongWhenIdExists |
| shouldThrowXWhenY | 条件Y下应抛出X异常 | shouldThrowExceptionWhenInvalidInput |
| shouldX | 应该X | shouldReturnEmptyList |
| shouldNotX | 不应该X | shouldNotThrowException |

### 3.3 测试结构规范

**Service 层测试结构**
```java
@SpringBootTest
class SongServiceTest {

    @MockBean
    private SongMapper songMapper;

    @MockBean
    private MinioClient minioClient;

    @Autowired
    private SongService songService;

    private Song testSong;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    @Test
    void methodName_shouldExpectedResult_whenCondition() {
        // Given: 准备测试数据
        when(songMapper.selectList(any())).thenReturn Collections.singletonList(testSong));

        // When: 执行被测试方法
        R result = songService.allSong();

        // Then: 验证结果
        assertNotNull(result);
        assertTrue(result.getSuccess());
        verify(songMapper, times(1)).selectList(any());
    }
}
```

**Controller 层测试结构**
```java
@SpringBootTest
@AutoConfigureMockMvc
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void endpoint_shouldExpectedResult() throws Exception {
        mockMvc.perform(get("/song")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }
}
```

### 3.4 测试数据管理

**使用 @BeforeEach 准备测试数据**
```java
@BeforeEach
void setUp() {
    testSong = new Song();
    testSong.setId(1);
    testSong.setName("Test Song");
    testSong.setSingerId(1);
    // ...
}
```

**使用 @Nested 实现测试分组**
```java
@Nested
@DisplayName("歌曲查询测试")
class SongQueryTests {

    @Test
    void allSong_shouldReturnAllSongs() {
        // ...
    }
}

@Nested
@DisplayName("歌曲删除测试")
class SongDeleteTests {

    @Test
    void deleteSong_shouldSuccess_whenSongExists() {
        // ...
    }
}
```

### 3.5 常见测试场景

| 场景 | 测试点 |
|------|--------|
| 增删改查 | 成功、失败（不存在）、失败（参数错误） |
| 分页查询 | 首页、末页、中间页、空页 |
| 边界条件 | 空值、零值、最大值、负数 |
| 异常处理 | 业务异常、系统异常 |
| 权限验证 | 有权限、无权限 |

## 4. 前端 TDD 规范

### 4.1 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 | 3.2.13 |
| 测试框架 | Vitest | 4.0.16 |
| 组件测试 | @vue/test-utils | 2.4.6 |
| 环境 | jsdom | 29.0.0 |
| 断言库 | Chai（内置于 Vitest） | - |

### 4.2 测试文件位置

```
src/
├── utils/
│   ├── index.ts
│   └── index.test.ts        # 工具函数测试
├── components/
│   └── MyComponent.vue
│   └── MyComponent.test.ts  # 组件测试（可选）
└── views/
    └── MyView.vue
    └── MyView.test.ts       # 视图测试（可选）
```

**测试文件命名**
```
[模块名].test.ts      # 工具函数测试
[组件名].test.ts      # 组件测试
```

### 4.3 测试命名规范

**使用 describe/it 结构**
```typescript
describe('模块名称', () => {
  describe('子模块或方法', () => {
    it('should预期行为 when条件', () => {
      // ...
    })
  })
})
```

**示例**
```typescript
describe('utils functions', () => {
  describe('getBirth', () => {
    it('should return empty string for null value', () => {
      expect(getBirth(null)).toBe('')
    })

    it('should format date correctly', () => {
      const date = new Date('2020-05-15')
      expect(getBirth(date.getTime())).toBe('2020-05-15')
    })
  })

  describe('formatSeconds', () => {
    it('should format seconds less than a minute', () => {
      expect(formatSeconds(45)).toBe('0:45')
    })
  })
})
```

### 4.4 工具函数测试规范

**纯函数测试**
```typescript
import { functionName } from './index'

describe('functionName', () => {
  it('should return expectedResult when condition', () => {
    const result = functionName(input)
    expect(result).toBe(expectedValue)
  })

  it('should return expectedResult when null input', () => {
    const result = functionName(null)
    expect(result).toBe('')
  })
})
```

### 4.5 组件测试规范

**基础组件测试**
```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import MyComponent from './MyComponent.vue'

describe('MyComponent', () => {
  it('should render correctly', () => {
    const wrapper = mount(MyComponent)
    expect(wrapper.text()).toContain('expected text')
  })

  it('should emit event when clicked', async () => {
    const wrapper = mount(MyComponent)
    await wrapper.find('button').trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
  })
})
```

## 5. 测试覆盖率要求

### 5.1 覆盖率指标

| 指标 | 要求 | 说明 |
|------|------|------|
| 行覆盖率 | ≥ 60% | 代码行数被执行的比例 |
| 分支覆盖率 | ≥ 50% | 条件分支被执行的比例 |
| 方法覆盖率 | ≥ 70% | 类方法被执行的比例 |

### 5.2 覆盖率配置

**Maven 配置（后端）**

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**运行覆盖率报告**
```bash
mvn test
# 报告位置: target/site/jacoco/index.html
```

**Vitest 配置（前端）**

Vitest 默认支持覆盖率收集，可通过配置启用：

```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      reportsDirectory: './coverage'
    }
  }
})
```

**运行前端测试**
```bash
npm run test
# 覆盖率报告: coverage/index.html
```

### 5.3 覆盖率检查

- **提交前检查**：本地运行测试，确保覆盖率达标
- **CI 检查**：持续集成中运行测试，覆盖率不达标则失败
- **定期审查**：定期审查覆盖率报告，识别测试薄弱区域

## 6. 测试工具链说明

### 6.1 后端工具

| 工具 | 用途 | 命令 |
|------|------|------|
| JUnit | 单元测试框架 | `mvn test` |
| Mockito | Mock 对象创建 | 内置于 Spring Boot Test |
| H2 | 内存数据库测试 | 自动配置 |
| Jacoco | 测试覆盖率 | `mvn test`（自动生成） |
| Spring Test | 集成测试 | `mvn test` |
| MockMvc | Web 层测试 | @AutoConfigureMockMvc |

### 6.2 前端工具

| 工具 | 用途 | 命令 |
|------|------|------|
| Vitest | 测试运行器 | `npm run test` |
| @vue/test-utils | Vue 组件测试 | - |
| jsdom | DOM 环境模拟 | 自动配置 |
| Chai | 断言库 | 内置 |

### 6.3 运行命令

**后端测试**
```bash
cd music-server

# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=SongServiceTest

# 生成覆盖率报告
mvn test
# 查看报告: target/site/jacoco/index.html
```

**前端测试**
```bash
cd music-client

# 运行所有测试
npm run test

# 监听模式（文件变化时自动运行）
npm run test -- --watch

# 生成覆盖率报告
npm run test -- --coverage
# 查看报告: coverage/index.html
```

### 6.4 测试配置文件

**后端测试配置**
```
src/test/resources/
├── application-test.yml      # 测试专用配置
└── application.yml           # 主配置（包含测试 profile）
```

**H2 内存数据库配置示例**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
```

## 7. TDD 实践检查清单

### 7.1 编写测试前
- [ ] 理解需求和验收标准
- [ ] 识别测试场景（正常、边界、异常）
- [ ] 确定测试数据

### 7.2 编写测试时
- [ ] 测试命名清晰描述预期行为
- [ ] 使用 Given-When-Then 结构
- [ ] 测试之间相互独立
- [ ] 避免测试实现细节

### 7.3 编写代码时
- [ ] 只编写让测试通过的最少代码
- [ ] 不添加未测试的功能
- [ ] 保持代码简洁

### 7.4 重构时
- [ ] 保持测试通过
- [ ] 消除重复代码
- [ ] 提升代码可读性

### 7.5 提交前
- [ ] 所有测试通过
- [ ] 覆盖率达标
- [ ] 代码通过 lint 检查

## 8. 参考资料

- [JUnit 5 文档](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 文档](https://site.mockito.org/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Vitest 文档](https://vitest.dev/)
- [Vue Test Utils 文档](https://vue-test-utils.vuejs.org/)
