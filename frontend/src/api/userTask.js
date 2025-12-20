import request from '../utils/request'

// 用户任务相关API

/**
 * 获取当前用户的任务列表
 */
export const getUserTasks = () => {
  return request({
    url: '/user-task/my-tasks',
    method: 'get'
  })
}

/**
 * 接受任务
 */
export const acceptTask = (userTaskId) => {
  return request({
    url: `/user-task/${userTaskId}/accept`,
    method: 'put'
  })
}

/**
 * 拒绝任务
 */
export const rejectTask = (userTaskId, reason) => {
  return request({
    url: `/user-task/${userTaskId}/reject`,
    method: 'put',
    params: { reason }
  })
}

/**
 * 完成任务
 */
export const completeTask = (userTaskId, result) => {
  return request({
    url: `/user-task/${userTaskId}/complete`,
    method: 'put',
    params: { result }
  })
}

/**
 * 获取用户任务详情
 */
export const getUserTaskDetail = (userTaskId) => {
  return request({
    url: `/user-task/${userTaskId}`,
    method: 'get'
  })
}

/**
 * 管理员分配任务给用户
 */
export const assignTask = (taskId, userId) => {
  return request({
    url: '/user-task/assign',
    method: 'post',
    params: { taskId, userId }
  })
}

/**
 * 管理员批量分配任务给多个用户
 */
export const batchAssignTask = (taskId, userIds) => {
  return request({
    url: '/user-task/batch-assign',
    method: 'post',
    params: { taskId },
    data: userIds
  })
}

/**
 * 获取任务的所有分配情况
 */
export const getTaskAssignments = (taskId) => {
  return request({
    url: `/user-task/task/${taskId}/assignees`,
    method: 'get'
  })
}

/**
 * 获取指定用户的任务列表（管理员）
 */
export const getUserTasksByAdmin = (userId) => {
  return request({
    url: `/user-task/user/${userId}/tasks`,
    method: 'get'
  })
}

/**
 * 更新任务完成状态
 */
export const updateTaskCompletionStatus = (userTaskId, status) => {
  return request({
    url: `/user-task/${userTaskId}/completion-status`,
    method: 'put',
    params: { status }
  })
}
