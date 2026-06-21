<template>
  <div class="web-layout">
    <header class="web-header">
      <div class="web-header-inner">
        <div class="brand" @click="goHome">
          <div class="brand-mark"><ThunderboltFilled /></div>
          <div class="brand-text">
            <div class="brand-name">运动平台</div>
            <div class="brand-tag">让坚持看得见</div>
          </div>
        </div>

        <nav class="web-nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.disabled ? '#' : item.path"
            class="nav-link"
            :class="{ 'is-disabled': item.disabled, 'is-active': $route.path === item.path }"
            @click.prevent="!item.disabled && router.push(item.path)"
          >
            <component :is="item.icon" />
            <span>{{ item.label }}</span>
            <a-tag v-if="item.disabled" color="default" class="nav-soon">敬请期待</a-tag>
          </router-link>
        </nav>

        <div class="web-user">
          <a-tooltip title="消息">
            <a-badge :count="0" :offset="[-2, 2]">
              <a-button type="text" shape="circle"><BellOutlined /></a-button>
            </a-badge>
          </a-tooltip>

          <a-dropdown trigger="click">
            <div class="user-chip">
              <a-avatar :size="34" class="user-avatar">
                {{ avatarText }}
              </a-avatar>
              <div class="user-meta">
                <div class="user-name">{{ nickname }}</div>
                <div class="user-role">普通用户</div>
              </div>
              <DownOutlined class="user-caret" />
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="router.push('/profile')">
                  <UserOutlined />个人资料
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item @click="handleLogout">
                  <LogoutOutlined />退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
    </header>

    <main class="web-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  BellOutlined,
  DownOutlined,
  LogoutOutlined,
  UserOutlined,
  ThunderboltFilled,
  ProfileOutlined,
  FireOutlined,
  HistoryOutlined,
  LineChartOutlined,
  AimOutlined
} from '@ant-design/icons-vue'
import { useLoginStore } from '@/stores/useLoginStore'
import { userInfoApi } from '@/api/userInfo'
import { showSuccess } from '@/utils/message'

const router = useRouter()
const loginStore = useLoginStore()

const navItems = [
  { path: '/plan',           label: '阶段计划', icon: AimOutlined },
  { path: '/exercise',       label: '运动打卡', icon: FireOutlined },
  { path: '/exercise/today', label: '今日记录', icon: HistoryOutlined },
  { path: '/profile',        label: '个人资料', icon: ProfileOutlined },
  { path: '/trend',          label: '趋势分析', icon: LineChartOutlined, disabled: true }
]

const nickname = computed(() => loginStore.userInfo?.nickName || '运动达人')
const avatarText = computed(() => (nickname.value || 'U').slice(0, 1).toUpperCase())

function goHome() {
  router.push('/')
}

async function handleLogout() {
  try { await userInfoApi.logout() } catch (e) { /* 忽略 */ }
  loginStore.logout()
  showSuccess('已退出登录')
  router.replace('/login')
}
</script>

<style lang="scss" scoped>
.web-layout { min-height: 100vh; display: flex; flex-direction: column; }

.web-header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color);
}
.web-header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px;
  height: 68px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.brand-mark {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: #fff;
  font-size: 18px;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}
.brand-text { line-height: 1.2; }
.brand-name { font-size: 16px; font-weight: 700; color: var(--text-primary); }
.brand-tag { font-size: 11px; color: var(--text-tertiary); margin-top: 2px; }

.web-nav {
  flex: 1;
  display: flex;
  gap: 4px;
}
.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: background 0.15s, color 0.15s;
  &:hover { background: var(--color-surface-hover); color: var(--text-primary); }
  &.is-active {
    background: var(--color-primary-light);
    color: var(--color-primary);
  }
  &.is-disabled {
    opacity: 0.55;
    cursor: not-allowed;
    &:hover { background: transparent; color: var(--text-secondary); }
  }
}
.nav-soon {
  margin-left: 4px;
  font-size: 10px;
  padding: 0 6px;
  border-radius: 8px !important;
  line-height: 16px;
}

.web-user { display: flex; align-items: center; gap: 12px; }
.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 10px 4px 4px;
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.2s;
  &:hover { background: var(--color-surface-hover); }
}
.user-avatar {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%) !important;
  color: #fff !important;
  font-weight: 600;
}
.user-meta { line-height: 1.2; }
.user-name { font-size: 13px; font-weight: 600; color: var(--text-primary); }
.user-role { font-size: 11px; color: var(--text-tertiary); }
.user-caret { font-size: 10px; color: var(--text-tertiary); }

.web-content {
  flex: 1;
  max-width: 1280px;
  width: 100%;
  margin: 0 auto;
  padding: 32px;
}

/* 路由切换过渡 */
.fade-slide-enter-active,
.fade-slide-leave-active { transition: all 0.22s ease-out; }
.fade-slide-enter-from { opacity: 0; transform: translateY(8px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-8px); }
</style>