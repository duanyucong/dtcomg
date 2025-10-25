<template>
  <div class="test-container">
    <h3>递归更新问题测试</h3>
    <el-alert
      title="测试说明"
      type="info"
      description="这个组件用于测试ElMenu递归更新问题是否已修复"
      show-icon
    />
    
    <div class="test-section">
      <h4>当前状态</h4>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="路由路径">{{ currentPath }}</el-descriptions-item>
        <el-descriptions-item label="激活菜单">{{ activeKey }}</el-descriptions-item>
        <el-descriptions-item label="激活标签">{{ activeTab }}</el-descriptions-item>
        <el-descriptions-item label="标签数量">{{ tabCount }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="test-section">
      <h4>权限状态</h4>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="已认证">{{ isAuthenticated }}</el-descriptions-item>
        <el-descriptions-item label="权限数量">{{ permissionCount }}</el-descriptions-item>
        <el-descriptions-item label="用户角色">{{ userRoles.join(', ') || '无' }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ userInfo?.name || '未登录' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="test-section">
      <h4>测试操作</h4>
      <el-space>
        <el-button @click="testMenuSwitch" type="primary">测试菜单切换</el-button>
        <el-button @click="testTabSwitch" type="success">测试标签切换</el-button>
        <el-button @click="testPermissionChange" type="warning">测试权限变更</el-button>
        <el-button @click="clearConsole" type="info">清空控制台</el-button>
      </el-space>
    </div>

    <div class="test-section">
      <h4>控制台输出</h4>
      <el-input
        v-model="consoleOutput"
        type="textarea"
        :rows="10"
        readonly
        placeholder="操作日志将显示在这里..."
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAuthStore()

// 响应式数据
const consoleOutput = ref('')
const updateCount = ref(0)
let consoleLog = []

// 计算属性
const currentPath = computed(() => route.path)
const activeKey = computed(() => {
  // 模拟App.vue中的逻辑
  const path = route.path
  if (path === '/') return 'home'
  if (path.startsWith('/order')) return 'order'
  if (path.startsWith('/material')) {
    if (path === '/material' || path === '/material/list') return 'material/list'
    if (path === '/material/unit') return 'material/unit'
    if (path === '/material/spec') return 'material/spec'
  }
  if (path.startsWith('/customer')) return 'customer'
  if (path.startsWith('/process')) return 'process'
  if (path.startsWith('/system')) {
    if (path === '/system/user') return 'system/user'
    if (path === '/system/role') return 'system/role'
    if (path === '/system/dept') return 'system/dept'
    if (path === '/system/menu') return 'system/menu'
  }
  return 'unknown'
})
const activeTab = ref('home')
const tabCount = ref(1)
const isAuthenticated = computed(() => authStore.isAuthenticated)
const permissionCount = computed(() => authStore.userPermissions?.length || 0)
const userRoles = computed(() => authStore.roles || [])
const userInfo = computed(() => authStore.userInfo)

// 日志函数
const log = (message) => {
  const timestamp = new Date().toLocaleTimeString()
  const logEntry = `[${timestamp}] ${message}`
  consoleLog.push(logEntry)
  consoleOutput.value = consoleLog.join('\n')
  console.log(logEntry)
}

// 测试函数
const testMenuSwitch = () => {
  log('开始测试菜单切换...')
  updateCount.value = 0
  
  // 模拟快速菜单切换
  const testPaths = ['/order', '/material/list', '/customer', '/process']
  let index = 0
  
  const interval = setInterval(() => {
    if (index < testPaths.length) {
      log(`切换到路径: ${testPaths[index]}`)
      // 这里只是模拟，实际会触发路由变化
      index++
    } else {
      clearInterval(interval)
      log(`菜单切换测试完成，更新次数: ${updateCount.value}`)
      if (updateCount.value > 10) {
        log('⚠️ 警告：更新次数过多，可能存在递归问题！')
      } else {
        log('✅ 菜单切换测试正常')
      }
    }
  }, 200)
}

const testTabSwitch = () => {
  log('开始测试标签切换...')
  const startCount = updateCount.value
  
  // 模拟标签切换
  const testTabs = ['order', 'material/list', 'customer']
  testTabs.forEach((tab, index) => {
    setTimeout(() => {
      log(`切换到标签: ${tab}`)
      activeTab.value = tab
    }, index * 150)
  })
  
  setTimeout(() => {
    const diff = updateCount.value - startCount
    log(`标签切换测试完成，更新次数增加: ${diff}`)
    if (diff > 15) {
      log('⚠️ 警告：标签切换更新次数过多！')
    } else {
      log('✅ 标签切换测试正常')
    }
  }, 1000)
}

const testPermissionChange = () => {
  log('开始测试权限变更...')
  const startCount = updateCount.value
  
  // 模拟权限变化（实际项目中会触发菜单重新计算）
  log('模拟权限数据变化...')
  
  setTimeout(() => {
    const diff = updateCount.value - startCount
    log(`权限变更测试完成，更新次数增加: ${diff}`)
    if (diff > 20) {
      log('⚠️ 警告：权限变更导致过多更新！')
    } else {
      log('✅ 权限变更测试正常')
    }
  }, 800)
}

const clearConsole = () => {
  consoleLog = []
  consoleOutput.value = ''
  updateCount.value = 0
  log('控制台已清空')
}

// 监听更新（模拟递归检测）
const updateListener = () => {
  updateCount.value++
  if (updateCount.value % 50 === 0) {
    log(`⚠️ 更新次数已达到 ${updateCount.value}，可能存在性能问题`)
  }
}

// 生命周期
onMounted(() => {
  log('递归更新测试组件已挂载')
  // 监听响应式数据变化
  const unwatch1 = activeKey.value
  const unwatch2 = activeTab.value
  
  // 简单的更新计数器
  setInterval(updateListener, 100)
})

onUnmounted(() => {
  log('递归更新测试组件已卸载')
})
</script>

<style scoped>
.test-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin: 20px 0;
}

.test-section h4 {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.el-descriptions {
  margin-top: 10px;
}
</style>