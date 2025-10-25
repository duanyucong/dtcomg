<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>角色管理</h2>
      <div class="header-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增角色</el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="queryParams" ref="queryForm" :inline="true" @submit.prevent="handleQuery">
        <el-form-item label="角色名称" prop="role_name">
          <el-input v-model="queryParams.role_name" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="权限字符" prop="role_key">
          <el-input v-model="queryParams.role_key" placeholder="请输入权限字符" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 内容区域 -->
    <el-card>
      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="roleList" border>
        <el-table-column label="角色编号" prop="role_id" align="center" width="100" />
        <el-table-column label="角色名称" prop="role_name" align="center" />
        <el-table-column label="权限字符" prop="role_key" align="center" />
        <el-table-column label="创建时间" prop="create_time" align="center" width="180">
          <template #default="scope">
            <span>{{ scope.row.create_time }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="right" width="300" fixed="right">
          <template #default="scope">
            <el-button type="primary" text icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="info" text icon="Setting" @click="handlePermission(scope.row)">权限</el-button>
            <el-button type="danger" text icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.page_size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" 
          :total="Number(total)"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑角色弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="角色名称" prop="role_name">
          <el-input v-model="formData.role_name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="role_key">
          <el-input v-model="formData.role_key" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="显示排序" prop="role_sort">
          <el-input-number v-model="formData.role_sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog :title="`分配权限 - ${currentRoleName}`" v-model="permissionDialogVisible" width="500px" @close="resetPermissionDialog">
      <el-tree
        ref="menuTreeRef"
        :data="menuList" 
        show-checkbox
        node-key="menu_id"
        :props="{ label: 'menu_name', children: 'children' }"
        :default-checked-keys="checkedKeys"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handlePermissionSubmit">确 定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue';
import { useRoleStore } from '@/stores/sys/role';
import { useMenuStore } from '@/stores/sys/menu'; // 引入 menuStore
import { ElMessage, ElMessageBox } from 'element-plus';

const roleStore = useRoleStore();
const menuStore = useMenuStore(); // 实例化 menuStore

const roleList = computed(() => roleStore.roleList);
const total = computed(() => roleStore.total);
const loading = computed(() => roleStore.loading);
const menuList = computed(() => menuStore.menuList); // 从 menuStore 获取菜单

const queryParams = reactive({
  page: 1,
  page_size: 10,
  role_name: '',
  role_key: ''
});

// --- 角色表单弹窗 ---
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const formData = ref({});
const formRules = {
  role_name: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
  role_key: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
};

// --- 分配权限弹窗 ---
const permissionDialogVisible = ref(false);
const menuTreeRef = ref(null);
const checkedKeys = ref([]); // 重命名以匹配模板
const currentRole = ref(null); // 用于存储当前操作的角色
const currentRoleName = ref(''); // 当前角色名称

/** 查询角色列表 */
function getList() {
  roleStore.fetchRoles(queryParams);
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.page = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.page = 1;
  queryParams.role_name = '';
  queryParams.role_key = '';
  handleQuery();
}

/** 重置表单 */
function resetForm() {
  formData.value = { roleSort: 0, status: '0' };
  if (formRef.value) {
    formRef.value.resetFields();
  }
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm();
  dialogTitle.value = '新增角色';
  dialogVisible.value = true;
}

/** 修改按钮操作 */
function handleUpdate(row) {
  resetForm();
  nextTick(() => {
    formData.value = { ...row };
    dialogTitle.value = '修改角色';
    dialogVisible.value = true;
  });
}

/** 提交按钮 */
async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const isUpdate = !!formData.value.role_id;
      try {
        if (isUpdate) {
          await roleStore.editRole(formData.value);
        } else {
          await roleStore.createRole(formData.value);
        }
        dialogVisible.value = false;
        getList();
      } catch (error) {
        // Error handled by store
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  ElMessageBox.confirm(`是否确认删除角色名为"${row.role_name}"的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await roleStore.removeRole(row.role_id);
    getList();
  }).catch(() => {});
}

/** 重置权限弹窗状态 */
function resetPermissionDialog() {
  checkedKeys.value = [];
  currentRole.value = null;
  currentRoleName.value = '';
  if (menuTreeRef.value) {
    menuTreeRef.value.setCheckedKeys([]);
  }
}

/** 分配权限按钮操作 */
async function handlePermission(row) {
  currentRole.value = row;
  currentRoleName.value = row.role_name || '未知角色';
  const res = await roleStore.getRoleWithMenus(row.role_id);
  checkedKeys.value = res.checkedIds;
  permissionDialogVisible.value = true;
}

/** 提交权限分配 */
async function handlePermissionSubmit() {
  const selectedKeys = menuTreeRef.value.getCheckedKeys();
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys();
  const allKeys = [...selectedKeys, ...halfCheckedKeys];
  try {
    await roleStore.updateRoleMenus(currentRole.value.role_id, allKeys);
    permissionDialogVisible.value = false;
    resetPermissionDialog(); // 重置权限弹窗状态
    getList();
  } catch (error) {
    // 使用toast提示错误
    const errorMessage = error.response?.data?.message || error.message || '操作失败';
    ElMessage.error(errorMessage);
  }
}



onMounted(() => {
  getList();
  if (menuStore.rawMenus.length === 0) {
      menuStore.fetchRawMenus();
  }
});
</script>

<style scoped>
.page-container {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: var(--el-font-size-extra-large);
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.header-actions .el-button {
  font-weight: 500;
}

.search-section {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }
}
</style>