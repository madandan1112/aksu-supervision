const TOKEN_KEY = 'aksu_token'
const USER_KEY = 'aksu_user_info'

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function clearToken() {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(USER_KEY)
}

export function getUserInfo() {
  const info = uni.getStorageSync(USER_KEY)
  return info ? JSON.parse(info) : null
}

export function setUserInfo(info) {
  uni.setStorageSync(USER_KEY, JSON.stringify(info))
}

export function isLoggedIn() {
  return !!getToken()
}
