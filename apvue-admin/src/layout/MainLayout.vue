<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-aside width="220px" class="aside">
        <div class="logo">
          <span>AP Admin</span>
        </div>
        <el-menu
          active-text-color="#409EFF"
          background-color="#304156"
          class="el-menu-vertical"
          text-color="#bfcbd9"
          :default-active="activeMenu"
          router
        >
          <el-menu-item index="/video">
            <el-icon><VideoPlay /></el-icon>
            <span>视频管理</span>
          </el-menu-item>
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

           <el-sub-menu index="1">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span>运营管理</span>
            </template>
            <el-menu-item index="/announcement">公告管理</el-menu-item>
            <el-menu-item index="/banner">轮播图管理</el-menu-item>
            <el-menu-item index="/feedback">反馈管理</el-menu-item>
            <el-menu-item index="/hot">热搜管理</el-menu-item>
          </el-sub-menu>

           <el-sub-menu index="2">
            <template #title>
              <el-icon><Film /></el-icon>
              <span>资源管理</span>
            </template>
            <el-menu-item index="/category">分类管理</el-menu-item>
            <el-menu-item index="/series">剧集管理</el-menu-item>
            <el-menu-item index="/episode">分集管理</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin-list">
             <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </el-menu-item>

        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <!-- 面包屑或其他 -->
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="el-dropdown-link">
                管理员 <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { VideoPlay, User, Menu, ArrowDown, Film, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const logout = () => {
  sessionStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  background-color: #2b2f3a;
  color: #fff;
}

.el-menu-vertical {
  border-right: none;
  flex: 1;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.header-right {
  cursor: pointer;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>