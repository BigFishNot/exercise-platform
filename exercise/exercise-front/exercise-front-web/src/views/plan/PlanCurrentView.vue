<template>
  <div class="plan-current-page">
    <!-- 头部状态卡 -->
    <a-card class="hero-card" :bordered="false">
      <div v-if="loading" class="hero-loading">
        <a-skeleton active :paragraph="{ rows: 3 }" />
      </div>
      <div v-else-if="current" class="hero-inner">
        <div class="hero-left">
          <a-tag :color="statusColor" class="status-pill">
            <span class="status-dot" :class="current.status === 1 ? 'is-on' : 'is-off'" />
            {{ current.statusName }}
          </a-tag>
          <h2 class="hero-title">
            {{ current.startDate }} ~ {{ current.endDate }}
          </h2>
          <p class="hero-sub">
            每日目标 <strong>{{ current.dailyTargetMinutes }}</strong> 分钟 ·
            已进行 <strong>{{ current.daysElapsed }}</strong> / {{ current.daysTotal }} 天
          </p>
          <p v-if="current.remark" class="hero-remark">
            <FileTextOutlined /> {{ current.remark }}
          </p>
        </div>

        <div class="hero-right">
          <a-progress
            type="circle"
            :percent="progressPct"
            :stroke-color="progressColor"
            :size="110"
          />
          <div class="progress-text">完成度</div>
        </div>
      </div>

      <a-empty v-else description="尚未制定阶段计划" class="hero-empty">
        <a-button type="primary" size="large" @click="goCreate">
          <PlusOutlined /> 立即创建
        </a-button>
      </a-empty>
    </a-card>

    <a-row v-if="current" :gutter="16" class="kpi-row">
      <a-col :xs="12" :md="6">
        <div class="kpi-card">
          <div class="kpi-label">阶段总天数</div>
          <div class="kpi-value">{{ current.daysTotal }}<span>天</span></div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--primary">
          <div class="kpi-label">已进行</div>
          <div class="kpi-value">{{ current.daysElapsed }}<span>天</span></div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--success">
          <div class="kpi-label">今日累计</div>
          <div class="kpi-value">{{ current.todayActualMinutes }}<span>分</span></div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--warning">
          <div class="kpi-label">剩余天数</div>
          <div class="kpi-value">{{ current.daysRemaining }}<span>天</span></div>
        </div>
      </a-col>
    </a-row>

    <a-card v-if="current" class="action-card" :bordered="false">
      <a-space wrap>
        <a-button @click="openTargetModal">
          <EditOutlined /> 修改每日目标
        </a-button>
        <a-popconfirm
          title="确认取消该阶段计划? 取消后不可恢复"
          ok-text="确认取消"
          cancel-text="再想想"
          @confirm="onCancel"
        >
          <a-button danger>
            <CloseCircleOutlined /> 取消计划
          </a-button>
        </a-popconfirm>
      </a-space>
    </a-card>

    <!-- 阶段日历（直接展示） -->
    <a-card v-if="current && calendar" class="calendar-card" :bordered="false">
      <template #title>
        <div class="cal-title">
          <CalendarOutlined /> 阶段日历
          <span class="cal-sub">每日目标 {{ current.dailyTargetMinutes }} 分钟</span>
        </div>
      </template>
      <template #extra>
        <a-space :size="12" class="cal-legend">
          <span><i class="dot dot-done" /> 已打卡</span>
          <span><i class="dot dot-ins" /> 时长不足</span>
          <span><i class="dot dot-not" /> 未打卡</span>
          <span><i class="dot dot-future" /> 未来</span>
          <span><i class="dot dot-today" /> 今天</span>
        </a-space>
      </template>

      <div class="cal-week-row">
        <div v-for="w in weekHeaders" :key="w" class="cal-week-cell">{{ w }}</div>
      </div>

      <div class="cal-grid">
        <div
          v-for="d in calendarWithPad"
          :key="d.key"
          class="cal-cell"
          :class="d.cls"
        >
          <template v-if="d.day">
            <div class="cal-day">{{ d.day }}</div>
            <div class="cal-status">
              <span v-if="d.status === 'DONE'" class="cal-badge done">✓</span>
              <span v-else-if="d.status === 'INSUFFICIENT'" class="cal-badge ins">{{ d.actualMinutes || 0 }}</span>
              <span v-else-if="d.status === 'NOT_DONE'" class="cal-badge not">·</span>
              <span v-else class="cal-badge future"></span>
            </div>
          </template>
        </div>
      </div>
    </a-card>

    <!-- 历史计划 -->
    <a-card v-if="history.length" class="history-card" :bordered="false" title="历史计划">
      <a-list :data-source="history" item-layout="horizontal">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #title>
                <span>{{ item.startDate }} ~ {{ item.endDate }} · 每日 {{ item.dailyTargetMinutes }} 分钟</span>
              </template>
              <template #description>
                <a-tag :color="item.status === 2 ? 'success' : 'default'">{{ item.statusName }}</a-tag>
                <span v-if="item.remark" class="text-tertiary">{{ item.remark }}</span>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-card>

    <!-- 修改每日目标弹窗 -->
    <a-modal
      v-model:open="targetModalOpen"
      title="修改每日目标"
      ok-text="保存"
      cancel-text="取消"
      :confirm-loading="saving"
      @ok="onSaveTarget"
    >
      <a-form :model="targetForm" layout="vertical">
        <a-form-item label="每日目标时长（分钟）" :rules="[{ required: true }]">
          <a-input-number
            v-model:value="targetForm.dailyTargetMinutes"
            :min="1"
            :max="600"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea
            v-model:value="targetForm.remark"
            :rows="3"
            placeholder="可选，例如：本阶段专注跳绳"
            maxlength="200"
            show-count
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import {
  PlusOutlined,
  CalendarOutlined,
  EditOutlined,
  CloseCircleOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue'
import { exercisePlanApi } from '@/api/exercisePlan'
import { showSuccess, showError, $confirm } from '@/utils/message'

const router = useRouter()

const loading = ref(false)
const current = ref(null)
const history = ref([])
const calendar = ref(null)
const targetModalOpen = ref(false)
const saving = ref(false)
const targetForm = reactive({ dailyTargetMinutes: 30, remark: '' })

const weekHeaders = ['一', '二', '三', '四', '五', '六', '日']

const statusColor = computed(() => {
  if (!current.value) return 'default'
  if (current.value.status === 1) return 'success'
  if (current.value.status === 2) return 'blue'
  return 'default'
})
const progressPct = computed(() => {
  if (!current.value || !current.value.daysTotal) return 0
  return Math.round((current.value.daysElapsed / current.value.daysTotal) * 100)
})
const progressColor = computed(() => {
  const p = progressPct.value
  if (p >= 80) return { from: '#10b981', to: '#0ea5e9' }
  if (p >= 40) return { from: '#0ea5e9', to: '#6366f1' }
  return { from: '#f59e0b', to: '#10b981' }
})

/** 把日历数据按周对齐（周一开始），前面补空 */
const calendarWithPad = computed(() => {
  if (!calendar.value?.days) return []
  const days = calendar.value.days
  if (!days.length) return []
  const firstDate = new Date(days[0].date)
  // 0=Sun, 1=Mon ... 转成 0=Mon
  const firstDow = (firstDate.getDay() + 6) % 7
  const todayStr = dayjs().format('YYYY-MM-DD')
  const cells = []
  for (let i = 0; i < firstDow; i++) {
    cells.push({ key: `pad-${i}`, day: null, cls: 'is-pad' })
  }
  days.forEach((d) => {
    const isToday = dayjs(d.date).format('YYYY-MM-DD') === todayStr
    let cls = ''
    if (d.checkInStatus === 'DONE') cls = 'is-done'
    else if (d.checkInStatus === 'INSUFFICIENT') cls = 'is-ins'
    else if (d.checkInStatus === 'NOT_DONE') cls = 'is-not'
    else cls = 'is-future'
    if (isToday) cls += ' is-today'
    cells.push({
      key: `d-${d.date}`,
      day: dayjs(d.date).date(),
      status: d.checkInStatus,
      actualMinutes: d.actualMinutes,
      cls
    })
  })
  // 末尾补齐到整周
  while (cells.length % 7 !== 0) {
    cells.push({ key: `pad-end-${cells.length}`, day: null, cls: 'is-pad' })
  }
  return cells
})

async function loadData() {
  loading.value = true
  try {
    const [curr, all] = await Promise.all([
      exercisePlanApi.getCurrent(),
      exercisePlanApi.list()
    ])
    current.value = curr || null
    history.value = (all || []).filter((p) => p.status !== 1)
    if (current.value?.planId) {
      try {
        calendar.value = await exercisePlanApi.getCalendar(current.value.planId)
      } catch (e) {
        calendar.value = null
      }
    } else {
      calendar.value = null
    }
  } catch (e) {
    showError(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function goCreate() {
  router.push('/plan/create')
}
function openTargetModal() {
  if (!current.value) return
  targetForm.dailyTargetMinutes = current.value.dailyTargetMinutes
  targetForm.remark = current.value.remark || ''
  targetModalOpen.value = true
}
async function onSaveTarget() {
  saving.value = true
  try {
    await exercisePlanApi.updateDailyTarget({
      planId: current.value.planId,
      dailyTargetMinutes: targetForm.dailyTargetMinutes,
      remark: targetForm.remark
    })
    showSuccess('已保存')
    targetModalOpen.value = false
    loadData()
  } catch (e) {
    showError(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}
function onCancel() {
  $confirm({
    title: '确认取消该阶段计划?',
    content: '取消后不可恢复，历史记录会保留',
    okText: '确认取消',
    cancelText: '再想想',
    okButtonProps: { danger: true },
    onOk: async () => {
      try {
        await exercisePlanApi.cancel(current.value.planId)
        showSuccess('已取消')
        loadData()
      } catch (e) {
        showError(e.message || '取消失败')
      }
    }
  })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.plan-current-page { display: flex; flex-direction: column; gap: 16px; }

/* Hero */
.hero-card {
  background:
    radial-gradient(800px 300px at 0% 0%, rgba(16, 185, 129, 0.10), transparent 60%),
    radial-gradient(600px 300px at 100% 100%, rgba(14, 165, 233, 0.10), transparent 60%),
    linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
}
.hero-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}
.hero-left { flex: 1; min-width: 0; }
.hero-title { font-size: 24px; font-weight: 700; margin: 12px 0 6px; color: var(--text-primary); }
.hero-sub { color: var(--text-secondary); font-size: 14px; margin: 0 0 6px; strong { color: var(--color-primary); font-weight: 700; } }
.hero-remark { color: var(--text-tertiary); font-size: 13px; margin: 0; }
.hero-right { text-align: center; }
.progress-text { font-size: 12px; color: var(--text-tertiary); margin-top: 6px; }
.hero-empty { padding: 40px 0; }
.hero-loading { padding: 12px 0; }

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
  &.is-off { background: var(--text-tertiary); }
}

/* KPI */
.kpi-row { margin-bottom: 0 !important; }
.kpi-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 20px;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-xs);
  transition: transform 0.2s, box-shadow 0.2s;
  &:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
}
.kpi-label { font-size: 13px; color: var(--text-tertiary); }
.kpi-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  span { font-size: 12px; font-weight: 400; color: var(--text-tertiary); margin-left: 4px; }
}
.kpi-card--primary .kpi-value { color: var(--color-primary); }
.kpi-card--success .kpi-value { color: var(--color-success); }
.kpi-card--warning .kpi-value { color: var(--color-warning); }

.action-card :deep(.ant-card-body) { padding: 16px 24px; }
.history-card { margin-top: 0; }

/* 阶段日历 */
.calendar-card { margin-top: 0; }
.calendar-card :deep(.ant-card-body) { padding: 0 24px 24px; }
.cal-title {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}
.cal-sub {
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 400;
}
.cal-legend {
  font-size: 12px;
  color: var(--text-tertiary);
  span { display: inline-flex; align-items: center; gap: 4px; }
  .dot {
    width: 12px;
    height: 12px;
    border-radius: 3px;
    display: inline-block;
  }
  .dot-done   { background: linear-gradient(135deg, var(--color-success), var(--color-accent)); }
  .dot-ins    { background: var(--color-warning-light); border: 1px solid var(--color-warning); }
  .dot-not    { background: var(--color-bg-soft); border: 1px solid var(--border-color); }
  .dot-future { background: #f8fafc; border: 1px dashed #cbd5e1; }
  .dot-today  { background: var(--color-primary); box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.18); }
}

.cal-week-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 8px;
}
.cal-week-cell {
  text-align: center;
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: 500;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}
.cal-cell {
  aspect-ratio: 1.2 / 1;
  background: var(--color-surface);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6px 4px;
  position: relative;
  transition: transform 0.2s, box-shadow 0.2s;
  &:hover { transform: translateY(-1px); box-shadow: var(--shadow-sm); }

  &.is-pad { background: transparent; border: none; pointer-events: none; }
  &.is-done {
    background: linear-gradient(135deg, var(--color-success-light), var(--color-accent-light));
    border-color: transparent;
  }
  &.is-ins { background: var(--color-warning-light); border-color: var(--color-warning); }
  &.is-not { background: var(--color-bg-soft); }
  &.is-future { background: #f8fafc; border-style: dashed; opacity: 0.65; }
  &.is-today {
    outline: 2px solid var(--color-primary);
    outline-offset: -2px;
  }
}
.cal-day { font-size: 16px; font-weight: 600; color: var(--text-primary); line-height: 1; }
.cal-status { margin-top: 4px; }
.cal-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 18px;
  padding: 0 6px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  &.done   { background: var(--color-success); color: #fff; }
  &.ins    { background: var(--color-warning); color: #fff; }
  &.not    { color: var(--text-tertiary); }
  &.future { background: transparent; }
}
</style>