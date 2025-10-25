<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>菜单管理</h2>
      <div class="header-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增菜单</el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <el-card>
      <el-table
        v-loading="loading"
        :data="menuList"
        row-key="menu_id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="menu_name" label="菜单名称" />
        <el-table-column prop="menu_type" label="菜单类型" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.menu_type === 'M' ? 'primary' : scope.row.menu_type === 'C' ? 'success' : 'warning'">
              {{ scope.row.menu_type === 'M' ? '目录' : scope.row.menu_type === 'C' ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="order_num" label="排序" align="center" width="80" />
        <el-table-column prop="perms" label="权限标识" align="center" />
        <el-table-column prop="component" label="组件路径" align="center" />
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="is_show" label="导航显示" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.is_show === '1' ? 'success' : 'warning'">{{ scope.row.is_show === '1' ? '显示' : '隐藏' }}</el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column label="创建时间" prop="create_time" align="center" width="180">
          <template #default="scope">
            <span>{{ scope.row.create_time }}</span>
          </template>
        </el-table-column> -->
        <el-table-column label="操作" align="right" width="300" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.menu_type === 'M' || scope.row.menu_type === 'C'" type="success" text icon="Plus" @click="handleQuickAdd(scope.row)">新增</el-button>
            <el-button type="primary" text icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="danger" text icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑菜单弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="上级菜单" prop="parent_id">
          <el-tree-select
            v-model="formData.parent_id"
            :data="menuOptions"
            :props="{ value: 'menu_id', label: 'menu_name', children: 'children' }"
            value-key="menu_id"
            placeholder="选择上级菜单"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menu_type">
          <el-radio-group v-model="formData.menu_type">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menu_name">
          <el-input v-model="formData.menu_name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="order_num">
          <el-input-number v-model="formData.order_num" :min="0" />
        </el-form-item>
        <el-form-item v-if="formData.menu_type !== 'F'" label="路由地址" prop="path">
          <el-input v-model="formData.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item v-if="formData.menu_type === 'C'" label="组件路径" prop="component">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item v-if="formData.menu_type !== 'M'" label="权限标识" prop="perms">
          <el-input v-model="formData.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="菜单状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否显示" prop="is_show" v-if="formData && formData.menu_type !== 'F'">
          <el-radio-group v-model="formData.is_show">
            <el-radio label="1">显示</el-radio>
            <el-radio label="0">隐藏</el-radio>
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
import { useMenuStore } from '@/stores/sys/menu';
import { ElMessage, ElMessageBox } from 'element-plus';

const menuStore = useMenuStore();

const menuList = computed(() => menuStore.menuList);
const menuOptions = computed(() => menuStore.menuOptions);
const loading = computed(() => menuStore.loading);

// --- 弹窗相关 ---
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const formData = ref({});
const formRules = {
  parent_id: [{ required: true, message: '上级菜单不能为空', trigger: 'change' }],
  menu_type: [{ required: true, message: '菜单类型不能为空', trigger: 'change' }],
  menu_name: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  order_num: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
  path: [{ required: true, message: '路由地址不能为空', trigger: 'blur' }],
};

/** 查询菜单列表 */
function getList() {
  menuStore.fetchRawMenus();
}

/** 重置表单 */
function resetForm() {
  formData.value = { parent_id: 0, menu_type: 'M', order_num: 0, status: '0', is_show: '1' };
  if (formRef.value) {
    formRef.value.resetFields();
  }
}

/** 新增按钮操作 */
function handleAdd() {
  resetForm();
  dialogTitle.value = '新增菜单';
  dialogVisible.value = true;
}

/** 快速新增子菜单按钮操作 */
function handleQuickAdd(row) {
  handleAdd();
  nextTick(() => {
    formData.value.parent_id = row.menu_id;
  });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  resetForm();
  nextTick(() => {
    formData.value = { ...row };
    dialogTitle.value = '修改菜单';
    dialogVisible.value = true;
  });
}

/** 提交按钮 */
async function handleSubmit() {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const isUpdate = !!formData.value.menu_id;
      try {
        if (isUpdate) {
          await menuStore.editMenu(formData.value);
        } else {
          await menuStore.createMenu(formData.value);
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
  ElMessageBox.confirm(`是否确认删除菜单名为"${row.menu_name}"的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await menuStore.removeMenu(row.menu_id);
    getList();
  }).catch(() => {});
}



onMounted(() => {
  getList();
});
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: var(--el-font-size-extra-large);
  font-weight: 600;
}
</style>
