<template>
  <div class="cart-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义咖啡豆光标 - 修复层级问题 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 - 悬浮玻璃效果 -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">购物车</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <router-link to="/menu" class="nav-link">
              <span class="nav-text">菜单</span>
              <span class="nav-underline"></span>
            </router-link>
            <router-link v-if="isLoggedIn && userInfo.role === 1" to="/cart" class="nav-link active">
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
                <span class="nav-text">{{ userInfo.username }}</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
          </nav>
        </div>
      </header>

      <!-- 主内容区域 -->
      <main class="cart-main">
        <div class="cart-wrapper">
          <!-- 装饰性斜向分隔线 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>
          
          <!-- 标题区域 -->
          <div class="cart-header">
            <h1 class="cart-title">
              <span class="title-word title-word--1">您的</span>
              <span class="title-word title-word--2">购物篮</span>
            </h1>
            <p class="cart-subtitle">精心挑选 · 即刻享用</p>
          </div>

          <!-- 空购物车状态 -->
          <div v-if="cartItems.length === 0" class="empty-state">
            <div class="empty-illustration">
              <div class="coffee-cup-empty">
                <div class="cup-body"></div>
                <div class="cup-handle"></div>
                <div class="steam">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
            <h3 class="empty-title">购物车空空如也</h3>
            <p class="empty-desc">来探索我们的现磨咖啡系列吧</p>
            <button class="browse-menu-btn" @click="$router.push('/menu')">
              <span>浏览菜单</span>
              <svg viewBox="0 0 24 24" fill="none" class="btn-arrow">
                <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>

          <!-- 购物车内容 -->
          <div v-else class="cart-content">
            <!-- 购物车项目列表 -->
            <div class="cart-items-section">
              <div class="cart-items-list" ref="itemsListRef">
                <div 
                  v-for="(item, index) in cartItems" 
                  :key="item.id" 
                  class="cart-item"
                  :style="{ '--item-index': index }"
                >
                  <!-- 产品图片 -->
                  <div class="item-image-wrapper">
                    <img :src="item.coffeeImage" :alt="item.coffeeName" class="item-image" />
                    <div class="image-overlay"></div>
                  </div>
                  
                  <!-- 产品信息 -->
                  <div class="item-details">
                    <h4 class="item-name">{{ item.coffeeName }}</h4>
                    <span class="item-price">¥{{ item.price }}</span>
                  </div>
                  
                  <!-- 偏好选项 -->
                  <div class="item-preferences">
                    <span class="pref-tag pref-tag--sweet">
                      <span class="pref-icon">🍯</span>
                      {{ getSugarText(item.sweet) }}
                    </span>
                    <span class="pref-tag pref-tag--temp">
                      <span class="pref-icon">{{ getTempIcon(item.temperature) }}</span>
                      {{ getTempText(item.temperature) }}
                    </span>
                  </div>
                  
                  <!-- 数量控制 -->
                  <div class="item-quantity">
                    <div class="quantity-control">
                      <button 
                        class="qty-btn qty-btn--minus"
                        @click="decrementQuantity(item)"
                        :disabled="item.quantity <= 1"
                      >
                        <span>−</span>
                      </button>
                      <span class="qty-value">{{ item.quantity }}</span>
                      <button 
                        class="qty-btn qty-btn--plus"
                        @click="incrementQuantity(item)"
                        :disabled="item.quantity >= 10"
                      >
                        <span>+</span>
                      </button>
                    </div>
                  </div>
                  
                  <!-- 小计 -->
                  <div class="item-subtotal">
                    <span class="subtotal-value">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
                  </div>
                  
                  <!-- 删除操作 -->
                  <div class="item-remove">
                    <button class="remove-btn" @click="removeFromCart(item.id)">
                      <svg viewBox="0 0 24 24" fill="none" class="remove-icon">
                        <path d="M4 7H20M10 11V17M14 11V17M5 7L6 19C6 19.5304 6.21071 20.0391 6.58579 20.4142C6.96086 20.7893 7.46957 21 8 21H16C16.5304 21 17.0391 20.7893 17.4142 20.4142C17.7893 20.0391 18 19.5304 18 19L19 7M9 7V4C9 3.73478 9.10536 3.48043 9.29289 3.29289C9.48043 3.10536 9.73478 3 10 3H14C14.2652 3 14.5196 3.10536 14.7071 3.29289C14.8946 3.48043 15 3.73478 15 4V7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单摘要 -->
            <div class="cart-summary-section">
              <div class="summary-card">
                <div class="summary-header">
                  <h3 class="summary-title">订单摘要</h3>
                </div>
                
                <div class="summary-details">
                  <div class="summary-row">
                    <span class="summary-label">商品总数</span>
                    <span class="item-count">{{ cartItems.length }} 件商品</span>
                  </div>
                  <div class="summary-row summary-row--total">
                    <span class="summary-label">总计</span>
                    <span class="summary-value total-value">¥{{ totalAmount.toFixed(2) }}</span>
                  </div>
                </div>
                
                <div class="summary-actions">
                  <button class="btn-secondary" @click="$router.push('/menu')">
                    <span>继续选购</span>
                  </button>
                  <button class="btn-primary" @click="checkout">
                    <span>去结算</span>
                    <svg viewBox="0 0 24 24" fill="none" class="btn-arrow">
                      <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                </div>
                
                <p class="secure-note">
                  <span class="secure-icon">🔒</span>
                  安全结算 · 品质保证
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- 结算对话框 -->
    <el-dialog 
      v-model="showCheckoutDialog" 
      :close-on-click-modal="false"
      custom-class="checkout-dialog"
    >
      <template #header>
        <div class="dialog-header">
          <h3 class="dialog-title">确认订单</h3>
          <p class="dialog-subtitle">只差一步 · 即可享用</p>
        </div>
      </template>
      
      <div class="checkout-form">
        <div class="form-group">
          <label class="form-label">收货人</label>
          <div class="form-value">{{ userInfo.username }}</div>
        </div>
        
        <div class="form-group">
          <label class="form-label">联系电话</label>
          <div class="form-value">{{ userInfo.phone || '未填写' }}</div>
        </div>
        
        <div class="form-group">
          <label class="form-label">备注信息</label>
          <el-input 
            v-model="orderRemark" 
            type="textarea"
            :rows="3"
            placeholder="如有特殊需求请在此留言（例如：多要几张纸巾、不要吸管...）"
            class="remark-input"
          />
        </div>
        
        <div class="order-total-preview">
          <span class="preview-label">订单总额</span>
          <span class="preview-value">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button class="dialog-btn dialog-btn--cancel" @click="showCheckoutDialog = false">
            取消
          </el-button>
          <el-button class="dialog-btn dialog-btn--confirm" @click="confirmOrder">
            确认下单
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserCart, updateCartItemQuantity, deleteCartItem } from '@/api/cart'
import { createOrder } from '@/api/order'

export default {
  name: 'CartView',
  setup() {
    const router = useRouter()
    const cartItems = ref([])
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const showCheckoutDialog = ref(false)
    const orderRemark = ref('')
    
    // Refs for animations
    const headerRef = ref(null)
    const itemsListRef = ref(null)
    const cursorRef = ref(null)

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadCartItems()
      }
      initCustomCursor()
      initScrollAnimation()
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
    
    const initScrollAnimation = () => {
      const observer = new IntersectionObserver(
        (entries) => {
          entries.forEach(entry => {
            if (entry.isIntersecting) {
              entry.target.style.opacity = '1'
              entry.target.style.transform = 'translateY(0)'
            }
          })
        },
        { threshold: 0.1 }
      )
      
      // Observe items after they're rendered
      setTimeout(() => {
        document.querySelectorAll('.cart-item').forEach(item => {
          observer.observe(item)
        })
      }, 100)
    }

    const checkLoginStatus = () => {
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
      } else {
        ElMessage.warning('请先登录后查看购物车')
        router.push('/login')
      }
    }

    const loadCartItems = async () => {
      try {
        const response = await getUserCart(userInfo.value.id)
        if (response.code === 200) {
          cartItems.value = response.data
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
        ElMessage.error('加载购物车失败')
      }
    }

    const updateQuantity = async (id, quantity) => {
      try {
        const response = await updateCartItemQuantity(id, quantity)
        if (response.code === 200) {
          // 静默成功
        } else {
          ElMessage.error(response.message)
          loadCartItems()
        }
      } catch (error) {
        console.error('更新数量失败:', error)
        ElMessage.error('更新数量失败')
        loadCartItems()
      }
    }

    const decrementQuantity = (item) => {
      if (item.quantity > 1) {
        item.quantity--
        updateQuantity(item.id, item.quantity)
      }
    }

    const incrementQuantity = (item) => {
      if (item.quantity < 10) {
        item.quantity++
        updateQuantity(item.id, item.quantity)
      }
    }

    const removeFromCart = async (id) => {
      try {
        const response = await deleteCartItem(id)
        if (response.code === 200) {
          ElMessage.success('已从购物车移除')
          loadCartItems()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }

    const subtotal = computed(() => {
      return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    })
    
    const totalAmount = computed(() => subtotal.value)

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

    const checkout = () => {
      if (cartItems.value.length === 0) {
        ElMessage.warning('购物车不能为空')
        return
      }
      showCheckoutDialog.value = true
    }

    const confirmOrder = async () => {
      try {
        const cartIds = cartItems.value.map(item => item.id)
        const orderData = {
          userId: userInfo.value.id,
          remark: orderRemark.value,
          cartIds: cartIds
        }

        const response = await createOrder(orderData)
        
        if (response.code === 200) {
          ElMessage.success('订单创建成功！')
          showCheckoutDialog.value = false
          orderRemark.value = ''
          loadCartItems()
          router.push('/orders')
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        ElMessage.error('创建订单失败')
      }
    }

    return {
      cartItems,
      isLoggedIn,
      userInfo,
      showCheckoutDialog,
      orderRemark,
      totalAmount,
      subtotal,
      headerRef,
      itemsListRef,
      cursorRef,
      updateQuantity,
      decrementQuantity,
      incrementQuantity,
      removeFromCart,
      getSugarText,
      getTempText,
      getTempIcon,
      checkout,
      confirmOrder
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
}

/* ===== 全局样式与背景 ===== */
.cart-page {
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
  background: radial-gradient(circle at 80% 20%, rgba(232, 184, 109, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 10% 80%, rgba(125, 155, 122, 0.06) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* ===== 自定义光标 - 修复层级，确保始终在最前 ===== */
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
  transition: all 0.3s ease;
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

.cart-badge {
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
.cart-main {
  padding-top: 120px;
  padding-bottom: 60px;
  min-height: 100vh;
}

.cart-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
  position: relative;
}

/* ===== 装饰性线条 ===== */
.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.15;
  pointer-events: none;
}

.decorative-line--1 {
  top: 20px;
  right: -100px;
  width: 300px;
  height: 1px;
  transform: rotate(-25deg);
}

.decorative-line--2 {
  bottom: 100px;
  left: -150px;
  width: 400px;
  height: 1px;
  transform: rotate(-15deg);
}

/* ===== 购物车标题 ===== */
.cart-header {
  margin-bottom: 48px;
  text-align: center;
}

.cart-title {
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

.cart-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  color: var(--warm-brown);
  letter-spacing: 4px;
  text-transform: uppercase;
  opacity: 0;
  animation: fadeIn 0.8s ease 0.5s forwards;
}

/* ===== 空购物车状态 ===== */
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

/* ===== 购物车内容 ===== */
.cart-content {
  display: flex;
  gap: 40px;
}

.cart-items-section {
  flex: 1;
  min-width: 0;
}

.cart-items-header {
  display: grid;
  grid-template-columns: 60px 1fr 140px 100px 90px 50px;
  padding: 16px 0;
  border-bottom: 1px solid rgba(212, 163, 115, 0.3);
  margin-bottom: 16px;
}

.header-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: var(--warm-brown);
  opacity: 0.7;
}

.cart-items-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 500px;
  overflow-y: auto;
  padding-right: 8px;
}

.cart-item {
  display: grid;
  grid-template-columns: 60px 1fr 140px 100px 90px 50px;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid rgba(212, 163, 115, 0.15);
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.5s ease, transform 0.5s ease, background-color 0.3s ease;
  animation: itemFadeIn 0.5s ease forwards;
  animation-delay: calc(var(--item-index) * 0.08s);
}

.cart-item:hover {
  background: linear-gradient(90deg, rgba(253, 248, 242, 0.8), transparent);
}

.item-image-wrapper {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(58, 36, 26, 0.12);
}

.item-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.cart-item:hover .item-image {
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
  padding-left: 16px;
}

.item-name {
  font-family: 'Playfair Display', serif;
  font-size: 18px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0 0 6px 0;
}

.item-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
}

.item-preferences {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pref-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--dark-roast);
  background: rgba(212, 163, 115, 0.1);
  padding: 4px 10px;
  border-radius: 20px;
  width: fit-content;
}

.pref-icon {
  font-size: 12px;
}

.item-quantity {
  display: flex;
  justify-content: center;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--cream);
  padding: 4px;
  border-radius: 30px;
  border: 1px solid rgba(212, 163, 115, 0.2);
}

.qty-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 300;
  color: var(--dark-roast);
  cursor: pointer;
  transition: all 0.2s ease;
}

.qty-btn:hover:not(:disabled) {
  background: var(--golden);
  color: var(--crema);
}

.qty-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--espresso);
  min-width: 20px;
  text-align: center;
}

.item-subtotal {
  text-align: right;
  padding-right: 12px;
}

.subtotal-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: var(--terracotta);
}

.item-remove {
  display: flex;
  justify-content: flex-end;
}

.remove-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #B0A090;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remove-btn:hover {
  background: rgba(200, 121, 92, 0.1);
  color: var(--terracotta);
}

.remove-icon {
  width: 18px;
  height: 18px;
}

/* ===== 订单摘要卡片 ===== */
.cart-summary-section {
  width: 320px;
  flex-shrink: 0;
}

.summary-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 24px;
  padding: 28px 24px;
  position: sticky;
  top: 120px;
  box-shadow: 0 10px 40px rgba(58, 36, 26, 0.08);
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.3);
}

.summary-title {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0;
}

.item-count {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
}

.summary-details {
  margin-bottom: 28px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.summary-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--warm-brown);
}

.summary-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--espresso);
}

.summary-row--total {
  margin-top: 12px;
  padding-top: 16px;
  border-top: 2px solid rgba(212, 163, 115, 0.3);
}

.summary-row--total .summary-label {
  font-size: 18px;
  font-weight: 600;
  color: var(--espresso);
}

.total-value {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 600;
  color: var(--terracotta);
}

.summary-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.btn-secondary,
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 24px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-secondary {
  background: transparent;
  border: 1.5px solid var(--golden);
  color: var(--dark-roast);
}

.btn-secondary:hover {
  background: rgba(212, 163, 115, 0.1);
  border-color: var(--dark-roast);
}

.btn-primary {
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  color: var(--crema);
  border: none;
  box-shadow: 0 6px 20px rgba(58, 36, 26, 0.2);
}

.btn-primary:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.25);
}

.btn-primary .btn-arrow {
  width: 18px;
  transition: transform 0.3s ease;
}

.btn-primary:hover .btn-arrow {
  transform: translateX(4px);
}

.secure-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  opacity: 0.7;
}

.secure-icon {
  font-size: 12px;
}

/* ===== 结算对话框 ===== */
:deep(.checkout-dialog) {
  border-radius: 32px !important;
  overflow: hidden;
  background: var(--crema) !important;
  box-shadow: 0 30px 60px rgba(58, 36, 26, 0.15) !important;
}

:deep(.checkout-dialog .el-dialog__header) {
  padding: 32px 32px 16px;
  margin: 0;
  border-bottom: 1px solid rgba(212, 163, 115, 0.2);
}

.dialog-header {
  text-align: left;
}

.dialog-title {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0 0 6px 0;
}

.dialog-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  letter-spacing: 1px;
}

:deep(.checkout-dialog .el-dialog__body) {
  padding: 24px 32px;
}

.checkout-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: var(--warm-brown);
}

.form-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  color: var(--espresso);
  padding: 12px 16px;
  background: var(--cream);
  border-radius: 12px;
  border: 1px solid rgba(212, 163, 115, 0.2);
}

.remark-input :deep(.el-textarea__inner) {
  background: var(--cream);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 12px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--espresso);
  padding: 14px 16px;
  resize: none;
}

.remark-input :deep(.el-textarea__inner:focus) {
  border-color: var(--golden);
  box-shadow: 0 0 0 3px rgba(212, 163, 115, 0.1);
}

.order-total-preview {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-top: 8px;
  padding-top: 20px;
  border-top: 2px solid rgba(212, 163, 115, 0.3);
}

.preview-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: var(--espresso);
}

.preview-value {
  font-family: 'Playfair Display', serif;
  font-size: 32px;
  font-weight: 600;
  color: var(--terracotta);
}

:deep(.checkout-dialog .el-dialog__footer) {
  padding: 16px 32px 32px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

.dialog-btn {
  padding: 12px 28px !important;
  border-radius: 40px !important;
  font-family: 'DM Sans', sans-serif !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
}

.dialog-btn--cancel {
  background: transparent !important;
  border: 1.5px solid rgba(212, 163, 115, 0.5) !important;
  color: var(--warm-brown) !important;
}

.dialog-btn--cancel:hover {
  background: rgba(212, 163, 115, 0.1) !important;
  border-color: var(--golden) !important;
}

.dialog-btn--confirm {
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown)) !important;
  border: none !important;
  color: var(--crema) !important;
}

.dialog-btn--confirm:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast)) !important;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(58, 36, 26, 0.2) !important;
}

/* ===== 动画定义 ===== */
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
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes itemFadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes steamFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
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

@keyframes badgePulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

/* ===== 滚动条样式 ===== */
.cart-items-list::-webkit-scrollbar {
  width: 6px;
}

.cart-items-list::-webkit-scrollbar-track {
  background: rgba(212, 163, 115, 0.1);
  border-radius: 10px;
}

.cart-items-list::-webkit-scrollbar-thumb {
  background: var(--golden);
  border-radius: 10px;
}

.cart-items-list::-webkit-scrollbar-thumb:hover {
  background: var(--terracotta);
}

/* ===== 响应式适配 ===== */
@media (max-width: 900px) {
  .cart-content {
    flex-direction: column;
  }
  
  .cart-summary-section {
    width: 100%;
  }
  
  .cart-items-header {
    display: none;
  }
  
  .cart-item {
    grid-template-columns: 60px 1fr auto;
    gap: 12px;
    position: relative;
    padding: 20px 0;
  }
  
  .item-preferences {
    grid-column: 2;
    margin-top: 8px;
  }
  
  .item-quantity,
  .item-subtotal,
  .item-remove {
    grid-row: 1;
  }
}

@media (max-width: 600px) {
  .app-header {
    padding: 16px 20px;
  }
  
  .logo-main {
    font-size: 20px;
  }
  
  .nav-menu {
    gap: 16px;
  }
  
  .cart-wrapper {
    padding: 0 20px;
  }
  
  .cart-title {
    font-size: 40px;
  }
}
</style>