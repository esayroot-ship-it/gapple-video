<script setup lang="ts">
import { ref, watch, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  NCard, NGrid, NGridItem, NEmpty, NInput, NIcon, NButton, NTabs, NTabPane, NSpin, useMessage
} from 'naive-ui'
import { Service } from '../api/user/services/Service'
import type { video } from '../api/user/models/video'
import type { series } from '../api/user/models/series'
import type { episode } from '../api/user/models/episode'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const keyword = ref('')
const activeTab = ref('video') // 'video' | 'series'
const loading = ref(false)
const videos = ref<video[]>([])
const seriesList = ref<series[]>([])
const searched = ref(false)

// Icons
const SearchIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('circle', { cx: '11', cy: '11', r: '8' }),
  h('line', { x1: '21', y1: '21', x2: '16.65', y2: '16.65' })
])

const ArrowLeftIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('line', { x1: '19', y1: '12', x2: '5', y2: '12' }),
  h('polyline', { points: '12 19 5 12 12 5' })
])

const doSearch = async () => {
  if (!keyword.value.trim()) return
  
  loading.value = true
  searched.value = true
  videos.value = []
  seriesList.value = []

  try {
    if (activeTab.value === 'video') {
      const res = await Service.getVideoByTitle(keyword.value)
      if (res.code === 1) {
        videos.value = (res.data as video[]) || []
      }
    } else {
      const res = await Service.getSeriesByTitle(keyword.value)
      if (res.code === 1) {
        seriesList.value = (res.data as series[]) || []
      }
    }
  } catch (e) {
    console.error(e)
    message.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = (val: string) => {
  activeTab.value = val
  doSearch()
}

const onSearchInput = () => {
  router.push({ query: { ...route.query, keyword: keyword.value } })
}

const goBack = () => {
  if (window.history.state && window.history.state.back) {
    router.back()
  } else {
    router.push({ name: 'home' })
  }
}

const handleVideoJump = async (item: video) => {
  if (!item.id) return
  let seriesId = 0
  
  try {
    const sRes = await Service.getSeriesByVideoId(item.id)
    if (sRes.code === 1) {
      const sData = sRes.data;
      const epData = Array.isArray(sData) ? sData[0] : (sData as episode);
      if (epData?.sid) {
          seriesId = epData.sid;
      }
    }
  } catch (e) {
    console.error('Failed to get series for video', e)
  }

  router.push({ 
    name: 'video', 
    params: { seriesId: String(seriesId) }, 
    query: { videoId: item.id } 
  })
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}


watch(
  () => route.query.keyword,
  (newVal) => {
    if (newVal) {
      keyword.value = String(newVal)
      doSearch()
    }
  },
  { immediate: true }
)

</script>

<template>
  <div class="search-page">
    <!-- Header -->
    <header class="search-header">
      <div class="header-left">
        <n-button circle secondary @click="goBack" class="back-btn">
          <template #icon><n-icon :component="ArrowLeftIcon" /></template>
        </n-button>
      </div>
      <div class="search-input-wrapper">
        <n-input 
          v-model:value="keyword" 
          placeholder="搜索视频或剧集..." 
          round 
          clearable
          size="large"
          @keydown.enter="onSearchInput"
        >
          <template #prefix>
            <n-icon :component="SearchIcon" />
          </template>
        </n-input>
        <n-button type="primary" round class="search-btn" @click="onSearchInput" :disabled="loading">
          搜索
        </n-button>
      </div>
    </header>

    <!-- Content -->
    <main class="search-content">
      <div class="tabs-wrapper">
        <n-tabs v-model:value="activeTab" type="segment" animated @update:value="handleTabChange">
          <n-tab-pane name="video" tab="视频" />
          <n-tab-pane name="series" tab="剧集" />
        </n-tabs>
      </div>

      <div class="results-container">
        <div v-if="loading" class="loading-state">
          <n-spin size="large" />
        </div>

        <div v-else-if="!searched">
           <div class="empty-state">
             <n-icon size="48" color="#ccc" :component="SearchIcon" />
             <p>输入关键词开始搜索</p>
           </div>
        </div>

        <div v-else>
           <!-- Video Results -->
           <div v-if="activeTab === 'video'">
              <n-grid v-if="videos.length > 0" x-gap="20" y-gap="24" cols="2 s:3 m:4 l:5" responsive="screen">
                <n-grid-item v-for="item in videos" :key="item.id">
                  <n-card hoverable class="result-card" @click="handleVideoJump(item)">
                    <template #cover>
                      <div class="video-cover">
                         <img :src="item.coverUrl || 'https://via.placeholder.com/300x169?text=Video'" class="cover-img" />
                         <div class="play-overlay">▶</div>
                      </div>
                    </template>
                    <div class="card-meta">
                        <div class="card-title" v-html="item.title"></div> <!-- Simple title render -->
                        <div class="card-desc">{{ item.description || '暂无简介' }}</div>
                    </div>
                  </n-card>
                </n-grid-item>
              </n-grid>
              <n-empty v-else description="未找到相关视频" />
           </div>

           <!-- Series Results -->
           <div v-if="activeTab === 'series'">
              <n-grid v-if="seriesList.length > 0" x-gap="20" y-gap="24" cols="2 s:3 m:4 l:5" responsive="screen">
                <n-grid-item v-for="item in seriesList" :key="item.id">
                  <n-card hoverable class="result-card" @click="item.id && router.push({ name: 'video', params: { seriesId: item.id } })">
                    <template #cover>
                      <div class="series-cover">
                         <img :src="item.coverUrl || 'https://via.placeholder.com/200x300?text=Series'" class="cover-img" />
                      </div>
                    </template>
                    <div class="card-meta">
                        <div class="card-title">{{ item.title }}</div>
                        <div class="card-time">{{ formatDate(item.createdtime) }}</div>
                    </div>
                  </n-card>
                </n-grid-item>
              </n-grid>
               <n-empty v-else description="未找到相关剧集" />
           </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: transparent;
  color: var(--text-primary);
}

.search-header {
  height: 80px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  gap: 20px;
}

.search-input-wrapper {
  flex: 1;
  max-width: 600px;
  display: flex;
  gap: 12px;
}

.search-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.tabs-wrapper {
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
}

:deep(.n-tabs-rail) {
  background-color: var(--bg-input);
}

.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: var(--text-tertiary);
  gap: 16px;
}

.result-card {
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
}

.result-card:hover {
  transform: translateY(-4px);
}

.video-cover {
  width: 100%;
  aspect-ratio: 16 / 9;
  position: relative;
  overflow: hidden;
}

.series-cover {
  width: 100%;
  aspect-ratio: 2 / 3;
  position: relative;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  opacity: 0;
  transition: opacity 0.2s;
}

.result-card:hover .play-overlay {
  opacity: 1;
}

.card-meta {
  padding: 12px;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-desc, .card-time {
  font-size: 12px;
  color: var(--text-tertiary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.n-card) {
  background-color: transparent;
}
</style>
