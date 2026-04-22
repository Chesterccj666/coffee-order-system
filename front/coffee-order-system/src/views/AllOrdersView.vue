<template>
  <div class="all-orders-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 - 完全照搬 MenuView.vue 的设计（店员版本） -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">全部订单</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <router-link to="/staff" class="nav-link">
              <span class="nav-text">待处理订单</span>
              <span class="nav-underline"></span>
            </router-link>
            <router-link to="/staff/all-orders" class="nav-link active">
              <span class="nav-text">全部订单</span>
              <span class="nav-underline"></span>
            </router-link>
            <router-link to="/profile" class="nav-link nav-link--user">
              <span class="user-avatar">{{ userInfo.username?.charAt(0) }}</span>
              <span class="nav-text">{{ userInfo.username }}</span>
              <span class="nav-underline"></span>
            </router-link>
          </nav>
        </div>
      </header>

      <!-- 主内容区域 -->
      <main class="orders-main">
        <div class="orders-wrapper">
          <!-- 装饰性线条 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>

          <!-- 页面标题 -->
          <div class="page-header">
            <h1 class="page-title">
              <span class="title-word title-word--1">全部</span>
              <span class="title-word title-word--2">订单</span>
            </h1>
            <p class="page-subtitle">浏览历史记录 · 追踪每笔交易</p>
          </div>

          <!-- 日期筛选栏 -->
          <div class="filter-bar">
            <div class="filter-bar-inner">
              <div class="date-picker-wrapper">
                <span class="filter-label">选择日期</span>
                <div class="date-navigation">
                  <button class="nav-btn nav-btn--prev" @click="previousDay">
                    <svg viewBox="0 0 24 24" fill="none" class="nav-icon">
                      <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                  <el-date-picker
                    v-model="selectedDate"
                    type="date"
                    placeholder="选择日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    @change="filterOrdersByDate"
                    class="custom-date-picker"
                  />
                  <button class="nav-btn nav-btn--next" @click="nextDay" :disabled="isToday(selectedDate)">
                    <svg viewBox="0 0 24 24" fill="none" class="nav-icon">
                      <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                </div>
              </div>
              <div class="filter-actions">
                <button class="filter-btn filter-btn--today" @click="showTodayOrders">
                  <span class="btn-icon">📅</span>
                  <span>今天</span>
                </button>
                <button class="filter-btn filter-btn--clear" @click="clearDateFilter">
                  <span class="btn-icon">🔄</span>
                  <span>清除筛选</span>
                </button>
              </div>
            </div>
          </div>

          <!-- 订单统计 -->
          <div class="stats-summary" v-if="completedOrders.length > 0">
            <div class="stat-item">
              <span class="stat-label">订单数量</span>
              <span class="stat-value">{{ completedOrders.length }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">总营业额</span>
              <span class="stat-value">¥{{ totalCompletedAmount.toFixed(2) }}</span>
            </div>
          </div>

          <!-- 订单列表 -->
          <div class="orders-grid" v-if="filteredOrders.length > 0">
            <div 
              v-for="(order, index) in filteredOrders" 
              :key="order.id" 
              class="order-card"
              :style="{ '--order-index': index }"
            >
              <!-- 订单头部 -->
              <div class="order-card-header">
                <div class="order-number">
                  <span class="label">订单号</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <div class="order-status" :class="`status-${getStatusClass(order.status)}`">
                  <span class="status-dot"></span>
                  <span class="status-text">{{ getStatusText(order.status) }}</span>
                </div>
              </div>

              <!-- 订单信息 -->
              <div class="order-card-body">
                <div class="info-row">
                  <span class="info-label">下单时间</span>
                  <span class="info-value">{{ formatDate(order.orderTime) }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">取餐码</span>
                  <span class="info-value take-code">{{ order.takeCode || '暂无' }}</span>
                </div>
                <div class="info-row" v-if="order.remark">
                  <span class="info-label">顾客备注</span>
                  <span class="info-value remark">{{ order.remark }}</span>
                </div>
                <div class="info-row total-row">
                  <span class="info-label">订单总额</span>
                  <span class="info-value total-amount">¥{{ order.totalAmount }}</span>
                </div>
              </div>

              <!-- 订单详情（悬浮展示） -->
              <el-popover
                placement="right"
                :width="360"
                trigger="hover"
                popper-class="order-detail-popover"
              >
                <template #reference>
                  <button class="view-details-btn">
                    <span>查看详情</span>
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M9 5L16 12L9 19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                </template>
                
                <div class="popover-content">
                  <h4 class="popover-title">订单商品明细</h4>
                  <div class="popover-items">
                    <div v-for="item in order.items" :key="item.id" class="popover-item">
                      <div class="popover-item-header">
                        <span class="item-name">{{ item.coffeeName }}</span>
                        <span class="item-quantity">×{{ item.quantity }}</span>
                      </div>
                      <div class="popover-item-details">
                        <span class="item-spec">
                          <span class="spec-icon">🍯</span>
                          {{ getSweetText(item.sweet) }}
                        </span>
                        <span class="item-spec">
                          <span class="spec-icon">{{ getTemperatureIcon(item.temperature) }}</span>
                          {{ getTemperatureText(item.temperature) }}
                        </span>
                      </div>
                      <div class="popover-item-footer">
                        <span class="item-price">单价 ¥{{ item.price }}</span>
                        <span class="item-subtotal">小计 ¥{{ item.totalPrice }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </el-popover>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <div class="empty-illustration">
              <div class="coffee-cup-empty">
                <div class="cup-body"></div>
                <div class="cup-handle"></div>
              </div>
            </div>
            <h3 class="empty-title">
              {{ selectedDate ? `${selectedDate} 暂无订单` : '暂无订单记录' }}
            </h3>
            <p class="empty-desc">换个日期试试，或者去处理待接单</p>
            <button class="browse-menu-btn" @click="$router.push('/staff')">
              <span>前往待处理订单</span>
              <svg viewBox="0 0 24 24" fill="none" class="btn-arrow">
                <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllOrdersByStatus, getOrderDetail } from '@/api/order'
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
    
    const cursorRef = ref(null)
    const headerRef = ref(null)

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadOrders()
        showTodayOrders()
      }
      initCustomCursor()
    })

    onUnmounted(() => {
      window.removeEventListener('mousemove', handleMouseMove)
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
        const response = await getAllOrdersByStatus(0)
        if (response.code === 200) {
          let ordersData = response.data || []
          
          orders.value = ordersData
        
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
      
      const selectedDateObj = new Date(date)
      selectedDateObj.setHours(0, 0, 0, 0)
      const selectedDay = selectedDateObj.getFullYear() + '-' + 
                         String(selectedDateObj.getMonth() + 1).padStart(2, '0') + '-' + 
                         String(selectedDateObj.getDate()).padStart(2, '0')
      
      filteredOrders.value = orders.value.filter(order => {
        const orderDateObj = new Date(order.orderTime)
        orderDateObj.setHours(0, 0, 0, 0)
        const orderDate = orderDateObj.getFullYear() + '-' + 
                         String(orderDateObj.getMonth() + 1).padStart(2, '0') + '-' + 
                         String(orderDateObj.getDate()).padStart(2, '0')
        return orderDate === selectedDay
      })
    }
    
    const showTodayOrders = () => {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
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

    // 前一天按钮点击事件
    const previousDay = () => {
      const currentDate = selectedDate.value ? new Date(selectedDate.value) : new Date()
      const prevDate = new Date(currentDate)
      prevDate.setDate(prevDate.getDate() - 1)
      selectedDate.value = formatDateToYYYYMMDD(prevDate)
      filterOrdersByDate(selectedDate.value)
    }

    // 后一天按钮点击事件
    const nextDay = () => {
      if (isToday(selectedDate.value)) return
      const currentDate = selectedDate.value ? new Date(selectedDate.value) : new Date()
      const nextDate = new Date(currentDate)
      nextDate.setDate(nextDate.getDate() + 1)
      selectedDate.value = formatDateToYYYYMMDD(nextDate)
      filterOrdersByDate(selectedDate.value)
    }

    // 检查是否是今天
    const isToday = (dateString) => {
      if (!dateString) return false
      const date = new Date(dateString)
      const today = new Date()
      return date.toDateString() === today.toDateString()
    }

    // 格式化日期为YYYY-MM-DD
    const formatDateToYYYYMMDD = (date) => {
      return date.getFullYear() + '-' + 
             String(date.getMonth() + 1).padStart(2, '0') + '-' + 
             String(date.getDate()).padStart(2, '0')
    }

    const totalAmount = computed(() => {
      return filteredOrders.value.reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
    })

    // 只统计已完成订单的数量
    const completedOrders = computed(() => {
      return filteredOrders.value.filter(order => order.status === 3)
    })

    // 只统计已完成订单的总营业额
    const totalCompletedAmount = computed(() => {
      return completedOrders.value.reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
    })

    const getStatusText = (status) => {
      const statusMap = { 1: '待接单', 2: '制作中', 3: '已完成', 4: '已取消' }
      return statusMap[status] || '未知状态'
    }

    const getStatusClass = (status) => {
      const classMap = { 1: 'pending', 2: 'processing', 3: 'completed', 4: 'cancelled' }
      return classMap[status] || 'default'
    }

    const getSweetText = (sweet) => {
      const sweetMap = { 1: '正常糖', 2: '少糖', 3: '不加糖' }
      return sweetMap[sweet] || '未知'
    }

    const getTemperatureText = (temperature) => {
      const tempMap = { 1: '烫', 2: '温热', 3: '少冰', 4: '正常冰' }
      return tempMap[temperature] || '未知'
    }

    const getTemperatureIcon = (temperature) => {
      const iconMap = { 1: '🔥', 2: '☀️', 3: '❄️', 4: '🧊' }
      return iconMap[temperature] || '☕'
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).replace(/\//g, '-')
    }

    return {
      orders,
      filteredOrders,
      completedOrders,
      totalCompletedAmount,
      selectedDate,
      userInfo,
      cursorRef,
      headerRef,
      totalAmount,
      loadOrders,
      filterOrdersByDate,
      showTodayOrders,
      clearDateFilter,
      previousDay,
      nextDay,
      isToday,
      getStatusText,
      getStatusClass,
      getSweetText,
      getTemperatureText,
      getTemperatureIcon,
      formatDate
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.all-orders-page {
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
  background: radial-gradient(circle at 80% 20%, rgba(232, 184, 109, 0.06) 0%, transparent 50%),
              radial-gradient(circle at 10% 80%, rgba(125, 155, 122, 0.05) 0%, transparent 50%);
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
  max-width: 1400px;
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

.logo-area:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 32px;
  animation: steamFloat 3s ease-in-out infinite;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-main {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 24px;
  font-weight: 600;
  color: var(--dark-roast);
  letter-spacing: -0.5px;
  line-height: 1.2;
}

.logo-sub {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 400;
  color: var(--warm-brown);
  letter-spacing: 2px;
  text-transform: uppercase;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 32px;
}

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

.nav-link:hover {
  color: var(--terracotta);
}

.nav-link.active {
  color: var(--terracotta);
}

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
.nav-link.active .nav-underline {
  width: 100%;
}

.nav-link--user {
  gap: 6px;
}

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
  text-transform: uppercase;
}

/* ===== 主内容区域 ===== */
.orders-main {
  padding-top: 120px;
  padding-bottom: 60px;
  min-height: 100vh;
}

.orders-wrapper {
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 40px;
  position: relative;
}

/* ===== 装饰线条 ===== */
.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.1;
  pointer-events: none;
}

.decorative-line--1 {
  top: 20px;
  right: -50px;
  width: 250px;
  height: 1px;
  transform: rotate(-15deg);
}

.decorative-line--2 {
  bottom: 100px;
  left: -80px;
  width: 300px;
  height: 1px;
  transform: rotate(-10deg);
}

/* ===== 页面标题 ===== */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 48px;
  font-weight: 400;
  line-height: 1.1;
  margin: 0 0 12px 0;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.title-word {
  display: inline-block;
  opacity: 0;
  animation: titleReveal 0.6s ease forwards;
}

.title-word--1 {
  color: var(--espresso);
  font-style: italic;
  animation-delay: 0.1s;
}

.title-word--2 {
  color: var(--dark-roast);
  font-weight: 600;
  animation-delay: 0.3s;
}

.page-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--warm-brown);
  letter-spacing: 3px;
  text-transform: uppercase;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.5s forwards;
}

/* ===== 筛选栏 ===== */
.filter-bar {
  margin-bottom: 32px;
}

.filter-bar-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 60px;
}

.date-picker-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-navigation {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-btn {
  width: 36px;
  height: 36px;
  border: 1px solid rgba(212, 163, 115, 0.3);
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--warm-brown);
}

.nav-btn:hover:not(:disabled) {
  background: var(--golden);
  border-color: var(--golden);
  color: var(--espresso);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(212, 163, 115, 0.3);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.nav-icon {
  width: 16px;
  height: 16px;
}

.filter-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--warm-brown);
}

.custom-date-picker {
  width: 200px;
}

:deep(.custom-date-picker .el-input__wrapper) {
  background: var(--cream);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 40px;
  box-shadow: none;
  padding: 8px 16px;
}

:deep(.custom-date-picker .el-input__wrapper:hover) {
  border-color: var(--golden);
}

:deep(.custom-date-picker .el-input__wrapper.is-focus) {
  border-color: var(--golden);
  box-shadow: 0 0 0 3px rgba(212, 163, 115, 0.1);
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.filter-btn--today {
  background: rgba(212, 163, 115, 0.12);
  color: var(--warm-brown);
  border: 1px solid rgba(212, 163, 115, 0.2);
}

.filter-btn--today:hover {
  background: rgba(212, 163, 115, 0.2);
  border-color: var(--golden);
}

.filter-btn--clear {
  background: transparent;
  border: 1px solid rgba(212, 163, 115, 0.2);
  color: var(--warm-brown);
}

.filter-btn--clear:hover {
  background: rgba(212, 163, 115, 0.08);
  border-color: var(--golden);
}

.btn-icon {
  font-size: 16px;
}

/* ===== 统计摘要 ===== */
.stats-summary {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-bottom: 32px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 60px;
}

.stat-item {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.stat-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  opacity: 0.8;
}

.stat-value {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 600;
  color: var(--terracotta);
}

/* ===== 订单网格 ===== */
.orders-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.order-card {
  position: relative;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
  padding: 24px;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
  animation: cardFadeIn 0.5s ease forwards;
  animation-delay: calc(var(--order-index) * 0.06s);
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(58, 36, 26, 0.1);
  border-color: rgba(212, 163, 115, 0.35);
  background: rgba(255, 255, 255, 0.8);
}

/* 订单头部 */
.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.15);
}

.order-number {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-number .label {
  font-family: 'DM Sans', sans-serif;
  font-size: 11px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--warm-brown);
  opacity: 0.6;
}

.order-number .value {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--espresso);
}

.order-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulseDot 1.5s ease-in-out infinite;
}

.status-pending {
  background: rgba(232, 184, 109, 0.12);
  color: #B8860B;
}
.status-pending .status-dot { background: #DAA520; }

.status-processing {
  background: rgba(212, 163, 115, 0.12);
  color: var(--warm-brown);
}
.status-processing .status-dot { background: var(--golden); }

.status-completed {
  background: rgba(125, 155, 122, 0.1);
  color: #5C7A5A;
}
.status-completed .status-dot { background: var(--sage); }

.status-cancelled {
  background: rgba(200, 121, 92, 0.1);
  color: var(--terracotta);
}
.status-cancelled .status-dot { background: var(--terracotta); }

/* 订单主体 */
.order-card-body {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.info-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
}

.info-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--espresso);
}

.info-value.take-code {
  font-family: 'Courier New', monospace;
  font-size: 18px;
  font-weight: 700;
  color: var(--terracotta);
  letter-spacing: 2px;
}

.info-value.remark {
  max-width: 200px;
  text-align: right;
  font-style: italic;
  opacity: 0.8;
}

.total-row {
  margin-top: 8px;
  padding-top: 12px;
  border-top: 1px solid rgba(212, 163, 115, 0.15);
}

.total-row .info-label {
  font-size: 15px;
  font-weight: 500;
  color: var(--espresso);
  opacity: 1;
}

.total-amount {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 600;
  color: var(--terracotta);
}

/* 查看详情按钮 */
.view-details-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px 20px;
  background: transparent;
  border: 1.5px solid rgba(212, 163, 115, 0.3);
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--warm-brown);
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-details-btn:hover {
  background: rgba(212, 163, 115, 0.08);
  border-color: var(--golden);
  color: var(--dark-roast);
}

.view-details-btn svg {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.view-details-btn:hover svg {
  transform: translateX(3px);
}

/* 详情弹窗 */
:deep(.order-detail-popover) {
  border-radius: 20px !important;
  overflow: hidden;
  border: 1px solid rgba(212, 163, 115, 0.2) !important;
  box-shadow: 0 20px 40px rgba(58, 36, 26, 0.15) !important;
  padding: 0 !important;
}

.popover-content {
  background: var(--crema);
}

.popover-title {
  font-family: 'Playfair Display', serif;
  font-size: 18px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0;
  padding: 20px 20px 12px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.15);
}

.popover-items {
  max-height: 320px;
  overflow-y: auto;
  padding: 12px 16px;
}

.popover-item {
  padding: 14px 0;
  border-bottom: 1px solid rgba(212, 163, 115, 0.1);
}

.popover-item:last-child {
  border-bottom: none;
}

.popover-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-name {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--espresso);
}

.item-quantity {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: var(--terracotta);
}

.popover-item-details {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.item-spec {
  display: flex;
  align-items: center;
  gap: 4px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  background: rgba(212, 163, 115, 0.08);
  padding: 3px 10px;
  border-radius: 20px;
}

.spec-icon {
  font-size: 11px;
}

.popover-item-footer {
  display: flex;
  justify-content: space-between;
}

.item-price,
.item-subtotal {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
}

.item-subtotal {
  font-weight: 600;
  color: var(--terracotta);
}

.popover-items::-webkit-scrollbar {
  width: 4px;
}

.popover-items::-webkit-scrollbar-track {
  background: rgba(212, 163, 115, 0.1);
  border-radius: 10px;
}

.popover-items::-webkit-scrollbar-thumb {
  background: var(--golden);
  border-radius: 10px;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 80px 40px;
}

.empty-illustration {
  margin-bottom: 32px;
}

.coffee-cup-empty {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto;
}

.coffee-cup-empty .cup-body {
  width: 80px;
  height: 70px;
  background: linear-gradient(135deg, var(--warm-brown), var(--dark-roast));
  border-radius: 0 0 30px 30px;
  margin: 0 auto;
  position: relative;
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.2);
}

.coffee-cup-empty .cup-body::before {
  content: '';
  position: absolute;
  top: -15px;
  left: -5px;
  right: -5px;
  height: 20px;
  background: var(--crema);
  border-radius: 50%;
  border: 2px solid var(--golden);
}

.coffee-cup-empty .cup-handle {
  position: absolute;
  right: 10px;
  top: 20px;
  width: 25px;
  height: 35px;
  border: 4px solid var(--warm-brown);
  border-left: none;
  border-radius: 0 20px 20px 0;
}

.empty-title {
  font-family: 'Playfair Display', serif;
  font-size: 26px;
  color: var(--espresso);
  margin: 0 0 12px 0;
}

.empty-desc {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--warm-brown);
  margin-bottom: 32px;
}

.browse-menu-btn {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 14px 32px;
  background: transparent;
  border: 1.5px solid var(--golden);
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--dark-roast);
  cursor: pointer;
  transition: all 0.3s ease;
}

.browse-menu-btn:hover {
  background: var(--dark-roast);
  border-color: var(--dark-roast);
  color: var(--crema);
}

.browse-menu-btn .btn-arrow {
  width: 20px;
  transition: transform 0.3s ease;
}

.browse-menu-btn:hover .btn-arrow {
  transform: translateX(4px);
}

/* ===== 动画 ===== */
@keyframes titleReveal {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes cardFadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes steamFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

@keyframes pulseDot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .orders-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 16px 20px;
  }
  
  .orders-wrapper {
    padding: 0 20px;
  }
  
  .page-title {
    font-size: 36px;
  }
  
  .filter-bar-inner {
    flex-direction: column;
    gap: 16px;
  }
  
  .stats-summary {
    flex-direction: column;
    gap: 16px;
    align-items: center;
  }
}

@media (max-width: 480px) {
  .logo-main {
    font-size: 18px;
  }
  
  .nav-menu {
    gap: 16px;
  }
  
  .date-picker-wrapper {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }
  
  .custom-date-picker {
    width: 100%;
  }
}
</style>