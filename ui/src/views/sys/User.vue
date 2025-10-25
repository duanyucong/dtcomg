<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增用户</el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :model="queryParams" ref="queryForm" :inline="true" @submit.prevent="handleQuery">
        <el-form-item label="用户名" prop="user_name">
          <el-input v-model="queryParams.user_name" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phone_number">
          <el-input v-model="queryParams.phone_number" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status" style="width: 200px;">
          <el-select v-model="queryParams.status" placeholder="用户状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="dept_id" style="width: 200px;">
          <el-select v-model="queryParams.dept_id" placeholder="请选择部门" clearable style="width: 100%;">
            <el-option v-for="item in flatDeptList" :key="item.dept_id" :label="item.dept_name" :value="item.dept_id" />
          </el-select>
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
      <el-table v-loading="loading" :data="userList" border>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="用户编号" prop="user_id" align="center" width="100" />
        <el-table-column label="用户名" prop="user_name" align="center" />
        <el-table-column label="昵称" prop="nick_name" align="center" />
        <el-table-column label="部门" prop="dept_id" align="center">
          <template #default="scope">
            <span>{{ scope.row.dept_id && deptMap[scope.row.dept_id] ? deptMap[scope.row.dept_id] : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" prop="phone_number" align="center" width="150" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用'
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" align="center" min-width="150">
          <template #default="scope">
            <el-tag v-for="role_id in scope.row.role_ids" :key="role_id" size="small" type="info"
              style="margin-right: 5px;">
              {{ roleNameMap[role_id] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="create_time" align="center" width="180">
          <template #default="scope">
            <span>{{ scope.row.create_time }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="right" width="380" fixed="right">
          <template #default="scope">
            <el-button type="primary" text icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button v-if="!isAdminUser(scope.row)" type="warning" text icon="Key"
              @click="handleResetPassword(scope.row)">密码</el-button>
            <el-button v-if="!isAdminUser(scope.row)" type="info" text icon="Setting"
              @click="handleAssignRole(scope.row)">角色</el-button>
            <el-button v-if="!isAdminUser(scope.row)" type="danger" text icon="Delete"
              @click="handleDelete(scope.row)">删除</el-button>
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
          @size-change="handleQuery"
           @current-change="handleQuery" />
      </div>
    </el-card>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="user_name">
          <el-input v-model="formData.user_name" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nick_name">
          <el-input v-model="formData.nick_name" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="归属部门" prop="dept_id">
          <el-select v-model="formData.dept_id" placeholder="请选择归属部门" style="width: 100%;">
            <el-option v-for="item in flatDeptList" :key="item.dept_id" :label="item.dept_name" :value="item.dept_id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!formData.user_id" label="密码" prop="password" :rules="getPasswordRules()">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone_number">
          <el-input v-model="formData.phone_number" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role_ids">
          <el-select v-model="formData.role_ids" multiple placeholder="请选择角色" style="width: 100%;">
            <el-option v-for="role in allRoles" :key="role.role_id" :label="role.role_name" :value="role.role_id" />
          </el-select>
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

    <!-- 分配角色弹窗 -->
    <el-dialog :title="`分配角色 - ${currentNickName}`" v-model="roleDialogVisible" width="500px">
      <el-checkbox-group v-model="assignedRoleIds">
        <el-checkbox v-for="role in allRoles" :key="role.role_id" :label="role.role_id">{{ role.role_name
          }}</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleRoleSubmit">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog :title="`重置密码 - ${currentNickName}`" v-model="passwordDialogVisible" width="500px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handlePasswordSubmit">确 定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue';
import { useUserStore } from '@/stores/sys/user';
import { useDeptStore } from '@/stores/sys/dept';
import { getRoleList } from '@/api/sys/role';
import { ElMessage, ElMessageBox } from 'element-plus';

const userStore = useUserStore();
const deptStore = useDeptStore();

const userList = computed(() => userStore.userList);
const total = computed(() => userStore.total);
const loading = computed(() => userStore.loading);
const flatDeptList = computed(() => deptStore.flatDeptList);

const deptMap = computed(() => {
  const map = {};
  deptStore.flatDeptList.forEach(item => {
    map[item.dept_id] = item.dept_name;
  });
  return map;
});

const queryParams = reactive({
  page: 1,
  page_size: 10,
  user_name: '',
  phone_number: '',
  status: '',
  dept_id: undefined
});

// --- 用户表单弹窗相关 ---
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const formData = ref({});

const formRules = {
  user_name: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  nick_name: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
  phone_number: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
};

// 动态密码验证规则
const getPasswordRules = () => {
  return formData.value.user_id ? [] : [{ required: true, message: '密码不能为空', trigger: 'blur' }];
};

// --- 角色分配弹窗相关 ---
const roleDialogVisible = ref(false);
const allRoles = ref([]);
const assignedRoleIds = ref([]);
const currentUserId = ref(null);
const currentNickName = ref('');

// --- 重置密码弹窗相关 ---
const passwordDialogVisible = ref(false);
const passwordFormRef = ref(null);
const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
});

const passwordRules = {
  newPassword: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const roleNameMap = computed(() => {
  const map = {};
  allRoles.value.forEach(role => {
    map[role.role_id] = role.role_name;
  });
  return map;
});


/** 查询用户列表 */
function getList() {
  userStore.fetchUsers(queryParams);
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.page = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.page = 1;
  queryParams.user_name = '';
  queryParams.phone_number = '';
  queryParams.status = '';
  queryParams.dept_id = undefined;
  handleQuery();
}

/** 重置表单 */
function resetForm() {
  formData.value = { status: '0' };
  if (formRef.value) {
    formRef.value.resetFields();
  }
}

/** 新增按钮操作 */
async function handleAdd() {
  resetForm();
  await deptStore.fetchDepts();
  await fetchAllRoles(); // 获取角色列表
  dialogTitle.value = '新增用户';
  dialogVisible.value = true;
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  resetForm();
  await deptStore.fetchDepts();
  await fetchAllRoles(); // 获取角色列表
  nextTick(() => {
    formData.value = { ...row };
    dialogTitle.value = '修改用户';
    dialogVisible.value = true;
  });
}

/** 提交按钮 */
async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const isUpdate = !!formData.value.user_id;
      try {
        if (isUpdate) {
          await userStore.editUser(formData.value);
          // 如果是更新用户，无论是否有角色选择，都更新角色（包括清空角色）
          await userStore.assignRoles(formData.value.user_id, formData.value.role_ids || []);
        } else {
          // 创建用户
          await userStore.createUser(formData.value);
          // 如果创建时选择了角色，需要获取新创建的用户ID并分配角色
          if (formData.value.role_ids && formData.value.role_ids.length > 0) {
            // 重新获取用户列表以获取最新创建的用户ID
            await userStore.fetchUsers(queryParams);
            const newUser = userList.value.find(user => user.user_name === formData.value.user_name);
            if (newUser && newUser.user_id) {
              await userStore.assignRoles(newUser.user_id, formData.value.role_ids);
            }
          }
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
  ElMessageBox.confirm(`是否确认删除用户名为"${row.user_name}"的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await userStore.removeUser(row.user_id);
    getList();
  }).catch(() => { });
}

/** 分配角色按钮操作 */
async function handleAssignRole(row) {
  currentUserId.value = row.user_id;
  currentNickName.value = row.nick_name || row.user_name || '未知用户';
  assignedRoleIds.value = row.role_ids || [];
  roleDialogVisible.value = true;
}

/** 提交角色分配 */
async function handleRoleSubmit() {
  try {
    await userStore.assignRoles(currentUserId.value, assignedRoleIds.value);
    roleDialogVisible.value = false;
    getList();
  } catch (error) {
    // Error handled by store
  }
}

/** 重置密码按钮操作 */
function handleResetPassword(row) {
  currentUserId.value = row.user_id;
  currentNickName.value = row.nick_name || row.user_name || '未知用户';
  passwordForm.value = {
    newPassword: '',
    confirmPassword: ''
  };
  passwordDialogVisible.value = true;
}

/** 提交密码重置 */
async function handlePasswordSubmit() {
  if (!passwordFormRef.value) return;

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.resetPassword(currentUserId.value, passwordForm.value.newPassword);
        passwordDialogVisible.value = false;
      } catch (error) {
        // Error handled by store
      }
    }
  });
}

/** 获取所有角色列表 */
async function fetchAllRoles() {
  try {
    const response = await getRoleList({ page: 1, pageSize: 1000 });
    allRoles.value = response.data.items;
  } catch (error) {
    ElMessage.error('获取角色列表失败');
  }
}

/** 判断用户是否为管理员 */
function isAdminUser(user) {
  return user.user_name === 'admin';
}



onMounted(() => {
  getList();
  fetchAllRoles();
  deptStore.fetchDepts();
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
