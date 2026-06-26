/**
 * 日期格式化
 * @param {Date|string|number} date 日期对象/时间戳/日期字符串
 * @param {string} fmt 格式模板，默认 YYYY-MM-DD HH:mm
 */
export function formatDate(date, fmt = 'YYYY-MM-DD HH:mm') {
  if (!date) return ''
  const d = date instanceof Date ? date : new Date(date)
  if (isNaN(d.getTime())) return ''

  const map = {
    'YYYY': d.getFullYear(),
    'MM': String(d.getMonth() + 1).padStart(2, '0'),
    'DD': String(d.getDate()).padStart(2, '0'),
    'HH': String(d.getHours()).padStart(2, '0'),
    'mm': String(d.getMinutes()).padStart(2, '0'),
    'ss': String(d.getSeconds()).padStart(2, '0')
  }

  let result = fmt
  for (const [key, value] of Object.entries(map)) {
    result = result.replace(key, value)
  }
  return result
}

/**
 * 相对时间（如：刚刚、5分钟前、昨天）
 */
export function timeAgo(date) {
  if (!date) return ''
  const d = date instanceof Date ? date : new Date(date)
  const now = Date.now()
  const diff = now - d.getTime()

  if (diff < 60 * 1000) return '刚刚'
  if (diff < 60 * 60 * 1000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 24 * 60 * 60 * 1000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 48 * 60 * 60 * 1000) return '昨天'
  if (diff < 30 * 24 * 60 * 60 * 1000) return `${Math.floor(diff / 86400000)}天前`
  return formatDate(d, 'YYYY-MM-DD')
}

/**
 * 文件大小格式化
 * @param {number} bytes 字节数
 */
export function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const k = 1024
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(i > 0 ? 1 : 0) + ' ' + units[i]
}

/**
 * 手机号脱敏
 */
export function maskPhone(phone) {
  if (!phone || phone.length < 7) return phone || ''
  return phone.slice(0, 3) + '****' + phone.slice(7)
}

/**
 * 截断文本
 */
export function truncateText(text, maxLen = 50) {
  if (!text) return ''
  return text.length > maxLen ? text.slice(0, maxLen) + '...' : text
}
