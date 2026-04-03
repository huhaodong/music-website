import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import ArtistManage from "../src/views/ArtistManage.vue";

const ElMessageBoxMock = vi.hoisted(() => ({
  confirm: vi.fn().mockResolvedValue("confirm"),
  alert: vi.fn().mockResolvedValue("ok"),
  prompt: vi.fn(),
}));

vi.mock("element-plus", () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
  ElMessageBox: ElMessageBoxMock,
}));

const HttpManagerMock = vi.hoisted(() => ({
  pageArtists: vi.fn().mockResolvedValue({
    data: {
      records: [
        { id: 1, name: "A1", type: "singer", sex: 1, location: "CN", introduction: "i1" },
        { id: 2, name: "A2", type: "composer", sex: 2, location: "US", introduction: "i2" },
      ],
      total: 2,
    },
  }),
  createArtist: vi.fn().mockResolvedValue({ success: true, message: "添加成功" }),
  updateArtist: vi.fn().mockResolvedValue({ success: true, message: "修改成功" }),
  deleteArtist: vi.fn().mockResolvedValue({ success: true, message: "删除成功" }),
}));

vi.mock("@/api/index", () => ({
  HttpManager: HttpManagerMock,
}));

describe("ArtistManage.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders artist list correctly", async () => {
    const wrapper = mount(ArtistManage, {
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
          "el-radio-group": true,
          "el-radio": true,
          "el-date-picker": true,
        },
      },
    });

    await flushPromises();
    expect(wrapper.vm.tableData.length).toBe(2);
    expect(wrapper.vm.total).toBe(2);
    expect(wrapper.vm.tableData[0].name).toBe("A1");
  });

  it("opens add dialog correctly", async () => {
    const wrapper = mount(ArtistManage, {
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
          "el-radio-group": true,
          "el-radio": true,
          "el-date-picker": true,
        },
      },
    });

    await flushPromises();
    wrapper.vm.showAddDialog();
    expect(wrapper.vm.dialogVisible).toBe(true);
    expect(wrapper.vm.dialogTitle).toBe("创建艺术家");
  });

  it("shows association alert when deleting artist with linked projects", async () => {
    (HttpManagerMock.deleteArtist as any).mockResolvedValue({
      success: false,
      message: "删除失败：该艺术家已关联项目，无法删除",
    });

    const wrapper = mount(ArtistManage, {
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
          "el-radio-group": true,
          "el-radio": true,
          "el-date-picker": true,
        },
      },
    });

    await flushPromises();
    await wrapper.vm.handleDelete({ id: 1, name: "A1", type: "singer" });
    expect((ElMessageBoxMock.alert as any).mock.calls.length).toBe(1);
  });
});
