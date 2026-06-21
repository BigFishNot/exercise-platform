import request from '@/utils/request'

/**
 * 管理端登录 API
 * 路径：/admin/login（exercise-admin 服务独有）
 */
export const authApi = {
  login: (account, password) =>
    request.post('/admin/login', { account, password }),
  logout: () => request.post('/admin/logout')
}