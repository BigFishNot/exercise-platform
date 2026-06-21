<template>
  <div class="brand-logo" :class="{ 'brand-logo--inverse': inverse }">
    <div class="brand-mark">
      <ThunderboltFilled />
    </div>
    <div class="brand-text">
      <div class="brand-name">运动平台</div>
      <div class="brand-tag">{{ tagline }}</div>
    </div>
  </div>
</template>

<script setup>
import { ThunderboltFilled } from '@ant-design/icons-vue'

defineProps({
  tagline: { type: String, default: '让每一次坚持都看得见' },
  inverse: { type: Boolean, default: false }
})
</script>

<style lang="scss" scoped>
.brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}
.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: #fff;
  font-size: 22px;
  box-shadow: 0 6px 18px rgba(16, 185, 129, 0.35);
}
.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}
.brand-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 0.5px;
}
.brand-tag {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-top: 2px;
}
.brand-logo--inverse .brand-name { color: #ffffff; }
.brand-logo--inverse .brand-tag { color: rgba(255, 255, 255, 0.78); }
.brand-logo--inverse .brand-mark {
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(8px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
}
</style>