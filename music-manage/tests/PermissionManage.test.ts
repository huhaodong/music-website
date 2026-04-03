import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import PermissionManage from "@/views/PermissionManage.vue";

const SystemManagerMock = vi.hoisted(() => ({
  getAllPermissions: vi.fn().mockResolvedValue({
    data: [
      { id: 1, name: "添加歌曲", code: "song:add", type: 2, status: 1 },
      { id: 2, name: "歌手列表", code: "singer:list", type: 1, status: 1 },
      { id: 3, name: "未知权限", code: "unknown", type: 99, status: 0 },
    ],
  }),
}));

vi.mock("element-plus", () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
}));

vi.mock("@/api/index", () => ({
  SystemManager: SystemManagerMock,
}));

describe("PermissionManage.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders permission list correctly", async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          "el-card": true,
          "el-table": true,
          "el-table-column": true,
          "el-tag": true,
        },
      },
    });

    await flushPromises();
    expect(wrapper.vm.permissions.length).toBe(3);
    expect(wrapper.vm.permissions[0].name).toBe("添加歌曲");
  });

  it("maps moduleName and typeName correctly", async () => {
    const wrapper = mount(PermissionManage, {
      global: {
        stubs: {
          "el-card": true,
          "el-table": true,
          "el-table-column": true,
          "el-tag": true,
        },
      },
    });

    await flushPromises();
    const p1 = wrapper.vm.permissions.find((p: any) => p.code === "song:add");
    const p2 = wrapper.vm.permissions.find((p: any) => p.code === "singer:list");
    const p3 = wrapper.vm.permissions.find((p: any) => p.code === "unknown");
    expect(p1.moduleName).toBe("歌曲");
    expect(p1.typeName).toBe("按钮");
    expect(p2.moduleName).toBe("歌手");
    expect(p2.typeName).toBe("菜单");
    expect(p3.moduleName).toBe("其他");
    expect(p3.typeName).toBe("其他");
  });
});

