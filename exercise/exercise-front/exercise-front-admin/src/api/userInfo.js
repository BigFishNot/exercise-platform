import request from '@/utils/request'

/**
 * 用户管理 API（管理端）
 * 路径与后端模块名一致：/userInfo/<动作>
 */
export const userInfoApi = {
  loadDataList: (params) => request.get('/userInfo/loadDataList', { params }),
  detail: (userId) => request.get('/userInfo/detail', { params: { userId } }),
  updateStatus: (userId, status) =>
    request.post('/userInfo/updateStatus', null, { params: { userId, status } })
}