<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { 
  NCarousel, NButton, 
  NGrid, NGridItem, NCard, NFloatButton, NModal, NInput, NIcon, useMessage, 
  NDropdown, NAvatar, NSkeleton, NEmpty, NSelect
} from 'naive-ui'
import { Service } from '../api/user/services/Service'
import { getUser, removeUser } from '../utils/auth'

const router = useRouter()
const message = useMessage()
const user = ref(getUser())

const SearchIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('circle', { cx: '11', cy: '11', r: '8' }),
  h('line', { x1: '21', y1: '21', x2: '16.65', y2: '16.65' })
])

const UserIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('path', { d: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' }),
  h('circle', { cx: '12', cy: '7', r: '4' })
])

const MessageSquareIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('path', { d: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z' })
])

const LogOutIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('path', { d: 'M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4' }),
  h('polyline', { points: '16 17 21 12 16 7' }),
  h('line', { x1: '21', y1: '12', x2: '9', y2: '12' })
])


const banners = ref<Array<any>>([])
const categories = ref<Array<any>>([])
const categorySeriesMap = ref<Record<number, Array<any>>>({})
const hotList = ref<Array<any>>([])
const announcements = ref<Array<any>>([])
const loading = ref(true)
const searchText = ref('')

const showFeedback = ref(false)
const feedbackContent = ref('')
const feedbackType = ref('suggestion') // Default type
const feedbackLoading = ref(false)

const feedbackTypeOptions = [
    { label: '功能建议', value: 'suggestion' },
    { label: '内容报错', value: 'content_error' },
    { label: '播放卡顿', value: 'playback_issue' },
    { label: '其他问题', value: 'other' }
]

const fetchData = async () => {
  loading.value = true
  try {
    // Parallel fetch for better performance
    const [bannerRes, categoryRes, hotRes, announceRes] = await Promise.all([
      Service.getAllBanner(),
      Service.getAllCategory(),
      Service.getHotList(),
      Service.getAllAnnouncement()
    ])

    if (bannerRes.code === 1) {
      banners.value = bannerRes.data as any || []
    }

    if (hotRes.code === 1) {
      hotList.value = hotRes.data as any || []
    }
    
    if (announceRes.code === 1) {
      announcements.value = announceRes.data as any || []
    }

    if (categoryRes.code === 1) {
      categories.value = categoryRes.data as any || []
      const seriesPromises = categories.value.map(async (cat) => {
        if (cat.id) {
          try {
            const seriesRes = await Service.getSeriesByCategory(cat.id)
            if (seriesRes.code === 1) {
              categorySeriesMap.value[cat.id] = seriesRes.data as any || []
            }
          } catch (e) {
            console.error(`Failed to load series for category ${cat.id}`, e)
          }
        }
      })
      await Promise.all(seriesPromises)
    }
  } catch (e) {
    console.error('Failed to fetch initial data', e)
    message.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (searchText.value.trim()) {
    router.push({ name: 'search', query: { keyword: searchText.value.trim() } })
  }
}

const handleHotClick = (word: string) => {
  if (word) {
    router.push({ name: 'search', query: { keyword: word.trim() } })
  }
}

const handleLogout = () => {
  removeUser()
  router.push({ name: 'login' })
}

const submitFeedback = async () => {
  if (!feedbackContent.value.trim()) {
      message.warning('请输入反馈内容')
      return
  }
  feedbackLoading.value = true
  try {
    const res = await Service.addFeedback(user.value?.id || 0, feedbackContent.value, feedbackType.value, 0)
    if (res.code === 1) {
      message.success('反馈提交成功')
      showFeedback.value = false
      feedbackContent.value = ''
      feedbackType.value = 'suggestion'
    } else {
      message.error(res.message || '提交失败')
    }
  } catch (e) {
    message.error('提交出错')
  } finally {
    feedbackLoading.value = false
  }
}

const fetchUserInfo = async () => {
  if (user.value?.id) {
    try {
      const res = await Service.getById(user.value.id)
      if (res.code === 1) {
        user.value = res.data as any
      }
    } catch (e) {
      console.error('Failed to fetch user info', e)
    }
  }
}

const getAvatarUrl = (url?: string) => {
  return url
}

onMounted(() => {
  fetchData()
  fetchUserInfo()
})

const userOptions = [
  { label: '个人空间', key: 'profile', icon: () => h(NIcon, null, { default: () => h(UserIcon) }) },
  { label: '退出登录', key: 'logout', icon: () => h(NIcon, null, { default: () => h(LogOutIcon) }) }
]

const handleUserSelect = (key: string) => {
  if (key === 'logout') {
    handleLogout()
  } else if (key === 'profile') {
    router.push({ name: 'profile' })
  }
}
</script>

<template>
  <div class="home-container">
    <!-- Fixed Top Bar -->
    <header class="top-bar">
      <div class="logo">金苹果影视</div>
      
      <div class="search-container">
        <n-input 
          v-model:value="searchText" 
          placeholder="搜索精彩剧集..." 
          round 
          clearable
          @keydown.enter="handleSearch"
        >
          <template #prefix>
            <n-icon :component="SearchIcon" />
          </template>
        </n-input>
      </div>

      <div class="user-actions">
        <n-dropdown :options="userOptions" @select="handleUserSelect">
          <div class="user-profile">
            <span class="username">{{ user?.nickname || user?.username || '用户' }}</span>
            <n-avatar round size="small" :src="getAvatarUrl(user?.avatarUrl)" style="background-color: #18a058;">
               <n-icon v-if="!user?.avatarUrl" :component="UserIcon" />
            </n-avatar>
          </div>
        </n-dropdown>
      </div>
    </header>


    <main class="main-content">

      <section class="top-section">
        <n-grid x-gap="24" :cols="4">

          <n-grid-item :span="3">
            <div class="banner-wrapper">
              <n-skeleton v-if="loading" height="100%" width="100%" />
              <n-carousel v-else autoplay draggable show-arrow>
                <div v-for="banner in banners" :key="banner.id" class="carousel-item">
                  <img 
                    :src="banner.imgurl" 
                    class="carousel-img" 
                    :alt="banner.title" 
                    @error="(e: any) => e.target.src = 'https://via.placeholder.com/1280x720?text=No+Image'"
                  />
                  <div class="banner-info" v-if="banner.title">
                      <h3>{{ banner.title }}</h3>
                  </div>
                </div>
                <div v-if="banners.length === 0 && !loading" class="carousel-item placeholder">
                    <n-empty description="暂无推荐内容" />
                </div>
              </n-carousel>
            </div>
          </n-grid-item>
          

          <n-grid-item :span="1">
            <div class="hot-list-container">
               <div class="hot-header">
                 <h3>🔥 热搜榜</h3>
               </div>
               <div class="hot-list-content">
                 <n-skeleton v-if="loading" text :repeat="10" />
                 <div v-else-if="hotList.length > 0" class="hot-items">
                    <div v-for="(item, index) in hotList" :key="item.id" class="hot-item" @click="handleHotClick(item.word)">
                       <span class="hot-rank" :class="{'top-3': index < 3}">{{ index + 1 }}</span>
                       <span class="hot-title" :title="item.word">{{ item.word }}</span>
                       <span class="hot-score" v-if="item.heat">Waiting...</span> <!-- Heat not always in basic model, placeholder -->
                    </div>
                 </div>
                 <n-empty v-else description="暂无热搜" />
               </div>
            </div>
          </n-grid-item>
        </n-grid>
      </section>


      <section class="announcement-bar" v-if="announcements.length > 0">
         <div class="marquee-container">
             <div class="marquee-content">
                 <span v-for="notice in announcements" :key="notice.id" class="notice-item">
                     📢 {{ notice.content }}
                 </span>
             </div>
         </div>
      </section>


      <section class="content-section">
        <div v-if="loading">
             <!-- Skeleton Loading for Categories -->
             <div v-for="i in 2" :key="i" class="category-block">
                <n-skeleton text style="width: 150px; height: 32px; margin-bottom: 16px;" />
                <n-grid x-gap="16" y-gap="16" cols="2 s:3 m:4 l:5 xl:6" responsive="screen">
                    <n-grid-item v-for="j in 6" :key="j">
                        <n-card>
                            <template #cover>
                                <n-skeleton height="250px" width="100%" />
                            </template>
                            <n-skeleton text style="width: 60%; margin-top: 8px;" />
                        </n-card>
                    </n-grid-item>
                </n-grid>
             </div>
        </div>

        <div v-else>
            <div v-for="cat in categories" :key="cat.id" class="category-block">
            <div class="category-header">
                <h2>{{ cat.name }}</h2>
            </div>
            
            <n-grid x-gap="20" y-gap="24" cols="2 s:3 m:4 l:5 xl:6" responsive="screen">
                <n-grid-item v-for="series in categorySeriesMap[cat.id] || []" :key="series.id">
                <n-card hoverable class="series-card" @click="router.push({ name: 'video', params: { seriesId: series.id } })">
                    <template #cover>
                    <div class="card-cover">
                         <img 
                            :src="series.coverUrl" 
                            loading="lazy"
                            @error="(e: any) => e.target.src = 'https://via.placeholder.com/300x450?text=No+Cover'"
                        />
                        <div class="card-overlay">
                            <span class="play-icon">▶</span>
                        </div>
                    </div>
                    </template>
                    <div class="card-info">
                        <h3 class="series-title" :title="series.title">{{ series.title }}</h3>
                        <p class="series-desc">{{ series.description || '暂无简介' }}</p>
                    </div>
                </n-card>
                </n-grid-item>
            </n-grid>

            <div v-if="!categorySeriesMap[cat.id] || categorySeriesMap[cat.id]?.length === 0" class="empty-category">
                <p>该分类下暂无内容</p>
            </div>
            </div>
        </div>
      </section>
    </main>


    <n-float-button :bottom="40" :right="40" @click="showFeedback = true" type="primary" class="feedback-btn">
      <n-icon :component="MessageSquareIcon" />
    </n-float-button>


    <n-modal v-model:show="showFeedback" title="意见反馈" preset="card" style="width: 500px">
      <div class="feedback-form">
          <div style="margin-bottom: 12px;">
              <span style="display: block; margin-bottom: 4px; font-weight: 500;">反馈类型</span>
              <n-select v-model:value="feedbackType" :options="feedbackTypeOptions" />
          </div>
          <div>
              <span style="display: block; margin-bottom: 4px; font-weight: 500;">详细描述</span>
              <n-input
                v-model:value="feedbackContent"
                type="textarea"
                placeholder="请告诉我们您的想法..."
                :autosize="{ minRows: 4, maxRows: 6 }"
              />
          </div>
      </div>
      <template #footer>
        <div class="modal-footer">
          <n-button @click="showFeedback = false">取消</n-button>
          <n-button type="primary" @click="submitFeedback" :loading="feedbackLoading" style="margin-left: 12px">
            提交反馈
          </n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<style scoped>

.home-container {
  min-height: 100vh;
  background-color: transparent;
  color: var(--text-primary);
}


.top-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: var(--bg-header);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  padding: 0 32px;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: background-color 0.3s, border-color 0.3s;
}

.logo {
  font-size: 24px;
  font-weight: 800;

  background: linear-gradient(45deg, #2c3e50, #3498db);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-right: 48px;
}

:global(.dark) .logo {
    background: linear-gradient(45deg, #ffffff, #a0a0a0);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.search-container {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  margin-right: 40px;
}

.search-container .n-input {
  width: 100%;
  max-width: 320px;
  background-color: var(--bg-input);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 20px;
  transition: background-color 0.2s, color 0.3s;
  color: var(--text-primary);
}

.user-profile:hover {
  background-color: var(--bg-input);
}


.home-container, .top-bar, .series-card, .user-profile, .hot-list-container, .banner-wrapper, .card-info, .category-header h2 {
    transition: background-color 0.3s ease, border-color 0.3s ease, color 0.3s ease, box-shadow 0.3s ease;
}

.username {
  font-weight: 500;
  font-size: 14px;
  color: var(--text-primary);
}

/* Main Content */
.main-content {
  padding-top: 84px;
  padding-left: 20px;
  padding-right: 20px;
  padding-bottom: 60px;
  max-width: 1400px; /* Restrict width to show side background */
  margin: 0 auto;
}


.top-section {
  width: 100%;
  margin-bottom: 24px;
}

.banner-wrapper {
  width: 100%;
  aspect-ratio: 16 / 9;
  max-height: 500px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 24px rgba(0,0,0,0.08);
  /* Make semi-transparent or solid depending on preference, currently solid for content legibility */
  background-color: var(--bg-card); 
}

/* Hot List */
.hot-list-container {
  height: 100%;
  background: var(--bg-card);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  transition: background-color 0.3s, border-color 0.3s;
  border: 1px solid var(--border-color);
}

.hot-header h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 700;
  color: #e74c3c;
}

.hot-list-content {
  flex: 1;
  overflow-y: auto;
}

.hot-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background 0.2s;
}

.hot-item:hover {
  background: var(--bg-input);
}

.hot-rank {
  width: 24px;
  height: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  font-weight: bold;
  font-size: 14px;
  color: var(--text-secondary);
  margin-right: 12px;
  background: var(--bg-input);
}

.hot-rank.top-3 {
  background: #ff7675;
  color: white;
}

.hot-title {
  flex: 1;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: var(--text-primary);
}

/* Announcement Marquee */
.announcement-bar {
  margin-bottom: 32px;
  background: rgba(255, 243, 205, 0.6);
  border: 1px solid rgba(255, 238, 186, 0.8);
  color: #856404;
  border-radius: 8px;
  height: 40px;
  display: flex;
  align-items: center;
  overflow: hidden;
  position: relative;
}

:global(.dark) .announcement-bar {
    background: rgba(44, 62, 80, 0.8);
    border-color: rgba(255, 255, 255, 0.1);
    color: #ecf0f1;
}

.marquee-container {
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
}

.marquee-content {
  display: inline-block;
  animation: marquee 20s linear infinite;
  padding-left: 100%;
}

.notice-item {
  margin-right: 48px;
  font-size: 14px;
  font-weight: 500;
}

@keyframes marquee {
  0% { transform: translateX(0); }
  100% { transform: translateX(-100%); }
}

.carousel-item {
  width: 100%;
  height: 100%;
  position: relative;
}

.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-info {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 20px 40px;
    background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
    color: white;
}

.banner-info h3 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
}

/* Category Lists */
.category-block {
  margin-bottom: 48px;
}

.category-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  border-left: 4px solid #3498db;
  padding-left: 12px;
}

.category-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.series-card {
  border-radius: 12px;
  overflow: hidden;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.series-card:hover {
    transform: translateY(-5px);
}

.card-cover {
    position: relative;
    aspect-ratio: 2 / 3;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.card-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.series-card:hover .card-cover img {
    transform: scale(1.05);
}

.card-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0,0,0,0.3);
    display: flex;
    justify-content: center;
    align-items: center;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.series-card:hover .card-overlay {
    opacity: 1;
}

.play-icon {
    font-size: 32px;
    color: white;
    background: rgba(255,255,255,0.2);
    width: 60px;
    height: 60px;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    backdrop-filter: blur(4px);
}

.card-info {
    padding: 12px 4px;
}

.series-title {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.series-desc {
    margin: 4px 0 0;
    font-size: 13px;
    color: var(--text-secondary);
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.empty-category {
    text-align: center;
    color: var(--text-tertiary);
    padding: 20px;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
}

/* Naive Card theme adaptation */
:deep(.n-card) {
    background-color: var(--bg-card);
    transition: background-color 0.3s ease, border-color 0.3s ease;
}
</style>