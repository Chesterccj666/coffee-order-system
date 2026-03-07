<template>
  <div class="staff-all-orders-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统 - 店员版</h1>
          <div class="nav-links">
            <el-button type="text" @click="$router.push('/staff')">待处理订单</el-button>
            <el-button type="text" @click="$router.push('/staff/all-orders')">全部订单</el-button>
            <el-button type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <h2 class="page-title">全部订单</h2>
        
        <!-- 日期筛选 -->
        <div class="date-filter">
          <span>选择日期：</span>
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="filterOrdersByDate"
          />
          <el-button @click="showTodayOrders" style="margin-left: 10px;">今天</el-button>
          <el-button @click="clearDateFilter" style="margin-left: 10px;" title="展示历史全部订单">清除筛选</el-button>
        </div>
        

        <!-- 订单列表 -->
        <div class="orders-list">
          <el-card 
            v-for="order in filteredOrders" 
            :key="order.id" 
            class="order-card"
          >
            <!-- 订单信息和详情并排显示 -->
            <div class="order-content">
              <div class="order-info">
                <div class="order-info-top">
                  <p><strong>订单号：</strong>{{ order.orderNo }}</p>
                  <p><strong>下单时间：</strong>{{ formatDate(order.orderTime) }}</p>
                  <p><strong>取餐码：</strong>{{ order.takeCode || '无' }}</p>
                  <p><strong>总金额：</strong>¥{{ order.totalAmount }}</p>
                  <p><strong>顾客备注：</strong>{{ order.remark || '无' }}</p>
                </div>
                <div class="order-status-bottom">
                  <p><strong>订单状态：</strong>
                    <el-tag 
                      :type="getStatusType(order.status)" 
                      size="large"
                    >
                      {{ getStatusText(order.status) }}
                    </el-tag>
                  </p>
                </div>
                <!-- 查看订单详情按钮 - 使用Popover -->
                <el-popover
                  placement="right"
                  :width="300"
                  trigger="hover"
                >
                  <template #reference>
                    <button class="view-details-btn">查看订单详情</button>
                  </template>
                  <div class="popover-order-details">
                    <h4>订单详情：</h4>
                    <div v-for="item in order.items" :key="item.id" class="order-item">
                      <div class="item-info">
                        <div class="item-text">
                          <p><strong>{{ item.coffeeName }}</strong></p>
                          <p>数量：{{ item.quantity }}</p>
                          <p>单价：¥{{ item.price }}</p>
                          <p>小计：¥{{ item.totalPrice }}</p>
                          <p>甜度：{{ getSweetText(item.sweet) }}</p>
                          <p>温度：{{ getTemperatureText(item.temperature) }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-popover>
              </div>
            </div>
          </el-card>
          
          <div v-if="filteredOrders.length === 0 && selectedDate" class="empty-orders">
            <p>{{ selectedDate }} 暂无订单</p>
          </div>
          <div v-else-if="filteredOrders.length === 0 && !selectedDate" class="empty-orders">
            <p>暂无订单</p>
          </div>
        </div>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <p>&copy; 2026 咖啡点单系统. 店员版</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllOrdersByStatus, getOrdersByStatus, updateOrderStatus, getOrderDetail } from '@/api/order'
import { useRouter } from 'vue-router'

export default {
  name: 'AllOrdersView',
  setup() {
    const router = useRouter()
    const orders = ref([])
    const filteredOrders = ref([])
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const selectedDate = ref('')

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadOrders()
        // 页面加载时默认显示今天的订单
        showTodayOrders()
      }
    })





    const checkLoginStatus = () => {
      // 从本地存储获取用户信息
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        
        // 检查用户角色是否为店员
        if (userInfo.value.role !== 2) {
          ElMessage.error('您没有权限访问店员页面')
          router.push('/')
        }
      } else {
        ElMessage.error('请先登录')
        router.push('/login')
      }
    }

    const loadOrders = async () => {
      try {
        // 获取所有状态的订单
        const response = await getAllOrdersByStatus(0)
        if (response.code === 200) {
          let ordersData = response.data || []
          
          orders.value = ordersData
        
          // 获取每个订单的详细信息
          for (let i = 0; i < orders.value.length; i++) {
            try {
              const orderDetailResponse = await getOrderDetail(orders.value[i].id)
              if (orderDetailResponse.code === 200) {
                orders.value[i].items = orderDetailResponse.data.items || []
              }
            } catch (err) {
              console.warn(`获取订单 ${orders.value[i].id} 详情失败:`, err)
              orders.value[i].items = []
            }
          }
          
          // 初始化过滤后的订单列表
          filteredOrders.value = [...orders.value]
        }
      } catch (error) {
        console.error('加载订单失败:', error)
        ElMessage.error('加载订单失败')
      }
    }
    
    const filterOrdersByDate = (date) => {
      if (!date) {
        filteredOrders.value = [...orders.value]
        return
      }
      
      // 将选择的日期转换为年月日格式
      const selectedDay = new Date(date).toISOString().split('T')[0]
      
      filteredOrders.value = orders.value.filter(order => {
        // 提取订单日期的年月日部分
        const orderDate = new Date(order.orderTime).toISOString().split('T')[0]
        return orderDate === selectedDay
      })
    }
    
    const showTodayOrders = () => {
      // 获取今天的日期并格式化为 YYYY-MM-DD
      const today = new Date()
      const year = today.getFullYear()
      const month = String(today.getMonth() + 1).padStart(2, '0')
      const day = String(today.getDate()).padStart(2, '0')
      const todayStr = `${year}-${month}-${day}`
      
      selectedDate.value = todayStr
      filterOrdersByDate(todayStr)
    }
    
    const clearDateFilter = () => {
      selectedDate.value = ''
      filteredOrders.value = [...orders.value]
    }



    const getStatusText = (status) => {
      switch (status) {
        case 1: return '待接单'
        case 2: return '制作中'
        case 3: return '已完成'
        case 4: return '已取消'
        default: return '未知状态'
      }
    }

    const getStatusType = (status) => {
      switch (status) {
        case 1: return 'warning' // 待接单 - 黄色
        case 2: return 'primary' // 制作中 - 蓝色
        case 3: return 'success' // 已完成 - 绿色
        case 4: return 'info' // 已取消 - 灰色
        default: return 'info'
      }
    }

    const getSweetText = (sweet) => {
      switch (sweet) {
        case 1: return '正常糖'
        case 2: return '少糖'
        case 3: return '不加糖'
        default: return '未知'
      }
    }

    const getTemperatureText = (temperature) => {
      switch (temperature) {
        case 1: return '烫'
        case 2: return '温热'
        case 3: return '少冰'
        case 4: return '正常冰'
        default: return '未知'
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }

    return {
      orders,
      filteredOrders,
      selectedDate,
      userInfo,
      loadOrders,
      filterOrdersByDate,
      showTodayOrders,
      clearDateFilter,
      getStatusText,
      getStatusType,
      getSweetText,
      getTemperatureText,
      formatDate
    }
  }
}
</script>

<style scoped>
.staff-all-orders-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

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
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.date-filter {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f0f2f5;
  border-radius: 4px;
}

/* 为日期筛选按钮添加样式 */
.date-filter .el-button {
  border-radius: 20px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
  margin: 0 5px;
}

.date-filter .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.date-filter > .el-button:nth-of-type(1) { /* "今天"按钮 */
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.date-filter > .el-button:nth-of-type(1):hover {
  background-color: #bae7ff;
  color: #096dd9;
}

.date-filter > .el-button:nth-of-type(2) { /* "清除筛选"按钮 */
  background-color: #fff2e8;
  color: #fa8c16;
  border: 1px solid #ffcca7;
}

.date-filter > .el-button:nth-of-type(2):hover {
  background-color: #ffe7ba;
  color: #d46b08;
}

.orders-list {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.order-card {
  margin-bottom: 0;
  height: fit-content;
}

.order-content {
  display: flex;
  gap: 20px;
}

.order-info {
  flex: 1;
  min-width: 0; /* 允许收缩 */
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.order-info-top {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.order-info-top p {
  margin: 8px 0;
  font-size: 16px;
}

.order-status-bottom {
  align-self: flex-end;
  margin-top: auto;
}

.order-status-bottom p {
  margin: 8px 0;
  font-size: 20px;
}

.order-status-bottom .el-tag {
  font-size: 18px;
  padding: 8px 16px;
}

.view-details-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 10px 20px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  z-index: 10;
  font-weight: 500;
}

.view-details-btn:hover {
  background-color: #2a7bea;
}

.popover-order-details h4 {
  margin: 10px 0;
  color: #333;
  font-size: 18px;
}

.order-item {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #fafafa;
  border-radius: 4px;
}

.item-info {
  display: flex;
  align-items: flex-start;
}

.item-text {
  flex: 1;
}

.item-text p {
  margin: 5px 0;
  font-size: 16px;
}

.empty-orders {
  grid-column: 1 / -1; /* 跨越所有网格列 */
  text-align: center;
  padding: 40px;
  color: #999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px; /* 确保有足够的空间进行居中 */
}

.empty-orders p {
  margin: 0;
  font-size: 50px;
  font-weight: 500;
}

.footer {
  background-color: #fff;
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>