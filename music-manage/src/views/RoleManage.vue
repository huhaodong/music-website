<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="showAddDialog" type="primary">添加角色</el-button>
      <el-input v-model="searchWord" placeholder="搜索角色" class="search-input" clearable @clear="getData"></el-input>
    </div>

    <el-table height="550px" border stripe :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="角色名称" prop="name" width="150" align="center"></el-table-column>
      <el-table-column label="角色编码" prop="code" width="150" align="center"></el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template v-slot="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="80" align="center"></el-table-column>
      <el-table-column label="描述" prop="description"></el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template v-slot="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template v-slot="scope">
          <el-button type="primary" size="small" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button type="warning" size="small" @click="showPermissionDialog(scope.row)">分配权限</el-button>
          <el-button type="danger" size="small" @click="deleteRow(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      background
      layout="total, prev, pager, next"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="tableData.length"
      @current-change="handleCurrentChange"
    >
    </el-pagination>

    <!-- 添加/编辑角色对话框 -->
    <el-dialog :title="dialogTitle" v-model="roleDialogVisible" width="500px" @close="resetRoleForm">
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="roleForm.sort" :min="0" :max="999"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea" :rows="3" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitRoleForm">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog title="分配权限" v-model="permissionDialogVisible" width="700px">
      <div class="permission-list">
        <div v-for="module in groupedPermissions" :key="module.name" class="permission-group">
          <div class="group-header">
            <el-checkbox
              :indeterminate="module.isIndeterminate"
              v-model="module.checked"
              @change="handleModuleCheck(module)"
            >
              {{ module.label }}
            </el-checkbox>
          </div>
          <div class="group-content">
            <el-checkbox-group v-model="selectedPermissionIds">
              <el-checkbox
                v-for="perm in module.permissions"
                :key="perm.id"
                :label="perm.id"
              >
                {{ perm.name }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitPermissionAssignment">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除提示框 -->
    <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, watch, ref, reactive, computed, onMounted } from "vue";
import { SystemManager } from "@/api/index";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import { ElMessage } from "element-plus";

interface PermissionGroup {
  name: string;
  label: string;
  permissions: any[];
  checked: boolean;
  isIndeterminate: boolean;
}

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();

    const tableData = ref<any[]>([]);
    const tempDate = ref<any[]>([]);
    const pageSize = ref(10);
    const currentPage = ref(1);
    const searchWord = ref("");

    const data = computed(() => {
      return tableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
    });

    watch(searchWord, () => {
      if (searchWord.value === "") {
        tableData.value = tempDate.value;
      } else {
        tableData.value = tempDate.value.filter(item =>
          item.name.includes(searchWord.value) ||
          item.code?.includes(searchWord.value)
        );
      }
      currentPage.value = 1;
    });

    async function getData() {
      try {
        const result = await SystemManager.getAllRoles() as any;
        if (result.data) {
          tableData.value = result.data;
          tempDate.value = result.data;
        }
      } catch (error) {
        ElMessage.error("获取角色列表失败");
      }
    }

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

    const allPermissions = ref<any[]>([]);
    const groupedPermissions = ref<PermissionGroup[]>([]);

    async function getAllPermissions() {
      try {
        const result = await SystemManager.getAllPermissions() as any;
        if (result.data) {
          allPermissions.value = result.data;
          buildPermissionGroups();
        }
      } catch (error) {
        ElMessage.error("获取权限列表失败");
      }
    }

    function buildPermissionGroups() {
      const groups: Record<string, PermissionGroup> = {};

      for (const perm of allPermissions.value) {
        const module = perm.code.split(':')[0];
        if (!groups[module]) {
          groups[module] = {
            name: module,
            label: moduleNames[module] || module,
            permissions: [],
            checked: false,
            isIndeterminate: false,
          };
        }
        groups[module].permissions.push(perm);
      }

      groupedPermissions.value = Object.values(groups);
    }

    function handleModuleCheck(module: PermissionGroup) {
      if (module.checked) {
        for (const perm of module.permissions) {
          if (!selectedPermissionIds.value.includes(perm.id)) {
            selectedPermissionIds.value.push(perm.id);
          }
        }
        module.isIndeterminate = false;
      } else {
        selectedPermissionIds.value = selectedPermissionIds.value.filter(
          id => !module.permissions.find((p: any) => p.id === id)
        );
        module.isIndeterminate = false;
      }
    }

    function updateGroupStates() {
      for (const module of groupedPermissions.value) {
        const checkedCount = module.permissions.filter(
          (p: any) => selectedPermissionIds.value.includes(p.id)
        ).length;

        if (checkedCount === 0) {
          module.checked = false;
          module.isIndeterminate = false;
        } else if (checkedCount === module.permissions.length) {
          module.checked = true;
          module.isIndeterminate = false;
        } else {
          module.checked = false;
          module.isIndeterminate = true;
        }
      }
    }

    function handleCurrentChange(val: number) {
      currentPage.value = val;
    }

    function handleSelectionChange(val: any[]) {
      multipleSelection.value = val;
    }

    function formatDate(dateStr: string): string {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleString();
    }

    const roleDialogVisible = ref(false);
    const isEdit = ref(false);
    const dialogTitle = ref("添加角色");
    const roleFormRef = ref();
    const roleForm = reactive({
      id: null as number | null,
      name: "",
      sort: 0,
      status: 1,
      description: "",
    });

    const roleRules = {
      name: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
    };

    function showAddDialog() {
      isEdit.value = false;
      dialogTitle.value = "添加角色";
      roleDialogVisible.value = true;
    }

    function showEditDialog(row: any) {
      isEdit.value = true;
      dialogTitle.value = "编辑角色";
      roleForm.id = row.id;
      roleForm.name = row.name;
      roleForm.sort = row.sort || 0;
      roleForm.status = row.status;
      roleForm.description = row.description || "";
      roleDialogVisible.value = true;
    }

    function resetRoleForm() {
      roleForm.id = null;
      roleForm.name = "";
      roleForm.sort = 0;
      roleForm.status = 1;
      roleForm.description = "";
    }

    async function submitRoleForm() {
      const valid = await roleFormRef.value.validate().catch(() => false);
      if (!valid) return;

      try {
        if (isEdit.value) {
          const result = await SystemManager.updateRole(roleForm) as any;
          ElMessage.success(result.message || "更新成功");
        } else {
          const result = await SystemManager.addRole(roleForm) as any;
          ElMessage.success(result.message || "添加成功");
        }
        roleDialogVisible.value = false;
        getData();
      } catch (error: any) {
        ElMessage.error(error.message || "操作失败");
      }
    }

    const permissionDialogVisible = ref(false);
    const currentRoleId = ref<number | null>(null);
    const selectedPermissionIds = ref<number[]>([]);

    async function showPermissionDialog(row: any) {
      currentRoleId.value = row.id;
      selectedPermissionIds.value = [];

      try {
        const roleResult = await SystemManager.getRolePermissions(row.id) as any;
        if (roleResult.data) {
          selectedPermissionIds.value = roleResult.data.map((p: any) => p.id);
        }
      } catch (error) {
        ElMessage.error("获取角色权限失败");
      }

      await getAllPermissions();
      updateGroupStates();

      permissionDialogVisible.value = true;
    }

    async function submitPermissionAssignment() {
      if (!currentRoleId.value) return;

      try {
        const result = await SystemManager.assignPermissions({
          roleId: currentRoleId.value,
          permissionIds: selectedPermissionIds.value,
        }) as any;
        ElMessage.success(result.message || "分配成功");
        permissionDialogVisible.value = false;
      } catch (error: any) {
        ElMessage.error(error.message || "分配失败");
      }
    }

    const idx = ref(-1);
    const multipleSelection = ref<any[]>([]);
    const delVisible = ref(false);

    async function confirm() {
      try {
        const result = await SystemManager.deleteRole(idx.value) as any;
        ElMessage.success(result.message || "删除成功");
        delVisible.value = false;
        getData();
      } catch (error: any) {
        ElMessage.error(error.message || "删除失败");
      }
    }

    function deleteRow(id: number) {
      idx.value = id;
      delVisible.value = true;
    }

    async function deleteAll() {
      for (const item of multipleSelection.value) {
        idx.value = item.id;
        await SystemManager.deleteRole(item.id) as any;
      }
      ElMessage.success("批量删除成功");
      multipleSelection.value = [];
      getData();
    }

    getData();

    return {
      searchWord,
      data,
      tableData,
      delVisible,
      pageSize,
      currentPage,
      roleDialogVisible,
      dialogTitle,
      roleForm,
      roleRules,
      roleFormRef,
      permissionDialogVisible,
      groupedPermissions,
      selectedPermissionIds,
      deleteAll,
      handleSelectionChange,
      handleCurrentChange,
      handleModuleCheck,
      formatDate,
      showAddDialog,
      showEditDialog,
      resetRoleForm,
      submitRoleForm,
      showPermissionDialog,
      submitPermissionAssignment,
      confirm,
      deleteRow,
    };
  },
});
</script>

<style scoped>
.handle-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 200px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.permission-list {
  max-height: 400px;
  overflow-y: auto;
}

.permission-group {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
}

.permission-group:last-child {
  margin-bottom: 0;
}

.group-header {
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.group-content {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding-left: 20px;
}

.group-content .el-checkbox {
  margin-right: 15px;
  width: 120px;
}
</style>
