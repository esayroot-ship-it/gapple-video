<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, NCard, NForm, NFormItem, NInput, NButton } from 'naive-ui'
import { Service } from '../api/user/services/Service'
import { setUser } from '../utils/auth'

const router = useRouter()
const message = useMessage()

const formValue = ref({
  username: '',
  password: ''
})

const loading = ref(false)


const handleLogin = async () => {
  if (!formValue.value.username || !formValue.value.password) {
    message.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const res = await Service.login(formValue.value.username, formValue.value.password)
    if (res.code === 1) {
      message.success('登录成功')
      const data = res.data as any
      if (data) {
          setUser({
              id: data.id || 0,
              username: data.username || formValue.value.username,
              nickname: data.nickname,
              avatarUrl: data.avatarUrl
          })
          router.push({ name: 'home' })
      } else {
          message.warning('登录返回数据异常')
      }
    } else {
      message.error(res.message || '登录失败')
    }
  } catch (error: any) {
    message.error(error.message || '登录出错')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push({ name: 'register' })
}
</script>

<template>
  <div class="login-container">
    <div class="login-content">
      <div class="header">
        <h1 class="platform-name">金苹果影视</h1>
        <p class="platform-desc">开启您的精彩视界</p>
      </div>
      <n-card class="login-card">
        <template #header>
          <span class="card-title">用户登录</span>
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
              @keydown.enter.prevent="handleLogin"
            />
          </n-form-item>
          <div class="actions">
            <n-button type="primary" @click="handleLogin" :loading="loading" block size="large">
              立即登录
            </n-button>
            <div class="links">
              <n-button text @click="goToRegister">还没有账号？立即注册</n-button>
            </div>
          </div>
        </n-form>
      </n-card>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background: url('../assets/login.jpg');
  background-size: cover;
}

.login-content {
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

.login-card {
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
