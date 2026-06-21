<template>
  <div class="exercise-start-page">
    <!-- 顶部今日累计 -->
    <a-row :gutter="16" class="kpi-row">
      <a-col :xs="12" :md="8">
        <div class="kpi-card">
          <div class="kpi-label">今日累计</div>
          <div class="kpi-value">{{ formatMinutes(todayActual) }}<span></span></div>
        </div>
      </a-col>
      <a-col :xs="12" :md="8">
        <div class="kpi-card kpi-card--primary">
          <div class="kpi-label">今日完成次数</div>
          <div class="kpi-value">{{ todayDoneCount }}<span>次</span></div>
        </div>
      </a-col>
      <a-col :xs="12" :md="8">
        <div class="kpi-card kpi-card--success">
          <div class="kpi-label">今日消耗</div>
          <div class="kpi-value">{{ todayCalories }}<span>kcal</span></div>
        </div>
      </a-col>
    </a-row>

    <!-- 进行中：倒计时面板 -->
    <a-card v-if="active" class="active-card" :bordered="false">
      <div class="active-inner">
        <div class="active-icon">
          <component :is="resolveIcon(active.typeIcon)" />
        </div>
        <div class="active-title">
          <a-tag color="processing" class="status-pill">进行中</a-tag>
          <h2>{{ active.typeName }}</h2>
          <p class="text-tertiary">开始时间：{{ active.startTime }}</p>
        </div>

        <div class="countdown-wrap">
          <a-progress
            type="circle"
            :percent="progressPct"
            :stroke-color="progressColor"
            :size="180"
            :status="paused ? 'normal' : (progressPct >= 100 ? 'success' : 'active')"
          >
            <template #format>
              <div class="cd-text">
                <div class="cd-time">{{ remainingDisplay }}</div>
                <div class="cd-sub">
                  {{ paused ? '已暂停' : '剩余' }}
                </div>
              </div>
            </template>
          </a-progress>
        </div>

        <a-space wrap class="active-actions">
          <a-button
            :type="paused ? 'default' : 'primary'"
            :danger="paused"
            size="large"
            @click="togglePause"
          >
            <component :is="paused ? PlayCircleFilled : PauseCircleFilled" />
            {{ paused ? '继续' : '暂停' }}
          </a-button>
          <a-button type="primary" size="large" @click="onFinish" :disabled="paused">
            <CheckCircleFilled /> 完成运动
          </a-button>
          <a-popconfirm
            title="确认放弃本次运动？放弃后不计入打卡"
            ok-text="确认放弃"
            cancel-text="再练会儿"
            ok-button-props="{ danger: true }"
            @confirm="onAbandon"
          >
            <a-button danger size="large">
              <CloseCircleFilled /> 放弃
            </a-button>
          </a-popconfirm>
        </a-space>
      </div>
    </a-card>

    <!-- 准备开始：选类型 + 选时长档 -->
    <a-card v-else class="setup-card" :bordered="false">
      <div class="setup-inner">
        <h2 class="setup-title">选一个运动开始吧</h2>

        <div class="form-row">
          <div class="form-label">运动类型</div>
          <div class="type-grid">
            <div
              v-for="t in typeOptions"
              :key="t.typeId"
              class="type-tile"
              :class="{ 'is-active': selectedType?.typeId === t.typeId }"
              @click="selectType(t)"
            >
              <div class="type-icon">
                <component :is="resolveIcon(t.icon)" />
              </div>
              <div class="type-name">{{ t.name }}</div>
            </div>
          </div>
        </div>

        <div v-if="selectedType" class="form-row">
          <div class="form-label">选择时长</div>
          <div class="level-grid">
            <a-button
              v-for="lv in levelOptions"
              :key="lv.seconds"
              size="large"
              :type="selectedSeconds === lv.seconds ? 'primary' : 'default'"
              @click="selectedSeconds = lv.seconds"
            >
              {{ lv.label }}
            </a-button>
            <a-button
              size="large"
              :type="customMode ? 'primary' : 'default'"
              @click="enterCustomMode"
            >
              <EditOutlined /> 自定义
            </a-button>
          </div>
          <div v-if="customMode" class="custom-row">
            <a-input-number
              v-model:value="customMinutes"
              :min="1"
              :max="120"
              addon-after="分钟"
              style="width: 200px"
            />
            <a-button type="primary" @click="confirmCustom">使用</a-button>
            <a-button @click="cancelCustom">取消</a-button>
          </div>
        </div>

        <a-empty v-if="!typeOptions.length" description="暂无可用运动类型" />

        <a-button
          v-if="selectedType && selectedSeconds"
          type="primary"
          size="large"
          block
          class="start-btn"
          @click="onStart"
          :loading="starting"
        >
          <PlayCircleFilled /> 开始运动
        </a-button>
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, h } from 'vue'
import {
  PlayCircleFilled,
  PauseCircleFilled,
  CheckCircleFilled,
  CloseCircleFilled,
  EditOutlined,
  ThunderboltOutlined,
  RocketOutlined,
  RiseOutlined,
  PauseOutlined,
  ReloadOutlined as ReloadIcon,
  CaretUpOutlined,
  SwapOutlined,
  CompressOutlined,
  ExpandOutlined,
  FireOutlined,
  HeartOutlined,
  TrophyOutlined
} from '@ant-design/icons-vue'
import { exerciseTypeApi } from '@/api/exerciseType'
import { exerciseRecordApi } from '@/api/exerciseRecord'
import { showSuccess, showError, $confirm } from '@/utils/message'

const typeOptions = ref([])
const selectedType = ref(null)
const selectedSeconds = ref(null)
const customMode = ref(false)
const customMinutes = ref(10)
const starting = ref(false)

const active = ref(null)
const elapsed = ref(0) // 已进行秒数（前端累加）
const paused = ref(false) // 暂停状态
const autoFinishing = ref(false) // 防止自动完成被重复触发
let timer = null

const todayActual = ref(0)
const todayDoneCount = ref(0)
const todayCalories = ref(0)

const ICON_MAP = {
  ThunderboltOutlined, RocketOutlined, RiseOutlined, PauseOutlined,
  ReloadOutlined: ReloadIcon, CaretUpOutlined, SwapOutlined, CompressOutlined,
  ExpandOutlined, FireOutlined, HeartOutlined, TrophyOutlined
}
function resolveIcon(name) {
  return ICON_MAP[name] || FireOutlined
}

const levelOptions = computed(() => {
  if (!selectedType.value?.durationLevels) return []
  try {
    const arr = JSON.parse(selectedType.value.durationLevels)
    return Array.isArray(arr) ? arr : []
  } catch { return [] }
})

const remainingDisplay = computed(() => {
  const sec = Math.max(0, (active.value?.planSeconds || 0) - elapsed.value)
  const m = Math.floor(sec / 60).toString().padStart(2, '0')
  const s = (sec % 60).toString().padStart(2, '0')
  return `${m}:${s}`
})
const progressPct = computed(() => {
  if (!active.value?.planSeconds) return 0
  return Math.min(100, Math.round((elapsed.value / active.value.planSeconds) * 100))
})
const progressColor = computed(() => {
  const p = progressPct.value
  if (p >= 95) return { from: '#10b981', to: '#0ea5e9' }
  if (p >= 50) return { from: '#0ea5e9', to: '#6366f1' }
  return { from: '#10b981', to: '#34d399' }
})

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

function selectType(t) {
  selectedType.value = t
  // 默认选第一个时长档
  const first = levelOptions.value[0]
  selectedSeconds.value = first?.seconds || (t.defaultSeconds || 60)
  customMode.value = false
}
function enterCustomMode() {
  customMode.value = true
}
function cancelCustom() {
  customMode.value = false
}
function confirmCustom() {
  const sec = Math.max(1, Math.min(120, Number(customMinutes.value) || 0)) * 60
  if (sec < 10 || sec > 7200) {
    showError('时长需在 10-7200 秒之间')
    return
  }
  selectedSeconds.value = sec
  customMode.value = false
}

async function loadTypeOptions() {
  try {
    const list = await exerciseTypeApi.getOptions()
    typeOptions.value = list || []
    if (typeOptions.value.length && !selectedType.value) {
      selectType(typeOptions.value[0])
    }
  } catch (e) {
    showError(e.message || '加载运动类型失败')
  }
}

async function loadActive() {
  try {
    const data = await exerciseRecordApi.getActive()
    if (data) {
      active.value = data
      paused.value = false
      const startMs = new Date(data.startTime.replace(/-/g, '/')).getTime()
      elapsed.value = Math.floor((Date.now() - startMs) / 1000)
      startTimer()
    } else {
      active.value = null
      paused.value = false
      stopTimer()
    }
  } catch (e) {
    active.value = null
  }
}

function startTimer() {
  stopTimer()
  if (paused.value) return
  timer = setInterval(() => {
    elapsed.value += 1
  }, 1000)
}
function stopTimer() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}
function togglePause() {
  paused.value = !paused.value
  if (paused.value) {
    stopTimer()
  } else {
    startTimer()
  }
}

/** 倒计时归零自动完成（无确认弹框） */
async function autoFinish() {
  if (autoFinishing.value) return
  if (!active.value) return
  autoFinishing.value = true
  stopTimer()
  try {
    const res = await exerciseRecordApi.finish({
      recordId: active.value.recordId,
      actualSeconds: elapsed.value
    })
    if (res?.status === 2) {
      showSuccess('时间到！已自动完成并计入打卡')
    } else {
      showSuccess('时间到！已自动完成')
    }
    active.value = null
    elapsed.value = 0
    paused.value = false
    await loadTodaySummary()
  } catch (e) {
    showError(e.message || '自动完成失败')
  } finally {
    autoFinishing.value = false
  }
}

/** 倒计时 watcher：归零时触发自动完成 */
watch(
  () => elapsed.value,
  (val) => {
    if (active.value && !paused.value && val >= active.value.planSeconds) {
      autoFinish()
    }
  }
)

async function loadTodaySummary() {
  try {
    const list = await exerciseRecordApi.listToday()
    let totalSec = 0
    let cal = 0
    let done = 0
    for (const r of (list || [])) {
      if (r.status === 2) {
        totalSec += r.actualSeconds || 0
        cal += Number(r.calories || 0)
        done += 1
      }
    }
    todayActual.value = totalSec
    todayCalories.value = Math.round(cal * 100) / 100
    todayDoneCount.value = done
  } catch (e) { /* 忽略 */ }
}

async function onStart() {
  if (!selectedType.value || !selectedSeconds.value) return
  starting.value = true
  try {
    await exerciseRecordApi.start({
      typeId: selectedType.value.typeId,
      planSeconds: selectedSeconds.value
    })
    showSuccess('开始啦，加油！')
    await loadActive()
    await loadTodaySummary()
  } catch (e) {
    showError(e.message || '开始失败')
  } finally {
    starting.value = false
  }
}

async function onFinish() {
  if (!active.value) return
  $confirm({
    title: '完成本次运动？',
    content: `已坚持 ${formatMinutes(elapsed.value)}，达到 95% 计入打卡`,
    okText: '完成',
    cancelText: '再练会儿',
    onOk: async () => {
      try {
        const res = await exerciseRecordApi.finish({
          recordId: active.value.recordId,
          actualSeconds: elapsed.value
        })
        if (res?.status === 2) showSuccess('完成！已计入打卡')
        else showSuccess('已记录，但时长未达标，记为放弃')
        stopTimer()
        active.value = null
        elapsed.value = 0
        paused.value = false
        await loadTodaySummary()
      } catch (e) {
        showError(e.message || '完成失败')
      }
    }
  })
}

async function onAbandon() {
  if (!active.value) return
  try {
    await exerciseRecordApi.abandon({ recordId: active.value.recordId })
    showSuccess('已放弃')
    stopTimer()
    active.value = null
    elapsed.value = 0
    paused.value = false
  } catch (e) {
    showError(e.message || '放弃失败')
  }
}

onMounted(async () => {
  await Promise.all([loadTypeOptions(), loadActive(), loadTodaySummary()])
})
onUnmounted(stopTimer)
</script>

<style lang="scss" scoped>
.exercise-start-page { display: flex; flex-direction: column; gap: 16px; }

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
}
.kpi-label { font-size: 13px; color: var(--text-tertiary); }
.kpi-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  span { font-size: 12px; font-weight: 400; color: var(--text-tertiary); margin-left: 4px; }
}
.kpi-card--primary .kpi-value { color: var(--color-primary); }
.kpi-card--success .kpi-value { color: var(--color-success); }

/* Active 面板 */
.active-card {
  background:
    radial-gradient(800px 300px at 0% 0%, rgba(16, 185, 129, 0.10), transparent 60%),
    radial-gradient(600px 300px at 100% 100%, rgba(14, 165, 233, 0.10), transparent 60%),
    linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
}
.active-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 16px 0 8px;
}
.active-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.30);
}
.active-title { text-align: center;
  h2 { font-size: 22px; font-weight: 700; margin: 8px 0 4px; }
  p { margin: 0; font-size: 13px; }
}
.status-pill { border-radius: 999px !important; padding-inline: 12px !important; }

.countdown-wrap { margin: 8px 0; }
.cd-text { text-align: center; }
.cd-time {
  font-size: 36px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  color: var(--text-primary);
}
.cd-sub { font-size: 12px; color: var(--text-tertiary); margin-top: 2px; }

.active-actions { justify-content: center; }

/* Setup 面板 */
.setup-card { min-height: 420px; }
.setup-inner { max-width: 720px; margin: 0 auto; }
.setup-title { font-size: 22px; font-weight: 700; margin: 0 0 20px; text-align: center; }

.form-row { margin-bottom: 20px; }
.form-label { font-size: 13px; color: var(--text-secondary); margin-bottom: 8px; font-weight: 500; }

.type-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(96px, 1fr));
  gap: 10px;
}
.type-tile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 8px;
  background: var(--color-surface);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s;
  &:hover { transform: translateY(-2px); box-shadow: var(--shadow-sm); }
  &.is-active {
    background: var(--color-primary-light);
    border-color: var(--color-primary);
    color: var(--color-primary);
  }
}
.type-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-accent-light));
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}
.type-tile.is-active .type-icon {
  background: var(--color-primary);
  color: #fff;
}
.type-name { font-size: 12px; font-weight: 500; }

.level-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
.custom-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}

.start-btn {
  margin-top: 12px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}
</style>