<template>
  <div class="exercise-type-page">
    <!-- KPI 卡片 -->
    <a-row :gutter="16" class="kpi-row">
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--primary">
          <div class="kpi-icon"><AppstoreOutlined /></div>
          <div class="kpi-body">
            <div class="kpi-label">类型总数</div>
            <div class="kpi-value">{{ stats.total }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--success">
          <div class="kpi-icon"><CheckCircleFilled /></div>
          <div class="kpi-body">
            <div class="kpi-label">启用中</div>
            <div class="kpi-value">{{ stats.enabled }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--warning">
          <div class="kpi-icon"><StopOutlined /></div>
          <div class="kpi-body">
            <div class="kpi-label">已停用</div>
            <div class="kpi-value">{{ stats.disabled }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--info">
          <div class="kpi-icon"><FieldTimeOutlined /></div>
          <div class="kpi-body">
            <div class="kpi-label">最大档位（分钟）</div>
            <div class="kpi-value">{{ stats.maxMinutes }}</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <a-card class="list-card" :bordered="false">
      <div class="list-toolbar">
        <div class="list-title">
          <h3>运动类型</h3>
          <span class="list-sub">仅室内运动，按排序权重升序展示</span>
        </div>
        <div class="list-toolbar-actions">
          <a-button type="primary" @click="onAdd">
            <PlusOutlined /> 新增类型
          </a-button>
        </div>
      </div>

      <a-form layout="inline" :model="query" class="search-form">
        <a-form-item label="名称">
          <a-input
            v-model:value="query.nameFuzzy"
            placeholder="请输入名称"
            allow-clear
            style="width: 200px"
            @pressEnter="onSearch"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="query.status"
            placeholder="全部"
            allow-clear
            style="width: 140px"
            @change="onSearch"
          >
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">停用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="onSearch">
              <SearchOutlined /> 查询
            </a-button>
            <a-button @click="onReset">
              <ReloadOutlined /> 重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="false"
        row-key="typeId"
        class="user-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'name'">
            <div class="cell-type">
              <div class="cell-icon">
                <component :is="resolveIcon(record.icon)" />
              </div>
              <div class="cell-text">
                <div class="cell-name">{{ record.name }}</div>
                <div class="cell-id">{{ record.typeId.slice(0, 8) }}…</div>
              </div>
            </div>
          </template>

          <template v-else-if="column.dataIndex === 'defaultSeconds'">
            <span class="text-secondary">{{ formatSeconds(record.defaultSeconds) }}</span>
          </template>

          <template v-else-if="column.dataIndex === 'durationLevels'">
            <div class="level-list">
              <a-tag
                v-for="(lv, i) in parseLevels(record.durationLevels)"
                :key="i"
                color="processing"
                class="level-tag"
              >
                {{ lv.label }}
              </a-tag>
              <span v-if="!parseLevels(record.durationLevels).length" class="text-tertiary">—</span>
            </div>
          </template>

          <template v-else-if="column.dataIndex === 'caloriesPerMinute'">
            <span class="text-secondary">{{ record.caloriesPerMinute }} kcal/分</span>
          </template>

          <template v-else-if="column.dataIndex === 'refCount'">
            <a-tag v-if="record.refCount > 0" color="warning">{{ record.refCount }} 次</a-tag>
            <span v-else class="text-tertiary">0</span>
          </template>

          <template v-else-if="column.dataIndex === 'sort'">
            <span class="text-secondary">{{ record.sort }}</span>
          </template>

          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'" class="status-pill">
              <span class="status-dot" :class="record.status === 1 ? 'is-on' : 'is-off'" />
              {{ record.statusName }}
            </a-tag>
          </template>

          <template v-else-if="column.dataIndex === 'updateTime'">
            <span class="text-secondary">{{ record.updateTime || '—' }}</span>
          </template>

          <template v-else-if="column.dataIndex === 'action'">
            <a-space :size="8">
              <a @click="onEdit(record)">
                <EditOutlined /> 编辑
              </a>
              <a-divider type="vertical" />
              <a @click="onToggleStatus(record)">
                <component :is="record.status === 1 ? PauseOutlined : PlayCircleOutlined" />
                {{ record.status === 1 ? '停用' : '启用' }}
              </a>
              <a-divider type="vertical" />
              <a-popconfirm
                :title="`确认删除「${record.name}」?`"
                ok-text="确定"
                cancel-text="取消"
                @confirm="onDelete(record)"
              >
                <a class="text-danger">
                  <DeleteOutlined /> 删除
                </a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>

      <a-pagination
        class="list-pagination"
        :current="query.pageNum"
        :page-size="query.pageSize"
        :total="total"
        show-size-changer
        show-quick-jumper
        :show-total="(t) => `共 ${t} 条`"
        @change="onPageChange"
        @showSizeChange="onPageSizeChange"
      />
    </a-card>

    <ExerciseTypeEditModal ref="editRef" @saved="loadData" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, h } from 'vue'
import {
  AppstoreOutlined,
  CheckCircleFilled,
  StopOutlined,
  FieldTimeOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  PauseOutlined,
  PlayCircleOutlined,
  DeleteOutlined,
  ThunderboltOutlined,
  RocketOutlined,
  RiseOutlined,
  PauseCircleOutlined,
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
import { showSuccess, showError, $confirm } from '@/utils/message'
import ExerciseTypeEditModal from './ExerciseTypeEditModal.vue'

const loading = ref(false)
const dataSource = ref([])
const total = ref(0)
const editRef = ref()

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  nameFuzzy: '',
  status: undefined
})

const stats = computed(() => {
  const enabled = dataSource.value.filter((u) => u.status === 1).length
  const disabled = dataSource.value.filter((u) => u.status === 0).length
  const maxSec = dataSource.value
    .map((u) => u.defaultSeconds || 0)
    .reduce((a, b) => Math.max(a, b), 0)
  return {
    total: total.value,
    enabled,
    disabled,
    maxMinutes: maxSec ? Math.round(maxSec / 60) : 0
  }
})

const columns = [
  { title: '运动类型', dataIndex: 'name', width: 220 },
  { title: '默认时长', dataIndex: 'defaultSeconds', width: 130 },
  { title: '时长档位', dataIndex: 'durationLevels', width: 280 },
  { title: '卡路里', dataIndex: 'caloriesPerMinute', width: 110 },
  { title: '引用次数', dataIndex: 'refCount', width: 100 },
  { title: '排序', dataIndex: 'sort', width: 80 },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '更新时间', dataIndex: 'updateTime', width: 170 },
  { title: '操作', dataIndex: 'action', width: 260, fixed: 'right' }
]

const ICON_MAP = {
  ThunderboltOutlined, RocketOutlined, RiseOutlined, PauseOutlined,
  ReloadOutlined: ReloadIcon, CaretUpOutlined, SwapOutlined, CompressOutlined,
  ExpandOutlined, FireOutlined, HeartOutlined, TrophyOutlined,
  PauseCircleOutlined
}
function resolveIcon(name) {
  return ICON_MAP[name] || ThunderboltOutlined
}

function formatSeconds(v) {
  if (!v) return '—'
  if (v < 60) return `${v} 秒`
  if (v % 60 === 0) return `${v / 60} 分钟`
  return `${Math.floor(v / 60)} 分 ${v % 60} 秒`
}

function parseLevels(json) {
  if (!json) return []
  try {
    const arr = JSON.parse(json)
    return Array.isArray(arr) ? arr : []
  } catch {
    return []
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await exerciseTypeApi.loadDataList({ ...query })
    dataSource.value = res?.list || []
    total.value = res?.total || 0
  } catch (e) {
    showError(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  query.pageNum = 1
  loadData()
}
function onReset() {
  query.nameFuzzy = ''
  query.status = undefined
  onSearch()
}
function onPageChange(page) {
  query.pageNum = page
  loadData()
}
function onPageSizeChange(page, size) {
  query.pageNum = page
  query.pageSize = size
  loadData()
}
function onAdd() {
  editRef.value?.open(null)
}
function onEdit(record) {
  editRef.value?.open(record)
}
async function onDelete(record) {
  try {
    await exerciseTypeApi.remove(record.typeId)
    showSuccess('已删除')
    loadData()
  } catch (e) {
    showError(e.message || '删除失败')
  }
}
async function onToggleStatus(record) {
  const next = record.status === 1 ? 0 : 1
  const actionText = next === 1 ? '启用' : '停用'
  $confirm({
    title: `确认${actionText}「${record.name}」?`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await exerciseTypeApi.updateStatus(record.typeId, next)
        showSuccess(`${actionText}成功`)
        loadData()
      } catch (e) {
        showError(e.message || `${actionText}失败`)
      }
    }
  })
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.exercise-type-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* KPI */
.kpi-row { margin-bottom: 0 !important; }
.kpi-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-xs);
  transition: transform 0.2s ease-out, box-shadow 0.2s ease-out;
  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
  }
}
.kpi-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.kpi-card--primary .kpi-icon { background: var(--color-primary-light); color: var(--color-primary); }
.kpi-card--success .kpi-icon { background: var(--color-success-light); color: var(--color-success); }
.kpi-card--warning .kpi-icon { background: var(--color-warning-light); color: var(--color-warning); }
.kpi-card--info .kpi-icon { background: var(--color-info-light); color: var(--color-info); }

.kpi-body { display: flex; flex-direction: column; }
.kpi-label { font-size: 13px; color: var(--text-tertiary); }
.kpi-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  margin-top: 2px;
}

/* 列表 */
.list-card :deep(.ant-card-body) { padding: 20px 24px; }
.list-toolbar {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
}
.list-title h3 { margin: 0; font-size: 18px; font-weight: 600; }
.list-sub { margin-left: 12px; color: var(--text-tertiary); font-size: 13px; }

.search-form {
  margin-bottom: 16px;
  :deep(.ant-form-item) { margin-bottom: 12px; }
}

.user-table :deep(.ant-table-thead > tr > th) {
  font-weight: 600;
  color: var(--text-secondary);
}

/* 单元格 */
.cell-type { display: flex; align-items: center; gap: 12px; }
.cell-icon {
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
.cell-text { display: flex; flex-direction: column; line-height: 1.3; }
.cell-name { font-weight: 600; color: var(--text-primary); }
.cell-id { font-size: 12px; color: var(--text-tertiary); }

.level-list { display: flex; flex-wrap: wrap; gap: 4px; }
.level-tag { border-radius: 6px; }

.status-pill {
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

.list-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>