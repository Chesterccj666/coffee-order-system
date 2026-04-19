<template>
  <div class="staff-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统 - 待处理订单</h1>
          <div class="nav-links">
            <!-- 店员功能 -->
            <el-button type="text" @click="$router.push('/staff')">待处理订单</el-button>
            <el-button type="text" @click="$router.push('/staff/all-orders')">全部订单</el-button>
            <el-button type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <!-- 左右两栏布局 -->
        <div class="orders-container">
          <!-- 左侧：待接单订单 -->
          <div class="pending-orders-column">
            <h3>待接单</h3>
            <div class="orders-list">
              <el-card 
                v-for="order in pendingOrders" 
                :key="order.id" 
                class="order-card"
              >
                <div class="order-header">
                  <div class="order-info">
                    <p><strong>订单号：</strong>{{ order.orderNo }}</p>
                    <p><strong>下单时间：</strong>{{ formatDate(order.orderTime) }}</p>
                    <p><strong>取餐码：</strong>{{ order.takeCode }}</p>
                    <p><strong>订单状态：</strong>
                      <el-tag 
                        :type="getStatusType(order.status)" 
                        size="small"
                      >
                        {{ getStatusText(order.status) }}
                      </el-tag>
                    </p>
                    <p><strong>总金额：</strong>¥{{ order.totalAmount }}</p>
                    <p><strong>顾客备注：</strong>{{ order.remark || '无' }}</p>
                  </div>
                  <div class="order-actions">
                    <el-button 
                      v-if="order.status === 1" 
                      type="primary" 
                      @click="acceptOrder(order.id)"
                    >
                      接单
                    </el-button>
                  </div>
                </div>
                
                <!-- 订单详情 -->
                <div class="order-details">
                  <h4>订单详情：</h4>
                  <div v-for="item in order.items" :key="item.id" class="order-item">
                    <div class="item-info">
                      <img :src="item.coffeeImage" class="item-image" alt="咖啡图片" />
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
              </el-card>
              
              <div v-if="pendingOrders.length === 0" class="empty-orders">
                <p>暂无待接单订单</p>
              </div>
            </div>
          </div>
          
          <!-- 右侧：制作中订单 -->
          <div class="processing-orders-column">
            <h3>制作中</h3>
            <div class="orders-list">
              <el-card 
                v-for="order in processingOrders" 
                :key="order.id" 
                class="order-card"
              >
                <div class="order-header">
                  <div class="order-info">
                    <p><strong>订单号：</strong>{{ order.orderNo }}</p>
                    <p><strong>下单时间：</strong>{{ formatDate(order.orderTime) }}</p>
                    <p><strong>取餐码：</strong>{{ order.takeCode }}</p>
                    <p><strong>订单状态：</strong>
                      <el-tag 
                        :type="getStatusType(order.status)" 
                        size="small"
                      >
                        {{ getStatusText(order.status) }}
                      </el-tag>
                    </p>
                    <p><strong>总金额：</strong>¥{{ order.totalAmount }}</p>
                    <p><strong>顾客备注：</strong>{{ order.remark || '无' }}</p>
                  </div>
                  <div class="order-actions">
                    <el-button 
                      v-if="order.status === 2" 
                      type="success" 
                      @click="completeOrder(order.id)"
                    >
                      出餐
                    </el-button>
                  </div>
                </div>
                
                <!-- 订单详情 -->
                <div class="order-details">
                  <h4>订单详情：</h4>
                  <div v-for="item in order.items" :key="item.id" class="order-item">
                    <div class="item-info">
                      <img :src="item.coffeeImage" class="item-image" alt="咖啡图片" />
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
              </el-card>
              
              <div v-if="processingOrders.length === 0" class="empty-orders">
                <p>暂无制作中订单</p>
              </div>
            </div>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, getAllOrdersByStatus, getOrdersByStatus, updateOrderStatus, getOrderDetail } from '@/api/order'
import { useRouter } from 'vue-router'

export default {
  name: 'PendingOrdersView',
  setup() {
    const router = useRouter()
    const pendingOrders = ref([]) // 待接单订单
    const processingOrders = ref([]) // 制作中订单
    const isLoggedIn = ref(false)
    const userInfo = ref({})

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadOrders()
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
        // 获取待接单订单
        const pendingResponse = await getOrdersByStatus(1)
        // 获取制作中订单
        const processingResponse = await getOrdersByStatus(2)
        
        const pendingOrdersData = pendingResponse.code === 200 ? pendingResponse.data || [] : []
        const processingOrdersData = processingResponse.code === 200 ? processingResponse.data || [] : []
        
        // 获取每个订单的详细信息
        for (let i = 0; i < pendingOrdersData.length; i++) {
          try {
            const orderDetailResponse = await getOrderDetail(pendingOrdersData[i].id)
            if (orderDetailResponse.code === 200) {
              pendingOrdersData[i].items = orderDetailResponse.data.items || []
            }
          } catch (err) {
            console.warn(`获取订单 ${pendingOrdersData[i].id} 详情失败:`, err)
            pendingOrdersData[i].items = []
          }
        }
        
        for (let i = 0; i < processingOrdersData.length; i++) {
          try {
            const orderDetailResponse = await getOrderDetail(processingOrdersData[i].id)
            if (orderDetailResponse.code === 200) {
              processingOrdersData[i].items = orderDetailResponse.data.items || []
            }
          } catch (err) {
            console.warn(`获取订单 ${processingOrdersData[i].id} 详情失败:`, err)
            processingOrdersData[i].items = []
          }
        }
        
        // 按时间顺序排序（旧订单在前，新订单在后）
        pendingOrdersData.sort((a, b) => new Date(a.orderTime) - new Date(b.orderTime))
        processingOrdersData.sort((a, b) => new Date(a.orderTime) - new Date(b.orderTime))
        
        // 更新响应式数据
        pendingOrders.value = pendingOrdersData
        processingOrders.value = processingOrdersData
      } catch (error) {
        console.error('加载订单失败:', error)
        ElMessage.error('加载订单失败')
      }
    }

    const acceptOrder = async (orderId) => {
      try {
        await ElMessageBox.confirm('确定要接此订单吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await updateOrderStatus(orderId, 2) // 已接单/制作中

        if (response.code === 200) {
          ElMessage.success('接单成功')
          
          // 从待接单列表中移除该订单
          const pendingIndex = pendingOrders.value.findIndex(order => order.id === orderId)
          if (pendingIndex !== -1) {
            const acceptedOrder = pendingOrders.value.splice(pendingIndex, 1)[0]
            // 更新订单状态为制作中
            acceptedOrder.status = 2
            // 添加到制作中列表
            processingOrders.value.push(acceptedOrder)
          }
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('接单失败:', error)
          ElMessage.error('接单失败')
        }
      }
    }

    const completeOrder = async (orderId) => {
      try {
        await ElMessageBox.confirm('确定已完成此订单吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await updateOrderStatus(orderId, 3) // 已完成/可取餐

        if (response.code === 200) {
          ElMessage.success('出餐成功')
          
          // 从制作中列表中移除该订单
          const processingIndex = processingOrders.value.findIndex(order => order.id === orderId)
          if (processingIndex !== -1) {
            processingOrders.value.splice(processingIndex, 1)
          }
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('出餐失败:', error)
          ElMessage.error('出餐失败')
        }
      }
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
      pendingOrders,
      processingOrders,
      userInfo,
      loadOrders,
      acceptOrder,
      completeOrder,
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
.staff-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

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
  margin-top: 60px;
}

.page-title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.orders-container {
  display: flex;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.pending-orders-column, .processing-orders-column {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.pending-orders-column h3, .processing-orders-column h3 {
  margin-top: 0;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eee;
  color: #333;
}

.orders-list {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.order-info {
  flex: 1;
}

.order-info p {
  margin: 5px 0;
  font-size: 14px;
}

.order-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-details h4 {
  margin-bottom: 10px;
  color: #333;
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

.item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 10px;
}

.item-text {
  flex: 1;
}

.item-text p {
  margin: 3px 0;
  font-size: 13px;
}

.empty-orders {
  text-align: center;
  padding: 40px;
  color: #999;
}

.footer {
  background-color: #fff;
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>