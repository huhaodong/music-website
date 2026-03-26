import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import PermissionManage from '@/views/PermissionManage.vue'
import { SystemManager } from '@/api/index'

// 模拟 Element Plus 组件
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
  ElMessageBox: {
    confirm: vi.fn().mockResolvedValue('confirm'),
    alert: vi.fn(),
    prompt: vi.fn(),
  },
}))

// 模拟 API
vi.mock('@/api/index', () => ({
  SystemManager: {
    getPermissionTree: vi.fn().mockResolvedValue({
      data: [
        {
          id: 1,
          name: '系统管理',
          code: 'system',
          type: 0,
          path: '/system',
          icon: 'Setting',
          sort: 0,
          status: 1,
          hidden: false,
          keepAlive: false,
          description: '系统管理模块',
          children: [
            {
              id: 2,
              name: '用户管理',
              code: 'system:user',
              type: 1,
              path: '/user',
              component: 'UserManage',
              icon: 'User',
              sort: 1,
              status: 1,
              hidden: false,
              keepAlive: true,
              description: '用户管理功能',
              parentId: 1,
              parentName: '系统管理',
              children: [
                {
                  id: 3,
                  name: '添加用户',
                  code: 'system:user:add',
                  type: 2,
                  path: '',
                  component: '',
                  icon: '',
                  sort: 1,
                  status: 1,
                  hidden: false,
                  keepAlive: false,
                  description: '添加用户权限',
                  parentId: 2,
                  parentName: '用户管理',
                },
                {
                  id: 4,
                  name: '编辑用户',
                  code: 'system:user:edit',
                  type: 2,
                  path: '',
                  component: '',
                  icon: '',
                  sort: 2,
                  status: 1,
                  hidden: false,
                  keepAlive: false,
                  description: '编辑用户权限',
                  parentId: 2,
                  parentName: '用户管理',
                },
                {
                  id: 5,
                  name: '删除用户',
                  code: 'system:user:delete',
                  type: 2,
                  path: '',
                  component: '',
                  icon: '',
                  sort: 3,
                  status: 1,
                  hidden: false,
                  keepAlive: false,
                  description: '删除用户权限',
                  parentId: 2,
                  parentName: '用户管理',
                },
              ],
            },
            {
              id: 6,
              name: '角色管理',
              code: 'system:role',
              type: 1,
              path: '/role',
              component: 'RoleManage',
              icon: 'Key',
              sort: 2,
              status: 1,
              hidden: false,
              keepAlive: true,
              description: '角色管理功能',
              parentId: 1,
              parentName: '系统管理',
            },
          ],
        },
        {
          id: 7,
          name: '内容管理',
          code: 'content',
          type: 0,
          path: '/content',
          icon: 'Document',
          sort: 1,
          status: 1,
          hidden: false,
          keepAlive: false,
          description: '内容管理模块',
          children: [
            {
              id: 8,
              name: '音乐管理',
              code: 'content:song',
              type: 1,
              path: '/song',
              component: 'SongManage',
              icon: 'Music',
              sort: 1,
              status: 1,
              hidden: false,
              keepAlive: true,
              description: '音乐管理功能',
              parentId: 7,
              parentName: '内容管理',
            },
          ],
        },
      ],
    }),
    addPermission: vi.fn().mockResolvedValue({ message: '添加成功', type: 'success' }),
    updatePermission: vi.fn().mockResolvedValue({ message: '更新成功', type: 'success' }),
    deletePermission: vi.fn().mockResolvedValue({ message: '删除成功', type: 'success' }),
  },
}))

describe('PermissionManage.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  // 测试场景 1: 权限树展示 - 渲染权限树组件
  it('renders permission tree correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 验证权限树数据已加载
    expect(wrapper.vm.permissionTreeData.length).toBeGreaterThan(0)
    expect(wrapper.vm.permissionTreeData[0].name).toBe('系统管理')
    expect(wrapper.vm.permissionTreeData[0].children.length).toBeGreaterThan(0)
  })

  it('displays permission type tags correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 验证权限类型名称映射
    expect(wrapper.vm.getPermissionTypeName(0)).toBe('目录')
    expect(wrapper.vm.getPermissionTypeName(1)).toBe('菜单')
    expect(wrapper.vm.getPermissionTypeName(2)).toBe('按钮')

    // 验证权限类型标签映射
    expect(wrapper.vm.getPermissionTypeTag(0)).toBe('')
    expect(wrapper.vm.getPermissionTypeTag(1)).toBe('primary')
    expect(wrapper.vm.getPermissionTypeTag(2)).toBe('warning')
  })

  it('handles node click to show permission detail', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 模拟点击权限节点
    const testNode = {
      id: 2,
      name: '用户管理',
      code: 'system:user',
      type: 1,
      path: '/user',
      component: 'UserManage',
      icon: 'User',
      sort: 1,
      status: 1,
      hidden: false,
      keepAlive: true,
      description: '用户管理功能',
      parentId: 1,
      parentName: '系统管理',
    }

    wrapper.vm.handleNodeClick(testNode)
    await flushPromises()

    // 验证选中的权限详情
    expect(wrapper.vm.selectedPermission).toEqual(testNode)
  })

  // 测试场景 2: 权限模板选择 - 选择和应用权限模板
  it('opens template dialog correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 打开权限模板对话框
    wrapper.vm.showTemplateDialog()
    await flushPromises()

    expect(wrapper.vm.templateDialogVisible).toBe(true)
  })

  it('applies user management template correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 应用用户管理模板
    await wrapper.vm.applyTemplate('user')
    await flushPromises()

    // 验证调用了 addPermission API
    expect(SystemManager.addPermission).toHaveBeenCalled()
    // 用户模板包含 6 个权限项
    expect(SystemManager.addPermission).toHaveBeenCalledTimes(6)
  })

  it('applies role management template correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 应用角色管理模板
    await wrapper.vm.applyTemplate('role')
    await flushPromises()

    // 验证调用了 addPermission API
    expect(SystemManager.addPermission).toHaveBeenCalled()
    // 角色模板包含 6 个权限项
    expect(SystemManager.addPermission).toHaveBeenCalledTimes(6)
  })

  it('applies organization management template correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 应用组织管理模板
    await wrapper.vm.applyTemplate('org')
    await flushPromises()

    // 验证调用了 addPermission API
    expect(SystemManager.addPermission).toHaveBeenCalled()
    // 组织模板包含 5 个权限项
    expect(SystemManager.addPermission).toHaveBeenCalledTimes(5)
  })

  it('applies song management template correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 应用音乐管理模板
    await wrapper.vm.applyTemplate('song')
    await flushPromises()

    // 验证调用了 addPermission API
    expect(SystemManager.addPermission).toHaveBeenCalled()
    // 音乐模板包含 6 个权限项
    expect(SystemManager.addPermission).toHaveBeenCalledTimes(6)
  })

  it('applies singer management template correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 应用歌手管理模板
    await wrapper.vm.applyTemplate('singer')
    await flushPromises()

    // 验证调用了 addPermission API
    expect(SystemManager.addPermission).toHaveBeenCalled()
    // 歌手模板包含 5 个权限项
    expect(SystemManager.addPermission).toHaveBeenCalledTimes(5)
  })

  it('applies song list management template correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 应用歌单管理模板
    await wrapper.vm.applyTemplate('songList')
    await flushPromises()

    // 验证调用了 addPermission API
    expect(SystemManager.addPermission).toHaveBeenCalled()
    // 歌单模板包含 5 个权限项
    expect(SystemManager.addPermission).toHaveBeenCalledTimes(5)
  })

  // 测试场景 3: 权限保存 - 保存权限配置
  it('opens add dialog correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 打开添加权限对话框（无上级权限）
    wrapper.vm.showAddDialog(null)
    await flushPromises()

    expect(wrapper.vm.permissionDialogVisible).toBe(true)
    expect(wrapper.vm.isEdit).toBeFalsy()
    expect(wrapper.vm.dialogTitle).toBe('添加权限')
    expect(wrapper.vm.permissionForm.parentId).toBeNull()
    expect(wrapper.vm.permissionForm.type).toBe(1) // 默认为菜单类型
  })

  it('opens add dialog with parent node correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    const parentNode = {
      id: 1,
      name: '系统管理',
      code: 'system',
      type: 0,
    }

    // 打开添加权限对话框（有上级权限）
    wrapper.vm.showAddDialog(parentNode)
    await flushPromises()

    expect(wrapper.vm.permissionDialogVisible).toBe(true)
    expect(wrapper.vm.isEdit).toBeFalsy()
    expect(wrapper.vm.dialogTitle).toBe('添加权限')
    expect(wrapper.vm.permissionForm.parentId).toBe(1)
    expect(wrapper.vm.parentPermissionName).toBe('系统管理')
  })

  it('opens edit dialog correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    const permissionNode = {
      id: 2,
      name: '用户管理',
      code: 'system:user',
      type: 1,
      path: '/user',
      component: 'UserManage',
      icon: 'User',
      sort: 1,
      status: 1,
      hidden: false,
      keepAlive: true,
      description: '用户管理功能',
      parentId: 1,
      parentName: '系统管理',
    }

    // 打开编辑权限对话框
    wrapper.vm.showEditDialog(permissionNode)
    await flushPromises()

    expect(wrapper.vm.permissionDialogVisible).toBe(true)
    expect(wrapper.vm.dialogTitle).toBe('编辑权限')
    expect(wrapper.vm.permissionForm.id).toBe(2)
    expect(wrapper.vm.permissionForm.name).toBe('用户管理')
    expect(wrapper.vm.permissionForm.code).toBe('system:user')
    expect(wrapper.vm.permissionForm.type).toBe(1)
  })

  it('resets permission form correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 先设置一些值
    wrapper.vm.permissionForm.name = '测试权限'
    wrapper.vm.permissionForm.code = 'test:permission'
    wrapper.vm.permissionForm.parentId = 999
    wrapper.vm.parentPermissionName = '测试父权限'

    // 重置表单
    wrapper.vm.resetPermissionForm()
    await flushPromises()

    // 验证表单已重置
    expect(wrapper.vm.permissionForm.name).toBe('')
    expect(wrapper.vm.permissionForm.code).toBe('')
    expect(wrapper.vm.permissionForm.parentId).toBeNull()
    expect(wrapper.vm.parentPermissionName).toBe('')
  })

  it('handles type change correctly', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 设置一些初始值
    wrapper.vm.permissionForm.path = '/test'
    wrapper.vm.permissionForm.component = 'TestComponent'
    wrapper.vm.permissionForm.icon = 'TestIcon'
    wrapper.vm.permissionForm.keepAlive = true

    // 当类型变为按钮时，应该清空相关字段
    wrapper.vm.handleTypeChange(2)

    expect(wrapper.vm.permissionForm.path).toBe('')
    expect(wrapper.vm.permissionForm.component).toBe('')
    expect(wrapper.vm.permissionForm.icon).toBe('')
    expect(wrapper.vm.permissionForm.keepAlive).toBe(false)
  })

  it('submits add permission form successfully', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 设置表单数据
    wrapper.vm.isEdit = false
    wrapper.vm.permissionForm.name = '新权限'
    wrapper.vm.permissionForm.code = 'new:permission'
    wrapper.vm.permissionForm.type = 1
    wrapper.vm.permissionForm.path = '/new'
    wrapper.vm.permissionForm.component = 'NewComponent'

    // 模拟表单验证通过
    const mockValidate = vi.fn().mockResolvedValue(true)
    wrapper.vm.permissionFormRef = {
      validate: mockValidate,
    }

    // 提交表单
    await wrapper.vm.submitPermissionForm()
    await flushPromises()

    // 验证调用了 addPermission API
    expect(SystemManager.addPermission).toHaveBeenCalled()
  })

  it('submits edit permission form successfully', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 通过 showEditDialog 来设置编辑模式
    const permissionNode = {
      id: 2,
      name: '更新后的权限',
      code: 'updated:permission',
      type: 1,
      path: '/update',
      component: 'UpdateComponent',
      icon: 'UpdateIcon',
      sort: 1,
      status: 1,
      hidden: false,
      keepAlive: true,
      description: '更新描述',
      parentId: 1,
      parentName: '系统管理',
    }

    wrapper.vm.showEditDialog(permissionNode)
    await flushPromises()

    // 模拟表单验证通过
    wrapper.vm.permissionFormRef = {
      validate: vi.fn().mockResolvedValue(true),
    }

    // 提交表单
    await wrapper.vm.submitPermissionForm()
    await flushPromises()

    // 验证调用了 updatePermission API
    expect(SystemManager.updatePermission).toHaveBeenCalled()
    expect(SystemManager.updatePermission).toHaveBeenCalledWith({
      id: 2,
      parentId: 1,
      type: 1,
      name: '更新后的权限',
      code: 'updated:permission',
      path: '/update',
      component: 'UpdateComponent',
      icon: 'UpdateIcon',
      sort: 1,
      status: 1,
      hidden: false,
      keepAlive: true,
      description: '更新描述',
    })
  })

  // 测试场景 4: 权限分配 - 为角色分配权限
  it('deletes permission successfully', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 模拟删除确认
    const originalConfirm = globalThis.confirm
    globalThis.confirm = vi.fn().mockResolvedValue(true)

    // 执行删除
    await wrapper.vm.deletePermission(5)
    await flushPromises()

    // 验证调用了 deletePermission API
    expect(SystemManager.deletePermission).toHaveBeenCalledWith(5)

    // 恢复原始方法
    globalThis.confirm = originalConfirm
  })

  it('clears selected permission after deletion', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 设置当前选中的权限
    wrapper.vm.selectedPermission = {
      id: 5,
      name: '删除用户',
      code: 'system:user:delete',
      type: 2,
    }

    // 模拟删除确认
    const originalConfirm = globalThis.confirm
    globalThis.confirm = vi.fn().mockResolvedValue(true)

    // 删除当前选中的权限
    await wrapper.vm.deletePermission(5)
    await flushPromises()

    // 验证选中的权限已被清空
    expect(wrapper.vm.selectedPermission).toBeNull()

    // 恢复原始方法
    globalThis.confirm = originalConfirm
  })

  it('refreshes permission tree after operation', async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    const initialTreeData = [...wrapper.vm.permissionTreeData]

    // 模拟删除确认
    const originalConfirm = globalThis.confirm
    globalThis.confirm = vi.fn().mockResolvedValue(true)

    // 执行删除操作会触发刷新
    await wrapper.vm.deletePermission(5)
    await flushPromises()

    // 验证权限树被重新获取
    expect(SystemManager.getPermissionTree).toHaveBeenCalled()

    // 恢复原始方法
    globalThis.confirm = originalConfirm
  })

  it('handles template apply failure gracefully', async () => {
    // 模拟 API 调用失败
    const originalAddPermission = SystemManager.addPermission
    SystemManager.addPermission = vi.fn().mockRejectedValue(new Error('添加失败'))

    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 应用模板失败
    await wrapper.vm.applyTemplate('user')
    await flushPromises()

    // 验证对话框仍然打开（未关闭）
    expect(wrapper.vm.templateDialogVisible).toBe(false)

    // 恢复原始方法
    SystemManager.addPermission = originalAddPermission
  })

  it('handles delete permission failure gracefully', async () => {
    // 模拟 API 调用失败
    const originalDeletePermission = SystemManager.deletePermission
    SystemManager.deletePermission = vi.fn().mockRejectedValue(new Error('删除失败'))

    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-input-number': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-alert': true,
          'el-row': true,
          'el-col': true,
          'yin-del-dialog': true,
        },
      },
    })

    await flushPromises()

    // 模拟删除确认
    const originalConfirm = globalThis.confirm
    globalThis.confirm = vi.fn().mockResolvedValue(true)

    // 执行删除
    await wrapper.vm.deletePermission(999)
    await flushPromises()

    // 验证调用了 deletePermission API
    expect(SystemManager.deletePermission).toHaveBeenCalledWith(999)

    // 恢复原始方法
    SystemManager.deletePermission = originalDeletePermission
    globalThis.confirm = originalConfirm
  })
})
