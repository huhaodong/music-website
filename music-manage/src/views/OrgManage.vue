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
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref, reactive } from "vue";
import { SystemManager } from "@/api/index";
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

    async function getOrgTree() {
      try {
        const result = await SystemManager.getOrganizationTree() as any;
        if (result.data) {
          orgTreeData.value = result.data;
        }
      } catch (error) {
        ElMessage.error("获取组织树失败");
      }
    }

    function handleNodeClick(data: any) {
      selectedOrg.value = data;
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
        ElMessage.error(error.message || "操作失败");
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
          ElMessage.error(error.message || "删除失败");
        }
      }
    }

    const idx = ref(-1);
    const delVisible = ref(false);

    async function confirm() {
      try {
        const result = await SystemManager.deleteOrganization(idx.value) as any;
        ElMessage.success(result.message || "删除成功");
        delVisible.value = false;
        getOrgTree();
      } catch (error: any) {
        ElMessage.error(error.message || "删除失败");
      }
    }

    // 初始化
    getOrgTree();

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
      handleNodeClick,
      showAddDialog,
      showEditDialog,
      resetOrgForm,
      submitOrgForm,
      deleteOrg,
      confirm,
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
</style>