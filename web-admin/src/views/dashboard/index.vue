<template>
  <div class="dashboard-container">
    <!-- 系统标题横幅 -->
    <div class="system-banner">
      <div class="system-title">
        <MarketLogo :size="36" />
        <h1>阿克苏地区市场监管执法智慧平台</h1>
      </div>
      <div class="system-subtitle">智慧监管 · 数据赋能 · 精准治理</div>
    </div>

    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2>欢迎回来，{{ username }}</h2>
        <p>{{ todayStr }} | 今日待办 {{ pendingTotal }} 项</p>
      </div>
      <div class="welcome-illustration">
        <el-icon size="48" style="color: rgba(255,255,255,0.3)"><OfficeBuilding /></el-icon>
      </div>
    </div>

    <!-- 待办提醒 -->
    <div v-if="hasPending" class="pending-bar">
      <div class="pending-item" v-if="pendingAppeals > 0" @click="$router.push('/appeal')">
        <span class="pending-dot danger"></span>
        <span>{{ pendingAppeals }} 条待处理诉求</span>
      </div>
      <div class="pending-item" v-if="pendingAlerts > 0" @click="$router.push('/alert')">
        <span class="pending-dot warning"></span>
        <span>{{ pendingAlerts }} 条待处理预警</span>
      </div>
      <div class="pending-item" v-if="pendingTasks > 0" @click="$router.push('/task')">
        <span class="pending-dot info"></span>
        <span>{{ pendingTasks }} 条待执行任务</span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="item in statCards" :key="item.key" :style="{'--accent': item.color}" @click="handleCardClick(item)">
        <div class="card-top">
          <div class="card-label">{{ item.label }}</div>
          <div class="card-icon" :style="{background: item.color + '1F', color: item.color}">
            <el-icon :size="22"><component :is="item.icon" /></el-icon>
          </div>
        </div>
        <div class="card-value">{{ item.value }}</div>
        <div class="card-percent" v-if="item.total > 0">{{ item.value }}家，占总数{{ item.percent }}%</div>
        <div class="card-pending" v-if="item.pending > 0">
          <span class="pending-badge">{{ item.pending }} 待处理</span>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="chart-grid">
      <div class="chart-card">
        <div class="chart-title">诉求状态分布</div>
        <div ref="pieChartRef" class="chart-body"></div>
      </div>
      <div class="chart-card">
        <div class="chart-title">预警等级分布</div>
        <div ref="barChartRef" class="chart-body"></div>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="list-grid">
      <div class="list-card">
        <div class="list-header">
          <span class="list-title">最近诉求</span>
          <el-button text type="primary" size="small" @click="$router.push('/appeal')">查看全部 →</el-button>
        </div>
        <div class="list-body">
          <div v-for="item in recentAppeals" :key="item.id" class="list-item" @click="handleAppealRowClick(item)">
            <div class="item-main">
              <span class="item-title">{{ item.title }}</span>
              <el-tag :type="statusTypeMap[item.status] || 'info'" size="small" effect="light">{{ statusLabel[item.status] || item.status }}</el-tag>
            </div>
            <div class="item-meta">
              <span>{{ item.appealType }}</span>
              <span>{{ item.createTime }}</span>
            </div>
          </div>
          <div v-if="recentAppeals.length === 0" class="empty-state">暂无诉求</div>
        </div>
      </div>
      <div class="list-card">
        <div class="list-header">
          <span class="list-title">待处理预警</span>
          <el-button text type="primary" size="small" @click="$router.push('/alert')">查看全部 →</el-button>
        </div>
        <div class="list-body">
          <div v-for="item in recentAlerts" :key="item.id" class="list-item" @click="handleAlertRowClick(item)">
            <div class="item-main">
              <span class="item-title">{{ item.title }}</span>
              <el-tag :type="levelTypeMap[item.level] || 'info'" size="small" effect="light">{{ levelLabel[item.level] || item.level }}</el-tag>
            </div>
            <div class="item-meta">
              <span>{{ item.enterpriseName }}</span>
              <span>{{ item.createTime }}</span>
            </div>
          </div>
          <div v-if="recentAlerts.length === 0" class="empty-state">暂无预警</div>
        </div>
      </div>
    </div>

    <!-- 底部版权 -->
    <div class="footer-copyright">
      <span>由新疆璟达智创科技有限公司开发</span>
      <span class="divider">|</span>
      <span>中国电信云服务技术支持</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getDashboardOverview } from '@/api/dashboard'
import MarketLogo from '@/components/MarketLogo.vue'
import { OfficeBuilding, TrendCharts, DataLine, Warning, DocumentChecked, Bell, List, Stamp, Edit } from '@element-plus/icons-vue'

const router = useRouter()
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

// 优先显示登录时保存的真实姓名，其次用户名，最后兜底
const username = ref(localStorage.getItem('aksu_supervision_realName') || localStorage.getItem('aksu_supervision_username') || '管理员')
const todayStr = computed(() => {
  const d = new Date()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekdays[d.getDay()]}`
})

const statusTypeMap = {
  'PENDING': 'warning', 'EVALUATED': '', 'ASSIGNED': 'primary', 'HANDLING': 'primary', 'HANDLED': 'success'
}
const statusLabel = {
  'PENDING': '待处理', 'EVALUATED': '已评价', 'ASSIGNED': '已分配', 'HANDLING': '处理中', 'HANDLED': '已处理'
}
const levelTypeMap = { 'HIGH': 'danger', 'MEDIUM': 'warning', 'LOW': 'info' }
const levelLabel = { 'HIGH': '高', 'MEDIUM': '中', 'LOW': '低' }

const statCards = ref([
  { key: 'enterprise', label: '企业总数', value: 0, pending: 0, total: 0, percent: 0, icon: markRaw(TrendCharts), color: '#C8102E', route: '/enterprise' },
  { key: 'appeal', label: '诉求总数', value: 0, pending: 0, total: 0, percent: 0, icon: markRaw(DocumentChecked), color: '#faad14', route: '/appeal' },
  { key: 'task', label: '任务总数', value: 0, pending: 0, total: 0, percent: 0, icon: markRaw(List), color: '#52c41a', route: '/task' },
  { key: 'alert', label: '预警数', value: 0, pending: 0, total: 0, percent: 0, icon: markRaw(Warning), color: '#f5222d', route: '/alert' },
  { key: 'inspection', label: '检查记录', value: 0, pending: 0, total: 0, percent: 0, icon: markRaw(Stamp), color: '#722ed1', route: '/inspection' },
  { key: 'rectification', label: '整改通知', value: 0, pending: 0, total: 0, percent: 0, icon: markRaw(Edit), color: '#eb2f96', route: '/rectification' }
])

const recentAppeals = ref([])
const recentAlerts = ref([])
const userType = ref('admin')

const pendingAppeals = ref(0)
const pendingAlerts = ref(0)
const pendingTasks = ref(0)
const pendingTotal = computed(() => pendingAppeals.value + pendingAlerts.value + pendingTasks.value)
const hasPending = computed(() => pendingTotal.value > 0)

const handleCardClick = (item) => {
  if (item.route) router.push(item.route)
}

const handleAppealRowClick = (row) => {
  router.push(`/appeal/${row.id}`)
}

const handleAlertRowClick = (row) => {
  router.push('/alert')
}

const fetchData = async () => {
  try {
    const res = await getDashboardOverview()
    const d = res.data
    
    // 计算总数用于百分比
    const totalCount = d.enterpriseCount || 0
    
    statCards.value[0].value = d.enterpriseCount || 0
    statCards.value[0].total = totalCount
    statCards.value[0].percent = totalCount > 0 ? Math.round((d.enterpriseCount || 0) / totalCount * 100) : 0
    
    statCards.value[1].value = d.appealCount || 0
    statCards.value[1].pending = d.pendingAppeals || 0
    statCards.value[1].total = d.appealCount || 0
    statCards.value[1].percent = d.appealCount > 0 ? Math.round((d.pendingAppeals || 0) / d.appealCount * 100) : 0
    
    statCards.value[2].value = d.taskCount || 0
    statCards.value[2].pending = d.pendingTasks || 0
    statCards.value[2].total = d.taskCount || 0
    statCards.value[2].percent = d.taskCount > 0 ? Math.round((d.pendingTasks || 0) / d.taskCount * 100) : 0
    
    statCards.value[3].value = d.alertCount || 0
    statCards.value[3].pending = d.pendingAlerts || 0
    statCards.value[3].total = d.alertCount || 0
    statCards.value[3].percent = d.alertCount > 0 ? Math.round((d.pendingAlerts || 0) / d.alertCount * 100) : 0
    
    statCards.value[4].value = d.inspectionCount || 0
    statCards.value[4].total = d.inspectionCount || 0
    statCards.value[4].percent = 100
    
    statCards.value[5].value = d.rectificationCount || 0
    statCards.value[5].total = d.rectificationCount || 0
    statCards.value[5].percent = 100

    pendingAppeals.value = d.pendingAppeals || 0
    pendingAlerts.value = d.pendingAlerts || 0
    pendingTasks.value = d.pendingTasks || 0
    userType.value = d.userType || 'admin'

    recentAppeals.value = d.recentAppeals || []
    recentAlerts.value = d.recentAlerts || []

    if (userType.value === 'enterprise' || userType.value === 'enterprise_user') {
      statCards.value = statCards.value.filter(c => c.key !== 'task' && c.key !== 'inspection')
    }

    updateCharts(d)
  } catch (e) {
    console.warn('Dashboard数据加载失败', e)
  }
}

const updateCharts = (d) => {
  const appealByStatus = d.appealByStatus || {}
  if (pieChart) {
    pieChart.setOption({
      series: [{
        data: [
          { value: appealByStatus.PENDING || 0, name: '待处理', itemStyle: { color: '#faad14' } },
          { value: appealByStatus.ASSIGNED || 0, name: '已分配', itemStyle: { color: '#C8102E' } },
          { value: appealByStatus.HANDLING || 0, name: '处理中', itemStyle: { color: '#e6a23c' } },
          { value: appealByStatus.HANDLED || 0, name: '已处理', itemStyle: { color: '#52c41a' } }
        ]
      }]
    })
  }

  const alertByLevel = d.alertByLevel || {}
  if (barChart) {
    barChart.setOption({
      series: [
        { name: '高', type: 'bar', data: [alertByLevel.HIGH || 0], itemStyle: { color: '#f5222d' }, label: { show: true, position: 'top', formatter: '{c}' } },
        { name: '中', type: 'bar', data: [alertByLevel.MEDIUM || 0], itemStyle: { color: '#faad14' }, label: { show: true, position: 'top', formatter: '{c}' } },
        { name: '低', type: 'bar', data: [alertByLevel.LOW || 0], itemStyle: { color: '#909399' }, label: { show: true, position: 'top', formatter: '{c}' } }
      ]
    })
  }
}

const initPieChart = () => {
  pieChart = markRaw(echarts.init(pieChartRef.value))
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{c} ({d}%)', fontSize: 12 },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: [
        { value: 0, name: '待处理', itemStyle: { color: '#faad14' } },
        { value: 0, name: '已分配', itemStyle: { color: '#C8102E' } },
        { value: 0, name: '处理中', itemStyle: { color: '#e6a23c' } },
        { value: 0, name: '已处理', itemStyle: { color: '#52c41a' } }
      ]
    }]
  })
  pieChart.on('click', (params) => {
    const statusMap = { '待处理': 'PENDING', '已分配': 'ASSIGNED', '处理中': 'HANDLING', '已处理': 'HANDLED' }
    const status = statusMap[params.name]
    if (status) router.push({ path: '/appeal', query: { status } })
  })
}

const initBarChart = () => {
  barChart = markRaw(echarts.init(barChartRef.value))
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['高', '中', '低'], bottom: 0 },
    grid: { top: 30, right: 20, bottom: 40, left: 50 },
    xAxis: { type: 'category', data: ['预警等级'] },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '高', type: 'bar', data: [0], itemStyle: { color: '#f5222d' }, label: { show: true, position: 'top', formatter: '{c}' } },
      { name: '中', type: 'bar', data: [0], itemStyle: { color: '#faad14' }, label: { show: true, position: 'top', formatter: '{c}' } },
      { name: '低', type: 'bar', data: [0], itemStyle: { color: '#909399' }, label: { show: true, position: 'top', formatter: '{c}' } }
    ]
  })
  barChart.on('click', (params) => {
    const levelMap = { '高': 'HIGH', '中': 'MEDIUM', '低': 'LOW' }
    const level = levelMap[params.seriesName]
    if (level) router.push({ path: '/alert', query: { level } })
  })
}

const handleResize = () => { pieChart?.resize(); barChart?.resize() }

onMounted(() => {
  initPieChart()
  initBarChart()
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  width: 100%;
  padding: 0;
  box-sizing: border-box;
}

/* 系统标题横幅 */
.system-banner {
  background: linear-gradient(135deg, #C8102E, #D5263D);
  padding: 20px 32px;
  margin-bottom: 16px;
  color: #fff;
  text-align: center;
  
  .system-title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-bottom: 6px;
    
    h1 {
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      letter-spacing: 2px;
    }
  }
  
  .system-subtitle {
    font-size: 13px;
    opacity: 0.8;
    letter-spacing: 4px;
  }
}

.welcome-banner {
  background: linear-gradient(135deg, #C8102E, #D5263D);
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  color: #fff;
  h2 { margin: 0 0 4px 0; font-size: 22px; }
  p { margin: 0; font-size: 14px; opacity: 0.85; }
}

/* 待办提醒 */
.pending-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.pending-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.2s;
  &:hover { transform: translateY(-1px); box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
}
.pending-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  &.danger { background: #f56c6c; }
  &.warning { background: #e6a23c; }
  &.info { background: #909399; }
}

/* 统计卡片网格 */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}
.stat-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
  .card-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  .card-label { font-size: 13px; color: #909399; }
  .card-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .card-value { font-size: 28px; font-weight: 700; color: #303133; }
  .card-percent {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
  .card-pending { margin-top: 6px; }
  .pending-badge {
    font-size: 11px;
    color: #f56c6c;
    background: #fef0f0;
    padding: 2px 8px;
    border-radius: 10px;
  }
}

/* 图表 */
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}
.chart-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}
.chart-body { height: 280px; cursor: pointer; }

/* 列表 */
.list-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.list-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.list-title { font-size: 15px; font-weight: 600; color: #303133; }
.list-body { max-height: 300px; overflow-y: auto; }
.list-item {
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  &:last-child { border-bottom: none; }
  &:hover { background: #fafbfc; }
}
.item-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.item-title { font-size: 14px; color: #303133; font-weight: 500; }
.item-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}
.empty-state {
  text-align: center;
  padding: 32px 0;
  color: #c0c4cc;
  font-size: 14px;
}

/* 底部版权 */
.footer-copyright {
  text-align: center;
  padding: 20px 0 24px;
  font-size: 12px;
  color: #999;
  
  .divider {
    margin: 0 8px;
    color: #ccc;
  }
}

@media (max-width: 768px) {
  .chart-grid, .list-grid { grid-template-columns: 1fr; }
  .system-banner .system-title h1 { font-size: 18px; }
}
</style>
