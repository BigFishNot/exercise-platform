import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/token'

/**
 * 用户端登录 store：token + 当前用户信息
 */
export const useLoginStore = defineStore('login-web', {
  state: () => ({
    token: getToken(),
    userInfo: JSON.parse(localStorage.getItem('user_info') || 'null')
  }),
  actions: {
    setLoginToken(token, userInfo) {
      this.token = token
      setToken(token)
      if (userInfo) {
        this.userInfo = userInfo
        localStorage.setItem('user_info', JSON.stringify(userInfo))
      }
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('user_info', JSON.stringify(userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      removeToken()
      localStorage.removeItem('user_info')
    }
  }
})