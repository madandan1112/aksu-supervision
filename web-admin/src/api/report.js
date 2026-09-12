import request from '@/utils/request'

// 管理端 - 报告列表
export function getReportList(params) {
  return request({
    url: '/admin/report/list',
    method: 'get',
    params
  })
}

// 审核报告（后端 @RequestParam status, comment）
export function reviewReport(id, data) {
  return request({
    url: `/admin/report/${id}/review`,
    method: 'put',
    params: data
  })
}

// 即将过期报告
export function getExpiringReports(params) {
  return request({
    url: '/admin/report/expiring',
    method: 'get',
    params
  })
}

// 更新报告（重新上传）
export function updateReport(id, data) {
  return request({
    url: `/admin/report/${id}`,
    method: 'put',
    data
  })
}

// 企业上传报告（后端 @RequestBody）
export function uploadReport(data) {
  return request({
    url: '/enterprise/report/upload',
    method: 'post',
    data
  })
}

// 企业报告列表
export function getEnterpriseReportList(params) {
  return request({
    url: '/enterprise/report/list',
    method: 'get',
    params
  })
}
