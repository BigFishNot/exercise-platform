<template>
  <div class="mail-log-page">
    <a-card :bordered="false">
      <a-form layout="inline" :model="query" class="search-form">
        <a-form-item label="状态">
          <a-select v-model:value="query.status" placeholder="全部" allow-clear style="width: 120px">
            <a-select-option :value="1">成功</a-select-option>
            <a-select-option :value="2">失败</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="模板分类">
          <a-select v-model:value="query.templateType" placeholder="全部" allow-clear style="width: 140px">
            <a-select-option :value="1">未打卡</a-select-option>
            <a-select-option :value="2">时长不足</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="触发">
          <a-select v-model:value="query.triggerType" placeholder="全部" allow-clear style="width: 120px">
            <a-select-option :value="1">定时</a-select-option>
            <a-select-option :value="2">手动</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="用户账号">
          <a-input v-model:value="query.userAccount" allow-clear placeholder="模糊匹配" style="width: 180px" />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="onSearch"><SearchOutlined /> 查询</a-button>
            <a-button @click="onReset"><ReloadOutlined /> 重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="false"
        row-key="logId"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'userId'">
            <span class="text-secondary">{{ record.userAccount || record.userId }}</span>
            <div class="text-tertiary" style="font-size: 12px">{{ record.userEmail || '—' }}</div>
          </template>
          <template v-else-if="column.dataIndex === 'typeName'">
            <a-tag :color="record.templateType === 1 ? 'orange' : 'gold'">{{ record.typeName }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'error'">
              <span class="status-dot" :class="record.status === 1 ? 'is-on' : 'is-off'" />
              {{ record.statusName }}
            </a-tag>
            <div v-if="record.errorSummary" class="text-tertiary err-tip" :title="record.errorSummary">
              {{ record.errorSummary }}
            </div>
          </template>
          <template v-else-if="column.dataIndex === 'triggerName'">
            <a-tag :color="record.triggerType === 1 ? 'blue' : 'purple'">{{ record.triggerName }}</a-tag>
            <div v-if="record.operatorId" class="text-tertiary" style="font-size: 12px">
              by {{ record.operatorAccount || record.operatorId.slice(0, 8) }}
            </div>
          </template>
          <template v-else-if="column.dataIndex === 'renderedTitle'">
            <span class="text-secondary title-tip" :title="record.renderedTitle">
              {{ record.renderedTitle || '—' }}
            </span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { mailLogApi } from '@/api/mail'
import { showError } from '@/utils/message'

const dataSource = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({
  pageNum: 1,
  pageSize: 20,
  status: undefined,
  templateType: undefined,
  triggerType: undefined,
  userAccount: ''
})

const columns = [
  { title: '用户', dataIndex: 'userId', width: 200 },
  { title: '模板', dataIndex: 'typeName', width: 120 },
  { title: '渲染标题', dataIndex: 'renderedTitle', ellipsis: true },
  { title: '触发', dataIndex: 'triggerName', width: 140 },
  { title: '状态', dataIndex: 'status', width: 180 },
  { title: '业务日期', dataIndex: 'sendDate', width: 120 },
  { title: '发送时间', dataIndex: 'createTime', width: 170 }
]

async function loadData() {
  loading.value = true
  try {
    const res = await mailLogApi.loadDataList({ ...query })
    dataSource.value = res?.list || []
    total.value = res?.total || 0
  } catch (e) {
    showError(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function onSearch() { query.pageNum = 1; loadData() }
function onReset() { query.status = undefined; query.templateType = undefined; query.triggerType = undefined; query.userAccount = ''; onSearch() }
function onPageChange(p) { query.pageNum = p; loadData() }
function onPageSizeChange(p, s) { query.pageNum = p; query.pageSize = s; loadData() }

onMounted(loadData)
</script>

<style lang="scss" scoped>
.list-pagination { margin-top: 16px; text-align: right; }
.status-dot {
  width: 6px; height: 6px; border-radius: 50%; display: inline-block; margin-right: 4px;
  &.is-on  { background: var(--color-success); }
  &.is-off { background: var(--color-danger); }
}
.err-tip { max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.title-tip { display: inline-block; max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; vertical-align: middle; }
</style>