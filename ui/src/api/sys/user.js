import request from '@/utils/request';

/**
 * 获取用户分页列表
 * @param {object} params 查询参数
 * @returns 
 */
export function getUserList(params) {
  return request({
    url: '/api/users',
    method: 'get',
    params,
  });
}

/**
 * 新增用户
 * @param {object} data 用户信息
 * @returns 
 */
export function addUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data,
  });
}

/**
 * 修改用户信息
 * @param {object} data 用户信息
 * @returns 
 */
export function updateUser(data) {
  return request({
    url: `/api/users/${data.user_id}`,
    method: 'put',
    data,
  });
}

/**
 * 删除用户
 * @param {number} user_id 用户ID
 * @returns 
 */
export function deleteUser(user_id) {
  return request({
    url: `/api/users/${user_id}`,
    method: 'delete',
  });
}

/**
 * 更新用户角色
 * @param {number} user_id 用户ID
 * @param {Array<number>} role_ids 角色ID列表
 * @returns 
 */
export function updateUserRoles(user_id, role_ids) {
  return request({
    url: `/api/users/${user_id}/roles`,
    method: 'put',
    data: role_ids,
  });
}

/**
 * 重置用户密码
 * @param {number} user_id 用户ID
 * @param {string} new_password 新密码
 * @returns 
 */
export function resetUserPassword(user_id, new_password) {
  return request({
    url: `/api/users/${user_id}/password`,
    method: 'put',
    data: { new_password },
  });
}
