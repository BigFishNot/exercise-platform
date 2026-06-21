import axios from 'axios'
import { getToken, removeToken } from './token'
import { showError } from './message'

// baseURL 固定 /api；token header：studentToken
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000
})

service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['studentToken'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (resp) => {
    const data = resp.data || {}
    if (data.code === 200) return data.data
    if (data.code === 401) {
      removeToken()
      const redirect = window.location.pathname + window.location.search
      window.location.href = '/login?redirect=' + encodeURIComponent(redirect)
      return Promise.reject(new Error('未登录'))
    }
    showError(data.info || '请求失败')
    return Promise.reject(new Error(data.info || '请求失败'))
  },
  (error) => {
    const info = error?.response?.data?.info || error.message || '网络异常'
    showError(info)
    return Promise.reject(error)
  }
)

export default service