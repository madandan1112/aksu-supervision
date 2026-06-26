import request from '@/utils/request'

// 管理端 - 诉求列表
export function getAppealList(params) {
  return request({
    url: '/admin/appeal/list',
    method: 'get',
    params
  })
}

// 诉求详情
export function getAppealDetail(id) {
  return request({
    url: `/appeal/${id}`,
    method: 'get'
  })
}

// 分配诉求（后端 @RequestParam assignedTo）
export function assignAppeal(id, data) {
  return request({
    url: `/admin/appeal/${id}/assign`,
    method: 'put',
    params: data
  })
}

// 处理诉求（后端 @RequestParam handleResult, handleAttachment）
export function handleAppeal(id, data) {
  return request({
    url: `/admin/appeal/${id}/handle`,
    method: 'put',
    params: data
  })
}

// 诉求统计
export function getAppealStats(params) {
  return request({
    url: '/admin/appeal/statistics',
    method: 'get',
    params
  })
}

// 评价诉求（后端 @RequestParam satisfaction, comment）
export function evaluateAppeal(id, data) {
  return request({
    url: `/appeal/${id}/evaluate`,
    method: 'post',
    params: data
  })
}
