import { ref, watch } from 'vue'
import 'element-plus/theme-chalk/dark/css-vars.css'

// 创建一个响应式的暗黑模式状态
export const useDark = () => {
  const isDark = ref(false)
  
  // 初始化时检查系统偏好和本地存储
  if (typeof window !== 'undefined') {
    // 首先检查本地存储
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      isDark.value = savedTheme === 'dark'
    } else {
      // 如果没有本地存储，则检查系统偏好
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    
    // 初始应用主题
    applyTheme(isDark.value)
  }
  
  return isDark
}

// 切换暗黑模式的函数
export const useToggle = (isDark) => {
  return () => {
    isDark.value = !isDark.value
    applyTheme(isDark.value)
  }
}

// 应用主题到DOM
function applyTheme(isDark) {
  if (isDark) {
    document.documentElement.classList.add('dark')
    document.documentElement.setAttribute('data-theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    document.documentElement.setAttribute('data-theme', 'light')
  }
}

// 监听系统主题变化
if (typeof window !== 'undefined') {
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    const savedTheme = localStorage.getItem('theme')
    // 只有当用户没有手动设置主题时，才跟随系统主题
    if (!savedTheme) {
      const isDark = e.matches
      applyTheme(isDark)
    }
  })
}