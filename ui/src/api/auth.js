import request from '@/utils/request'

/**
 * 认证相关API
 */

// 登录API
export const loginApi = {
  /**
   * 用户登录
   * @param {Object} data - 登录信息
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise} 登录结果
   */
  login(data) {
    return request({
      url: '/api/auth/login',
      method: 'post',
      data
    })
  },
  
  /**
   * 获取当前用户信息
   * @returns {Promise} 用户信息
   */
  getCurrentUser() {
    return request({
      url: '/api/auth/getInfo',
      method: 'get'
    })
  },
  
  /**
   * 用户登出
   * @returns {Promise} 登出结果
   */
  logout() {
    return request({
      url: '/api/auth/logout',
      method: 'post'
    })
  }
}

export default loginApi