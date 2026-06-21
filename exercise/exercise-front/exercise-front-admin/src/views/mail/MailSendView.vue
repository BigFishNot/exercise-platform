<template>
  <div class="mail-send-page">
    <a-card :bordered="false" title="手动发送邮件">
      <a-alert
        type="info"
        show-icon
        class="mb-16"
        message="发送规则"
        description="同一用户同一模板当日仅发送一次；邮箱缺失的用户会被跳过。发送结果可在「发送日志」查看。"
      />

      <a-form layout="vertical" :label-col="{ style: { width: '120px' } }" style="max-width: 720px">
        <a-form-item label="邮件模板">
          <a-select v-model:value="form.templateId" placeholder="选择模板">
            <a-select-option v-for="t in templates" :key="t.templateId" :value="t.templateId">
              {{ t.typeName }} - {{ t.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="接收用户">
          <a-select
            v-model:value="form.userIds"
            mode="multiple"
            show-search
            :filter-option="filterUser"
            placeholder="选择一个或多个用户"
            style="width: 100%"
          >
            <a-select-option v-for="u in userList" :key="u.userId" :value="u.userId">
              {{ u.nickName }}（{{ u.account }}）{{ u.email ? '· ' + u.email : '· 无邮箱' }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注（可选）">
          <a-input v-model:value="form.remark" placeholder="例如：周一活动提醒" />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="sending" :disabled="!canSend" @click="onSend">
              <SendOutlined /> 立即发送
            </a-button>
            <a-button @click="loadData">刷新</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 发送结果 -->
    <a-card v-if="result" :bordered="false" title="发送结果" class="mt-16">
      <a-row :gutter="16">
        <a-col :span="8">
          <a-statistic title="成功" :value="result.sentCount || 0" :value-style="{ color: '#10b981' }" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="跳过" :value="result.skippedCount || 0" :value-style="{ color: '#94a3b8' }" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="失败" :value="result.failedCount || 0" :value-style="{ color: '#ef4444' }" />
        </a-col>
      </a-row>

      <a-divider v-if="(result.skipped && result.skipped.length) || (result.failed && result.failed.length)" />

      <div v-if="result.skipped && result.skipped.length" class="result-block">
        <h4>跳过用户（{{ result.skipped.length }}）</h4>
        <a-table
          :data-source="result.skipped"
          :pagination="false"
          row-key="userId"
          size="small"
          :columns="[
            { title: '账号', dataIndex: 'account' },
            { title: '原因', dataIndex: 'reason' }
          ]"
        />
      </div>

      <div v-if="result.failed && result.failed.length" class="result-block">
        <h4>失败用户（{{ result.failed.length }}）</h4>
        <a-table
          :data-source="result.failed"
          :pagination="false"
          row-key="userId"
          size="small"
          :columns="[
            { title: '账号', dataIndex: 'account' },
            { title: '错误', dataIndex: 'error' }
          ]"
        />
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { SendOutlined } from '@ant-design/icons-vue'
import { mailTemplateApi, mailSendApi } from '@/api/mail'
import { userInfoApi } from '@/api/userInfo'
import { showSuccess, showError } from '@/utils/message'

const templates = ref([])
const userList = ref([])
const sending = ref(false)
const result = ref(null)

const form = reactive({
  templateId: undefined,
  userIds: [],
  remark: ''
})

const canSend = computed(() => form.templateId && form.userIds.length > 0)

function filterUser(input, option) {
  const text = (option.children && option.children[0]) || ''
  return text.toLowerCase().includes(input.toLowerCase())
}

async function loadData() {
  try {
    const [ts, us] = await Promise.all([
      mailTemplateApi.loadDataList(),
      userInfoApi.loadDataList({ pageNum: 1, pageSize: 200, status: 1 })
    ])
    templates.value = (ts || []).filter(t => t.enabled === 1)
    userList.value = us?.list || []
  } catch (e) {
    showError(e.message || '加载失败')
  }
}

async function onSend() {
  if (!canSend.value) {
    showError('请选择模板和至少一个用户')
    return
  }
  sending.value = true
  result.value = null
  try {
    const res = await mailSendApi.send({ ...form })
    result.value = res
    showSuccess(`已发送：成功 ${res.sentCount} / 跳过 ${res.skippedCount} / 失败 ${res.failedCount}`)
  } catch (e) {
    showError(e.message || '发送失败')
  } finally {
    sending.value = false
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.mb-16 { margin-bottom: 16px; }
.mt-16 { margin-top: 16px; }
.result-block { margin-top: 12px; }
.result-block h4 { margin: 0 0 8px; font-size: 14px; color: var(--text-secondary); }
</style>