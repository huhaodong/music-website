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
                <el-button type="danger" size="small" @click="removeMember(scope.row)">移除</el-button>
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
import { defineComponent, getCurrentInstance, ref, reactive } from "vue";
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

    function extractOrgArray(payload: any): any[] {
      // 兼容不同后端分页/包装结构：data / data.records / data.list
      if (Array.isArray(payload)) return payload;
      if (Array.isArray(payload?.records)) return payload.records;
      if (Array.isArray(payload?.list)) return payload.list;
      return [];
    }

    function normalizeOrgTree(nodes: any[], parentName = ""): any[] {
      return (nodes || []).map((raw: any) => {
        // 兼容 children 字段命名差异
        const rawChildren =
          raw?.children ??
          raw?.childrenList ??
          raw?.childList ??
          raw?.childrens ??
          [];

        const childrenArr = extractOrgArray(rawChildren);
        const normalized: any = {
          ...raw,
          // 兜底字段，避免空渲染/细节页显示异常
          id: raw?.id,
          name: raw?.name ?? raw?.orgName ?? raw?.title ?? "",
          sort: raw?.sort ?? 0,
          status: raw?.status ?? 1,
          parentId: raw?.parentId ?? raw?.parent_id ?? null,
          parentName: raw?.parentName ?? parentName,
          children: childrenArr.length ? normalizeOrgTree(childrenArr, raw?.name ?? "") : [],
        };

        // 子节点排序（若后端未排序）
        if (Array.isArray(normalized.children) && normalized.children.length) {
          normalized.children.sort((a: any, b: any) => (a?.sort ?? 0) - (b?.sort ?? 0));
        }
        return normalized;
      });
    }

    function buildTreeFromList(list: any[]): any[] {
      const items = (list || []).map((raw: any) => ({
        ...raw,
        id: raw?.id,
        name: raw?.name ?? raw?.orgName ?? "",
        sort: raw?.sort ?? 0,
        status: raw?.status ?? 1,
        // 兼容 parentId 为 0 / null 两种根节点表示
        parentId: raw?.parentId ?? raw?.parent_id ?? null,
        children: [] as any[],
      }));

      const byId = new Map<number, any>();
      for (const item of items) {
        if (item?.id != null) byId.set(item.id, item);
      }

      const roots: any[] = [];
      for (const item of items) {
        const pid = item.parentId === 0 ? null : item.parentId;
        if (pid == null) {
          roots.push(item);
          continue;
        }
        const parent = byId.get(pid);
        if (!parent) {
          // 找不到父节点时降级为根节点，避免整棵树丢失
          roots.push(item);
          continue;
        }
        item.parentName = parent.name;
        parent.children.push(item);
      }

      const sortRecursively = (nodes: any[]) => {
        nodes.sort((a: any, b: any) => (a?.sort ?? 0) - (b?.sort ?? 0));
        for (const n of nodes) {
          if (Array.isArray(n.children) && n.children.length) sortRecursively(n.children);
        }
      };
      sortRecursively(roots);

      return roots;
    }

    async function getOrgTree() {
      try {
        // 优先使用后端 tree 接口
        const treeRes = (await SystemManager.getOrganizationTree()) as any;
        const treeData = extractOrgArray(treeRes?.data);
        if (Array.isArray(treeData) && treeData.length) {
          orgTreeData.value = normalizeOrgTree(treeData);
          return;
        }

        // tree 为空时，回退到 list 并在前端构建树（常见原因：根节点 parentId 用 0 表示导致后端 buildTree 过滤掉）
        const listRes = (await SystemManager.getAllOrganizations()) as any;
        const listData = extractOrgArray(listRes?.data);
        orgTreeData.value = normalizeOrgTree(buildTreeFromList(listData));
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
      memberList.value = allUsers.value.filter(user => user.orgId === orgId);
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
      try {
        for (const member of selectedMembers.value) {
          await SystemManager.updateUser({
            id: member.id,
            orgId: selectedOrg.value.id,
          });
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
        await ElMessageBox.confirm(`确定要将成员 "${member.username}" 从组织中移除吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
        await SystemManager.updateUser({
          id: member.id,
          orgId: null,
        });
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
