<template>
  <div class="container">
    <div class="handle-box">
      <el-button @click="showAddDialog(null)" type="primary">添加顶级组织</el-button>
    </div>

    <div class="org-container">
      <div class="org-tree">
        <el-tree
          :data="orgTreeData"
          :props="treeProps"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
          @node-click="handleNodeClick"
        >
          <template #default="{ data }">
            <span class="custom-tree-node">
              <span>{{ data.name }}</span>
              <span class="node-actions">
                <el-button type="text" size="small" @click.stop="showAddDialog(data)">添加</el-button>
                <el-button type="text" size="small" @click.stop="showEditDialog(data)">编辑</el-button>
                <el-button type="text" size="small" @click.stop="deleteOrg(data.id)" style="color: #ff0000">删除</el-button>
              </span>
            </span>
          </template>
        </el-tree>
      </div>

      <div class="org-detail" v-if="selectedOrg">
        <el-card header="组织详情">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="组织名称">{{ selectedOrg.name }}</el-descriptions-item>
            <el-descriptions-item label="上级组织">{{ selectedOrg.parentName || '无' }}</el-descriptions-item>
            <el-descriptions-item label="排序">{{ selectedOrg.sort }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="selectedOrg.status === 1 ? 'success' : 'danger'" size="small">
                {{ selectedOrg.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ selectedOrg.description || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card header="组织成员" class="member-card">
          <div class="member-header">
            <span>成员列表 ({{ memberList.length }})</span>
            <el-button type="primary" size="small" @click="showAddMemberDialog">添加成员</el-button>
          </div>
          <el-table :data="memberList" border stripe max-height="300">
            <el-table-column prop="orgPath" label="所属组织" min-width="160"></el-table-column>
            <el-table-column prop="username" label="用户名" width="100"></el-table-column>
            <el-table-column prop="nickname" label="昵称" width="100"></el-table-column>
            <el-table-column prop="phoneNum" label="手机号" width="120"></el-table-column>
            <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
            <el-table-column label="状态" width="80">
              <template v-slot="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template v-slot="scope">
                <el-button
                  type="danger"
                  size="small"
                  :disabled="Number(scope.row.orgId) !== Number(selectedOrg?.id)"
                  @click="removeMember(scope.row)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>

    <!-- 添加/编辑组织对话框 -->
    <el-dialog :title="dialogTitle" v-model="orgDialogVisible" width="500px" @close="resetOrgForm">
      <el-form :model="orgForm" :rules="orgRules" ref="orgFormRef" label-width="100px">
        <el-form-item label="上级组织" v-if="orgForm.parentId">
          <el-input v-model="parentOrgName" disabled></el-input>
        </el-form-item>
        <el-form-item label="组织名称" prop="name">
          <el-input v-model="orgForm.name" placeholder="请输入组织名称"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="orgForm.sort" :min="0" :max="999"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="orgForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="orgForm.description" type="textarea" :rows="3" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="orgDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitOrgForm">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除提示框 -->
    <yin-del-dialog :delVisible="delVisible" @confirm="confirm" @cancelRow="delVisible = $event"></yin-del-dialog>

    <!-- 添加成员对话框 -->
    <el-dialog title="添加成员" v-model="addMemberDialogVisible" width="600px">
      <div class="available-members">
        <el-table
          ref="memberTableRef"
          :data="availableMembers"
          border
          stripe
          max-height="300"
          @selection-change="handleMemberSelectionChange"
        >
          <el-table-column type="selection" width="40"></el-table-column>
          <el-table-column prop="username" label="用户名" width="100"></el-table-column>
          <el-table-column prop="nickname" label="昵称" width="100"></el-table-column>
          <el-table-column prop="phoneNum" label="手机号" width="120"></el-table-column>
          <el-table-column prop="email" label="邮箱"></el-table-column>
        </el-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addMemberDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="confirmAddMembers">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref, reactive, computed } from "vue";
import { SystemManager, HttpManager } from "@/api/index";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import { ElMessage, ElMessageBox } from "element-plus";

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance();

    const orgTreeData = ref<any[]>([]);
    const selectedOrg = ref<any>(null);
    const treeProps = {
      children: 'children',
      label: 'name',
    };

    const orgDialogVisible = ref(false);
    const isEdit = ref(false);
    const dialogTitle = ref("添加组织");
    const orgFormRef = ref();
    const parentOrgName = ref("");
    const orgForm = reactive({
      id: null as number | null,
      parentId: null as number | null,
      name: "",
      sort: 0,
      status: 1,
      description: "",
    });

    const orgRules = {
      name: [{ required: true, message: "请输入组织名称", trigger: "blur" }],
    };

    function getErrorMessage(error: any, fallback = "操作失败") {
      if (typeof error === "string") return error;
      return error?.message || error?.msg || error?.data?.message || fallback;
    }

    async function getOrgTree() {
      try {
        const result = await SystemManager.getOrganizationTree() as any;
        if (result.data) {
          orgTreeData.value = result.data;
        }
      } catch (error: any) {
        ElMessage.error(getErrorMessage(error, "获取组织树失败"));
      }
    }

    function showAddDialog(node: any) {
      isEdit.value = false;
      dialogTitle.value = "添加组织";
      if (node) {
        orgForm.parentId = node.id;
        parentOrgName.value = node.name;
      } else {
        orgForm.parentId = null;
        parentOrgName.value = "";
      }
      orgDialogVisible.value = true;
    }

    function showEditDialog(node: any) {
      isEdit.value = true;
      dialogTitle.value = "编辑组织";
      orgForm.id = node.id;
      orgForm.parentId = node.parentId;
      orgForm.name = node.name;
      orgForm.sort = node.sort || 0;
      orgForm.status = node.status;
      orgForm.description = node.description || "";
      parentOrgName.value = node.parentName || "";
      orgDialogVisible.value = true;
    }

    function resetOrgForm() {
      orgForm.id = null;
      orgForm.parentId = null;
      orgForm.name = "";
      orgForm.sort = 0;
      orgForm.status = 1;
      orgForm.description = "";
      parentOrgName.value = "";
    }

    async function submitOrgForm() {
      const valid = await orgFormRef.value.validate().catch(() => false);
      if (!valid) return;

      try {
        if (isEdit.value) {
          const result = await SystemManager.updateOrganization(orgForm) as any;
          ElMessage.success(result.message || "更新成功");
        } else {
          const result = await SystemManager.addOrganization(orgForm) as any;
          ElMessage.success(result.message || "添加成功");
        }
        orgDialogVisible.value = false;
        getOrgTree();
      } catch (error: any) {
        ElMessage.error(getErrorMessage(error, "操作失败"));
      }
    }

    async function deleteOrg(id: number) {
      try {
        await ElMessageBox.confirm("确定要删除该组织吗？删除后不可恢复。", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        const result = await SystemManager.deleteOrganization(id) as any;
        ElMessage.success(result.message || "删除成功");
        if (selectedOrg.value?.id === id) {
          selectedOrg.value = null;
        }
        getOrgTree();
      } catch (error: any) {
        if (error !== "cancel") {
          ElMessage.error(getErrorMessage(error, "删除失败"));
        }
      }
    }

    const idx = ref(-1);
    const delVisible = ref(false);

    const memberList = ref<any[]>([]);
    const allUsers = ref<any[]>([]);
    const addMemberDialogVisible = ref(false);
    const memberTableRef = ref();
    const selectedMembers = ref<any[]>([]);

    const orgPathMap = computed(() => {
      const map = new Map<number, string>();
      const visit = (nodes: any[], parentPath: string) => {
        (nodes || []).forEach((n: any) => {
          const id = Number(n?.id);
          const name = String(n?.name ?? "");
          const path = parentPath ? `${parentPath} / ${name}` : name;
          if (!Number.isNaN(id)) {
            map.set(id, path);
          }
          const children = Array.isArray(n?.children) ? n.children : [];
          if (children.length) {
            visit(children, path);
          }
        });
      };
      visit(orgTreeData.value, "");
      return map;
    });

    function getDescendantOrgIds(node: any): number[] {
      const ids: number[] = [];
      const walk = (n: any) => {
        const id = Number(n?.id);
        if (!Number.isNaN(id)) ids.push(id);
        const children = Array.isArray(n?.children) ? n.children : [];
        children.forEach(walk);
      };
      walk(node);
      return ids;
    }

    const availableMembers = computed(() => {
      if (!selectedOrg.value) return [];
      const orgIds = new Set(getDescendantOrgIds(selectedOrg.value));
      return allUsers.value.filter(user => !orgIds.has(Number(user?.orgId)));
    });

    async function getAllUsers() {
      try {
        const result = (await SystemManager.getAllUsers()) as ResponseBody;
        if (result.data) {
          allUsers.value = result.data;
        }
      } catch (error) {
        console.error("获取用户列表失败", error);
      }
    }

    async function loadMembers(orgId: number) {
      const orgIds = selectedOrg.value ? new Set(getDescendantOrgIds(selectedOrg.value)) : new Set<number>([Number(orgId)]);
      memberList.value = allUsers.value
        .filter(user => orgIds.has(Number(user?.orgId)))
        .map((u: any) => {
          const oid = Number(u?.orgId);
          return {
            ...u,
            orgPath: orgPathMap.value.get(oid) || "-",
          };
        });
    }

    function updateLocalUserOrgId(userId: number, orgId: number | null) {
      const idx = allUsers.value.findIndex((u: any) => u?.id === userId);
      if (idx < 0) return;
      const next = { ...allUsers.value[idx], orgId };
      allUsers.value.splice(idx, 1, next);
    }

    function handleNodeClick(data: any) {
      selectedOrg.value = data;
      loadMembers(data.id);
    }

    function showAddMemberDialog() {
      if (!selectedOrg.value) {
        ElMessage.warning("请先选择一个组织");
        return;
      }
      selectedMembers.value = [];
      if (memberTableRef.value) {
        memberTableRef.value.clearSelection();
      }
      addMemberDialogVisible.value = true;
    }

    function handleMemberSelectionChange(val: any[]) {
      selectedMembers.value = val;
    }

    async function confirmAddMembers() {
      if (selectedMembers.value.length === 0) {
        ElMessage.warning("请选择要添加的成员");
        return;
      }
      if (!selectedOrg.value?.id) {
        ElMessage.warning("请先选择一个组织");
        return;
      }
      try {
        for (const member of selectedMembers.value) {
          await SystemManager.updateUser({
            id: member.id,
            orgId: selectedOrg.value.id,
          });
          updateLocalUserOrgId(member.id, selectedOrg.value.id);
        }
        ElMessage.success("添加成员成功");
        addMemberDialogVisible.value = false;
        loadMembers(selectedOrg.value.id);
      } catch (error: any) {
        ElMessage.error(getErrorMessage(error, "添加成员失败"));
      }
    }

    async function removeMember(member: any) {
      try {
        if (!selectedOrg.value?.id) {
          ElMessage.warning("请先选择一个组织");
          return;
        }
        await ElMessageBox.confirm(`确定要将成员 "${member.username}" 从组织中移除吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        await SystemManager.updateUser({
          id: member.id,
          orgId: null,
        });
        updateLocalUserOrgId(member.id, null);
        ElMessage.success("移除成员成功");
        loadMembers(selectedOrg.value.id);
      } catch (error: any) {
        if (error !== "cancel") {
          ElMessage.error(getErrorMessage(error, "移除成员失败"));
        }
      }
    }

    // 初始化
    getOrgTree();
    getAllUsers();

    return {
      orgTreeData,
      selectedOrg,
      treeProps,
      orgDialogVisible,
      dialogTitle,
      orgForm,
      orgRules,
      orgFormRef,
      parentOrgName,
      delVisible,
      memberList,
      availableMembers,
      addMemberDialogVisible,
      memberTableRef,
      handleNodeClick,
      showAddDialog,
      showEditDialog,
      resetOrgForm,
      submitOrgForm,
      deleteOrg,
      confirm,
      showAddMemberDialog,
      handleMemberSelectionChange,
      confirmAddMembers,
      removeMember,
    };
  },
});
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.org-container {
  display: flex;
  gap: 20px;
}

.org-tree {
  flex: 0 0 400px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  max-height: 600px;
  overflow-y: auto;
}

.org-detail {
  flex: 1;
}

.custom-tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 10px;
}

.node-actions {
  display: flex;
  gap: 5px;
}

.member-card {
  margin-top: 20px;
}

.member-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.available-members {
  max-height: 350px;
  overflow-y: auto;
}
</style>
