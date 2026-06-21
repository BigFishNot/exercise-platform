import request from '@/utils/request'

/**
 * 运动类型管理端 API
 */
export const exerciseTypeApi = {
  loadDataList: (query) => request.post('/exerciseType/loadDataList', query),
  add: (data) => request.post('/exerciseType/add', data),
  update: (data) => request.post('/exerciseType/update', data),
  remove: (typeId) => request.post('/exerciseType/delete', { typeId }),
  updateStatus: (typeId, status) =>
    request.post('/exerciseType/updateStatus', { typeId, status })
}
