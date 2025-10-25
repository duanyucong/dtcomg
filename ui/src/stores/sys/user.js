import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getUserList, addUser, updateUser, deleteUser, updateUserRoles, resetUserPassword } from '@/api/sys/user';
import { ElMessage } from 'element-plus';

export const useUserStore = defineStore('sys-user', () => {
  const userList = ref([]);
  const total = ref(0);
  const loading = ref(false);

  const fetchUsers = async (params) => {
    loading.value = true;
    try {
      const response = await getUserList(params);
      userList.value = response.data.items;
      total.value = response.data.total;
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '获取用户列表失败');
    } finally {
      loading.value = false;
    }
  };

  const createUser = async (data) => {
    try {
      await addUser(data);
      ElMessage.success('新增成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '新增用户失败');
      throw error;
    }
  };

  const editUser = async (data) => {
    try {
      await updateUser(data);
      ElMessage.success('修改成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '修改用户失败');
      throw error;
    }
  };

  const removeUser = async (user_id) => {
    try {
      await deleteUser(user_id);
      ElMessage.success('删除成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '删除用户失败');
      throw error;
    }
  };

  const assignRoles = async (user_id, role_ids) => {
    try {
      await updateUserRoles(user_id, role_ids);
      ElMessage.success('角色分配成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '角色分配失败');
      throw error;
    }
  };

  const resetPassword = async (user_id, new_password) => {
    try {
      await resetUserPassword(user_id, new_password);
      ElMessage.success('密码重置成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '密码重置失败');
      throw error;
    }
  };

  return { userList, total, loading, fetchUsers, createUser, editUser, removeUser, assignRoles, resetPassword };
});
