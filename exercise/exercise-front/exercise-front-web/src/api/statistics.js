import request from '@/utils/request'

/**
 * 统计 用户端 API
 */
export const statisticsApi = {
  personalSummary: () => request.get('/statistics/personalSummary')
}
