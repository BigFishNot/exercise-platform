<template>
  <a-layout class="admin-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      :width="siderWidth"
      :collapsed="collapsed"
      collapsible
      theme="light"
      class="admin-sider"
      :trigger="null"
    >
      <div class="sider-header">
        <BrandLogo v-if="!collapsed" tagline="ADMIN CONSOLE" />
        <div v-else class="sider-logo-collapsed">
          <ThunderboltFilled />
        </div>
      </div>

      <a-menu
        mode="inline"
        :selected-keys="[activeKey]"
        class="admin-menu"
        @click="onMenuClick"
      >
        <a-menu-item key="/user/list">
          <template #icon><TeamOutlined /></template>
          用户管理
        </a-menu-item>
        <a-menu-item key="/placeholder-1" disabled>
          <template #icon><FireOutlined /></template>
          运动类型
          <span class="menu-soon">敬请期待</span>
        </a-menu-item>
        <a-menu-item key="/placeholder-2" disabled>
          <template #icon><MailOutlined /></template>
          邮件配置
          <span class="menu-soon">敬请期待</span>
        </a-menu-item>
      </a-menu>

      <div class="sider-footer">
        <a-button
          block
          type="text"
          class="collapse-btn"
          @click="collapsed = !collapsed"
        >
          <component :is="collapsed ? MenuUnfoldOutlined : MenuFoldOutlined" />
          <span v-if="!collapsed" style="margin-left: 8px">收起菜单</span>
        </a-button>
      </div>
    </a-layout-sider>

    <a-layout>
      <!-- 顶栏 -->
      <a-layout-header class="admin-header">
        <div class="header-left">
          <a-breadcrumb class="header-crumb">
            <a-breadcrumb-item>
              <HomeOutlined />
            </a-breadcrumb-item>
            <a-breadcrumb-item>{{ crumbTitle }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-tooltip title="刷新">
            <a-button type="text" shape="circle" @click="reload">
              <ReloadOutlined />
            </a-button>
          </a-tooltip>
          <a-tooltip title="通知">
            <a-badge :count="0" :offset="[-2, 2]">
              <a-button type="text" shape="circle">
                <BellOutlined />
              </a-button>
            </a-badge>
          </a-tooltip>
          <a-dropdown trigger="click">
            <div class="user-chip">
              <a-avatar :size="32" class="user-avatar">
                {{ avatarText }}
              </a-avatar>
              <span class="user-name">管理员</span>
              <DownOutlined />
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <a-layout-content class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  TeamOutlined,
  FireOutlined,
  MailOutlined,
  ReloadOutlined,
  BellOutlined,
  DownOutlined,
  HomeOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  ThunderboltFilled
} from '@ant-design/icons-vue'
import { useLoginStore } from '@/stores/useLoginStore'
import { showSuccess } from '@/utils/message'
import BrandLogo from '@/components/BrandLogo.vue'

const route = useRoute()
const router = useRouter()
const loginStore = useLoginStore()

const collapsed = ref(false)
const siderWidth = computed(() => (collapsed.value ? 72 : 232))
const activeKey = computed(() => route.path)
const crumbTitle = computed(() => route.meta?.title || '首页')
const avatarText = 'A'

function onMenuClick({ key }) {
  if (key.startsWith('/placeholder')) return
  router.push(key)
}
function reload() {
  router.replace({ path: '/redirect' + route.fullPath }).catch(() => {
    window.location.reload()
  })
}
function handleLogout() {
  loginStore.logout()
  showSuccess('已退出登录')
  router.replace('/login')
}
</script>

<style lang="scss" scoped>
.admin-layout { min-height: 100vh; }

/* 侧边栏 */
.admin-sider {
  position: relative;
  border-right: 1px solid var(--border-color);
  box-shadow: var(--shadow-xs);
  background: #ffffff;
  display: flex;
  flex-direction: column;
}
.sider-header {
  display: flex;
  align-items: center;
  padding: 20px 16px;
  border-bottom: 1px solid var(--border-color);
  min-height: 72px;
}
.sider-logo-collapsed {
  width: 40px;
  height: 40px;
  margin: 0 auto;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.admin-menu {
  border-inline-end: 0 !important;
  padding: 12px 8px;
  flex: 1;
  :deep(.ant-menu-item) {
    height: 44px;
    line-height: 44px;
    margin: 4px 0;
    border-radius: var(--radius-md);
    font-weight: 500;
    &.ant-menu-item-selected {
      background: var(--color-primary-light) !important;
      color: var(--color-primary) !important;
      &::after { display: none; }
    }
    &:hover { background: var(--color-surface-hover) !important; }
  }
}
.menu-soon {
  margin-left: auto;
  padding: 2px 8px;
  font-size: 11px;
  background: var(--color-warning-light);
  color: var(--color-warning);
  border-radius: 10px;
}
.sider-footer {
  padding: 12px;
  border-top: 1px solid var(--border-color);
}
.collapse-btn {
  color: var(--text-secondary);
  &:hover { color: var(--color-primary); background: var(--color-primary-light); }
}

/* 顶栏 */
.admin-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color);
  height: 64px;
}
.header-crumb :deep(.ant-breadcrumb-link),
.header-crumb :deep(.ant-breadcrumb-separator) {
  color: var(--text-secondary);
  font-weight: 500;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
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
.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

/* 内容区 */
.admin-content {
  margin: 16px;
  padding: 0;
  min-height: calc(100vh - 96px);
}

/* 路由切换过渡 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.22s ease-out;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>