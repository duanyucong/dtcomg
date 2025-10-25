<template>
  <el-button 
    type="danger" 
    plain
    @click="handleLogout"
  >
    退出登录
  </el-button>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'

// 认证store
const authStore = useAuthStore()

// 登出处理
const handleLogout = async () => {
  await authStore.logout()
}
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>