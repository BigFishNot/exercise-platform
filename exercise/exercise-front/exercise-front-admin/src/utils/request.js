import axios from 'axios'
import { getToken, removeToken } from './token'
import { showError } from './message'

// baseURL 固定 /api；生产可由环境变量拼接
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000
})

// 请求拦截：从登录态读取 token，写入 adminToken
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['adminToken'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截：统一解 ResponseVO 包装；401 跳转登录
service.interceptors.response.use(
  (resp) => {
    const data = resp.data || {}
    if (data.code === 200) {
      return data.data
    }
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