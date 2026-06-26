<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>诉求趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-wrapper"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>预警等级分布</span>
          </template>
          <div ref="pieChartRef" class="chart-wrapper"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表区 -->
    <el-row :gutter="16" class="list-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近诉求</span>
              <el-button text type="primary" @click="$router.push('/appeal')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentAppeals" size="small" :show-header="true">
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
              <span>最近预警</span>
            </div>
          </template>
          <el-table :data="recentAlerts" size="small" :show-header="true">
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
import { ref, onMounted, onBeforeUnmount, markRaw } from 'vue'
import * as echarts from 'echarts'
import { getDashboardOverview } from '@/api/dashboard'

const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart = null
let pieChart = null

const statusTypeMap = {
  'PENDING': 'warning', 'EVALUATED': '', 'ASSIGNED': 'primary', 'HANDLED': 'success'
}
const statusLabel = {
  'PENDING': '待处理', 'EVALUATED': '已评价', 'ASSIGNED': '已分配', 'HANDLED': '已处理'
}
const levelTypeMap = { 'HIGH': 'danger', 'MEDIUM': 'warning', 'LOW': 'info' }
const levelLabel = { 'HIGH': '高', 'MEDIUM': '中', 'LOW': '低' }

const statCards = ref([
  { key: 'enterprise', label: '企业总数', value: 0, icon: 'OfficeBuilding', color: '#1A73E8' },
  { key: 'appeal', label: '诉求总数', value: 0, icon: 'Document', color: '#faad14' },
  { key: 'task', label: '任务总数', value: 0, icon: 'List', color: '#52c41a' },
  { key: 'alert', label: '预警数', value: 0, icon: 'Warning', color: '#f5222d' }
])

const recentAppeals = ref([])
const recentAlerts = ref([])

const fetchData = async () => {
  try {
    const res = await getDashboardOverview()
    const d = res.data
    statCards.value[0].value = d.enterpriseCount || 0
    statCards.value[1].value = d.appealCount || 0
    statCards.value[2].value = d.taskCount || 0
    statCards.value[3].value = d.alertCount || 0
    recentAppeals.value = d.recentAppeals || []
    recentAlerts.value = d.recentAlerts || []
    updateCharts(d)
  } catch (e) {
    console.warn('Dashboard数据加载失败，使用空数据')
  }
}

const updateCharts = (d) => {
  // 诉求状态分布饼图
  const appealByStatus = d.appealByStatus || {}
  if (pieChart) {
    pieChart.setOption({
      series: [{
        data: [
          { value: appealByStatus.PENDING || 0, name: '待处理', itemStyle: { color: '#faad14' } },
          { value: appealByStatus.ASSIGNED || 0, name: '已分配', itemStyle: { color: '#1A73E8' } },
          { value: appealByStatus.HANDLED || 0, name: '已处理', itemStyle: { color: '#52c41a' } },
          { value: appealByStatus.EVALUATED || 0, name: '已评价', itemStyle: { color: '#909399' } }
        ]
      }]
    })
  }

  // 预警等级分布
  const alertByLevel = d.alertByLevel || {}
  if (trendChart) {
    trendChart.setOption({
      series: [
        { name: '高', type: 'bar', data: [alertByLevel.HIGH || 0], itemStyle: { color: '#f5222d' } },
        { name: '中', type: 'bar', data: [alertByLevel.MEDIUM || 0], itemStyle: { color: '#faad14' } },
        { name: '低', type: 'bar', data: [alertByLevel.LOW || 0], itemStyle: { color: '#909399' } }
      ]
    })
  }
}

const initTrendChart = () => {
  trendChart = markRaw(echarts.init(trendChartRef.value))
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
  trendChart.setOption(option)
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
        { value: 0, name: '已处理', itemStyle: { color: '#52c41a' } },
        { value: 0, name: '已评价', itemStyle: { color: '#909399' } }
      ]
    }]
  }
  pieChart.setOption(option)
}

const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  initTrendChart()
  initPieChart()
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .stat-row {
    margin-bottom: 16px;
    .stat-card {
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
    }
  }
  .chart-row {
    margin-bottom: 16px;
    .chart-wrapper {
      height: 300px;
    }
  }
  .list-row {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
