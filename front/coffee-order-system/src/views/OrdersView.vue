<template>
  <div class="orders-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 - 悬浮玻璃效果 -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">我的订单</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <router-link to="/menu" class="nav-link active">
              <span class="nav-text">菜单</span>
              <span class="nav-underline"></span>
            </router-link>
            <router-link v-if="isLoggedIn && userInfo.role === 1" to="/cart" class="nav-link">
              <span class="nav-text">购物车</span>
              <span class="nav-underline"></span>
            </router-link>
            <router-link v-if="isLoggedIn && userInfo.role === 1" to="/orders" class="nav-link">
              <span class="nav-text">我的订单</span>
              <span class="nav-underline"></span>
            </router-link>
            
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

      <!-- 固定筛选栏 -->
      <div class="filter-bar-wrapper">
        <div class="filter-bar">
          <div 
            class="filter-tab" 
            :class="{ active: activeTab === 'all' }"
            @click="switchTab('all')"
          >
            <span class="tab-icon">📋</span>
            <span class="tab-text">全部订单</span>
            <span class="tab-indicator"></span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: activeTab === 'pending' }"
            @click="switchTab('pending')"
          >
            <span class="tab-icon">⏳</span>
            <span class="tab-text">待接单</span>
            <span class="tab-indicator"></span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: activeTab === 'processing' }"
            @click="switchTab('processing')"
          >
            <span class="tab-icon">🔥</span>
            <span class="tab-text">制作中</span>
            <span class="tab-indicator"></span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: activeTab === 'completed' }"
            @click="switchTab('completed')"
          >
            <span class="tab-icon">✨</span>
            <span class="tab-text">已完成</span>
            <span class="tab-indicator"></span>
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: activeTab === 'cancelled' }"
            @click="switchTab('cancelled')"
          >
            <span class="tab-icon">✕</span>
            <span class="tab-text">已取消</span>
            <span class="tab-indicator"></span>
          </div>
        </div>
      </div>

      <!-- 主内容区域 -->
      <main class="orders-main">
        <div class="orders-wrapper">
          <!-- 装饰性斜向线条 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>

          <!-- 标题区域 -->
          <div class="orders-header">
            <h1 class="orders-title">
              <span class="title-word title-word--1">订单</span>
              <span class="title-word title-word--2">记录</span>
            </h1>
            <p class="orders-subtitle">每一杯咖啡 · 都是独特的故事</p>
          </div>

          <!-- 空状态 -->
          <div v-if="orders.length === 0" class="empty-state">
            <div class="empty-illustration">
              <div class="coffee-cup-empty">
                <div class="cup-body"></div>
                <div class="cup-handle"></div>
                <div class="steam">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
            <h3 class="empty-title">暂无订单记录</h3>
            <p class="empty-desc">去挑选一杯心仪的咖啡吧</p>
            <button class="browse-menu-btn" @click="$router.push('/menu')">
              <span>浏览菜单</span>
              <svg viewBox="0 0 24 24" fill="none" class="btn-arrow">
                <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>

          <!-- 订单列表 -->
          <div v-else class="orders-list">
            <div 
              v-for="(order, index) in orders" 
              :key="order.id" 
              class="order-card"
              :style="{ '--order-index': index }"
            >
              <!-- 订单头部 -->
              <div class="order-card-header">
                <div class="order-meta">
                  <span class="order-number">
                    <span class="meta-label">订单号</span>
                    <span class="meta-value">{{ order.orderNo }}</span>
                  </span>
                  <span class="order-time">
                    <span class="meta-label">下单时间</span>
                    <span class="meta-value">{{ formatDate(order.orderTime) }}</span>
                  </span>
                </div>
                <div class="order-status-badge" :class="`status-${getStatusClass(order.status)}`">
                  <span class="status-text">{{ getStatusText(order.status) }}</span>
                </div>
              </div>

              <!-- 订单商品列表 -->
              <div class="order-items-section">
                <div 
                  v-for="(item, itemIndex) in order.items" 
                  :key="itemIndex" 
                  class="order-item"
                >
                  <div class="item-image-wrapper">
                    <img :src="item.coffeeImage" :alt="item.coffeeName" class="item-image" />
                    <div class="image-overlay"></div>
                  </div>
                  
                  <div class="item-details">
                    <h4 class="item-name">{{ item.coffeeName }}</h4>
                    <div class="item-preferences">
                      <span class="pref-badge">
                        <span class="pref-icon">🍯</span>
                        {{ getSugarText(item.sweet) }}
                      </span>
                      <span class="pref-badge">
                        <span class="pref-icon">{{ getTempIcon(item.temperature) }}</span>
                        {{ getTempText(item.temperature) }}
                      </span>
                    </div>
                  </div>

                  <div class="item-quantity">
                    <span class="quantity-multiply">×</span>
                    <span class="quantity-value">{{ item.quantity }}</span>
                  </div>

                  <div class="item-price">
                    <span class="price-currency">¥</span>
                    <span class="price-value">{{ item.totalPrice }}</span>
                  </div>
                </div>
              </div>

              <!-- 订单底部 -->
              <div class="order-card-footer">
                <div class="take-code-section" v-if="order.takeCode">
                  <span class="take-code-label">取餐码</span>
                  <span class="take-code-value">{{ order.takeCode }}</span>
                </div>

                <div class="order-total-section">
                  <span class="total-label">合计</span>
                  <span class="total-amount">¥{{ order.totalAmount }}</span>
                </div>

                <div class="order-actions-section" v-if="showActions(order.status)">
                  <button 
                    v-if="order.status === 1" 
                    class="action-btn action-btn--cancel"
                    @click="cancelOrder(order.id)"
                  >
                    <svg viewBox="0 0 24 24" fill="none" class="btn-icon">
                      <path d="M6 18L18 6M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                    <span>取消订单</span>
                  </button>
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
import { useRouter } from 'vue-router'
import { getUserOrders, getUserOrdersByStatus, getOrderDetail, cancelOrder as cancelOrderApi } from '@/api/order'

export default {
  name: 'OrdersView',
  setup() {
    const router = useRouter()
    const orders = ref([])
    const activeTab = ref('all')
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    
    const headerRef = ref(null)
    const cursorRef = ref(null)

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
      } else {
        ElMessage.warning('请先登录后查看订单')
        router.push('/login')
      }
    }

    const switchTab = (tabName) => {
      activeTab.value = tabName
      loadOrders()
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
          response = await getUserOrdersByStatus(userInfo.value.id, status)
        } else {
          response = await getUserOrders(userInfo.value.id)
        }

        if (response.code === 200) {
          let ordersData = response.data || []
          
          for (let order of ordersData) {
            try {
              const orderDetailResponse = await getOrderDetail(order.id)
              if (orderDetailResponse.code === 200) {
                order.items = orderDetailResponse.data.items || []
              } else {
                order.items = []
              }
            } catch (error) {
              console.error(`获取订单 ${order.id} 详情失败:`, error)
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

    const getStatusClass = (status) => {
      const classMap = {
        1: 'pending',
        2: 'processing',
        3: 'completed',
        4: 'cancelled'
      }
      return classMap[status] || 'default'
    }

    const getSugarText = (sugarValue) => {
      const sugarMap = {
        1: '正常糖',
        2: '少糖',
        3: '不加糖'
      }
      return sugarMap[sugarValue] || '正常糖'
    }

    const getTempText = (tempValue) => {
      const tempMap = {
        1: '烫',
        2: '温热',
        3: '少冰',
        4: '正常冰'
      }
      return tempMap[tempValue] || '温热'
    }

    const getTempIcon = (tempValue) => {
      const iconMap = {
        1: '🔥',
        2: '☀️',
        3: '❄️',
        4: '🧊'
      }
      return iconMap[tempValue] || '☕'
    }

    const showActions = (status) => {
      return status === 1 || status === 3
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
          loadOrders()
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
      activeTab,
      isLoggedIn,
      userInfo,
      headerRef,
      cursorRef,
      switchTab,
      getStatusText,
      getStatusClass,
      getSugarText,
      getTempText,
      getTempIcon,
      showActions,
      cancelOrder,
      formatDate,
      loadOrders
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables - 复古意式咖啡馆色盘 ===== */
:root {
  --espresso: #1E1B1A;
  --dark-roast: #3A241A;
  --warm-brown: #5C3A2A;
  --crema: #FDF8F2;
  --cream: #FFF5E6;
  --golden: #D4A373;
  --honey: #E8B86D;
  --terracotta: #C8795C;
  --sage: #7D9B7A;
  --soft-pink: #E8C3B9;
}

/* ===== 全局样式与背景 ===== */
.orders-page {
  min-height: 100vh;
  background-color: var(--crema);
  position: relative;
  overflow-x: hidden;
  font-family: 'DM Sans', 'Quicksand', sans-serif;
}

.background-texture {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-radial-gradient(circle at 20% 30%, rgba(212, 163, 115, 0.03) 0px, transparent 2px),
    repeating-linear-gradient(45deg, rgba(58, 36, 26, 0.02) 0px, rgba(58, 36, 26, 0.02) 1px, transparent 1px, transparent 8px);
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
  transition: width 0.2s, height 0.2s, border-color 0.2s;
  box-shadow: 0 0 15px rgba(212, 163, 115, 0.3);
}

/* ===== 页面容器 ===== */
.page-container {
  position: relative;
  z-index: 1;
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

/* ===== 导航菜单 ===== */
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

/* ===== 筛选栏 ===== */
.filter-bar-wrapper {
  position: fixed;
  top: 84px;
  left: 0;
  right: 0;
  z-index: 99;
  padding: 16px 40px;
  background: rgba(253, 248, 242, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(212, 163, 115, 0.12);
}

.filter-bar {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.4);
  padding: 6px;
  border-radius: 60px;
  border: 1px solid rgba(212, 163, 115, 0.15);
}

.filter-tab {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: transparent;
  overflow: hidden;
}

.filter-tab .tab-icon {
  font-size: 16px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.filter-tab .tab-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--warm-brown);
  transition: all 0.3s ease;
}

.filter-tab .tab-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--golden), var(--terracotta));
  transition: width 0.3s ease;
}

.filter-tab:hover {
  background: rgba(212, 163, 115, 0.08);
}

.filter-tab:hover .tab-icon {
  opacity: 1;
}

.filter-tab.active {
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.12), rgba(200, 121, 92, 0.08));
}

.filter-tab.active .tab-icon {
  opacity: 1;
  transform: scale(1.1);
}

.filter-tab.active .tab-text {
  color: var(--terracotta);
  font-weight: 600;
}

.filter-tab.active .tab-indicator {
  width: 60%;
}

/* ===== 主内容区域 ===== */
.orders-main {
  padding-top: 180px;
  padding-bottom: 60px;
  min-height: 100vh;
}

.orders-wrapper {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 40px;
  position: relative;
}

/* ===== 装饰性线条 ===== */
.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.12;
  pointer-events: none;
}

.decorative-line--1 {
  top: 0;
  right: -50px;
  width: 250px;
  height: 1px;
  transform: rotate(-20deg);
}

.decorative-line--2 {
  bottom: 200px;
  left: -80px;
  width: 300px;
  height: 1px;
  transform: rotate(-12deg);
}

/* ===== 订单标题 ===== */
.orders-header {
  margin-bottom: 48px;
  text-align: center;
}

.orders-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 56px;
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
  animation: titleReveal 0.8s ease forwards;
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

.orders-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  color: var(--warm-brown);
  letter-spacing: 4px;
  text-transform: uppercase;
  opacity: 0;
  animation: fadeIn 0.8s ease 0.5s forwards;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 60px 40px;
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

.cup-body {
  width: 80px;
  height: 70px;
  background: linear-gradient(135deg, var(--warm-brown), var(--dark-roast));
  border-radius: 0 0 30px 30px;
  margin: 0 auto;
  position: relative;
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.2);
}

.cup-body::before {
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

.cup-handle {
  position: absolute;
  right: 10px;
  top: 20px;
  width: 25px;
  height: 35px;
  border: 4px solid var(--warm-brown);
  border-left: none;
  border-radius: 0 20px 20px 0;
}

.steam {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.steam span {
  width: 4px;
  height: 20px;
  background: var(--golden);
  border-radius: 2px;
  opacity: 0.6;
  animation: steamRise 2s ease-in-out infinite;
}

.steam span:nth-child(2) {
  animation-delay: 0.3s;
  height: 15px;
}

.steam span:nth-child(3) {
  animation-delay: 0.6s;
  height: 25px;
}

.empty-title {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  color: var(--espresso);
  margin: 0 0 12px 0;
}

.empty-desc {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
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
  font-size: 16px;
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

/* ===== 订单列表 ===== */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.order-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 24px;
  padding: 28px;
  opacity: 0;
  transform: translateY(30px);
  animation: cardFadeIn 0.6s ease forwards;
  animation-delay: calc(var(--order-index) * 0.1s);
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(58, 36, 26, 0.06);
}

.order-card:hover {
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 12px 40px rgba(58, 36, 26, 0.1);
  transform: translateY(-2px);
  border-color: rgba(212, 163, 115, 0.35);
}

/* 订单头部 */
.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 18px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.2);
}

.order-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-number,
.order-time {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--warm-brown);
  opacity: 0.7;
  min-width: 70px;
}

.meta-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--espresso);
}

.order-status-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulseDot 1.5s ease-in-out infinite;
}

.status-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.status-pending {
  background: rgba(232, 184, 109, 0.15);
  color: #B8860B;
}
.status-pending .status-dot { background: #DAA520; }

.status-processing {
  background: rgba(212, 163, 115, 0.15);
  color: var(--warm-brown);
}
.status-processing .status-dot { background: var(--golden); }

.status-completed {
  background: rgba(125, 155, 122, 0.12);
  color: #5C7A5A;
}
.status-completed .status-dot { background: var(--sage); }

.status-cancelled {
  background: rgba(200, 121, 92, 0.1);
  color: var(--terracotta);
}
.status-cancelled .status-dot { background: var(--terracotta); }

/* 订单商品区域 */
.order-items-section {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px dashed rgba(212, 163, 115, 0.15);
}

.order-item:last-child {
  border-bottom: none;
}

.item-image-wrapper {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(58, 36, 26, 0.12);
  flex-shrink: 0;
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.order-item:hover .item-image {
  transform: scale(1.08);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.1), transparent);
}

.item-details {
  flex: 1;
  padding-left: 16px;
}

.item-name {
  font-family: 'Playfair Display', serif;
  font-size: 17px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0 0 8px 0;
}

.item-preferences {
  display: flex;
  gap: 8px;
}

.pref-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  background: rgba(212, 163, 115, 0.08);
  padding: 4px 10px;
  border-radius: 20px;
}

.pref-icon {
  font-size: 11px;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 20px;
}

.quantity-multiply {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  opacity: 0.6;
}

.quantity-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: var(--espresso);
}

.item-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
  min-width: 80px;
  justify-content: flex-end;
}

.price-currency {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
}

.price-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 17px;
  font-weight: 600;
  color: var(--terracotta);
}

/* 订单底部 */
.order-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20px;
  margin-top: 8px;
  border-top: 1px solid rgba(212, 163, 115, 0.2);
}

.take-code-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 18px;
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.08), rgba(232, 184, 109, 0.05));
  border-radius: 40px;
  border: 1px solid rgba(212, 163, 115, 0.15);
}

.take-code-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--warm-brown);
}

.take-code-value {
  font-family: 'Courier New', monospace;
  font-size: 22px;
  font-weight: 700;
  color: var(--terracotta);
  letter-spacing: 4px;
}

.order-total-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-left: auto;
  margin-right: 24px;
}

.total-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--warm-brown);
}

.total-amount {
  font-family: 'Playfair Display', serif;
  font-size: 26px;
  font-weight: 600;
  color: var(--terracotta);
}

.order-actions-section {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  background: transparent;
  border: 1.5px solid rgba(212, 163, 115, 0.4);
  color: var(--warm-brown);
}

.action-btn--cancel:hover {
  background: rgba(200, 121, 92, 0.1);
  border-color: var(--terracotta);
  color: var(--terracotta);
}

.action-btn .btn-icon {
  width: 16px;
  height: 16px;
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

@keyframes steamRise {
  0%, 100% {
    opacity: 0.4;
    transform: translateY(0) scaleY(1);
  }
  50% {
    opacity: 0.8;
    transform: translateY(-10px) scaleY(1.2);
  }
}

@keyframes pulseDot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .app-header {
    padding: 16px 20px;
  }

  .filter-bar-wrapper {
    padding: 12px 16px;
  }

  .filter-tab {
    padding: 10px 12px;
  }

  .filter-tab .tab-text {
    font-size: 12px;
  }

  .orders-wrapper {
    padding: 0 20px;
  }

  .orders-title {
    font-size: 40px;
  }

  .order-card {
    padding: 20px;
  }

  .order-card-footer {
    flex-wrap: wrap;
    gap: 16px;
  }

  .order-total-section {
    margin-left: 0;
  }

  .order-item {
    flex-wrap: wrap;
    gap: 12px;
  }

  .item-quantity {
    padding: 0;
  }
}

@media (max-width: 480px) {
  .filter-bar-wrapper {
    padding: 10px 12px;
  }

  .filter-tab .tab-icon {
    display: none;
  }

  .logo-main {
    font-size: 18px;
  }

  .nav-menu {
    gap: 16px;
  }
}
</style>