import { getToken, clearToken } from './auth'

// #ifdef H5
const BASE_URL = '/api'
// #endif
// #ifndef H5
const BASE_URL = 'http://localhost:8080/api'
// #endif

function request(options) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const header = {
      'Content-Type': 'application/json',
      ...(options.header || {})
    }
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        if (res.statusCode === 200) {
          const data = res.data
          if (data.code === 0 || data.code === 200) {
            resolve(data.data)
          } else if (data.code === 401) {
            handleUnauthorized()
            reject(new Error(data.message || '登录已过期，请重新登录'))
          } else {
            uni.showToast({ title: data.message || '请求失败', icon: 'none' })
            reject(new Error(data.message || '请求失败'))
          }
        } else if (res.statusCode === 401) {
          handleUnauthorized()
          reject(new Error('登录已过期，请重新登录'))
        } else {
          uni.showToast({ title: `请求错误(${res.statusCode})`, icon: 'none' })
          reject(new Error(`请求错误: ${res.statusCode}`))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常，请稍后重试', icon: 'none' })
        reject(err)
      }
    })
  })
}

function handleUnauthorized() {
  clearToken()
  uni.showToast({ title: '登录已过期', icon: 'none' })
  setTimeout(() => {
    uni.reLaunch({ url: '/pages/login/index' })
  }, 1500)
}

export function get(url, data = {}) {
  return request({ url, method: 'GET', data })
}

export function post(url, data = {}) {
  return request({ url, method: 'POST', data })
}

export function put(url, data = {}) {
  return request({ url, method: 'PUT', data })
}

export function del(url, data = {}) {
  return request({ url, method: 'DELETE', data })
}

export function uploadFile(url, filePath, name = 'file', formData = {}) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    uni.uploadFile({
      url: BASE_URL + url,
      filePath,
      name,
      formData,
      header: token ? { 'Authorization': `Bearer ${token}` } : {},
      success: (res) => {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data)
          if (data.code === 0 || data.code === 200) {
            resolve(data.data)
          } else {
            uni.showToast({ title: data.message || '上传失败', icon: 'none' })
            reject(new Error(data.message))
          }
        } else if (res.statusCode === 401) {
          handleUnauthorized()
          reject(new Error('登录已过期'))
        } else {
          reject(new Error(`上传失败: ${res.statusCode}`))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

export default request
