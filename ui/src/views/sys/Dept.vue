<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>部门管理</h2>
      <div class="header-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增部门</el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="deptList"
        row-key="dept_id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="dept_name" label="部门名称" />
        <el-table-column prop="order_num" label="排序" align="center" width="100" />
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" align="center" width="180">
          <template #default="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="right" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" text icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="danger" text icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑部门弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="上级部门" prop="parent_id">
          <el-tree-select
            v-model="formData.parent_id"
            :data="deptOptions"
            :props="{ value: 'dept_id', label: 'dept_name', children: 'children' }"
            value-key="dept_id"
            placeholder="选择上级部门"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="dept_name">
          <el-input v-model="formData.dept_name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="order_num">
          <el-input-number v-model="formData.order_num" :min="0" />
        </el-form-item>
        <el-form-item label="部门状态" prop="status">
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

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue';
import { useDeptStore } from '@/stores/sys/dept';
import { ElMessage, ElMessageBox } from 'element-plus';

const deptStore = useDeptStore();

const deptList = computed(() => deptStore.deptList);
const deptOptions = computed(() => deptStore.deptOptions);
const loading = computed(() => deptStore.loading);

// --- 弹窗相关 ---
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const formData = ref({});
const formRules = {
  parent_id: [{ required: true, message: '上级部门不能为空', trigger: 'change' }],
  dept_name: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }],
  order_num: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
};

/** 查询部门列表 */
function getList() {
  deptStore.fetchDepts();
}

/** 重置表单 */
function resetForm() {
  formData.value = { parent_id: 0, order_num: 0, status: '0' };
  if (formRef.value) {
    formRef.value.resetFields();
  }
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm();
  dialogTitle.value = '新增部门';
  dialogVisible.value = true;
}

/** 修改按钮操作 */
function handleUpdate(row) {
  resetForm();
  nextTick(() => {
    formData.value = { ...row };
    dialogTitle.value = '修改部门';
    dialogVisible.value = true;
  });
}

/** 提交按钮 */
async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const isUpdate = !!formData.value.dept_id;
      try {
        if (isUpdate) {
          await deptStore.editDept(formData.value);
        } else {
          await deptStore.createDept(formData.value);
        }
        dialogVisible.value = false;
        getList();
      } catch (error) {
        const errorMessage = error.response?.data?.message || error.message || '操作失败';
        ElMessage.error(errorMessage);
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  ElMessageBox.confirm(`是否确认删除部门名为"${row.dept_name}"的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deptStore.removeDept(row.dept_id);
    getList();
  }).catch(() => {});
}



onMounted(() => {
  getList();
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
e>
