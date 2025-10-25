import router from './index';
import { useAuthStore } from '@/stores/auth';
import { useMenuStore } from '@/stores/sys/menu';
import { ElMessage } from 'element-plus';

const whiteList = ['/login', '/403', '/404']; // 路由白名单

router.beforeEach(async (to, from, next) => {
  // console.log('路由守卫 - 访问路径:', to.path, '来源路径:', from.path);
  const authStore = useAuthStore();
  const menuStore = useMenuStore();

  const hasToken = !!authStore.token;
  // console.log('路由守卫 - Token存在:', hasToken);

  if (hasToken) {
    if (to.path === '/login') {
      // console.log('路由守卫 - 已登录访问登录页，重定向到首页');
      // 如果已登录，访问登录页则重定向到首页
      next({ path: '/' });
    } else {
      // 检查动态路由是否已添加
      // console.log('路由守卫 - 动态路由状态:', menuStore.areRoutesGenerated);
      if (menuStore.areRoutesGenerated) {
        // console.log('路由守卫 - 动态路由已存在，直接放行');
        next(); // 已添加，直接放行
      } else {
        // console.log('路由守卫 - 开始加载动态路由');
        try {
          // 1. 获取用户信息 (包含权限)
          // console.log('路由守卫 - 步骤1: 获取用户信息');
          await authStore.getCurrentUser();
          // console.log('路由守卫 - 用户信息获取完成');
          // 2. 获取菜单数据
          // console.log('路由守卫 - 步骤2: 获取菜单数据');
          await menuStore.fetchAndGenerateMenus();
          // console.log('路由守卫 - 菜单数据获取完成');
          // 3. 根据菜单数据生成动态路由
          // console.log('路由守卫 - 步骤3: 生成动态路由');
          menuStore.generateDynamicRoutes();
          // console.log('路由守卫 - 动态路由生成完成');

          // 使用 replace: true, 确保导航不会留下历史记录
          // 并且因为是动态添加路由，需要重新进入导航流程
          // console.log('路由守卫 - 重新导航到目标路径:', to.path);
          next({ ...to, replace: true });
        } catch (error) {
          // console.error('路由守卫 - 加载动态路由失败:', error);
          // 如果在获取信息或生成路由时出错（例如token失效）
          // 清除所有状态并重定向到登录页
          authStore.clearAuthState();
          menuStore.clearMenus();
          ElMessage.error(error.message || '登录状态已过期，请重新登录');
          next('/login');
        }
      }
    }
  } else {
    // 未登录
    // console.log('路由守卫 - 未登录，检查白名单');
    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，直接放行
      // console.log('路由守卫 - 路径在白名单中，放行');
      next();
    } else {
      // 不在白名单中，重定向到登录页
      // console.log('路由守卫 - 路径不在白名单中，重定向到登录页');
      next(`/login?redirect=${to.path}`);
    }
  }
});