<template>
  <div class="exercise-today-page">
    <a-card :bordered="false">
      <div class="page-header">
        <h2>今日运动记录</h2>
        <a-button @click="loadData">
          <ReloadOutlined /> 刷新
        </a-button>
      </div>

      <a-row :gutter="16" class="kpi-row">
        <a-col :xs="12" :md="8">
          <div class="kpi-card">
            <div class="kpi-label">今日总时长（DONE）</div>
            <div class="kpi-value">{{ formatMinutes(todayActual) }}</div>
          </div>
        </a-col>
        <a-col :xs="12" :md="8">
          <div class="kpi-card kpi-card--primary">
            <div class="kpi-label">完成次数</div>
            <div class="kpi-value">{{ doneCount }}<span>次</span></div>
          </div>
        </a-col>
        <a-col :xs="12" :md="8">
          <div class="kpi-card kpi-card--success">
            <div class="kpi-label">累计消耗</div>
            <div class="kpi-value">{{ Math.round(totalCalories * 100) / 100 }}<span>kcal</span></div>
          </div>
        </a-col>
      </a-row>

      <a-divider />

      <a-empty v-if="!records.length" description="今天还没开始运动" />

      <a-list v-else :data-source="records" item-layout="horizontal">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #avatar>
                <div class="rec-icon">
                  <component :is="resolveIcon(item.typeIcon)" />
                </div>
              </template>
              <template #title>
                <span class="rec-name">{{ item.typeName }}</span>
                <a-tag :color="statusColor(item.status)" class="ml-8">
                  {{ item.statusName }}
                </a-tag>
              </template>
              <template #description>
                <div class="rec-meta">
                  <span><FieldTimeOutlined /> 计划 {{ formatMinutes(item.planSeconds) }}</span>
                  <span>
                    <ClockCircleOutlined /> 实际
                    <span :class="{ 'text-warn': item.status === 3 }">
                      {{ formatMinutes(item.actualSeconds) }}
                    </span>
                  </span>
                  <span><FireOutlined /> {{ Math.round(Number(item.calories || 0) * 100) / 100 }} kcal</span>
                  <span><CalendarOutlined /> {{ item.startTime }}</span>
                </div>
                <div v-if="item.remark" class="rec-remark">{{ item.remark }}</div>
                <div v-if="item.status === 3 && item.abandonReason" class="rec-abandon">
                  放弃原因：{{ abandonReasonText(item.abandonReason) }}
                </div>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  ReloadOutlined,
  FieldTimeOutlined,
  ClockCircleOutlined,
  FireOutlined,
  CalendarOutlined,
  ThunderboltOutlined,
  RocketOutlined,
  RiseOutlined,
  PauseOutlined,
  ReloadOutlined as ReloadIcon,
  CaretUpOutlined,
  SwapOutlined,
  CompressOutlined,
  ExpandOutlined,
  HeartOutlined,
  TrophyOutlined
} from '@ant-design/icons-vue'
import { exerciseRecordApi } from '@/api/exerciseRecord'
import { showError } from '@/utils/message'

const records = ref([])

const ICON_MAP = {
  ThunderboltOutlined, RocketOutlined, RiseOutlined, PauseOutlined,
  ReloadOutlined: ReloadIcon, CaretUpOutlined, SwapOutlined, CompressOutlined,
  ExpandOutlined, FireOutlined, HeartOutlined, TrophyOutlined
}
function resolveIcon(name) {
  return ICON_MAP[name] || FireOutlined
}

function statusColor(s) {
  if (s === 2) return 'success'
  if (s === 3) return 'default'
  return 'processing'
}
function formatSeconds(s) {
  s = Number(s) || 0
  if (s < 60) return `${s} 秒`
  if (s % 60 === 0) return `${s / 60} 分钟`
  return `${Math.floor(s / 60)} 分 ${s % 60} 秒`
}

/** KPI 展示专用：单位统一为"分钟"，< 60 秒显示"不到 1 分钟" */
function formatMinutes(s) {
  s = Number(s) || 0
  if (s <= 0) return '0 分钟'
  if (s < 60) return '不到 1 分钟'
  const m = Math.floor(s / 60)
  const sec = s % 60
  if (sec === 0) return `${m} 分钟`
  return `${m} 分 ${sec} 秒`
}
function abandonReasonText(r) {
  if (r === 'USER_MANUAL') return '主动放弃'
  if (r === 'UNDER_DURATION') return '时长不足 95%'
  if (r === 'TIMEOUT') return '超时'
  return r || '—'
}

const todayActual = computed(() => records.value
  .filter((r) => r.status === 2)
  .reduce((s, r) => s + (r.actualSeconds || 0), 0))
const doneCount = computed(() => records.value.filter((r) => r.status === 2).length)
const totalCalories = computed(() => records.value
  .filter((r) => r.status === 2)
  .reduce((s, r) => s + Number(r.calories || 0), 0))

async function loadData() {
  try {
    records.value = await exerciseRecordApi.listToday()
  } catch (e) {
    showError(e.message || '加载失败')
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.exercise-today-page { max-width: 960px; margin: 0 auto; }
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  h2 { margin: 0; font-size: 20px; font-weight: 700; }
}

.kpi-row { margin-bottom: 0 !important; }
.kpi-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-xs);
}
.kpi-label { font-size: 13px; color: var(--text-tertiary); }
.kpi-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  span { font-size: 12px; font-weight: 400; color: var(--text-tertiary); margin-left: 4px; }
}
.kpi-card--primary .kpi-value { color: var(--color-primary); }
.kpi-card--success .kpi-value { color: var(--color-success); }

.rec-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-accent-light));
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.rec-name { font-weight: 600; font-size: 15px; }
.ml-8 { margin-left: 8px; }
.rec-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
  span { display: inline-flex; align-items: center; gap: 4px; }
}
.text-warn { color: var(--color-warning); }
.rec-remark {
  margin-top: 6px;
  font-size: 13px;
  color: var(--text-tertiary);
  font-style: italic;
}
.rec-abandon {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-tertiary);
}
</style>