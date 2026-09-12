<template>
  <div id="app-wrapper" :class="{ 'full-screen': isFullScreen, 'inspector-mode': userStore.isInspector }">
    <router-view />
    <!-- 底部药丸TabBar -->
    <nav v-if="showTabBar" class="pill-nav">
      <div
        v-for="tab in currentTabs"
        :key="tab.path"
        class="pill-tab"
        :class="{ active: currentTab === tab.path }"
        @click="$router.push(tab.path)"
      >
        <div class="pill-icon" v-html="tab.svg"></div>
        <span class="pill-label">{{ tab.label }}</span>
      </div>
    </nav>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores/user'

const route = useRoute()
const userStore = useUserStore()

// SVG图标
const icons = {
  home: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`,
  scan: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 7V5a2 2 0 0 1 2-2h2"/><path d="M17 3h2a2 2 0 0 1 2 2v2"/><path d="M21 17v2a2 2 0 0 1-2 2h-2"/><path d="M7 21H5a2 2 0 0 1-2-2v-2"/><rect x="7" y="7" width="10" height="10" rx="1"/></svg>`,
  appeal: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>`,
  rectification: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>`,
  report: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>`,
  profile: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`
}

const inspectorTabs = [
  { path: '/home', svg: icons.home, label: '首页' },
  { path: '/scan', svg: icons.scan, label: '扫码' },
  { path: '/appeal', svg: icons.appeal, label: '诉求' },
  { path: '/profile', svg: icons.profile, label: '我的' }
]

const enterpriseTabs = [
  { path: '/home', svg: icons.home, label: '首页' },
  { path: '/appeal', svg: icons.appeal, label: '诉求' },
  { path: '/rectification', svg: icons.rectification, label: '整改' },
  { path: '/profile', svg: icons.profile, label: '我的' }
]

const currentTabs = computed(() => {
  return userStore.isInspector ? inspectorTabs : enterpriseTabs
})

const showTabBar = computed(() => {
  return userStore.isLoggedIn && !['/', '/login'].includes(route.path)
})

const isFullScreen = computed(() => {
  return route.meta.fullScreen === true
})

const currentTab = computed(() => {
  if (route.path.startsWith('/appeal')) return '/appeal'
  if (route.path.startsWith('/rectification')) return '/rectification'
  if (route.path.startsWith('/scan')) return '/scan'
  if (route.path.startsWith('/profile')) return '/profile'
  return route.path
})
</script>

<style>
/* 全局样式 - 浅色智能家居风 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  background: #F0F2F5;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
a {
  text-decoration: none;
  color: inherit;
}

/* 全局CSS变量（市监V2：企业蓝 2563EB / 执法红 C8102E） */
:root {
  --bg-primary: #F6F7F9;
  --bg-card: #FFFFFF;
  --text-primary: #18181B;
  --text-secondary: #52525B;
  --text-tertiary: #9CA3AF;
  --accent-start: #2563EB;
  --accent-end: #1D4ED8;
  --accent-blue: #2563EB;
  --accent-purple: #7C3AED;
  --accent-green: #34C759;
  --accent-orange: #FF9500;
  --accent-red: #EF4444;
  --brand-red: #C8102E;
  --brand-red-light: #D5263D;
  --brand-red-dark: #B00E24;
  --brand-gold: #C9A063;
  --brand-gold-light: #E5C893;
  --shadow-sm: 0 2px 8px rgba(24, 24, 27, 0.06);
  --shadow-md: 0 4px 16px rgba(24, 24, 27, 0.10);
  --shadow-lg: 0 8px 32px rgba(24, 24, 27, 0.14);
  --radius-sm: 12px;
  --radius-md: 16px;
  --radius-lg: 20px;
  --radius-xl: 24px;
  --radius-pill: 999px;
}

/* 执法模式：主色切换为市监红 */
#app-wrapper.inspector-mode {
  --accent-start: #D5263D;
  --accent-end: #B00E24;
  --accent-blue: #C8102E;
}
</style>

<style scoped>
#app-wrapper {
  min-height: 100vh;
  padding-bottom: 90px;
  background: var(--bg-primary);
}

#app-wrapper.full-screen {
  padding-bottom: 0;
}

/* 药丸导航 */
.pill-nav {
  position: fixed;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 40px);
  max-width: 440px;
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-pill);
  display: flex;
  align-items: center;
  justify-content: space-around;
  z-index: 999;
  box-shadow: var(--shadow-md), 0 -2px 8px rgba(0,0,0,0.04);
  padding: 0 8px;
}

.pill-tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  padding: 8px 4px;
  border-radius: 16px;
  transition: all 0.3s ease;
  position: relative;
}

.pill-tab .pill-icon {
  width: 22px;
  height: 22px;
  color: var(--text-tertiary);
  transition: all 0.3s ease;
}

.pill-tab .pill-icon svg {
  width: 100%;
  height: 100%;
}

.pill-tab .pill-label {
  font-size: 10px;
  color: var(--text-tertiary);
  transition: all 0.3s ease;
}

.pill-tab.active {
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

#app-wrapper.inspector-mode .pill-tab.active {
  box-shadow: 0 4px 12px rgba(200, 16, 46, 0.3);
}

.pill-tab.active .pill-icon,
.pill-tab.active .pill-label {
  color: #fff;
}

.pill-tab:not(.active):active {
  background: rgba(24, 24, 27, 0.05);
}

@media (max-width: 360px) {
  .pill-nav {
    height: 56px;
  }
  .pill-tab .pill-icon {
    width: 20px;
    height: 20px;
  }
  .pill-tab .pill-label {
    font-size: 9px;
  }
}
</style>
