<template>
  <div class="login-container" :class="{ dark: isDark }">
    <div class="theme-toggle">
      <el-button 
        circle 
        class="theme-toggle-button" 
        @click="toggleDarkMode"
        :title="isDark ? '切换到浅色模式' : '切换到深色模式'"
      >
        <el-icon size="18">
          <Moon v-if="!isDark" />
          <Sunny v-else />
        </el-icon>
      </el-button>
    </div>
    
    <div class="login-card">
      <div class="login-header">
        <h1 class="title">欢迎回来</h1>
        <p class="subtitle">锻铁锤的管理系统</p>
      </div>
      
      <el-form 
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            native-type="submit"
          >
            <template #icon>
              <el-icon><Unlock /></el-icon>
            </template>
            立即登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useDark } from '@/utils/dark'
import { Moon, Sunny } from '@element-plus/icons-vue'

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 路由
const router = useRouter()

// 认证store
const authStore = useAuthStore()

// 深色模式
const isDark = useDark()

// 切换深色模式
const toggleDarkMode = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    document.documentElement.setAttribute('data-theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    document.documentElement.setAttribute('data-theme', 'light')
  }
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        // 使用store处理登录
        await authStore.login(loginForm)
      } catch (error) {
        // 优先使用后端返回的具体错误信息
        const errorMessage = error.response?.data?.message || error.message || '登录失败'
        ElMessage.error(errorMessage)
      } finally {
        loading.value = false
      }
    }
  })
}

// 初始化主题
onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme) {
    isDark.value = savedTheme === 'dark'
  } else {
    isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
  }
  
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    document.documentElement.setAttribute('data-theme', 'dark')
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-primary) 100%);
  transition: background 0.3s ease;
  margin: 0;
  padding: 0;
  position: fixed;
  top: 0;
  left: 0;
}

/* 深色模式样式 */
.login-container.dark {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.theme-toggle {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;
}

.theme-toggle-button {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  color: var(--el-text-color-primary);
  transition: all 0.3s ease;
}

.theme-toggle-button:hover {
  background-color: var(--el-fill-color-light);
  transform: scale(1.1);
}

.login-card {
  width: 420px;
  padding: 40px 30px;
  background-color: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color-lighter);
}

.login-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 28px;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
  font-weight: 600;
}

.subtitle {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 0;
}

.login-button {
  width: 100%;
  border-radius: 8px;
  letter-spacing: 2px;
  font-weight: 500;
}

/* 深色模式下的特殊样式 */
:deep(.el-input__wrapper) {
  background-color: var(--el-bg-color);
  border-color: var(--el-border-color);
}

:deep(.el-input__inner) {
  color: var(--el-text-color-primary);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    max-width: 420px;
    padding: 30px 20px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .theme-toggle {
    top: 10px;
    right: 10px;
  }
}
</style>