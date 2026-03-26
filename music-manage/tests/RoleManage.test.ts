import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import RoleManage from '@/views/RoleManage.vue'
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
    getAllRoles: vi.fn().mockResolvedValue({
      data: [
        { id: 1, name: '超级管理员', code: 'admin', sort: 0, status: 1, description: '拥有所有权限', createTime: '2024-01-01T00:00:00.000Z' },
        { id: 2, name: '普通用户', code: 'user', sort: 1, status: 1, description: '普通用户权限', createTime: '2024-01-02T00:00:00.000Z' },
      ]
    }),
    getPermissionTree: vi.fn().mockResolvedValue({
      data: [
        { id: 1, name: '系统管理', type: 0, children: [
          { id: 2, name: '用户管理', type: 1 },
          { id: 3, name: '角色管理', type: 1 },
        ]},
        { id: 4, name: '内容管理', type: 0, children: [] }
      ]
    }),
    getRolePermissions: vi.fn().mockResolvedValue({
      data: [
        { id: 2, name: '用户管理' },
        { id: 3, name: '角色管理' },
      ]
    }),
    addRole: vi.fn().mockResolvedValue({ message: '添加成功', type: 'success' }),
    updateRole: vi.fn().mockResolvedValue({ message: '更新成功', type: 'success' }),
    deleteRole: vi.fn().mockResolvedValue({ message: '删除成功', type: 'success' }),
    assignPermissions: vi.fn().mockResolvedValue({ message: '分配成功', type: 'success' }),
  }
}))

describe('RoleManage.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders role list correctly', async () => {
    const wrapper = mount(RoleManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-tag': true,
          'el-tree': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const tableData = wrapper.vm.tableData
    expect(tableData.length).toBe(2)
    expect(tableData[0].name).toBe('超级管理员')
  })

  it('filters roles by search word', async () => {
    const wrapper = mount(RoleManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-tag': true,
          'el-tree': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    wrapper.vm.searchWord = '超级'
    await flushPromises()
    expect(wrapper.vm.tableData.length).toBe(1)
    expect(wrapper.vm.tableData[0].name).toBe('超级管理员')
  })

  it('opens add dialog correctly', async () => {
    const wrapper = mount(RoleManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-tag': true,
          'el-tree': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    wrapper.vm.showAddDialog()
    expect(wrapper.vm.roleDialogVisible).toBe(true)
  })

  it('opens edit dialog correctly', async () => {
    const wrapper = mount(RoleManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-tag': true,
          'el-tree': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const role = wrapper.vm.tableData[0]
    wrapper.vm.showEditDialog(role)
    expect(wrapper.vm.roleDialogVisible).toBe(true)
  })

  it('opens permission dialog correctly', async () => {
    const wrapper = mount(RoleManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-tag': true,
          'el-tree': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const role = wrapper.vm.tableData[0]
    await wrapper.vm.showPermissionDialog(role)
    expect(wrapper.vm.permissionDialogVisible).toBe(true)
  })

  it('formats date correctly', async () => {
    const wrapper = mount(RoleManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-tag': true,
          'el-tree': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const formattedDate = wrapper.vm.formatDate('2024-01-01T00:00:00.000Z')
    expect(formattedDate).toContain('2024')
  })
})