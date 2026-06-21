<template>
  <a-modal
    v-model:open="visible"
    :title="isEdit ? '编辑运动类型' : '新增运动类型'"
    :confirm-loading="saving"
    @ok="onSubmit"
    @cancel="onCancel"
    ok-text="保存"
    cancel-text="取消"
    width="560px"
    destroy-on-close
  >
    <a-form
      ref="formRef"
      :model="form"
      :rules="rules"
      layout="vertical"
      class="edit-form"
    >
      <a-form-item label="名称" name="name">
        <a-input v-model:value="form.name" placeholder="如：跳绳" allow-clear maxlength="32" show-count />
      </a-form-item>

      <a-form-item label="图标" name="icon">
        <a-select
          v-model:value="form.icon"
          placeholder="选择图标"
          allow-clear
          show-search
          :options="iconOptions"
        />
      </a-form-item>

      <a-row :gutter="12">
        <a-col :span="12">
          <a-form-item label="默认倒计时（秒）" name="defaultSeconds">
            <a-input-number
              v-model:value="form.defaultSeconds"
              :min="10"
              :max="7200"
              style="width: 100%"
              placeholder="10-7200"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="排序权重" name="sort">
            <a-input-number v-model:value="form.sort" :min="0" :max="999" style="width: 100%" />
          </a-form-item>
        </a-col>
      </a-row>

      <a-form-item label="每分钟消耗卡路里" name="caloriesPerMinute">
        <a-input-number
          v-model:value="form.caloriesPerMinute"
          :min="0"
          :max="100"
          :precision="2"
          style="width: 100%"
          placeholder="如：8.00"
        />
      </a-form-item>

      <a-form-item label="时长档位" extra="可选，输入秒数后回车添加；可拖拽排序">
        <a-select
          mode="tags"
          v-model:value="durationChips"
          :token-separators="[',']"
          placeholder="输入秒数，回车添加"
          @change="onChipsChange"
        >
          <template #tagRender="{ label }">
            <a-tag closable color="processing">{{ formatSeconds(label.value) }}</a-tag>
          </template>
        </a-select>
        <div class="duration-hint">已添加 {{ durationChips.length }} 个档位</div>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { exerciseTypeApi } from '@/api/exerciseType'
import { showSuccess, showError } from '@/utils/message'

const props = defineProps({})
const emit = defineEmits(['saved'])

const visible = ref(false)
const saving = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  typeId: null,
  name: '',
  icon: null,
  defaultSeconds: 60,
  durationLevels: null,
  caloriesPerMinute: 0,
  sort: 0
})
const durationChips = ref([])

const rules = {
  name: [{ required: true, message: '请输入名称', max: 32 }],
  defaultSeconds: [{ required: true, message: '请输入默认倒计时' }],
  icon: [{ required: true, message: '请选择图标' }],
  caloriesPerMinute: [{ required: true, message: '请输入卡路里' }]
}

const iconOptions = [
  { value: 'ThunderboltOutlined', label: '⚡ 闪电' },
  { value: 'RocketOutlined',      label: '🚀 火箭' },
  { value: 'RiseOutlined',        label: '📈 上升' },
  { value: 'PauseOutlined',       label: '⏸ 暂停' },
  { value: 'ReloadOutlined',      label: '🔄 循环' },
  { value: 'CaretUpOutlined',     label: '▲ 上箭头' },
  { value: 'SwapOutlined',        label: '⇄ 交换' },
  { value: 'CompressOutlined',    label: '⤓ 压缩' },
  { value: 'ExpandOutlined',      label: '⤢ 展开' },
  { value: 'FireOutlined',        label: '🔥 火焰' },
  { value: 'HeartOutlined',       label: '❤ 心' },
  { value: 'TrophyOutlined',      label: '🏆 奖杯' }
]

function formatSeconds(v) {
  const n = Number(v)
  if (!Number.isFinite(n)) return v
  if (n < 60) return `${n} 秒`
  if (n % 60 === 0) return `${n / 60} 分钟`
  return `${Math.floor(n / 60)} 分 ${n % 60} 秒`
}

function onChipsChange(values) {
  // mode="tags" 给的是字符串数组，转 number 并去重
  const arr = Array.from(new Set(values.map((v) => Number(v)).filter((n) => Number.isFinite(n) && n > 0)))
  durationChips.value = arr.map(String)
}

function open(record) {
  visible.value = true
  isEdit.value = !!record
  if (record) {
    form.typeId = record.typeId
    form.name = record.name
    form.icon = record.icon
    form.defaultSeconds = record.defaultSeconds
    form.caloriesPerMinute = Number(record.caloriesPerMinute || 0)
    form.sort = record.sort || 0
    try {
      const arr = record.durationLevels ? JSON.parse(record.durationLevels) : []
      durationChips.value = (Array.isArray(arr) ? arr : [])
        .map((it) => String(it.seconds))
        .filter(Boolean)
    } catch {
      durationChips.value = []
    }
  } else {
    form.typeId = null
    form.name = ''
    form.icon = null
    form.defaultSeconds = 60
    form.caloriesPerMinute = 0
    form.sort = 0
    durationChips.value = []
  }
}

function onCancel() {
  visible.value = false
}

async function onSubmit() {
  await formRef.value.validate()
  saving.value = true
  try {
    const durationLevels = durationChips.value.map((v, idx) => ({
      label: formatSeconds(v),
      seconds: Number(v),
      sort: idx + 1
    }))
    const payload = {
      typeId: form.typeId,
      name: form.name,
      icon: form.icon,
      defaultSeconds: form.defaultSeconds,
      caloriesPerMinute: form.caloriesPerMinute,
      sort: form.sort,
      durationLevels: JSON.stringify(durationLevels)
    }
    if (isEdit.value) {
      await exerciseTypeApi.update(payload)
      showSuccess('已保存')
    } else {
      await exerciseTypeApi.add(payload)
      showSuccess('已新增')
    }
    visible.value = false
    emit('saved')
  } catch (e) {
    if (e?.errorFields) return
    showError(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

defineExpose({ open })
</script>

<style lang="scss" scoped>
.edit-form :deep(.ant-form-item) { margin-bottom: 16px; }
.duration-hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-tertiary);
}
</style>