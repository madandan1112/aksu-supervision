import request from '@/utils/request'

// 管理端 - 检查记录列表
export function getAdminInspectionList(params) {
  return request({ url: '/admin/inspection/list', method: 'get', params })
}

// 检查记录详情
export function getAdminInspectionDetail(id) {
  return request({ url: `/admin/inspection/${id}`, method: 'get' })
}

// 检查统计
export function getAdminInspectionStats() {
  return request({ url: '/admin/inspection/stats', method: 'get' })
}

// 管理端 - 整改通知列表
export function getAdminRectificationList(params) {
  return request({ url: '/admin/rectification/list', method: 'get', params })
}

// 整改详情
export function getAdminRectificationDetail(id) {
  return request({ url: `/admin/rectification/${id}`, method: 'get' })
}

// 整改反馈列表
export function getAdminRectificationFeedbacks(id) {
  return request({ url: `/admin/rectification/${id}/feedbacks`, method: 'get' })
}

// 整改统计
export function getAdminRectificationStats() {
  return request({ url: '/admin/rectification/stats', method: 'get' })
}
