<template>
  <div class="statistics-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">数据统计</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <template v-if="isLoggedIn && userInfo.role === 3">
              <router-link to="/admin/coffee" class="nav-link">
                <span class="nav-text">咖啡管理</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/admin/statistics" class="nav-link active">
                <span class="nav-text">数据统计</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/admin/add-admin" class="nav-link">
                <span class="nav-text">添加管理员</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <template v-if="!isLoggedIn">
              <router-link to="/login" class="nav-link nav-link--highlight">
                <span class="nav-text">登录</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            <template v-else>
              <router-link to="/profile" class="nav-link nav-link--user">
                <span class="user-avatar">{{ userInfo.username?.charAt(0) }}</span>
                <span class="nav-text">{{ userInfo.username }}</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
          </nav>
        </div>
      </header>

      <!-- 主内容区域 -->
      <main class="statistics-main">
        <div class="statistics-wrapper">
          <!-- 装饰性线条 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>

          <!-- 页面标题 -->
          <div class="page-header">
            <h1 class="page-title">
              <span class="title-word title-word--1">数据</span>
              <span class="title-word title-word--2">统计</span>
            </h1>
            <p class="page-subtitle">洞察数据 · 驱动决策</p>
          </div>

          <!-- 统计卡片 -->
          <div class="stats-grid">
            <div class="stat-card stat-card--primary">
              <div class="stat-icon">💰</div>
              <div class="stat-content">
                <span class="stat-value">¥{{ formatNumber(totalSales) }}</span>
                <span class="stat-label">历史总销售额</span>
              </div>
              <div class="stat-decoration"></div>
            </div>
            
            <div class="stat-card stat-card--secondary">
              <div class="stat-icon">☕</div>
              <div class="stat-content">
                <span class="stat-value">{{ formatNumber(totalCoffeesSold) }}</span>
                <span class="stat-label">历史总销量（杯）</span>
              </div>
              <div class="stat-decoration"></div>
            </div>
            
            <div class="stat-card stat-card--accent">
              <div class="stat-icon">📈</div>
              <div class="stat-content">
                <span class="stat-value">¥{{ formatNumber(pastSevenDaysSales) }}</span>
                <span class="stat-label">过去七天销售额</span>
              </div>
              <div class="stat-decoration"></div>
            </div>
            
            <div class="stat-card stat-card--warm">
              <div class="stat-icon">🛒</div>
              <div class="stat-content">
                <span class="stat-value">{{ formatNumber(pastSevenDaysQuantity) }}</span>
                <span class="stat-label">过去七天销量（杯）</span>
              </div>
              <div class="stat-decoration"></div>
            </div>
          </div>

          <!-- 图表区域 - 第一行 -->
          <div class="charts-row">
            <div class="chart-card">
              <div class="chart-header">
                <span class="chart-icon">📊</span>
                <h3 class="chart-title">各类别历史销售额</h3>
                <p class="chart-subtitle">按咖啡类别统计总销售额</p>
              </div>
              <div class="chart-body">
                <div ref="categorySalesChartRef" class="chart-container"></div>
              </div>
            </div>

            <div class="chart-card">
              <div class="chart-header">
                <span class="chart-icon">🏆</span>
                <h3 class="chart-title">热销咖啡排行榜</h3>
                <p class="chart-subtitle">历史总销量 Top 10</p>
              </div>
              <div class="chart-body">
                <div ref="coffeeSalesChartRef" class="chart-container"></div>
              </div>
            </div>
          </div>

          <!-- 图表区域 - 第二行 -->
          <div class="charts-row">
            <div class="chart-card">
              <div class="chart-header">
                <span class="chart-icon">📈</span>
                <h3 class="chart-title">过去七天销售额趋势</h3>
                <p class="chart-subtitle">每日销售额变化</p>
              </div>
              <div class="chart-body">
                <div ref="dailySalesChartRef" class="chart-container"></div>
              </div>
            </div>

            <div class="chart-card">
              <div class="chart-header">
                <span class="chart-icon">📉</span>
                <h3 class="chart-title">过去七天销量趋势</h3>
                <p class="chart-subtitle">每日销量变化</p>
              </div>
              <div class="chart-body">
                <div ref="dailyQuantityChartRef" class="chart-container"></div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  getTotalSalesAmount, 
  getTotalCoffeesSold, 
  getCategorySalesStats, 
  getTopSellingCoffee, 
  getDailySalesForLastWeek, 
  getDailyQuantityForLastWeek 
} from '@/api/admin'

export default {
  name: 'StatisticsView',
  setup() {
    const totalSales = ref(0)
    const totalCoffeesSold = ref(0)
    const pastSevenDaysSales = ref(0)
    const pastSevenDaysQuantity = ref(0)
    
    const categorySalesChartRef = ref(null)
    const coffeeSalesChartRef = ref(null)
    const dailySalesChartRef = ref(null)
    const dailyQuantityChartRef = ref(null)
    
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const cursorRef = ref(null)
    const headerRef = ref(null)
    
    let chartInstances = []

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadData()
      }
      initCustomCursor()
    })

    onUnmounted(() => {
      window.removeEventListener('mousemove', handleMouseMove)
      // 销毁所有图表实例
      chartInstances.forEach(chart => chart.dispose())
    })

    const handleMouseMove = (e) => {
      if (cursorRef.value) {
        cursorRef.value.style.left = e.clientX + 'px'
        cursorRef.value.style.top = e.clientY + 'px'
      }
    }

    const initCustomCursor = () => {
      window.addEventListener('mousemove', handleMouseMove)
    }

    const checkLoginStatus = () => {
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        if (userInfo.value.role !== 3) {
          ElMessage.error('您没有权限访问此页面')
          window.location.href = '/'
        }
      } else {
        ElMessage.warning('请先登录')
        window.location.href = '/login'
      }
    }

    const formatNumber = (num) => {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w'
      }
      return num.toFixed(2).replace(/\.00$/, '')
    }

    const loadData = async () => {
      try {
        // 获取历史总销售额
        const totalSalesRes = await getTotalSalesAmount()
        if (totalSalesRes.code === 200) {
          totalSales.value = totalSalesRes.data || 0
        }

        // 获取历史总销量
        const totalCoffeesSoldRes = await getTotalCoffeesSold()
        if (totalCoffeesSoldRes.code === 200) {
          totalCoffeesSold.value = totalCoffeesSoldRes.data || 0
        }

        // 获取过去七天销售额
        const dailySalesRes = await getDailySalesForLastWeek()
        if (dailySalesRes.code === 200) {
          const dailySalesData = dailySalesRes.data || []
          pastSevenDaysSales.value = dailySalesData.reduce((sum, day) => sum + (day.sales || 0), 0)
        }

        // 加载各类别销售统计
        const categoryStatsResponse = await getCategorySalesStats()
        if (categoryStatsResponse.code === 200) {
          const stats = categoryStatsResponse.data || []
          const categoryNames = stats.map(item => item.category || '未知类别')
          const categorySales = stats.map(item => item.totalSales || 0)
          await nextTick()
          renderCategorySalesChart(categoryNames, categorySales)
        }

        // 加载热销咖啡数据
        const topSellingResponse = await getTopSellingCoffee(10)
        if (topSellingResponse.code === 200) {
          let topSellingData = topSellingResponse.data || []
          topSellingData.sort((a, b) => (b.sales || 0) - (a.sales || 0))
          const coffeeNames = topSellingData.map(coffee => coffee.name)
          const coffeeSales = topSellingData.map(coffee => coffee.sales)
          await nextTick()
          renderCoffeeSalesChart(coffeeNames, coffeeSales)
        }
        
        // 加载过去七天销售额数据
        const dailySalesResponse = await getDailySalesForLastWeek()
        if (dailySalesResponse.code === 200) {
          const dailySalesData = dailySalesResponse.data || []
          const dates = dailySalesData.map(item => formatChartDate(item))
          const sales = dailySalesData.map(item => extractSalesValue(item))
          await nextTick()
          renderDailySalesChart(dates, sales)
        }
        
        // 加载过去七天销量数据
        const dailyQuantityResponse = await getDailyQuantityForLastWeek()
        if (dailyQuantityResponse.code === 200) {
          const dailyQuantityData = dailyQuantityResponse.data || []
          
          pastSevenDaysQuantity.value = dailyQuantityData.reduce((sum, day) => {
            const qty = extractQuantityValue(day)
            return sum + qty
          }, 0)
          
          const quantityDates = dailyQuantityData.map(item => formatChartDate(item))
          const quantity = dailyQuantityData.map(item => extractQuantityValue(item))
          await nextTick()
          renderDailyQuantityChart(quantityDates, quantity)
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
        ElMessage.error('加载统计数据失败')
      }
    }

    const formatChartDate = (item) => {
      let dateStr = item.date || item.DATE || item['DATE(order_time)'] || item['date(order_time)']
      if (!dateStr || dateStr === '未知日期') return '未知'
      const dateObj = new Date(dateStr)
      const month = String(dateObj.getMonth() + 1).padStart(2, '0')
      const day = String(dateObj.getDate()).padStart(2, '0')
      return `${month}-${day}`
    }

    const extractSalesValue = (item) => {
      if (item.sales !== undefined) return parseFloat(item.sales) || 0
      if (item.SALES !== undefined) return parseFloat(item.SALES) || 0
      if (item['IFNULL(SUM(total_amount), 0)'] !== undefined) return parseFloat(item['IFNULL(SUM(total_amount), 0)']) || 0
      return 0
    }

    const extractQuantityValue = (item) => {
      if (item.quantity !== undefined) return parseInt(item.quantity) || 0
      if (item.QUANTITY !== undefined) return parseInt(item.QUANTITY) || 0
      if (item['IFNULL(SUM(quantity), 0)'] !== undefined) return parseInt(item['IFNULL(SUM(quantity), 0)']) || 0
      return 0
    }

    // 咖啡色系配色
    const chartColors = {
      primary: '#D4A373',      // 金色
      secondary: '#C8795C',    // 赤陶
      accent: '#7D9B7A',       // 鼠尾草绿
      warm: '#E8B86D',         // 蜂蜜色
      dark: '#3A241A',         // 深烘焙棕
      espresso: '#1E1B1A',     // 浓缩黑
      cream: '#FDF8F2',        // 奶油白
      warmBrown: '#5C3A2A'     // 暖棕色
    }



    const renderCategorySalesChart = (categories, sales) => {
      if (!categorySalesChartRef.value) return
      
      const chart = echarts.init(categorySalesChartRef.value)
      chartInstances.push(chart)
      
      // 计算最大值用于背景效果
      const maxSales = Math.max(...sales)
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: 'rgba(253, 248, 242, 0.98)',
          borderColor: chartColors.primary,
          borderWidth: 1,
          borderRadius: 12,
          padding: [12, 16],
          textStyle: { color: chartColors.espresso, fontFamily: 'DM Sans', fontSize: 13 },
          formatter: (params) => {
            const data = params[0]
            return `
              <div style="display: flex; flex-direction: column; gap: 8px;">
                <div style="display: flex; align-items: center; gap: 8px;">
                  <span style="display: inline-block; width: 10px; height: 10px; border-radius: 3px; background: linear-gradient(135deg, ${chartColors.primary}, ${chartColors.secondary});"></span>
                  <span style="font-weight: 600;">${data.name}</span>
                </div>
                <div style="display: flex; align-items: baseline; gap: 12px;">
                  <span style="font-size: 12px; color: ${chartColors.warmBrown};">销售额</span>
                  <span style="font-size: 20px; font-weight: 700; color: ${chartColors.secondary};">¥${data.value.toFixed(2)}</span>
                </div>
              </div>
            `
          }
        },
        grid: { left: '1%', right: '0%', bottom: '0%', top: '12%', containLabel: true },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 12, 
            fontWeight: 600,
            margin: 12
          },
          axisLine: { 
            lineStyle: { 
              color: chartColors.primary, 
              width: 2,
              opacity: 0.4
            } 
          },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          name: '销售额 (元)',
          nameTextStyle: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 12, 
            fontWeight: 500,
            padding: [0, 0, 0, 0]
          },
          axisLabel: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 11,
            formatter: (value) => {
              if (value >= 1000) return (value / 1000).toFixed(0) + 'k'
              return value
            }
          },
          splitLine: { 
            lineStyle: { 
              color: chartColors.primary, 
              opacity: 0.08, 
              type: 'dashed',
              width: 1
            } 
          }
        },
        series: [{
          name: '销售额',
          type: 'bar',
          data: sales,
          barWidth: 55,
          itemStyle: {
            borderRadius: [12, 12, 0, 0],
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: chartColors.warm },
                { offset: 0.5, color: chartColors.primary },
                { offset: 1, color: chartColors.secondary }
              ]
            }
          },
          label: {
            show: true,
            position: 'top',
            color: chartColors.secondary,
            fontFamily: 'DM Sans',
            fontSize: 11,
            fontWeight: 600,
            formatter: (params) => {
              if (params.value >= 1000) return '¥' + (params.value / 1000).toFixed(1) + 'k'
              return '¥' + params.value
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(212, 163, 115, 0.06)',
            borderRadius: [12, 12, 0, 0]
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 20,
              shadowColor: chartColors.secondary + '40',
              borderRadius: [12, 12, 0, 0]
            }
          }
        }],
        // 添加视觉装饰
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: 5,
            style: {
              text: '☕ 各类别销售对比',
              fill: chartColors.warmBrown,
              fontFamily: 'Playfair Display',
              fontSize: 13,
              fontWeight: 500,
              opacity: 0.4
            }
          }
        ]
      }
      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }

    const renderCoffeeSalesChart = (names, sales) => {
      if (!coffeeSalesChartRef.value) return
      
      const chart = echarts.init(coffeeSalesChartRef.value)
      chartInstances.push(chart)
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: 'rgba(253, 248, 242, 0.98)',
          borderColor: chartColors.accent,
          borderWidth: 1,
          borderRadius: 12,
          padding: [12, 16],
          textStyle: { color: chartColors.espresso, fontFamily: 'DM Sans', fontSize: 13 },
          formatter: (params) => {
            const data = params[0]
            const rank = data.dataIndex + 1
            let medal = ''
            if (rank === 1) medal = '🥇'
            else if (rank === 2) medal = '🥈'
            else if (rank === 3) medal = '🥉'
            
            return `
              <div style="display: flex; flex-direction: column; gap: 8px;">
                <div style="display: flex; align-items: center; gap: 8px;">
                  <span style="display: inline-block; width: 10px; height: 10px; border-radius: 3px; background: linear-gradient(135deg, ${chartColors.accent}, #5C7A5A);"></span>
                  <span style="font-weight: 600;">${medal} ${data.name}</span>
                </div>
                <div style="display: flex; align-items: baseline; gap: 12px;">
                  <span style="font-size: 12px; color: ${chartColors.warmBrown};">销量</span>
                  <span style="font-size: 20px; font-weight: 700; color: ${chartColors.accent};">${data.value} 杯</span>
                </div>
                <div style="font-size: 11px; color: ${chartColors.warmBrown}; opacity: 0.7;">
                  排名第 ${rank} 位
                </div>
              </div>
            `
          }
        },
        grid: { left: '0%', right: '0%', bottom: '0%', top: '12%', containLabel: true },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 11, 
            rotate: 35,
            fontWeight: 500,
            margin: 14,
            interval: 0
          },
          axisLine: { 
            lineStyle: { 
              color: chartColors.accent, 
              width: 2,
              opacity: 0.4
            } 
          },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          name: '销量 (杯)',
          nameTextStyle: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 12, 
            fontWeight: 500
          },
          axisLabel: { color: chartColors.warmBrown, fontFamily: 'DM Sans', fontSize: 11 },
          splitLine: { 
            lineStyle: { 
              color: chartColors.accent, 
              opacity: 0.08, 
              type: 'dashed',
              width: 1
            } 
          }
        },
        series: [{
          name: '销量',
          type: 'bar',
          data: sales,
          barWidth: 35,
          itemStyle: {
            borderRadius: [10, 10, 0, 0],
            color: (params) => {
              // 前三名使用不同颜色
              if (params.dataIndex === 0) {
                return {
                  type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
                  colorStops: [
                    { offset: 0, color: '#FFD700' },
                    { offset: 1, color: '#DAA520' }
                  ]
                }
              } else if (params.dataIndex === 1) {
                return {
                  type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
                  colorStops: [
                    { offset: 0, color: '#C0C0C0' },
                    { offset: 1, color: '#A9A9A9' }
                  ]
                }
              } else if (params.dataIndex === 2) {
                return {
                  type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
                  colorStops: [
                    { offset: 0, color: '#CD7F32' },
                    { offset: 1, color: '#B8860B' }
                  ]
                }
              }
              return {
                type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: chartColors.accent },
                  { offset: 1, color: '#5C7A5A' }
                ]
              }
            }
          },
          label: {
            show: true,
            position: 'top',
            color: (params) => {
              if (params.dataIndex === 0) return '#B8860B'
              if (params.dataIndex === 1) return '#808080'
              if (params.dataIndex === 2) return '#8B6914'
              return chartColors.accent
            },
            fontFamily: 'DM Sans',
            fontSize: 10,
            fontWeight: 600
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(125, 155, 122, 0.05)',
            borderRadius: [10, 10, 0, 0]
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 20,
              shadowColor: chartColors.accent + '40',
              borderRadius: [10, 10, 0, 0]
            }
          }
        }],
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: 5,
            style: {
              text: '🏆 热销排行榜 Top 10',
              fill: chartColors.warmBrown,
              fontFamily: 'Playfair Display',
              fontSize: 13,
              fontWeight: 500,
              opacity: 0.4
            }
          }
        ]
      }
      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }

    const renderDailySalesChart = (dates, sales) => {
      if (!dailySalesChartRef.value) return
      
      const chart = echarts.init(dailySalesChartRef.value)
      chartInstances.push(chart)
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(253, 248, 242, 0.98)',
          borderColor: chartColors.warm,
          borderWidth: 1,
          borderRadius: 12,
          padding: [12, 16],
          textStyle: { color: chartColors.espresso, fontFamily: 'DM Sans', fontSize: 13 },
          formatter: (params) => {
            const data = params[0]
            return `
              <div style="display: flex; flex-direction: column; gap: 8px;">
                <div style="display: flex; align-items: center; gap: 8px;">
                  <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background: ${chartColors.warm};"></span>
                  <span style="font-weight: 600;">${data.name}</span>
                </div>
                <div style="display: flex; align-items: baseline; gap: 12px;">
                  <span style="font-size: 12px; color: ${chartColors.warmBrown};">销售额</span>
                  <span style="font-size: 20px; font-weight: 700; color: ${chartColors.warm};">¥${data.value.toFixed(2)}</span>
                </div>
              </div>
            `
          }
        },
        grid: { left: '1%', right: '3%', bottom: '0%', top: '12%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 11, 
            fontWeight: 500,
            margin: 10
          },
          axisLine: { 
            lineStyle: { 
              color: chartColors.warm, 
              width: 2,
              opacity: 0.4
            } 
          },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          name: '销售额 (元)',
          nameTextStyle: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 12, 
            fontWeight: 500
          },
          axisLabel: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 11,
            formatter: (value) => {
              if (value >= 1000) return (value / 1000).toFixed(0) + 'k'
              return value
            }
          },
          splitLine: { 
            lineStyle: { 
              color: chartColors.warm, 
              opacity: 0.08, 
              type: 'dashed',
              width: 1
            } 
          }
        },
        series: [{
          name: '销售额',
          type: 'line',
          data: sales,
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { 
            color: chartColors.warm, 
            width: 3,
            shadowBlur: 15,
            shadowColor: chartColors.warm + '60'
          },
          itemStyle: { 
            color: chartColors.secondary,
            borderColor: chartColors.cream,
            borderWidth: 2
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: chartColors.warm + '50' },
              { offset: 0.5, color: chartColors.warm + '20' },
              { offset: 1, color: chartColors.warm + '02' }
            ])
          },
          label: {
            show: true,
            position: 'top',
            color: chartColors.warm,
            fontFamily: 'DM Sans',
            fontSize: 10,
            fontWeight: 600,
            formatter: (params) => {
              if (params.value >= 1000) return '¥' + (params.value / 1000).toFixed(1) + 'k'
              return '¥' + params.value
            }
          },
          emphasis: {
            focus: 'series',
            itemStyle: {
              shadowBlur: 20,
              shadowColor: chartColors.warm + '80'
            }
          }
        }],
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: 5,
            style: {
              text: '📈 销售额七日趋势',
              fill: chartColors.warmBrown,
              fontFamily: 'Playfair Display',
              fontSize: 13,
              fontWeight: 500,
              opacity: 0.4
            }
          }
        ]
      }
      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }

    const renderDailyQuantityChart = (dates, quantity) => {
      if (!dailyQuantityChartRef.value) return
      
      const chart = echarts.init(dailyQuantityChartRef.value)
      chartInstances.push(chart)
      
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(253, 248, 242, 0.98)',
          borderColor: chartColors.secondary,
          borderWidth: 1,
          borderRadius: 12,
          padding: [12, 16],
          textStyle: { color: chartColors.espresso, fontFamily: 'DM Sans', fontSize: 13 },
          formatter: (params) => {
            const data = params[0]
            return `
              <div style="display: flex; flex-direction: column; gap: 8px;">
                <div style="display: flex; align-items: center; gap: 8px;">
                  <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background: ${chartColors.secondary};"></span>
                  <span style="font-weight: 600;">${data.name}</span>
                </div>
                <div style="display: flex; align-items: baseline; gap: 12px;">
                  <span style="font-size: 12px; color: ${chartColors.warmBrown};">销量</span>
                  <span style="font-size: 20px; font-weight: 700; color: ${chartColors.secondary};">${data.value} 杯</span>
                </div>
              </div>
            `
          }
        },
        grid: { left: '1%', right: '3%', bottom: '0%', top: '12%', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 11, 
            fontWeight: 500,
            margin: 10
          },
          axisLine: { 
            lineStyle: { 
              color: chartColors.secondary, 
              width: 2,
              opacity: 0.4
            } 
          },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'value',
          name: '销量 (杯)',
          nameTextStyle: { 
            color: chartColors.warmBrown, 
            fontFamily: 'DM Sans', 
            fontSize: 12, 
            fontWeight: 500
          },
          axisLabel: { color: chartColors.warmBrown, fontFamily: 'DM Sans', fontSize: 11 },
          splitLine: { 
            lineStyle: { 
              color: chartColors.secondary, 
              opacity: 0.08, 
              type: 'dashed',
              width: 1
            } 
          }
        },
        series: [{
          name: '销量',
          type: 'line',
          data: quantity,
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { 
            color: chartColors.secondary, 
            width: 3,
            shadowBlur: 15,
            shadowColor: chartColors.secondary + '60'
          },
          itemStyle: { 
            color: chartColors.dark,
            borderColor: chartColors.crema,
            borderWidth: 2
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: chartColors.secondary + '50' },
              { offset: 0.5, color: chartColors.secondary + '20' },
              { offset: 1, color: chartColors.secondary + '02' }
            ])
          },
          label: {
            show: true,
            position: 'top',
            color: chartColors.terracotta,
            fontFamily: 'DM Sans',
            fontSize: 10,
            fontWeight: 600
          },
          emphasis: {
            focus: 'series',
            itemStyle: {
              shadowBlur: 20,
              shadowColor: chartColors.secondary + '80'
            }
          }
        }],
        graphic: [
          {
            type: 'text',
            left: 'center',
            top: 5,
            style: {
              text: '📉 销量七日趋势',
              fill: chartColors.warmBrown,
              fontFamily: 'Playfair Display',
              fontSize: 13,
              fontWeight: 500,
              opacity: 0.4
            }
          }
        ]
      }
      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }

    return {
      totalSales,
      totalCoffeesSold,
      pastSevenDaysSales,
      pastSevenDaysQuantity,
      categorySalesChartRef,
      coffeeSalesChartRef,
      dailySalesChartRef,
      dailyQuantityChartRef,
      isLoggedIn,
      userInfo,
      cursorRef,
      headerRef,
      formatNumber
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.statistics-page {
  --espresso: #1E1B1A;
  --dark-roast: #3A241A;
  --warm-brown: #5C3A2A;
  --crema: #FDF8F2;
  --cream: #FFF5E6;
  --golden: #D4A373;
  --honey: #E8B86D;
  --terracotta: #C8795C;
  --sage: #7D9B7A;
  
  min-height: 100vh;
  background-color: var(--crema);
  position: relative;
  overflow-x: hidden;
  font-family: 'DM Sans', 'Quicksand', sans-serif;
}

/* ===== 背景纹理 ===== */
.background-texture {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-radial-gradient(circle at 20% 30%, rgba(212, 163, 115, 0.02) 0px, transparent 2px),
    repeating-linear-gradient(45deg, rgba(58, 36, 26, 0.015) 0px, rgba(58, 36, 26, 0.015) 1px, transparent 1px, transparent 8px);
  pointer-events: none;
  z-index: 0;
}

.background-gradient {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 80% 20%, rgba(232, 184, 109, 0.05) 0%, transparent 50%),
              radial-gradient(circle at 10% 80%, rgba(125, 155, 122, 0.04) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* ===== 自定义光标 ===== */
.custom-cursor {
  position: fixed;
  width: 24px;
  height: 24px;
  border: 1.5px solid var(--golden);
  border-radius: 50%;
  pointer-events: none;
  z-index: 99999;
  transform: translate(-50%, -50%);
  transition: width 0.2s, height 0.2s;
  box-shadow: 0 0 15px rgba(212, 163, 115, 0.3);
}

/* ===== 页面容器 ===== */
.page-container {
  position: relative;
  z-index: 1;
  min-height: 100vh;
}

/* ===== 头部导航 ===== */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 20px 40px;
  background: rgba(253, 248, 242, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(212, 163, 115, 0.15);
}

.header-inner {
  max-width: 1600px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.logo-area:hover { transform: scale(1.02); }
.logo-icon { font-size: 32px; animation: steamFloat 3s ease-in-out infinite; }
.logo-text { display: flex; flex-direction: column; }
.logo-main { font-family: 'Playfair Display', serif; font-size: 24px; font-weight: 600; color: var(--dark-roast); }
.logo-sub { font-family: 'DM Sans', sans-serif; font-size: 14px; color: var(--warm-brown); letter-spacing: 2px; }

.nav-menu { display: flex; align-items: center; gap: 32px; }
.nav-link {
  position: relative;
  text-decoration: none;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--dark-roast);
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: color 0.3s ease;
}
.nav-link:hover { color: var(--terracotta); }
.nav-link.active { color: var(--terracotta); }
.nav-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--golden), var(--terracotta));
  transition: width 0.3s ease;
}
.nav-link:hover .nav-underline,
.nav-link.active .nav-underline { width: 100%; }
.nav-link--user { gap: 6px; }
.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--golden), var(--terracotta));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--crema);
  font-weight: 600;
  font-size: 14px;
}

/* ===== 主内容区域 ===== */
.statistics-main { padding-top: 120px; padding-bottom: 60px; min-height: 100vh; }
.statistics-wrapper { max-width: 1500px; margin: 0 auto; padding: 0 40px; position: relative; }

/* ===== 装饰线条 ===== */
.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.1;
  pointer-events: none;
}
.decorative-line--1 { top: 20px; right: -50px; width: 250px; height: 1px; transform: rotate(-15deg); }
.decorative-line--2 { bottom: 100px; left: -80px; width: 300px; height: 1px; transform: rotate(-10deg); }

/* ===== 页面标题 ===== */
.page-header { margin-bottom: 36px; }
.page-title {
  font-family: 'Playfair Display', serif;
  font-size: 42px;
  font-weight: 400;
  margin: 0 0 8px 0;
  display: flex;
  gap: 16px;
}
.title-word { display: inline-block; opacity: 0; animation: titleReveal 0.6s ease forwards; }
.title-word--1 { color: var(--espresso); font-style: italic; animation-delay: 0.1s; }
.title-word--2 { color: var(--dark-roast); font-weight: 600; animation-delay: 0.3s; }
.page-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  letter-spacing: 3px;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.5s forwards;
}

/* ===== 统计卡片 ===== */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; margin-bottom: 40px; }
.stat-card {
  position: relative;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 24px;
  padding: 28px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  overflow: hidden;
  transition: all 0.3s ease;
  opacity: 0;
  animation: cardReveal 0.5s ease forwards;
}
.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }
.stat-card:hover { transform: translateY(-4px); box-shadow: 0 12px 30px rgba(58, 36, 26, 0.1); }
.stat-icon { font-size: 42px; opacity: 0.8; }
.stat-content { display: flex; flex-direction: column; }
.stat-value { font-family: 'Playfair Display', serif; font-size: 36px; font-weight: 600; line-height: 1.1; }
.stat-label { font-family: 'DM Sans', sans-serif; font-size: 13px; color: var(--warm-brown); opacity: 0.7; margin-top: 4px; }
.stat-decoration {
  position: absolute;
  bottom: -30px;
  right: -30px;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(212, 163, 115, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}
.stat-card--primary .stat-value { color: var(--golden); }
.stat-card--secondary .stat-value { color: var(--terracotta); }
.stat-card--accent .stat-value { color: var(--sage); }
.stat-card--warm .stat-value { color: var(--honey); }

/* ===== 图表行 ===== */
.charts-row { display: grid; grid-template-columns: repeat(2, 1fr); gap: 24px; margin-bottom: 30px; }
.chart-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 24px;
  overflow: hidden;
  transition: all 0.3s ease;
  opacity: 0;
  animation: cardReveal 0.5s ease forwards;
  animation-delay: 0.5s;
}
.chart-card:hover { box-shadow: 0 12px 30px rgba(58, 36, 26, 0.08); }
.chart-header {
  padding: 24px 24px 0 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.chart-icon { font-size: 24px; }
.chart-title { font-family: 'Playfair Display', serif; font-size: 18px; font-weight: 500; color: var(--espresso); margin: 0; }
.chart-subtitle { font-family: 'DM Sans', sans-serif; font-size: 12px; color: var(--warm-brown); opacity: 0.6; margin: 0 0 0 36px; width: 100%; }
.chart-body { padding: 16px 20px 20px; }
.chart-container { width: 100%; height: 350px; }

/* 图表卡片悬浮效果增强 */
.chart-card {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.chart-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 40px rgba(58, 36, 26, 0.12);
  border-color: rgba(212, 163, 115, 0.3);
}

.chart-container {
  transition: all 0.3s ease;
}

.chart-card:hover .chart-container {
  transform: scale(1.01);
}

/* ===== 动画 ===== */
@keyframes titleReveal { 0% { opacity: 0; transform: translateY(30px); } 100% { opacity: 1; transform: translateY(0); } }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes cardReveal { 0% { opacity: 0; transform: translateY(20px); } 100% { opacity: 1; transform: translateY(0); } }
@keyframes steamFloat { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-3px); } }

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .charts-row { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .app-header { padding: 16px 20px; }
  .statistics-wrapper { padding: 0 20px; }
  .page-title { font-size: 32px; }
  .stats-grid { grid-template-columns: 1fr; }
  .stat-value { font-size: 28px; }
  .chart-container { height: 280px; }
}
</style>