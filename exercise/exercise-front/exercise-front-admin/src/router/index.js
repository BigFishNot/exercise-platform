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
      },
      {
        path: '/mail/config',
        name: 'MailConfig',
        component: () => import('@/views/mail/MailConfigView.vue'),
        meta: { requireLogin: true, title: '邮件配置' }
      },
      {
        path: '/mail/template',
        name: 'MailTemplate',
        component: () => import('@/views/mail/MailTemplateView.vue'),
        meta: { requireLogin: true, title: '邮件模板' }
      },
      {
        path: '/mail/log',
        name: 'MailLog',
        component: () => import('@/views/mail/MailLogView.vue'),
        meta: { requireLogin: true, title: '发送日志' }
      },
      {
        path: '/mail/send',
        name: 'MailSend',
        component: () => import('@/views/mail/MailSendView.vue'),
        meta: { requireLogin: true, title: '手动发送' }
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