<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="deleteAll" type="danger" plain>批量删除</el-button>
      <el-button @click="showAddDialog">添加用户</el-button>
      <el-input v-model="searchWord" placeholder="搜索用户" class="search-input" clearable @clear="getData"></el-input>
      <el-select v-model="filterRole" placeholder="筛选角色" clearable @change="handleFilterChange">
        <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id"></el-option>
      </el-select>
    </div>

    <el-table height="550px" border stripe :data="data" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="50" align="center"></el-table-column>
      <el-table-column label="用户名" prop="username" width="120" align="center"></el-table-column>
      <el-table-column label="昵称" prop="nickname" width="100" align="center"></el-table-column>
      <el-table-column label="邮箱" prop="email" width="180" align="center"></el-table-column>
      <el-table-column label="手机号" prop="phone" width="120" align="center"></el-table-column>
      <el-table-column label="角色" prop="roleNames" width="150" align="center">
        <template v-slot="scope">
          <el-tag v-for="role in scope.row.roles" :key="role.id" size="small" style="margin-right: 4px">
            {{ role.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template v-slot="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template v-slot="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template v-slot="scope">
          <el-button type="primary" size="small" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button type="warning" size="small" @click="showRoleDialog(scope.row)">分配角色</el-button>
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

    <!-- 添加/编辑用户对话框 -->
    <el-dialog :title="dialogTitle" v-model="userDialogVisible" width="500px" @close="resetUserForm">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" placeholder="请选择角色">
            <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitUserForm">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog title="分配角色" v-model="roleDialogVisible" width="500px">
      <el-checkbox-group v-model="selectedRoleIds" class="role-checkbox-group">
        <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">{{ role.name }}</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitRoleAssignment">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除提示框 -->
    <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, watch, ref, reactive, computed } from "vue";
import { SystemManager } from "@/api/index";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import { ElMessage } from "element-plus";

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();

    const tableData = ref<any[]>([]);
    const tempDate = ref<any[]>([]);
    const roleList = ref<any[]>([]);
    const pageSize = ref(10);
    const currentPage = ref(1);
    const searchWord = ref("");
    const filterRole = ref<number | null>(null);

    const data = computed(() => {
      return tableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
    });

    watch(searchWord, () => {
      if (searchWord.value === "") {
        tableData.value = tempDate.value;
      } else {
        tableData.value = tempDate.value.filter(item =>
          item.username.includes(searchWord.value) ||
          item.nickname?.includes(searchWord.value) ||
          item.email?.includes(searchWord.value)
        );
      }
      currentPage.value = 1;
    });

    watch(filterRole, () => {
      handleFilterChange();
    });

    async function getData() {
      try {
        const result = await SystemManager.getAllUsers() as any;
        if (result.data) {
          tableData.value = result.data;
          tempDate.value = result.data;
        }
      } catch (error) {
        ElMessage.error("获取用户列表失败");
      }
    }

    async function getRoles() {
      try {
        const result = await SystemManager.getAllRoles() as any;
        if (result.data) {
          roleList.value = result.data;
        }
      } catch (error) {
        ElMessage.error("获取角色列表失败");
      }
    }

    function handleFilterChange() {
      if (!filterRole.value) {
        tableData.value = tempDate.value;
      } else {
        tableData.value = tempDate.value.filter(item =>
          item.roles?.some((role: any) => role.id === filterRole.value)
        );
      }
      currentPage.value = 1;
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

    // 用户表单相关
    const userDialogVisible = ref(false);
    const isEdit = ref(false);
    const dialogTitle = ref("添加用户");
    const userFormRef = ref();
    const userForm = reactive({
      id: null as number | null,
      username: "",
      nickname: "",
      password: "",
      email: "",
      phone: "",
      status: 1,
      roleId: null as number | null,
    });

    const userRules = {
      username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
      email: [
        { required: true, message: "请输入邮箱", trigger: "blur" },
        { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" },
      ],
    };

    function showAddDialog() {
      isEdit.value = false;
      dialogTitle.value = "添加用户";
      userDialogVisible.value = true;
    }

    function showEditDialog(row: any) {
      isEdit.value = true;
      dialogTitle.value = "编辑用户";
      userForm.id = row.id;
      userForm.username = row.username;
      userForm.nickname = row.nickname || "";
      userForm.email = row.email || "";
      userForm.phone = row.phone || "";
      userForm.status = row.status;
      userDialogVisible.value = true;
    }

    function resetUserForm() {
      userForm.id = null;
      userForm.username = "";
      userForm.nickname = "";
      userForm.password = "";
      userForm.email = "";
      userForm.phone = "";
      userForm.status = 1;
      userForm.roleId = null;
    }

    async function submitUserForm() {
      const valid = await userFormRef.value.validate().catch(() => false);
      if (!valid) return;

      try {
        if (isEdit.value) {
          const result = await SystemManager.updateUser(userForm) as any;
          ElMessage.success(result.message || "更新成功");
        } else {
          const result = await SystemManager.addUser(userForm) as any;
          ElMessage.success(result.message || "添加成功");
        }
        userDialogVisible.value = false;
        getData();
      } catch (error: any) {
        ElMessage.error(error.message || "操作失败");
      }
    }

    // 角色分配相关
    const roleDialogVisible = ref(false);
    const selectedRoleIds = ref<number[]>([]);
    const currentUserId = ref<number | null>(null);

    function showRoleDialog(row: any) {
      currentUserId.value = row.id;
      selectedRoleIds.value = row.roles?.map((r: any) => r.id) || [];
      roleDialogVisible.value = true;
    }

    async function submitRoleAssignment() {
      if (!currentUserId.value) return;
      try {
        const result = await SystemManager.assignRoles({
          userId: currentUserId.value,
          userType: 'consumer',
          roleIds: selectedRoleIds.value,
        }) as any;
        ElMessage.success(result.message || "分配成功");
        roleDialogVisible.value = false;
        getData();
      } catch (error: any) {
        ElMessage.error(error.message || "分配失败");
      }
    }

    // 删除相关
    const idx = ref(-1);
    const multipleSelection = ref<any[]>([]);
    const delVisible = ref(false);

    async function confirm() {
      try {
        const result = await SystemManager.deleteUser(idx.value) as any;
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
      if (multipleSelection.value.length === 0) {
        ElMessage.warning("请先选择要删除的用户");
        return;
      }
      for (const item of multipleSelection.value) {
        idx.value = item.id;
        await SystemManager.deleteUser(item.id) as any;
      }
      ElMessage.success("批量删除成功");
      multipleSelection.value = [];
      getData();
    }

    // 初始化
    getData();
    getRoles();

    return {
      searchWord,
      data,
      tableData,
      delVisible,
      pageSize,
      currentPage,
      roleList,
      filterRole,
      userDialogVisible,
      dialogTitle,
      userForm,
      userRules,
      userFormRef,
      isEdit,
      roleDialogVisible,
      selectedRoleIds,
      deleteAll,
      handleSelectionChange,
      handleCurrentChange,
      handleFilterChange,
      formatDate,
      showAddDialog,
      showEditDialog,
      resetUserForm,
      submitUserForm,
      showRoleDialog,
      submitRoleAssignment,
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
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.role-checkbox-group {
  display: flex;
  flex-direction: column;
}

:deep(.el-select-dropdown__list) {
  display: flex;
  flex-direction: column;
}
</style>