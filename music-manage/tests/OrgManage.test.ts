import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import OrgManage from '@/views/OrgManage.vue'
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
    getOrganizationTree: vi.fn().mockResolvedValue({
      data: [
        {
          id: 1,
          name: '总公司',
          code: 'HQ',
          sort: 0,
          status: 1,
          description: '总公司',
          children: [
            { id: 2, name: '研发部', code: 'RD', parentId: 1, sort: 0, status: 1 },
            { id: 3, name: '市场部', code: 'MKT', parentId: 1, sort: 1, status: 1 },
          ]
        },
        {
          id: 4,
          name: '分公司',
          code: 'BR',
          sort: 1,
          status: 1,
          description: '分公司',
          children: []
        }
      ]
    }),
    addOrganization: vi.fn().mockResolvedValue({ message: '添加成功', type: 'success' }),
    updateOrganization: vi.fn().mockResolvedValue({ message: '更新成功', type: 'success' }),
    deleteOrganization: vi.fn().mockResolvedValue({ message: '删除成功', type: 'success' }),
  }
}))

describe('OrgManage.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders organization tree correctly', async () => {
    const wrapper = mount(OrgManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const orgTreeData = wrapper.vm.orgTreeData
    expect(orgTreeData.length).toBe(2)
    expect(orgTreeData[0].name).toBe('总公司')
    expect(orgTreeData[0].children.length).toBe(2)
  })

  it('opens add dialog for root organization', async () => {
    const wrapper = mount(OrgManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    wrapper.vm.showAddDialog(null)
    expect(wrapper.vm.orgDialogVisible).toBe(true)
  })

  it('opens add dialog for child organization', async () => {
    const wrapper = mount(OrgManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const parentNode = wrapper.vm.orgTreeData[0]
    wrapper.vm.showAddDialog(parentNode)
    expect(wrapper.vm.orgDialogVisible).toBe(true)
  })

  it('opens edit dialog correctly', async () => {
    const wrapper = mount(OrgManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const node = wrapper.vm.orgTreeData[0].children[0]
    wrapper.vm.showEditDialog(node)
    expect(wrapper.vm.orgDialogVisible).toBe(true)
  })

  it('handles node click correctly', async () => {
    const wrapper = mount(OrgManage, {
      global: {
        stubs: {
          'el-button': true,
          'el-tree': true,
          'el-card': true,
          'el-descriptions': true,
          'el-descriptions-item': true,
          'el-tag': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-input': true,
          'el-input-number': true,
          'el-radio-group': true,
          'el-radio': true,
          'yin-del-dialog': true,
        }
      }
    })

    await flushPromises()
    const node = wrapper.vm.orgTreeData[0]
    wrapper.vm.handleNodeClick(node)
    expect(wrapper.vm.selectedOrg).toEqual(node)
  })
})