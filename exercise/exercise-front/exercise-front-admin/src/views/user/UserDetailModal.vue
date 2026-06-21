<template>
  <a-modal
    v-model:open="visible"
    :footer="null"
    width="640px"
    :closable="false"
    :mask-closable="true"
    destroy-on-close
    class="detail-modal"
  >
    <!-- 头部 -->
    <div v-if="data" class="detail-header">
      <a-avatar :size="64" class="detail-avatar">
        {{ (data.nickName || data.account || '?').slice(0, 1).toUpperCase() }}
      </a-avatar>
      <div class="detail-title">
        <div class="detail-name">{{ data.nickName || '—' }}</div>
        <div class="detail-account">{{ data.account }}</div>
        <div class="detail-tags">
          <a-tag :color="data.roleType === 1 ? 'blue' : 'default'" class="role-pill">
            <CrownFilled v-if="data.roleType === 1" />
            <UserOutlined v-else />
            {{ data.roleTypeName }}
          </a-tag>
          <a-tag :color="data.status === 1 ? 'success' : 'error'" class="status-pill">
            <span class="status-dot" :class="data.status === 1 ? 'is-on' : 'is-off'" />
            {{ data.statusName }}
          </a-tag>
        </div>
      </div>
      <a-button shape="circle" type="text" class="detail-close" @click="visible = false">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- 身体数据 KPI -->
    <div v-if="data" class="detail-kpi-row">
      <div class="detail-kpi">
        <div class="detail-kpi-label">身高</div>
        <div class="detail-kpi-value">{{ data.height || '—' }}<span>cm</span></div>
      </div>
      <div class="detail-kpi">
        <div class="detail-kpi-label">体重</div>
        <div class="detail-kpi-value">{{ data.weight || '—' }}<span>kg</span></div>
      </div>
      <div class="detail-kpi detail-kpi--accent">
        <div class="detail-kpi-label">BMI</div>
        <div class="detail-kpi-value">{{ data.bmi || '—' }}</div>
      </div>
    </div>

    <!-- 信息分组 -->
    <a-tabs v-if="data" class="detail-tabs">
      <a-tab-pane key="base" tab="基础信息">
        <a-descriptions :column="2" bordered size="small" class="detail-descs">
          <a-descriptions-item label="账号">{{ data.account }}</a-descriptions-item>
          <a-descriptions-item label="昵称">{{ data.nickName || '—' }}</a-descriptions-item>
          <a-descriptions-item label="邮箱" :span="2">{{ data.email || '—' }}</a-descriptions-item>
          <a-descriptions-item label="性别">{{ data.genderName }}</a-descriptions-item>
          <a-descriptions-item label="出生年月">
            {{ data.birthDate ? dayjs(data.birthDate).format('YYYY-MM') : '—' }}
          </a-descriptions-item>
        </a-descriptions>
      </a-tab-pane>

      <a-tab-pane key="log" tab="登录与时间">
        <a-descriptions :column="1" bordered size="small" class="detail-descs">
          <a-descriptions-item label="注册时间">{{ data.registerTime || '—' }}</a-descriptions-item>
          <a-descriptions-item label="最近登录">{{ data.lastLoginTime || '—' }}</a-descriptions-item>
        </a-descriptions>
      </a-tab-pane>
    </a-tabs>

    <!-- 底部操作 -->
    <div v-if="data" class="detail-footer">
      <a-space>
        <a-button @click="visible = false">关闭</a-button>
        <a-button
          :type="data.status === 1 ? 'default' : 'primary'"
          @click="handleToggle"
          :loading="toggling"
        >
          {{ data.status === 1 ? '停用该用户' : '启用该用户' }}
        </a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup>
import { ref } from 'vue'
import dayjs from 'dayjs'
import {
  CrownFilled,
  UserOutlined,
  CloseOutlined
} from '@ant-design/icons-vue'
import { userInfoApi } from '@/api/userInfo'
import { showError, showSuccess, $confirm } from '@/utils/message'

const visible = ref(false)
const data = ref(null)
const toggling = ref(false)
const emit = defineEmits(['changed'])

async function open(userId) {
  visible.value = true
  data.value = null
  try {
    data.value = await userInfoApi.detail(userId)
  } catch (e) {
    showError(e.message || '加载详情失败')
    visible.value = false
  }
}

async function handleToggle() {
  if (!data.value) return
  const next = data.value.status === 1 ? 0 : 1
  const actionText = next === 1 ? '启用' : '停用'
  toggling.value = true
  $confirm({
    title: `确认${actionText}该用户?`,
    content: `账号：${data.value.account}`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await userInfoApi.updateStatus(data.value.userId, next)
        showSuccess(`${actionText}成功`)
        emit('changed')
        visible.value = false
      } catch (e) {
        showError(e.message || `${actionText}失败`)
      } finally {
        toggling.value = false
      }
    },
    onCancel: () => {
      toggling.value = false
    }
  })
}

defineExpose({ open })
</script>

<style lang="scss" scoped>
.detail-modal :deep(.ant-modal-body) {
  padding: 0;
}

/* 头部 */
.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px 24px 20px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.08) 0%, rgba(14, 165, 233, 0.06) 100%);
  position: relative;
}
.detail-avatar {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%) !important;
  color: #fff !important;
  font-weight: 700;
  font-size: 24px;
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.3);
}
.detail-title { flex: 1; min-width: 0; }
.detail-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
}
.detail-account {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-top: 2px;
}
.detail-tags {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
.role-pill, .status-pill {
  border-radius: 999px !important;
  padding-inline: 10px !important;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: inline-block;
  &.is-on { background: var(--color-success); box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.18); }
  &.is-off { background: var(--color-danger); box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.18); }
}
.detail-close {
  position: absolute;
  top: 16px;
  right: 16px;
}

/* KPI */
.detail-kpi-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 0 24px;
  margin-top: -4px;
}
.detail-kpi {
  padding: 16px;
  background: var(--color-bg-soft);
  border-radius: var(--radius-md);
  text-align: center;
  border: 1px solid var(--border-color);
}
.detail-kpi--accent {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-accent-light) 100%);
  border-color: transparent;
}
.detail-kpi-label {
  font-size: 12px;
  color: var(--text-tertiary);
}
.detail-kpi-value {
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

/* Tabs */
.detail-tabs {
  padding: 16px 24px 0;
  :deep(.ant-tabs-nav) { margin-bottom: 12px; }
}
.detail-descs :deep(.ant-descriptions-item-label) {
  color: var(--text-secondary);
  width: 110px;
}

/* 底部 */
.detail-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--color-bg-soft);
  margin-top: 8px;
}
</style>