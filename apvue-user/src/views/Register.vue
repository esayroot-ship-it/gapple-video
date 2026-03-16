<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, NCard, NForm, NFormItem, NInput, NButton } from 'naive-ui'
import { Service } from '../api/user/services/Service'

const router = useRouter()
const message = useMessage()

const formValue = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)

const handleRegister = async () => {
  if (!formValue.value.username || !formValue.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  if (formValue.value.password !== formValue.value.confirmPassword) {
    message.warning('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    const res = await Service.register(formValue.value.username, formValue.value.password)
    if (res.code === 1) {
      message.success('注册成功，请登录')
      router.push({ name: 'login' })
    } else {
      message.error(res.message || '注册失败')
    }
  } catch (error: any) {
    message.error(error.message || '注册出错')
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="register-container">
    <div class="register-content">
      <div class="header">
        <h1 class="platform-name">影视平台</h1>
        <p class="platform-desc">加入我们，探索无限视听可能</p>
      </div>
      <n-card class="register-card">
        <template #header>
          <span class="card-title">新用户注册</span>
        </template>
        <n-form size="large">
          <n-form-item label="用户名">
            <n-input v-model:value="formValue.username" placeholder="请输入用户名" />
          </n-form-item>
          <n-form-item label="密码">
            <n-input
              v-model:value="formValue.password"
              type="password"
              show-password-on="click"
              placeholder="请输入密码"
            />
          </n-form-item>
          <n-form-item label="确认密码">
            <n-input
              v-model:value="formValue.confirmPassword"
              type="password"
              show-password-on="click"
              placeholder="请再次输入密码"
              @keydown.enter.prevent="handleRegister"
            />
          </n-form-item>
          <div class="actions">
            <n-button type="primary" @click="handleRegister" :loading="loading" block size="large">
              立即注册
            </n-button>
            <div class="links">
              <n-button text @click="goToLogin">已有账号？返回登录</n-button>
            </div>
          </div>
        </n-form>
      </n-card>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
  background-size: cover;
}

.register-content {
  width: 100%;
  max-width: 450px;
  padding: 20px;
  z-index: 1;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.platform-name {
  font-size: 36px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.platform-desc {
  font-size: 16px;
  color: #5d6d7e;
  margin-top: 10px;
}

.register-card {
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

:deep(.n-card-header), :deep(.n-card__content) {
    background-color: transparent !important;
}

.card-title {
  font-weight: 600;
  font-size: 20px;
  color: #34495e;
}

:deep(.n-input) {
  background-color: rgba(255, 255, 255, 0.5) !important;
}
:deep(.n-input:hover), :deep(.n-input:focus-within) {
  background-color: rgba(255, 255, 255, 0.8) !important;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 10px;
}

.links {
  text-align: center;
}

.links :deep(.n-button) {
  font-size: 14px;
  color: #5d6d7e;
}
.links :deep(.n-button:hover) {
    color: #2c3e50;
}
</style>
