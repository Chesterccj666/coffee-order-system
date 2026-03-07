<template>
  <div class="statistics-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统</h1>
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
            <!-- 公有功能 -->
            <el-button v-if="!isLoggedIn" type="text" @click="$router.push('/login')">登录</el-button>
            <el-button v-else type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <h2 class="page-title">数据统计</h2>
        
        <!-- 统计卡片 -->
        <div class="stats-cards">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">¥{{ totalSales.toFixed(2) }}</div>
              <div class="stat-label">总销售额</div>
            </div>
          </el-card>
          
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalCoffees }}</div>
              <div class="stat-label">咖啡总数</div>
            </div>
          </el-card>
          
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalCategories }}</div>
              <div class="stat-label">咖啡类别数</div>
            </div>
          </el-card>
        </div>

        <!-- 图表区域 -->
        <div class="charts-section">
          <!-- 销售额统计图表 -->
          <el-card class="chart-card">
            <template #header>
              <span class="chart-title">各类别销售额统计</span>
            </template>
            <div ref="categorySalesChartRef" class="chart-container"></div>
          </el-card>

          <!-- 销量统计图表 -->
          <el-card class="chart-card">
            <template #header>
              <span class="chart-title">各咖啡销量排行</span>
            </template>
            <div ref="coffeeSalesChartRef" class="chart-container"></div>
          </el-card>
        </div>

        <!-- 过去七天销售额统计 -->
        <el-card class="daily-sales-card">
          <template #header>
            <span class="card-title">过去七天销售额统计</span>
          </template>
          <div ref="dailySalesChartRef" class="chart-container"></div>
        </el-card>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getTotalSalesAmount, getCategorySalesStats, getTopSellingCoffee, getAllCoffeeCategories, getDailySalesForLastWeek } from '@/api/admin'

export default {
  name: 'StatisticsView',
  setup() {
    const totalSales = ref(0)
    const totalCoffees = ref(0)
    const totalCategories = ref(0)
    
    const categorySalesChartRef = ref(null)
    const coffeeSalesChartRef = ref(null)
    const dailySalesChartRef = ref(null)
    
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
        // 加载总销售额
        const totalSalesResponse = await getTotalSalesAmount()
        if (totalSalesResponse.code === 200) {
          totalSales.value = totalSalesResponse.data || 0
        } else {
          ElMessage.error('加载总销售额失败')
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
          
          totalCategories.value = categoryNames.length
          
          // 渲染类别销售额图表
          await nextTick()
          renderCategorySalesChart(categoryNames, categorySales)
        } else {
          ElMessage.error('加载类别销售数据失败')
        }

        // 准备销量图表数据（使用类别销售数据替代）
        const topSellingResponse = await getTopSellingCoffee(10)
        if (topSellingResponse.code === 200) {
          const topSellingData = topSellingResponse.data || []
          totalCoffees.value = topSellingData.length
          
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

    return {
      totalSales,
      totalCoffees,
      totalCategories,
      categorySalesChartRef,
      coffeeSalesChartRef,
      dailySalesChartRef,
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

.daily-sales-card {
  margin-top: 20px;
}

.card-title {
  font-size: 16px;

  font-weight: bold;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}
</style>