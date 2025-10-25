/**
 * 权限控制指令
 * 提供元素级别的权限控制功能
 * 
 * @author 系统管理员
 * @since 2025-01-XX
 */

import { permissionManager } from '@/utils/permission'

/**
 * 权限控制指令 v-permission
 * 如果没有权限，则移除元素
 * 
 * 用法示例：
 * <el-button v-permission="'system:user:add'">新增用户</el-button>
 * <el-button v-permission="['system:user:add', 'system:user:edit']">操作</el-button>
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    let hasPermission = false

    if (typeof value === 'string') {
      // 单个权限检查
      hasPermission = permissionManager.hasPermission(value)
    } else if (Array.isArray(value)) {
      // 多个权限，满足任意一个即可
      hasPermission = permissionManager.hasAnyPermission(value)
    }

    if (!hasPermission) {
      // 没有权限，移除元素
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

/**
 * 权限控制指令 v-has-permission
 * 如果没有权限，则隐藏元素（不移除）
 * 
 * 用法示例：
 * <el-button v-has-permission="'system:user:add'">新增用户</el-button>
 * <el-button v-has-permission="['system:user:add', 'system:user:edit']">操作</el-button>
 */
export const hasPermission = {
  mounted(el, binding) {
    const { value } = binding
    let hasPermission = false

    if (typeof value === 'string') {
      hasPermission = permissionManager.hasPermission(value)
    } else if (Array.isArray(value)) {
      hasPermission = permissionManager.hasAnyPermission(value)
    }

    if (!hasPermission) {
      el.style.display = 'none'
    }
  },
  updated(el, binding) {
    const { value } = binding
    let hasPermission = false

    if (typeof value === 'string') {
      hasPermission = permissionManager.hasPermission(value)
    } else if (Array.isArray(value)) {
      hasPermission = permissionManager.hasAnyPermission(value)
    }

    el.style.display = hasPermission ? '' : 'none'
  }
}

/**
 * 权限控制指令 v-has-all-permission
 * 必须拥有所有指定权限才显示
 * 
 * 用法示例：
 * <el-button v-has-all-permission="['system:user:add', 'system:role:add']">高级操作</el-button>
 */
export const hasAllPermission = {
  mounted(el, binding) {
    const { value } = binding
    let hasPermission = false

    if (Array.isArray(value)) {
      hasPermission = permissionManager.hasAllPermissions(value)
    }

    if (!hasPermission) {
      el.style.display = 'none'
    }
  },
  updated(el, binding) {
    const { value } = binding
    let hasPermission = false

    if (Array.isArray(value)) {
      hasPermission = permissionManager.hasAllPermissions(value)
    }

    el.style.display = hasPermission ? '' : 'none'
  }
}

/**
 * 管理员权限指令 v-admin
 * 只有管理员才能看到
 * 
 * 用法示例：
 * <el-button v-admin>管理员操作</el-button>
 */
export const admin = {
  mounted(el, binding) {
    const hasPermission = permissionManager.isAdmin()
    
    if (!hasPermission) {
      el.style.display = 'none'
    }
  },
  updated(el, binding) {
    const hasPermission = permissionManager.isAdmin()
    
    el.style.display = hasPermission ? '' : 'none'
  }
}