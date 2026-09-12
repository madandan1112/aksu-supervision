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

      <!-- 报表管理 -->
      <el-tab-pane label="报表管理" name="report">
        <div class="report-toolbar">
          <div class="toolbar-left">
            <el-select v-model="reportType" placeholder="选择报表类型" style="width: 180px">
              <el-option label="诉求报表" value="appeal" />
              <el-option label="任务报表" value="task" />
              <el-option label="检查报表" value="inspection" />
              <el-option label="整改报表" value="rectification" />
              <el-option label="企业报表" value="enterprise" />
            </el-select>
            <el-date-picker
              v-model="reportDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 280px"
            />
            <el-button type="primary" @click="generateReport">生成报表</el-button>
          </div>
          <div class="toolbar-right">
            <el-upload
              :show-file-list="false"
              :before-upload="handleImport"
              accept=".xlsx,.xls,.csv"
            >
              <el-button type="success">
                <el-icon><Upload /></el-icon> 导入识别
              </el-button>
            </el-upload>
            <el-dropdown @command="handleExport" style="margin-left: 12px">
              <el-button type="warning">
                <el-icon><Download /></el-icon> 导出
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="xlsx">导出 Excel</el-dropdown-item>
                  <el-dropdown-item command="csv">导出 CSV</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- 报表预览 -->
        <el-card shadow="never" style="margin-top: 16px">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>{{ reportTitle }}</span>
              <el-tag v-if="reportData.length" type="info" size="small">共 {{ reportData.length }} 条</el-tag>
            </div>
          </template>
          <el-table :data="reportData" border stripe size="small" v-loading="reportLoading" max-height="500">
            <el-table-column v-for="col in reportColumns" :key="col.key" :prop="col.key" :label="col.label" :min-width="col.width || 120" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="!reportLoading && !reportData.length" description="请选择报表类型并生成报表" />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, markRaw, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, Download } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import * as XLSX from 'xlsx'
import { getAppealList } from '@/api/appeal'
import { getTaskList } from '@/api/task'
import { getAlertList } from '@/api/alert'
import { getEnterpriseList } from '@/api/enterprise'
import { getAdminInspectionList, getAdminRectificationList } from '@/api/inspection'

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

// 报表相关
const reportType = ref('appeal')
const reportDateRange = ref(null)
const reportData = ref([])
const reportColumns = ref([])
const reportLoading = ref(false)
const reportTitle = ref('报表预览')

const createChart = (el) => { const c = markRaw(echarts.init(el)); charts.push(c); return c }

const initAppealCharts = async () => {
  await nextTick()
  try {
    const res = await getAppealList({ page: 1, size: 1000 })
    const list = res.data?.list || []
    appealStats.total = res.data?.total || list.length
    appealStats.handled = list.filter(a => a.status === 'HANDLED' || a.status === 'EVALUATED').length
    // 平均处理时长：由创建时间→处理时间真实计算（无处理时间的不计入）
    const durations = list.filter(a => a.createTime && a.handleTime)
      .map(a => (new Date(a.handleTime) - new Date(a.createTime)) / 86400000)
    appealStats.avgDays = durations.length > 0 ? (durations.reduce((s, d) => s + d, 0) / durations.length).toFixed(1) : 0

    // 诉求类型分布
    const appealTypeNames = { COMPLAINT: '投诉', CONSULT: '咨询', OTHER: '其他' }
    const typeMap = {}
    list.forEach(a => {
      const key = appealTypeNames[a.appealType] || a.appealType || '未分类'
      typeMap[key] = (typeMap[key] || 0) + 1
    })
    const tc = createChart(appealTypeChartRef.value)
    tc.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(typeMap).map(([k, v]) => ({ value: v, name: k })), label: { show: true, formatter: '{b}\n{c} ({d}%)' }, emphasis: { label: { fontSize: 14, fontWeight: 'bold' } }, itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 } }] })

    // 诉求状态分布
    const statusMap = {}
    list.forEach(a => { statusMap[a.status] = (statusMap[a.status] || 0) + 1 })
    const statusNames = { PENDING: '待分流', ASSIGNED: '已分配', HANDLING: '处理中', HANDLED: '已办结', EVALUATED: '已评价' }
    const statusColors = { PENDING: '#e6a23c', ASSIGNED: '#C8102E', HANDLING: '#909399', HANDLED: '#67c23a', EVALUATED: '#722ed1' }
    const appealTotal = list.length
    const sc = createChart(appealStatusChartRef.value)
    sc.setOption({ tooltip: { trigger: 'axis', formatter: (p) => p.map(i => `${i.name}: ${i.value}条 (${appealTotal > 0 ? Math.round(i.value / appealTotal * 100) : 0}%)`).join('<br/>') }, xAxis: { type: 'category', data: Object.keys(statusMap).map(k => statusNames[k] || k) }, yAxis: { type: 'value', minInterval: 1 }, series: [{ type: 'bar', data: Object.entries(statusMap).map(([k, v]) => ({ value: v, itemStyle: { color: statusColors[k] || '#C8102E' } })), label: { show: true, position: 'top', formatter: (p) => `${p.value}条${appealTotal > 0 ? ` (${Math.round(p.value / appealTotal * 100)}%)` : ''}` } }] })

    // 诉求趋势（近6月，按创建时间）
    const months = []
    const now = new Date()
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
      months.push({ key: `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`, label: `${d.getMonth() + 1}月` })
    }
    const trendMap = Object.fromEntries(months.map(m => [m.key, 0]))
    list.forEach(a => {
      if (!a.createTime) return
      const key = a.createTime.slice(0, 7)
      if (key in trendMap) trendMap[key]++
    })
    const trendChart = createChart(appealTrendChartRef.value)
    trendChart.setOption({ tooltip: { trigger: 'axis' }, xAxis: { type: 'category', data: months.map(m => m.label) }, yAxis: { type: 'value', minInterval: 1 }, series: [{ type: 'line', smooth: true, data: months.map(m => trendMap[m.key]), areaStyle: { opacity: 0.15 }, itemStyle: { color: '#C8102E' } }] })

    // 满意度评价分布（1-5星，未评价不计入）
    const satMap = {}
    list.forEach(a => {
      if (a.satisfaction != null) satMap[a.satisfaction] = (satMap[a.satisfaction] || 0) + 1
    })
    const satLabels = { 1: '1星 很不满意', 2: '2星 不满意', 3: '3星 一般', 4: '4星 满意', 5: '5星 非常满意' }
    const satColors = { 1: '#f5222d', 2: '#fa8c16', 3: '#fadb14', 4: '#73d13d', 5: '#52c41a' }
    const satChart = createChart(satisfactionChartRef.value)
    satChart.setOption(Object.keys(satMap).length > 0 ? {
      tooltip: { trigger: 'item', formatter: '{b}: {c}条 ({d}%)' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(satMap).map(([k, v]) => ({ value: v, name: satLabels[k] || `${k}星`, itemStyle: { color: satColors[k] } })), label: { show: true, formatter: '{b}\n{c}条' } }]
    } : { title: { text: '暂无评价数据', left: 'center', top: 'middle', textStyle: { color: '#999', fontSize: 14, fontWeight: 'normal' } } })
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

    // 任务类型分布
    const taskTypeNames = { DAILY: '日常检查', SPECIAL: '专项检查', COMPLAINT: '投诉核查', RANDOM: '随机抽查' }
    const typeMap = {}
    list.forEach(t => {
      const key = taskTypeNames[t.taskType] || t.taskType || '未分类'
      typeMap[key] = (typeMap[key] || 0) + 1
    })
    const tc = createChart(taskTypeChartRef.value)
    tc.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(typeMap).map(([k, v]) => ({ value: v, name: k })), label: { show: true, formatter: '{b}\n{c} ({d}%)' }, emphasis: { label: { fontSize: 14, fontWeight: 'bold' } }, itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 } }] })

    // 任务状态统计
    const statusMap = {}
    list.forEach(t => { statusMap[t.status] = (statusMap[t.status] || 0) + 1 })
    const statusNames = { PENDING: '待执行', IN_PROGRESS: '进行中', COMPLETED: '已完成', TERMINATED: '已终止' }
    const statusColors = { PENDING: '#e6a23c', IN_PROGRESS: '#C8102E', COMPLETED: '#67c23a', TERMINATED: '#f56c6c' }
    const taskTotal = list.length
    const sc = createChart(taskStatusChartRef.value)
    sc.setOption({ tooltip: { trigger: 'axis', formatter: (p) => p.map(i => `${i.name}: ${i.value}条 (${taskTotal > 0 ? Math.round(i.value / taskTotal * 100) : 0}%)`).join('<br/>') }, xAxis: { type: 'category', data: Object.keys(statusMap).map(k => statusNames[k] || k) }, yAxis: { type: 'value', minInterval: 1 }, series: [{ type: 'bar', data: Object.entries(statusMap).map(([k, v]) => ({ value: v, itemStyle: { color: statusColors[k] } })), label: { show: true, position: 'top', formatter: (p) => `${p.value}条${taskTotal > 0 ? ` (${Math.round(p.value / taskTotal * 100)}%)` : ''}` } }] })
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
    lc.setOption({ tooltip: { trigger: 'item', formatter: '{b}: {c}条 ({d}%)' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(levelMap).map(([k, v]) => ({ value: v, name: levelNames[k] || k, itemStyle: { color: levelColors[k] } })), label: { show: true, formatter: '{b}\n{c}条 ({d}%)' }, emphasis: { label: { fontSize: 14, fontWeight: 'bold' } }, itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 } }] })

    const typeMap = {}
    list.forEach(a => { typeMap[a.alertType] = (typeMap[a.alertType] || 0) + 1 })
    const alertTotal = list.length || 1
    const alertTypeNames = { LICENSE_EXPIRING: '许可证即将到期', LICENSE_EXPIRED: '许可证已过期', RECTIFICATION_OVERDUE: '整改超期未反馈', INSPECTION_OVERDUE: '检查任务超期', REPORT_MISSING: '合规报告未提交', REGISTRATION_ANOMALY: '注册备案异常', CREDIT_ANOMALY: '信用异常', REPEAT_VIOLATION: '屡次违规' }
    const ac = createChart(alertTypeChartRef.value)
    ac.setOption({ tooltip: { trigger: 'axis', formatter: (p) => p.map(i => `${i.name}: ${i.value}条 (${Math.round(i.value / alertTotal * 100)}%)`).join('<br/>') }, xAxis: { type: 'category', data: Object.keys(typeMap).map(k => alertTypeNames[k] || k || '未分类') }, yAxis: { type: 'value', minInterval: 1 }, series: [{ type: 'bar', data: Object.values(typeMap).map(v => ({ value: v, itemStyle: { color: '#faad14' } })), label: { show: true, position: 'top', formatter: (p) => `${p.value}条 (${Math.round(p.value / alertTotal * 100)}%)` } }] })
  } catch { /* ignore */ }
}

// 报表生成
const generateReport = async () => {
  reportLoading.value = true
  reportData.value = []
  try {
    if (reportType.value === 'appeal') {
      reportTitle.value = '诉求报表'
      reportColumns.value = [
        { key: 'id', label: 'ID', width: 60 },
        { key: 'appealType', label: '诉求类型', width: 100 },
        { key: 'title', label: '标题', width: 200 },
        { key: 'status', label: '状态', width: 80 },
        { key: 'createTime', label: '创建时间', width: 160 }
      ]
      const res = await getAppealList({ page: 1, size: 9999 })
      reportData.value = res.data?.list || []
    } else if (reportType.value === 'task') {
      reportTitle.value = '任务报表'
      reportColumns.value = [
        { key: 'id', label: 'ID', width: 60 },
        { key: 'taskType', label: '任务类型', width: 100 },
        { key: 'title', label: '标题', width: 200 },
        { key: 'status', label: '状态', width: 80 },
        { key: 'createTime', label: '创建时间', width: 160 }
      ]
      const res = await getTaskList({ page: 1, size: 9999 })
      reportData.value = res.data?.list || []
    } else if (reportType.value === 'inspection') {
      reportTitle.value = '检查报表'
      reportColumns.value = [
        { key: 'id', label: 'ID', width: 60 },
        { key: 'enterpriseName', label: '企业名称', width: 160 },
        { key: 'inspectorName', label: '检查人员', width: 100 },
        { key: 'status', label: '状态', width: 80 },
        { key: 'checkType', label: '检查类型', width: 120 },
        { key: 'createTime', label: '创建时间', width: 160 }
      ]
      const res = await getAdminInspectionList({ page: 1, size: 9999 })
      reportData.value = res.data?.content || res.data?.items || []
    } else if (reportType.value === 'rectification') {
      reportTitle.value = '整改报表'
      reportColumns.value = [
        { key: 'id', label: 'ID', width: 60 },
        { key: 'noticeNo', label: '通知书编号', width: 160 },
        { key: 'enterpriseId', label: '企业ID', width: 80 },
        { key: 'status', label: '状态', width: 80 },
        { key: 'deadline', label: '整改期限', width: 100 },
        { key: 'createTime', label: '创建时间', width: 160 }
      ]
      const res = await getAdminRectificationList({ page: 1, size: 9999 })
      reportData.value = res.data?.content || res.data?.items || []
    } else if (reportType.value === 'enterprise') {
      reportTitle.value = '企业报表'
      reportColumns.value = [
        { key: 'id', label: 'ID', width: 60 },
        { key: 'enterpriseName', label: '企业名称', width: 160 },
        { key: 'creditCode', label: '信用代码', width: 180 },
        { key: 'industry', label: '行业', width: 80 },
        { key: 'area', label: '区域', width: 80 },
        { key: 'legalPerson', label: '法人', width: 80 }
      ]
      const res = await getEnterpriseList({ page: 1, size: 9999 })
      reportData.value = res.data?.list || []
    }
    ElMessage.success(`报表已生成，共 ${reportData.value.length} 条数据`)
  } catch (e) {
    ElMessage.error('生成报表失败')
  } finally {
    reportLoading.value = false
  }
}

// 导入识别
const handleImport = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target.result)
      const workbook = XLSX.read(data, { type: 'array' })
      const sheet = workbook.Sheets[workbook.SheetNames[0]]
      const json = XLSX.utils.sheet_to_json(sheet)

      if (json.length === 0) {
        ElMessage.warning('文件内容为空')
        return false
      }

      // 自动识别列
      const keys = Object.keys(json[0])
      reportColumns.value = keys.map(k => ({ key: k, label: k, width: 120 }))
      reportData.value = json
      reportTitle.value = `导入数据 - ${file.name}`
      ElMessage.success(`导入成功，识别到 ${json.length} 条数据，${keys.length} 个字段`)
    } catch (err) {
      ElMessage.error('文件解析失败，请检查文件格式')
    }
  }
  reader.readAsArrayBuffer(file)
  return false // 阻止自动上传
}

// 导出
const handleExport = (format) => {
  if (!reportData.value.length) {
    return ElMessage.warning('请先生成报表')
  }

  const exportData = reportData.value.map(row => {
    const obj = {}
    reportColumns.value.forEach(col => {
      obj[col.label] = row[col.key] ?? ''
    })
    return obj
  })

  const ws = XLSX.utils.json_to_sheet(exportData)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '报表')

  if (format === 'xlsx') {
    XLSX.writeFile(wb, `${reportTitle.value}.xlsx`)
    ElMessage.success('Excel 文件已导出')
  } else {
    XLSX.writeFile(wb, `${reportTitle.value}.csv`)
    ElMessage.success('CSV 文件已导出')
  }
}

const initCurrentTab = () => {
  if (activeTab.value === 'appeal') initAppealCharts()
  else if (activeTab.value === 'task') initTaskCharts()
  else if (activeTab.value === 'alert') initAlertCharts()
}

// 切换 tab 时初始化对应图表（隐藏 pane 内 echarts 无法正确测量尺寸，必须激活后再 init）
const initializedTabs = new Set([activeTab.value])
watch(activeTab, (tab) => {
  if (initializedTabs.has(tab)) return
  initializedTabs.add(tab)
  initCurrentTab()
})

onMounted(() => initCurrentTab())
onBeforeUnmount(() => { charts.forEach(c => c.dispose()); charts = [] })
</script>

<style scoped>
.report-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.toolbar-right {
  display: flex;
  align-items: center;
}
</style>
