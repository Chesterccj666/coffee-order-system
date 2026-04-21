<template>
  <div class="home">
    <div class="home-container">
      <!-- 头部 -->
      <div class="header">
        <div class="header-content">
          <h1 class="logo">☕ 咖啡点单系统 - 首页</h1>
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
      </div>

      <!-- 主要内容区域 -->
      <div class="main-content">
        <div class="welcome-section">
          <!-- 顾客欢迎内容 -->
          <div v-if="!isLoggedIn || userInfo.role === 1">
            <h2>欢迎来到我们的咖啡世界</h2>
            <p>精心调制的每一杯咖啡，为您带来温暖与舒适</p>
            <el-button type="primary" size="large" @click="$router.push('/menu')">开始点单</el-button>
          </div>
          
          <!-- 店员欢迎内容 -->
          <div v-if="isLoggedIn && userInfo.role === 2">
            <h2>店员工作台</h2>
            <p>管理订单，服务顾客，提升效率</p>
          </div>
          
          <!-- 管理员欢迎内容 -->
          <div v-if="isLoggedIn && userInfo.role === 3">
            <h2>管理员面板</h2>
            <p>管理咖啡商品，查看销售数据，优化运营策略</p>
            <div class="admin-actions">
              <el-button type="primary" size="large" @click="$router.push('/admin/coffee')">咖啡管理</el-button>
              <el-button type="success" size="large" @click="$router.push('/admin/statistics')">数据统计</el-button>
            </div>
          </div>
        </div>

        <!-- 店员订单统计 -->
        <div v-if="isLoggedIn && userInfo.role === 2" class="staff-dashboard">
          <h3>销售统计</h3>
          <div class="stats-grid">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">¥{{ todaySales.toFixed(2) }}</div>
                <div class="stat-label">今日销售额</div>
              </div>
            </el-card>
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">¥{{ totalSales.toFixed(2) }}</div>
                <div class="stat-label">历史总销售额</div>
              </div>
            </el-card>
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ completedOrdersCount }}</div>
                <div class="stat-label">今日已完成订单</div>
              </div>
            </el-card>
          </div>
          
          <h3 style="margin-top: 30px;">订单统计</h3>
          <div class="stats-grid">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ pendingOrdersCount }}</div>
                <div class="stat-label">待接单</div>
              </div>
            </el-card>
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-number">{{ processingOrdersCount }}</div>
                <div class="stat-label">制作中</div>
              </div>
            </el-card>
          </div>
          
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="$router.push('/staff')">处理待接单</el-button>
            <el-button type="success" size="large" @click="$router.push('/staff/all-orders')">查看全部订单</el-button>
          </div>
        </div>

        <!-- 管理员热销咖啡展示 -->
        <div v-if="isLoggedIn && userInfo.role === 3" class="top-selling-section">
          <h3>热销咖啡 Top 5</h3>
          <div class="top-selling-grid">
            <el-card 
              v-for="coffee in topSellingCoffees" 
              :key="coffee.id" 
              class="top-selling-item"
            >
              <div class="coffee-image-container">
                <img :src="coffee.coffeeImage" class="top-selling-image" alt="咖啡图片" />
              </div>
              <div class="coffee-info">
                <h4>{{ coffee.name }}</h4>
                <p class="price">¥{{ coffee.price }}</p>
                <p class="sales">过去七天销量: {{ coffee.sales }} 杯</p>
                <el-button 
                  :type="coffee.recommend === '1' ? 'info' : 'warning'"
                  :disabled="coffee.recommend === '1'"
                  @click="toggleRecommend(coffee)"
                >
                  {{ coffee.recommend === '1' ? '已推荐' : '推荐' }}
                </el-button>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 顾客推荐咖啡展示 -->
        <div v-if="!isLoggedIn || userInfo.role === 1" class="recommended-section">
          <div class="coffee-display-container">
            <div class="coffee-carousel-section">
              <el-carousel 
                height="325px"
                :autoplay="true"
                :interval="3000" 
                indicator-position="outside"
                @change="handleCarouselChange"
                ref="carouselRef"
              >
                <el-carousel-item v-for="(coffee, index) in limitedRecommendedCoffees" :key="coffee.id">
                  <div class="carousel-coffee-item" @click="$router.push(`/menu#${coffee.id}`)">
                    <img :src="coffee.coffeeImage" class="carousel-coffee-image" alt="咖啡图片" />
                  </div>
                </el-carousel-item>
              </el-carousel>
            </div>
            <div class="coffee-info-section">
              <div class="coffee-info-card" v-if="currentCoffee">
                <h4>{{ currentCoffee.name }}</h4>
                <p class="price">¥{{ currentCoffee.price }}</p>
                <p class="description">{{ currentCoffee.description }}</p>
                <el-button type="primary" @click="$router.push(`/menu#${currentCoffee.id}`)">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部 -->
      <div class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getRecommendedCoffee, getTopSellingLastWeek } from '@/api/coffee'
import { getOrdersByStatus, getAllOrdersByStatus } from '@/api/order'
import { setRecommend } from '@/api/admin'

export default {
  name: 'HomeView',
  setup() {
    const recommendedCoffees = ref([])
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const currentCoffee = ref(null)
    const carouselRef = ref(null)

    // 限制推荐咖啡数量最多为8种
    const limitedRecommendedCoffees = computed(() => {
      return recommendedCoffees.value.slice(0, 8)
    })

    // 管理员热销咖啡数据
    const topSellingCoffees = ref([])

    onMounted(() => {
      loadRecommendedCoffees()
      checkLoginStatus()
      // 如果是管理员角色，加载热销咖啡数据
      if (isLoggedIn.value && userInfo.value.role === 3) {
        loadTopSellingCoffees()
      }
    })

    const loadRecommendedCoffees = async () => {
      try {
        const response = await getRecommendedCoffee()
        if (response.code === 200) {
          recommendedCoffees.value = response.data
          // 初始化当前显示的咖啡信息
          if (limitedRecommendedCoffees.value.length > 0) {
            currentCoffee.value = limitedRecommendedCoffees.value[0]
          }
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载推荐咖啡失败:', error)
        ElMessage.error('加载推荐咖啡失败')
      }
    }

    // 加载热销咖啡数据（过去七天）
    const loadTopSellingCoffees = async () => {
      try {
        const response = await getTopSellingLastWeek(5)
        if (response.code === 200) {
          // 处理返回的数据，将lastWeekSales字段映射到sales字段以便前端显示，并处理字段名映射
          topSellingCoffees.value = (response.data || []).map(item => ({
            id: item.id,
            name: item.name,
            price: item.price,
            coffeeImage: item.coffee_image || item.coffeeImage,  // 处理数据库字段名映射
            description: item.description,
            category: item.category,
            stock: item.stock,
            status: item.status,
            recommend: item.recommend,
            sales: item.lastWeekSales || item.sales || 0,  // 映射过去七天销量
          }))
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载热销咖啡失败:', error)
        ElMessage.error('加载热销咖啡失败')
      }
    }

    // 切换推荐状态
    const toggleRecommend = async (coffee) => {
      try {
        const response = await setRecommend(coffee.id, '1')
        if (response.code === 200) {
          ElMessage.success('推荐成功')
          // 更新本地数据
          coffee.recommend = '1'
          // 重新加载热销咖啡列表
          loadTopSellingCoffees()
        } else if (response.code === 500 && response.message.includes('推荐的咖啡数量已达上限')) {
          // 特殊处理推荐数量达到上限的情况
          ElMessage.error('推荐的咖啡数量已达上限（8个），无法再推荐更多咖啡')
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('设置推荐失败:', error)
        ElMessage.error('设置推荐失败')
      }
    }

    // 店员订单统计
    const pendingOrdersCount = ref(0)
    const processingOrdersCount = ref(0)
    const completedOrdersCount = ref(0)
    // 销售额统计
    const todaySales = ref(0)
    const totalSales = ref(0)

    const loadOrderStats = async () => {
      if (isLoggedIn.value && userInfo.value.role === 2) { // 店员角色
        try {
          // 加载订单数量统计
          const pendingResponse = await getOrdersByStatus(1)
          if (pendingResponse.code === 200) {
            pendingOrdersCount.value = pendingResponse.data ? pendingResponse.data.length : 0
          }

          const processingResponse = await getOrdersByStatus(2)
          if (processingResponse.code === 200) {
            processingOrdersCount.value = processingResponse.data ? processingResponse.data.length : 0
          }

          const completedResponse = await getOrdersByStatus(3)
          if (completedResponse.code === 200) {
            const completedOrders = completedResponse.data || []
            
            // 计算今日销售额（已完成订单）
            const today = new Date()
            today.setHours(0, 0, 0, 0)
            
            // 过滤出今天的已完成订单
            const todayCompletedOrders = completedOrders.filter(order => new Date(order.orderTime) >= today)
            completedOrdersCount.value = todayCompletedOrders.length
            
            todaySales.value = todayCompletedOrders
              .reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
              
            // 计算历史总销售额（所有已完成订单）
            totalSales.value = completedOrders
              .reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
          }
          
          // 获取所有订单用于计算历史总额
          const allOrdersResponse = await getAllOrdersByStatus(0)
          if (allOrdersResponse.code === 200) {
            const allOrders = allOrdersResponse.data || []
            // 计算历史总销售额（所有已完成订单）
            totalSales.value = allOrders
              .filter(order => order.status === 3) // 仅已完成订单
              .reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
          }
        } catch (error) {
          console.error('加载订单统计失败:', error)
        }
      }
    }

    const handleCarouselChange = (index) => {
      // 当轮播图切换时，更新右侧的咖啡信息
      currentCoffee.value = limitedRecommendedCoffees.value[index]
    }

    const checkLoginStatus = () => {
      // 从本地存储获取用户信息
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        // 如果是管理员角色，加载热销咖啡数据
        if (userInfo.value.role === 3) {
          loadTopSellingCoffees()
        }
      }
    }

    onMounted(async () => {
      loadRecommendedCoffees()
      checkLoginStatus()
      // 等待用户信息加载完成后再加载订单统计
      setTimeout(loadOrderStats, 100)
    })

    return {
      recommendedCoffees,
      limitedRecommendedCoffees,
      isLoggedIn,
      userInfo,
      currentCoffee,
      carouselRef,
      handleCarouselChange,
      // 管理员热销咖啡
      topSellingCoffees,
      toggleRecommend,
      // 店员订单统计
      pendingOrdersCount,
      processingOrdersCount,
      completedOrdersCount,
      // 销售额统计
      todaySales,
      totalSales
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  padding: 20px 0;
}

.home-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.admin-actions {
  margin-top: 20px;
  display: flex;
  gap: 15px;
  justify-content: center;
}

.top-selling-section {
  margin-top: 40px;
}

.top-selling-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.top-selling-item {
  text-align: center;
  padding: 15px;
}

.coffee-image-container {
  width: 100%;
  height: 120px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 10px;
}

.top-selling-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.coffee-info h4 {
  margin: 10px 0 5px 0;
  font-size: 16px;
  color: #333;
}

.price {
  font-weight: bold;
  color: #e74c3c;
  margin: 5px 0;
}

.sales {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  margin: 0;
  color: #8B4513;
  font-size: 24px;
}

.nav-links .el-button {
  margin-left: 20px;
  font-size: 16px;
}

.main-content {
  padding: 30px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-section h2 {
  color: #333;
  font-size: 32px;
  margin-bottom: 15px;
}

.welcome-section p {
  color: #666;
  font-size: 18px;
  margin-bottom: 25px;
}

.recommended-section {
  margin-top: 30px;
}

.recommended-section h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.coffee-display-container {
  display: flex;
  gap: 30px;
  align-items: center;
}

.coffee-carousel-section {
  flex: 1;
  max-width: 50%;
}

.coffee-info-section {
  flex: 1;
  max-width: 50%;
}

.coffee-info-card {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  height: 250px;
  display: flex;
  flex-direction: column;
}

.coffee-info-card h4 {
  font-size: 24px;
  color: #333;
  margin-top: 0;
  margin-bottom: 15px;
}

.coffee-info-card .price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
  margin: 10px 0;
}

.staff-dashboard {
  margin-top: 30px;
}

.staff-dashboard h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #3498db;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 16px;
  color: #666;
}

.quick-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .quick-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .quick-actions .el-button {
    width: 100%;
    max-width: 300px;
  }
}

.coffee-info-card .description {
  color: #666;
  line-height: 1.6;
  margin: 15px 0;
  flex-grow: 1;
}

.carousel-coffee-item {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
}

.carousel-coffee-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.carousel-coffee-item:hover .carousel-coffee-image {
  transform: scale(1.05);
}

.el-carousel__item {
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .coffee-display-container {
    flex-direction: column;
  }
  
  .coffee-carousel-section,
  .coffee-info-section {
    max-width: 100%;
  }
}

.footer {
  text-align: center;
  padding: 20px;
  color: #999;
  border-top: 1px solid #eee;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .home-container {
    margin: 0 10px;
    border-radius: 8px;
  }
  
  .header-content {
    flex-direction: column;
    height: auto;
    gap: 10px;
  }
  
  .nav-links .el-button {
    margin-left: 0;
    margin-top: 5px;
  }
  
  .main-content {
    padding: 20px;
  }
  
  .welcome-section h2 {
    font-size: 24px;
  }
  
  .welcome-section p {
    font-size: 16px;
  }
}
</style>