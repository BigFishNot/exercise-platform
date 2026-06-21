<template>
  <div class="plan-create-page">
    <a-card :bordered="false">
      <div class="form-header">
        <h2>创建阶段计划</h2>
        <p class="text-tertiary">为接下来的运动设定一个可达成的小目标，1-60 天</p>
      </div>

      <a-alert
        v-if="hasOngoing"
        type="warning"
        show-icon
        class="mb-16"
        message="你已有一个进行中的阶段计划"
        description="新建计划前请先取消或结束当前计划。"
      />

      <a-form
        :model="form"
        :rules="rules"
        ref="formRef"
        layout="vertical"
        :disabled="hasOngoing"
        @finish="onSubmit"
      >
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="开始日期" name="startDate">
              <a-date-picker
                v-model:value="form.startDate"
                :disabled-date="disableStart"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                size="large"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="结束日期" name="endDate">
              <a-date-picker
                v-model:value="form.endDate"
                :disabled-date="disableEnd"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                size="large"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="每日目标时长" name="dailyTargetMinutes" extra="1-600 分钟">
          <a-slider
            v-model:value="form.dailyTargetMinutes"
            :min="5"
            :max="180"
            :step="5"
            :marks="marks"
          />
          <div class="target-preview">
            <span class="big">{{ form.dailyTargetMinutes }}</span>
            <span class="unit">分钟 / 天</span>
            <a-tag :color="levelColor" class="ml-12">{{ levelText }}</a-tag>
          </div>
        </a-form-item>

        <a-form-item label="备注（可选）" name="remark">
          <a-textarea
            v-model:value="form.remark"
            :rows="3"
            placeholder="例如：本阶段专注跳绳 + 拉伸"
            maxlength="200"
            show-count
          />
        </a-form-item>

        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" size="large" :loading="saving" :disabled="hasOngoing">
              创建计划
            </a-button>
            <a-button size="large" @click="$router.back()">返回</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { exercisePlanApi } from '@/api/exercisePlan'
import { showSuccess, showError } from '@/utils/message'

const router = useRouter()

const formRef = ref()
const saving = ref(false)
const hasOngoing = ref(false)

const form = reactive({
  startDate: dayjs().format('YYYY-MM-DD'),
  endDate: dayjs().add(13, 'day').format('YYYY-MM-DD'),
  dailyTargetMinutes: 30,
  remark: ''
})

const rules = {
  startDate: [{ required: true, message: '请选择开始日期' }],
  endDate: [{ required: true, message: '请选择结束日期' }],
  dailyTargetMinutes: [{ required: true, type: 'number', message: '请选择每日目标' }]
}

const marks = {
  5: '5',
  30: '30',
  60: '60',
  90: '90',
  120: '120',
  180: '180'
}

const levelText = computed(() => {
  const m = form.dailyTargetMinutes
  if (m < 15) return '轻松'
  if (m < 30) return '适中'
  if (m < 60) return '推荐'
  if (m < 90) return '进阶'
  return '挑战'
})
const levelColor = computed(() => {
  const m = form.dailyTargetMinutes
  if (m < 15) return 'cyan'
  if (m < 30) return 'blue'
  if (m < 60) return 'green'
  if (m < 90) return 'orange'
  return 'red'
})

function disableStart(d) {
  return d && d.isBefore(dayjs().startOf('day'))
}
function disableEnd(d) {
  if (!d) return false
  if (d.isBefore(dayjs().startOf('day'))) return true
  if (form.startDate) {
    const start = dayjs(form.startDate)
    if (d.isBefore(start)) return true
    if (d.diff(start, 'day') > 59) return true
  }
  return false
}

onMounted(async () => {
  try {
    const curr = await exercisePlanApi.getCurrent()
    hasOngoing.value = !!curr
  } catch (e) { /* 忽略 */ }
})

async function onSubmit() {
  saving.value = true
  try {
    // 直接传 yyyy-MM-dd 字符串，避免被序列化为 ISO 8601 导致 Date 反序列化失败
    const payload = {
      ...form,
      startDate: form.startDate,
      endDate: form.endDate
    }
    await exercisePlanApi.add(payload)
    showSuccess('阶段计划已创建，加油！')
    router.replace('/plan')
  } catch (e) {
    showError(e.message || '创建失败')
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.plan-create-page { max-width: 720px; margin: 0 auto; }
.form-header { margin-bottom: 24px;
  h2 { font-size: 22px; font-weight: 700; margin: 0 0 6px; }
  p { margin: 0; font-size: 14px; }
}
.mb-16 { margin-bottom: 16px; }
.ml-12 { margin-left: 12px; }
.target-preview {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-top: 8px;
  .big { font-size: 32px; font-weight: 700; color: var(--color-primary); }
  .unit { font-size: 14px; color: var(--text-secondary); }
}
</style>