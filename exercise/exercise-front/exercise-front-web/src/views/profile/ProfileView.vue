<template>
  <div class="profile-page">
    <!-- 顶部用户卡 -->
    <a-card class="profile-hero" :bordered="false">
      <div class="hero-inner" v-if="profile">
        <a-avatar :size="76" class="hero-avatar">
          {{ (profile.nickName || 'U').slice(0, 1).toUpperCase() }}
        </a-avatar>
        <div class="hero-info">
          <div class="hero-name">
            {{ profile.nickName || '运动达人' }}
            <a-tag color="success" class="hero-tag">已认证</a-tag>
          </div>
          <div class="hero-account">{{ profile.account }}</div>
          <div class="hero-meta">
            <span><MailOutlined /> {{ profile.email || '未设置邮箱' }}</span>
            <span><CalendarOutlined /> {{ profile.lastLoginTime || '首次登录' }}</span>
          </div>
        </div>
        <div class="hero-actions">
          <a-button type="default" @click="loadProfile">
            <ReloadOutlined /> 刷新
          </a-button>
        </div>
      </div>
    </a-card>

    <a-row :gutter="16" class="profile-body">
      <!-- 左：身体指标 KPI + 修改密码 -->
      <a-col :xs="24" :md="10">
        <a-card title="身体指标" :bordered="false" class="card-block">
          <template #extra>
            <span class="text-tertiary">自动计算</span>
          </template>
          <div class="kpi-grid">
            <div class="kpi-cell">
              <div class="kpi-label">身高</div>
              <div class="kpi-value">{{ profile?.height || '—' }}<span>cm</span></div>
            </div>
            <div class="kpi-cell">
              <div class="kpi-label">体重</div>
              <div class="kpi-value">{{ profile?.weight || '—' }}<span>kg</span></div>
            </div>
            <div class="kpi-cell kpi-cell--accent">
              <div class="kpi-label">BMI</div>
              <div class="kpi-value">{{ profile?.bmi || '—' }}</div>
              <div class="kpi-tag" :class="bmiTag.class">{{ bmiTag.text }}</div>
            </div>
          </div>
        </a-card>

        <a-card title="修改密码" :bordered="false" class="card-block">
          <a-form :model="pwdForm" :rules="pwdRules" ref="pwdRef" layout="vertical" @finish="onChangePwd">
            <a-form-item label="原密码" name="oldPassword">
              <a-input-password v-model:value="pwdForm.oldPassword" size="large" placeholder="请输入原密码">
                <template #prefix><LockOutlined /></template>
              </a-input-password>
            </a-form-item>
            <a-form-item label="新密码" name="newPassword">
              <a-input-password
                v-model:value="pwdForm.newPassword"
                size="large"
                placeholder="8-32 位，需含字母与数字"
              >
                <template #prefix><KeyOutlined /></template>
              </a-input-password>
            </a-form-item>
            <a-button type="primary" html-type="submit" block :loading="pwdLoading">
              修改密码
            </a-button>
          </a-form>
        </a-card>
      </a-col>

      <!-- 右：基本资料 -->
      <a-col :xs="24" :md="14">
        <a-card title="基本资料" :bordered="false" class="card-block">
          <template #extra>
            <a-space>
              <a-button @click="loadProfile">重置</a-button>
              <a-button type="primary" @click="onSave" :loading="saving">保存修改</a-button>
            </a-space>
          </template>

          <a-form
            v-if="profile"
            :model="form"
            :rules="rules"
            ref="formRef"
            layout="vertical"
          >
            <a-row :gutter="12">
              <a-col :span="12">
                <a-form-item label="账号">
                  <a-input :value="profile.account" disabled size="large" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱" name="email">
                  <a-input v-model:value="form.email" size="large" placeholder="可选" allow-clear>
                    <template #prefix><MailOutlined /></template>
                  </a-input>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="12">
              <a-col :span="12">
                <a-form-item label="昵称" name="nickName">
                  <a-input v-model:value="form.nickName" size="large" placeholder="2-20 字符" allow-clear>
                    <template #prefix><UserOutlined /></template>
                  </a-input>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="性别">
                  <a-radio-group v-model:value="form.gender" size="large">
                    <a-radio-button :value="1">男</a-radio-button>
                    <a-radio-button :value="2">女</a-radio-button>
                    <a-radio-button :value="0">其他</a-radio-button>
                  </a-radio-group>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="12">
              <a-col :span="8">
                <a-form-item label="出生年月">
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
              <a-col :span="8">
                <a-form-item label="身高 (cm)">
                  <a-input-number v-model:value="form.height" :min="0" :max="300" :precision="1" size="large" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="体重 (kg)">
                  <a-input-number v-model:value="form.weight" :min="0" :max="500" :precision="1" size="large" style="width: 100%" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-alert
              type="info"
              show-icon
              class="form-tip"
              message="BMI = 体重(kg) / 身高(m)²，后端在你保存身高体重后自动计算并落库。"
            />
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import {
  MailOutlined,
  CalendarOutlined,
  LockOutlined,
  KeyOutlined,
  ReloadOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import { userInfoApi } from '@/api/userInfo'
import { useLoginStore } from '@/stores/useLoginStore'
import { showSuccess, showError } from '@/utils/message'

const loginStore = useLoginStore()
const formRef = ref()
const pwdRef = ref()

const loading = ref(false)
const saving = ref(false)
const pwdLoading = ref(false)
const profile = ref(null)

const form = reactive({
  email: '', nickName: '', gender: 1, birthDate: null, height: null, weight: null
})
const pwdForm = reactive({ oldPassword: '', newPassword: '' })

const rules = {
  nickName: [
    { required: true, message: '请输入昵称' },
    { min: 2, max: 20, message: '昵称长度 2-20 字符' }
  ]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 8, max: 32, message: '密码长度 8-32 位' },
    {
      validator: (_, v) => {
        if (!v) return Promise.resolve()
        return /[A-Za-z]/.test(v) && /\d/.test(v)
          ? Promise.resolve()
          : Promise.reject(new Error('密码必须同时包含字母和数字'))
      }
    }
  ]
}

const bmiTag = computed(() => {
  const v = Number(profile.value?.bmi)
  if (!v) return { class: '', text: '—' }
  if (v < 18.5) return { class: 'is-warn', text: '偏瘦' }
  if (v < 24) return { class: 'is-good', text: '正常' }
  if (v < 28) return { class: 'is-warn', text: '超重' }
  return { class: 'is-bad', text: '肥胖' }
})

async function loadProfile() {
  loading.value = true
  try {
    const data = await userInfoApi.getProfile()
    profile.value = data
    form.email = data?.email || ''
    form.nickName = data?.nickName || ''
    form.gender = data?.gender ?? 1
    form.birthDate = data?.birthDate ? dayjs(data.birthDate).format('YYYY-MM-DD') : null
    form.height = data?.height || null
    form.weight = data?.weight || null
    loginStore.setUserInfo(data)
  } catch (e) {
    showError(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function onSave() {
  saving.value = true
  try {
    await userInfoApi.updateProfile({ ...form })
    showSuccess('保存成功')
    loadProfile()
  } finally {
    saving.value = false
  }
}

async function onChangePwd() {
  pwdLoading.value = true
  try {
    await userInfoApi.changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    showSuccess('密码修改成功，请重新登录')
    loginStore.logout()
    window.location.href = '/login'
  } finally {
    pwdLoading.value = false
  }
}

onMounted(loadProfile)
</script>

<style lang="scss" scoped>
.profile-page { display: flex; flex-direction: column; gap: 16px; }

/* Hero */
.profile-hero {
  background:
    radial-gradient(800px 300px at 0% 0%, rgba(16, 185, 129, 0.10), transparent 60%),
    radial-gradient(600px 300px at 100% 100%, rgba(14, 165, 233, 0.10), transparent 60%),
    linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
}
.hero-inner {
  display: flex;
  align-items: center;
  gap: 20px;
}
.hero-avatar {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%) !important;
  color: #fff !important;
  font-weight: 700;
  font-size: 28px;
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.3);
}
.hero-info { flex: 1; min-width: 0; }
.hero-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
}
.hero-tag { border-radius: 999px !important; padding-inline: 10px !important; }
.hero-account {
  margin-top: 4px;
  color: var(--text-tertiary);
  font-size: 13px;
}
.hero-meta {
  margin-top: 10px;
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: var(--text-secondary);
  flex-wrap: wrap;
  span { display: inline-flex; align-items: center; gap: 6px; }
}
.hero-actions { display: flex; gap: 8px; }

/* Body */
.profile-body { margin-top: 0 !important; }
.card-block + .card-block { margin-top: 16px; }

.card-block :deep(.ant-card-head) {
  border-bottom: 1px solid var(--border-color);
}
.card-block :deep(.ant-card-head-title) {
  font-weight: 600;
  font-size: 15px;
}

/* KPI 网格 */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.kpi-cell {
  padding: 16px;
  background: var(--color-bg-soft);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
  text-align: center;
  position: relative;
}
.kpi-cell--accent {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-accent-light) 100%);
  border-color: transparent;
}
.kpi-label { font-size: 12px; color: var(--text-tertiary); }
.kpi-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-top: 4px;
  span {
    font-size: 12px;
    font-weight: 400;
    color: var(--text-tertiary);
    margin-left: 4px;
  }
}
.kpi-tag {
  display: inline-block;
  margin-top: 6px;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  &.is-good { background: var(--color-success-light); color: var(--color-success); }
  &.is-warn { background: var(--color-warning-light); color: var(--color-warning); }
  &.is-bad  { background: var(--color-danger-light); color: var(--color-danger); }
}

.form-tip { margin-top: 16px; border-radius: var(--radius-md); }
</style>