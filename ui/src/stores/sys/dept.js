import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getDeptTree, addDept, updateDept, deleteDept } from '@/api/sys/dept';
import { ElMessage } from 'element-plus';

export const useDeptStore = defineStore('sys-dept', () => {
  const deptList = ref([]);
  const deptOptions = ref([]);
  const flatDeptList = ref([]); // 新增：用于扁平列表
  const loading = ref(false);

  // 辅助函数：递归展平树
  const flatten = (tree) => {
    let result = [];
    tree.forEach(node => {
      result.push({ dept_id: node.dept_id, dept_name: node.dept_name });
      if (node.children && node.children.length > 0) {
        result = result.concat(flatten(node.children));
      }
    });
    return result;
  };

  const fetchDepts = async () => {
    loading.value = true;
    try {
      const response = await getDeptTree();
      deptList.value = response.data;
      deptOptions.value = [{ dept_id: 0, dept_name: '主类目', children: response.data }];
      flatDeptList.value = flatten(response.data); // 填充扁平列表
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '获取部门列表失败');
    } finally {
      loading.value = false;
    }
  };

  const createDept = async (data) => {
    try {
      await addDept(data);
      ElMessage.success('新增成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '新增部门失败');
      throw error;
    }
  };

  const editDept = async (data) => {
    try {
      await updateDept(data);
      ElMessage.success('修改成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '修改部门失败');
      throw error;
    }
  };

  const removeDept = async (dept_id) => {
    try {
      await deleteDept(dept_id);
      ElMessage.success('删除成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '删除部门失败');
      throw error;
    }
  };

  return { deptList, deptOptions, flatDeptList, loading, fetchDepts, createDept, editDept, removeDept };
});
