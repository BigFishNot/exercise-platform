import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/token'

/**
 * 管理端登录 store：仅持有 token
 */
export const useLoginStore = defineStore('login-admin', {
  state: () => ({
    token: getToken()
  }),
  actions: {
    setLoginToken(token) {
      this.token = token
      setToken(token)
    },
    logout() {
      this.token = ''
      removeToken()
    }
  }
})