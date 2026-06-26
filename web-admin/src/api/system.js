import request from '@/utils/request'

// 用户管理
export function getUserList(params) {
  return request({ url: '/admin/system/user/list', method: 'get', params })
}

export function updateUser(id, data) {
  return request({ url: `/admin/system/user/${id}`, method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/admin/system/user/${id}`, method: 'delete' })
}

// 重置密码（后端 @RequestParam newPassword）
export function resetUserPassword(id, newPassword) {
  return request({ url: `/admin/system/user/${id}/reset-password`, method: 'put', params: { newPassword: newPassword || '123456' } })
}

// 角色管理
export function getRoleList(params) {
  return request({ url: '/admin/system/role/list', method: 'get', params })
}

export function createRole(data) {
  return request({ url: '/admin/system/role', method: 'post', data })
}

export function updateRole(id, data) {
  return request({ url: `/admin/system/role/${id}`, method: 'put', data })
}

export function deleteRole(id) {
  return request({ url: `/admin/system/role/${id}`, method: 'delete' })
}

// 权限管理
export function getPermissionList(params) {
  return request({ url: '/admin/system/permission/list', method: 'get', params })
}

// 参数配置（后端 create/update 都是 @RequestParam）
export function getConfigList(params) {
  return request({ url: '/admin/system/config/list', method: 'get', params })
}

export function createConfig(data) {
  return request({ url: '/admin/system/config', method: 'post', params: data })
}

export function updateConfig(id, data) {
  return request({ url: `/admin/system/config/${id}`, method: 'put', params: data })
}

export function deleteConfig(id) {
  return request({ url: `/admin/system/config/${id}`, method: 'delete' })
}

// 操作日志
export function getLogList(params) {
  return request({ url: '/admin/system/log/list', method: 'get', params })
}

// 小程序用户管理
export function getMiniappUserList(params) {
  return request({ url: '/admin/system/miniapp-user/list', method: 'get', params })
}

export function createMiniappUser(data) {
  return request({ url: '/admin/system/miniapp-user', method: 'post', data })
}

export function getMiniappUserCount() {
  return request({ url: '/admin/system/miniapp-user/count', method: 'get' })
}

// 后台管理 - 企业详情查看（执法人员扫码查看）
export function getEnterpriseDetailByCode(code) {
  return request({ url: '/admin/system/enterprise/detail', method: 'get', params: { code } })
}

export function getEnterpriseDetailById(id) {
  return request({ url: `/admin/system/enterprise/${id}/detail`, method: 'get' })
}

// 组织架构管理
export function getOrgTree() {
  return request({ url: '/admin/system/org/tree', method: 'get' })
}

export function getOrgList() {
  return request({ url: '/admin/system/org/list', method: 'get' })
}

export function createOrgNode(data) {
  return request({ url: '/admin/system/org', method: 'post', data })
}

export function updateOrgNode(id, data) {
  return request({ url: `/admin/system/org/${id}`, method: 'put', data })
}

export function deleteOrgNode(id) {
  return request({ url: `/admin/system/org/${id}`, method: 'delete' })
}

export function getOrgPath(id) {
  return request({ url: `/admin/system/org/${id}/path`, method: 'get' })
}

// 岗位管理
export function getPositionList(params) {
  return request({ url: '/admin/system/position/list', method: 'get', params })
}

export function createPosition(data) {
  return request({ url: '/admin/system/position', method: 'post', data })
}

export function updatePosition(id, data) {
  return request({ url: `/admin/system/position/${id}`, method: 'put', data })
}

export function deletePosition(id) {
  return request({ url: `/admin/system/position/${id}`, method: 'delete' })
}

export function getPositionsByCategory(category) {
  return request({ url: `/admin/system/position/category/${category}`, method: 'get' })
}

// 职务管理
export function getJobTitleList(params) {
  return request({ url: '/admin/system/job-title/list', method: 'get', params })
}

export function createJobTitle(data) {
  return request({ url: '/admin/system/job-title', method: 'post', data })
}

export function updateJobTitle(id, data) {
  return request({ url: `/admin/system/job-title/${id}`, method: 'put', data })
}

export function deleteJobTitle(id) {
  return request({ url: `/admin/system/job-title/${id}`, method: 'delete' })
}

export function getLeadershipTitles() {
  return request({ url: '/admin/system/job-title/leadership', method: 'get' })
}

// 用户岗位职务分配
export function assignUserPosition(data) {
  return request({ url: '/admin/system/user-position', method: 'post', data })
}

export function removeUserPosition(id) {
  return request({ url: `/admin/system/user-position/${id}`, method: 'delete' })
}

export function getUserPositions(userId) {
  return request({ url: `/admin/system/user-position/user/${userId}`, method: 'get' })
}

export function getOrgUserPositions(orgId) {
  return request({ url: `/admin/system/user-position/org/${orgId}`, method: 'get' })
}

export function setPrimaryPosition(id) {
  return request({ url: `/admin/system/user-position/${id}/primary`, method: 'put' })
}

// 事件驱动：入职/调动/离职/升降级
export function onboardUser(userId, data) {
  return request({ url: `/admin/system/user/${userId}/onboard`, method: 'post', data })
}

export function transferUser(userId, data) {
  return request({ url: `/admin/system/user/${userId}/transfer`, method: 'post', data })
}

export function resignUser(userId, data) {
  return request({ url: `/admin/system/user/${userId}/resign`, method: 'post', data })
}

export function promoteUser(userId, data) {
  return request({ url: `/admin/system/user/${userId}/promote`, method: 'post', data })
}
