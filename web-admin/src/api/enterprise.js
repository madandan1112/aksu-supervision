import request from '@/utils/request'

// 管理端 - 企业列表
export function getEnterpriseList(params) {
  return request({
    url: '/admin/enterprise/list',
    method: 'get',
    params
  })
}

// 企业详情
export function getEnterpriseDetail(id) {
  return request({
    url: `/admin/enterprise/${id}`,
    method: 'get'
  })
}

// 更新企业状态
export function updateEnterpriseStatus(id, data) {
  return request({
    url: `/admin/enterprise/${id}/status`,
    method: 'put',
    data
  })
}

// 管理端 - 编辑企业信息
export function adminUpdateEnterprise(id, data) {
  return request({
    url: `/admin/enterprise/${id}`,
    method: 'put',
    data
  })
}
