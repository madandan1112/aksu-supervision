import request from '@/utils/request'

// 管理端 - 任务列表
export function getTaskList(params) {
  return request({
    url: '/admin/task/list',
    method: 'get',
    params
  })
}

// 创建任务（后端 @RequestBody TaskCreateRequest）
export function createTask(data) {
  return request({
    url: '/admin/task',
    method: 'post',
    data
  })
}

// 更新任务（后端 @RequestBody TaskCreateRequest）
export function updateTask(id, data) {
  return request({
    url: `/admin/task/${id}`,
    method: 'put',
    data
  })
}

// 终止任务（后端 @RequestParam reason）
export function terminateTask(id, data) {
  return request({
    url: `/admin/task/${id}/terminate`,
    method: 'post',
    params: data
  })
}

// 检查员任务列表
export function getInspectorTaskList(params) {
  return request({
    url: '/inspector/task/list',
    method: 'get',
    params
  })
}

// 检查员任务统计
export function getInspectorTaskStats(params) {
  return request({
    url: '/inspector/task/statistics',
    method: 'get',
    params
  })
}

// 认领任务
export function claimTask(id) {
  return request({
    url: `/inspector/task/${id}/claim`,
    method: 'post'
  })
}

// 申请延期（后端 @RequestParam requestedEndTime, reason）
export function requestExtension(id, data) {
  return request({
    url: `/inspector/task/${id}/extend`,
    method: 'post',
    params: data
  })
}
