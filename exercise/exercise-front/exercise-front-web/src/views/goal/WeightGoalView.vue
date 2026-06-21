<template>
  <div class="weight-goal-page">
    <!-- 当前生效中目标 -->
    <a-card v-if="active" class="active-card" :bordered="false">
      <div class="active-header">
        <div>
          <a-tag color="success" class="status-pill">
            <span class="status-dot is-on" />
            {{ active.statusName }}
          </a-tag>
          <h2 class="active-title">{{ formatWeight(active.targetWeight) }} kg</h2>
          <p class="active-sub">
            目标日期：<strong>{{ active.targetDate }}</strong>
            <span v-if="daysLeft != null" class="ml-12">
              剩余 <strong>{{ daysLeft }}</strong> 天
            </span>
          </p>
          <p v-if="active.targetBodyFat != null" class="active-sub">
            目标体脂率：<strong>{{ formatNumber(active.targetBodyFat) }}%</strong>
          </p>
          <p v-if="active.remark" class="active-remark">
            <FileTextOutlined /> {{ active.remark }}
          </p>
        </div>
        <div class="active-progress">
          <a-progress
            type="circle"
            :percent="progressPct"
            :stroke-color="progressColor"
            :size="120"
          />
          <div class="progress-text">完成度</div>
        </div>
      </div>

      <a-divider style="margin: 16px 0" />

      <a-space wrap>
        <a-button type="primary" @click="openUpsert">
          <EditOutlined /> 修改目标
        </a-button>
        <a-popconfirm
          title="确认归档当前目标？归档后不可再激活"
          ok-text="确认归档"
          cancel-text="再想想"
          ok-button-props="{ danger: true }"
          @confirm="onArchive"
        >
          <a-button danger>
            <InboxOutlined /> 归档
          </a-button>
        </a-popconfirm>
      </a-space>
    </a-card>

    <!-- 空态：立即创建 -->
    <a-card v-else class="empty-card" :bordered="false">
      <a-empty description="尚未设定减肥目标">
        <a-button type="primary" size="large" @click="openUpsert">
          <PlusOutlined /> 立即创建
        </a-button>
      </a-empty>
    </a-card>

    <!-- 历史目标 -->
    <a-card v-if="history.length" class="history-card" :bordered="false" title="历史目标">
      <a-list :data-source="history" item-layout="horizontal">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #title>
                <span class="hist-title">
                  {{ formatWeight(item.targetWeight) }} kg
                  <span class="hist-sub">→ {{ item.targetDate }}</span>
                </span>
              </template>
              <template #description>
                <a-tag :color="item.status === 1 ? 'success' : 'default'" class="ml-8">
                  {{ item.statusName }}
                </a-tag>
                <span v-if="item.targetBodyFat != null" class="text-tertiary">
                  体脂率 {{ formatNumber(item.targetBodyFat) }}%
                </span>
                <span v-if="item.remark" class="text-tertiary ml-12">{{ item.remark }}</span>
                <span v-if="item.archiveTime" class="text-tertiary ml-12">
                  归档于 {{ item.archiveTime }}
                </span>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-card>

    <!-- 新增/修改弹窗 -->
    <a-modal
      v-model:open="upsertOpen"
      :title="active ? '修改目标' : '创建减肥目标'"
      ok-text="保存"
      cancel-text="取消"
      :confirm-loading="saving"
      @ok="onUpsert"
    >
      <a-form layout="vertical">
        <a-form-item label="目标体重 (kg)" :required="true">
          <a-input-number
            v-model:value="form.targetWeight"
            :min="0"
            :max="500"
            :precision="2"
            :step="0.1"
            style="width: 100%"
            placeholder="如：60.00"
          />
        </a-form-item>
        <a-form-item label="目标体脂率 (%)（可选）">
          <a-input-number
            v-model:value="form.targetBodyFat"
            :min="0"
            :max="100"
            :precision="2"
            :step="0.1"
            style="width: 100%"
            placeholder="如：22.0"
          />
        </a-form-item>
        <a-form-item label="目标日期" :required="true">
          <a-date-picker
            v-model:value="form.targetDate"
            :disabled-date="disableDate"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            placeholder="选择目标日期（今天或之后）"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="备注（可选）">
          <a-textarea
            v-model:value="form.remark"
            :rows="2"
            maxlength="200"
            show-count
            placeholder="例如：通过跳绳 + 控饮食 3 个月减到 65kg"
          />
        </a-form-item>
        <a-alert
          v-if="active"
          type="warning"
          show-icon
          message="修改后无法撤销，请确认"
        />
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import {
  EditOutlined,
  InboxOutlined,
  PlusOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue'
import { weightGoalApi } from '@/api/weightGoal'
import { useLoginStore } from '@/stores/useLoginStore'
import { showSuccess, showError, $confirm } from '@/utils/message'

const loginStore = useLoginStore()

const active = ref(null)
const history = ref([])
const upsertOpen = ref(false)
const saving = ref(false)
const form = reactive({
  targetWeight: null,
  targetBodyFat: null,
  targetDate: dayjs().add(90, 'day').format('YYYY-MM-DD'),
  remark: ''
})

const daysLeft = computed(() => {
  if (!active.value?.targetDate) return null
  const end = dayjs(active.value.targetDate)
  const today = dayjs().startOf('day')
  return Math.max(0, end.diff(today, 'day'))
})

const progressPct = computed(() => {
  if (!active.value) return 0
  // 简化：剩余天数 / 总跨度 = 已完成进度
  // 这里用日期范围内已过去的天数
  const start = dayjs(active.value.createTime)
  const end = dayjs(active.value.targetDate)
  const today = dayjs().startOf('day')
  const total = end.diff(start, 'day')
  if (total <= 0) return 100
  const elapsed = Math.max(0, today.diff(start, 'day'))
  return Math.min(100, Math.round((elapsed / total) * 100))
})

const progressColor = computed(() => {
  const p = progressPct.value
  if (p >= 80) return { from: '#10b981', to: '#0ea5e9' }
  if (p >= 40) return { from: '#0ea5e9', to: '#6366f1' }
  return { from: '#f59e0b', to: '#10b981' }
})

function formatWeight(v) {
  if (v == null) return '—'
  return Number(v).toFixed(2)
}
function formatNumber(v) {
  if (v == null) return '—'
  return Number(v).toFixed(2)
}
function disableDate(d) {
  return d && d.isBefore(dayjs().startOf('day'))
}

async function loadData() {
  try {
    const [curr, all] = await Promise.all([
      weightGoalApi.getActive(),
      weightGoalApi.list()
    ])
    active.value = curr || null
    history.value = (all || []).filter((g) => g.status !== 1)
  } catch (e) {
    showError(e.message || '加载失败')
  }
}

function openUpsert() {
  if (active.value) {
    form.targetWeight = Number(active.value.targetWeight) || null
    form.targetBodyFat = active.value.targetBodyFat != null ? Number(active.value.targetBodyFat) : null
    form.targetDate = active.value.targetDate
    form.remark = active.value.remark || ''
  } else {
    form.targetWeight = null
    form.targetBodyFat = null
    form.targetDate = dayjs().add(90, 'day').format('YYYY-MM-DD')
    form.remark = ''
  }
  upsertOpen.value = true
}

async function onUpsert() {
  if (form.targetWeight == null) {
    showError('请输入目标体重')
    return
  }
  if (!form.targetDate) {
    showError('请选择目标日期')
    return
  }
  saving.value = true
  try {
    const payload = {
      targetWeight: form.targetWeight,
      targetBodyFat: form.targetBodyFat,
      targetDate: form.targetDate,
      remark: form.remark
    }
    if (active.value) {
      await weightGoalApi.update(payload)
      showSuccess('已更新')
    } else {
      await weightGoalApi.add(payload)
      showSuccess('已创建')
    }
    upsertOpen.value = false
    loadData()
  } catch (e) {
    showError(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function onArchive() {
  if (!active.value) return
  $confirm({
    title: '确认归档当前目标？',
    content: '归档后不可再激活',
    okText: '确认归档',
    cancelText: '再想想',
    okButtonProps: { danger: true },
    onOk: async () => {
      try {
        await weightGoalApi.archive(active.value.goalId)
        showSuccess('已归档')
        loadData()
      } catch (e) {
        showError(e.message || '归档失败')
      }
    }
  })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.weight-goal-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 960px;
  margin: 0 auto;
}

.active-card {
  background:
    radial-gradient(800px 300px at 0% 0%, rgba(16, 185, 129, 0.10), transparent 60%),
    radial-gradient(600px 300px at 100% 100%, rgba(14, 165, 233, 0.10), transparent 60%),
    linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
}
.active-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}
.status-pill {
  border-radius: 999px !important;
  padding-inline: 12px !important;
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
}
.active-title {
  font-size: 32px;
  font-weight: 700;
  margin: 12px 0 6px;
  color: var(--text-primary);
}
.active-sub {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 4px;
  strong { color: var(--color-primary); font-weight: 700; }
}
.ml-12 { margin-left: 12px; }
.ml-8 { margin-left: 8px; }
.active-remark {
  font-size: 13px;
  color: var(--text-tertiary);
  margin: 4px 0 0;
}
.active-progress { text-align: center; }
.progress-text { font-size: 12px; color: var(--text-tertiary); margin-top: 6px; }

.empty-card { min-height: 280px; }
.empty-card :deep(.ant-empty) { padding: 40px 0; }

.history-card { margin-top: 0; }
.hist-title { font-weight: 600; color: var(--text-primary); }
.hist-sub { color: var(--text-tertiary); font-weight: 400; margin-left: 8px; font-size: 13px; }
</style>