import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/token'

const routes = [
  { path: '/login',    name: 'Login',    component: () => import('@/views/auth/LoginView.vue'),    meta: { title: '登录' } },
  { path: '/register', name: 'Register', component: () => import('@/views/auth/RegisterView.vue'), meta: { title: '注册' } },
  {
    path: '/',
    component: () => import('@/layout/WebLayout.vue'),
    redirect: '/profile',
    children: [
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { title: '个人资料' }
      },
      {
        path: '/plan',
        name: 'PlanCurrent',
        component: () => import('@/views/plan/PlanCurrentView.vue'),
        meta: { title: '阶段计划' }
      },
      {
        path: '/plan/create',
        name: 'PlanCreate',
        component: () => import('@/views/plan/PlanCreateView.vue'),
        meta: { title: '创建阶段计划' }
      },
      {
        path: '/plan/calendar/:planId',
        name: 'PlanCalendar',
        component: () => import('@/views/plan/PlanCalendarView.vue'),
        meta: { title: '阶段日历' }
      },
      {
        path: '/exercise',
        name: 'ExerciseStart',
        component: () => import('@/views/exercise/ExerciseStartView.vue'),
        meta: { title: '运动打卡' }
      },
      {
        path: '/exercise/today',
        name: 'ExerciseToday',
        component: () => import('@/views/exercise/ExerciseTodayView.vue'),
        meta: { title: '今日运动' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 用户端所有页面都需要登录（除登录 / 注册）
router.beforeEach((to, from, next) => {
  const token = getToken()
  const isAuthRoute = to.path === '/login' || to.path === '/register'
  if (!isAuthRoute && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  if (isAuthRoute && token) {
    return next('/')
  }
  next()
})

export default router