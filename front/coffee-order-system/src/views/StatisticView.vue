<template>
  <div class="statistics-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统 - 数据统计</h1>
          <div class="nav-links">
            <!-- 顾客功能 -->
            <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/menu')">菜单</el-button>
            <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/cart')">购物车</el-button>
            <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/orders')">我的订单</el-button>
            <!-- 店员功能 -->
            <el-button v-if="isLoggedIn && userInfo.role === 2" type="text" @click="$router.push('/staff')">待处理订单</el-button>
            <el-button v-if="isLoggedIn && userInfo.role === 2" type="text" @click="$router.push('/staff/all-orders')">全部订单</el-button>
            <!-- 管理员功能 -->
            <el-button v-if="isLoggedIn && userInfo.role === 3" type="text" @click="$router.push('/admin/coffee')">咖啡管理</el-button>
            <el-button v-if="isLoggedIn && userInfo.role === 3" type="text" @click="$router.push('/admin/statistics')">数据统计</el-button>
            <el-button v-if="isLoggedIn && userInfo.role === 3" type="text" @click="$router.push('/admin/add-admin')">添加管理员</el-button>
            <!-- 公有功能 -->
            <el-button v-if="!isLoggedIn" type="text" @click="$router.push('/login')">登录</el-button>
            <el-button v-else type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <!-- 统计卡片 -->
        <div class="stats-cards">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">¥{{ totalSales.toFixed(2) }}</div>
              <div class="stat-label">历史总销售额</div>
            </div>
          </el-card>
          
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalCoffeesSold }}</div>
              <div class="stat-label">历史总销量（杯）</div>
            </div>
          </el-card>
          
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">¥{{ pastSevenDaysSales.toFixed(2) }}</div>
              <div class="stat-label">过去七天总销售额</div>
            </div>
          </el-card>
        </div>

        <!-- 图表区域 -->
        <div class="charts-section">
          <!-- 销售额统计图表 -->
          <el-card class="chart-card">
            <template #header>
              <span class="chart-title">各种类咖啡历史总销售额统计</span>
            </template>
            <div ref="categorySalesChartRef" class="chart-container"></div>
          </el-card>

          <!-- 销量统计图表 -->
          <el-card class="chart-card">
            <template #header>
              <span class="chart-title">各咖啡历史总销量排行（Top10）</span>
            </template>
            <div ref="coffeeSalesChartRef" class="chart-container"></div>
          </el-card>
        </div>

        <!-- 过去七天销售额统计 -->
        <div class="charts-section">
          <el-card class="chart-card">
            <template #header>
              <span class="chart-title">过去七天销售额变化趋势</span>
            </template>
            <div ref="dailySalesChartRef" class="chart-container"></div>
          </el-card>

          <!-- 过去七天销量统计 -->
          <el-card class="chart-card">
            <template #header>
              <span class="chart-title">过去七天销量变化趋势</span>
            </template>
            <div ref="dailyQuantityChartRef" class="chart-container"></div>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getTotalSalesAmount, getTotalCoffeesSold, getCategorySalesStats, getTopSellingCoffee, getDailySalesForLastWeek, getDailyQuantityForLastWeek } from '@/api/admin'

export default {
  name: 'StatisticsView',
  setup() {
      const totalSales = ref(0)  // 历史总销售额
      const totalCoffeesSold = ref(0)  // 历史总销量
      const pastSevenDaysSales = ref(0)  // 过去七天销售额
    
    const categorySalesChartRef = ref(null)
    const coffeeSalesChartRef = ref(null)
    const dailySalesChartRef = ref(null)
    const dailyQuantityChartRef = ref(null)
    
    // 登录状态相关
    const isLoggedIn = ref(false)
    const userInfo = ref({})

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadData()
      }
    })

    const checkLoginStatus = () => {
      // 从本地存储获取用户信息
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        // 检查是否为管理员
        if (userInfo.value.role !== 3) {
          ElMessage.error('您没有权限访问此页面')
          // 重定向到首页
          window.location.href = '/'
        }
      } else {
        ElMessage.warning('请先登录')
        window.location.href = '/login'
      }
    }

    const loadData = async () => {
      try {
        // 获取历史总销售额
        const totalSalesRes = await getTotalSalesAmount()
        if (totalSalesRes.code === 200) {
          totalSales.value = totalSalesRes.data || 0
        } else {
          ElMessage.error('获取历史总销售额失败: ' + totalSalesRes.message)
        }

        // 获取历史总销量
        const totalCoffeesSoldRes = await getTotalCoffeesSold()
        if (totalCoffeesSoldRes.code === 200) {
          totalCoffeesSold.value = totalCoffeesSoldRes.data || 0
        } else {
          ElMessage.error('获取历史总销量失败: ' + totalCoffeesSoldRes.message)
        }

        // 获取过去七天销售额
        const dailySalesRes = await getDailySalesForLastWeek()
        if (dailySalesRes.code === 200) {
          const dailySalesData = dailySalesRes.data || []
          pastSevenDaysSales.value = dailySalesData.reduce((sum, day) => sum + (day.sales || 0), 0)
        } else {
          ElMessage.error('获取过去七天销售额失败: ' + dailySalesRes.message)
        }

        // 加载各类别销售统计数据
        const categoryStatsResponse = await getCategorySalesStats()
        if (categoryStatsResponse.code === 200) {
          const stats = categoryStatsResponse.data || []
          
          // 准备图表数据 - 使用实际类别名称和销售额
          const categoryNames = stats.map(item => {
            return item.category || '未知类别'
          })
          const categorySales = stats.map(item => {
            return item.totalSales || 0
          })
          

          
          // 渲染类别销售额图表
          await nextTick()
          renderCategorySalesChart(categoryNames, categorySales)
        } else {
          ElMessage.error('加载类别销售数据失败')
        }

        // 准备销量图表数据（使用类别销售数据替代）
        const topSellingResponse = await getTopSellingCoffee(10)
        if (topSellingResponse.code === 200) {
          let topSellingData = topSellingResponse.data || []
          
          // 按销量降序排序
          topSellingData.sort((a, b) => (b.sales || 0) - (a.sales || 0))
          
          // 准备销量图表数据
          const coffeeNames = topSellingData.map(coffee => coffee.name)
          const coffeeSales = topSellingData.map(coffee => coffee.sales)
          
          // 渲染咖啡销量图表
          await nextTick()
          renderCoffeeSalesChart(coffeeNames, coffeeSales)
        } else {
          ElMessage.error('加载热销咖啡数据失败')
        }
        
        // 加载过去七天销售额数据
        const dailySalesResponse = await getDailySalesForLastWeek()
        if (dailySalesResponse.code === 200) {
          const dailySalesData = dailySalesResponse.data || []
          
          // 准备图表数据
          const dates = dailySalesData.map(item => {
            // 处理后端返回的数据结构，可能是Map或对象
            // 检查所有可能的键名
            let dateStr = '未知日期';
            if (item.date) {
              dateStr = item.date
            } else if (item.DATE) {
              dateStr = item.DATE
            } else if (item['DATE(order_time)']) {
              dateStr = item['DATE(order_time)']
            } else if (item['date(order_time)']) {
              dateStr = item['date(order_time)']
            }
            
            // 格式化日期，只保留月和日
            if (dateStr !== '未知日期') {
              // 将日期字符串转换为Date对象再格式化
              const dateObj = new Date(dateStr);
              const month = String(dateObj.getMonth() + 1).padStart(2, '0'); // 月份从0开始，需加1
              const day = String(dateObj.getDate()).padStart(2, '0');
              return `${month}-${day}`;
            }
            return dateStr;
          })
          const sales = dailySalesData.map(item => {
            if (item.sales !== undefined) {
              return parseFloat(item.sales) || 0
            } else if (item.SALES !== undefined) {
              return parseFloat(item.SALES) || 0
            } else if (item['IFNULL(SUM(total_amount), 0)'] !== undefined) {
              return parseFloat(item['IFNULL(SUM(total_amount), 0)']) || 0
            }
            return 0
          })
          
          // 渲染每日销售额图表
          await nextTick()
          renderDailySalesChart(dates, sales)
        } else {
          ElMessage.error('加载每日销售额数据失败')
        }
        
        // 加载过去七天销量数据
        const dailyQuantityResponse = await getDailyQuantityForLastWeek()
        if (dailyQuantityResponse.code === 200) {
          const dailyQuantityData = dailyQuantityResponse.data || []
          
          // 准备图表数据
          const quantityDates = dailyQuantityData.map(item => {
            // 处理后端返回的数据结构，可能是Map或对象
            // 检查所有可能的键名
            let dateStr = '未知日期';
            if (item.date) {
              dateStr = item.date
            } else if (item.DATE) {
              dateStr = item.DATE
            } else if (item['DATE(order_time)']) {
              dateStr = item['DATE(order_time)']
            } else if (item['date(order_time)']) {
              dateStr = item['date(order_time)']
            }
            
            // 格式化日期，只保留月和日
            if (dateStr !== '未知日期') {
              // 将日期字符串转换为Date对象再格式化
              const dateObj = new Date(dateStr);
              const month = String(dateObj.getMonth() + 1).padStart(2, '0'); // 月份从0开始，需加1
              const day = String(dateObj.getDate()).padStart(2, '0');
              return `${month}-${day}`;
            }
            return dateStr;
          })
          const quantity = dailyQuantityData.map(item => {
            if (item.quantity !== undefined) {
              return parseInt(item.quantity) || 0
            } else if (item.QUANTITY !== undefined) {
              return parseInt(item.QUANTITY) || 0
            } else if (item['IFNULL(SUM(quantity), 0)'] !== undefined) {
              return parseInt(item['IFNULL(SUM(quantity), 0)']) || 0
            }
            return 0
          })
          
          // 渲染每日销量图表
          await nextTick()
          renderDailyQuantityChart(quantityDates, quantity)
        } else {
          ElMessage.error('加载每日销量数据失败')
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
        ElMessage.error('加载统计数据失败')
      }
    }

    const renderCategorySalesChart = (categories, sales) => {
      if (!categorySalesChartRef.value) return
      
      const chart = echarts.init(categorySalesChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: categories
        },
        yAxis: {
          type: 'value',
          name: '销售额 (元)'
        },
        series: [{
          name: '销售额',
          type: 'bar',
          data: sales,
          itemStyle: {
            color: '#5470c6'
          }
        }]
      }
      chart.setOption(option)
      
      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        chart.resize()
      })
    }

    const renderCoffeeSalesChart = (names, sales) => {
      if (!coffeeSalesChartRef.value) return
      
      const chart = echarts.init(coffeeSalesChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            interval: 0,
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '销量 (杯)'
        },
        series: [{
          name: '销量',
          type: 'bar',
          data: sales,
          itemStyle: {
            color: '#91cc75'
          }
        }]
      }
      chart.setOption(option)
      
      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        chart.resize()
      })
    }

    const renderDailySalesChart = (dates, sales) => {
      if (!dailySalesChartRef.value) return
      
      const chart = echarts.init(dailySalesChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '销售额 (元)'
        },
        series: [{
          name: '销售额',
          type: 'line',
          data: sales,
          smooth: true,
          itemStyle: {
            color: '#fac858'
          },
          lineStyle: {
            color: '#fac858'
          },
          areaStyle: {
            opacity: 0.2,
            color: '#fac858'
          }
        }]
      }
      chart.setOption(option)
      
      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        chart.resize()
      })
    }
    
    const renderDailyQuantityChart = (dates, quantity) => {
      if (!dailyQuantityChartRef.value) return
      
      const chart = echarts.init(dailyQuantityChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '8%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '销量 (杯)'
        },
        series: [{
          name: '销量',
          type: 'line',
          data: quantity,
          smooth: true,
          itemStyle: {
            color: '#73c0de'
          },
          lineStyle: {
            color: '#73c0de'
          },
          areaStyle: {
            opacity: 0.2,
            color: '#73c0de'
          }
        }]
      }
      chart.setOption(option)
      
      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        chart.resize()
      })
    }

    return {
      totalSales,
      totalCoffeesSold,
      pastSevenDaysSales,
      categorySalesChartRef,
      coffeeSalesChartRef,
      dailySalesChartRef,
      dailyQuantityChartRef,
      // 登录状态相关
      isLoggedIn,
      userInfo
    }
  }
}
</script>

<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo {
  margin: 0;
  color: #8B4513;
  font-size: 24px;
  cursor: pointer;
}

.nav-links .el-button {
  margin-left: 20px;
  font-size: 16px;
}

.main-content {
  padding: 20px;
  background-color: #f5f5f5;
  margin-top: 50px;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 200px;
  text-align: center;
}

.stat-content {
  padding: 20px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.charts-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.chart-card {
  flex: 1;
  min-width: 400px;
  height: 420px;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
}
</style>