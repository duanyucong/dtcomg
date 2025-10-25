<template>
  <div class="forbidden-container">
    <div class="forbidden-content">
      <div class="forbidden-icon">
        <el-icon size="120" color="#f56c6c">
          <Lock />
        </el-icon>
      </div>
      <h1 class="forbidden-title">403</h1>
      <h2 class="forbidden-subtitle">访问被拒绝</h2>
      <p class="forbidden-description">
        抱歉，您没有权限访问该页面或执行该操作。
      </p>
      <div class="forbidden-actions">
        <el-button type="primary" @click="goBack" size="large">
          <el-icon><ArrowLeft /></el-icon>
          返回上一页
        </el-button>
        <el-button @click="goHome" size="large">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button type="info" @click="goLogin" size="large" v-if="!isAuthenticated">
          <el-icon><User /></el-icon>
          重新登录
        </el-button>
      </div>
      <div class="forbidden-details" v-if="permissionInfo">
        <el-alert
          :title="permissionInfo.title"
          :description="permissionInfo.description"
          type="warning"
          :closable="false"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Lock, ArrowLeft, HomeFilled, User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 是否已认证
const isAuthenticated = computed(() => authStore.isAuthenticated)

// 权限信息
const permissionInfo = computed(() => {
  const requiredPermission = route.query.permission
  const requiredRole = route.query.role
  
  if (requiredPermission) {
    return {
      title: '缺少权限',
      description: `需要权限: ${requiredPermission}`
    }
  } else if (requiredRole) {
    return {
      title: '缺少角色',
      description: `需要角色: ${requiredRole}`
    }
  }
  
  return null
})

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    goHome()
  }
}

// 返回首页
const goHome = () => {
  router.push('/')
}

// 重新登录
const goLogin = () => {
  router.push('/login')
}

// 页面加载时记录日志
onMounted(() => {
  console.warn('用户访问了403页面:', {
    path: route.path,
    query: route.query,
    user: authStore.userInfo,
    roles: authStore.userRole,
    permissions: authStore.userPermissions
  })
})
</script>

<style scoped>
.forbidden-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Arial', sans-serif;
}

.forbidden-content {
  text-align: center;
  padding: 60px 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  max-width: 500px;
  width: 90%;
}

.forbidden-icon {
  margin-bottom: 20px;
  animation: pulse 2s infinite;
}

.forbidden-title {
  font-size: 120px;
  font-weight: bold;
  color: #f56c6c;
  margin: 0 0 10px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.forbidden-subtitle {
  font-size: 32px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 600;
}

.forbidden-description {
  font-size: 16px;
  color: #606266;
  margin-bottom: 30px;
  line-height: 1.6;
}

.forbidden-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 30px;
}

.forbidden-details {
  margin-top: 20px;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

@media (max-width: 768px) {
  .forbidden-content {
    padding: 40px 20px;
    margin: 20px;
  }
  
  .forbidden-title {
    font-size: 80px;
  }
  
  .forbidden-subtitle {
    font-size: 24px;
  }
  
  .forbidden-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>