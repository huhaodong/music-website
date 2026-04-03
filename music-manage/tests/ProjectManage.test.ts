import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import ProjectManage from "../src/views/ProjectManage.vue";

const routerPushMock = vi.hoisted(() => vi.fn());

vi.mock("vue-router", () => ({
  useRouter: () => ({
    push: routerPushMock,
  }),
}));

vi.mock("element-plus", () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
  ElMessageBox: {
    confirm: vi.fn().mockResolvedValue("confirm"),
    alert: vi.fn().mockResolvedValue("ok"),
    prompt: vi.fn(),
  },
}));

const HttpManagerMock = vi.hoisted(() => ({
  pageProjects: vi.fn().mockResolvedValue({
    data: {
      records: [
        { id: 1, name: "P1", status: "DRAFT", orgId: 1, description: "d1" },
        { id: 2, name: "P2", status: "HOLD", orgId: 2, description: "d2" },
      ],
      total: 2,
    },
  }),
  createProject: vi.fn().mockResolvedValue({ success: true, message: "添加成功" }),
  updateProject: vi.fn().mockResolvedValue({ success: true, message: "修改成功" }),
  deleteProject: vi.fn().mockResolvedValue({ success: true, message: "删除成功" }),
}));

vi.mock("@/api/index", () => ({
  HttpManager: HttpManagerMock,
}));

describe("ProjectManage.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders project list correctly", async () => {
    const wrapper = mount(ProjectManage, {
      global: {
        directives: { loading: () => {} },
        stubs: {
          "el-select": true,
          "el-option": true,
          "el-input": true,
          "el-button": true,
          "el-table": true,
          "el-table-column": true,
          "el-tag": true,
          "el-pagination": true,
          "el-dialog": true,
          "el-form": true,
          "el-form-item": true,
        },
      },
    });

    await flushPromises();
    expect(wrapper.vm.tableData.length).toBe(2);
    expect(wrapper.vm.total).toBe(2);
    expect(wrapper.vm.tableData[0].name).toBe("P1");
  });

  it("navigates to detail page", async () => {
    const wrapper = mount(ProjectManage, {
      global: {
        directives: { loading: () => {} },
        stubs: {
          "el-select": true,
          "el-option": true,
          "el-input": true,
          "el-button": true,
          "el-table": true,
          "el-table-column": true,
          "el-tag": true,
          "el-pagination": true,
          "el-dialog": true,
          "el-form": true,
          "el-form-item": true,
        },
      },
    });

    await flushPromises();
    wrapper.vm.goDetail({ id: 1, name: "P1" });
    expect(routerPushMock).toHaveBeenCalledWith("/Home/project/1");
  });

  it("deletes project correctly", async () => {
    const wrapper = mount(ProjectManage, {
      global: {
        directives: { loading: () => {} },
        stubs: {
          "el-select": true,
          "el-option": true,
          "el-input": true,
          "el-button": true,
          "el-table": true,
          "el-table-column": true,
          "el-tag": true,
          "el-pagination": true,
          "el-dialog": true,
          "el-form": true,
          "el-form-item": true,
        },
      },
    });

    await flushPromises();
    await wrapper.vm.handleDelete({ id: 1, name: "P1" });
    expect((HttpManagerMock.deleteProject as any).mock.calls.length).toBe(1);
  });
});
