<template>
  <div class="container">
    <div class="handle-box artist-handle">
      <el-select v-model="query.type" clearable placeholder="类型" @change="handleSearch">
        <el-option v-for="opt in typeOptions" :key="opt.value" :label="opt.label" :value="opt.value"></el-option>
      </el-select>
      <el-input v-model="query.keyword" clearable placeholder="关键字" @keyup.enter="handleSearch"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="primary" @click="showAddDialog">创建艺术家</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading" max-height="550">
      <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
      <el-table-column prop="name" label="名称" min-width="140"></el-table-column>
      <el-table-column label="类型" width="200" align="center">
        <template v-slot="scope">
          <el-tag v-for="t in scope.row.types" :key="t" size="small" type="info" style="margin-right: 4px;">{{ typeLabel(t) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sex" label="性别" width="100" align="center">
        <template v-slot="scope">
          <span>{{ sexLabel(scope.row.sex) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="地区" width="140"></el-table-column>
      <el-table-column prop="introduction" label="简介" min-width="220">
        <template v-slot="scope">
          <div class="ellipsis">{{ scope.row.introduction || "-" }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template v-slot="scope">
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="types">
          <el-select v-model="form.types" multiple placeholder="请选择类型" filterable>
            <el-option v-for="opt in typeOptions" :key="opt.value" :label="opt.label" :value="opt.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :label="0">女</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birth" type="date" placeholder="选择日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="form.location" placeholder="请输入地区"></el-input>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.introduction" type="textarea" :rows="3" placeholder="请输入简介"></el-input>
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
import { HttpManager } from "@/api/index";
import { ElMessage, ElMessageBox } from "element-plus";

type ArtistRow = {
  id: number;
  name: string;
  types: string[];
  sex?: number;
  birth?: any;
  location?: string;
  introduction?: string;
};

export default defineComponent({
  setup() {
    const loading = ref(false);
    const submitLoading = ref(false);
    const tableData = ref<ArtistRow[]>([]);
    const total = ref(0);

    const query = reactive({
      type: "",
      keyword: "",
      page: 1,
      size: 10,
    });

    const typeOptions = [
      { label: "歌手", value: "singer" },
      { label: "作词", value: "lyricist" },
      { label: "作曲", value: "composer" },
      { label: "制作人", value: "producer" },
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
        const res: any = await HttpManager.pageArtists({
          type: query.type || undefined,
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

    const dialogVisible = ref(false);
    const isEdit = ref(false);
    const dialogTitle = computed(() => (isEdit.value ? "编辑艺术家" : "创建艺术家"));
    const formRef = ref();

    const form = reactive<Partial<ArtistRow>>({
      id: undefined,
      name: "",
      types: [],
      sex: 2,
      birth: null,
      location: "",
      introduction: "",
    });

    const rules = {
      name: [{ required: true, message: "请输入名称", trigger: "blur" }],
      types: [{ required: true, message: "请选择类型", trigger: "change" }],
    };

    function resetForm() {
      form.id = undefined;
      form.name = "";
      form.types = [];
      form.sex = 2;
      form.birth = null;
      form.location = "";
      form.introduction = "";
    }

    function showAddDialog() {
      isEdit.value = false;
      resetForm();
      dialogVisible.value = true;
    }

    function showEditDialog(row: ArtistRow) {
      isEdit.value = true;
      form.id = row.id;
      form.name = row.name;
      form.types = row.types ?? [];
      form.sex = row.sex ?? 2;
      form.birth = row.birth ?? null;
      form.location = row.location ?? "";
      form.introduction = row.introduction ?? "";
      dialogVisible.value = true;
    }

    async function submitForm() {
      const valid = await formRef.value?.validate?.().catch(() => false);
      if (!valid) return;

      submitLoading.value = true;
      try {
        const payload = {
          name: form.name,
          types: form.types,
          sex: form.sex,
          birth: form.birth,
          location: form.location,
          introduction: form.introduction,
        };
        let res: any;
        if (isEdit.value && form.id) {
          res = await HttpManager.updateArtist(Number(form.id), payload);
        } else {
          res = await HttpManager.createArtist(payload);
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

    async function handleDelete(row: ArtistRow) {
      try {
        await ElMessageBox.confirm(`确定要删除艺术家 "${row.name}" 吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        const res: any = await HttpManager.deleteArtist(row.id);
        if (res?.success === false) {
          const msg = res?.message || "删除失败";
          if (String(msg).includes("已关联项目")) {
            await ElMessageBox.alert(msg, "无法删除", { type: "warning" });
            return;
          }
          ElMessage.error(msg);
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

    function sexLabel(v: any) {
      if (v === 0) return "女";
      if (v === 1) return "男";
      if (v === 2) return "保密";
      return "-";
    }

    function typeLabel(v: any) {
      if (v === "singer") return "歌手";
      if (v === "lyricist") return "作词";
      if (v === "composer") return "作曲";
      if (v === "producer") return "制作人";
      return v || "-";
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
      typeOptions,
      dialogVisible,
      dialogTitle,
      formRef,
      form,
      rules,
      handleSearch,
      handlePageChange,
      handleSizeChange,
      showAddDialog,
      showEditDialog,
      submitForm,
      handleDelete,
      resetForm,
      sexLabel,
      typeLabel,
    };
  },
});
</script>

<style scoped>
.artist-handle {
  width: 100%;
  gap: 10px;
}

.ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
