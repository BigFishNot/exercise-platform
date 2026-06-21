import request from '@/utils/request'

/**
 * 身体数据 用户端 API
 */
export const bodyDataApi = {
  upsert: (data) => request.post('/bodyData/upsert', data),
  getToday: () => request.get('/bodyData/getToday'),
  getById: (id) => request.get(`/bodyData/getById?id=${id}`),
  delete: (id) => request.post('/bodyData/delete', { id }),
  /** range: 7d / 30d / 90d / stage / custom */
  getTrend: (query) => request.post('/bodyData/getTrend', query)
}
