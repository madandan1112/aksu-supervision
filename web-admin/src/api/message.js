import request from '@/utils/request'

// 消息列表
export function getMessageList(params) {
  return request({
    url: '/message/list',
    method: 'get',
    params
  })
}

// 标记已读
export function markMessageRead(id) {
  return request({
    url: `/message/${id}/read`,
    method: 'put'
  })
}

// 删除消息
export function deleteMessage(id) {
  return request({
    url: `/message/${id}`,
    method: 'delete'
  })
}

// 消息设置
export function updateMessageSettings(data) {
  return request({
    url: '/message/settings',
    method: 'put',
    data
  })
}
