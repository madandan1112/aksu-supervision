import request from '@/utils/request'

export function getDashboardOverview() {
  return request({
    url: '/admin/dashboard/overview',
    method: 'get'
  })
}

export function getIndustryView() {
  return request({
    url: '/admin/dashboard/industry-view',
    method: 'get'
  })
}

export function getAreaView() {
  return request({
    url: '/admin/dashboard/area-view',
    method: 'get'
  })
}

export function getRiskProfile() {
  return request({
    url: '/admin/dashboard/risk-profile',
    method: 'get'
  })
}
