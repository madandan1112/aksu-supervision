<template>
  <div class="big-screen-container">
    <!-- 顶部标题 -->
    <header class="screen-header">
      <div class="header-bg">
        <div class="header-left-line"></div>
        <h1 class="header-title">阿克苏地区监督执法大数据平台</h1>
        <div class="header-right-line"></div>
      </div>
      <div class="header-right-info">
        <span class="header-username">
          <el-icon :size="14"><UserFilled /></el-icon>
          {{ currentUserName }}
        </span>
        <button class="header-back-btn" @click="goHome">
          <el-icon :size="14"><HomeFilled /></el-icon>
          返回主页
        </button>
        <span class="header-time">{{ currentTime }}</span>
      </div>
    </header>

    <!-- 主体内容区 -->
    <main class="screen-main">
      <!-- 核心指标 -->
      <section class="kpi-section">
        <div class="kpi-card" v-for="(item, i) in kpiData" :key="i">
          <div class="kpi-icon" :style="{ background: item.bg }">
            <el-icon :size="24" color="#fff"><component :is="item.icon" /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value" :style="{ color: item.color }">{{ item.value }}</div>
            <div class="kpi-label">{{ item.label }}</div>
          </div>
          <div class="kpi-trend" v-if="item.trend">
            <span :class="item.trend > 0 ? 'up' : 'down'">
              {{ item.trend > 0 ? '↑' : '↓' }} {{ Math.abs(item.trend) }}%
            </span>
          </div>
        </div>
      </section>

      <!-- 三栏布局 -->
      <section class="content-section">
        <!-- 左侧面板 -->
        <aside class="left-panel">
          <!-- 左上：企业行业分布 -->
          <div class="panel-box">
            <div class="panel-header">
              <span class="panel-title">企业行业分布</span>
              <span class="panel-sub">单位：家</span>
            </div>
            <div class="panel-body">
              <div ref="industryChartRef" class="chart-container"></div>
            </div>
          </div>

          <!-- 左下：诉求状态统计 -->
          <div class="panel-box">
            <div class="panel-header">
              <span class="panel-title">诉求状态统计</span>
              <span class="panel-sub">实时</span>
            </div>
            <div class="panel-body">
              <div ref="appealStatusChartRef" class="chart-container"></div>
            </div>
          </div>
        </aside>

        <!-- 中间面板 -->
        <div class="center-panel">
          <!-- 中间上部：地图/企业分布 -->
          <div class="panel-box map-panel">
            <div class="panel-header center">
              <span class="panel-title">区域企业分布</span>
            </div>
            <div class="panel-body">
              <div ref="mapChartRef" class="chart-container map-chart"></div>
            </div>
          </div>

          <!-- 中间下部：诉求趋势 -->
          <div class="panel-box trend-panel">
            <div class="panel-header">
              <span class="panel-title">月度诉求处理趋势</span>
              <span class="panel-sub">单位：件</span>
            </div>
            <div class="panel-body">
              <div ref="trendChartRef" class="chart-container"></div>
            </div>
          </div>
        </div>

        <!-- 右侧面板 -->
        <aside class="right-panel">
          <!-- 右上：整改完成率 -->
          <div class="panel-box">
            <div class="panel-header">
              <span class="panel-title">整改完成率</span>
              <span class="panel-sub">本月</span>
            </div>
            <div class="panel-body">
              <div ref="rectifyChartRef" class="chart-container"></div>
            </div>
          </div>

          <!-- 右下：预警等级分布 -->
          <div class="panel-box">
            <div class="panel-header">
              <span class="panel-title">预警等级分布</span>
              <span class="panel-sub">实时</span>
            </div>
            <div class="panel-body">
              <div ref="alertChartRef" class="chart-container"></div>
            </div>
          </div>
        </aside>
      </section>

      <!-- 底部滚动信息 -->
      <section class="bottom-section">
        <div class="scroll-box">
          <div class="scroll-label">最新动态</div>
          <div class="scroll-content">
            <span v-for="(msg, i) in scrollMessages" :key="i" class="scroll-item">
              <el-icon><Bell /></el-icon>
              {{ msg }}
            </span>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { OfficeBuilding, DocumentChecked, Warning, TrendCharts, Bell, UserFilled, HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()

// 当前用户名
const currentUserName = ref(localStorage.getItem('aksu_supervision_realName') || localStorage.getItem('aksu_supervision_username') || '管理员')

const goHome = () => {
  router.push('/dashboard')
}

// 时间显示
const currentTime = ref('')
let timeTimer = null
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

// KPI数据
const kpiData = [
  { label: '监管企业总数', value: '1,245', icon: 'OfficeBuilding', color: '#00D4FF', bg: 'linear-gradient(135deg, #C8102E 0%, #00D4FF 100%)', trend: 5.2 },
  { label: '本月诉求量', value: '186', icon: 'Document', color: '#FFD700', bg: 'linear-gradient(135deg, #FF8C00 0%, #FFD700 100%)', trend: 12.3 },
  { label: '待处理诉求', value: '42', icon: 'DocumentChecked', color: '#FF6B6B', bg: 'linear-gradient(135deg, #FF4757 0%, #FF6B6B 100%)', trend: -8.1 },
  { label: '整改完成率', value: '92.6%', icon: 'TrendCharts', color: '#52C41A', bg: 'linear-gradient(135deg, #52C41A 0%, #00D4FF 100%)', trend: 3.5 },
  { label: '执法人员', value: '68', icon: 'User', color: '#A78BFA', bg: 'linear-gradient(135deg, #7C3AED 0%, #A78BFA 100%)' },
  { label: '预警信息', value: '15', icon: 'Warning', color: '#FF6B6B', bg: 'linear-gradient(135deg, #DC2626 0%, #FF6B6B 100%)' }
]

// 滚动消息
const scrollMessages = [
  '阿克苏市某食品加工企业通过食品安全专项检查',
  '温宿县12家企业完成特种设备安全隐患整改',
  '拜城县开展春季产品质量抽检行动',
  '库车市市场监管局受理消费投诉23件已全部分派',
  '新和县开展"3·15"消费者权益保护宣传活动',
  '沙雅县完成第一季度企业合规报告审核'
]

// 图表引用
const industryChartRef = ref(null)
const appealStatusChartRef = ref(null)
const mapChartRef = ref(null)
const trendChartRef = ref(null)
const rectifyChartRef = ref(null)
const alertChartRef = ref(null)
let charts = []

const createChart = (el) => {
  if (!el) return null
  const c = markRaw(echarts.init(el, null, { renderer: 'canvas' }))
  charts.push(c)
  return c
}

// 通用深色主题配置
const darkTheme = {
  backgroundColor: 'transparent',
  textStyle: { color: '#8B9DC3' },
  tooltip: {
    backgroundColor: 'rgba(10, 22, 40, 0.9)',
    borderColor: '#C8102E',
    borderWidth: 1,
    textStyle: { color: '#fff' }
  }
}

const initCharts = () => {
  // 1. 企业行业分布（环形图）
  const industryChart = createChart(industryChartRef.value)
  if (industryChart) {
    industryChart.setOption({
      ...darkTheme,
      tooltip: { trigger: 'item', formatter: '{b}: {c}家 ({d}%)' },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center',
        textStyle: { color: '#8B9DC3', fontSize: 11 },
        itemWidth: 10,
        itemHeight: 10
      },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['65%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 5,
          borderColor: '#060B26',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#fff' },
          itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 212, 255, 0.5)' }
        },
        data: [
          { value: 342, name: '食品制造', itemStyle: { color: '#C8102E' } },
          { value: 218, name: '纺织服装', itemStyle: { color: '#00D4FF' } },
          { value: 156, name: '建材加工', itemStyle: { color: '#52C41A' } },
          { value: 198, name: '化工生产', itemStyle: { color: '#FFD700' } },
          { value: 187, name: '农林牧渔', itemStyle: { color: '#FF6B6B' } },
          { value: 144, name: '其他行业', itemStyle: { color: '#A78BFA' } }
        ]
      }]
    })
  }

  // 2. 诉求状态统计（仪表盘+柱状图组合）
  const appealChart = createChart(appealStatusChartRef.value)
  if (appealChart) {
    appealChart.setOption({
      ...darkTheme,
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
      xAxis: {
        type: 'category',
        data: ['待处理', '处理中', '已处理', '已评价'],
        axisLine: { lineStyle: { color: '#1A3A5C' } },
        axisLabel: { color: '#8B9DC3', fontSize: 11 }
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#1A3A5C', type: 'dashed' } },
        axisLabel: { color: '#8B9DC3', fontSize: 11 }
      },
      series: [{
        type: 'bar',
        barWidth: '40%',
        data: [
          { value: 42, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#FF6B6B' }, { offset: 1, color: '#FF4757' }] } } },
          { value: 68, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#FFD700' }, { offset: 1, color: '#FF8C00' }] } } },
          { value: 156, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#52C41A' }, { offset: 1, color: '#00D4FF' }] } } },
          { value: 89, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#C8102E' }, { offset: 1, color: '#00D4FF' }] } } }
        ],
        itemStyle: { borderRadius: [4, 4, 0, 0] }
      }]
    })
  }

  // 3. 区域企业分布（阿克苏地区7县2市真实地理坐标）
  const mapChart = createChart(mapChartRef.value)
  if (mapChart) {
    // 阿克苏地区7县2市真实地理坐标（经度, 纬度）
    const regions = [
      { name: '阿克苏市', coord: [80.26, 41.17], value: 425 },
      { name: '库车市', coord: [82.96, 41.72], value: 312 },
      { name: '温宿县', coord: [80.24, 41.28], value: 198 },
      { name: '拜城县', coord: [81.87, 41.80], value: 156 },
      { name: '新和县', coord: [82.61, 41.55], value: 134 },
      { name: '沙雅县', coord: [82.78, 41.22], value: 178 },
      { name: '乌什县', coord: [79.23, 41.30], value: 98 },
      { name: '阿瓦提县', coord: [80.37, 40.64], value: 145 },
      { name: '柯坪县', coord: [79.05, 40.51], value: 87 }
    ]

    // 计算坐标范围以设置地图视窗
    const lngs = regions.map(r => r.coord[0])
    const lats = regions.map(r => r.coord[1])
    const lngMin = Math.min(...lngs) - 0.5
    const lngMax = Math.max(...lngs) + 0.5
    const latMin = Math.min(...lats) - 0.5
    const latMax = Math.max(...lats) + 0.5

    mapChart.setOption({
      ...darkTheme,
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          if (params.seriesType === 'effectScatter') {
            const d = regions[params.dataIndex]
            return `<div style="font-weight:bold;color:#00D4FF">${d.name}</div>企业数量：${d.value}家`
          }
          return params.name
        }
      },
      // 使用散点坐标系模拟地图
      xAxis: {
        show: false,
        min: lngMin,
        max: lngMax
      },
      yAxis: {
        show: false,
        min: latMin,
        max: latMax
      },
      series: [
        // 连接线 - 各县市与阿克苏市的连线
        {
          type: 'lines',
          coordinateSystem: 'cartesian2d',
          z: 1,
          effect: {
            show: true,
            period: 4,
            trailLength: 0.3,
            symbol: 'arrow',
            symbolSize: 5,
            color: '#C8102E'
          },
          lineStyle: {
            color: 'rgba(26, 115, 232, 0.3)',
            width: 1,
            curveness: 0.2
          },
          data: regions.filter(r => r.name !== '阿克苏市').map(r => ({
            coords: [[80.26, 41.17], r.coord]
          }))
        },
        // 涟漪效果散点 - 各区县
        {
          type: 'effectScatter',
          coordinateSystem: 'cartesian2d',
          symbolSize: (val) => Math.max(15, Math.sqrt(val[2]) * 1.2),
          data: regions.map(r => ({
            value: [...r.coord, r.value],
            name: r.name
          })),
          rippleEffect: {
            brushType: 'stroke',
            scale: 3,
            period: 3
          },
          itemStyle: {
            color: (params) => {
              const colors = ['#00D4FF', '#C8102E', '#52C41A', '#FFD700', '#FF6B6B', '#A78BFA', '#FF8C00', '#00FF88', '#FF69B4']
              return colors[params.dataIndex % colors.length]
            },
            shadowBlur: 10,
            shadowColor: 'rgba(0, 212, 255, 0.5)'
          },
          label: {
            show: true,
            formatter: (params) => {
              const d = regions[params.dataIndex]
              return `{name|${d.name}}\n{val|${d.value}家}`
            },
            rich: {
              name: {
                fontSize: 12,
                fontWeight: 'bold',
                color: '#fff',
                lineHeight: 18
              },
              val: {
                fontSize: 11,
                color: '#00D4FF',
                lineHeight: 16
              }
            },
            position: 'top',
            distance: 10
          }
        }
      ]
    })
  }

  // 4. 月度诉求处理趋势（折线+柱状图混合）
  const trendChart = createChart(trendChartRef.value)
  if (trendChart) {
    trendChart.setOption({
      ...darkTheme,
      tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
      legend: {
        data: ['新增诉求', '处理完成'],
        textStyle: { color: '#8B9DC3' },
        top: 0
      },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月'],
        axisLine: { lineStyle: { color: '#1A3A5C' } },
        axisLabel: { color: '#8B9DC3', fontSize: 11 }
      },
      yAxis: [
        {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#1A3A5C', type: 'dashed' } },
          axisLabel: { color: '#8B9DC3', fontSize: 11 }
        }
      ],
      series: [
        {
          name: '新增诉求',
          type: 'bar',
          barWidth: '30%',
          data: [45, 52, 48, 63, 58, 72],
          itemStyle: {
            color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#C8102E' }, { offset: 1, color: '#00D4FF' }] },
            borderRadius: [4, 4, 0, 0]
          }
        },
        {
          name: '处理完成',
          type: 'line',
          smooth: true,
          data: [38, 45, 42, 55, 52, 65],
          lineStyle: { color: '#FFD700', width: 3 },
          itemStyle: { color: '#FFD700', borderWidth: 2 },
          areaStyle: {
            color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(255, 215, 0, 0.3)' }, { offset: 1, color: 'rgba(255, 215, 0, 0)' }] }
          }
        }
      ]
    })
  }

  // 5. 整改完成率（仪表盘）
  const rectifyChart = createChart(rectifyChartRef.value)
  if (rectifyChart) {
    rectifyChart.setOption({
      ...darkTheme,
      series: [{
        type: 'gauge',
        startAngle: 200,
        endAngle: -20,
        min: 0,
        max: 100,
        splitNumber: 10,
        radius: '90%',
        center: ['50%', '55%'],
        axisLine: {
          lineStyle: {
            width: 15,
            color: [
              [0.3, '#FF6B6B'],
              [0.7, '#FFD700'],
              [1, '#52C41A']
            ]
          }
        },
        pointer: {
          itemStyle: { color: '#00D4FF' },
          width: 6
        },
        axisTick: { show: false },
        splitLine: { length: 10, lineStyle: { color: '#1A3A5C', width: 2 } },
        axisLabel: { color: '#8B9DC3', fontSize: 10, distance: 20 },
        title: {
          offsetCenter: [0, '70%'],
          fontSize: 14,
          color: '#8B9DC3'
        },
        detail: {
          valueAnimation: true,
          fontSize: 36,
          fontWeight: 'bold',
          color: '#00D4FF',
          offsetCenter: [0, '0%'],
          formatter: '{value}%'
        },
        data: [{ value: 92.6, name: '整改完成率' }]
      }]
    })
  }

  // 6. 预警等级分布（横向柱状图）
  const alertChart = createChart(alertChartRef.value)
  if (alertChart) {
    alertChart.setOption({
      ...darkTheme,
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '8%', bottom: '3%', top: '3%', containLabel: true },
      xAxis: {
        type: 'value',
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#1A3A5C', type: 'dashed' } },
        axisLabel: { color: '#8B9DC3', fontSize: 11 }
      },
      yAxis: {
        type: 'category',
        data: ['食品安全', '特种设备', '产品质量', '环保合规', '消防隐患'],
        axisLine: { lineStyle: { color: '#1A3A5C' } },
        axisLabel: { color: '#8B9DC3', fontSize: 11 }
      },
      series: [{
        type: 'bar',
        barWidth: '50%',
        data: [
          { value: 8, itemStyle: { color: '#FF6B6B', borderRadius: [0, 4, 4, 0] } },
          { value: 5, itemStyle: { color: '#FFD700', borderRadius: [0, 4, 4, 0] } },
          { value: 3, itemStyle: { color: '#FF8C00', borderRadius: [0, 4, 4, 0] } },
          { value: 2, itemStyle: { color: '#C8102E', borderRadius: [0, 4, 4, 0] } },
          { value: 1, itemStyle: { color: '#52C41A', borderRadius: [0, 4, 4, 0] } }
        ],
        label: {
          show: true,
          position: 'right',
          color: '#fff',
          fontSize: 12,
          formatter: '{c}条'
        }
      }]
    })
  }
}

// 响应式处理
const handleResize = () => {
  charts.forEach(c => c?.resize())
}

// 自动刷新数据
let refreshTimer = null
const refreshData = () => {
  // 随机微调KPI数据
  kpiData.forEach(item => {
    if (item.trend !== undefined) {
      const change = (Math.random() - 0.5) * 2
      item.trend = Number((item.trend + change).toFixed(1))
    }
  })
}

onMounted(() => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
  initCharts()
  window.addEventListener('resize', handleResize)
  refreshTimer = setInterval(refreshData, 30000)
})

onBeforeUnmount(() => {
  if (timeTimer) clearInterval(timeTimer)
  if (refreshTimer) clearInterval(refreshTimer)
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c?.dispose())
  charts = []
})
</script>

<style lang="scss" scoped>
.big-screen-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #060B26 0%, #0A1628 50%, #060B26 100%);
  color: #fff;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background:
      radial-gradient(ellipse at 50% 0%, rgba(26, 115, 232, 0.15) 0%, transparent 70%),
      radial-gradient(ellipse at 0% 100%, rgba(0, 212, 255, 0.05) 0%, transparent 50%),
      radial-gradient(ellipse at 100% 100%, rgba(0, 212, 255, 0.05) 0%, transparent 50%);
    pointer-events: none;
    z-index: 0;
  }
}

// 顶部标题
.screen-header {
  position: relative;
  z-index: 1;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 24px;

  .header-bg {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    max-width: 800px;

    .header-left-line,
    .header-right-line {
      flex: 1;
      height: 2px;
      background: linear-gradient(90deg, transparent, #C8102E, #00D4FF);
    }

    .header-right-line {
      background: linear-gradient(90deg, #00D4FF, #C8102E, transparent);
    }

    .header-title {
      font-size: 26px;
      font-weight: 700;
      color: #fff;
      text-shadow: 0 0 20px rgba(26, 115, 232, 0.5), 0 0 40px rgba(0, 212, 255, 0.3);
      padding: 0 24px;
      white-space: nowrap;
      letter-spacing: 4px;
    }
  }

  .header-right-info {
    position: absolute;
    right: 24px;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    align-items: center;
    gap: 12px;

    .header-username {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #00D4FF;
      background: rgba(10, 22, 40, 0.6);
      padding: 5px 12px;
      border: 1px solid rgba(26, 115, 232, 0.3);
      border-radius: 4px;
    }

    .header-back-btn {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #fff;
      background: linear-gradient(135deg, rgba(26, 115, 232, 0.6) 0%, rgba(0, 212, 255, 0.4) 100%);
      border: 1px solid rgba(26, 115, 232, 0.4);
      border-radius: 4px;
      padding: 5px 12px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: linear-gradient(135deg, rgba(26, 115, 232, 0.8) 0%, rgba(0, 212, 255, 0.6) 100%);
        border-color: rgba(0, 212, 255, 0.6);
        box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
      }
    }

    .header-time {
      font-size: 14px;
      color: #00D4FF;
      font-family: 'Courier New', monospace;
      text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
      background: rgba(10, 22, 40, 0.6);
      padding: 5px 12px;
      border: 1px solid rgba(26, 115, 232, 0.3);
      border-radius: 4px;
    }
  }
}

// 主体内容
.screen-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 16px 16px;
  gap: 12px;
  position: relative;
  z-index: 1;
  overflow: hidden;
}

// KPI区域
.kpi-section {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
  height: 90px;
  flex-shrink: 0;

  .kpi-card {
    background: rgba(10, 22, 40, 0.6);
    border: 1px solid rgba(26, 115, 232, 0.2);
    border-radius: 8px;
    display: flex;
    align-items: center;
    padding: 0 16px;
    gap: 12px;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      border-color: rgba(26, 115, 232, 0.5);
      box-shadow: 0 0 20px rgba(26, 115, 232, 0.2);
    }

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 3px;
      height: 100%;
      background: linear-gradient(180deg, #C8102E, #00D4FF);
    }

    .kpi-icon {
      width: 44px;
      height: 44px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .kpi-info {
      flex: 1;
      min-width: 0;

      .kpi-value {
        font-size: 24px;
        font-weight: 700;
        line-height: 1.2;
        text-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
      }

      .kpi-label {
        font-size: 12px;
        color: #8B9DC3;
        margin-top: 2px;
      }
    }

    .kpi-trend {
      font-size: 11px;
      font-weight: 600;

      .up { color: #52C41A; }
      .down { color: #FF6B6B; }
    }
  }
}

// 内容三栏
.content-section {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1.4fr 1fr;
  gap: 12px;
  min-height: 0;
}

.left-panel,
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 0;
}

.center-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 0;
}

// 面板通用样式
.panel-box {
  background: rgba(10, 22, 40, 0.5);
  border: 1px solid rgba(26, 115, 232, 0.2);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex: 1;
  min-height: 0;

  &.map-panel {
    flex: 1.5;
  }

  &.trend-panel {
    flex: 1;
  }

  .panel-header {
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    border-bottom: 1px solid rgba(26, 115, 232, 0.15);
    background: rgba(26, 115, 232, 0.05);
    flex-shrink: 0;

    &.center {
      justify-content: center;
    }

    .panel-title {
      font-size: 15px;
      font-weight: 600;
      color: #fff;
      position: relative;
      padding-left: 12px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 16px;
        background: linear-gradient(180deg, #C8102E, #00D4FF);
        border-radius: 2px;
      }
    }

    .panel-sub {
      font-size: 11px;
      color: #8B9DC3;
      background: rgba(26, 115, 232, 0.1);
      padding: 2px 8px;
      border-radius: 10px;
    }
  }

  .panel-body {
    flex: 1;
    padding: 8px;
    min-height: 0;

    .chart-container {
      width: 100%;
      height: 100%;
    }

    .map-chart {
      background: radial-gradient(ellipse at center, rgba(26, 115, 232, 0.05) 0%, transparent 70%);
    }
  }
}

// 底部滚动信息
.bottom-section {
  height: 40px;
  flex-shrink: 0;

  .scroll-box {
    height: 100%;
    background: rgba(10, 22, 40, 0.6);
    border: 1px solid rgba(26, 115, 232, 0.2);
    border-radius: 8px;
    display: flex;
    align-items: center;
    padding: 0 16px;
    gap: 16px;
    overflow: hidden;

    .scroll-label {
      font-size: 13px;
      font-weight: 600;
      color: #FFD700;
      white-space: nowrap;
      text-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
    }

    .scroll-content {
      flex: 1;
      display: flex;
      gap: 40px;
      animation: scrollLeft 30s linear infinite;
      white-space: nowrap;

      .scroll-item {
        font-size: 13px;
        color: #8B9DC3;
        display: flex;
        align-items: center;
        gap: 6px;

        :deep(.el-icon) {
          color: #00D4FF;
        }
      }
    }
  }
}

@keyframes scrollLeft {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

// 响应式
@media (max-width: 1400px) {
  .kpi-section {
    grid-template-columns: repeat(3, 1fr);
    height: auto;

    .kpi-card {
      padding: 12px;
    }
  }

  .content-section {
    grid-template-columns: 1fr 1.2fr 1fr;
  }
}

@media (max-width: 1024px) {
  .content-section {
    grid-template-columns: 1fr;
    overflow-y: auto;
  }

  .kpi-section {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
