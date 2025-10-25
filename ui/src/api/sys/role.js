import request from '@/utils/request';

/**
 * 获取角色分页列表
 * @param {object} params 查询参数
 * @returns 
 */
export function getRoleList(params) {
  return request({
    url: '/api/roles',
    method: 'get',
    params,
  });
}

/**
 * 新增角色
 * @param {object} data 角色信息
 * @returns 
 */
export function addRole(data) {
  return request({
    url: '/api/roles',
    method: 'post',
    data,
  });
}

/**
 * 修改角色信息
 * @param {object} data 角色信息
 * @returns 
 */
export function updateRole(data) {
  return request({
    url: `/api/roles/${data.role_id}`,
    method: 'put',
    data,
  });
}

/**
 * 删除角色
 * @param {number} role_id 角色ID
 * @returns 
 */
export function deleteRole(role_id) {
  return request({
    url: `/api/roles/${role_id}`,
    method: 'delete',
  });
}

/**
 * 获取角色的菜单ID列表
 * @param {number} role_id 角色ID
 * @returns 
 */
export function getRoleMenus(role_id) {
  return request({
    url: `/api/roles/${role_id}/menus`,
    method: 'get',
  });
}

/**
 * 更新角色菜单权限
 * @param {number} role_id 角色ID
 * @param {Array<number>} menu_ids 菜单ID列表
 * @returns 
 */
export function updateRoleMenus(role_id, menu_ids) {
  return request({
    url: `/api/roles/${role_id}/menus`,
    method: 'put',
    data: menu_ids,
  });
}
