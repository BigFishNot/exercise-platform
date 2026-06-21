import request from '@/utils/request'

/**
 * 阶段计划 管理端 API
 * 供后续用户详情"按用户查阶段计划"使用
 */
export const exercisePlanApi = {
  loadByUser: (query) => request.post('/exercisePlan/admin/loadByUser', query),
  detail: (planId) => request.get(`/exercisePlan/admin/detail/${planId}`)
}
