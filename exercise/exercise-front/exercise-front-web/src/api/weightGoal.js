import request from '@/utils/request'

/**
 * 减肥目标 用户端 API
 */
export const weightGoalApi = {
  add: (data) => request.post('/weightGoal/add', data),
  update: (data) => request.post('/weightGoal/update', data),
  archive: (goalId) => request.post('/weightGoal/archive', { goalId }),
  getActive: () => request.get('/weightGoal/getActive'),
  list: () => request.get('/weightGoal/list')
}
