import axios from 'axios'

const request = axios.create({
  // 在开发环境下使用相对路径，让vite代理生效
  // 在生产环境下使用配置的API地址
  baseURL: import.meta.env.PROD ? (import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:5320') : '',
  timeout: 10000
})

// 存储刷新Token的Promise，避免并发请求时重复刷新
let refreshingTokenPromise = null

// 刷新Token函数
async function refreshToken() {
  const refreshToken = localStorage.getItem('refresh_token')
  if (!refreshToken) {
    return null
  }
  
  try {
    const baseURL = import.meta.env.PROD ? (import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:5320') : ''
    const response = await axios.post(`${baseURL}/api/auth/refresh`, {
      refresh_token: refreshToken
    })
    
    const { access_token } = response.data.data
    localStorage.setItem('access_token', access_token)
    return access_token
  } catch (error) {
    // 刷新失败，清除本地存储并跳转到登录页
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    window.location.href = '/login'
    return null
  }
}

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    // 如果是blob类型的响应（如文件下载），直接返回response
    if (response.config.responseType === 'blob') {
      return response
    }
    
    const { code, message, data } = response.data
    if (code === 200) {
      return { data, message }
    } else {
      // 保留原始错误信息，创建一个包含更多上下文的错误对象
      const error = new Error(message || '请求失败')
      error.response = { data: response.data }
      return Promise.reject(error)
    }
  },
  async (error) => {
    const originalRequest = error.config
    
    // 如果是401错误且不是刷新Token的请求
    if (error.response?.status === 401 && !originalRequest._retry) {
      // 标记请求已重试
      originalRequest._retry = true
      
      // 如果正在刷新Token，等待刷新完成
      if (refreshingTokenPromise) {
        await refreshingTokenPromise
        // 使用新的Token重新发送请求
        const newToken = localStorage.getItem('access_token')
        if (newToken) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          return request(originalRequest)
        }
      } else {
        // 开始刷新Token
        refreshingTokenPromise = refreshToken()
        const newToken = await refreshingTokenPromise
        refreshingTokenPromise = null
        
        // 如果刷新成功，使用新的Token重新发送请求
        if (newToken) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          return request(originalRequest)
        }
      }
    }
    
    // 已经有response对象的错误，直接返回，保留完整的错误信息
    if (error.response?.data) {
      return Promise.reject(error)
    }
    return Promise.reject(error)
  }
)

export default request