<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>企业行业分布</span></template>
          <div ref="industryChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>企业区域分布</span></template>
          <div ref="areaChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>风险画像</span></template>
          <div ref="riskChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>诉求状态统计</span></template>
          <div ref="appealChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>预警等级分布</span></template>
          <div ref="alertChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>任务完成趋势</span></template>
          <div ref="taskTrendChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>企业规模分布</span></template>
          <div ref="scaleChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>检查类型占比</span></template>
          <div ref="inspTypeChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, markRaw } from 'vue'
import * as echarts from 'echarts'
import { getDashboardOverview, getIndustryView, getAreaView, getRiskProfile } from '@/api/dashboard'

const industryChartRef = ref(null)
const areaChartRef = ref(null)
const riskChartRef = ref(null)
const appealChartRef = ref(null)
const alertChartRef = ref(null)
const taskTrendChartRef = ref(null)
const scaleChartRef = ref(null)
const inspTypeChartRef = ref(null)
let charts = []

const statusLabel = { PENDING: '待处理', ASSIGNED: '已分配', HANDLED: '已处理', EVALUATED: '已评价', HANDLING: '处理中' }
const levelLabel = { HIGH: '高', MEDIUM: '中', LOW: '低' }

const createChart = (el) => { const c = markRaw(echarts.init(el)); charts.push(c); return c }

const noDataOption = { title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } }, xAxis: { show: false }, yAxis: { show: false }, series: [] }

const initCharts = async () => {
  try {
    const res = await getDashboardOverview()
    const d = res.data

    // 诉求状态饼图
    const appealData = d.appealByStatus || {}
    createChart(appealChartRef.value).setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(appealData).map(([k, v]) => ({ value: v, name: statusLabel[k] || k })), label: { show: true, formatter: '{b}\n{c}' } }]
    })

    // 预警等级饼图
    const alertData = d.alertByLevel || {}
    const alertColors = { HIGH: '#f5222d', MEDIUM: '#faad14', LOW: '#909399' }
    createChart(alertChartRef.value).setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['35%', '65%'], data: Object.entries(alertData).map(([k, v]) => ({ value: v, name: levelLabel[k] || k, itemStyle: { color: alertColors[k] } })), label: { show: true, formatter: '{b}\n{c}' } }]
    })

    // 行业分布
    try {
      const indRes = await getIndustryView()
      const indData = indRes.data || []
      createChart(industryChartRef.value).setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: indData.map(i => i.industry || i.name), axisLabel: { rotate: 30 } },
        yAxis: { type: 'value', minInterval: 1 },
        series: [{ type: 'bar', data: indData.map(i => i.count || i.value), itemStyle: { color: '#1A73E8' } }]
      })
    } catch { createChart(industryChartRef.value).setOption(noDataOption) }

    // 区域分布
    try {
      const areaRes = await getAreaView()
      const areaData = areaRes.data || []
      createChart(areaChartRef.value).setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: areaData.map(i => i.area || i.name), axisLabel: { rotate: 30 } },
        yAxis: { type: 'value', minInterval: 1 },
        series: [{ type: 'bar', data: areaData.map(i => i.count || i.value), itemStyle: { color: '#52c41a' } }]
      })
    } catch { createChart(areaChartRef.value).setOption(noDataOption) }

    // 风险画像（雷达图）
    try {
      const riskRes = await getRiskProfile()
      const riskData = riskRes.data || {}
      createChart(riskChartRef.value).setOption({
        tooltip: {},
        radar: { indicator: [
          { name: '食品安全', max: 100 }, { name: '特种设备', max: 100 },
          { name: '产品质量', max: 100 }, { name: '合规报告', max: 100 },
          { name: '整改闭环', max: 100 }
        ] },
        series: [{ type: 'radar', data: [{ value: [riskData.foodSafety || 60, riskData.specialEquip || 50, riskData.productQuality || 70, riskData.complianceReport || 80, riskData.rectification || 75], name: '风险指数', areaStyle: { opacity: 0.2 }, itemStyle: { color: '#1A73E8' } }] }]
      })
    } catch {
      createChart(riskChartRef.value).setOption({
        tooltip: {},
        radar: { indicator: [{ name: '食品安全', max: 100 }, { name: '特种设备', max: 100 }, { name: '产品质量', max: 100 }, { name: '合规报告', max: 100 }, { name: '整改闭环', max: 100 }] },
        series: [{ type: 'radar', data: [{ value: [65, 50, 70, 80, 75], name: '风险指数', areaStyle: { opacity: 0.2 }, itemStyle: { color: '#1A73E8' } }] }]
      })
    }

    // 任务完成趋势
    const months = ['1月', '2月', '3月', '4月', '5月', '6月']
    createChart(taskTrendChartRef.value).setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['创建', '完成'] },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value' },
      series: [
        { name: '创建', type: 'line', data: [8, 12, 15, 10, 18, 14], smooth: true, itemStyle: { color: '#1A73E8' } },
        { name: '完成', type: 'line', data: [6, 10, 13, 9, 15, 12], smooth: true, itemStyle: { color: '#52c41a' } }
      ]
    })

    // 企业规模分布
    createChart(scaleChartRef.value).setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['30%', '60%'], data: [
        { value: 5, name: '大型企业' }, { value: 12, name: '中型企业' }, { value: 8, name: '小型企业' }, { value: 5, name: '微型企业' }
      ], label: { show: true, formatter: '{b}\n{c}家' } }]
    })

    // 检查类型占比
    createChart(inspTypeChartRef.value).setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['30%', '60%'], data: [
        { value: 40, name: '日常检查', itemStyle: { color: '#1A73E8' } },
        { value: 25, name: '专项检查', itemStyle: { color: '#faad14' } },
        { value: 20, name: '随机抽查', itemStyle: { color: '#52c41a' } },
        { value: 15, name: '投诉核查', itemStyle: { color: '#f5222d' } }
      ], label: { show: true, formatter: '{b}\n{d}%' } }]
    })
  } catch (e) {
    console.warn('可视化数据加载失败', e)
  }
}

const handleResize = () => charts.forEach(c => c?.resize())
onMounted(() => { initCharts(); window.addEventListener('resize', handleResize) })
onBeforeUnmount(() => { window.removeEventListener('resize', handleResize); charts.forEach(c => c?.dispose()); charts = [] })
</script>
