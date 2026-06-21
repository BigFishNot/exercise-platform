<template>
  <div class="plan-calendar-page">
    <a-card :bordered="false" v-if="calendar">
      <div class="cal-header">
        <div>
          <h2 class="cal-title">
            阶段日历
            <a-tag :color="statusColor" class="status-pill ml-12">
              <span class="status-dot" :class="calendar.status === 1 ? 'is-on' : 'is-off'" />
              {{ calendar.statusName }}
            </a-tag>
          </h2>
          <p class="text-tertiary">
            {{ calendar.startDate }} ~ {{ calendar.endDate }} · 每日目标 {{ calendar.dailyTargetMinutes }} 分钟
          </p>
        </div>
        <a-button @click="$router.back()">
          <ArrowLeftOutlined /> 返回
        </a-button>
      </div>

      <a-divider />

      <a-row :gutter="16" class="legend-row">
        <a-col :span="6"><span class="lg lg-future" />未来</a-col>
        <a-col :span="6"><span class="lg lg-done" />已完成</a-col>
        <a-col :span="6"><span class="lg lg-ins" />时长不足</a-col>
        <a-col :span="6"><span class="lg lg-not" />未打卡</a-col>
      </a-row>

      <div class="cal-grid">
        <div
          v-for="d in calendar.days"
          :key="d.date"
          class="cal-cell"
          :class="cellClass(d)"
        >
          <div class="cal-day">{{ dayjs(d.date).format('D') }}</div>
          <div class="cal-week">{{ dayjs(d.date).format('ddd') }}</div>
          <div class="cal-status">
            <span v-if="d.checkInStatus === 'DONE'" class="cal-badge done">完成</span>
            <span v-else-if="d.checkInStatus === 'INSUFFICIENT'" class="cal-badge ins">{{ d.actualMinutes || 0 }}/{{ d.targetMinutes }}</span>
            <span v-else-if="d.checkInStatus === 'NOT_DONE'" class="cal-badge not">未打卡</span>
            <span v-else class="cal-badge future">—</span>
          </div>
        </div>
      </div>

      <a-alert
        v-if="showCheckInHint"
        type="info"
        show-icon
        class="hint"
        message="打卡判定将由 exerciseCheckIn 模块实时填充"
        description="当日累计有效运动时长 >= 每日目标时，标记为完成（DONE）。当前页面会随该模块自动更新。"
      />
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import { exercisePlanApi } from '@/api/exercisePlan'
import { showError } from '@/utils/message'

const route = useRoute()
const router = useRouter()

const calendar = ref(null)
const showCheckInHint = ref(true)

const statusColor = computed(() => {
  const s = calendar.value?.status
  if (s === 1) return 'success'
  if (s === 2) return 'blue'
  return 'default'
})

function cellClass(d) {
  switch (d.checkInStatus) {
    case 'DONE': return 'is-done'
    case 'INSUFFICIENT': return 'is-ins'
    case 'NOT_DONE': return 'is-not'
    case 'FUTURE': return 'is-future'
    default: return ''
  }
}

onMounted(async () => {
  try {
    const data = await exercisePlanApi.getCalendar(route.params.planId)
    // 后端返回的 days 中是 string 还是 Date 取决于序列化，这里统一为 dayjs 对象
    if (data?.days) {
      data.days = data.days.map((d) => ({
        ...d,
        _date: d.date ? dayjs(d.date) : null
      }))
    }
    calendar.value = data
  } catch (e) {
    showError(e.message || '加载失败')
  }
})
</script>

<style lang="scss" scoped>
.plan-calendar-page { max-width: 1000px; margin: 0 auto; }
.cal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}
.cal-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 6px;
  display: flex;
  align-items: center;
}
.ml-12 { margin-left: 12px; }
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

.legend-row { margin: 0 0 16px; font-size: 13px; color: var(--text-secondary); }
.lg {
  display: inline-block;
  width: 14px; height: 14px;
  border-radius: 4px;
  margin-right: 6px;
  vertical-align: middle;
}
.lg-future { background: #f1f5f9; border: 1px dashed #cbd5e1; }
.lg-done { background: linear-gradient(135deg, var(--color-success), var(--color-accent)); }
.lg-ins { background: var(--color-warning-light); border: 1px solid var(--color-warning); }
.lg-not { background: var(--color-bg-soft); border: 1px solid var(--border-color); }

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
}
.cal-cell {
  aspect-ratio: 1;
  background: var(--color-surface);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px 4px;
  text-align: center;
  transition: transform 0.2s, box-shadow 0.2s;
  &:hover { transform: translateY(-1px); box-shadow: var(--shadow-sm); }

  &.is-done {
    background: linear-gradient(135deg, var(--color-success-light), var(--color-accent-light));
    border-color: transparent;
  }
  &.is-ins {
    background: var(--color-warning-light);
    border-color: var(--color-warning);
  }
  &.is-not { background: var(--color-bg-soft); }
  &.is-future { background: #f8fafc; border-style: dashed; opacity: 0.65; }
}
.cal-day { font-size: 20px; font-weight: 700; color: var(--text-primary); line-height: 1; }
.cal-week { font-size: 11px; color: var(--text-tertiary); margin: 2px 0 6px; }
.cal-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  &.done { background: var(--color-success); color: #fff; }
  &.ins { background: var(--color-warning); color: #fff; }
  &.not { background: #fff; color: var(--text-tertiary); border: 1px solid var(--border-color); }
  &.future { background: transparent; color: var(--text-tertiary); }
}

.hint { margin-top: 16px; }
</style>