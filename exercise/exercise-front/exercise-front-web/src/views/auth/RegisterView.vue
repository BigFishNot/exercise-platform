<template>
  <div class="register-page">
    <aside class="reg-brand">
      <div class="brand-bg">
        <span class="bg-blob bg-blob--1" />
        <span class="bg-blob bg-blob--2" />
      </div>
      <div class="brand-inner">
        <BrandLogo inverse tagline="加入我们 · JOIN US" />
        <div class="brand-hero">
          <h1>从今天开始<br />记录你的运动</h1>
          <p>只需 30 秒完成注册，开启你的第一个阶段计划。</p>

          <ul class="brand-steps">
            <li class="is-active"><span class="step-no">1</span>填写资料</li>
            <li><span class="step-no">2</span>制定计划</li>
            <li><span class="step-no">3</span>开始打卡</li>
          </ul>
        </div>
      </div>
    </aside>

    <main class="reg-form-wrap">
      <div class="form-card">
        <div class="form-header">
          <h2>创建账号</h2>
          <p class="text-tertiary">已有账号？<router-link to="/login" class="text-primary-color">立即登录</router-link></p>
        </div>

        <a-form :model="form" :rules="rules" ref="formRef" layout="vertical" @finish="onSubmit">
          <a-row :gutter="12">
            <a-col :span="12">
              <a-form-item label="账号" name="account">
                <a-input v-model:value="form.account" size="large" placeholder="手机号 / 邮箱" allow-clear>
                  <template #prefix><UserOutlined /></template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="邮箱（可选）" name="email">
                <a-input v-model:value="form.email" size="large" placeholder="可选" allow-clear>
                  <template #prefix><MailOutlined /></template>
                </a-input>
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="昵称" name="nickName">
            <a-input v-model:value="form.nickName" size="large" placeholder="2-20 字符" allow-clear>
              <template #prefix><SmileOutlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item label="密码" name="password">
            <a-input-password v-model:value="form.password" size="large" placeholder="8-32 位，需含字母与数字">
              <template #prefix><LockOutlined /></template>
            </a-input-password>
            <div class="pwd-strength">
              <span class="pwd-strength-bar" :class="strengthClass" />
              <span class="pwd-strength-text">{{ strengthText }}</span>
            </div>
          </a-form-item>

          <a-row :gutter="12">
            <a-col :span="12">
              <a-form-item label="性别" name="gender">
                <a-radio-group v-model:value="form.gender" size="large">
                  <a-radio-button :value="1">男</a-radio-button>
                  <a-radio-button :value="2">女</a-radio-button>
                  <a-radio-button :value="0">其他</a-radio-button>
                </a-radio-group>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="出生年月" name="birthDate">
                <a-date-picker
                  v-model:value="form.birthDate"
                  picker="month"
                  format="YYYY-MM"
                  value-format="YYYY-MM-DD"
                  placeholder="请选择"
                  size="large"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item>
            <a-checkbox v-model:checked="agreed">我已阅读并同意 <a class="text-primary-color">《服务条款》</a></a-checkbox>
          </a-form-item>

          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
            :disabled="!agreed"
          >
            注册并登录
          </a-button>
        </a-form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import {
  UserOutlined,
  LockOutlined,
  MailOutlined,
  SmileOutlined
} from '@ant-design/icons-vue'
import { useLoginStore } from '@/stores/useLoginStore'
import { userInfoApi } from '@/api/userInfo'
import { showSuccess } from '@/utils/message'
import BrandLogo from '@/components/BrandLogo.vue'

const router = useRouter()
const loginStore = useLoginStore()

const formRef = ref()
const loading = ref(false)
const agreed = ref(false)
const form = reactive({
  account: '',
  email: '',
  nickName: '',
  password: '',
  gender: 1,
  birthDate: dayjs().subtract(20, 'year').format('YYYY-MM-DD')
})

const rules = {
  account: [{ required: true, message: '账号不能为空' }],
  nickName: [
    { required: true, message: '请输入昵称' },
    { min: 2, max: 20, message: '昵称长度 2-20 字符' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 8, max: 32, message: '密码长度 8-32 位' },
    {
      validator: (_, v) => {
        if (!v) return Promise.resolve()
        return /[A-Za-z]/.test(v) && /\d/.test(v)
          ? Promise.resolve()
          : Promise.reject(new Error('密码必须同时包含字母和数字'))
      }
    }
  ],
  gender: [{ required: true, message: '请选择性别' }],
  birthDate: [{ required: true, message: '请选择出生年月' }]
}

const strength = computed(() => {
  const v = form.password || ''
  if (!v) return 0
  let s = 0
  if (v.length >= 8) s++
  if (/[A-Za-z]/.test(v) && /\d/.test(v)) s++
  if (/[^A-Za-z0-9]/.test(v)) s++
  return s
})
const strengthClass = computed(() => `is-${strength.value}`)
const strengthText = computed(() => ['', '弱', '中', '强'][strength.value] || '')

async function onSubmit() {
  loading.value = true
  try {
    const data = await userInfoApi.register({ ...form })
    if (data?.token) {
      loginStore.setLoginToken(data.token, data.userInfo)
      showSuccess('注册成功，欢迎加入！')
      router.replace('/')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 100vh;
  background: var(--color-bg);
}

/* 品牌区 */
.reg-brand {
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
  opacity: 0.5;
}
.bg-blob--1 { width: 360px; height: 360px; background: #10b981; top: -100px; left: -100px; }
.bg-blob--2 { width: 300px; height: 300px; background: #34d399; bottom: -60px; right: -60px; }

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
  max-width: 480px;
  h1 {
    font-size: 42px;
    font-weight: 700;
    line-height: 1.2;
    margin: 24px 0 16px;
    letter-spacing: 1px;
  }
  p { font-size: 15px; line-height: 1.7; color: rgba(255, 255, 255, 0.78); }
}
.brand-steps {
  list-style: none;
  margin: 40px 0 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  li {
    display: flex;
    align-items: center;
    gap: 12px;
    color: rgba(255, 255, 255, 0.55);
    font-size: 15px;
    .step-no {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255, 255, 255, 0.10);
      font-weight: 600;
    }
    &.is-active {
      color: #fff;
      .step-no {
        background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
      }
    }
  }
}

/* 表单 */
.reg-form-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  overflow-y: auto;
}
.form-card {
  width: 100%;
  max-width: 540px;
  background: var(--color-surface);
  padding: 40px;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-color);
}
.form-header {
  margin-bottom: 24px;
  h2 { font-size: 26px; font-weight: 700; margin: 0 0 6px; }
  p { margin: 0; font-size: 14px; }
  a { font-weight: 600; }
}

.pwd-strength {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}
.pwd-strength-bar {
  flex: 1;
  height: 4px;
  background: var(--border-color);
  border-radius: 2px;
  position: relative;
  overflow: hidden;
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    width: 0%;
    transition: width 0.2s, background 0.2s;
  }
  &.is-1::after { width: 33%; background: var(--color-danger); }
  &.is-2::after { width: 66%; background: var(--color-warning); }
  &.is-3::after { width: 100%; background: var(--color-success); }
}
.pwd-strength-text {
  font-size: 12px;
  color: var(--text-tertiary);
  width: 32px;
  text-align: right;
}

@media (max-width: 960px) {
  .register-page { grid-template-columns: 1fr; }
  .reg-brand { display: none; }
}
</style>