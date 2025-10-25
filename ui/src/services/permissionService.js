
/**
 * 权限服务
 * 核心职责：存储和管理用户权限，并提供权限校验方法。
 *
 * @author 系统管理员
 * @since 2025-09-22
 */
export class PermissionService {
  constructor() {
    // 用户权限列表
    this.userPermissions = [];
    // 是否已初始化
    this.initialized = false;
  }

  /**
   * 设置用户权限
   * @param {Array<string>} permissions 权限数组，如 ['system:user:add', 'web:customer:view']
   */
  setPermissions(permissions) {
    this.userPermissions = permissions || [];
    this.initialized = true;
    // console.log('用户权限已设置:', this.userPermissions);
  }

  /**
   * 检查是否有指定权限
   * @param {string} permission 权限标识，如 'system:user:add'
   * @returns {boolean} 是否有权限
   */
  hasPermission(permission) {
    if (!permission) return false;
    // 超级管理员拥有所有权限
    if (this.userPermissions.includes('*:*')) return true;
    return this.userPermissions.includes(permission);
  }

  /**
   * 检查是否有任意一个权限
   * @param {Array<string>} permissions 权限数组
   * @returns {boolean} 是否有任意权限
   */
  hasAnyPermission(permissions) {
    if (!permissions || !Array.isArray(permissions)) return false;
    return permissions.some(permission => this.hasPermission(permission));
  }

  /**
   * 检查是否有所有权限
   * @param {Array<string>} permissions 权限数组
   * @returns {boolean} 是否有所有权限
   */
  hasAllPermissions(permissions) {
    if (!permissions || !Array.isArray(permissions)) return false;
    return permissions.every(permission => this.hasPermission(permission));
  }

  /**
   * 检查是否是超级管理员
   * @returns {boolean}
   */
  isAdmin() {
    // 检查是否有超级管理员的权限标识
    return this.hasPermission('*:*');
  }

  /**
   * 获取用户的权限标识列表
   * @returns {Array<string>} 权限列表
   */
  getPermissionList() {
    return [...this.userPermissions];
  }

  /**
   * 过滤菜单树（移除无权限的菜单和隐藏的菜单）
   * @param {Array} menuList 菜单列表
   * @returns {Array} 过滤后的菜单
   */
  filterMenus(menuList) {
    if (!menuList || !Array.isArray(menuList)) return [];
    
    // console.log('开始过滤菜单，原始菜单数量:', menuList.length);
    // console.log('当前用户权限列表:', this.userPermissions);

    const filteredMenus = menuList.filter(menu => {
      // console.log('处理菜单:', menu.menu_name, '类型:', menu.menu_type, '权限:', menu.perms, '显示状态:', menu.is_show);
      
      // 只处理目录（M）和菜单（C）
      if (menu.menu_type !== 'M' && menu.menu_type !== 'C') {
        // console.log('跳过非菜单项:', menu.menu_name);
        return false;
      }

      // 如果菜单设置为隐藏（is_show=0），则不显示在导航栏
      if (menu.is_show === '0') {
        // console.log('菜单设置为隐藏，不显示:', menu.menu_name);
        return false;
      }

      // 如果菜单不需要权限，则直接显示
      if (!menu.perms) {
        // console.log('菜单无需权限，直接显示:', menu.menu_name);
        return true;
      }

      // 检查菜单权限
      const hasPerm = this.hasPermission(menu.perms);
      // console.log('菜单权限检查:', menu.menu_name, '需要权限:', menu.perms, '是否有权限:', hasPerm);

      // 如果有子菜单，递归过滤
      if (menu.children && menu.children.length > 0) {
        const originalChildrenCount = menu.children.length;
        menu.children = this.filterMenus(menu.children);
        // console.log('子菜单过滤结果:', menu.menu_name, '原始子菜单数:', originalChildrenCount, '过滤后:', menu.children.length);
        // 如果子菜单过滤后仍有内容，或者当前菜单本身有权限，则显示该菜单
        const shouldShow = menu.children.length > 0 || hasPerm;
        // console.log('菜单最终显示决定:', menu.menu_name, '是否显示:', shouldShow);
        return shouldShow;
      }

      // console.log('菜单最终显示决定:', menu.menu_name, '是否显示:', hasPerm);
      return hasPerm;
    });
    
    // console.log('菜单过滤完成，过滤后菜单数量:', filteredMenus.length);
    return filteredMenus;
  }

  /**
   * 清空权限（用户登出时使用）
   */
  clearPermissions() {
    this.userPermissions = [];
    this.initialized = false;
    // console.log('用户权限已清空');
  }
}
