<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <aside class="login-brand">
      <div class="brand-bg">
        <span class="bg-blob bg-blob--1" />
        <span class="bg-blob bg-blob--2" />
      </div>

      <div class="brand-inner">
        <BrandLogo inverse tagline="用户端 · USER CONSOLE" />

        <div class="brand-hero">
          <h1>让运动<br />成为习惯</h1>
          <p>定制你的阶段计划，每日打卡，让每一次坚持都被记录。</p>

          <div class="brand-feature-grid">
            <div class="brand-feature">
              <FireFilled class="bf-icon" />
              <div class="bf-title">每日打卡</div>
              <div class="bf-desc">累计有效时长，自动判定打卡状态</div>
            </div>
            <div class="brand-feature">
              <LineChartOutlined class="bf-icon" />
              <div class="bf-title">趋势可视化</div>
              <div class="bf-desc">体重 / BMI / 时长一目了然</div>
            </div>
            <div class="brand-feature">
              <BulbFilled class="bf-icon" />
              <div class="bf-title">AI 鼓励</div>
              <div class="bf-desc">每日 3 次，陪你继续前行</div>
            </div>
            <div class="brand-feature">
              <MailOutlined class="bf-icon" />
              <div class="bf-title">温柔提醒</div>
              <div class="bf-desc">未打卡 / 时长不足，邮件触达</div>
            </div>
          </div>
        </div>
      </div>
    </aside>

    <!-- 右侧表单区 -->
    <main class="login-form-wrap">
      <div class="form-card">
        <div class="form-header">
          <h2>欢迎回来 👋</h2>
          <p class="text-tertiary">登录后开启今日运动</p>
        </div>

        <a-form
          :model="form"
          :rules="rules"
          ref="formRef"
          layout="vertical"
          @finish="onSubmit"
        >
          <a-form-item label="账号" name="account">
            <a-input
              v-model:value="form.account"
              size="large"
              placeholder="手机号 / 邮箱"
              allow-clear
            >
              <template #prefix><UserOutlined /></template>
            </a-input>
          </a-form-item>
          <a-form-item label="密码" name="password">
            <a-input-password v-model:value="form.password" size="large" placeholder="请输入密码">
              <template #prefix><LockOutlined /></template>
            </a-input-password>
          </a-form-item>

          <div class="form-extra">
            <a-checkbox v-model:checked="remember">记住我</a-checkbox>
            <a class="text-primary-color">忘记密码？</a>
          </div>

          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            登录
          </a-button>
        </a-form>

        <div class="form-divider"><span>没有账号？</span><router-link to="/register">立即注册</router-link></div>
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
  FireFilled,
  LineChartOutlined,
  BulbFilled,
  MailOutlined
} from '@ant-design/icons-vue'
import { useLoginStore } from '@/stores/useLoginStore'
import { userInfoApi } from '@/api/userInfo'
import { showSuccess } from '@/utils/message'
import BrandLogo from '@/components/BrandLogo.vue'

const router = useRouter()
const route = useRoute()
const loginStore = useLoginStore()

const formRef = ref()
const loading = ref(false)
const remember = ref(true)
const form = reactive({ account: '', password: '' })
const rules = {
  account: [{ required: true, message: '请输入账号' }],
  password: [{ required: true, message: '请输入密码' }]
}

async function onSubmit() {
  loading.value = true
  try {
    const data = await userInfoApi.login({ account: form.account, password: form.password })
    if (data?.token) {
      loginStore.setLoginToken(data.token, data.userInfo)
      showSuccess('登录成功，开始运动吧～')
      const redirect = route.query.redirect || '/'
      router.replace(redirect)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 100vh;
  background: var(--color-bg);
}

/* 品牌区 */
.login-brand {
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(900px 600px at -10% -20%, rgba(16, 185, 129, 0.40), transparent 60%),
    radial-gradient(800px 600px at 110% 120%, rgba(14, 165, 233, 0.40), transparent 55%),
    linear-gradient(160deg, #064e3b 0%, #0f172a 100%);
  color: #fff;
}
.brand-bg { position: absolute; inset: 0; pointer-events: none; }
.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.55;
}
.bg-blob--1 { width: 380px; height: 380px; background: #10b981; top: -100px; left: -80px; }
.bg-blob--2 { width: 320px; height: 320px; background: #0ea5e9; bottom: -80px; right: -60px; }

.brand-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 56px 64px;
}
.brand-hero {
  margin-top: 80px;
  max-width: 520px;
  h1 {
    font-size: 48px;
    font-weight: 700;
    line-height: 1.15;
    margin: 32px 0 16px;
    letter-spacing: 1px;
  }
  p {
    font-size: 15px;
    line-height: 1.7;
    color: rgba(255, 255, 255, 0.78);
  }
}
.brand-feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 36px;
  max-width: 460px;
}
.brand-feature {
  padding: 16px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: var(--radius-md);
  backdrop-filter: blur(8px);
}
.bf-icon {
  font-size: 22px;
  color: #34d399;
}
.bf-title { font-weight: 600; font-size: 14px; margin-top: 8px; }
.bf-desc { font-size: 12px; color: rgba(255, 255, 255, 0.7); margin-top: 4px; line-height: 1.5; }

/* 表单 */
.login-form-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}
.form-card {
  width: 100%;
  max-width: 420px;
  background: var(--color-surface);
  padding: 40px;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-color);
}
.form-header {
  margin-bottom: 28px;
  h2 { font-size: 26px; font-weight: 700; margin: 0 0 6px; }
  p { margin: 0; font-size: 14px; }
}
.form-extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -8px 0 24px;
  font-size: 13px;
}
.form-divider {
  margin-top: 24px;
  text-align: center;
  font-size: 13px;
  color: var(--text-tertiary);
  a {
    color: var(--color-primary);
    margin-left: 6px;
    font-weight: 600;
  }
}

@media (max-width: 960px) {
  .login-page { grid-template-columns: 1fr; }
  .login-brand { display: none; }
}
</style>