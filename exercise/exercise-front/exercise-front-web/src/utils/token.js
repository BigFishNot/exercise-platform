/**
 * 用户端 token 工具
 * 用户端 token header：studentToken
 */
const TOKEN_KEY = 'user_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  if (token) localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}