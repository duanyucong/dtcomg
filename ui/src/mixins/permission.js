/**
 * @deprecated since 2025-09-22. Please use the `usePermission` composable instead.
 * 
 * 权限相关混入
 * 提供权限检查和菜单过滤功能
 */

import { permissionManager } from '@/utils/permission'

export default {
  methods: {
    /**
     * 检查是否有指定权限
     * @param {string} permission - 权限标识
     * @returns {boolean} 是否有权限
     */
    hasPermission(permission) {
      return permissionManager.hasPermission(permission)
    },

    /**
     * 检查是否有任意一个权限
     * @param {string[]} permissions - 权限标识数组
     * @returns {boolean} 是否有任意权限
     */
    hasAnyPermission(permissions) {
      return permissionManager.hasAnyPermission(permissions)
    },

    /**
     * 检查是否拥有所有权限
     * @param {string[]} permissions - 权限标识数组
     * @returns {boolean} 是否拥有所有权限
     */
    hasAllPermissions(permissions) {
      return permissionManager.hasAllPermissions(permissions)
    },

    /**
     * 过滤菜单列表，只保留有权限的菜单
     * @param {Array} menus - 菜单列表
     * @returns {Array} 过滤后的菜单列表
     */
    filterMenusByPermission(menus) {
      return permissionManager.filterMenus(menus)
    },

    /**
     * 检查菜单是否有权限
     * @param {Object} menu - 菜单对象
     * @returns {boolean} 菜单是否有权限
     */
    menuHasPermission(menu) {
      return permissionManager.menuHasPermission(menu)
    },

    /**
     * 过滤路由列表，只保留有权限的路由
     * @param {Array} routes - 路由列表
     * @returns {Array} 过滤后的路由列表
     */
    filterRoutesByPermission(routes) {
      return permissionManager.filterRoutes(routes)
    },

    /**
     * 检查用户角色
     * @param {string} role - 角色标识
     * @returns {boolean} 是否有该角色
     */
    hasRole(role) {
      return permissionManager.hasRole(role)
    },

    /**
     * 检查是否有任意一个角色
     * @param {string[]} roles - 角色标识数组
     * @returns {boolean} 是否有任意角色
     */
    hasAnyRole(roles) {
      return permissionManager.hasAnyRole(roles)
    },

    /**
     * 检查是否有所有角色
     * @param {string[]} roles - 角色标识数组
     * @returns {boolean} 是否拥有所有角色
     */
    hasAllRoles(roles) {
      return permissionManager.hasAllRoles(roles)
    },

    /**
     * 获取当前用户的权限列表
     * @returns {Array} 权限列表
     */
    getCurrentPermissions() {
      return permissionManager.getCurrentPermissions()
    },

    /**
     * 获取当前用户的角色列表
     * @returns {Array} 角色列表
     */
    getCurrentRoles() {
      return permissionManager.getCurrentRoles()
    },

    /**
     * 检查是否是管理员
     * @returns {boolean} 是否是管理员
     */
    isAdmin() {
      return permissionManager.isAdmin()
    },

    /**
     * 检查是否已登录
     * @returns {boolean} 是否已登录
     */
    isAuthenticated() {
      return permissionManager.isAuthenticated()
    },

    /**
     * 获取权限信息（包含权限和角色）
     * @returns {Object} 权限信息对象
     */
    getPermissionInfo() {
      return permissionManager.getPermissionInfo()
    }
  }
}