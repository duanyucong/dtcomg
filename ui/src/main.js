import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 导入Element Plus暗黑模式样式
import 'element-plus/theme-chalk/dark/css-vars.css'
import './assets/styles/element-variables.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import router from './router'
import App from './App.vue'
import './router/guard'
// 导入权限指令
import permission, { hasPermission, hasAllPermission, admin } from './directives/permission'
// 导入暗黑模式工具函数
import { useDark } from './utils/dark'

// 初始化暗黑模式
const isDark = useDark()

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

// 将暗黑模式状态添加到全局属性中，方便在组件中访问
app.config.globalProperties.$isDark = isDark

// 注册所有Element Plus图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册权限指令
app.directive('permission', permission)
app.directive('has-permission', hasPermission)
app.directive('has-all-permission', hasAllPermission)
app.directive('admin', admin)

// 应用启动时初始化认证状态
const initializeAuth = () => {
  const token = localStorage.getItem('access_token')
  if (token && token !== 'undefined' && token !== 'null' && token.trim() !== '') {
    // 如果有有效token，尝试获取用户信息
    import('@/stores/auth').then(({ useAuthStore }) => {
      const authStore = useAuthStore()
      authStore.getCurrentUser().catch(() => {
        // 静默处理用户信息获取失败，让路由守卫处理重定向
        console.warn('用户信息获取失败，可能需要重新登录')
      })
    })
  }
}

// 初始化认证状态
initializeAuth()

app.mount('#app')
