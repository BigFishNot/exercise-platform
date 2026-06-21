/**
 * Token 工具
 * 管理端 token header：adminToken；存储用 localStorage
 */
const TOKEN_KEY = 'admin_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  }
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}