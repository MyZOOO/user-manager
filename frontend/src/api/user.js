import request from '@/utils/request'

/**
 * 获取用户列表
 */
export function getUserList() {
  return request({
    url: '/user/list',
    method: 'get'
  })
}

/**
 * 根据ID获取用户
 */
export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

/**
 * 创建用户
 */
export function createUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUser(id, data) {
  return request({
    url: `/user/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

/**
 * 更新当前用户资料（邮箱、年龄等）
 */
export function updateUserProfile(id, data) {
  return request({
    url: `/user/${id}`,
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function changePassword(id, data) {
  return request({
    url: `/user/${id}/password`,
    method: 'post',
    data
  })
}
