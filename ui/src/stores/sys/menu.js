import { defineStore } from 'pinia';
import { getMenuTree, getNavMenus, addMenu, updateMenu, deleteMenu } from '@/api/sys/menu';
import { permissionManager } from '@/utils/permission';
import { ElMessage } from 'element-plus';
import router from '@/router';

// 使用 import.meta.glob 动态导入所有视图组件
const viewModules = import.meta.glob('/src/views/**/*.vue');
// console.log('Available view modules:', Object.keys(viewModules));

/**
 * 递归地将后端菜单数据转换为前端路由记录
 * @param {Array} menus - 后端返回的菜单数组
 * @param {string} parentPath - 父级路径
 * @returns {Array} - Vue Router 路由记录数组
 */
function transformMenusToRoutes(menus, parentPath = '') {
  // console.log('转换菜单为路由，输入菜单数:', menus.length, '父路径:', parentPath);
  const routes = [];
  for (const menu of menus) {
    // 如果菜单状态不是 '0' (正常), 则跳过此菜单及其所有子菜单
    if (menu.status !== '0') {
      // console.log('跳过非正常状态的菜单:', menu.menu_name, '状态:', menu.status);
      continue;
    }
    const fullPath = parentPath && menu.path ? `${parentPath}/${menu.path}` : menu.path;
    // console.log('处理菜单:', menu.menu_name, '路径:', fullPath, '组件:', menu.component, '类型:', menu.menu_type);

    if (menu.component && menu.menu_type === 'C') {
      const componentPath = `/src/views/${menu.component}.vue`;
      // console.log('检查组件路径:', componentPath);
      const componentLoader = viewModules[componentPath];

      // 安全检查：确保组件存在
      if (!componentLoader) {
        // console.error(`[动态路由]：找不到组件 -> ${componentPath}，请检查后端返回的 component 字符串是否正确。`);
        // console.log('可用组件列表:', Object.keys(viewModules));
        continue; // 跳过此路由
      }

      const route = {
        path: `/${fullPath}`,
        name: fullPath.replace(/:/g, '').replace(/\//g, '-'),
        component: componentLoader,
        meta: {
          title: menu.menu_name,
          icon: menu.icon,
          permission: menu.perms
        }
      };
      // console.log('生成路由:', route);
      routes.push(route);
    } else {
      // console.log('跳过菜单，原因: 无组件或类型不是C', '组件:', menu.component, '类型:', menu.menu_type);
    }

    if (menu.children && menu.children.length > 0) {
      // console.log('处理子菜单，数量:', menu.children.length);
      routes.push(...transformMenusToRoutes(menu.children, fullPath));
    }
  }
  // console.log('路由转换完成，生成路由数:', routes.length);
  return routes;
}


export const useMenuStore = defineStore('menu', {
  state: () => ({
    rawMenus: [], // For menu management page
    sidebarMenus: [], // For visible sidebar UI
    routableMenus: [], // For dynamic route generation
    isDynamicRouteGenerated: false,
    loading: false,
  }),

  actions: {
    async fetchAndGenerateMenus() {
      this.loading = true;
      try {
        // 1. 获取当前用户有权限访问的所有菜单（包括隐藏的）
        const navRes = await getNavMenus();
        const allPermittedMenus = navRes.data || [];

        // 2. 将这份完整的菜单列表存起来，用于生成路由
        this.routableMenus = allPermittedMenus;

        // 3. 过滤出需要在侧边栏显示的菜单
        this.sidebarMenus = permissionManager.filterMenus(allPermittedMenus);

      } catch (error) {
        // console.error('获取导航菜单失败:', error);
        ElMessage.error(error.response?.data?.message || error.message || '获取导航菜单失败');
        this.sidebarMenus = [];
        this.routableMenus = [];
      } finally {
        this.loading = false;
      }
    },

    generateDynamicRoutes() {
      if (this.isDynamicRouteGenerated) {
        // console.log('动态路由已经生成，跳过生成过程');
        return;
      }
      
      // console.log('开始生成动态路由，路由菜单数据:', this.routableMenus);
      
      // 使用完整的、有权限的菜单数据生成动态路由
      const dynamicRoutes = transformMenusToRoutes(this.routableMenus);
      
      // console.log('生成的动态路由:', dynamicRoutes);

      dynamicRoutes.forEach(route => {
        router.addRoute(route);
      });

      this.isDynamicRouteGenerated = true;
      // console.log('动态路由生成完成，当前路由总数:', router.getRoutes().length);
      // console.log('所有路由:', router.getRoutes());
    },

    clearMenus() {
      this.rawMenus = [];
      this.sidebarMenus = [];
      this.routableMenus = [];
      this.isDynamicRouteGenerated = false;
    },

    async fetchRawMenus() {
      try {
        const treeRes = await getMenuTree();
        this.rawMenus = treeRes.data || [];
      } catch (error) {
        this.rawMenus = [];
        ElMessage.error(error.response?.data?.message || error.message || '获取菜单管理数据失败，可能缺少权限。');
      }
    },

    // --- 以下是菜单管理的CRUD操作 ---
    async createMenu(menuData) {
      try {
        await addMenu(menuData);
        ElMessage.success('新增成功');
      } catch (error) {
        ElMessage.error(error.response?.data?.message || error.message || '新增菜单失败');
        throw error;
      }
    },

    async editMenu(menuData) {
      try {
        await updateMenu(menuData);
        ElMessage.success('修改成功');
      } catch (error) {
        ElMessage.error(error.response?.data?.message || error.message || '修改菜单失败');
        throw error;
      }
    },

    async removeMenu(menu_id) {
      try {
        await deleteMenu(menu_id);
        ElMessage.success('删除成功');
      } catch (error) {
        // 优先显示后端返回的具体错误信息
        const errorMessage = error.response?.data?.message || error.message || '删除菜单失败';
        ElMessage.error(errorMessage);
        throw error;
      }
    },
  },

  getters: {
    getSidebarMenus: (state) => state.sidebarMenus,
    menuList: (state) => state.rawMenus,
    menuOptions: (state) => [{
      menu_id: 0,
      menu_name: '主类目',
      children: state.rawMenus
    }],
    areRoutesGenerated: (state) => state.isDynamicRouteGenerated,
  },
});