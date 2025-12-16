import request from '@/utils/request'

/**
 * 获取权限列表
 */
export function getPermissionList() {
  return request({
    url: '/permissions',
    method: 'get'
  })
}

/**
 * 根据ID获取权限
 */
export function getPermissionById(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'get'
  })
}

/**
 * 创建权限
 */
export function createPermission(data) {
  return request({
    url: '/permissions',
    method: 'post',
    data
  })
}

/**
 * 更新权限
 */
export function updatePermission(id, data) {
  return request({
    url: `/permissions/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除权限
 */
export function deletePermission(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'delete'
  })
}
