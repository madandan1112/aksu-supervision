import { defineStore } from 'pinia'
import { ref } from 'vue'
import { get } from '@/utils/request'

export const useMessageStore = defineStore('message', () => {
  const unreadCount = ref(0)

  /** 获取未读消息数 */
  async function fetchUnreadCount() {
    try {
      const data = await get('/message/unread-count')
      unreadCount.value = data.count || 0
    } catch (e) {
      console.error('获取未读消息数失败', e)
    }
  }

  /** 减少未读数 */
  function decreaseUnread(count = 1) {
    unreadCount.value = Math.max(0, unreadCount.value - count)
  }

  /** 清零未读数 */
  function clearUnread() {
    unreadCount.value = 0
  }

  return {
    unreadCount,
    fetchUnreadCount,
    decreaseUnread,
    clearUnread
  }
})
