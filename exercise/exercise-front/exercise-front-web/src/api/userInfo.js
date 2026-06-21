import request from '@/utils/request'

/**
 * 用户端 userInfo API
 */
export const userInfoApi = {
  register: (data) => request.post('/userInfo/register', data),
  login: (data) => request.post('/userInfo/login', data),
  logout: () => request.post('/userInfo/logout'),
  getProfile: () => request.get('/userInfo/getProfile'),
  updateProfile: (data) => request.post('/userInfo/updateProfile', data),
  changePassword: (data) => request.post('/userInfo/changePassword', data)
}