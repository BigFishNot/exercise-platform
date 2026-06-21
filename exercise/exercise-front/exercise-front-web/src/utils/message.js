import { message, Modal } from 'ant-design-vue'

export const $message = message
export const $confirm = Modal.confirm

export function showError(msg) {
  message.error(msg || '操作失败')
}

export function showSuccess(msg) {
  message.success(msg || '操作成功')
}