<template>
  <div class="mail-config-page">
    <a-card :bordered="false" title="邮件 SMTP 配置（QQ 邮箱默认）">
      <a-alert
        v-if="!vo?.passwordConfigured"
        type="warning"
        show-icon
        class="mb-16"
        message="尚未配置 SMTP 密码"
        description="QQ 邮箱需要在【设置 → 账户 → POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务】开启 SMTP 服务并获取 16 位授权码。授权码不是 QQ 登录密码。"
      />

      <a-form
        :model="form"
        :rules="rules"
        ref="formRef"
        layout="vertical"
        :label-col="{ style: { width: '160px' } }"
        style="max-width: 720px"
      >
        <a-form-item label="SMTP 主机" name="smtpHost">
          <a-input v-model:value="form.smtpHost" placeholder="如 smtp.qq.com" />
        </a-form-item>
        <a-form-item label="SMTP 端口" name="smtpPort">
          <a-input-number v-model:value="form.smtpPort" :min="1" :max="65535" style="width: 200px" />
        </a-form-item>
        <a-form-item label="是否 SSL" name="useSsl">
          <a-switch v-model:checked="useSslBool" checked-children="SSL" un-checked-children="STARTTLS" />
        </a-form-item>
        <a-form-item label="SMTP 账号" name="smtpUsername">
          <a-input v-model:value="form.smtpUsername" placeholder="如 123456@qq.com" />
        </a-form-item>
        <a-form-item label="SMTP 授权码" name="smtpPassword">
          <a-input-password
            v-model:value="form.smtpPassword"
            :placeholder="vo?.passwordConfigured ? '留空表示不修改' : '请输入 16 位授权码'"
          />
        </a-form-item>
        <a-form-item label="发件人显示名">
          <a-input v-model:value="form.sender" placeholder="如 运动平台" />
        </a-form-item>
        <a-form-item label="每日发送时间点" extra="HH:mm 逗号分隔，1-6 个；如 09:00,18:00">
          <a-input v-model:value="form.sendTimePoints" placeholder="如 09:00,18:00" />
        </a-form-item>
        <a-form-item label="启用总开关" name="enabled">
          <a-switch v-model:checked="enabledBool" checked-children="启用" un-checked-children="停用" />
        </a-form-item>

        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="saving" @click="onSave">保存配置</a-button>
            <a-button @click="loadData">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { mailConfigApi } from '@/api/mail'
import { showSuccess, showError } from '@/utils/message'

const vo = ref(null)
const saving = ref(false)
const formRef = ref()

const form = reactive({
  smtpHost: 'smtp.qq.com',
  smtpPort: 465,
  useSsl: 1,
  smtpUsername: '',
  smtpPassword: '',
  sender: '运动平台',
  sendTimePoints: '09:00,18:00',
  enabled: 1
})

const useSslBool = ref(true)
const enabledBool = ref(true)
watch(useSslBool, (v) => form.useSsl = v ? 1 : 0)
watch(enabledBool, (v) => form.enabled = v ? 1 : 0)

const rules = {
  smtpHost: [{ required: true, message: '请输入 SMTP 主机' }],
  smtpPort: [{ required: true, message: '请输入 SMTP 端口' }],
  smtpUsername: [{ required: true, message: '请输入 SMTP 账号' }]
}

async function loadData() {
  try {
    const data = await mailConfigApi.get()
    vo.value = data
    if (data) {
      form.smtpHost = data.smtpHost || 'smtp.qq.com'
      form.smtpPort = data.smtpPort || 465
      form.useSsl = data.useSsl ?? 1
      form.smtpUsername = data.smtpUsername || ''
      form.sender = data.sender || '运动平台'
      form.sendTimePoints = data.sendTimePoints || ''
      form.enabled = data.enabled ?? 1
      form.smtpPassword = ''
      useSslBool.value = form.useSsl === 1
      enabledBool.value = form.enabled === 1
    }
  } catch (e) {
    showError(e.message || '加载失败')
  }
}

async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await mailConfigApi.update({ ...form })
    showSuccess('已保存')
    loadData()
  } catch (e) {
    showError(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.mail-config-page { max-width: 960px; margin: 0 auto; }
.mb-16 { margin-bottom: 16px; }
</style>