<template>
  <div class="video-page">
    <!-- Header -->
    <header class="top-bar">
      <div class="header-left">
          <n-button quaternary circle @click="goBack" class="back-btn">
              <template #icon>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="24" height="24">
                      <line x1="19" y1="12" x2="5" y2="12"></line>
                      <polyline points="12 19 5 12 12 5"></polyline>
                  </svg>
              </template>
          </n-button>
          <div class="logo" @click="router.push({ name: 'home' })" style="cursor: pointer;">影视平台</div>
      </div>
      <div class="user-actions">
        <n-dropdown :options="userOptions" @select="handleUserSelect">
          <div class="user-profile">
            <span class="username">{{ user?.nickname || user?.username || '用户' }}</span>
            <n-avatar round size="small" :src="getAvatarUrl(user?.avatarUrl)" style="background-color: #18a058;">
               <span v-if="!user?.avatarUrl">U</span>
            </n-avatar>
          </div>
        </n-dropdown>
      </div>
    </header>

    <div class="main-content">
      <n-grid x-gap="24" :cols="24">
        <!-- Left: Video, Info, Comments -->
        <n-grid-item :span="18">
          <!-- Video Player -->
          <div class="player-wrapper">
            <div class="player-container">
                <div id="xgplayer" class="video-player"></div>
                <div v-if="!currentVideo?.playUrl && !loading" class="loading-video">
                  <n-spin v-if="loading" size="large" />
                  <span v-else>请选择剧集播放</span>
                </div>
            </div>
          </div>

          <!-- Video Info -->
          <n-card :bordered="false" class="video-info-card" v-if="currentVideo">
            <div class="info-header">
              <h1 class="video-title">{{ currentVideo.title }}</h1>
              <div class="actions">
                 <n-button strong secondary round type="warning" @click="handleFavorite" class="action-btn">
                    <template #icon>
                        <span v-if="isFavorited">★</span>
                        <span v-else>☆</span>
                    </template>
                    {{ isFavorited ? '已收藏' : '收藏' }}
                 </n-button>
              </div>
            </div>
            <p class="video-desc">{{ currentVideo.description || '暂无简介' }}</p>
            <div class="video-meta">
                <div class="rating-info">
                   <div class="avg-rating">
                       <span class="label">平均评分:</span>
                       <span class="score">{{ currentVideo.avgRating || '0.0' }}</span>
                       <n-rate readonly allow-half :value="currentVideo.avgRating || 0" :count="5" size="small" />
                   </div>
                   <n-divider vertical />
                   <div class="user-rating">
                       <span class="label">{{ hasRated ? '我的评分:' : '我要评分:' }}</span>
                       <n-rate v-model:value="myRatingValue" :count="5" size="small" @update:value="handleRate" />
                       <n-button v-if="hasRated" size="tiny" text type="error" @click="handleDeleteRating" class="delete-rating-btn">删除</n-button>
                   </div>
                </div>
            </div>
          </n-card>

          <!-- Comments -->
          <n-card title="评论" :bordered="false" class="comments-card">
             <div class="add-comment">
                <n-input
                    v-model:value="commentContent"
                    type="textarea"
                    placeholder="发一条友善的评论..."
                    :autosize="{ minRows: 3 }"
                />
                <div class="comment-actions">
                    <n-button type="primary" @click="postComment" :disabled="!commentContent.trim()">
                        发布评论
                    </n-button>
                </div>
             </div>
             
             <n-divider />

             <div class="comment-list">
                 <n-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧~" />
                 <div v-for="comment in comments" :key="comment.id" class="comment-item">
                     <div class="comment-avatar">
                         <n-avatar round size="small" :src="getAvatarUrl(comment.avatarUrl)" :style="{ backgroundColor: !comment.avatarUrl ? stringToColor(comment.uid + '') : 'transparent' }">
                             <span v-if="!comment.avatarUrl">{{ (comment.uid + '').slice(0, 1) }}</span>
                         </n-avatar>
                     </div>
                     <div class="comment-body">
                         <div class="comment-header">
                             <span class="comment-user">{{ comment.nickname || ('用户' + comment.uid) }}</span>
                             <span class="comment-time">{{ formatDate(comment.createdat) }}</span>
                         </div>
                         <div class="comment-text">{{ comment.content }}</div>
                         <div class="comment-footer">
                             <n-button text size="tiny" @click="handleLikeComment(comment)" :class="{ 'liked': (commentLikes[comment.id!] || 0) > 0 }">
                                 <template #icon>👍</template>
                                 {{ commentLikes[comment.id!] || 0 }}
                             </n-button>
                         </div>
                     </div>
                 </div>
             </div>
          </n-card>
        </n-grid-item>

        <!-- Right: Episodes -->
        <n-grid-item :span="6">
            <n-card title="选集" :bordered="false" class="episodes-card">
                <div class="episode-grid">
                    <div 
                        v-for="ep in episodes" 
                        :key="ep.id" 
                        class="episode-btn"
                        :class="{ active: currentEpisode?.id === ep.id }"
                        @click="selectEpisode(ep)"
                    >
                        {{ ep.no }}
                    </div>
                </div>
                <div class="card-footer">
                    <n-divider />
                    <n-button 
                        block 
                        strong 
                        round 
                        :loading="subLoading"
                        :type="isSubscribed ? 'default' : 'primary'" 
                        @click="handleSubscribe"
                        class="subscribe-btn"
                    >
                        <template #icon>
                            <span v-if="isSubscribed">✅</span>
                            <span v-else>➕</span>
                        </template>
                        {{ isSubscribed ? '已订阅' : '订阅' }}
                    </n-button>
                </div>
            </n-card>

            <!-- AI Chat Component -->
             <AiChat 
                :context-text="currentVideo ? `Title: ${currentVideo.title}\nDescription: ${currentVideo.description}` : ''" 
             />
        </n-grid-item>
      </n-grid>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, reactive, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { 
    NButton, NDropdown, NAvatar, NGrid, NGridItem, NCard, NSpin, 
    NInput, NDivider, NEmpty, useMessage, NRate
} from 'naive-ui';
import Player from 'xgplayer';
import 'xgplayer/dist/index.min.css';
import AiChat from '../components/AiChat.vue';
import { Service } from '../api/user/services/Service';
import { getUser, removeUser } from '../utils/auth';
import type { video } from '../api/user/models/video';
import type { episode } from '../api/user/models/episode';
import type { comment } from '../api/user/models/comment';
import type { favorite } from '../api/user/models/favorite';
import type { rating } from '../api/user/models/rating';
import type { user as UserType } from '../api/user/models/user';

type ExtendedComment = comment & { 
    nickname?: string;
    avatarUrl?: string;
    user?: UserType;
};

const router = useRouter();
const route = useRoute();
const message = useMessage();
const user = getUser();

const getAvatarUrl = (url?: string) => {
  return url
}

const seriesId = ref(Number(route.params.seriesId));

const episodes = ref<episode[]>([]);
const currentVideo = ref<video | null>(null);
const currentEpisode = ref<episode | null>(null);
const comments = ref<ExtendedComment[]>([]);
const commentLikes = reactive<Record<number, number>>({});
const commentContent = ref('');
const isFavorited = ref(false);
const userFavorites = ref<favorite[]>([]);
const isSubscribed = ref(false);
const loading = ref(false);
const subLoading = ref(false);
const myRatingValue = ref(0);
const hasRated = ref(false);

let player: Player | null = null;


const userOptions = [
  { label: '个人空间', key: 'profile' },
  { label: '退出登录', key: 'logout' }
];

const handleUserSelect = (key: string) => {
  if (key === 'logout') {
    removeUser();
    router.push({ name: 'login' });
  } else if (key === 'profile') {
    router.push({ name: 'profile' });
  }
};

const goBack = () => {
  if (window.history.state && window.history.state.back) {
    router.back();
  } else {
    router.push({ name: 'home' });
  }
};


const checkUserRating = async (videoId: number) => {
    if (!user?.id) return;
    try {
        const res = await Service.getUserRating(user.id, videoId);
        if (res.code === 1 && res.data) {
             const r = res.data as rating;
             if (r && r.score) {
                 myRatingValue.value = r.score;
                 hasRated.value = true;
             } else {
                 myRatingValue.value = 0;
                 hasRated.value = false;
             }
        } else {
            myRatingValue.value = 0;
            hasRated.value = false;
        }
    } catch (e) {
        console.error('Failed to check rating', e);
        myRatingValue.value = 0;
        hasRated.value = false;
    }
}

const handleRate = async (value: number) => {
    if (!user?.id) {
        message.warning('请先登录');
        myRatingValue.value = 0;
        return;
    }
    if (!currentVideo.value?.id) return;

    try {
        const res = await Service.addRating(user.id, currentVideo.value.id, value);
        if (res.code === 1) {
            message.success('评分成功');
            hasRated.value = true;

            const vRes = await Service.getVideoById(currentVideo.value.id);
            if (vRes.code === 1) {
                currentVideo.value = vRes.data as video;
            }
        } else {
            message.error(res.message || '评分失败');
        }
    } catch (e) {
        console.error(e);
        message.error('评分出错');
    }
}

const handleDeleteRating = async () => {
     if (!user?.id || !currentVideo.value?.id) return;
     try {
         const res = await Service.deleteRating(user.id, currentVideo.value.id);
         if (res.code === 1) {
             message.success('已删除评分');
             hasRated.value = false;
             myRatingValue.value = 0;
             // Refresh avg
             const vRes = await Service.getVideoById(currentVideo.value.id);
             if (vRes.code === 1) {
                currentVideo.value = vRes.data as video;
             }
         } else {
             message.warning(res.message || '删除失败');
         }
     } catch (e) {
         console.error(e);
     }
}

const loadEpisodes = async () => {
  if (!seriesId.value) return;
  try {
    const res = await Service.getEpisodesBySeriesId(seriesId.value);
    if (res.code === 1 && Array.isArray(res.data)) {
      episodes.value = res.data.sort((a: episode, b: episode) => (a.no || 0) - (b.no || 0));
      
      const targetVid = Number(route.query.videoId);
      let targetEp = null;
      if (targetVid) {
          targetEp = episodes.value.find(ep => ep.vid === targetVid);
      }
      

      if (!currentVideo.value && episodes.value.length > 0) {
         if (targetEp) {
             selectEpisode(targetEp);
         } else if (!route.query.videoId) {

             if (episodes.value[0]) {
                 selectEpisode(episodes.value[0]);
             }
         }
      } else if (targetEp) {
         // Highlight the correct episode if video loaded via query param
         currentEpisode.value = targetEp;
      }
    }
  } catch (error) {
    console.error('Failed to load episodes', error);
  }
};

const checkSubscription = async () => {
    if (!user?.id || !seriesId.value) return;
    try {
        const res = await Service.getIfSubscribed(user.id, seriesId.value);
        if (res.code == 1) {
            // Backend returns 1 for subscribed, 0 (or null/other) for not subscribed
            isSubscribed.value  = true;
        } else {
            isSubscribed.value = false;
        }
    } catch (e) {
        console.error('Failed to check subscription', e);
    }
};

const handleSubscribe = async () => {
    if (!user?.id) {
        message.warning('请先登录');
        return;
    }
    if (!seriesId.value) return;

    subLoading.value = true;
    try {
        if (isSubscribed.value) {
            // 已订阅 -> 取消
            const delRes = await Service.deleteSubscribe(user.id, seriesId.value);
            if (delRes.code === 1) {
                message.success('已取消订阅');
                isSubscribed.value = false;
            } else {
                message.warning(delRes.message || '取消订阅失败');
            }
        } else {
            // 未订阅 -> 订阅
            const addRes = await Service.addSubscribe(user.id, seriesId.value);
            if (addRes.code === 1) {
                message.success('订阅成功');
                isSubscribed.value = true;
            } else {
                message.warning(addRes.message || '订阅失败');
            }
        }
    } catch (e) {
        console.error(e);
        message.error('请求出错');
    } finally {
        subLoading.value = false;
    }
};

const selectEpisode = async (ep: episode) => {
  if (currentEpisode.value?.id === ep.id) return;
  // Use router replace to update URL without adding history stack noise
  if (ep.vid) {
      router.replace({ 
          name: 'video', 
          params: { seriesId: seriesId.value }, 
          query: { videoId: ep.vid } 
      });
      // The watch on query.videoId will trigger loadVideo
      // But we can also set it directly for snappiness if needed, usually watch is enough
  }
};

const initOrUpdatePlayer = (url: string) => {
    if (!url) return;

    if (player) {
        if (player.hasStart) {
            player.src = url;
            player.play();
        } else {
            player.start(url);
        }
    } else {
        player = new Player({
            id: 'xgplayer',
            url: url,
            height: '100%',
            width: '100%',
            autoplay: true,
            playbackRate: [0.5, 0.75, 1, 1.5, 2],
            defaultPlaybackRate: 1,
            pip: true,
            cssFullscreen: true,
            poster: currentVideo.value?.coverUrl,
        });
    }
};

const loadVideo = async (videoId: number) => {
  loading.value = true;
  try {
    const res = await Service.getVideoById(videoId);
    if (res.code === 1) {
      currentVideo.value = res.data as video;
      initOrUpdatePlayer(currentVideo.value.playUrl || '');
      
      checkFavorite(videoId);
      loadComments(videoId);
      if (user?.id) {
        Service.addHistory(user.id, videoId);
        checkUserRating(videoId);
      }
    } else {
        message.error('视频加载失败');
    }
  } catch (error) {
    console.error('Failed to load video', error);
  } finally {
    loading.value = false;
  }
};

const loadComments = async (videoId: number) => {
  try {
    const res = await Service.getComments(videoId);
    if (res.code === 1) {
      const list = res.data as ExtendedComment[];

      // 并发获取每条评论的用户信息
      const commentsWithUser = await Promise.all(list.map(async (item) => {
        // 1. 创建一个新对象（浅拷贝），确保 Vue 能检测到属性变化，也不污染原始数据
        const c = { ...item };

        if (c.uid) {
          try {
            const uRes = await Service.getById(c.uid);
            if (uRes.code === 1) {
              const uData = uRes.data as UserType;
              c.user = uData;

              // 2. 核心修复：优先使用昵称，如果没有昵称则使用用户名(username)
              // 原代码漏掉了 uData.username 的回退
              c.nickname = uData.nickname || uData.username || c.nickname;

              c.avatarUrl = uData.avatarUrl;
            }
          } catch (e) {
            console.error('Failed to fetch user info for comment', c.id);
          }
        }
        return c;
      }));

      // 将处理好的包含完整用户信息的列表赋值给响应式变量
      comments.value = commentsWithUser;

      // 加载每条评论的点赞数
      comments.value.forEach(async (c) => {
        if (c.id) {
          const likeRes = await Service.getLikeCount(c.id);
          if (likeRes.code === 1) {
            commentLikes[c.id] = Number(likeRes.data) || 0;
          }
        }
      });
    }
  } catch (error) {
    console.error('Failed to load comments', error);
  }
};

const handleLikeComment = async (comment: comment) => {
    if (!user?.id) {
        message.warning('请先登录');
        return;
    }
    if (!comment.id) return;

    try {
        const res = await Service.likeComment(user.id, comment.id);
        if (res.code === 1) {
            message.success('点赞成功');
            // Update local count
            const likeRes = await Service.getLikeCount(comment.id);
            if (likeRes.code === 1) {
                commentLikes[comment.id] = Number(likeRes.data) || 0;
            }
        } else {
            message.warning(res.message || '点赞失败');
        }
    } catch (e) {
        console.error(e);
    }
};

const postComment = async () => {
  if (!user?.id) {
    message.warning('请先登录');
    return;
  }
  if (!currentVideo.value?.id) return;

  try {
    const res = await Service.addComment(user.id, currentVideo.value.id, commentContent.value);
    if (res.code === 1) {
      message.success('评论发表成功');
      commentContent.value = '';
      loadComments(currentVideo.value.id);
    } else {
      message.error(res.message || '发表失败');
    }
  } catch (error) {
    console.error('Failed to post comment', error);
  }
};

const checkFavorite = async (videoId: number) => {
  if (!user?.id) return;
  try {
      if (userFavorites.value.length === 0) {
           const res = await Service.getFavoritesByUserId(user.id);
           if (res.code === 1) {
               userFavorites.value = res.data as favorite[];
           }
      }
      isFavorited.value = userFavorites.value.some(f => f.vid === videoId);
  } catch(e) {
      console.error(e);
  }
}

const handleFavorite = async () => {
    if (!user?.id) {
        message.warning('请先登录');
        return;
    }
    if (!currentVideo.value?.id) return;

    try {
        if (isFavorited.value) {
            const fav = userFavorites.value.find(f => f.vid === currentVideo.value?.id);
            if (fav && fav.id) {
                const res = await Service.deleteFavorite(fav.id);
                if (res.code === 1) {
                    isFavorited.value = false;
                    userFavorites.value = userFavorites.value.filter(f => f.id !== fav.id);
                    message.success('已取消收藏');
                }
            }
        } else {
            const res = await Service.addFavorite(user.id, currentVideo.value.id);
            if (res.code === 1) {
                isFavorited.value = true;
                message.success('收藏成功');
                const favRes = await Service.getFavoritesByUserId(user.id);
                if (favRes.code === 1) userFavorites.value = favRes.data as favorite[];
            }
        }
    } catch (e) {
        console.error(e);
    }
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString();
};

const stringToColor = (str: string) => {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    const c = (hash & 0x00ffffff).toString(16).toUpperCase();
    return '#' + '00000'.substring(0, 6 - c.length) + c;
}

onMounted(() => {
  const qVid = Number(route.query.videoId);
  if (qVid) {
      loadVideo(qVid);
  }
  
  if (seriesId.value) {
      loadEpisodes();
      checkSubscription();
  }
});

watch(() => route.params.seriesId, (newId) => {
  seriesId.value = Number(newId);
  if (seriesId.value) {
      loadEpisodes();
      checkSubscription();
  }
});

watch(() => route.query.videoId, (newVid) => {
    const vid = Number(newVid);
    if (vid) {
        loadVideo(vid);
        // Sync active episode highlight
        if (episodes.value.length > 0) {
            const found = episodes.value.find(e => e.vid === vid);
            if (found) currentEpisode.value = found;
        }
    }
});

onBeforeUnmount(() => {
    if (player) {
        player.destroy();
        player = null;
    }
});
</script>

<style scoped>
.video-page {
  min-height: 100vh;
  background-color: transparent;
  color: var(--text-primary);
}

/* Header */
.top-bar {
  height: 64px;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: background-color 0.3s;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.back-btn {
    color: var(--text-secondary);
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
}

.user-actions {
  display: flex;
  align-items: center;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  color: var(--text-primary);
}
.user-profile:hover {
    background-color: var(--bg-input);
}

.main-content {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
}

.player-wrapper {
  background: black;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  margin-bottom: 20px;
}

.player-container {
  width: 100%;
  aspect-ratio: 16/9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background-color: black;
}

.video-player {
  width: 100%;
  height: 100%;
}

/* 自定义 xgplayer 颜色 */
:deep(.xgplayer-progress-played) {
    background: #18a058 !important; /* 进度条已播放部分 */
}
:deep(.xgplayer-drag) {
    background: #18a058 !important; /* 进度条拖拽点 */
    box-shadow: 0 0 5px #18a058;
}
:deep(.xgplayer-volume-active) {
    background: #18a058 !important; /* 音量条激活部分 */
}
:deep(.xgplayer-enter-spinner) {
    border-top-color: #18a058 !important; /* 加载动画颜色 */
}
:deep(.xg-inner-controls svg) {
    fill: #fff; /* 控制栏图标颜色保持白色 */
}
:deep(.xg-inner-controls svg:hover) {
    fill: #18a058; /* 图标悬停颜色改为绿色 */
}

.video-info-card {
  margin-bottom: 20px;
  background-color: var(--bg-card);
  border: 1px solid var(--border-color);
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.video-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
}

.actions {
  display: flex;
  gap: 12px;
}

.video-desc {
    color: var(--text-secondary);
    margin-bottom: 16px;
    line-height: 1.6;
}

.video-meta {
    display: flex;
    gap: 12px;
}

.comments-card {
    min-height: 300px;
    background-color: var(--bg-card) !important; /* Force background */
    border: 1px solid var(--border-color);
    border-radius: 8px; /* Ensure rounded corners if consistent */
}

.add-comment {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 12px;
}

.comment-list {
    margin-top: 20px;
}

.comment-item {
    display: flex;
    gap: 16px;
    padding: 16px 0;
    border-bottom: 1px solid var(--border-color);
}

.comment-body {
    flex: 1;
}

.comment-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    font-size: 14px;
}

.comment-user {
    font-weight: 600;
    color: var(--text-primary);
}

.comment-time {
    color: var(--text-tertiary);
}

.comment-text {
    line-height: 1.5;
    margin-bottom: 8px;
    color: var(--text-primary);
}

.comment-footer {
    display: flex;
    gap: 16px;
}

.liked {
    color: var(--accent-color) !important;
}

.episodes-card {
    position: sticky;
    top: 84px;
    background-color: var(--bg-card);
    border: 1px solid var(--border-color);
}

.episode-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(50px, 1fr));
    gap: 10px;
}

.episode-btn {
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg-body);
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
    font-weight: 500;
    color: var(--text-primary);
}

.episode-btn:hover {
    background: var(--bg-input);
}

.episode-btn.active {
    background: #18a058;
    color: white;
}

.card-footer {
    margin-top: 16px;
}

.subscribe-btn {
    margin-top: 8px;
}

.rating-info {
    display: flex;
    align-items: center;
    gap: 16px;
    background: var(--bg-input);
    padding: 10px 16px;
    border-radius: 8px;
    flex-wrap: wrap;
}

.avg-rating, .user-rating {
    display: flex;
    align-items: center;
    gap: 8px;
}

.rating-info .label {
    font-size: 14px;
    color: var(--text-secondary);
}

.rating-info .score {
    font-size: 18px;
    font-weight: bold;
    color: #f0a020;
}

.delete-rating-btn {
    margin-left: 8px;
}
</style>
