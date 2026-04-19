<template>
  <div class="orders-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统 - 我的订单</h1>
          <div class="nav-links">
            <!-- 顾客功能 -->
            <el-button type="text" @click="$router.push('/menu')">菜单</el-button>
            <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/cart')">购物车</el-button>
            <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/orders')">我的订单</el-button>
            
            <el-button v-if="!isLoggedIn" type="text" @click="$router.push('/login')">登录</el-button>
            <el-button v-else type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">      
        <el-tabs v-model="activeTab" @tab-change="loadOrders">
          <el-tab-pane label="全部订单" name="all"></el-tab-pane>
          <el-tab-pane label="待接单" name="pending"></el-tab-pane>
          <el-tab-pane label="制作中" name="processing"></el-tab-pane>
          <el-tab-pane label="已完成" name="completed"></el-tab-pane>
          <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
        </el-tabs>

        <div class="orders-list">
          <el-card 
            class="order-card" 
            v-for="order in orders" 
            :key="order.id"
            shadow="hover"
          >
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号: {{ order.orderNo }}</span>
                <span class="order-time">下单时间：{{ formatDate(order.orderTime) }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusTagType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="order-details">
              <div class="order-items">
                <div 
                  class="order-item" 
                  v-for="(item, index) in order.items" 
                  :key="index"
                >
                  <img :src="item.coffeeImage" class="item-image" alt="咖啡图片" />
                  <div class="item-info">
                    <div class="item-name">{{ item.coffeeName }}</div>
                    <div class="item-specs">
                      <span class="spec">{{ getSugarText(item.sweet) }}</span>
                      <span class="spec">{{ getTempText(item.temperature) }}</span>
                    </div>
                  </div>

                  <div class="item-quantity">×{{ item.quantity }}</div>
                  <div class="item-price">¥{{ item.totalPrice }}</div>
                </div>
              </div>
              
              <div class="order-total">
                <div class="order-total-content">
                  <div class="item-take-code" v-if="order.takeCode">
                    <span class="take-code-label">取餐码:</span>
                    <span class="take-code-value">{{ order.takeCode }}</span>
                  </div>
                  <span class="total-label">合计：</span>
                  <span class="total-amount">¥{{ order.totalAmount }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-actions" v-if="showActions(order.status)">
              <el-button 
                v-if="order.status === 1" 
                type="danger" 
                size="small" 
                @click="cancelOrder(order.id)"
              >
                取消订单
              </el-button>

            </div>
          </el-card>
        </div>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserOrders, getUserOrdersByStatus, getOrdersByStatus, updateOrderStatus, getOrderDetail, cancelOrder as cancelOrderApi } from '@/api/order'

export default {
  name: 'OrdersView',
  setup() {
    const router = useRouter()
    const orders = ref([])
    const activeTab = ref('all')
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
      } else {
        ElMessage.warning('请先登录')
        router.push('/login')
      }
    }

    const loadOrders = async () => {
      try {
        let status = null
        switch (activeTab.value) {
          case 'pending':
            status = 1
            break
          case 'processing':
            status = 2
            break
          case 'completed':
            status = 3
            break
          case 'cancelled':
            status = 4
            break
          default:
            status = null
        }

        let response
        if (status) {
          // 按状态获取当前用户的订单
          response = await getUserOrdersByStatus(userInfo.value.id, status)
        } else {
          // 获取用户的所有订单
          response = await getUserOrders(userInfo.value.id)
        }

        if (response.code === 200) {
          let ordersData = response.data || []
          
          // 如果不是按特定状态查询，需要进一步过滤
          if (!status) {
            // 不需要额外过滤，因为getUserOrders已经返回用户的订单
          }
          
          // 获取每个订单的详细信息
          for (let order of ordersData) {
            try {
              const orderDetailResponse = await getOrderDetail(order.id)
              if (orderDetailResponse.code === 200) {
                order.items = orderDetailResponse.data.items || []
              } else {
                order.items = []
              }
            } catch (error) {
              console.error(`获取订单 ${order.id} 的详情失败:`, error)
              order.items = []
            }
          }
          
          orders.value = ordersData
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载订单失败:', error)
        ElMessage.error('加载订单失败')
      }
    }

    const getStatusText = (status) => {
      const statusMap = {
        1: '待接单',
        2: '制作中',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知状态'
    }

    const getStatusTagType = (status) => {
      const typeMap = {
        1: 'warning', // 待接单 - 黄色
        2: 'info',    // 制作中 - 蓝色
        3: 'success', // 已完成 - 绿色
        4: 'danger'   // 已取消 - 红色
      }
      return typeMap[status] || 'info'
    }

    const getSugarText = (sugarValue) => {
      const sugarMap = {
        1: '正常糖',
        2: '少糖',
        3: '不加糖'
      }
      return sugarMap[sugarValue] || '未知'
    }

    const getTempText = (tempValue) => {
      const tempMap = {
        1: '烫',
        2: '温热',
        3: '少冰',
        4: '正常冰'
      }
      return tempMap[tempValue] || '未知'
    }

    const showActions = (status) => {
      return status === 1 || status === 3 // 待接单或已完成显示操作按钮
    }

    const cancelOrder = async (orderId) => {
      try {
        await ElMessageBox.confirm('确定要取消此订单吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await cancelOrderApi(orderId)

        if (response.code === 200) {
          ElMessage.success('订单已取消')
          loadOrders() // 重新加载订单
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('取消订单失败:', error)
          ElMessage.error('取消订单失败')
        }
      }
    }



    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }

    return {
      orders,
      activeTab,
      isLoggedIn,
      userInfo,
      getStatusText,
      getStatusTagType,
      getSugarText,
      getTempText,
      showActions,
      cancelOrder,
      formatDate,
      loadOrders
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
  margin-top: 40px;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.order-card {
  margin-bottom: 20px;
  width: 50% !important; /* 设定为原来宽度的一半 */
  display: block; /* 确保是块级元素 */
  margin-left: auto; /* 居中对齐 */
  margin-right: auto; /* 居中对齐 */
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  gap: 20px;
}

.order-no, .order-time, .take-code {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-weight: bold;
}

.order-details {
  margin-bottom: 15px;
}

.order-items {
  margin-bottom: 10px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 5px;
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.item-specs {
  display: flex;
  gap: 10px;
}

.spec {
  font-size: 12px;
  color: #999;
}

.item-quantity, .item-price {
  margin-left: 10px;
  font-weight: bold;
}

.order-total {
  padding: 10px 0;
  border-top: 1px solid #eee;
}

.order-total-content {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 15px; /* 在取餐码和合计金额之间留一定间隙 */
}

.item-take-code {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
}

.take-code-label {
  font-size: 14px;
  color: #666;
  margin-right: 8px;
}

.take-code-value {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
  letter-spacing: 2px;
}

.total-label {
  font-size: 16px;
  color: #666;
}

.total-amount {
  font-size: 20px;
  color: #e74c3c;
  font-weight: bold;
}

.order-actions {
  text-align: right;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}
</style>