import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getRoleList, addRole, updateRole, deleteRole, updateRoleMenus, getRoleMenus } from '@/api/sys/role';
import { ElMessage } from 'element-plus';

export const useRoleStore = defineStore('sys-role', () => {
  const roleList = ref([]);
  const total = ref(0);
  const loading = ref(false);

  const fetchRoles = async (params) => {
    loading.value = true;
    try {
      const response = await getRoleList(params);
      // 适配后端分页接口
      if (response.data && typeof response.data.total !== 'undefined') {
        roleList.value = response.data.items || response.data.records;
        total.value = response.data.total;
      } else {
        // 兼容非分页列表
        roleList.value = response.data;
        total.value = response.data.length;
      }
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '获取角色列表失败');
    } finally {
      loading.value = false;
    }
  };

  const createRole = async (data) => {
    try {
      await addRole(data);
      ElMessage.success('新增成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '新增角色失败');
      throw error;
    }
  };

  const editRole = async (data) => {
    try {
      await updateRole(data);
      ElMessage.success('修改成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '修改角色失败');
      throw error;
    }
  };

  const removeRole = async (role_id) => {
    try {
      await deleteRole(role_id);
      ElMessage.success('删除成功');
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '删除角色失败');
      throw error;
    }
  };

  const getRoleWithMenus = async (role_id) => {
    try {
      const response = await getRoleMenus(role_id);
      // 假设API直接返回ID列表
      return { checkedIds: response.data || [] };
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '获取角色权限失败');
      return { checkedIds: [] };
    }
  };

  const updateRoleMenusAction = async (role_id, menu_ids) => {
    try {
      await updateRoleMenus(role_id, menu_ids);
      ElMessage.success('权限分配成功');
    } catch (error) {
      ElMessage.error(error.message || '权限分配失败');
      throw error; // 重新抛出错误，让调用层处理
    }
  };

  return {
    roleList,
    total,
    loading,
    fetchRoles,
    createRole,
    editRole,
    removeRole,
    getRoleWithMenus,
    updateRoleMenus: updateRoleMenusAction, // 统一命名
  };
});