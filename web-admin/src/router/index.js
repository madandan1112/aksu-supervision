import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页概览', icon: 'Odometer' }
      },
      // 诉求管理
      {
        path: 'appeal',
        name: 'AppealList',
        component: () => import('@/views/appeal/list.vue'),
        meta: { title: '诉求列表', icon: 'Document' }
      },
      {
        path: 'appeal/:id',
        name: 'AppealDetail',
        component: () => import('@/views/appeal/detail.vue'),
        meta: { title: '诉求详情', hidden: true }
      },
      // 任务调度
      {
        path: 'task',
        name: 'TaskList',
        component: () => import('@/views/task/list.vue'),
        meta: { title: '任务列表', icon: 'List' }
      },
      {
        path: 'task/create',
        name: 'TaskCreate',
        component: () => import('@/views/task/create.vue'),
        meta: { title: '创建任务', hidden: true }
      },
      {
        path: 'task/:id',
        name: 'TaskDetail',
        component: () => import('@/views/task/detail.vue'),
        meta: { title: '任务详情', hidden: true }
      },
      // 数据管理 - 企业档案
      {
        path: 'enterprise',
        name: 'EnterpriseList',
        component: () => import('@/views/enterprise/list.vue'),
        meta: { title: '企业档案', icon: 'OfficeBuilding' }
      },
      {
        path: 'enterprise/:id',
        name: 'EnterpriseDetail',
        component: () => import('@/views/enterprise/detail.vue'),
        meta: { title: '企业详情', hidden: true }
      },
      // 报告管理
      {
        path: 'report',
        name: 'ReportList',
        component: () => import('@/views/report/list.vue'),
        meta: { title: '报告管理', icon: 'Notebook' }
      },
      // 预警管理
      {
        path: 'alert',
        name: 'AlertList',
        component: () => import('@/views/alert/list.vue'),
        meta: { title: '预警管理', icon: 'Bell' }
      },
      // 统计分析
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '统计分析', icon: 'PieChart' }
      },
      // 数据可视化
      {
        path: 'visualization',
        name: 'Visualization',
        component: () => import('@/views/visualization/index.vue'),
        meta: { title: '数据可视化', icon: 'DataAnalysis' }
      },
      // 数据大屏
      {
        path: 'bigscreen',
        name: 'BigScreen',
        component: () => import('@/views/bigscreen/index.vue'),
        meta: { title: '数据大屏', icon: 'Monitor', fullScreen: true }
      },
      // 系统管理
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色权限', icon: 'Lock' }
      },
      {
        path: 'system/config',
        name: 'ConfigManage',
        component: () => import('@/views/system/config/index.vue'),
        meta: { title: '参数配置', icon: 'Setting' }
      },
      {
        path: 'system/log',
        name: 'LogManage',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', icon: 'Tickets' }
      },
      {
        path: 'system/org',
        name: 'OrgManage',
        component: () => import('@/views/system/org/index.vue'),
        meta: { title: '组织架构', icon: 'OfficeBuilding' }
      },
      {
        path: 'system/position',
        name: 'PositionManage',
        component: () => import('@/views/system/position/index.vue'),
        meta: { title: '岗位管理', icon: 'Stamp' }
      },
      {
        path: 'system/job-title',
        name: 'JobTitleManage',
        component: () => import('@/views/system/job-title/index.vue'),
        meta: { title: '职务管理', icon: 'Medal' }
      },
      // 小程序用户管理
      {
        path: 'miniapp/user',
        name: 'MiniappUser',
        component: () => import('@/views/miniapp/user.vue'),
        meta: { title: '小程序用户', icon: 'UserFilled' }
      },
      // 后台管理 - 企业信息查看
      {
        path: 'backend/enterprise',
        name: 'BackendEnterprise',
        component: () => import('@/views/backend/enterprise.vue'),
        meta: { title: '企业信息查看', icon: 'OfficeBuilding' }
      },
      {
        path: 'backend/enterprise/:id',
        name: 'BackendEnterpriseDetail',
        component: () => import('@/views/backend/enterprise-detail.vue'),
        meta: { title: '企业详细信息', hidden: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 阿克苏监管平台` : '阿克苏监管平台'

  if (to.meta.noAuth) {
    next()
  } else {
    const token = getToken()
    if (token) {
      next()
    } else {
      next({ path: '/login', query: { redirect: to.fullPath } })
    }
  }
})

export default router
