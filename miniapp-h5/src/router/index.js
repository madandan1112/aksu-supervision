import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/scan',
    name: 'Scan',
    component: () => import('../views/Scan.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/appeal',
    name: 'AppealList',
    component: () => import('../views/AppealList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/appeal/create',
    name: 'AppealCreate',
    component: () => import('../views/AppealCreate.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/appeal/:id',
    name: 'AppealDetail',
    component: () => import('../views/AppealDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/rectification',
    name: 'RectificationList',
    component: () => import('../views/RectificationList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/rectification/:id',
    name: 'RectificationDetail',
    component: () => import('../views/RectificationDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/report',
    name: 'ReportList',
    component: () => import('../views/ReportList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/')
  } else if (to.path === '/' && token) {
    next('/home')
  } else {
    next()
  }
})

export default router
