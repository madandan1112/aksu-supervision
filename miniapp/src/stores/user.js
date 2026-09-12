import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, clearToken, getUserInfo, setUserInfo } from '@/utils/auth'
import { post, get } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref(getUserInfo())

  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => userInfo.value?.realName || userInfo.value?.username || '')
  const enterpriseName = computed(() => userInfo.value?.enterpriseName || '')
  const userType = computed(() => userInfo.value?.userType || '')
  const isAdmin = computed(() => userInfo.value?.userType === 'admin')
  const isInspector = computed(() => userInfo.value?.userType === 'inspector')
  const isEnterprise = computed(() => userInfo.value?.userType === 'enterprise')

  /** 账号密码登录（小程序端免验证码通道） */
  async function accountLogin(username, password) {
    const data = await post('/auth/miniprogram-login', { username, password })
    token.value = data.token
    // 必须先持久化 token，后续 /auth/userinfo 请求才能带上 Authorization
    setToken(data.token)
    const info = await get('/auth/userinfo')
    userInfo.value = info
    setUserInfo(info)
    return data
  }

  /** 微信登录 */
  async function wxLogin() {
    const [, loginRes] = await uni.login({ provider: 'weixin' })
    if (!loginRes || !loginRes.code) {
      throw new Error('微信登录失败')
    }
    const data = await post('/auth/wx-login', { code: loginRes.code })
    token.value = data.token
    setToken(data.token)
    const info = await get('/auth/userinfo')
    userInfo.value = info
    setUserInfo(info)
    return data
  }

  /** 退出登录 */
  function logout() {
    token.value = ''
    userInfo.value = null
    clearToken()
    uni.reLaunch({ url: '/pages/login/index' })
  }

  /** 更新用户信息 */
  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    setUserInfo(userInfo.value)
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    userName,
    enterpriseName,
    userType,
    isAdmin,
    isInspector,
    isEnterprise,
    accountLogin,
    wxLogin,
    logout,
    updateUserInfo
  }
})
