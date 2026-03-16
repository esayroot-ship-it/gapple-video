import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import Profile from '../views/Profile.vue';
import Search from '../views/Search.vue';
import Video from '../views/Video.vue';
import { isLoggedIn } from '../utils/auth';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      meta: { requiresAuth: true }
    },
    {
      path: '/search',
      name: 'search',
      component: Search,
      meta: { requiresAuth: true }
    },
    {
      path: '/video/:seriesId',
      name: 'video',
      component: Video,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { guestOnly: true }
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
      meta: { guestOnly: true }
    }
  ]
});

router.beforeEach((to, _from, next) => {
  const loggedIn = isLoggedIn();

  if (to.meta.requiresAuth && !loggedIn) {
    next({ name: 'login' });
  } else if (to.meta.guestOnly && loggedIn) {
    next({ name: 'home' });
  } else {
    next();
  }
});

export default router;
