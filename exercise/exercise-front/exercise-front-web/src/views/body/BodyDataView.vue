<template>
  <div class="body-data-page">
    <!-- 今日卡 -->
    <a-card class="today-card" :bordered="false">
      <div class="today-inner">
        <div class="today-left">
          <div class="today-label">今日体重</div>
          <div class="today-value">
            <span v-if="today">{{ formatWeight(today.weight) }}</span>
            <span v-else class="text-tertiary">未记录</span>
            <span class="today-unit">kg</span>
          </div>
          <div v-if="today" class="today-bmi">
            BMI <strong>{{ formatNumber(today.bmi) }}</strong>
            <a-tag :color="bmiColor(today.bmi)" class="bmi-tag">{{ bmiText(today.bmi) }}</a-tag>
          </div>
          <div v-else class="today-bmi text-tertiary">
            记录体重后自动计算 BMI
          </div>
        </div>
        <a-button
          type="primary"
          size="large"
          @click="openUpsertModal"
        >
          <EditOutlined />
          {{ today ? '更新今日' : '记录体重' }}
        </a-button>
      </div>
    </a-card>

    <!-- 趋势卡 -->
    <a-card class="trend-card" :bordered="false">
      <template #title>
        <div class="trend-header">
          <span><LineChartOutlined /> 体重趋势</span>
          <a-radio-group
            v-model:value="rangeKey"
            size="small"
            button-style="solid"
            @change="loadTrend"
          >
            <a-radio-button value="7d">近 7 天</a-radio-button>
            <a-radio-button value="30d">近 30 天</a-radio-button>
            <a-radio-button value="90d">近 90 天</a-radio-button>
            <a-radio-button value="stage">本阶段</a-radio-button>
          </a-radio-group>
        </div>
      </template>

      <a-row :gutter="16" class="summary-row">
        <a-col :xs="12" :md="6">
          <div class="summary-cell">
            <div class="summary-label">最新</div>
            <div class="summary-value">
              <span v-if="summary?.latestWeight">{{ formatWeight(summary.latestWeight) }}</span>
              <span v-else>—</span>
              <span class="summary-unit">kg</span>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :md="6">
          <div class="summary-cell">
            <div class="summary-label">区间变化</div>
            <div class="summary-value" :class="changeClass">
              <span v-if="summary?.weightChange7d != null">
                {{ summary.weightChange7d > 0 ? '+' : '' }}{{ formatNumber(summary.weightChange7d) }}
              </span>
              <span v-else>—</span>
              <span class="summary-unit">kg</span>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :md="6">
          <div class="summary-cell">
            <div class="summary-label">最低</div>
            <div class="summary-value">
              <span v-if="summary?.minWeight">{{ formatWeight(summary.minWeight) }}</span>
              <span v-else>—</span>
              <span class="summary-unit">kg</span>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :md="6">
          <div class="summary-cell">
            <div class="summary-label">最高</div>
            <div class="summary-value">
              <span v-if="summary?.maxWeight">{{ formatWeight(summary.maxWeight) }}</span>
              <span v-else>—</span>
              <span class="summary-unit">kg</span>
            </div>
          </div>
        </a-col>
      </a-row>

      <div class="chart-wrap">
        <div v-if="!points.length" class="empty-chart">
          <a-empty description="该区间暂无记录" />
        </div>
        <div v-else class="chart-area">
          <!-- 纯 SVG 折线图（不引入图表库） -->
          <svg :viewBox="`0 0 ${chartSize.w} ${chartSize.h}`" preserveAspectRatio="none">
            <!-- 网格 -->
            <line
              v-for="g in 4"
              :key="`g-${g}`"
              :x1="padding" :x2="chartSize.w - padding"
              :y1="padding + ((chartSize.h - padding * 2) / 4) * g"
              :y2="padding + ((chartSize.h - padding * 2) / 4) * g"
              stroke="var(--border-color)"
              stroke-dasharray="3 3"
            />
            <!-- 折线 -->
            <polyline
              v-if="points.length >= 2"
              :points="linePoints"
              fill="none"
              stroke="url(#lineGrad)"
              stroke-width="2.5"
              stroke-linejoin="round"
              stroke-linecap="round"
            />
            <!-- 面积渐变 -->
            <defs>
              <linearGradient id="lineGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" stop-color="#10b981" />
                <stop offset="100%" stop-color="#0ea5e9" />
              </linearGradient>
              <linearGradient id="areaGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stop-color="#10b981" stop-opacity="0.30" />
                <stop offset="100%" stop-color="#0ea5e9" stop-opacity="0.02" />
              </linearGradient>
            </defs>
            <polygon
              v-if="points.length >= 2"
              :points="areaPoints"
              fill="url(#areaGrad)"
            />
            <!-- 数据点 -->
            <g v-for="(p, i) in pointPositions" :key="`pt-${i}`">
              <circle
                :cx="p.x"
                :cy="p.y"
                r="4"
                fill="#fff"
                stroke="#10b981"
                stroke-width="2"
              />
            </g>
            <!-- 最高/最低标记 -->
            <g v-if="extremes.max">
              <line
                :x1="extremes.max.x" :x2="extremes.max.x"
                :y1="padding" :y2="chartSize.h - padding"
                stroke="var(--color-danger)" stroke-dasharray="2 2" stroke-width="1"
              />
              <circle
                :cx="extremes.max.x" :cy="extremes.max.y"
                r="5" fill="var(--color-danger)"
              />
            </g>
            <g v-if="extremes.min">
              <circle
                :cx="extremes.min.x" :cy="extremes.min.y"
                r="5" fill="var(--color-success)"
              />
            </g>
          </svg>
          <div class="x-labels">
            <span
              v-for="(p, i) in points"
              :key="`xl-${i}`"
              class="x-label"
              :class="{ 'is-edge': i === 0 || i === points.length - 1 }"
            >
              {{ formatXLabel(p.recordDate) }}
            </span>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 录入/更新弹窗 -->
    <a-modal
      v-model:open="upsertOpen"
      :title="today ? '更新今日体重' : '记录体重'"
      ok-text="保存"
      cancel-text="取消"
      :confirm-loading="saving"
      @ok="onUpsert"
    >
      <a-form layout="vertical">
        <a-form-item label="体重 (kg)" :required="true">
          <a-input-number
            v-model:value="form.weight"
            :min="0"
            :max="500"
            :precision="2"
            :step="0.1"
            style="width: 100%"
            placeholder="如：65.50"
          />
        </a-form-item>
        <a-form-item label="备注（可选）">
          <a-textarea v-model:value="form.remark" :rows="2" maxlength="200" show-count />
        </a-form-item>
        <a-alert
          v-if="!hasHeight"
          type="warning"
          show-icon
          message="尚未设置身高"
          description="请先到「个人资料」设置身高（cm），BMI 需要身高才能计算。"
        />
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { EditOutlined, LineChartOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { bodyDataApi } from '@/api/bodyData'
import { useLoginStore } from '@/stores/useLoginStore'
import { showSuccess, showError } from '@/utils/message'

const router = useRouter()
const loginStore = useLoginStore()

const today = ref(null)
const summary = ref(null)
const points = ref([])
const rangeKey = ref('30d')
const upsertOpen = ref(false)
const saving = ref(false)
const form = reactive({ weight: null, remark: '' })

const hasHeight = computed(() => {
  const h = loginStore.userInfo?.height
  return h && Number(h) > 0
})

const chartSize = { w: 800, h: 240 }
const padding = 24

const sortedPoints = computed(() => points.value || [])

const scaleY = computed(() => {
  const ws = sortedPoints.value.map((p) => Number(p.weight)).filter((n) => Number.isFinite(n))
  if (ws.length === 0) return { min: 0, max: 100, range: 100 }
  const min = Math.min(...ws)
  const max = Math.max(...ws)
  const pad = (max - min) * 0.15 || 1
  return { min: min - pad, max: max + pad, range: (max - min) + pad * 2 }
})

function px(i) {
  const n = sortedPoints.value.length
  if (n <= 1) return chartSize.w / 2
  const usableW = chartSize.w - padding * 2
  return padding + (usableW * i) / (n - 1)
}
function py(w) {
  const { min, range } = scaleY.value
  const usableH = chartSize.h - padding * 2
  return padding + usableH - ((Number(w) - min) / range) * usableH
}

const pointPositions = computed(() => {
  return sortedPoints.value.map((p, i) => ({ x: px(i), y: py(p.weight) }))
})
const linePoints = computed(() =>
  pointPositions.value.map((p) => `${p.x},${p.y}`).join(' ')
)
const areaPoints = computed(() => {
  const pts = pointPositions.value
  if (pts.length < 2) return ''
  const start = `${pts[0].x},${chartSize.h - padding}`
  const end = `${pts[pts.length - 1].x},${chartSize.h - padding}`
  return [start, ...pts.map((p) => `${p.x},${p.y}`), end].join(' ')
})
const extremes = computed(() => {
  const ws = sortedPoints.value
  if (ws.length === 0) return { max: null, min: null }
  let maxIdx = 0, minIdx = 0
  ws.forEach((p, i) => {
    if (Number(p.weight) > Number(ws[maxIdx].weight)) maxIdx = i
    if (Number(p.weight) < Number(ws[minIdx].weight)) minIdx = i
  })
  return {
    max: { x: px(maxIdx), y: py(ws[maxIdx].weight) },
    min: { x: px(minIdx), y: py(ws[minIdx].weight) }
  }
})

const changeClass = computed(() => {
  const v = summary.value?.weightChange7d
  if (v == null) return ''
  if (v > 0) return 'is-up'
  if (v < 0) return 'is-down'
  return ''
})

function formatWeight(v) {
  if (v == null) return '—'
  return Number(v).toFixed(2)
}
function formatNumber(v) {
  if (v == null) return '—'
  return Number(v).toFixed(2)
}
function bmiColor(b) {
  const v = Number(b)
  if (!v) return 'default'
  if (v < 18.5) return 'gold'
  if (v < 24) return 'green'
  if (v < 28) return 'orange'
  return 'red'
}
function bmiText(b) {
  const v = Number(b)
  if (!v) return '—'
  if (v < 18.5) return '偏瘦'
  if (v < 24) return '正常'
  if (v < 28) return '超重'
  return '肥胖'
}
function formatXLabel(d) {
  if (!d) return ''
  return dayjs(d).format(rangeKey.value === '7d' ? 'MM-DD' : 'MM-DD')
}

async function loadToday() {
  try {
    today.value = await bodyDataApi.getToday()
  } catch (e) {
    today.value = null
  }
}

async function loadTrend() {
  try {
    const res = await bodyDataApi.getTrend({ range: rangeKey.value })
    summary.value = res?.summary || null
    points.value = res?.points || []
  } catch (e) {
    showError(e.message || '加载趋势失败')
  }
}

function openUpsertModal() {
  if (!hasHeight.value) {
    showError('请先在「个人资料」设置身高')
    router.push('/profile')
    return
  }
  form.weight = today.value ? Number(today.value.weight) : null
  form.remark = today.value?.remark || ''
  upsertOpen.value = true
}

async function onUpsert() {
  if (form.weight == null) {
    showError('请填写体重')
    return
  }
  saving.value = true
  try {
    await bodyDataApi.upsert({
      weight: form.weight,
      remark: form.remark
    })
    showSuccess(today.value ? '已更新' : '已记录')
    upsertOpen.value = false
    await Promise.all([loadToday(), loadTrend()])
  } catch (e) {
    showError(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadToday(), loadTrend()])
})
</script>

<style lang="scss" scoped>
.body-data-page { display: flex; flex-direction: column; gap: 16px; max-width: 1080px; margin: 0 auto; }

/* 今日卡 */
.today-card {
  background:
    radial-gradient(800px 300px at 0% 0%, rgba(16, 185, 129, 0.10), transparent 60%),
    radial-gradient(600px 300px at 100% 100%, rgba(14, 165, 233, 0.10), transparent 60%);
}
.today-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}
.today-left { display: flex; flex-direction: column; gap: 4px; }
.today-label { font-size: 13px; color: var(--text-tertiary); }
.today-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}
.today-unit {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-tertiary);
  margin-left: 6px;
}
.today-bmi {
  font-size: 13px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  strong { color: var(--color-primary); font-weight: 700; }
}
.bmi-tag { border-radius: 999px !important; padding-inline: 10px !important; }

/* 趋势卡 */
.trend-card :deep(.ant-card-body) { padding: 0 24px 24px; }
.trend-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  flex-wrap: wrap;
  gap: 12px;
}
.summary-row { margin-bottom: 8px !important; }
.summary-cell {
  padding: 16px;
  background: var(--color-bg-soft);
  border-radius: var(--radius-md);
  text-align: center;
  border: 1px solid var(--border-color);
}
.summary-label { font-size: 12px; color: var(--text-tertiary); }
.summary-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-top: 2px;
  &.is-up   { color: var(--color-danger); }
  &.is-down { color: var(--color-success); }
}
.summary-unit {
  font-size: 12px;
  font-weight: 400;
  color: var(--text-tertiary);
  margin-left: 4px;
}

.chart-wrap { margin-top: 12px; }
.empty-chart { padding: 40px 0; }
.chart-area { position: relative; }
.chart-area svg { width: 100%; height: 240px; }
.x-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 11px;
  color: var(--text-tertiary);
}
.x-label {
  flex: 1;
  text-align: center;
  &:first-child { text-align: left; }
  &:last-child { text-align: right; }
  &.is-edge { font-weight: 600; color: var(--text-secondary); }
}
</style>