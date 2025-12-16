import request from '@/utils/request'

/**
 * 获取菜单列表（扁平）
 */
export function getMenuList() {
  return request({
    url: '/menus',
    method: 'get'
  })
}

/**
 * 获取菜单树
 */
export function getMenuTree() {
  return request({
    url: '/menus/tree',
    method: 'get'
  })
}

/**
 * 根据ID获取菜单
 */
export function getMenuById(id) {
  return request({
    url: `/menus/${id}`,
    method: 'get'
  })
}

/**
 * 创建菜单
 */
export function createMenu(data) {
  return request({
    url: '/menus',
    method: 'post',
    data
  })
}

/**
 * 更新菜单
 */
export function updateMenu(id, data) {
  return request({
    url: `/menus/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除菜单
 */
export function deleteMenu(id) {
  return request({
    url: `/menus/${id}`,
    method: 'delete'
  })
}
