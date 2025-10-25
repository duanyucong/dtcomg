import request from '@/utils/request';

/**
 * 获取菜单树形列表（菜单管理使用，包含所有状态）
 * @returns 
 */
export function getMenuTree() {
  return request({
    url: '/api/menus',
    method: 'get',
  });
}

/**
 * 获取导航菜单（仅正常状态，用于侧边栏导航）
 * @returns 
 */
export function getNavMenus() {
  return request({
    url: '/api/menus/nav',
    method: 'get',
  });
}

/**
 * 新增菜单
 * @param {object} data 菜单信息
 * @returns 
 */
export function addMenu(data) {
  return request({
    url: '/api/menus',
    method: 'post',
    data,
  });
}

/**
 * 修改菜单信息
 * @param {object} data 菜单信息
 * @returns 
 */
export function updateMenu(data) {
  return request({
    url: `/api/menus/${data.menu_id}`,
    method: 'put',
    data,
  });
}

/**
 * 删除菜单
 * @param {number} menu_id 菜单ID
 * @returns 
 */
export function deleteMenu(menu_id) {
  return request({
    url: `/api/menus/${menu_id}`,
    method: 'delete',
  });
}
