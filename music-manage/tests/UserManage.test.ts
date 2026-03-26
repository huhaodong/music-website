import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import UserManage from '@/views/UserManage.vue'
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
    getAllUsers: vi.fn().mockResolvedValue({
      data: [
        { id: 1, username: 'admin', nickname: '管理员', email: 'admin@example.com', phone: '13800138000', status: 1, roles: [{ id: 1, name: '超级管理员' }], createTime: '2024-01-01T00:00:00.000Z' },
        { id: 2, username: 'user1', nickname: '用户1', email: 'user1@example.com', phone: '13800138001', status: 1, roles: [{ id: 2, name: '普通用户' }], createTime: '2024-01-02T00:00:00.000Z' },
      ]
    }),
    getAllRoles: vi.fn().mockResolvedValue({
      data: [
        { id: 1, name: '超级管理员', code: 'admin' },
        { id: 2, name: '普通用户', code: 'user' },
      ]
    }),
    addUser: vi.fn().mockResolvedValue({ message: '添加成功', type: 'success' }),
    updateUser: vi.fn().mockResolvedValue({ message: '更新成功', type: 'success' }),
    deleteUser: vi.fn().mockResolvedValue({ message: '删除成功', type: 'success' }),
    assignRoles: vi.fn().mockResolvedValue({ message: '分配成功', type: 'success' }),
  }
}))

describe('UserManage.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders user list correctly', async () => {
    const wrapper = mount(UserManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-select': true,
          'el-option': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-checkbox-group': true,
          'el-checkbox': true,
          'el-tag': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const tableData = wrapper.vm.tableData
    expect(tableData.length).toBe(2)
    expect(tableData[0].username).toBe('admin')
  })

  it('filters users by search word', async () => {
    const wrapper = mount(UserManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-select': true,
          'el-option': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-checkbox-group': true,
          'el-checkbox': true,
          'el-tag': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    wrapper.vm.searchWord = 'admin'
    await flushPromises()
    expect(wrapper.vm.tableData.length).toBe(1)
    expect(wrapper.vm.tableData[0].username).toBe('admin')
  })

  it('opens add dialog correctly', async () => {
    const wrapper = mount(UserManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-select': true,
          'el-option': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-checkbox-group': true,
          'el-checkbox': true,
          'el-tag': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    wrapper.vm.showAddDialog()
    expect(wrapper.vm.userDialogVisible).toBe(true)
    expect(wrapper.vm.isEdit).toBe(false)
    expect(wrapper.vm.dialogTitle).toBe('添加用户')
  })

  it('opens edit dialog correctly', async () => {
    const wrapper = mount(UserManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-select': true,
          'el-option': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-checkbox-group': true,
          'el-checkbox': true,
          'el-tag': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const user = wrapper.vm.tableData[0]
    wrapper.vm.showEditDialog(user)
    expect(wrapper.vm.userDialogVisible).toBe(true)
    expect(wrapper.vm.isEdit).toBe(true)
    expect(wrapper.vm.dialogTitle).toBe('编辑用户')
    expect(wrapper.vm.userForm.username).toBe('admin')
  })

  it('opens role assignment dialog correctly', async () => {
    const wrapper = mount(UserManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-input': true,
          'el-table': true,
          'el-pagination': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-select': true,
          'el-option': true,
          'el-radio-group': true,
          'el-radio': true,
          'el-checkbox-group': true,
          'el-checkbox': true,
          'el-tag': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const user = wrapper.vm.tableData[0]
    wrapper.vm.showRoleDialog(user)
    expect(wrapper.vm.roleDialogVisible).toBe(true)
  })
})