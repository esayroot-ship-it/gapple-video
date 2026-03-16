<script setup lang="ts">
import { ref, onMounted, h, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { 
  NCard, NTabs, NTabPane, 
  NGrid, NGridItem, NSkeleton, NEmpty, NAvatar, NButton, NIcon, useMessage,
  NModal, NInput, NUpload, NUploadDragger, NText, NP, type UploadFileInfo
} from 'naive-ui'
import 'vue-cropper/dist/index.css'
import { VueCropper } from 'vue-cropper'
import { getUser, removeUser, setUser } from '../utils/auth'
import { Service } from '../api/user/services/Service'
import type { series } from '../api/user/models/series'
import type { video } from '../api/user/models/video'
import type { favorite } from '../api/user/models/favorite'
import type { history } from '../api/user/models/history'
import type { episode } from '../api/user/models/episode'
import type { user as UserType } from '../api/user/models/user'

const router = useRouter()
const message = useMessage()
const user = getUser()
const displayUser = ref(user)
const showEditModal = ref(false)
const editNickname = ref('')


const showCropperModal = ref(false)
const cropperStep = ref<'upload' | 'crop'>('upload')
const cropperImg = ref('')
const cropperRef = ref()
const uploadingAvatar = ref(false)

const openAvatarModal = () => {
    cropperStep.value = 'upload'
    cropperImg.value = ''
    showCropperModal.value = true
}

const getAvatarUrl = (url?: string) => {
  return url
}

const onFileChange = (options: { file: UploadFileInfo, fileList: Array<UploadFileInfo> }) => {
    const file = options.file.file
    if (file) {
        const reader = new FileReader()
        reader.onload = (e) => {
            cropperImg.value = e.target?.result as string
            cropperStep.value = 'crop'
            nextTick(() => {
                 if(cropperRef.value && cropperRef.value.refresh) {
                    cropperRef.value.refresh()
                }
            })
        }
        reader.readAsDataURL(file)
    }
}

const handleConfirmCrop = () => {
    if (!cropperRef.value) return
    
    cropperRef.value.getCropBlob(async (blob: Blob) => {
        if (!displayUser.value?.id) return
        
        uploadingAvatar.value = true
        try {
            const file = new File([blob], 'avatar.png', { type: 'image/png' })
            const res = await Service.setuserimg(displayUser.value.id, file)
            if (res.code === 1) {
                message.success('头像修改成功')
                const updatedUser = res.data as UserType
                if (updatedUser && updatedUser.avatarUrl) {
                     const newUserInfo = {
                        ...displayUser.value,
                        avatarUrl: updatedUser.avatarUrl
                    }
                    setUser(newUserInfo)
                    displayUser.value = newUserInfo
                }
                showCropperModal.value = false
            } else {
                message.error(res.message || '头像修改失败')
            }
        } catch (e) {
            console.error(e)
            message.error('头像上传出错')
        } finally {
            uploadingAvatar.value = false
        }
    })
}

const openEditModal = () => {
    editNickname.value = displayUser.value?.nickname || ''
    showEditModal.value = true
}

const handleEditNickname = async () => {
    if (!displayUser.value || !displayUser.value.username) return;
    try {
        const res = await Service.setNickname(editNickname.value, displayUser.value.username)
        if (res.code === 1) {
            message.success('昵称修改成功')
            const updatedUser = res.data as UserType
            if (updatedUser) {
                 const newUserInfo = {
                     ...displayUser.value,
                     nickname: updatedUser.nickname || editNickname.value
                 }
                 setUser(newUserInfo)
                 displayUser.value = newUserInfo
            }
            showEditModal.value = false
        } else {
            message.error(res.message || '修改失败')
        }
    } catch (e) {
        console.error(e)
        message.error('请求出错')
    }
}


const activeTab = ref('subscribe')
const loading = ref(false)
const subscribes = ref<series[]>([])
const favorites = ref<(favorite & { video?: video; series?: series })[]>([])
const histories = ref<(history & { video?: video; series?: series })[]>([])


const UserIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('path', { d: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' }),
  h('circle', { cx: '12', cy: '7', r: '4' })
])

const ArrowLeftIcon = () => h('svg', { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: '2', strokeLinecap: 'round', strokeLinejoin: 'round', width: '1em', height: '1em' }, [
  h('line', { x1: '19', y1: '12', x2: '5', y2: '12' }),
  h('polyline', { points: '12 19 5 12 12 5' })
])


const formatDate = (dateStr?: string) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

const handleJump = (item: any, type: 'subscribe' | 'favorite' | 'history') => {
  let seriesId: number | undefined;
  let videoId: number | undefined;

  if (type === 'subscribe') {
    seriesId = item.id;
  } else {
    seriesId = item.series?.id;
    videoId = item.video?.id || item.vid;
  }

  if (seriesId) {
    router.push({
      name: 'video',
      params: { seriesId },
      query: videoId ? { videoId } : undefined
    });
  } else {
    message.warning('无法跳转：未找到相关剧集信息');
  }
}

const fetchData = async () => {
  if (!user?.id) return
  loading.value = true
  try {
    const [subRes, favRes, histRes] = await Promise.all([
      Service.getSubscribes(user.id),
      Service.getFavoritesByUserId(user.id),
      Service.getHistorysByUserId(user.id)
    ])

    if (subRes.code === 1) subscribes.value = (subRes.data as series[]) || []

    if (favRes.code === 1) {
      const favs = favRes.data as favorite[] || []
      const favPromises = favs.map(async (fav) => {
        if (!fav.vid) return { ...fav }
        try {
          const [vRes, sRes] = await Promise.all([
             Service.getVideoById(fav.vid),
             Service.getSeriesByVideoId(fav.vid)
          ])
          let videoData = vRes.code === 1 ? vRes.data as video : undefined
          let seriesData = undefined
          if (sRes.code === 1) {
             const epData = Array.isArray(sRes.data) ? sRes.data[0] : (sRes.data as episode);
             if (epData?.sid) seriesData = { id: epData.sid } as series;
          }
          return { ...fav, video: videoData, series: seriesData }
        } catch (e) { console.error(e) }
        return { ...fav }
      })
      favorites.value = await Promise.all(favPromises)
    }

    if (histRes.code === 1) {
      const hists = histRes.data as history[] || []
      const histPromises = hists.map(async (hItem) => {
        if (!hItem.vid) return { ...hItem }
        try {
           const [vRes, sRes] = await Promise.all([
               Service.getVideoById(hItem.vid),
               Service.getSeriesByVideoId(hItem.vid)
           ])
           let videoData = vRes.code === 1 ? vRes.data as video : undefined
           let seriesData = undefined
           if (sRes.code === 1) {
              const epData = Array.isArray(sRes.data) ? sRes.data[0] : (sRes.data as episode);
              if (epData?.sid) seriesData = { id: epData.sid } as series;
           }
           return { ...hItem, video: videoData, series: seriesData }
        } catch(e) { console.error(e) }
        return { ...hItem }
      })
      histories.value = await Promise.all(histPromises)
    }
  } catch (e) {
    console.error('Failed to fetch profile data', e)
    message.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  if (window.history.state && window.history.state.back) router.back()
  else router.push({ name: 'home' })
}

const handleLogout = () => {
  removeUser()
  router.push({ name: 'login' })
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="profile-container">
    <header class="profile-header">
      <div class="header-left">
        <n-button circle secondary @click="goBack" class="back-btn">
          <template #icon><n-icon :component="ArrowLeftIcon" /></template>
        </n-button>
        <div class="user-info">
           <div class="avatar-section" @click="openAvatarModal">
                <div class="avatar-wrapper" style="cursor: pointer;">
                    <n-avatar 
                        round 
                        :size="100" 
                        :src="getAvatarUrl(displayUser?.avatarUrl)" 
                        style="background-color: #18a058; border: 4px solid rgba(255,255,255,0.2);"
                    >
                        <n-icon v-if="!getAvatarUrl(displayUser?.avatarUrl)" :component="UserIcon" size="60" />
                    </n-avatar>
                    <div class="avatar-overlay">
                        <n-icon size="24" color="white">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>
                        </n-icon>
                        <span>更换头像</span>
                    </div>
                </div>
           </div>
           <div class="user-details">
             <div class="name-wrapper">
                 <h1 class="username">{{ displayUser?.nickname || displayUser?.username || '用户' }}</h1>
                 <n-button circle size="tiny" quaternary class="edit-btn" @click="openEditModal">
                     <template #icon>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-edit-2"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path></svg>
                     </template>
                 </n-button>
             </div>
             <p class="user-id" v-if="displayUser?.nickname">用户名: {{ displayUser?.username }}</p>
             <p class="user-id">ID: {{ displayUser?.id }}</p>
           </div>
        </div>
      </div>
      <div class="header-right">
        <n-button type="error" ghost @click="handleLogout">退出登录</n-button>
      </div>
    </header>

    <main class="profile-content">
      <div class="content-wrapper">
        <n-tabs v-model:value="activeTab" type="line" animated size="large" class="profile-tabs">
          <n-tab-pane name="subscribe" tab="我的订阅">
            <div v-if="loading" class="tab-content">
              <n-grid x-gap="20" y-gap="24" cols="2 s:3 m:4 l:5" responsive="screen">
                <n-grid-item v-for="i in 5" :key="i">
                  <n-card class="skeleton-card">
                    <template #cover><n-skeleton height="250px" /></template>
                    <n-skeleton text width="80%" style="margin: 12px" />
                  </n-card>
                </n-grid-item>
              </n-grid>
            </div>
            <div v-else-if="subscribes.length > 0" class="tab-content">
               <n-grid x-gap="20" y-gap="24" cols="2 s:3 m:4 l:5" responsive="screen">
                <n-grid-item v-for="item in subscribes" :key="item.id">
                  <div class="media-card" @click="handleJump(item, 'subscribe')">
                    <div class="portrait-cover">
                       <img :src="item.coverUrl || 'https://via.placeholder.com/200x300?text=Series'" class="card-img" />
                    </div>
                    <div class="card-meta">
                        <div class="card-title">{{ item.title || '未知剧集' }}</div>
                        <div class="card-time" v-if="item.createdtime">发布于 {{ formatDate(item.createdtime) }}</div>
                    </div>
                  </div>
                </n-grid-item>
              </n-grid>
            </div>
            <div v-else class="empty-state"><n-empty description="暂无订阅内容" /></div>
          </n-tab-pane>

          <n-tab-pane name="favorite" tab="我的收藏">
             <div v-if="loading" class="tab-content">
              <n-grid x-gap="20" y-gap="24" cols="2 s:3 m:4 l:5" responsive="screen">
                <n-grid-item v-for="i in 5" :key="i">
                  <n-card class="skeleton-card">
                    <template #cover><n-skeleton height="150px" /></template>
                    <n-skeleton text width="60%" style="margin: 12px" />
                  </n-card>
                </n-grid-item>
              </n-grid>
            </div>
            <div v-else-if="favorites.length > 0" class="tab-content">
               <n-grid x-gap="20" y-gap="24" cols="2 s:3 m:4 l:5" responsive="screen">
                <n-grid-item v-for="item in favorites" :key="item.id">
                  <div class="media-card" @click="handleJump(item, 'favorite')">
                    <div class="landscape-cover">
                        <img :src="item.video?.coverUrl || 'https://via.placeholder.com/300x169?text=Video'" class="card-img" />
                    </div>
                     <div class="card-meta">
                        <div class="card-title">{{ item.video?.title || '未知视频' }}</div>
                         <div class="card-time" v-if="item.createdtime">收藏于 {{ formatDate(item.createdtime) }}</div>
                     </div>
                  </div>
                </n-grid-item>
              </n-grid>
            </div>
            <div v-else class="empty-state"><n-empty description="暂无收藏视频" /></div>
          </n-tab-pane>

          <n-tab-pane name="history" tab="观看历史">
             <div v-if="loading" class="tab-content">
               <n-grid :cols="1" y-gap="12">
                 <n-grid-item v-for="i in 3" :key="i">
                   <n-card class="skeleton-card">
                     <n-skeleton text width="40%" />
                     <n-skeleton text width="20%" style="margin-top: 8px" />
                   </n-card>
                 </n-grid-item>
               </n-grid>
            </div>
            <div v-else-if="histories.length > 0" class="tab-content">
               <div class="history-list">
                 <div v-for="item in histories" :key="item.id" class="history-card" @click="handleJump(item, 'history')">
                    <div class="history-content">
                        <div class="history-thumb-wrapper">
                             <img :src="item.video?.coverUrl || 'https://via.placeholder.com/160x90?text=History'" class="history-thumb" />
                        </div>
                        <div class="history-info">
                            <h3 class="history-title">{{ item.video?.title || '未知视频' }}</h3>
                            <p class="history-time">上次观看: {{ formatDate(item.lasttime) }}</p>
                        </div>
                    </div>
                 </div>
               </div>
            </div>
            <div v-else class="empty-state"><n-empty description="暂无观看历史" /></div>
          </n-tab-pane>
        </n-tabs>
      </div>
    </main>

    <n-modal v-model:show="showEditModal" preset="dialog" title="修改昵称">
        <div style="padding: 20px 0;"><n-input v-model:value="editNickname" placeholder="请输入新昵称" /></div>
        <template #action>
            <n-button @click="showEditModal = false">取消</n-button>
            <n-button type="primary" @click="handleEditNickname">确认</n-button>
        </template>
    </n-modal>

    <n-modal v-model:show="showCropperModal" preset="card" title="修改头像" style="width: 600px">
        <div v-if="cropperStep === 'upload'" style="padding: 20px;">
                          <n-upload
                             multiple
                             directory-dnd
                             :show-file-list="false"
                             :default-upload="false"
                             accept="image/*"
                             @change="onFileChange"
                         >
                <n-upload-dragger>
                    <div style="margin-bottom: 12px">
                        <n-icon size="48" :depth="3">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
                        </n-icon>
                    </div>
                    <n-text style="font-size: 16px">点击或者拖拽图片到该区域来上传</n-text>
                    <n-p depth="3" style="margin: 8px 0 0 0">请上传 JPG/PNG 格式的图片</n-p>
                </n-upload-dragger>
            </n-upload>
        </div>
        <div v-else style="height: 400px; width: 100%">
            <vue-cropper
                ref="cropperRef"
                :img="cropperImg"
                :outputSize="1"
                outputType="png"
                :fixed="true"
                :fixedNumber="[1, 1]"
                :centerBox="true"
                :autoCrop="true"
                :fixedBox="false"
                :canMove="true"
                :canScale="true"
                :enlarge="1"
                mode="contain"
            />
        </div>
        <template #footer>
            <div style="display: flex; justify-content: flex-end; gap: 12px">
                <n-button @click="showCropperModal = false">取消</n-button>
                <n-button v-if="cropperStep === 'crop'" type="primary" :loading="uploadingAvatar" @click="handleConfirmCrop">确认上传</n-button>
            </div>
        </template>
    </n-modal>
  </div>
</template>

<style scoped>
.profile-container {
  min-height: 100vh;
  background-color: transparent;
  color: var(--text-primary);
}
.profile-header {
  height: 200px;
  background: linear-gradient(135deg, #2c3e50, #4ca1af);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 40px;
}
.header-left { display: flex; align-items: center; gap: 24px; }
.back-btn { background: rgba(255,255,255,0.2); color: white; border: none; }
.user-info { display: flex; align-items: center; gap: 20px; }
.avatar-section { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.avatar-wrapper { position: relative; width: 100px; height: 100px; border-radius: 50%; overflow: hidden; transition: transform 0.3s ease; }
.avatar-wrapper:hover { transform: scale(1.05); }
.avatar-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.5); display: flex; flex-direction: column; align-items: center; justify-content: center; opacity: 0; transition: opacity 0.3s ease; color: white; font-size: 12px; gap: 4px; }
.avatar-wrapper:hover .avatar-overlay { opacity: 1; }
.user-details .username { margin: 0; font-size: 28px; font-weight: 700; color: white; }
.name-wrapper { display: flex; align-items: center; gap: 8px; }
.edit-btn { color: rgba(255,255,255,0.7); }
.edit-btn:hover { color: white; }
.user-details .user-id { margin: 4px 0 0; opacity: 0.8; font-size: 14px; color: rgba(255,255,255,0.8); }
.profile-content { max-width: 1200px; margin: -60px auto 0; padding: 0 20px 40px; position: relative; z-index: 10; }
.content-wrapper { min-height: 500px; }
.tab-content { padding: 24px 0; }
.empty-state { padding: 60px 0; background: rgba(255, 255, 255, 0.1); border-radius: 12px; backdrop-filter: blur(4px); }
:deep(.n-tabs-tab) { font-size: 18px !important; font-weight: 600 !important; color: rgba(255, 255, 255, 0.8) !important; }
:deep(.n-tabs-tab--active) { color: #fff !important; }
:deep(.n-tabs-bar) { background-color: #fff !important; height: 3px !important; border-radius: 3px; }
.media-card { background-color: #ffffff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1); box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08); height: 100%; display: flex; flex-direction: column; }
.media-card:hover { transform: translateY(-8px); box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15); }
.portrait-cover { width: 100%; aspect-ratio: 2 / 3; overflow: hidden; }
.landscape-cover { width: 100%; aspect-ratio: 16 / 9; overflow: hidden; }
.card-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.media-card:hover .card-img { transform: scale(1.05); }
.card-meta { padding: 16px; background: white; flex-grow: 1; }
.card-title { font-weight: 700; font-size: 16px; margin-bottom: 6px; color: #1a1a1a; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.4; }
.card-time { font-size: 12px; color: #8c8c8c; }
.history-list { display: flex; flex-direction: column; gap: 16px; }
.history-card { background-color: #ffffff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.2s ease; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); }
.history-card:hover { transform: translateX(8px); box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1); }
.history-content { display: flex; padding: 16px; gap: 20px; align-items: center; }
.history-thumb-wrapper { width: 180px; aspect-ratio: 16 / 9; flex-shrink: 0; overflow: hidden; border-radius: 8px; }
.history-thumb { width: 100%; height: 100%; object-fit: cover; }
.history-info { flex-grow: 1; }
.history-title { margin: 0 0 8px; font-size: 18px; font-weight: 700; color: #1a1a1a; }
.history-time { margin: 0; font-size: 14px; color: #8c8c8c; }
.skeleton-card { border-radius: 12px; background-color: rgba(255, 255, 255, 0.8) !important; }
:deep(.cropper-view-box), :deep(.cropper-face) { border-radius: 50%; }
</style>