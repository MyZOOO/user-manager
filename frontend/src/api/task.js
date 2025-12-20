import request from '../utils/request'

// 任务管理相关API

/**
 * 发布任务
 */
export const publishTask = (data) => {
  return request({
    url: '/task/publish',
    method: 'post',
    data
  })
}

/**
 * 获取我发布的任务
 */
export const getMyPublishedTasks = () => {
  return request({
    url: '/task/my-published',
    method: 'get'
  })
}

/**
 * 获取所有任务列表
 */
export const getTaskList = () => {
  return request({
    url: '/task/list',
    method: 'get'
  })
}

/**
 * 获取任务详情
 */
export const getTaskDetail = (taskId) => {
  return request({
    url: `/task/${taskId}`,
    method: 'get'
  })
}

/**
 * 根据状态获取任务
 */
export const getTasksByStatus = (status) => {
  return request({
    url: `/task/status/${status}`,
    method: 'get'
  })
}

/**
 * 更新任务状态
 */
export const updateTaskStatus = (taskId, status) => {
  return request({
    url: `/task/${taskId}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 更新任务环节
 */
export const updateTaskStage = (taskId, stage) => {
  return request({
    url: `/task/${taskId}/stage`,
    method: 'put',
    params: { stage }
  })
}

/**
 * 编辑任务
 */
export const updateTask = (taskId, data) => {
  return request({
    url: `/task/${taskId}`,
    method: 'put',
    data
  })
}

/**
 * 删除任务
 */
export const deleteTask = (taskId) => {
  return request({
    url: `/task/${taskId}`,
    method: 'delete'
  })
}

// 用户任务分配相关API

/**
 * 分配任务给用户
 */
export const assignTask = (taskId, userId) => {
  return request({
    url: '/user-task/assign',
    method: 'post',
    params: { taskId, userId }
  })
}

/**
 * 批量分配任务给多个用户
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
 * 获取我的任务
 */
export const getMyTasks = () => {
  return request({
    url: '/user-task/my-tasks',
    method: 'get'
  })
}

/**
 * 获取任务的分配用户
 */
export const getTaskAssignees = (taskId) => {
  return request({
    url: `/user-task/task/${taskId}/assignees`,
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
 * 取消任务分配
 */
export const removeTaskAssignment = (userTaskId) => {
  return request({
    url: `/user-task/${userTaskId}`,
    method: 'delete'
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
