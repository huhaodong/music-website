<template>
  <div class="container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限列表</span>
        </div>
      </template>

      <el-table :data="permissions" stripe style="width: 100%">
        <el-table-column prop="code" label="权限编码" width="200" />
        <el-table-column prop="name" label="权限名称" width="180" />
        <el-table-column prop="typeName" label="类型" width="100" />
        <el-table-column prop="moduleName" label="模块" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import { SystemManager } from "@/api/index";
import { ElMessage } from "element-plus";

export default defineComponent({
  setup() {
    const permissions = ref<any[]>([]);

    const moduleNames: Record<string, string> = {
      song: '歌曲',
      singer: '歌手',
      songlist: '歌单',
      user: '用户',
      role: '角色',
      org: '组织',
      comment: '评论',
      system: '系统',
    };

    const typeNames: Record<number, string> = {
      1: '菜单',
      2: '按钮',
    };

    function getModuleName(code: string): string {
      const module = code.split(':')[0];
      return moduleNames[module] || '其他';
    }

    async function getPermissions() {
      try {
        const result = await SystemManager.getAllPermissions() as any;
        if (result.data) {
          permissions.value = result.data.map((p: any) => ({
            ...p,
            moduleName: getModuleName(p.code),
            typeName: typeNames[p.type] || '其他',
          }));
        }
      } catch (error) {
        ElMessage.error("获取权限列表失败");
      }
    }

    onMounted(() => {
      getPermissions();
    });

    return {
      permissions,
    };
  },
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
