<template>
  <div v-if="!isAuthenticated" class="login-container">
    <router-view />
  </div>
  <el-container v-else style="height: 100vh;">
    <el-aside width="240px" style="background-color: var(--el-menu-bg-color);">
      <div class="sidebar-header">
        <h2>锻铁锤的管理系统</h2>
      </div>
      <el-menu
        :default-active="activeKey"
        class="el-menu-vertical"
      >
        <el-menu-item index="home" @click="handleMenuSelect('home')">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 动态菜单项 -->
        <template v-for="menuItem in sidebarMenus" :key="menuItem.fullPath">
          <!-- 单层级菜单 -->
          <el-menu-item
            v-if="!menuItem.children || menuItem.children.length === 0"
            :index="menuItem.fullPath"
            @click="handleMenuSelect(menuItem.fullPath)"
          >
            <el-icon v-if="menuItem.icon && menuItem.icon !== '#'"><component :is="menuItem.icon" /></el-icon>
            <span>{{ menuItem.menu_name }}</span>
          </el-menu-item>

          <!-- 多层级菜单 -->
          <el-sub-menu
            v-else
            :index="menuItem.fullPath"
          >
            <template #title>
              <el-icon v-if="menuItem.icon && menuItem.icon !== '#'"><component :is="menuItem.icon" /></el-icon>
              <span>{{ menuItem.menu_name }}</span>
            </template>
            <el-menu-item
              v-for="child in menuItem.children"
              :key="child.fullPath"
              :index="child.fullPath"
              @click="handleMenuSelect(child.fullPath)"
            >
              {{ child.menu_name }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <!-- 顶部导航标签 -->
      <el-header height="40px" style="background-color: var(--el-bg-color); border-bottom: 1px solid var(--el-border-color-light); padding: 0 16px; display: flex; align-items: center;">
        <div class="tab-navigation" style="flex: 1; overflow: hidden;">
          <el-tabs
            v-model="activeTab"
            type="card"
            closable
            @tab-click="handleTabClick"
            @tab-remove="handleTabRemove"
            class="navigation-tabs"
          >
            <el-tab-pane
              v-for="tab in openTabs"
              :key="tab.name"
              :label="tab.title"
              :name="tab.name"
              :closable="tab.closable"
            />
          </el-tabs>
        </div>
        <div style="min-width: 70px; display: flex; justify-content: center; align-items: center;">
          <ThemeSwitch />
        </div>
        <div style="margin-left: 16px; display: flex; align-items: center; gap: 12px;">
          <el-dropdown trigger="click" v-if="authStore.userInfo">
            <span class="user-info" style="cursor: pointer; color: var(--el-text-color-primary); font-size: 14px;">
              <el-icon><User /></el-icon>
              {{ authStore.userInfo?.name || authStore.userInfo?.username }}
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showUserInfoDialog = true">
                  <el-icon><User /></el-icon>
                  用户信息
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button v-else type="danger" size="small" @click="handleLogout">
            退出登录
          </el-button>
        </div>
      </el-header>
      <el-main style="padding: 24px; background-color: var(--el-bg-color-page, #f5f5f5);">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
    <!-- 用户信息弹窗 -->
    <el-dialog title="用户信息" v-model="showUserInfoDialog" width="400px">
      <div class="user-info-content">
        <!-- 显示用户昵称 -->
        <div class="user-nickname">{{ authStore.userInfo?.nick_name || '未知用户' }}</div>
        
        <!-- 用户详细信息 -->
        <div class="user-details">
          <div class="info-item">
            <span class="info-label">用户名：</span>
            <span class="info-value">{{ authStore.userInfo?.user_name || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">昵称：</span>
            <span class="info-value">{{ authStore.userInfo?.nick_name || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号：</span>
            <span class="info-value">{{ authStore.userInfo?.phone_number || '-' }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showUserInfoDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </template>

<script setup>
import { ref, onMounted, watch, computed, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { debounce } from 'lodash-es';
import { HomeFilled, Document, Box, Tools, User, Avatar, ArrowDown, SwitchButton, Setting } from '@element-plus/icons-vue';
import ThemeSwitch from './components/ThemeSwitch.vue';
import { useAuthStore } from '@/stores/auth';
import { useMenuStore } from '@/stores/sys/menu';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const menuStore = useMenuStore();
const activeKey = ref('home');
const activeTab = ref('home');
const showUserInfoDialog = ref(false);

// Helper to build full paths
const buildFullPaths = (menus, parentPath = '') => {
  // console.log('buildFullPaths - 输入菜单数:', menus.length, '父路径:', parentPath);
  const result = menus.map(menu => {
    const currentPath = menu.path || '';
    // Avoid creating paths like '/system' for top-level items, let router handle the leading slash
    const fullPath = parentPath ? `${parentPath}/${currentPath}` : currentPath;

    const newMenu = { ...menu, fullPath };

    if (menu.children && menu.children.length > 0) {
      newMenu.children = buildFullPaths(menu.children, fullPath);
    }
    return newMenu;
  });
  // console.log('buildFullPaths - 输出菜单数:', result.length);
  return result;
};

// 从 store 获取动态菜单并构建完整路径
const sidebarMenus = computed(() => {
  const menus = buildFullPaths(menuStore.sidebarMenus);
  // console.log('App.vue - 侧边栏菜单数据:', menus);
  return menus;
});

// 计算属性：是否已认证
const isAuthenticated = computed(() => {
  return authStore.isAuthenticated && !!authStore.token;
});

// 组件挂载时初始化认证状态
onMounted(() => {
  // 路由守卫已经处理了认证和菜单加载，这里可以简化或移除
});

// 监听路由变化，确保正确显示导航
watch(() => route.path, (newPath) => {
  if (isAuthenticated.value) {
    updateActiveMenu();
  }
});

// 已打开的标签页
const openTabs = ref([
  { name: 'home', title: '首页', closable: false }
]);

// 页面标题映射 (可以考虑也从菜单数据动态生成)
const pageTitleMap = ref({});
watch(sidebarMenus, (menus) => {
  const map = {};
  const processMenus = (menuItems) => {
    for (const menu of menuItems) {
      if (menu.fullPath) {
        map[menu.fullPath] = menu.menu_name;
      }
      if (menu.children && menu.children.length > 0) {
        processMenus(menu.children);
      }
    }
  };
  processMenus(menus);
  pageTitleMap.value = map;
  pageTitleMap.value['home'] = '首页';
}, { immediate: true, deep: true });


// 添加标签页
const addTab = (key, title) => {
  // 验证key和title的有效性，避免创建空白标签
  if (!key || key.trim() === '') {
    return;
  }
  
  const existingTab = openTabs.value.find(tab => tab.name === key);
  const tabTitle = title || pageTitleMap.value[key] || key;
  
  if (!existingTab) {
    openTabs.value.push({
      name: key,
      title: tabTitle,
      closable: key !== 'home' // 首页不可关闭
    });
  }
  if (activeTab.value !== key) {
    activeTab.value = key;
  }
};

// 移除标签页
const handleTabRemove = (targetName) => {
  const tabs = openTabs.value;
  let activeName = activeTab.value;

  if (activeName === targetName) {
    tabs.forEach((tab, index) => {
      if (tab.name === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1];
        if (nextTab) {
          activeName = nextTab.name;
        }
      }
    });
  }

  openTabs.value = tabs.filter(tab => tab.name !== targetName);

  if (activeTab.value !== activeName) {
    activeTab.value = activeName;
    activeKey.value = activeName;
    // 确保路由跳转时不会创建空白标签
    const targetPath = activeName === 'home' ? '/' : `/${activeName}`;
    router.push(targetPath);
  }
};

// 点击标签页
const handleTabClick = (tab) => {
  const tabName = tab.props.name;
  if (activeTab.value !== tabName) {
    activeTab.value = tabName;
    activeKey.value = tabName;
    router.push(`/${tabName === 'home' ? '' : tabName}`);
  }
};

// 退出登录
const handleLogout = () => {
  authStore.logout();
};

// 根据路由路径设置激活的菜单项（防抖处理）
const updateActiveMenu = debounce(() => {
  const path = route.path.startsWith('/') ? route.path.substring(1) : route.path;
  let menuKey = 'home';

  if (path === '') {
      menuKey = 'home';
  } else {
      // 尝试直接匹配路径 - 使用fullPath进行匹配
      const findMenu = (menus) => {
          for(const menu of menus) {
              if(menu.fullPath === path) return menu;
              if(menu.children) {
                  const found = findMenu(menu.children);
                  if(found) return found;
              }
          }
          return null;
      }
      const matchedMenu = findMenu(sidebarMenus.value);
      if(matchedMenu) {
          menuKey = matchedMenu.fullPath; // 使用fullPath作为菜单key
      } else {
          // 如果直接匹配不到，使用旧的 startsWith 逻辑作为回退
          const segments = path.split('/');
          if(segments.length > 1) {
              menuKey = segments[0];
          }
      }
  }

  // 立即更新菜单激活状态，确保焦点立即显示
  if (activeKey.value !== menuKey) {
    activeKey.value = menuKey;
  }
  if (activeTab.value !== path) {
      activeTab.value = path;
  }

  // 只在路径有效且不是空白路径时才添加标签
  if (path && pageTitleMap.value[path]) {
    addTab(path, pageTitleMap.value[path]);
  }

}, 50); // 减少防抖延迟到50ms，提高响应速度

// 组件挂载时设置激活状态
onMounted(() => {
  // 确保在认证状态下才更新菜单
  if (isAuthenticated.value) {
    updateActiveMenu();
  }
});

// 监听路由变化，更新激活状态
watch(() => route.path, (newPath, oldPath) => {
  if (newPath !== oldPath && isAuthenticated.value) {
    // 立即更新菜单激活状态，避免防抖延迟
    const path = newPath.startsWith('/') ? newPath.substring(1) : newPath;
    let menuKey = 'home';
    
    if (path !== '') {
      // 尝试直接匹配路径 - 使用fullPath进行匹配
      const findMenu = (menus) => {
        for(const menu of menus) {
          if(menu.fullPath === path) return menu;
          if(menu.children) {
            const found = findMenu(menu.children);
            if(found) return found;
          }
        }
        return null;
      }
      const matchedMenu = findMenu(sidebarMenus.value);
      if(matchedMenu) {
        menuKey = matchedMenu.fullPath; // 使用fullPath作为菜单key
      } else {
        // 如果直接匹配不到，使用旧的 startsWith 逻辑作为回退
        const segments = path.split('/');
        if(segments.length > 1) {
          menuKey = segments[0];
        }
      }
    }
    
    // 立即更新activeKey，确保菜单焦点立即显示
    if (activeKey.value !== menuKey) {
      activeKey.value = menuKey;
    }
    
    // 同时调用防抖函数处理其他逻辑
    updateActiveMenu();
  }
});

const handleMenuSelect = (key) => {
  // console.log('处理菜单选择，key:', key);
  // 立即更新activeKey，确保菜单焦点立即显示
  activeKey.value = key;
  const title = pageTitleMap.value[key];
  // console.log('菜单标题:', title);
  addTab(key, title);
  
  // 立即进行路由跳转，避免延迟
  if (key === 'home') {
    // console.log('跳转到首页');
    router.push('/');
  } else {
    // console.log('跳转到路径:', `/${key}`);
    router.push(`/${key}`);
  }
};

</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}
</style>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

#app {
  height: 100vh;
  width: 100vw;
}

.sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-light);
  background-color: var(--el-menu-bg-color);
}

.sidebar-header h2 {
  margin: 0;
  color: var(--el-menu-text-color);
  font-size: 16px;
  font-weight: 600;
  text-align: center;
}

.tab-navigation {
  height: 100%;
  display: flex;
  align-items: center;
  position: relative;
  padding-right: 60px; /* 为右侧主题切换按钮留出空间 */
}

.header-right {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 100; /* 提高z-index确保按钮在最上层 */
  display: flex;
  align-items: center;
  pointer-events: auto; /* 确保可以接收点击事件 */
}

.navigation-tabs {
  width: 100%;
}

.navigation-tabs .el-tabs__header {
  margin: 0;
  border-bottom: none;
}

.navigation-tabs .el-tabs__nav-wrap {
  padding: 0 16px;
}

.navigation-tabs .el-tabs__item {
  height: 32px;
  line-height: 32px;
  padding: 0 16px;
  border: 1px solid var(--el-border-color);
  border-radius: 4px 4px 0 0;
  margin-right: 4px;
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-regular);
  font-size: 12px;
}

.navigation-tabs .el-tabs__item.is-active {
  background-color: var(--el-bg-color);
  color: var(--el-color-primary);
  border-bottom-color: var(--el-bg-color);
}

.navigation-tabs .el-tabs__item:hover {
  color: var(--el-color-primary);
}

.el-menu-vertical {
  height: calc(100% - 64px);
  border-right: none;
}

.el-menu-vertical {
  height: calc(100% - 64px);
  border-right: none;
}

/* 菜单项基础样式 */
.el-menu-vertical .el-menu-item {
  transition: background-color 0.3s, color 0.3s;
}

/* 菜单项悬浮样式 */
.el-menu-vertical .el-menu-item:hover {
  background-color: var(--el-menu-hover-bg-color) !important;
}

/* 菜单项激活样式 */
.el-menu-vertical .el-menu-item.is-active {
  background-color: var(--el-menu-hover-bg-color) !important;
  color: var(--el-menu-active-color) !important;
}

/* 子菜单激活样式 */
.el-menu-vertical .el-sub-menu__title:hover {
  background-color: var(--el-menu-hover-bg-color) !important;
}

.el-menu-vertical .el-sub-menu.is-active > .el-sub-menu__title {
  background-color: var(--el-menu-hover-bg-color) !important;
  color: var(--el-menu-active-color) !important;
}

/* 暗黑模式下的侧边栏样式 */
html.dark .el-aside {
  background-color: var(--el-menu-bg-color) !important;
}

html.dark .sidebar-header {
  background-color: var(--el-menu-bg-color) !important;
  border-bottom: 1px solid var(--el-border-color-dark) !important;
}

html.dark .el-menu {
  background-color: var(--el-menu-bg-color) !important;
}

/* 用户信息弹窗样式 */
.user-info-content {
  padding: 10px 0;
}

.user-nickname {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  color: var(--el-color-primary);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-label {
  width: 80px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
}

.info-value {
  flex: 1;
  color: var(--el-text-color-primary);
}
</style>