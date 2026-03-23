# M0: 项目基线确认与TDD基础设施搭建

> **里程碑编号**: M0
> **预计周期**: 第1周（5个工作日）
> **前置依赖**: 无
> **核心目标**: 确认原项目可完整运行，搭建前后端测试框架，建立开发规范

---

## 1. 目标概述

M0 是整个二次开发的起点。在动任何业务代码之前，必须确认：
1. 原开源项目可以正常编译、启动、运行
2. 前后端测试框架已搭建就绪
3. 开发规范和分支策略已建立
4. 团队对现有代码结构有清晰认知

**原则：M0 结束时不改动任何业务代码，仅新增测试和配置。**

---

## 2. 开发任务清单

### 2.1 环境搭建与项目启动验证（Day 1-2）

| 编号 | 任务 | 交付物 | 工时 |
|------|------|--------|------|
| M0-01 | 克隆项目并搭建本地开发环境 | 可运行的本地环境 | 0.5天 |
| M0-02 | 配置 MySQL / Redis / MinIO 依赖服务 | docker-compose.dev.yml | 0.5天 |
| M0-03 | 后端 Spring Boot 编译启动验证 | 启动成功截图/日志 | 0.25天 |
| M0-04 | 前端 music-client 编译启动验证 | 启动成功截图 | 0.25天 |
| M0-05 | 前端 music-manage 编译启动验证 | 启动成功截图 | 0.25天 |
| M0-06 | 执行原项目核心功能冒烟测试 | 冒烟测试报告 | 0.25天 |

### 2.2 后端测试框架搭建（Day 2-3）

| 编号 | 任务 | 交付物 | 工时 |
|------|------|--------|------|
| M0-07 | 引入 JUnit 5 + Mockito 依赖 | pom.xml 更新 | 0.25天 |
| M0-08 | 配置 H2 内存数据库用于测试 | application-test.yml | 0.25天 |
| M0-09 | 编写第一个 Service 层单元测试（选取现有简单 Service） | SongServiceTest.java | 0.5天 |
| M0-10 | 引入 Spring MockMvc 配置，编写第一个 Controller 测试 | SongControllerTest.java | 0.5天 |
| M0-11 | 配置测试覆盖率报告工具（JaCoCo） | jacoco 配置 + 首次报告 | 0.25天 |
| M0-12 | 验证 `mvn test` 可正常执行全部测试 | 测试通过截图 | 0.25天 |

### 2.3 前端测试框架搭建（Day 3-4）

| 编号 | 任务 | 交付物 | 工时 |
|------|------|--------|------|
| M0-13 | music-client 引入 Vitest + Vue Test Utils | package.json + vitest.config.ts | 0.5天 |
| M0-14 | 编写第一个前端工具函数测试 | utils.test.ts | 0.25天 |
| M0-15 | 编写第一个 Vue 组件测试（选取简单组件） | Component.test.ts | 0.5天 |
| M0-16 | music-manage 引入 Vitest + Vue Test Utils | package.json + vitest.config.ts | 0.25天 |
| M0-17 | 验证 `npm run test` 可正常执行 | 测试通过截图 | 0.25天 |

### 2.4 开发规范建立（Day 4-5）

| 编号 | 任务 | 交付物 | 工时 |
|------|------|--------|------|
| M0-18 | 建立 Git 分支管理策略（Git Flow） | 分支说明文档 |  0.25天 |
| M0-19 | 配置 commit message 规范（commitlint） | commitlint 配置 | 0.25天 |
| M0-20 | 配置 ESLint / Prettier（前端） | .eslintrc + .prettierrc | 0.25天 |
| M0-21 | 建立 develop 分支，保护 main 分支 | Git 分支结构 | 0.25天 |
| M0-22 | 编写现有代码结构分析文档 | 代码结构说明.md | 0.5天 |
| M0-23 | 编写 TDD 开发流程规范文档 | TDD规范.md | 0.5天 |

---

## 3. TDD 实施细节

### 3.1 后端测试示例模板

```java
// RED: 先写失败测试
@SpringBootTest
class SongServiceTest {

    @MockBean
    private SongMapper songMapper;

    @Autowired
    private SongService songService;

    @Test
    @DisplayName("根据ID查询歌曲 - 歌曲存在时返回正确数据")
    void findById_whenSongExists_shouldReturnSong() {
        // Given
        Song mockSong = new Song();
        mockSong.setId(1);
        mockSong.setName("测试歌曲");
        when(songMapper.selectById(1)).thenReturn(mockSong);

        // When
        Song result = songService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals("测试歌曲", result.getName());
        verify(songMapper).selectById(1);
    }

    @Test
    @DisplayName("根据ID查询歌曲 - 歌曲不存在时返回null")
    void findById_whenSongNotExists_shouldReturnNull() {
        when(songMapper.selectById(999)).thenReturn(null);
        Song result = songService.findById(999);
        assertNull(result);
    }
}
```

### 3.2 前端测试示例模板

```typescript
// RED: 先写失败测试
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SongCard from '@/components/SongCard.vue'

describe('SongCard', () => {
  it('应正确显示歌曲名称', () => {
    const wrapper = mount(SongCard, {
      props: {
        song: { id: 1, name: '测试歌曲', singer: '测试歌手' }
      }
    })
    expect(wrapper.text()).toContain('测试歌曲')
  })

  it('点击卡片应触发 select 事件', async () => {
    const wrapper = mount(SongCard, {
      props: {
        song: { id: 1, name: '测试歌曲', singer: '测试歌手' }
      }
    })
    await wrapper.trigger('click')
    expect(wrapper.emitted('select')).toBeTruthy()
  })
})
```

---

## 4. 测试验收清单

### 4.1 环境验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M0-V01 | 后端可编译 | `mvn clean compile` 零错误 | 执行命令检查输出 | ☐ |
| M0-V02 | 后端可启动 | Spring Boot 启动无异常，端口监听正常 | 访问 `/actuator/health` | ☐ |
| M0-V03 | 前台可启动 | `npm run dev` 启动无错误 | 浏览器访问首页 | ☐ |
| M0-V04 | 后台可启动 | `npm run dev` 启动无错误 | 浏览器访问后台登录页 | ☐ |
| M0-V05 | 数据库连接正常 | 可正常读写数据 | 登录/查询操作 | ☐ |
| M0-V06 | Redis 连接正常 | 验证码功能正常 | 注册时获取验证码 | ☐ |
| M0-V07 | MinIO 连接正常 | 文件上传功能正常 | 上传一张图片 | ☐ |

### 4.2 冒烟测试验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M0-V08 | 后台管理员登录 | 使用默认管理员账号可登录后台 | 手动登录 | ☐ |
| M0-V09 | 前台用户注册 | 新用户可注册成功 | 注册新账号 | ☐ |
| M0-V10 | 前台用户登录 | 注册用户可正常登录 | 登录已注册账号 | ☐ |
| M0-V11 | 歌曲播放 | 歌曲可正常播放 | 选择一首歌播放 | ☐ |
| M0-V12 | 后台歌手管理 | CRUD 操作正常 | 增删改查歌手 | ☐ |
| M0-V13 | 后台歌曲管理 | CRUD 操作正常 | 增删改查歌曲 | ☐ |
| M0-V14 | 后台歌单管理 | CRUD 操作正常 | 增删改查歌单 | ☐ |

### 4.3 测试框架验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M0-V15 | 后端单元测试可运行 | `mvn test` 通过，至少1个测试用例 | 执行命令 | ☐ |
| M0-V16 | 后端 MockMvc 测试可运行 | Controller 测试通过 | 执行命令 | ☐ |
| M0-V17 | 后端覆盖率报告可生成 | JaCoCo 报告可查看 | 打开 HTML 报告 | ☐ |
| M0-V18 | 前端 music-client 测试可运行 | `npm run test` 通过 | 执行命令 | ☐ |
| M0-V19 | 前端 music-manage 测试可运行 | `npm run test` 通过 | 执行命令 | ☐ |

### 4.4 规范验收

| 编号 | 验收项 | 验收标准 | 验证方法 | 通过 |
|------|--------|----------|----------|------|
| M0-V20 | Git 分支结构已建立 | main + develop 分支存在 | `git branch -a` | ☐ |
| M0-V21 | Commit 规范已配置 | 不规范的 commit message 被拦截 | 提交不规范消息验证 | ☐ |
| M0-V22 | ESLint 已配置 | `npm run lint` 可执行 | 执行命令 | ☐ |
| M0-V23 | 代码结构文档已输出 | 文档内容完整 | 人工审阅 | ☐ |

---

## 5. 里程碑完成标志

**M0 完成的充要条件：**

- [  ] 原项目前后端可正常编译、启动、运行
- [  ] 冒烟测试全部通过（14项核心功能可用）
- [  ] 后端 JUnit 5 + Mockito + MockMvc 测试框架可运行
- [  ] 前端 Vitest + Vue Test Utils 测试框架可运行
- [  ] JaCoCo 覆盖率报告可生成
- [  ] Git Flow 分支策略已建立
- [  ] 开发规范文档已输出

**进入 M1 的前提：M0 全部验收项通过。**
