<template>
  <div class="dashboard-container">
    <!-- 待办提醒 -->
    <el-row v-if="hasPending" :gutter="16" class="pending-row">
      <el-col :span="24">
        <el-alert type="warning" :closable="false" show-icon>
          <template #title>
            <span>
              您有 <b>{{ pendingTotal }}</b> 条待办事项需要处理：
              <span v-if="pendingAppeals > 0">诉求 <b>{{ pendingAppeals }}</b> 条</span>
              <span v-if="pendingAlerts > 0">；预警 <b>{{ pendingAlerts }}</b> 条</span>
              <span v-if="pendingTasks > 0">；任务 <b>{{ pendingTasks }}</b> 条</span>
            </span>
          </template>
        </el-alert>
      </el-col>
    </el-row>

    <!-- 统计卡片 - 可点击跳转 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <el-card shadow="hover" class="stat-card clickable" @click="handleCardClick(item)">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.value }}</div>
              <div v-if="item.pending > 0" class="stat-pending">
                <el-tag type="danger" size="small" effect="dark">{{ item.pending }} 待处理</el-tag>
              </div>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span class="click-hint">点击查看详情 <el-icon><ArrowRight /></el-icon></span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>诉求状态分布</span>
          </template>
          <div ref="pieChartRef" class="chart-wrapper"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>预警等级分布</span>
          </template>
          <div ref="barChartRef" class="chart-wrapper"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表区 - 可点击跳转 -->
    <el-row :gutter="16" class="list-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近诉求</span>
              <el-button text type="primary" @click="$router.push('/appeal')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentAppeals" size="small" :show-header="true" @row-click="handleAppealRowClick" class="clickable-table">
            <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
            <el-table-column prop="appealType" label="类型" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="statusTypeMap[row.status] || 'info'" size="small">{{ statusLabel[row.status] || row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160" />
          </el-table>
          <el-empty v-if="recentAppeals.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待处理预警</span>
              <el-button text type="primary" @click="$router.push('/alert')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentAlerts" size="small" :show-header="true" @row-click="handleAlertRowClick" class="clickable-table">
            <el-table-column prop="title" label="预警内容" min-width="140" show-overflow-tooltip />
            <el-table-column prop="level" label="等级" width="80">
              <template #default="{ row }">
                <el-tag :type="levelTypeMap[row.level] || 'info'" size="small">{{ levelLabel[row.level] || row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="enterpriseName" label="企业" width="120" show-overflow-tooltip />
            <el-table-column prop="createTime" label="时间" width="160" />
          </el-table>
          <el-empty v-if="recentAlerts.length === 0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getDashboardOverview } from '@/api/dashboard'

const router = useRouter()
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

const statusTypeMap = {
  'PENDING': 'warning', 'EVALUATED': '', 'ASSIGNED': 'primary', 'HANDLING': 'primary', 'HANDLED': 'success'
}
const statusLabel = {
  'PENDING': '待处理', 'EVALUATED': '已评价', 'ASSIGNED': '已分配', 'HANDLING': '处理中', 'HANDLED': '已处理'
}
const levelTypeMap = { 'HIGH': 'danger', 'MEDIUM': 'warning', 'LOW': 'info' }
const levelLabel = { 'HIGH': '高', 'MEDIUM': '中', 'LOW': '低' }

const statCards = ref([
  { key: 'enterprise', label: '企业总数', value: 0, pending: 0, icon: 'OfficeBuilding', color: '#1A73E8', route: '/enterprise' },
  { key: 'appeal', label: '诉求总数', value: 0, pending: 0, icon: 'Document', color: '#faad14', route: '/appeal' },
  { key: 'task', label: '任务总数', value: 0, pending: 0, icon: 'List', color: '#52c41a', route: '/task' },
  { key: 'alert', label: '预警数', value: 0, pending: 0, icon: 'Warning', color: '#f5222d', route: '/alert' }
])

const recentAppeals = ref([])
const recentAlerts = ref([])
const userType = ref('admin')

const pendingAppeals = ref(0)
const pendingAlerts = ref(0)
const pendingTasks = ref(0)
const pendingTotal = computed(() => pendingAppeals.value + pendingAlerts.value + pendingTasks.value)
const hasPending = computed(() => pendingTotal.value > 0)

// 统计卡片点击跳转
const handleCardClick = (item) => {
  if (item.route) {
    router.push(item.route)
  }
}

// 诉求行点击跳转
const handleAppealRowClick = (row) => {
  router.push(`/appeal/${row.id}`)
}

// 预警行点击跳转
const handleAlertRowClick = (row) => {
  router.push('/alert')
}

const fetchData = async () => {
  try {
    const res = await getDashboardOverview()
    const d = res.data
    statCards.value[0].value = d.enterpriseCount || 0
    statCards.value[1].value = d.appealCount || 0
    statCards.value[1].pending = d.pendingAppeals || 0
    statCards.value[2].value = d.taskCount || 0
    statCards.value[2].pending = d.pendingTasks || 0
    statCards.value[3].value = d.alertCount || 0
    statCards.value[3].pending = d.pendingAlerts || 0

    pendingAppeals.value = d.pendingAppeals || 0
    pendingAlerts.value = d.pendingAlerts || 0
    pendingTasks.value = d.pendingTasks || 0
    userType.value = d.userType || 'admin'

    recentAppeals.value = d.recentAppeals || []
    recentAlerts.value = d.recentAlerts || []

    // 企业用户不显示任务卡片
    if (userType.value === 'enterprise' || userType.value === 'enterprise_user') {
      statCards.value = statCards.value.filter(c => c.key !== 'task')
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
          { value: appealByStatus.ASSIGNED || 0, name: '已分配', itemStyle: { color: '#1A73E8' } },
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
        { name: '高', type: 'bar', data: [alertByLevel.HIGH || 0], itemStyle: { color: '#f5222d' } },
        { name: '中', type: 'bar', data: [alertByLevel.MEDIUM || 0], itemStyle: { color: '#faad14' } },
        { name: '低', type: 'bar', data: [alertByLevel.LOW || 0], itemStyle: { color: '#909399' } }
      ]
    })
  }
}

const initPieChart = () => {
  pieChart = markRaw(echarts.init(pieChartRef.value))
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: [
        { value: 0, name: '待处理', itemStyle: { color: '#faad14' } },
        { value: 0, name: '已分配', itemStyle: { color: '#1A73E8' } },
        { value: 0, name: '处理中', itemStyle: { color: '#e6a23c' } },
        { value: 0, name: '已处理', itemStyle: { color: '#52c41a' } }
      ]
    }]
  }
  pieChart.setOption(option)
}

const initBarChart = () => {
  barChart = markRaw(echarts.init(barChartRef.value))
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['高', '中', '低'], bottom: 0 },
    grid: { top: 20, right: 20, bottom: 40, left: 50 },
    xAxis: { type: 'category', data: ['预警等级'] },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '高', type: 'bar', data: [0], itemStyle: { color: '#f5222d' } },
      { name: '中', type: 'bar', data: [0], itemStyle: { color: '#faad14' } },
      { name: '低', type: 'bar', data: [0], itemStyle: { color: '#909399' } }
    ]
  }
  barChart.setOption(option)
}

const handleResize = () => {
  pieChart?.resize()
  barChart?.resize()
}

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
  .pending-row {
    margin-bottom: 12px;
    b { color: #f56c6c; }
  }

  .stat-row {
    margin-bottom: 16px;
    .stat-card {
      cursor: pointer;
      transition: transform 0.2s, box-shadow 0.2s;
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
      .stat-content {
        display: flex;
        align-items: center;
        justify-content: space-between;
        .stat-info {
          .stat-label {
            font-size: 14px;
            color: #909399;
            margin-bottom: 8px;
          }
          .stat-value {
            font-size: 28px;
            font-weight: 700;
            color: #303133;
          }
          .stat-pending {
            margin-top: 6px;
          }
        }
        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
        }
      }
      .stat-footer {
        margin-top: 8px;
        padding-top: 8px;
        border-top: 1px solid #f0f0f0;
        .click-hint {
          font-size: 12px;
          color: #409eff;
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  .chart-row {
    margin-bottom: 16px;
    .chart-wrapper {
      height: 280px;
    }
  }

  .list-row {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .clickable-table {
      :deep(.el-table__row) {
        cursor: pointer;
        &:hover {
          background-color: #ecf5ff;
        }
      }
    }
  }
}
</style>
