import request from '@/utils/request'

// 预警列表
export function getAlertList(params) {
  return request({
    url: '/inspector/alert/list',
    method: 'get',
    params
  })
}

// 预警详情
export function getAlertDetail(id) {
  return request({
    url: `/inspector/alert/${id}`,
    method: 'get'
  })
}

// 处理预警（后端 @RequestParam handleResult）
export function handleAlert(id, data) {
  return request({
    url: `/inspector/alert/${id}/handle`,
    method: 'post',
    params: data
  })
}

// 预警统计
export function getAlertStats(params) {
  return request({
    url: '/inspector/alert/statistics',
    method: 'get',
    params
  })
}
