<template>
  <div class="stats-page">
    <a-row :gutter="16" class="kpi-row">
      <a-col :xs="12" :md="6">
        <div class="kpi-card">
          <div class="kpi-label">累计有效运动</div>
          <div class="kpi-value">{{ formatSeconds(vo?.totalActiveSeconds) }}</div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--primary">
          <div class="kpi-label">有效运动天数</div>
          <div class="kpi-value">{{ vo?.totalActiveDays || 0 }}<span>天</span></div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--success">
          <div class="kpi-label">累计消耗</div>
          <div class="kpi-value">{{ vo?.totalCalories || 0 }}<span>kcal</span></div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--warning">
          <div class="kpi-label">近 7 天运动</div>
          <div class="kpi-value">{{ vo?.last7DaysActiveDays || 0 }}<span>天</span></div>
        </div>
      </a-col>
    </a-row>

    <!-- 阶段计划进度 -->
    <a-card class="card" :bordered="false" title="阶段计划">
      <div v-if="vo?.hasActivePlan" class="plan-block">
        <a-row :gutter="16">
          <a-col :xs="24" :md="14">
            <div class="plan-text">
              <h3>{{ vo.startDate }} ~ {{ vo.endDate }}</h3>
              <p class="plan-sub">
                每日目标 <strong>{{ vo.dailyTargetMinutes }}</strong> 分钟
                <span class="ml-12">已进行 <strong>{{ doneDays }}</strong> / {{ vo.totalDays }} 天</span>
              </p>
            </div>
            <div class="plan-progress">
              <a-progress
                :percent="vo.planCompletionRate || 0"
                :stroke-color="{ from: '#10b981', to: '#0ea5e9' }"
                :show-info="false"
              />
              <div class="plan-progress-text">
                完成度 <strong>{{ vo.planCompletionRate }}%</strong>
                · 已打卡 <strong>{{ vo.doneDays }}</strong> 天
                · 时长不足 <strong>{{ vo.insufficientDays }}</strong> 天
                · 剩余 <strong>{{ vo.daysRemaining }}</strong> 天
              </div>
            </div>
          </a-col>
          <a-col :xs="24" :md="10">
            <div class="plan-today">
              <div class="plan-today-label">今日打卡</div>
              <a-tag :color="checkInColor" class="plan-today-tag">
                <span class="status-dot" :class="vo.todayCheckInStatus === 1 ? 'is-on' : 'is-off'" />
                {{ vo.todayCheckInStatusName || '未打卡' }}
              </a-tag>
              <div class="plan-today-progress">
                {{ vo.todayActualMinutes || 0 }} / {{ vo.todayTargetMinutes || 0 }} 分钟
                <span class="ml-12">({{ vo.todayCompletionRate || 0 }}%)</span>
              </div>
              <a-progress
                :percent="vo.todayCompletionRate || 0"
                :stroke-color="checkInColor === 'success' ? { from: '#10b981', to: '#0ea5e9' } : '#f59e0b'"
                :show-info="false"
                size="small"
              />
            </div>
          </a-col>
        </a-row>
      </div>
      <a-empty v-else description="暂无进行中的阶段计划">
        <a-button type="primary" @click="$router.push('/plan/create')">
          立即创建
        </a-button>
      </a-empty>
    </a-card>

    <!-- 体重 vs 目标 -->
    <a-card class="card" :bordered="false" title="体重与目标">
      <a-row :gutter="16">
        <a-col :xs="12" :md="6">
          <div class="m-cell">
            <div class="m-label">最新体重</div>
            <div class="m-value">
              <span v-if="vo?.latestWeight">{{ formatWeight(vo.latestWeight) }}</span>
              <span v-else>—</span>
              <span class="m-unit">kg</span>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :md="6">
          <div class="m-cell">
            <div class="m-label">最新 BMI</div>
            <div class="m-value">
              <span v-if="vo?.latestBmi">{{ formatNumber(vo.latestBmi) }}</span>
              <span v-else>—</span>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :md="6">
          <div class="m-cell">
            <div class="m-label">7 天变化</div>
            <div class="m-value" :class="weightChangeClass">
              <span v-if="vo?.weightChange7d != null">
                {{ vo.weightChange7d > 0 ? '+' : '' }}{{ formatNumber(vo.weightChange7d) }}
              </span>
              <span v-else>—</span>
              <span class="m-unit">kg</span>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :md="6">
          <div class="m-cell">
            <div class="m-label">距目标还差</div>
            <div class="m-value" :class="distanceClass">
              <span v-if="vo?.distanceToTarget != null">
                {{ vo.distanceToTarget > 0 ? '+' : '' }}{{ formatNumber(vo.distanceToTarget) }}
              </span>
              <span v-else>—</span>
              <span class="m-unit">kg</span>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 累计速览 -->
    <a-card class="card" :bordered="false" title="累计速览">
      <a-row :gutter="16">
        <a-col :xs="24" :md="8">
          <div class="kpi-mini">
            <div class="kpi-mini-label">日均运动时长</div>
            <div class="kpi-mini-value">{{ formatSeconds(vo?.avgSecondsPerActiveDay) }}</div>
          </div>
        </a-col>
        <a-col :xs="24" :md="8">
          <div class="kpi-mini">
            <div class="kpi-mini-label">近 30 天运动</div>
            <div class="kpi-mini-value">{{ vo?.last30DaysActiveDays || 0 }} 天</div>
          </div>
        </a-col>
        <a-col :xs="12" :md="4">
          <div class="kpi-mini">
            <div class="kpi-mini-label">阶段计划完成率</div>
            <div class="kpi-mini-value">{{ vo?.planCompletionRate || 0 }}<span>%</span></div>
          </div>
        </a-col>
        <a-col :xs="12" :md="4">
          <div class="kpi-mini">
            <div class="kpi-mini-label">目标体重</div>
            <div class="kpi-mini-value">
              <span v-if="vo?.targetWeight">{{ formatWeight(vo.targetWeight) }}</span>
              <span v-else>—</span>
              <span class="m-unit">kg</span>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { statisticsApi } from '@/api/statistics'
import { showError } from '@/utils/message'

const vo = ref(null)

const checkInColor = computed(() => {
  const s = vo.value?.todayCheckInStatus
  if (s === 1) return 'success'
  if (s === 2) return 'warning'
  return 'default'
})

const doneDays = computed(() => {
  const v = vo.value
  if (!v?.hasActivePlan || !v?.startDate || !v?.endDate) return 0
  const start = new Date(v.startDate)
  const end = new Date(v.endDate)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const total = Math.floor((end - start) / 86400000) + 1
  const elapsed = Math.max(0, Math.min(total, Math.floor((today - start) / 86400000) + 1))
  return elapsed
})

const weightChangeClass = computed(() => {
  const v = vo.value?.weightChange7d
  if (v == null) return ''
  if (v > 0) return 'is-up'
  if (v < 0) return 'is-down'
  return ''
})
const distanceClass = computed(() => {
  const v = vo.value?.distanceToTarget
  if (v == null) return ''
  if (v > 0) return 'is-up'
  if (v < 0) return 'is-down'
  return ''
})

function formatSeconds(s) {
  s = Number(s) || 0
  if (s <= 0) return '0 秒'
  if (s < 60) return `${s} 秒`
  if (s % 60 === 0) return `${s / 60} 分钟`
  return `${Math.floor(s / 60)} 分 ${s % 60} 秒`
}
function formatWeight(v) {
  if (v == null) return '—'
  return Number(v).toFixed(2)
}
function formatNumber(v) {
  if (v == null) return '—'
  return Number(v).toFixed(2)
}

async function loadData() {
  try {
    vo.value = await statisticsApi.personalSummary()
  } catch (e) {
    showError(e.message || '加载失败')
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.stats-page { display: flex; flex-direction: column; gap: 16px; max-width: 1080px; margin: 0 auto; }

/* 顶部 KPI */
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

/* 通用卡 */
.card { background: var(--color-surface); }
.card :deep(.ant-card-head-title) { font-weight: 600; }

/* 阶段计划块 */
.plan-block { padding: 4px 0; }
.plan-text h3 { font-size: 18px; font-weight: 600; margin: 0 0 6px; }
.plan-sub { color: var(--text-secondary); font-size: 14px; margin: 0 0 16px;
  strong { color: var(--color-primary); font-weight: 700; }
}
.ml-12 { margin-left: 12px; }
.plan-progress-text {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-tertiary);
  strong { color: var(--text-primary); font-weight: 600; margin: 0 4px; }
}

.plan-today {
  background: var(--color-bg-soft);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 16px;
  height: 100%;
}
.plan-today-label { font-size: 12px; color: var(--text-tertiary); margin-bottom: 8px; }
.plan-today-tag {
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
  &.is-on  { background: var(--color-success); box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.18); }
  &.is-off { background: var(--text-tertiary); }
}
.plan-today-progress { margin: 12px 0 4px; font-size: 13px; color: var(--text-secondary); }

/* 体重 / 目标卡 */
.m-cell {
  padding: 16px;
  background: var(--color-bg-soft);
  border-radius: var(--radius-md);
  text-align: center;
  border: 1px solid var(--border-color);
}
.m-label { font-size: 12px; color: var(--text-tertiary); }
.m-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-top: 4px;
  &.is-up   { color: var(--color-danger); }
  &.is-down { color: var(--color-success); }
}
.m-unit { font-size: 12px; font-weight: 400; color: var(--text-tertiary); margin-left: 4px; }

/* 累计速览 */
.kpi-mini {
  padding: 14px;
  background: var(--color-bg-soft);
  border-radius: var(--radius-md);
  text-align: center;
  border: 1px solid var(--border-color);
  height: 100%;
}
.kpi-mini-label { font-size: 12px; color: var(--text-tertiary); }
.kpi-mini-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-top: 4px;
  span { font-size: 12px; font-weight: 400; color: var(--text-tertiary); margin-left: 4px; }
}
</style>