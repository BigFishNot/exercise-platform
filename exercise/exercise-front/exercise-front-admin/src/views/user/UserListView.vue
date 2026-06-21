<template>
  <div class="user-list-page">
    <!-- KPI 卡片 -->
    <a-row :gutter="16" class="kpi-row">
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--primary">
          <div class="kpi-icon"><TeamOutlined /></div>
          <div class="kpi-body">
            <div class="kpi-label">用户总数</div>
            <div class="kpi-value">{{ stats.total }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--success">
          <div class="kpi-icon"><CheckCircleFilled /></div>
          <div class="kpi-body">
            <div class="kpi-label">正常用户</div>
            <div class="kpi-value">{{ stats.enabled }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--warning">
          <div class="kpi-icon"><StopOutlined /></div>
          <div class="kpi-body">
            <div class="kpi-label">停用用户</div>
            <div class="kpi-value">{{ stats.disabled }}</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :md="6">
        <div class="kpi-card kpi-card--info">
          <div class="kpi-icon"><UserOutlined /></div>
          <div class="kpi-body">
            <div class="kpi-label">普通用户</div>
            <div class="kpi-value">{{ stats.normalUser }}</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 列表卡片 -->
    <a-card class="list-card" :bordered="false">
      <div class="list-toolbar">
        <div class="list-title">
          <h3>用户列表</h3>
          <span class="list-sub">管理后台注册 / 启停的用户</span>
        </div>
        <div class="list-toolbar-actions">
          <a-button type="primary" disabled>
            <PlusOutlined />
            新增用户
          </a-button>
        </div>
      </div>

      <!-- 搜索区 -->
      <a-form layout="inline" :model="query" class="search-form">
        <a-form-item label="昵称">
          <a-input
            v-model:value="query.nickNameFuzzy"
            placeholder="请输入昵称"
            allow-clear
            style="width: 180px"
            @pressEnter="onSearch"
          />
        </a-form-item>
        <a-form-item label="账号">
          <a-input
            v-model:value="query.account"
            placeholder="请输入账号"
            allow-clear
            style="width: 180px"
            @pressEnter="onSearch"
          />
        </a-form-item>
        <a-form-item label="角色">
          <a-select
            v-model:value="query.roleType"
            placeholder="全部"
            allow-clear
            style="width: 140px"
            @change="onSearch"
          >
            <a-select-option :value="1">系统管理员</a-select-option>
            <a-select-option :value="2">普通用户</a-select-option>
          </a-select>
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
              <SearchOutlined />查询
            </a-button>
            <a-button @click="onReset">
              <ReloadOutlined />重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="false"
        row-key="userId"
        class="user-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'account'">
            <div class="cell-account">
              <a-avatar :size="32" class="cell-avatar">
                {{ (record.nickName || record.account || '?').slice(0, 1).toUpperCase() }}
              </a-avatar>
              <div class="cell-account-text">
                <div class="cell-account-name">{{ record.nickName || '—' }}</div>
                <div class="cell-account-id">{{ record.account }}</div>
              </div>
            </div>
          </template>

          <template v-else-if="column.dataIndex === 'email'">
            <span class="text-secondary">{{ record.email || '—' }}</span>
          </template>

          <template v-else-if="column.dataIndex === 'gender'">
            <span class="text-secondary">{{ record.genderName }}</span>
          </template>

          <template v-else-if="column.dataIndex === 'body'">
            <span class="cell-body">
              <span>{{ record.height || '—' }} cm</span>
              <span class="cell-body-divider">·</span>
              <span>{{ record.weight || '—' }} kg</span>
              <a-tag v-if="record.bmi" :color="bmiColor(record.bmi)" class="cell-bmi">
                BMI {{ record.bmi }}
              </a-tag>
            </span>
          </template>

          <template v-else-if="column.dataIndex === 'roleType'">
            <a-tag :color="record.roleType === 1 ? 'blue' : 'default'" class="role-pill">
              <component :is="record.roleType === 1 ? CrownFilled : UserOutlined" />
              {{ record.roleTypeName }}
            </a-tag>
          </template>

          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'" class="status-pill">
              <span class="status-dot" :class="record.status === 1 ? 'is-on' : 'is-off'" />
              {{ record.statusName }}
            </a-tag>
          </template>

          <template v-else-if="column.dataIndex === 'registerTime'">
            <span class="text-secondary">{{ record.registerTime || '—' }}</span>
          </template>

          <template v-else-if="column.dataIndex === 'action'">
            <a-space :size="8">
              <a @click="onView(record)">
                <EyeOutlined /> 查看
              </a>
              <a-divider type="vertical" />
              <a @click="onToggleStatus(record)">
                <component :is="record.status === 1 ? PauseOutlined : PlayCircleOutlined" />
                {{ record.status === 1 ? '停用' : '启用' }}
              </a>
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

    <UserDetailModal ref="detailRef" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import {
  TeamOutlined,
  CheckCircleFilled,
  StopOutlined,
  UserOutlined,
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  PauseOutlined,
  PlayCircleOutlined,
  CrownFilled
} from '@ant-design/icons-vue'
import { userInfoApi } from '@/api/userInfo'
import { showSuccess, showError, $confirm } from '@/utils/message'
import UserDetailModal from './UserDetailModal.vue'

const loading = ref(false)
const dataSource = ref([])
const total = ref(0)
const detailRef = ref()

const query = reactive({
  pageNum: 1,
  pageSize: 20,
  nickNameFuzzy: '',
  account: '',
  roleType: undefined,
  status: undefined
})

const stats = computed(() => ({
  total: total.value,
  enabled: dataSource.value.filter((u) => u.status === 1).length,
  disabled: dataSource.value.filter((u) => u.status === 0).length,
  normalUser: dataSource.value.filter((u) => u.roleType === 2).length
}))

const columns = [
  { title: '用户', dataIndex: 'account', width: 220 },
  { title: '邮箱', dataIndex: 'email', width: 200, ellipsis: true },
  { title: '性别', dataIndex: 'gender', width: 80 },
  { title: '身高 / 体重 / BMI', dataIndex: 'body', width: 260 },
  { title: '角色', dataIndex: 'roleType', width: 120 },
  { title: '状态', dataIndex: 'status', width: 110 },
  { title: '注册时间', dataIndex: 'registerTime', width: 170 },
  { title: '操作', dataIndex: 'action', width: 200, fixed: 'right' }
]

function bmiColor(bmi) {
  const v = Number(bmi)
  if (v < 18.5) return 'gold'
  if (v < 24) return 'green'
  if (v < 28) return 'orange'
  return 'red'
}

async function loadData() {
  loading.value = true
  try {
    const res = await userInfoApi.loadDataList({ ...query })
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
  query.nickNameFuzzy = ''
  query.account = ''
  query.roleType = undefined
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
function onView(record) {
  detailRef.value?.open(record.userId)
}
async function onToggleStatus(record) {
  const next = record.status === 1 ? 0 : 1
  const actionText = next === 1 ? '启用' : '停用'
  $confirm({
    title: `确认${actionText}该用户?`,
    content: `账号：${record.account}`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await userInfoApi.updateStatus(record.userId, next)
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
.user-list-page {
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

/* 列表卡片 */
.list-card :deep(.ant-card-body) { padding: 20px 24px; }
.list-toolbar {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
}
.list-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}
.list-sub {
  margin-left: 12px;
  color: var(--text-tertiary);
  font-size: 13px;
}

.search-form {
  margin-bottom: 16px;
  :deep(.ant-form-item) { margin-bottom: 12px; }
  :deep(.ant-form-item-label > label) {
    color: var(--text-secondary);
    font-weight: 500;
  }
}

.user-table :deep(.ant-table-thead > tr > th) {
  font-weight: 600;
  color: var(--text-secondary);
}

/* 单元格 */
.cell-account { display: flex; align-items: center; gap: 12px; }
.cell-avatar {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%) !important;
  color: #fff !important;
  font-weight: 600;
}
.cell-account-text { display: flex; flex-direction: column; line-height: 1.3; }
.cell-account-name { font-weight: 600; color: var(--text-primary); }
.cell-account-id { font-size: 12px; color: var(--text-tertiary); }

.cell-body {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary);
}
.cell-body-divider { color: var(--text-tertiary); }
.cell-bmi { margin-left: 8px; border-radius: 6px; }

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

.list-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>