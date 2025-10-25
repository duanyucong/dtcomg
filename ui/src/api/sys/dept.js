import request from '@/utils/request';

/**
 * 获取部门树形列表
 * @returns 
 */
export function getDeptTree() {
  return request({
    url: '/api/depts',
    method: 'get',
  });
}

/**
 * 新增部门
 * @param {object} data 部门信息
 * @returns 
 */
export function addDept(data) {
  return request({
    url: '/api/depts',
    method: 'post',
    data,
  });
}

/**
 * 修改部门信息
 * @param {object} data 部门信息
 * @returns 
 */
export function updateDept(data) {
  return request({
    url: `/api/depts/${data.dept_id}`,
    method: 'put',
    data,
  });
}

/**
 * 删除部门
 * @param {number} dept_id 部门ID
 * @returns 
 */
export function deleteDept(dept_id) {
  return request({
    url: `/api/depts/${dept_id}`,
    method: 'delete',
  });
}
