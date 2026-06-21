import request from '@/utils/request'

/**
 * AI 鼓励 用户端 API
 * 每天最多 3 次，超额抛 AI_ENCOURAGE_LIMIT_EXCEEDED
 */
export const aiEncourageApi = {
  generate: () => request.get('/aiEncourage/generate')
}
