import request from '@/utils/request'

/**
 * 打卡判定 用户端 API
 */
export const exerciseCheckInApi = {
  /** 当日打卡状态 */
  getToday: () => request.get('/exerciseCheckIn/getToday')
}
