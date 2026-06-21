import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/token'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { requireLogin: false, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/user/list',
    children: [
      {
        path: '/user/list',
        name: 'UserList',
        component: () => import('@/views/user/UserListView.vue'),
        meta: { requireLogin: true, title: '用户管理' }
      },
      {
        path: '/exerciseType/list',
        name: 'ExerciseTypeList',
        component: () => import('@/views/exerciseType/ExerciseTypeListView.vue'),
        meta: { requireLogin: true, title: '运动类型' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳登录页
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.meta.requireLogin && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  if (to.path === '/login' && token) {
    return next('/')
  }
  next()
})

export default router