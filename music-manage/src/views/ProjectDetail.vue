<template>
  <div class="container">
    <div class="detail-header">
      <el-button @click="goBack">返回</el-button>
      <div class="title">项目详情</div>
    </div>

    <el-card class="card" header="基础信息" v-loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ project.id }}</el-descriptions-item>
        <el-descriptions-item label="组织">{{ project.orgName ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="名称" :span="2">{{ project.name || "-" }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag size="small" :type="statusTagType(project.status)">{{ statusLabel(project.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ project.createdByName ?? "-" }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ project.description || "-" }}</el-descriptions-item>
      </el-descriptions>

      <div class="status-actions" v-if="statusActions.length">
        <div class="status-actions-title">状态变更</div>
        <el-button
          v-for="a in statusActions"
          :key="a.value"
          size="small"
          type="primary"
          :loading="statusLoading"
          @click="changeStatus(a.value)"
        >
          变更为{{ a.label }}
        </el-button>
      </div>
    </el-card>

    <el-card class="card" header="关联艺术家">
      <el-table :data="artists" border stripe max-height="320">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="名称" min-width="140"></el-table-column>
        <el-table-column label="类型" width="200" align="center">
          <template v-slot="scope">
            <el-tag v-for="t in scope.row.types" :key="t" size="small" type="info" style="margin-right: 4px;">{{ t }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="160" align="center"></el-table-column>
      </el-table>
    </el-card>

    <el-card class="card" header="关联歌曲">
      <el-table :data="songs" border stripe max-height="320">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="名称" min-width="180"></el-table-column>
        <el-table-column prop="introduction" label="简介" min-width="220">
          <template v-slot="scope">
            <div class="ellipsis">{{ scope.row.introduction || "-" }}</div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { HttpManager } from "@/api/index";
import { ElMessage } from "element-plus";

export default defineComponent({
  setup() {
    const route = useRoute();
    const router = useRouter();
    const loading = ref(false);
    const statusLoading = ref(false);

    const projectId = computed(() => Number(route.params.id));

    const project = reactive<any>({
      id: null,
      orgId: null,
      name: "",
      description: "",
      status: "",
      createdBy: null,
    });
    const artists = ref<any[]>([]);
    const songs = ref<any[]>([]);

    function normalizeDetail(payload: any) {
      const data = payload?.data ?? payload;
      return {
        project: data?.project ?? {},
        artists: Array.isArray(data?.artists) ? data.artists : [],
        songs: Array.isArray(data?.songs) ? data.songs : [],
      };
    }

    async function fetchDetail() {
      if (!projectId.value) return;
      loading.value = true;
      try {
        const res: any = await HttpManager.getProjectDetail(projectId.value);
        const detail = normalizeDetail(res);
        Object.assign(project, detail.project || {});
        artists.value = detail.artists || [];
        songs.value = detail.songs || [];
      } catch (e: any) {
        ElMessage.error(e?.message || "获取详情失败");
      } finally {
        loading.value = false;
      }
    }

    const statusActions = computed(() => {
      const from = String(project.status || "DRAFT");
      const map: Record<string, string[]> = {
        DRAFT: ["RELEASED", "HOLD"],
        RELEASED: ["HOLD"],
        HOLD: ["DRAFT", "RELEASED"],
      };
      const targets = map[from] || [];
      return targets.map((v) => ({ value: v, label: statusLabel(v) }));
    });

    async function changeStatus(to: string) {
      if (!projectId.value) return;
      statusLoading.value = true;
      try {
        const res: any = await HttpManager.changeProjectStatus(projectId.value, { status: to, remark: "" });
        if (res?.success === false) {
          ElMessage.error(res?.message || "状态变更失败");
          return;
        }
        ElMessage.success(res?.message || "状态变更成功");
        fetchDetail();
      } catch (e: any) {
        ElMessage.error(e?.message || "状态变更失败");
      } finally {
        statusLoading.value = false;
      }
    }

    function goBack() {
      router.back();
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
      fetchDetail();
    });

    return {
      loading,
      statusLoading,
      project,
      artists,
      songs,
      statusActions,
      changeStatus,
      goBack,
      statusLabel,
      statusTagType,
    };
  },
});
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.title {
  font-weight: 600;
}

.card {
  margin-bottom: 16px;
}

.status-actions {
  margin-top: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.status-actions-title {
  font-size: 12px;
  color: #606266;
  margin-right: 6px;
}

.ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
