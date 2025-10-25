import { defineStore } from 'pinia'
import { loginApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { permissionManager } from '@/utils/permission'
import { useMenuStore } from '@/stores/sys/menu';

/**
 * 用户认证状态管理
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    // 用户信息
    user: null,
    // 访问令牌
    token: localStorage.getItem('access_token') || '',
    // 是否已登录
    isAuthenticated: !!localStorage.getItem('access_token') && localStorage.getItem('access_token') !== 'undefined' && localStorage.getItem('access_token') !== 'null',
    // 用户角色列表
    roles: [],
    // 权限列表
    permissions: []
  }),
  
  getters: {
    // 获取用户信息
    userInfo: (state) => state.user,
    // 获取用户角色
    userRole: (state) => state.roles,
    // 获取用户权限
    userPermissions: (state) => state.permissions,


  },
  
  actions: {
    /**
     * 用户登录
     * @param {Object} loginData - 登录信息
     * @param {string} loginData.username - 用户名
     * @param {string} loginData.password - 密码
     */
    async login(loginData) {
      console.log('开始登录流程，用户名:', loginData.username);
      try {
        console.log('步骤1: 调用登录API');
        const response = await loginApi.login(loginData)
        console.log('登录API返回:', response);
        
        // 保存token - 适配新的响应格式，直接获取response.data中的token
        this.token = response.data.token
        localStorage.setItem('access_token', this.token)
        console.log('Token已保存:', this.token);
        
        // 获取用户信息
        console.log('步骤2: 获取用户信息');
        await this.getCurrentUser()
        console.log('用户信息获取完成');
        
        // 设置登录状态
        this.isAuthenticated = true
        console.log('登录状态已设置');
        
        ElMessage.success('登录成功')
        
        // 跳转到首页
        console.log('步骤3: 跳转到首页');
        router.push('/')
        console.log('跳转完成');
        
        return response
      } catch (error) {
        console.error('登录失败:', error);
        // 优先使用后端返回的具体错误信息
        const errorMessage = error.response?.data?.message || error.message || '登录失败'
        ElMessage.error(errorMessage)
        throw error
      }
    },
    
    /**
     * 获取当前用户信息
     * 获取用户详细信息，包括角色和权限
     */
    async getCurrentUser() {
      try {
        const response = await loginApi.getCurrentUser()
        
        // 保存用户信息 - 适配新的响应格式，从response.data.user获取用户信息
        this.user = response.data.user
        
        // 保存角色信息 - 从response.data.roles获取所有角色
        if (response.data.roles) {
          this.roles = response.data.roles
        }
        
        // 保存权限信息 - 从response.data.permissions获取权限列表
        if (response.data.permissions) {
          this.permissions = Array.from(response.data.permissions)
          // 更新权限管理器中的权限数据
          permissionManager.setPermissions(this.permissions)
        }
        
        // console.log('用户信息获取成功:', {
        //   user: this.user,
        //   roles: this.roles,
        //   permissions: this.permissions
        // })
        
        return response
      } catch (error) {
        // 如果是401错误，表示需要重新登录
        if (error.response?.status === 401) {
          console.warn('登录已过期，需要重新登录')
          // 清除本地状态但不自动跳转，让调用方决定如何处理
          this.clearAuthState()
        } else {
          console.error('获取用户信息失败:', error)
          // 优先使用后端返回的具体错误信息
          const errorMessage = error.response?.data?.message || error.message || '获取用户信息失败'
          ElMessage.error(errorMessage)
        }
        throw error
      }
    },
    
    /**
     * 清除认证状态
     */
    clearAuthState() {
      this.user = null
      this.token = ''
      this.roles = []
      this.permissions = []
      this.isAuthenticated = false
      permissionManager.clearPermissions()
      useMenuStore().clearMenus()
      localStorage.removeItem('access_token')
    },
    
    /**
     * 用户登出
     * 清除所有用户相关的状态和权限数据
     */
    async logout() {
      try {
        // 调用登出API
        await loginApi.logout()
      } catch (error) {
        // 忽略登出API的错误，因为可能是因为token过期导致的
        console.warn('登出API调用失败:', error)
      } finally {
        // 清除本地状态
        this.user = null
        this.token = ''
        this.roles = []
        this.permissions = []
        this.isAuthenticated = false
        
        // 清除权限管理器中的权限数据
        permissionManager.clearPermissions()
        // 清除菜单状态
        useMenuStore().clearMenus()
        
        // 清除localStorage中的token
        localStorage.removeItem('access_token')
        
        ElMessage.success('已退出登录')
        
        // 跳转到登录页
        router.push('/login')
      }
    },
    
  }
})

export default useAuthStore
