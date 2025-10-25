<template>
  <div class="theme-switch-container" @click="toggleDarkMode">
    <el-icon v-if="isDark" :size="24" color="#ffffff">
      <Moon />
    </el-icon>
    <el-icon v-else :size="24" color="#606266">
      <Sunny />
    </el-icon>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Moon, Sunny } from '@element-plus/icons-vue'
import { useDark, useToggle } from '../utils/dark'

const isDark = useDark()
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

// 初始化时根据系统主题设置
onMounted(() => {
  // 如果本地存储有主题设置，则使用本地存储的设置
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme) {
    isDark.value = savedTheme === 'dark'
  }
})

// 监听主题变化，保存到本地存储
watch(isDark, (newValue) => {
  localStorage.setItem('theme', newValue ? 'dark' : 'light')
})
</script>

<style scoped>
.theme-switch-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  cursor: pointer;
  min-width: 40px;
  min-height: 40px;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.theme-switch-container:hover {
  background-color: rgba(125, 125, 125, 0.1);
}
</style>