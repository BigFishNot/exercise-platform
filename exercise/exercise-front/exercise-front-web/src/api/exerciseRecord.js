import request from '@/utils/request'

/**
 * 运动记录 用户端 API
 */
export const exerciseRecordApi = {
  start: (data) => request.post('/exerciseRecord/start', data),
  finish: (data) => request.post('/exerciseRecord/finish', data),
  abandon: (data) => request.post('/exerciseRecord/abandon', { ...data, abandonReason: 'USER_MANUAL' }),
  getActive: () => request.get('/exerciseRecord/getActive'),
  listToday: () => request.get('/exerciseRecord/listToday'),
  sumTodayActual: () => request.get('/exerciseRecord/sumTodayActual')
}
