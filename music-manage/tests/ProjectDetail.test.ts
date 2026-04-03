import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount, flushPromises } from "@vue/test-utils";
import ProjectDetail from "../src/views/ProjectDetail.vue";

const routerBackMock = vi.hoisted(() => vi.fn());

vi.mock("vue-router", () => ({
  useRoute: () => ({ params: { id: "1" } }),
  useRouter: () => ({
    back: routerBackMock,
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

const HttpManagerMock = vi.hoisted(() => ({
  getProjectDetail: vi.fn(),
  changeProjectStatus: vi.fn().mockResolvedValue({ success: true, message: "状态变更成功" }),
}));

vi.mock("@/api/index", () => ({
  HttpManager: HttpManagerMock,
}));

describe("ProjectDetail.vue", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("shows only allowed status actions for DRAFT", async () => {
    HttpManagerMock.getProjectDetail.mockResolvedValue({
      data: {
        project: { id: 1, name: "P1", status: "DRAFT", orgId: 1, createdBy: 1, description: "d" },
        artists: [{ id: 1, name: "A1", type: "singer", role: "singer" }],
        songs: [{ id: 1, name: "S1", introduction: "i" }],
      },
    });

    const wrapper = mount(ProjectDetail, {
      global: {
        directives: { loading: () => {} },
        stubs: {
          "el-button": true,
          "el-card": true,
          "el-descriptions": true,
          "el-descriptions-item": true,
          "el-tag": true,
          "el-table": true,
          "el-table-column": true,
        },
      },
    });

    await flushPromises();
    expect(wrapper.vm.statusActions.map((a: any) => a.value)).toEqual(["RELEASED", "HOLD"]);
  });

  it("hides illegal transitions for RELEASED", async () => {
    HttpManagerMock.getProjectDetail.mockResolvedValue({
      data: {
        project: { id: 1, name: "P1", status: "RELEASED" },
        artists: [],
        songs: [],
      },
    });

    const wrapper = mount(ProjectDetail, {
      global: {
        directives: { loading: () => {} },
        stubs: {
          "el-button": true,
          "el-card": true,
          "el-descriptions": true,
          "el-descriptions-item": true,
          "el-tag": true,
          "el-table": true,
          "el-table-column": true,
        },
      },
    });

    await flushPromises();
    expect(wrapper.vm.statusActions.map((a: any) => a.value)).toEqual(["HOLD"]);
  });

  it("calls status update api", async () => {
    HttpManagerMock.getProjectDetail.mockResolvedValue({
      data: {
        project: { id: 1, name: "P1", status: "DRAFT" },
        artists: [],
        songs: [],
      },
    });

    const wrapper = mount(ProjectDetail, {
      global: {
        directives: { loading: () => {} },
        stubs: {
          "el-button": true,
          "el-card": true,
          "el-descriptions": true,
          "el-descriptions-item": true,
          "el-tag": true,
          "el-table": true,
          "el-table-column": true,
        },
      },
    });

    await flushPromises();
    await wrapper.vm.changeStatus("HOLD");
    expect(HttpManagerMock.changeProjectStatus).toHaveBeenCalledWith(1, { status: "HOLD", remark: "" });
  });
});
