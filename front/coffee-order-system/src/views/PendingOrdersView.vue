<template>
  <div class="pending-orders-page">
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
              <span class="logo-sub">待处理订单</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <router-link to="/staff" class="nav-link active">
              <span class="nav-text">待处理订单</span>
              <span class="nav-underline"></span>
              <span class="order-badge" v-if="pendingOrders.length + processingOrders.length > 0">
                {{ pendingOrders.length + processingOrders.length }}
              </span>
            </router-link>
            <router-link to="/staff/all-orders" class="nav-link">
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
              <span class="title-word title-word--1">待处理</span>
              <span class="title-word title-word--2">订单</span>
            </h1>
            <p class="page-subtitle">高效处理 · 用心服务每一杯咖啡</p>
          </div>

          <!-- 统计摘要 -->
          <div class="stats-summary">
            <div class="stat-item stat-item--pending">
              <span class="stat-icon">⏳</span>
              <div class="stat-content">
                <span class="stat-value">{{ pendingOrders.length }}</span>
                <span class="stat-label">待接单</span>
              </div>
            </div>
            <div class="stat-item stat-item--processing">
              <span class="stat-icon">🔥</span>
              <div class="stat-content">
                <span class="stat-value">{{ processingOrders.length }}</span>
                <span class="stat-label">制作中</span>
              </div>
            </div>
            <div class="stat-item stat-item--total">
              <span class="stat-icon">📋</span>
              <div class="stat-content">
                <span class="stat-value">{{ pendingOrders.length + processingOrders.length }}</span>
                <span class="stat-label">总计待处理</span>
              </div>
            </div>
          </div>

          <!-- 左右两栏布局 -->
          <div class="orders-columns">
            <!-- 左侧：待接单 -->
            <div class="column pending-column">
              <div class="column-header">
                <div class="header-left">
                  <span class="column-icon">⏳</span>
                  <h3 class="column-title">待接单</h3>
                </div>
                <span class="column-count">{{ pendingOrders.length }} 单</span>
              </div>
              
              <div class="column-content">
                <div 
                  v-for="(order, index) in pendingOrders" 
                  :key="order.id" 
                  class="order-card"
                  :class="{ 'new-order': isNewOrder(order) }"
                  :style="{ '--order-index': index }"
                >
                  <!-- 订单头部 -->
                  <div class="order-card-header">
                    <div class="order-number">
                      <span class="label">订单号</span>
                      <span class="value">{{ order.orderNo }}</span>
                    </div>
                    <div class="order-time">
                      <span class="time-icon">🕐</span>
                      <span class="time-value">{{ formatTimeAgo(order.orderTime) }}</span>
                    </div>
                  </div>

                  <!-- 取餐码与备注 -->
                  <div class="order-meta">
                    <div class="take-code-badge">
                      <span class="badge-label">取餐码</span>
                      <span class="badge-value">{{ order.takeCode }}</span>
                    </div>
                    <div class="remark-badge" v-if="order.remark">
                      <span class="badge-label">备注</span>
                      <span class="badge-value">{{ order.remark }}</span>
                    </div>
                  </div>

                  <!-- 订单商品列表 -->
                  <div class="order-items">
                    <div v-for="item in order.items" :key="item.id" class="order-item">
                      <div class="item-image-wrapper">
                        <img :src="item.coffeeImage" :alt="item.coffeeName" class="item-image" />
                      </div>
                      <div class="item-details">
                        <div class="item-name-row">
                          <span class="item-name">{{ item.coffeeName }}</span>
                          <span class="item-quantity">×{{ item.quantity }}</span>
                        </div>
                        <div class="item-specs">
                          <span class="spec-tag">
                            <span class="spec-icon">🍯</span>
                            {{ getSweetText(item.sweet) }}
                          </span>
                          <span class="spec-tag">
                            <span class="spec-icon">{{ getTemperatureIcon(item.temperature) }}</span>
                            {{ getTemperatureText(item.temperature) }}
                          </span>
                        </div>
                        <div class="item-price-row">
                          <span class="item-unit-price">¥{{ item.price }}</span>
                          <span class="item-subtotal">小计 ¥{{ item.totalPrice }}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 订单底部 -->
                  <div class="order-card-footer">
                    <div class="order-total">
                      <span class="total-label">订单总额</span>
                      <span class="total-value">¥{{ order.totalAmount }}</span>
                    </div>
                    <button class="accept-btn" @click="acceptOrder(order.id)">
                      <span>接单</span>
                      <svg viewBox="0 0 24 24" fill="none">
                        <path d="M5 13L9 17L19 7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </button>
                  </div>
                </div>

                <div v-if="pendingOrders.length === 0" class="empty-column">
                  <div class="empty-illustration">
                    <span class="empty-icon">☕</span>
                  </div>
                  <p class="empty-text">暂无待接单订单</p>
                  <p class="empty-hint">新订单会实时显示在这里</p>
                </div>
              </div>
            </div>

            <!-- 右侧：制作中 -->
            <div class="column processing-column">
              <div class="column-header">
                <div class="header-left">
                  <span class="column-icon">🔥</span>
                  <h3 class="column-title">制作中</h3>
                </div>
                <span class="column-count">{{ processingOrders.length }} 单</span>
              </div>
              
              <div class="column-content">
                <div 
                  v-for="(order, index) in processingOrders" 
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
                    <div class="order-status-badge status-processing">
                      <span class="status-dot"></span>
                      <span class="status-text">制作中</span>
                    </div>
                  </div>

                  <!-- 取餐码与备注 -->
                  <div class="order-meta">
                    <div class="take-code-badge processing-badge">
                      <span class="badge-label">取餐码</span>
                      <span class="badge-value">{{ order.takeCode }}</span>
                    </div>
                    <div class="remark-badge" v-if="order.remark">
                      <span class="badge-label">备注</span>
                      <span class="badge-value">{{ order.remark }}</span>
                    </div>
                  </div>

                  <!-- 订单商品列表 -->
                  <div class="order-items">
                    <div v-for="item in order.items" :key="item.id" class="order-item">
                      <div class="item-image-wrapper">
                        <img :src="item.coffeeImage" :alt="item.coffeeName" class="item-image" />
                      </div>
                      <div class="item-details">
                        <div class="item-name-row">
                          <span class="item-name">{{ item.coffeeName }}</span>
                          <span class="item-quantity">×{{ item.quantity }}</span>
                        </div>
                        <div class="item-specs">
                          <span class="spec-tag">
                            <span class="spec-icon">🍯</span>
                            {{ getSweetText(item.sweet) }}
                          </span>
                          <span class="spec-tag">
                            <span class="spec-icon">{{ getTemperatureIcon(item.temperature) }}</span>
                            {{ getTemperatureText(item.temperature) }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 订单底部 -->
                  <div class="order-card-footer">
                    <div class="order-total">
                      <span class="total-label">订单总额</span>
                      <span class="total-value">¥{{ order.totalAmount }}</span>
                    </div>
                    <button class="complete-btn" @click="completeOrder(order.id)">
                      <span>出餐完成</span>
                      <svg viewBox="0 0 24 24" fill="none">
                        <path d="M5 13L9 17L19 7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </button>
                  </div>
                </div>

                <div v-if="processingOrders.length === 0" class="empty-column">
                  <div class="empty-illustration">
                    <span class="empty-icon">🛎️</span>
                  </div>
                  <p class="empty-text">暂无制作中订单</p>
                  <p class="empty-hint">接单后订单会显示在这里</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrdersByStatus, updateOrderStatus, getOrderDetail } from '@/api/order'
import { useRouter } from 'vue-router'

export default {
  name: 'PendingOrdersView',
  setup() {
    const router = useRouter()
    const pendingOrders = ref([])
    const processingOrders = ref([])
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    
    const cursorRef = ref(null)
    const headerRef = ref(null)

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadOrders()
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
        const pendingResponse = await getOrdersByStatus(1)
        const processingResponse = await getOrdersByStatus(2)
        
        const pendingOrdersData = pendingResponse.code === 200 ? pendingResponse.data || [] : []
        const processingOrdersData = processingResponse.code === 200 ? processingResponse.data || [] : []
        
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
        
        pendingOrdersData.sort((a, b) => new Date(a.orderTime) - new Date(b.orderTime))
        processingOrdersData.sort((a, b) => new Date(a.orderTime) - new Date(b.orderTime))
        
        pendingOrders.value = pendingOrdersData
        processingOrders.value = processingOrdersData
      } catch (error) {
        console.error('加载订单失败:', error)
        ElMessage.error('加载订单失败')
      }
    }

    const isNewOrder = (order) => {
      const orderTime = new Date(order.orderTime)
      const now = new Date()
      const diffMinutes = (now - orderTime) / (1000 * 60)
      return diffMinutes < 2
    }

    const formatTimeAgo = (dateString) => {
      if (!dateString) return ''
      const orderTime = new Date(dateString)
      const now = new Date()
      const diffMinutes = Math.floor((now - orderTime) / (1000 * 60))
      
      if (diffMinutes < 1) return '刚刚'
      if (diffMinutes < 60) return `${diffMinutes}分钟前`
      const diffHours = Math.floor(diffMinutes / 60)
      if (diffHours < 24) return `${diffHours}小时前`
      return orderTime.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const acceptOrder = async (orderId) => {
      try {
        await ElMessageBox.confirm('确定要接此订单吗？', '确认接单', {
          confirmButtonText: '确定接单',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await updateOrderStatus(orderId, 2)

        if (response.code === 200) {
          ElMessage.success('接单成功，开始制作')
          
          const pendingIndex = pendingOrders.value.findIndex(order => order.id === orderId)
          if (pendingIndex !== -1) {
            const acceptedOrder = pendingOrders.value.splice(pendingIndex, 1)[0]
            acceptedOrder.status = 2
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
        await ElMessageBox.confirm('确认订单已完成，可以出餐了吗？', '确认出餐', {
          confirmButtonText: '确认出餐',
          cancelButtonText: '取消',
          type: 'success'
        })

        const response = await updateOrderStatus(orderId, 3)

        if (response.code === 200) {
          ElMessage.success('出餐成功，顾客可取餐')
          
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

    return {
      pendingOrders,
      processingOrders,
      userInfo,
      cursorRef,
      headerRef,
      loadOrders,
      isNewOrder,
      formatTimeAgo,
      acceptOrder,
      completeOrder,
      getSweetText,
      getTemperatureText,
      getTemperatureIcon
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.pending-orders-page {
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

.order-badge {
  background: var(--terracotta);
  color: var(--crema);
  font-size: 11px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 20px;
  margin-left: 4px;
  animation: badgePulse 2s infinite;
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
  padding-bottom: 40px;
  min-height: 100vh;
}

.orders-wrapper {
  max-width: 1600px;
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
  margin-bottom: 32px;
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

/* ===== 统计摘要 ===== */
.stats-summary {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 32px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 60px;
}

.stat-icon {
  font-size: 32px;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-family: 'Playfair Display', serif;
  font-size: 36px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  opacity: 0.8;
}

.stat-item--pending .stat-value {
  color: #DAA520;
}

.stat-item--processing .stat-value {
  color: var(--terracotta);
}

.stat-item--total .stat-value {
  color: var(--dark-roast);
}

/* ===== 两栏布局 ===== */
.orders-columns {
  display: flex;
  gap: 30px;
}

.column {
  flex: 1;
  min-width: 0;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 28px;
  overflow: hidden;
}

.column-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.2);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.column-icon {
  font-size: 24px;
}

.column-title {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0;
}

.column-count {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--warm-brown);
  background: rgba(212, 163, 115, 0.1);
  padding: 6px 14px;
  border-radius: 30px;
}

.column-content {
  padding: 20px;
  max-height: calc(100vh - 320px);
  overflow-y: auto;
}

/* ===== 订单卡片 ===== */
.order-card {
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  opacity: 1;
  transform: translateY(20px);
  animation: cardFadeIn 0.4s ease forwards;
  animation-delay: calc(var(--order-index) * 0.05s);
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(58, 36, 26, 0.1);
  border-color: rgba(212, 163, 115, 0.35);
}

.order-card.new-order {
  border-left: 4px solid var(--terracotta);
  animation: newOrderPulse 3s ease-in-out;
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-number {
  display: flex;
  flex-direction: column;
  gap: 2px;
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

.order-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
}

.time-icon {
  opacity: 0.6;
}

.order-status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: 30px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  font-weight: 500;
}

.status-processing {
  background: rgba(212, 163, 115, 0.12);
  color: var(--warm-brown);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--golden);
  animation: pulseDot 1.5s ease-in-out infinite;
}

/* 取餐码与备注 */
.order-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.take-code-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(212, 163, 115, 0.1);
  padding: 6px 14px;
  border-radius: 30px;
}

.take-code-badge.processing-badge {
  background: rgba(232, 184, 109, 0.12);
}

.badge-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  opacity: 0.7;
}

.badge-value {
  font-family: 'Courier New', monospace;
  font-size: 18px;
  font-weight: 700;
  color: var(--terracotta);
  letter-spacing: 2px;
}

.remark-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(200, 121, 92, 0.08);
  padding: 6px 14px;
  border-radius: 30px;
  max-width: 100%;
}

.remark-badge .badge-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 400;
  color: var(--warm-brown);
  letter-spacing: 0;
}

/* 订单商品 */
.order-items {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.15);
}

.order-item {
  display: flex;
  gap: 14px;
  padding: 10px 0;
}

.order-item:not(:last-child) {
  border-bottom: 1px dashed rgba(212, 163, 115, 0.1);
}

.item-image-wrapper {
  width: 55px;
  height: 55px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(58, 36, 26, 0.1);
  flex-shrink: 0;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-details {
  flex: 1;
}

.item-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
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

.item-specs {
  display: flex;
  gap: 8px;
  margin-bottom: 6px;
}

.spec-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-family: 'DM Sans', sans-serif;
  font-size: 11px;
  color: var(--warm-brown);
  background: rgba(212, 163, 115, 0.08);
  padding: 3px 8px;
  border-radius: 20px;
}

.spec-icon {
  font-size: 10px;
}

.item-price-row {
  display: flex;
  gap: 16px;
}

.item-unit-price,
.item-subtotal {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
}

.item-subtotal {
  font-weight: 500;
  color: var(--terracotta);
}

/* 订单底部 */
.order-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.order-total {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
}

.total-value {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 600;
  color: var(--terracotta);
}

.accept-btn,
.complete-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.accept-btn {
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  color: var(--crema);
  box-shadow: 0 6px 16px rgba(58, 36, 26, 0.2);
}

.accept-btn:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(58, 36, 26, 0.25);
}

.complete-btn {
  background: linear-gradient(135deg, var(--sage), #5C7A5A);
  color: var(--crema);
  box-shadow: 0 6px 16px rgba(125, 155, 122, 0.25);
}

.complete-btn:hover {
  background: linear-gradient(135deg, #5C7A5A, #4A6648);
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(125, 155, 122, 0.3);
}

.accept-btn svg,
.complete-btn svg {
  width: 18px;
  height: 18px;
}

/* 空状态 */
.empty-column {
  text-align: center;
  padding: 60px 40px;
}

.empty-illustration {
  margin-bottom: 20px;
}

.empty-icon {
  font-size: 56px;
  opacity: 0.4;
}

.empty-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  color: var(--espresso);
  margin: 0 0 8px 0;
}

.empty-hint {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 0;
}

/* 滚动条 */
.column-content::-webkit-scrollbar {
  width: 5px;
}

.column-content::-webkit-scrollbar-track {
  background: rgba(212, 163, 115, 0.08);
  border-radius: 10px;
}

.column-content::-webkit-scrollbar-thumb {
  background: var(--golden);
  border-radius: 10px;
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

@keyframes badgePulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.05); }
}

@keyframes pulseDot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

@keyframes newOrderPulse {
  0% { border-left-color: var(--terracotta); }
  50% { border-left-color: var(--honey); }
  100% { border-left-color: var(--terracotta); }
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .orders-columns {
    flex-direction: column;
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
  
  .stats-summary {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .stat-item {
    padding: 16px 24px;
  }
  
  .stat-value {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .logo-main {
    font-size: 18px;
  }
  
  .nav-menu {
    gap: 16px;
  }
  
  .column-header {
    padding: 16px;
  }
  
  .column-content {
    padding: 12px;
  }
  
  .order-card-footer {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .accept-btn,
  .complete-btn {
    justify-content: center;
  }
}
</style>