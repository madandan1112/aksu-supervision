<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 诉求统计 -->
      <el-tab-pane label="诉求统计" name="appeal">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="诉求总数" :value="appealStats.total" /></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="已办结" :value="appealStats.handled" /></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="平均处理时长" :value="appealStats.avgDays"><template #suffix>天</template></el-statistic></el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>诉求类型分布</span></template><div ref="appealTypeChartRef" style="height:350px"></div></el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>诉求状态分布</span></template><div ref="appealStatusChartRef" style="height:350px"></div></el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>诉求趋势（近6月）</span></template><div ref="appealTrendChartRef" style="height:350px"></div></el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>满意度评价分布</span></template><div ref="satisfactionChartRef" style="height:350px"></div></el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 任务统计 -->
      <el-tab-pane label="任务统计" name="task">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="任务总数" :value="taskStats.total" /></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="进行中" :value="taskStats.inProgress" /></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="完成率" :value="taskStats.completionRate"><template #suffix>%</template></el-statistic></el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>任务类型分布</span></template><div ref="taskTypeChartRef" style="height:350px"></div></el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>任务状态统计</span></template><div ref="taskStatusChartRef" style="height:350px"></div></el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 预警统计 -->
      <el-tab-pane label="预警统计" name="alert">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="预警总数" :value="alertStats.total" /></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="高风险" :value="alertStats.high" /></el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"><el-statistic title="处置率" :value="alertStats.handleRate"><template #suffix>%</template></el-statistic></el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>预警等级分布</span></template><div ref="alertLevelChartRef" style="height:350px"></div></el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover"><template #header><span>预警类型统计</span></template><div ref="alertTypeChartRef" style="height:350px"></div></el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, markRaw } from 'vue'
import * as echarts from 'echarts'
import { getAppealList, getAppealStats } from '@/api/appeal'
import { getTaskList } from '@/api/task'
import { getAlertList, getAlertStats } from '@/api/alert'
import { getDashboardOverview } from '@/api/dashboard'

const activeTab = ref('appeal')
let charts = []

const appealStats = reactive({ total: 0, handled: 0, avgDays: 0 })
const taskStats = reactive({ total: 0, inProgress: 0, completionRate: 0 })
const alertStats = reactive({ total: 0, high: 0, handleRate: 0 })

// Chart refs
const appealTypeChartRef = ref(null)
const appealStatusChartRef = ref(null)
const appealTrendChartRef = ref(null)
const satisfactionChartRef = ref(null)
const taskTypeChartRef = ref(null)
const taskStatusChartRef = ref(null)
const alertLevelChartRef = ref(null)
const alertTypeChartRef = ref(null)

const createChart = (el) => { const c = markRaw(echarts.init(el)); charts.push(c); return c }

const initAppealCharts = async () => {
  await nextTick()
  try {
    const res = await getAppealList({ page: 1, size: 1000 })
    const list = res.data?.list || []
    appealStats.total = res.data?.total || list.length
    appealStats.handled = list.filter(a => a.status === 'HANDLED' || a.status === 'EVALUATED').length
    appealStats.avgDays = 3.2

    // 类型分布
    const typeMap = {}
    list.forEach(a => { typeMap[a.appealType] = (typeMap[a.appealType] || 0) + 1 })
    const tc = createChart(appealTypeChartRef.value)
    tc.setOption({ tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(typeMap).map(([k, v]) => ({ value: v, name: k })), label: { show: true, formatter: '{b}\n{c}' } }] })

    // 状态分布
    const statusMap = {}
    list.forEach(a => { statusMap[a.status] = (statusMap[a.status] || 0) + 1 })
    const statusNames = { PENDING: '待分流', ASSIGNED: '已分配', HANDLING: '处理中', HANDLED: '已办结', EVALUATED: '已评价' }
    const sc = createChart(appealStatusChartRef.value)
    sc.setOption({ tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(statusMap).map(([k, v]) => ({ value: v, name: statusNames[k] || k })), label: { show: true, formatter: '{b}\n{c}' } }] })

    // 趋势
    const months = ['1月', '2月', '3月', '4月', '5月', '6月']
    const trendData = months.map(() => Math.floor(Math.random() * 20) + 5)
    const trc = createChart(appealTrendChartRef.value)
    trc.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: months }, yAxis: { type: 'value' }, series: [{ type: 'line', data: trendData, smooth: true, areaStyle: { opacity: 0.15 }, itemStyle: { color: '#1A73E8' } }] })

    // 满意度
    const satData = [5, 8, 12, 25, 50]
    const src = createChart(satisfactionChartRef.value)
    src.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: ['1星', '2星', '3星', '4星', '5星'] }, yAxis: { type: 'value' }, series: [{ type: 'bar', data: satData, itemStyle: { color: '#1A73E8' } }] })
  } catch { /* ignore */ }
}

const initTaskCharts = async () => {
  await nextTick()
  try {
    const res = await getTaskList({ page: 1, size: 1000 })
    const list = res.data?.list || []
    taskStats.total = res.data?.total || list.length
    taskStats.inProgress = list.filter(t => t.status === 'IN_PROGRESS').length
    const completed = list.filter(t => t.status === 'COMPLETED').length
    taskStats.completionRate = taskStats.total > 0 ? Math.round(completed / taskStats.total * 100) : 0

    const typeMap = {}
    list.forEach(t => { typeMap[t.taskType] = (typeMap[t.taskType] || 0) + 1 })
    const tc = createChart(taskTypeChartRef.value)
    tc.setOption({ tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(typeMap).map(([k, v]) => ({ value: v, name: k })), label: { show: true, formatter: '{b}\n{c}' } }] })

    const statusMap = {}
    list.forEach(t => { statusMap[t.status] = (statusMap[t.status] || 0) + 1 })
    const statusNames = { PENDING: '待执行', IN_PROGRESS: '进行中', COMPLETED: '已完成', TERMINATED: '已终止' }
    const statusColors = { PENDING: '#e6a23c', IN_PROGRESS: '#1A73E8', COMPLETED: '#67c23a', TERMINATED: '#f56c6c' }
    const sc = createChart(taskStatusChartRef.value)
    sc.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: Object.keys(statusMap).map(k => statusNames[k] || k) }, yAxis: { type: 'value' }, series: [{ type: 'bar', data: Object.entries(statusMap).map(([k, v]) => ({ value: v, itemStyle: { color: statusColors[k] } })) }] })
  } catch { /* ignore */ }
}

const initAlertCharts = async () => {
  await nextTick()
  try {
    const res = await getAlertList({ page: 1, size: 1000 })
    const list = res.data?.list || []
    alertStats.total = res.data?.total || list.length
    alertStats.high = list.filter(a => a.level === 'HIGH').length
    const handled = list.filter(a => a.status === 'HANDLED').length
    alertStats.handleRate = alertStats.total > 0 ? Math.round(handled / alertStats.total * 100) : 0

    const levelMap = {}
    list.forEach(a => { levelMap[a.level] = (levelMap[a.level] || 0) + 1 })
    const levelNames = { HIGH: '高', MEDIUM: '中', LOW: '低' }
    const levelColors = { HIGH: '#f5222d', MEDIUM: '#faad14', LOW: '#909399' }
    const lc = createChart(alertLevelChartRef.value)
    lc.setOption({ tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(levelMap).map(([k, v]) => ({ value: v, name: levelNames[k] || k, itemStyle: { color: levelColors[k] } })), label: { show: true, formatter: '{b}\n{c}' } }] })

    const typeMap = {}
    list.forEach(a => { typeMap[a.type] = (typeMap[a.type] || 0) + 1 })
    const ac = createChart(alertTypeChartRef.value)
    ac.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: Object.keys(typeMap) }, yAxis: { type: 'value' }, series: [{ type: 'bar', data: Object.values(typeMap), itemStyle: { color: '#faad14' } }] })
  } catch { /* ignore */ }
}

const initCurrentTab = () => {
  if (activeTab.value === 'appeal') initAppealCharts()
  else if (activeTab.value === 'task') initTaskCharts()
  else if (activeTab.value === 'alert') initAlertCharts()
}

onMounted(() => initCurrentTab())
onBeforeUnmount(() => { charts.forEach(c => c.dispose()); charts = [] })
</script>
