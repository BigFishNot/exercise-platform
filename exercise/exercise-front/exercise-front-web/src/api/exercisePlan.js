import request from '@/utils/request'

/**
 * 阶段计划 用户端 API
 */
export const exercisePlanApi = {
  add: (data) => request.post('/exercisePlan/add', data),
  updateDailyTarget: (data) => request.post('/exercisePlan/updateDailyTarget', data),
  cancel: (planId) => request.post('/exercisePlan/cancel', { planId }),
  getCurrent: () => request.get('/exercisePlan/getCurrent'),
  list: () => request.get('/exercisePlan/list'),
  getCalendar: (planId) => request.get(`/exercisePlan/getCalendar/${planId}`)
}
