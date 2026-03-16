import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/video',
    children: [
      {
        path: 'video',
        name: 'VideoManage',
        component: () => import('../views/video/VideoList.vue'),
        meta: { title: '视频管理' }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('../views/user/UserList.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'announcement',
        name: 'AnnouncementManage',
        component: () => import('../views/announcement/AnnouncementList.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'banner',
        name: 'BannerManage',
        component: () => import('../views/banner/BannerList.vue'),
        meta: { title: '轮播图管理' }
      },
      {
        path: 'category',
        name: 'CategoryManage',
        component: () => import('../views/category/CategoryList.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'series',
        name: 'SeriesManage',
        component: () => import('../views/series/SeriesList.vue'),
        meta: { title: '剧集管理' }
      },
      {
        path: 'episode',
        name: 'EpisodeManage',
        component: () => import('../views/episode/EpisodeList.vue'),
        meta: { title: '分集管理' }
      },
      {
        path: 'feedback',
        name: 'FeedbackManage',
        component: () => import('../views/feedback/FeedbackList.vue'),
        meta: { title: '反馈管理' }
      },
      {
        path: 'hot',
        name: 'HotManage',
        component: () => import('../views/hot/HotList.vue'),
        meta: { title: '热搜管理' }
      },
      {
        path: 'admin-list',
        name: 'AdminManage',
        component: () => import('../views/admin/AdminList.vue'),
        meta: { title: '管理员管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = sessionStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router