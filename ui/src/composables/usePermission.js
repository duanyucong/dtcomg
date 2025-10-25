
/**
 * 权限相关的 Composable
 * 提供了与 `permission.js` mixin 相同的功能，但更符合 Vue 3 的组合式 API 风格。
 * 
 * @author 系统管理员
 * @since 2025-09-22
 */
import { permissionManager } from '@/utils/permission';

/**
 * @description 权限判断
 * @returns {Object} 包含所有权限检查方法的对象
 */
export function usePermission() {
  return {
    /**
     * 检查是否有指定权限
     * @param {string} permission - 权限标识
     * @returns {boolean} 是否有权限
     */
    hasPermission: permissionManager.hasPermission.bind(permissionManager),

    /**
     * 检查是否有任意一个权限
     * @param {string[]} permissions - 权限标识数组
     * @returns {boolean} 是否有任意权限
     */
    hasAnyPermission: permissionManager.hasAnyPermission.bind(permissionManager),

    /**
     * 检查是否拥有所有权限
     * @param {string[]} permissions - 权限标识数组
     * @returns {boolean} 是否拥有所有权限
     */
    hasAllPermissions: permissionManager.hasAllPermissions.bind(permissionManager),

    /**
     * 过滤菜单列表，只保留有权限的菜单
     * @param {Array} menus - 菜单列表
     * @returns {Array} 过滤后的菜单列表
     */
    filterMenusByPermission: permissionManager.filterMenus.bind(permissionManager),

    /**
     * 获取当前用户的权限列表
     * @returns {Array} 权限列表
     */
    getCurrentPermissions: permissionManager.getPermissionList.bind(permissionManager),

    /**
     * 检查是否是管理员
     * @returns {boolean} 是否是管理员
     */
    isAdmin: permissionManager.isAdmin.bind(permissionManager),
  };
}
