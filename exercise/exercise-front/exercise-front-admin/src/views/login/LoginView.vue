<template>
  <div class="login-page">
    <!-- 左侧：品牌展示区 -->
    <aside class="login-brand">
      <div class="brand-bg">
        <span class="bg-blob bg-blob--1" />
        <span class="bg-blob bg-blob--2" />
        <span class="bg-blob bg-blob--3" />
      </div>

      <div class="brand-inner">
        <BrandLogo inverse tagline="管理后台 · ADMIN CONSOLE" />

        <div class="brand-hero">
          <h1>高效管理<br />每一次运动</h1>
          <p>从用户、阶段计划到邮件触达，统一在一个工作台里完成。</p>

          <ul class="brand-features">
            <li><CheckCircleFilled />用户与状态一目了然</li>
            <li><CheckCircleFilled />阶段计划 / 打卡实时可视化</li>
            <li><CheckCircleFilled />邮件模板与定时任务一键配置</li>
          </ul>
        </div>

        <footer class="brand-footer">© {{ year }} 运动平台 · 管理后台</footer>
      </div>
    </aside>

    <!-- 右侧：表单区 -->
    <main class="login-form-wrap">
      <div class="login-form-inner">
        <div class="form-header">
          <h2>欢迎回来 👋</h2>
          <p class="text-tertiary">请使用管理员账号登录后台</p>
        </div>

        <a-form
          :model="form"
          :rules="rules"
          ref="formRef"
          layout="vertical"
          class="login-form"
          @finish="onSubmit"
        >
          <a-form-item label="账号" name="account">
            <a-input
              v-model:value="form.account"
              size="large"
              placeholder="请输入管理员账号"
              allow-clear
            >
              <template #prefix><UserOutlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item label="密码" name="password">
            <a-input-password
              v-model:value="form.password"
              size="large"
              placeholder="请输入密码"
            >
              <template #prefix><LockOutlined /></template>
            </a-input-password>
          </a-form-item>

          <div class="form-extra">
            <a-checkbox v-model:checked="remember">记住我</a-checkbox>
            <a class="text-primary-color">忘记密码？</a>
          </div>

          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
          >
            登录
          </a-button>
        </a-form>

        <a-alert
          class="login-hint"
          type="info"
          show-icon
          message="默认账号 admin / 密码 admin123，首次登录后请尽快修改。"
        />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  UserOutlined,
  LockOutlined,
  CheckCircleFilled
} from '@ant-design/icons-vue'
import { useLoginStore } from '@/stores/useLoginStore'
import { authApi } from '@/api/auth'
import { showSuccess, showError } from '@/utils/message'
import BrandLogo from '@/components/BrandLogo.vue'

const router = useRouter()
const route = useRoute()
const loginStore = useLoginStore()

const formRef = ref()
const loading = ref(false)
const remember = ref(true)
const form = reactive({ account: 'admin', password: 'admin123' })

const rules = {
  account: [{ required: true, message: '请输入账号' }],
  password: [{ required: true, message: '请输入密码' }]
}

const year = new Date().getFullYear()

async function onSubmit() {
  loading.value = true
  try {
    const data = await authApi.login(form.account, form.password)
    if (data?.token) {
      loginStore.setLoginToken(data.token)
      showSuccess('登录成功')
      const redirect = route.query.redirect || '/'
      router.replace(redirect)
    }
  } catch (e) {
    showError(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
  background: var(--color-bg);
}

/* 品牌区 */
.login-brand {
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(1200px 600px at -10% -20%, rgba(16, 185, 129, 0.45), transparent 60%),
    radial-gradient(900px 600px at 110% 120%, rgba(14, 165, 233, 0.45), transparent 55%),
    linear-gradient(135deg, #064e3b 0%, #0f172a 100%);
  color: #fff;
}
.brand-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
}
.bg-blob--1 { width: 380px; height: 380px; background: #10b981; top: -100px; left: -100px; }
.bg-blob--2 { width: 320px; height: 320px; background: #0ea5e9; bottom: -80px; right: -80px; }
.bg-blob--3 { width: 220px; height: 220px; background: #34d399; top: 40%; left: 60%; opacity: 0.3; }

.brand-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 56px 64px;
}
.brand-hero {
  margin-top: auto;
  max-width: 520px;
  h1 {
    font-size: 44px;
    font-weight: 700;
    line-height: 1.2;
    margin: 32px 0 16px;
    letter-spacing: 1px;
  }
  p {
    font-size: 15px;
    line-height: 1.7;
    color: rgba(255, 255, 255, 0.78);
    margin: 0;
  }
}
.brand-features {
  list-style: none;
  margin: 32px 0 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  li {
    display: flex;
    align-items: center;
    gap: 10px;
    color: rgba(255, 255, 255, 0.92);
    font-size: 14px;
    .anticon { color: #34d399; font-size: 18px; }
  }
}
.brand-footer {
  margin-top: auto;
  padding-top: 32px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

/* 表单区 */
.login-form-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: var(--color-bg);
}
.login-form-inner {
  width: 100%;
  max-width: 420px;
}
.form-header {
  margin-bottom: 32px;
  h2 {
    font-size: 28px;
    font-weight: 700;
    margin: 0 0 8px;
    color: var(--text-primary);
  }
  p { margin: 0; font-size: 14px; }
}
.login-form {
  :deep(.ant-form-item-label > label) {
    font-weight: 500;
    color: var(--text-secondary);
  }
}
.form-extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -8px 0 24px;
  font-size: 13px;
}
.login-hint {
  margin-top: 24px;
  border-radius: var(--radius-md);
}

@media (max-width: 960px) {
  .login-page { grid-template-columns: 1fr; }
  .login-brand { display: none; }
}
</style>