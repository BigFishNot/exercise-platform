import request from '@/utils/request'

/**
 * 运动类型用户端 API
 * 后续阶段计划 / 运动记录 / 打卡判定等模块都会调用此处
 */
export const exerciseTypeApi = {
  /** 用户端下拉：仅返回启用的运动类型 */
  getOptions: () => request.get('/exerciseType/getOptions'),
  /** 详情 */
  getDetail: (typeId) => request.get(`/exerciseType/detail/${typeId}`)
}
