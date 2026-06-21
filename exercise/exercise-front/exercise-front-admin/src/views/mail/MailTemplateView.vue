<template>
  <div class="mail-template-page">
    <a-card :bordered="false">
      <div class="list-toolbar">
        <div class="list-title">
          <h3>邮件模板</h3>
          <span class="list-sub">每种类型一条模板；占位符：{nickName} {date} {actualMinutes} {targetMinutes}</span>
        </div>
        <a-button type="primary" @click="openUpsert(null)">
          <PlusOutlined /> 新增模板
        </a-button>
      </div>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="false"
        row-key="templateId"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'typeName'">
            <a-tag :color="record.templateType === 1 ? 'orange' : 'gold'">{{ record.typeName }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'enabled'">
            <a-tag :color="record.enabled === 1 ? 'success' : 'default'">
              {{ record.enabled === 1 ? '启用' : '停用' }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-space :size="8">
              <a @click="openUpsert(record)"><EditOutlined /> 编辑</a>
              <a-popconfirm
                title="确认删除该模板？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="onDelete(record)"
              >
                <a class="text-danger"><DeleteOutlined /> 删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="upsertOpen"
      :title="form.templateId ? '编辑模板' : '新增模板'"
      width="720px"
      :confirm-loading="saving"
      @ok="onUpsert"
    >
      <a-form :model="form" :rules="rules" ref="formRef" layout="vertical">
        <a-form-item label="模板分类" name="templateType">
          <a-select v-model:value="form.templateType" :disabled="!!form.templateId" placeholder="选择分类">
            <a-select-option :value="1">今日未打卡</a-select-option>
            <a-select-option :value="2">今日时长不足</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="名称" name="name">
          <a-input v-model:value="form.name" placeholder="如：未打卡提醒 v2" />
        </a-form-item>
        <a-form-item label="邮件标题" name="title">
          <a-input v-model:value="form.title" placeholder="支持占位符" />
        </a-form-item>
        <a-form-item label="邮件正文" name="content">
          <a-textarea v-model:value="form.content" :rows="8" placeholder="支持占位符：{nickName} {date} {actualMinutes} {targetMinutes}" />
        </a-form-item>
        <a-form-item label="启用" name="enabled">
          <a-switch v-model:checked="enabledBool" checked-children="启用" un-checked-children="停用" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { mailTemplateApi } from '@/api/mail'
import { showSuccess, showError } from '@/utils/message'

const dataSource = ref([])
const loading = ref(false)
const saving = ref(false)
const upsertOpen = ref(false)
const formRef = ref()

const form = reactive({
  templateId: null,
  templateType: 1,
  name: '',
  title: '',
  content: '',
  enabled: 1
})
const enabledBool = ref(true)
watch(enabledBool, (v) => form.enabled = v ? 1 : 0)

const rules = {
  templateType: [{ required: true, message: '请选择分类' }],
  name: [{ required: true, message: '请输入名称' }],
  title: [{ required: true, message: '请输入标题' }],
  content: [{ required: true, message: '请输入正文' }]
}

const columns = [
  { title: '分类', dataIndex: 'typeName', width: 140 },
  { title: '名称', dataIndex: 'name', width: 200 },
  { title: '标题', dataIndex: 'title', ellipsis: true },
  { title: '启用', dataIndex: 'enabled', width: 100 },
  { title: '更新时间', dataIndex: 'updateTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 180, fixed: 'right' }
]

async function loadData() {
  loading.value = true
  try {
    dataSource.value = (await mailTemplateApi.loadDataList()) || []
  } catch (e) {
    showError(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function openUpsert(record) {
  if (record) {
    Object.assign(form, {
      templateId: record.templateId,
      templateType: record.templateType,
      name: record.name,
      title: record.title,
      content: record.content,
      enabled: record.enabled
    })
    enabledBool.value = record.enabled === 1
  } else {
    Object.assign(form, {
      templateId: null,
      templateType: 1,
      name: '',
      title: '',
      content: '',
      enabled: 1
    })
    enabledBool.value = true
  }
  upsertOpen.value = true
}

async function onUpsert() {
  await formRef.value.validate()
  saving.value = true
  try {
    await mailTemplateApi.upsert({ ...form })
    showSuccess('已保存')
    upsertOpen.value = false
    loadData()
  } catch (e) {
    showError(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function onDelete(record) {
  try {
    await mailTemplateApi.remove(record.templateId)
    showSuccess('已删除')
    loadData()
  } catch (e) {
    showError(e.message || '删除失败')
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.list-toolbar {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 16px;
}
.list-title h3 { margin: 0; font-size: 18px; font-weight: 600; }
.list-sub { margin-left: 12px; color: var(--text-tertiary); font-size: 13px; }
</style>