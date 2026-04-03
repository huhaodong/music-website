<template>
  <div class="container">
    <div class="handle-box project-handle">
      <el-select v-model="query.status" clearable placeholder="状态" @change="handleSearch">
        <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value"></el-option>
      </el-select>
      <el-input v-model="query.keyword" clearable placeholder="关键字" @keyup.enter="handleSearch"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="primary" @click="showAddDialog">创建项目</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading" max-height="550">
      <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
      <el-table-column prop="name" label="名称" min-width="180"></el-table-column>
      <el-table-column prop="status" label="状态" width="120" align="center">
        <template v-slot="scope">
          <el-tag size="small" :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orgName" label="组织" width="150" align="center">
        <template v-slot="scope">
          <span>{{ scope.row.orgName ?? "-" }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="220">
        <template v-slot="scope">
          <div class="ellipsis">{{ scope.row.description || "-" }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" align="center">
        <template v-slot="scope">
          <el-button size="small" @click="goDetail(scope.row)">详情</el-button>
          <el-button size="small" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      background
      layout="total, sizes, prev, pager, next"
      :total="total"
      :current-page="query.page"
      :page-size="query.size"
      :page-sizes="[5, 10, 20, 50]"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { HttpManager } from "@/api/index";
import { ElMessage, ElMessageBox } from "element-plus";

type ProjectRow = {
  id: number;
  orgId?: number;
  orgName?: string;
  name: string;
  description?: string;
  status?: string;
};

export default defineComponent({
  setup() {
    const router = useRouter();
    const loading = ref(false);
    const submitLoading = ref(false);
    const tableData = ref<ProjectRow[]>([]);
    const total = ref(0);

    const query = reactive({
      status: "",
      keyword: "",
      page: 1,
      size: 10,
    });

    const statusOptions = [
      { label: "草稿", value: "DRAFT" },
      { label: "已发布", value: "RELEASED" },
      { label: "暂停", value: "HOLD" },
    ];

    function pickPagePayload(payload: any) {
      const data = payload?.data ?? payload;
      return {
        records: Array.isArray(data?.records) ? data.records : [],
        total: Number(data?.total ?? 0),
      };
    }

    async function fetchList() {
      loading.value = true;
      try {
        const res: any = await HttpManager.pageProjects({
          status: query.status || undefined,
          keyword: query.keyword || undefined,
          page: query.page,
          size: query.size,
        });
        const pageData = pickPagePayload(res);
        tableData.value = pageData.records;
        total.value = pageData.total;
      } catch (e: any) {
        ElMessage.error(e?.message || "获取列表失败");
      } finally {
        loading.value = false;
      }
    }

    function handleSearch() {
      query.page = 1;
      fetchList();
    }

    function handlePageChange(p: number) {
      query.page = p;
      fetchList();
    }

    function handleSizeChange(s: number) {
      query.size = s;
      query.page = 1;
      fetchList();
    }

    function goDetail(row: ProjectRow) {
      router.push(`/Home/project/${row.id}`);
    }

    const dialogVisible = ref(false);
    const isEdit = ref(false);
    const dialogTitle = computed(() => (isEdit.value ? "编辑项目" : "创建项目"));
    const formRef = ref();

    const form = reactive<Partial<ProjectRow>>({
      id: undefined,
      name: "",
      description: "",
    });

    const rules = {
      name: [{ required: true, message: "请输入项目名称", trigger: "blur" }],
    };

    function resetForm() {
      form.id = undefined;
      form.name = "";
      form.description = "";
    }

    function showAddDialog() {
      isEdit.value = false;
      resetForm();
      dialogVisible.value = true;
    }

    function showEditDialog(row: ProjectRow) {
      isEdit.value = true;
      form.id = row.id;
      form.name = row.name;
      form.description = row.description ?? "";
      dialogVisible.value = true;
    }

    async function submitForm() {
      const valid = await formRef.value?.validate?.().catch(() => false);
      if (!valid) return;

      submitLoading.value = true;
      try {
        const payload = {
          name: form.name,
          description: form.description,
        };
        let res: any;
        if (isEdit.value && form.id) {
          res = await HttpManager.updateProject(Number(form.id), payload);
        } else {
          res = await HttpManager.createProject(payload);
        }
        if (res?.success === false) {
          ElMessage.error(res?.message || "操作失败");
          return;
        }
        ElMessage.success(res?.message || "操作成功");
        dialogVisible.value = false;
        fetchList();
      } catch (e: any) {
        ElMessage.error(e?.message || "操作失败");
      } finally {
        submitLoading.value = false;
      }
    }

    async function handleDelete(row: ProjectRow) {
      try {
        await ElMessageBox.confirm(`确定要删除项目 "${row.name}" 吗？删除后列表将不可见。`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        const res: any = await HttpManager.deleteProject(row.id);
        if (res?.success === false) {
          ElMessage.error(res?.message || "删除失败");
          return;
        }
        ElMessage.success(res?.message || "删除成功");
        fetchList();
      } catch (e: any) {
        if (e !== "cancel") {
          ElMessage.error(e?.message || "删除失败");
        }
      }
    }

    function statusLabel(v: any) {
      if (v === "DRAFT") return "草稿";
      if (v === "RELEASED") return "已发布";
      if (v === "HOLD") return "暂停";
      return v || "-";
    }

    function statusTagType(v: any) {
      if (v === "RELEASED") return "success";
      if (v === "HOLD") return "warning";
      return "info";
    }

    onMounted(() => {
      fetchList();
    });

    return {
      loading,
      submitLoading,
      tableData,
      total,
      query,
      statusOptions,
      dialogVisible,
      dialogTitle,
      formRef,
      form,
      rules,
      handleSearch,
      handlePageChange,
      handleSizeChange,
      goDetail,
      showAddDialog,
      showEditDialog,
      submitForm,
      handleDelete,
      resetForm,
      statusLabel,
      statusTagType,
    };
  },
});
</script>

<style scoped>
.project-handle {
  width: 100%;
  gap: 10px;
}

.ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
