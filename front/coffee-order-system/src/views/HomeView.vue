<template>
  <div class="home-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 - 完全照搬 MenuView.vue 的设计 -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">首页</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <!-- 顾客功能 -->
            <template v-if="isLoggedIn && userInfo.role === 1">
              <router-link to="/menu" class="nav-link">
                <span class="nav-text">菜单</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/cart" class="nav-link">
                <span class="nav-text">购物车</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/orders" class="nav-link">
                <span class="nav-text">我的订单</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <!-- 店员功能 -->
            <template v-if="isLoggedIn && userInfo.role === 2">
              <router-link to="/staff" class="nav-link">
                <span class="nav-text">待处理订单</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/staff/all-orders" class="nav-link">
                <span class="nav-text">全部订单</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <!-- 管理员功能 -->
            <template v-if="isLoggedIn && userInfo.role === 3">
              <router-link to="/admin/coffee" class="nav-link">
                <span class="nav-text">咖啡管理</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/admin/statistics" class="nav-link">
                <span class="nav-text">数据统计</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <!-- 公有功能 -->
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
      <main class="home-main">
        <div class="home-wrapper">
          <!-- 装饰性线条 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>

          <!-- 欢迎区域 -->
          <section class="welcome-section">
            <!-- 未登录 / 顾客欢迎内容 -->
            <div v-if="!isLoggedIn || userInfo.role === 1" class="welcome-content welcome-content--customer">
              <div class="welcome-text">
                <h1 class="welcome-title">
                  <span class="title-word title-word--1">欢迎来到</span>
                  <span class="title-word title-word--2">咖啡工坊</span>
                </h1>
                <p class="welcome-subtitle">精心调制的每一杯咖啡，为您带来温暖与舒适</p>
                <div class="welcome-decoration">
                  <span class="decoration-line"></span>
                  <span class="decoration-icon">☕</span>
                  <span class="decoration-line"></span>
                </div>
                <button class="cta-button" @click="$router.push('/menu')">
                  <span>开始点单</span>
                  <svg viewBox="0 0 24 24" fill="none" class="btn-arrow">
                    <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
              <div class="welcome-illustration">
                <div class="coffee-cup-illustration">
                  <div class="cup-shadow"></div>
                  <div class="cup-body-large">
                    <div class="cup-rim"></div>
                  </div>
                  <div class="cup-handle-large"></div>
                  <div class="steam-animation">
                    <span></span><span></span><span></span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 店员欢迎内容 -->
            <div v-if="isLoggedIn && userInfo.role === 2" class="welcome-content welcome-content--staff">
              <div class="welcome-text">
                <h1 class="welcome-title">
                  <span class="title-word title-word--1">店员</span>
                  <span class="title-word title-word--2">工作台</span>
                </h1>
                <p class="welcome-subtitle">管理订单 · 服务顾客 · 提升效率</p>
                <div class="welcome-decoration">
                  <span class="decoration-line"></span>
                  <span class="decoration-icon">🛎️</span>
                  <span class="decoration-line"></span>
                </div>
              </div>
            </div>

            <!-- 管理员欢迎内容 -->
            <div v-if="isLoggedIn && userInfo.role === 3" class="welcome-content welcome-content--admin">
              <div class="welcome-text">
                <h1 class="welcome-title">
                  <span class="title-word title-word--1">管理员</span>
                  <span class="title-word title-word--2">控制台</span>
                </h1>
                <p class="welcome-subtitle">管理商品 · 查看数据 · 优化运营</p>
                <div class="welcome-decoration">
                  <span class="decoration-line"></span>
                  <span class="decoration-icon">📊</span>
                  <span class="decoration-line"></span>
                </div>
                <div class="admin-actions">
                  <button class="action-btn action-btn--primary" @click="$router.push('/admin/coffee')">
                    <span>咖啡管理</span>
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M12 4V20M20 12H4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                  <button class="action-btn action-btn--secondary" @click="$router.push('/admin/statistics')">
                    <span>数据统计</span>
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M3 17V20M9 11V20M15 7V20M21 3V20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </section>

          <!-- 顾客推荐咖啡展示 -->
          <section v-if="!isLoggedIn || userInfo.role === 1" class="featured-section">
            <div class="section-header">
              <h2 class="section-title">
                <span class="title-icon">✨</span>
                <span class="title-accent">本周推荐</span>
              </h2>
              <p class="section-subtitle">精选咖啡 · 不容错过</p>
            </div>

            <div class="featured-carousel-container">
              <div class="carousel-wrapper">
                <el-carousel 
                  height="400px"
                  :autoplay="true"
                  :interval="3000" 
                  indicator-position="outside"
                  @change="handleCarouselChange"
                  ref="carouselRef"
                >
                  <el-carousel-item v-for="coffee in limitedRecommendedCoffees" :key="coffee.id">
                    <div class="carousel-item-content" @click="$router.push(`/menu#coffee-${coffee.id}`)">
                      <img :src="coffee.coffeeImage" class="carousel-image" :alt="coffee.name" />
                      <div class="carousel-overlay">
                        <span class="overlay-text">点击查看详情</span>
                      </div>
                    </div>
                  </el-carousel-item>
                </el-carousel>
              </div>
              
              <div class="featured-info-card" v-if="currentCoffee">
                <div class="info-card-content">
                  <h3 class="featured-name">{{ currentCoffee.name }}</h3>
                  <span class="featured-price">¥{{ currentCoffee.price }}</span>
                  <p class="featured-description">{{ currentCoffee.description }}</p>
                  <button class="featured-action" @click="$router.push(`/menu#coffee-${currentCoffee.id}`)">
                    <span>查看详情</span>
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                </div>
                <div class="info-card-decoration"></div>
              </div>
            </div>
          </section>

          <!-- 店员仪表盘 -->
          <section v-if="isLoggedIn && userInfo.role === 2" class="dashboard-section">
            <div class="section-header">
              <h2 class="section-title">
                <span class="title-icon">📈</span>
                <span class="title-accent">销售概览</span>
              </h2>
              <p class="section-subtitle">今日数据 · 一目了然</p>
            </div>

            <div class="stats-grid">
              <div class="stat-card stat-card--primary">
                <div class="stat-icon">💰</div>
                <div class="stat-content">
                  <span class="stat-value">¥{{ todaySales.toFixed(2) }}</span>
                  <span class="stat-label">今日销售额</span>
                </div>
              </div>
              
              <div class="stat-card stat-card--secondary">
                <div class="stat-icon">📊</div>
                <div class="stat-content">
                  <span class="stat-value">¥{{ totalSales.toFixed(2) }}</span>
                  <span class="stat-label">历史总销售额</span>
                </div>
              </div>
              
              <div class="stat-card stat-card--accent">
                <div class="stat-icon">✅</div>
                <div class="stat-content">
                  <span class="stat-value">{{ completedOrdersCount }}</span>
                  <span class="stat-label">今日已完成</span>
                </div>
              </div>
            </div>

            <div class="section-header section-header--small">
              <h3 class="section-title">
                <span class="title-icon">📋</span>
                <span class="title-accent">订单状态</span>
              </h3>
            </div>

            <div class="stats-grid stats-grid--two">
              <div class="stat-card stat-card--warning">
                <div class="stat-icon">⏳</div>
                <div class="stat-content">
                  <span class="stat-value">{{ pendingOrdersCount }}</span>
                  <span class="stat-label">待接单</span>
                </div>
              </div>
              
              <div class="stat-card stat-card--info">
                <div class="stat-icon">🔥</div>
                <div class="stat-content">
                  <span class="stat-value">{{ processingOrdersCount }}</span>
                  <span class="stat-label">制作中</span>
                </div>
              </div>
            </div>

            <div class="quick-actions">
              <button class="action-btn action-btn--primary" @click="$router.push('/staff')">
                <span>处理待接单</span>
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </button>
              <button class="action-btn action-btn--secondary" @click="$router.push('/staff/all-orders')">
                <span>查看全部订单</span>
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M3 6H21M3 12H21M3 18H21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </section>

          <!-- 管理员热销咖啡展示 -->
          <section v-if="isLoggedIn && userInfo.role === 3" class="top-selling-section">
            <div class="section-header">
              <h2 class="section-title">
                <span class="title-icon">🔥</span>
                <span class="title-accent">热销排行</span>
              </h2>
              <p class="section-subtitle">过去七天 · 销量前五</p>
            </div>

            <div class="top-selling-grid">
              <div 
                v-for="(coffee, index) in topSellingCoffees" 
                :key="coffee.id" 
                class="top-selling-card"
                :style="{ '--rank': index + 1 }"
              >
                <div class="rank-badge">{{ index + 1 }}</div>
                <div class="card-image-wrapper">
                  <img :src="coffee.coffeeImage" :alt="coffee.name" class="card-image" />
                </div>
                <div class="card-info">
                  <h4 class="coffee-name">{{ coffee.name }}</h4>
                  <span class="coffee-price">¥{{ coffee.price }}</span>
                  <div class="sales-info">
                    <span class="sales-icon">📦</span>
                    <span class="sales-count">{{ coffee.sales }} 杯</span>
                  </div>
                </div>
                <button 
                  class="recommend-btn"
                  :class="{ 'is-recommended': coffee.recommend === '1' }"
                  :disabled="coffee.recommend === '1'"
                  @click="toggleRecommend(coffee)"
                >
                  <span v-if="coffee.recommend === '1'">已推荐</span>
                  <span v-else>设为推荐</span>
                </button>
              </div>
            </div>

            <div v-if="topSellingCoffees.length === 0" class="empty-state">
              <span class="empty-icon">☕</span>
              <p>暂无销售数据</p>
            </div>
          </section>
        </div>
      </main>

      <!-- 底部 -->
      <footer class="app-footer">
        <div class="footer-content">
          <div class="footer-brand">
            <span class="footer-logo">☕</span>
            <span class="footer-name">咖啡工坊</span>
          </div>
          <p class="footer-copyright">© 2026 咖啡点单系统 · 版权所有</p>
          <p class="footer-tagline">每一杯咖啡 · 都是独特的故事</p>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed } from 'vue'
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
    const cursorRef = ref(null)
    const headerRef = ref(null)

    const limitedRecommendedCoffees = computed(() => {
      return recommendedCoffees.value.slice(0, 8)
    })

    const topSellingCoffees = ref([])
    const pendingOrdersCount = ref(0)
    const processingOrdersCount = ref(0)
    const completedOrdersCount = ref(0)
    const todaySales = ref(0)
    const totalSales = ref(0)

    onMounted(() => {
      loadRecommendedCoffees()
      checkLoginStatus()
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

    const loadRecommendedCoffees = async () => {
      try {
        const response = await getRecommendedCoffee()
        if (response.code === 200) {
          recommendedCoffees.value = response.data
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

    const loadTopSellingCoffees = async () => {
      try {
        const response = await getTopSellingLastWeek(5)
        if (response.code === 200) {
          topSellingCoffees.value = (response.data || []).map(item => ({
            id: item.id,
            name: item.name,
            price: item.price,
            coffeeImage: item.coffee_image || item.coffeeImage,
            description: item.description,
            category: item.category,
            stock: item.stock,
            status: item.status,
            recommend: item.recommend,
            sales: item.lastWeekSales || item.sales || 0,
          }))
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载热销咖啡失败:', error)
        ElMessage.error('加载热销咖啡失败')
      }
    }

    const toggleRecommend = async (coffee) => {
      try {
        const response = await setRecommend(coffee.id, '1')
        if (response.code === 200) {
          ElMessage.success('推荐成功')
          coffee.recommend = '1'
          loadTopSellingCoffees()
        } else if (response.code === 500 && response.message.includes('推荐的咖啡数量已达上限')) {
          ElMessage.error('推荐的咖啡数量已达上限（8个），无法再推荐更多咖啡')
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('设置推荐失败:', error)
        ElMessage.error('设置推荐失败')
      }
    }

    const loadOrderStats = async () => {
      if (isLoggedIn.value && userInfo.value.role === 2) {
        try {
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
            const today = new Date()
            today.setHours(0, 0, 0, 0)
            
            const todayCompletedOrders = completedOrders.filter(order => new Date(order.orderTime) >= today)
            completedOrdersCount.value = todayCompletedOrders.length
            
            todaySales.value = todayCompletedOrders
              .reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
              
            totalSales.value = completedOrders
              .reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
          }
          
          const allOrdersResponse = await getAllOrdersByStatus(0)
          if (allOrdersResponse.code === 200) {
            const allOrders = allOrdersResponse.data || []
            totalSales.value = allOrders
              .filter(order => order.status === 3)
              .reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0)
          }
        } catch (error) {
          console.error('加载订单统计失败:', error)
        }
      }
    }

    const handleCarouselChange = (index) => {
      currentCoffee.value = limitedRecommendedCoffees.value[index]
    }

    const checkLoginStatus = () => {
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        if (userInfo.value.role === 3) {
          loadTopSellingCoffees()
        } else if (userInfo.value.role === 2) {
          setTimeout(loadOrderStats, 100)
        }
      }
    }

    return {
      recommendedCoffees,
      limitedRecommendedCoffees,
      isLoggedIn,
      userInfo,
      currentCoffee,
      carouselRef,
      cursorRef,
      headerRef,
      handleCarouselChange,
      topSellingCoffees,
      toggleRecommend,
      pendingOrdersCount,
      processingOrdersCount,
      completedOrdersCount,
      todaySales,
      totalSales
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.home-page {
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
  display: flex;
  flex-direction: column;
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

.nav-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--golden), var(--terracotta));
  transition: width 0.3s ease;
}

.nav-link:hover .nav-underline {
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
.home-main {
  flex: 1;
  padding-top: 84px;
  padding-bottom: 40px;
}

.home-wrapper {
  max-width: 1200px;
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

/* ===== 欢迎区域 ===== */
.welcome-section {
  margin-bottom: 60px;
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 60px;
  min-height: 400px;
}

.welcome-content--customer {
  justify-content: space-between;
}

.welcome-content--staff,
.welcome-content--admin {
  justify-content: center;
  text-align: center;
}

.welcome-text {
  flex: 1;
  animation: fadeInUp 0.8s ease forwards;
}

.welcome-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 52px;
  font-weight: 400;
  line-height: 1.2;
  margin: 0 0 16px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
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

.welcome-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 18px;
  color: var(--warm-brown);
  margin-bottom: 32px;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.5s forwards;
}

.welcome-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 32px;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.7s forwards;
}

.decoration-line {
  width: 60px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
}

.decoration-icon {
  font-size: 24px;
}

.cta-button {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 40px;
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  border: none;
  border-radius: 50px;
  color: var(--crema);
  font-family: 'DM Sans', sans-serif;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 24px rgba(58, 36, 26, 0.2);
  opacity: 0;
  animation: fadeIn 0.6s ease 0.9s forwards;
}

.cta-button:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(58, 36, 26, 0.25);
}

.cta-button .btn-arrow {
  width: 20px;
  transition: transform 0.3s ease;
}

.cta-button:hover .btn-arrow {
  transform: translateX(4px);
}

.welcome-illustration {
  flex: 1;
  display: flex;
  justify-content: center;
  opacity: 0;
  animation: fadeIn 0.8s ease 0.4s forwards;
}

.coffee-cup-illustration {
  position: relative;
  width: 200px;
  height: 220px;
}

.cup-shadow {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 20px;
  background: radial-gradient(ellipse, rgba(58, 36, 26, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.cup-body-large {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  width: 140px;
  height: 120px;
  background: linear-gradient(135deg, var(--warm-brown), var(--dark-roast));
  border-radius: 0 0 50px 50px;
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.2);
}

.cup-rim {
  position: absolute;
  top: -20px;
  left: -10px;
  right: -10px;
  height: 30px;
  background: var(--crema);
  border-radius: 50%;
  border: 3px solid var(--golden);
}

.cup-handle-large {
  position: absolute;
  right: -20px;
  top: 40px;
  width: 40px;
  height: 60px;
  border: 6px solid var(--warm-brown);
  border-left: none;
  border-radius: 0 30px 30px 0;
}

.steam-animation {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.steam-animation span {
  width: 6px;
  height: 30px;
  background: var(--golden);
  border-radius: 3px;
  opacity: 0.5;
  animation: steamRise 3s ease-in-out infinite;
}

.steam-animation span:nth-child(2) {
  animation-delay: 0.4s;
  height: 25px;
}

.steam-animation span:nth-child(3) {
  animation-delay: 0.8s;
  height: 35px;
}

/* 管理员操作按钮 */
.admin-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 24px;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.9s forwards;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.action-btn svg {
  width: 18px;
  height: 18px;
}

.action-btn--primary {
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  color: var(--crema);
  box-shadow: 0 6px 20px rgba(58, 36, 26, 0.2);
}

.action-btn--primary:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.25);
}

.action-btn--secondary {
  background: transparent;
  border: 1.5px solid var(--golden);
  color: var(--dark-roast);
}

.action-btn--secondary:hover {
  background: rgba(212, 163, 115, 0.1);
  border-color: var(--dark-roast);
}

/* ===== 区块标题 ===== */
.section-header {
  margin-bottom: 32px;
  text-align: center;
}

.section-header--small {
  margin-top: 40px;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-family: 'Playfair Display', serif;
  font-size: 32px;
  font-weight: 400;
  color: var(--espresso);
  margin: 0 0 8px 0;
}

.title-icon {
  font-size: 28px;
}

.title-accent {
  position: relative;
}

.section-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  letter-spacing: 2px;
  text-transform: uppercase;
  margin: 0;
}

/* ===== 推荐轮播区域 ===== */
.featured-section {
  margin-bottom: 40px;
}

.featured-carousel-container {
  display: flex;
  gap: 40px;
  align-items: stretch;
}

.carousel-wrapper {
  flex: 1;
  max-width: 500px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.1);
}

.carousel-item-content {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.carousel-item-content:hover .carousel-image {
  transform: scale(1.05);
}

.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to top, rgba(30, 27, 26, 0.7), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.carousel-item-content:hover .carousel-overlay {
  opacity: 1;
}

.overlay-text {
  color: var(--crema);
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
}

.featured-info-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 24px;
  padding: 32px;
  position: relative;
  overflow: hidden;
}

.info-card-content {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.featured-name {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 600;
  color: var(--espresso);
  margin: 0 0 8px 0;
}

.featured-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 24px;
  font-weight: 600;
  color: var(--terracotta);
  margin-bottom: 16px;
}

.featured-description {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--warm-brown);
  line-height: 1.6;
  margin: 0 0 24px 0;
  flex: 1;
}

.featured-action {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: transparent;
  border: 1.5px solid var(--golden);
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--dark-roast);
  cursor: pointer;
  transition: all 0.3s ease;
  width: fit-content;
}

.featured-action:hover {
  background: var(--dark-roast);
  border-color: var(--dark-roast);
  color: var(--crema);
}

.featured-action svg {
  width: 18px;
}

.info-card-decoration {
  position: absolute;
  bottom: -50px;
  right: -50px;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(212, 163, 115, 0.08) 0%, transparent 70%);
  border-radius: 50%;
  z-index: 1;
}

/* ===== 店员仪表盘 ===== */
.dashboard-section {
  margin-bottom: 40px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.stats-grid--two {
  grid-template-columns: repeat(2, 1fr);
}

.stat-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
  padding: 28px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(58, 36, 26, 0.1);
}

.stat-icon {
  font-size: 40px;
  opacity: 0.8;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-family: 'Playfair Display', serif;
  font-size: 36px;
  font-weight: 600;
  color: var(--espresso);
  line-height: 1.2;
}

.stat-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  opacity: 0.8;
}

.stat-card--primary .stat-value {
  color: var(--terracotta);
}

.stat-card--secondary .stat-value {
  color: var(--golden);
}

.stat-card--accent .stat-value {
  color: var(--sage);
}

.stat-card--warning .stat-value {
  color: #E8B86D;
}

.stat-card--info .stat-value {
  color: #7D9B7A;
}

.quick-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 32px;
}

/* ===== 热销排行区域 ===== */
.top-selling-section {
  margin-bottom: 40px;
}

.top-selling-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.top-selling-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
  padding: 24px 20px;
  position: relative;
  text-align: center;
  transition: all 0.3s ease;
  opacity: 0;
  animation: cardFadeIn 0.5s ease forwards;
  animation-delay: calc(var(--rank) * 0.08s);
}

.top-selling-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(58, 36, 26, 0.1);
}

.rank-badge {
  position: absolute;
  top: -10px;
  left: -10px;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, var(--golden), var(--terracotta));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Playfair Display', serif;
  font-size: 18px;
  font-weight: 700;
  color: var(--crema);
  box-shadow: 0 4px 12px rgba(200, 121, 92, 0.3);
}

.card-image-wrapper {
  width: 120px;
  height: 120px;
  margin: 0 auto 16px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 6px 16px rgba(58, 36, 26, 0.12);
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.top-selling-card:hover .card-image {
  transform: scale(1.08);
}

.coffee-name {
  font-family: 'Playfair Display', serif;
  font-size: 18px;
  font-weight: 600;
  color: var(--espresso);
  margin: 0 0 4px 0;
}

.coffee-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 18px;
  font-weight: 600;
  color: var(--terracotta);
  display: block;
  margin-bottom: 12px;
}

.sales-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 16px;
}

.sales-icon {
  font-size: 14px;
}

.sales-count {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
}

.recommend-btn {
  width: 100%;
  padding: 10px 16px;
  background: transparent;
  border: 1.5px solid var(--golden);
  border-radius: 30px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  color: var(--dark-roast);
  cursor: pointer;
  transition: all 0.3s ease;
}

.recommend-btn:hover:not(:disabled) {
  background: var(--golden);
  color: var(--crema);
}

.recommend-btn.is-recommended {
  background: rgba(125, 155, 122, 0.1);
  border-color: var(--sage);
  color: var(--sage);
  cursor: default;
}

.recommend-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 60px 40px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 24px;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.4;
  display: block;
  margin-bottom: 16px;
}

.empty-state p {
  font-family: 'DM Sans', sans-serif;
  color: var(--warm-brown);
}

/* ===== 底部 ===== */
.app-footer {
  padding: 32px 40px;
  background: rgba(253, 248, 242, 0.5);
  border-top: 1px solid rgba(212, 163, 115, 0.15);
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}

.footer-logo {
  font-size: 24px;
}

.footer-name {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  color: var(--dark-roast);
}

.footer-copyright {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 0 0 4px 0;
}

.footer-tagline {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  opacity: 0.5;
  letter-spacing: 2px;
  margin: 0;
}

/* ===== 轮播图样式覆盖 ===== */
:deep(.el-carousel__indicator) {
  padding: 6px 4px;
}

:deep(.el-carousel__button) {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgba(212, 163, 115, 0.4);
  transition: all 0.3s ease;
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  width: 24px;
  border-radius: 4px;
  background-color: var(--terracotta);
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

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
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
    transform: translateY(-15px) scaleY(1.2);
  }
}

@keyframes liquidWave {
  0%, 100% {
    border-radius: 40% 40% 50% 50% / 40% 40% 50% 50%;
  }
  50% {
    border-radius: 45% 45% 40% 40% / 45% 45% 40% 40%;
  }
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .welcome-content--customer {
    flex-direction: column;
    text-align: center;
  }
  
  .featured-carousel-container {
    flex-direction: column;
  }
  
  .carousel-wrapper {
    max-width: 100%;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid--two {
    grid-template-columns: 1fr;
  }
  
  .top-selling-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 16px 20px;
  }
  
  .home-wrapper {
    padding: 0 20px;
  }
  
  .welcome-title {
    font-size: 36px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .top-selling-grid {
    grid-template-columns: 1fr;
  }
  
  .admin-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .quick-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .action-btn {
    width: 100%;
    max-width: 280px;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .logo-main {
    font-size: 18px;
  }
  
  .nav-menu {
    gap: 16px;
  }
  
  .welcome-title {
    font-size: 28px;
  }
  
  .coffee-cup-illustration {
    transform: scale(0.8);
  }
}
</style>