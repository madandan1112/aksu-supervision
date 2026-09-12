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

// 接收预警
export function acceptAlert(id) {
  return request({
    url: `/inspector/alert/${id}/accept`,
    method: 'post'
  })
}

// 处理预警
export function handleAlert(id, data) {
  return request({
    url: `/inspector/alert/${id}/handle`,
    method: 'post',
    params: data
  })
}

// 退回预警
export function rejectAlert(id, reason) {
  return request({
    url: `/inspector/alert/${id}/reject`,
    method: 'post',
    params: { reason }
  })
}

// 预警统计
export function getAlertStats() {
  return request({
    url: '/inspector/alert/statistics',
    method: 'get'
  })
}

// 手动触发预警扫描
export function triggerAlertScan() {
  return request({
    url: '/inspector/alert/scan',
    method: 'post'
  })
}

// 预警类型选项
export function getAlertTypes() {
  return request({
    url: '/inspector/alert/types',
    method: 'get'
  })
}

// 企业活跃预警
export function getEnterpriseActiveAlerts(enterpriseId) {
  return request({
    url: `/inspector/alert/enterprise/${enterpriseId}/active`,
    method: 'get'
  })
}
