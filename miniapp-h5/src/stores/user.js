import { defineStore } from 'pinia'
import request from '../utils/request'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    userType: localStorage.getItem('userType') || ''
  }),

  getters: {
    isInspector: (state) => state.userType === 'inspector',
    isEnterprise: (state) => state.userType === 'enterprise' || state.userType === 'enterprise_user',
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(username, password) {
      const res = await request.post('/auth/miniprogram-login', {
        username,
        password
      })

      const data = res.data?.data || res.data
      if (data.token) {
        this.token = data.token
        localStorage.setItem('token', data.token)
      }
      if (data.userType) {
        this.userType = data.userType
        localStorage.setItem('userType', data.userType)
      }
      if (data.realName) {
        this.userInfo = { ...this.userInfo, realName: data.realName, userType: data.userType }
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      }

      // 获取完整用户信息
      try {
        const infoRes = await request.get('/auth/userinfo')
        const info = infoRes.data?.data || infoRes.data
        if (info) {
          this.userInfo = info
          localStorage.setItem('userInfo', JSON.stringify(info))
          if (info.userType) {
            this.userType = info.userType
            localStorage.setItem('userType', info.userType)
          }
        }
      } catch { /* ignore */ }

      return data
    },

    logout() {
      this.token = ''
      this.userInfo = null
      this.userType = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('userType')
    }
  }
})
