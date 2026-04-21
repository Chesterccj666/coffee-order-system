<template>
  <div class="menu-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">菜单</span>
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

      <!-- 主内容区域 -->
      <main class="menu-main">
        <div class="menu-layout">
          <!-- 左侧分类导航 -->
          <aside class="category-sidebar">
            <div class="sidebar-inner">
              <!-- 搜索框 -->
              <div class="search-wrapper">
                <div class="search-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M21 21L15 15M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </div>
                <input 
                  type="text"
                  v-model="searchKeyword" 
                  placeholder="搜索咖啡..."
                  class="search-input"
                  @input="onSearchChange"
                />
                <button 
                  v-if="searchKeyword" 
                  class="clear-search"
                  @click="clearSearch"
                >
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>

              <!-- 分类菜单 -->
              <nav class="category-nav">
                <div 
                  v-for="category in categories" 
                  :key="category"
                  class="category-item"
                  :class="{ active: activeCategory === category }"
                  @click="onCategoryChange(category)"
                  :data-category="category"
                >
                  <span class="category-icon">{{ getCategoryIcon(category) }}</span>
                  <span class="category-name">{{ category }}</span>
                  <span class="category-count">{{ getCategoryCount(category) }}</span>
                  <span class="category-indicator"></span>
                </div>
              </nav>

              <!-- 装饰性引用 -->
              <div class="sidebar-quote">
                <p class="quote-text">"咖啡是液体阳光"</p>
                <p class="quote-author">— 意大利谚语</p>
              </div>
            </div>
          </aside>

          <!-- 右侧咖啡列表 -->
          <div class="coffee-content">
            <div class="content-inner" ref="contentInnerRef">
              <!-- 装饰性元素 -->
              <div class="decorative-line decorative-line--1"></div>
              <div class="decorative-line decorative-line--2"></div>

              <!-- 搜索结果模式 -->
              <div v-if="searchKeyword" class="search-results-section">
                <div class="section-header">
                  <h2 class="section-title">
                    <span class="title-accent">搜索结果</span>
                  </h2>
                  <p class="section-subtitle">找到 {{ getCoffeesBySearch().length }} 款咖啡</p>
                </div>

                <div v-if="getCoffeesBySearch().length === 0" class="no-results">
                  <div class="no-results-illustration">
                    <span class="illustration-icon">🔍</span>
                  </div>
                  <h3 class="no-results-title">未找到匹配的咖啡</h3>
                  <p class="no-results-desc">试试其他关键词吧</p>
                  <button class="clear-search-btn" @click="clearSearch">清除搜索</button>
                </div>

                <div v-else class="coffee-grid">
                  <div 
                    v-for="(coffee, index) in getCoffeesBySearch()" 
                    :key="coffee.id"
                    class="coffee-card-wrapper"
                    :style="{ '--card-index': index }"
                  >
                    <CoffeeCard 
                      :coffee="coffee"
                      :selected-options="selectedOptions[coffee.id]"
                      :is-logged-in="isLoggedIn"
                      @add-to-cart="addToCart"
                      @login-required="promptLogin"
                    />
                  </div>
                </div>
              </div>

              <!-- 分类展示模式 -->
              <div v-else>
                <div 
                  v-for="category in categories" 
                  :key="category"
                  class="category-section"
                  :id="'category-' + category"
                >
                  <div v-if="getCoffeesByCategory(category).length > 0">
                    <div class="section-header">
                      <h2 class="section-title">
                        <span class="title-icon">{{ getCategoryIcon(category) }}</span>
                        <span class="title-accent">{{ category }}</span>
                      </h2>
                      <p class="section-subtitle">{{ getCategoryDescription(category) }}</p>
                    </div>

                    <div class="coffee-grid">
                      <div 
                        v-for="(coffee, index) in getCoffeesByCategory(category)" 
                        :key="coffee.id"
                        class="coffee-card-wrapper"
                        :style="{ '--card-index': index }"
                      >
                        <CoffeeCard 
                          :coffee="coffee"
                          :selected-options="selectedOptions[coffee.id]"
                          :is-logged-in="isLoggedIn"
                          @add-to-cart="addToCart"
                          @login-required="promptLogin"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- 迷你购物车 -->
    <MiniCart 
      v-if="isLoggedIn && userInfo.role === 1"
      :items="miniCartItems"
      :total="miniCartTotal"
      :item-count="miniCartItemCount"
      @remove="removeCartItem"
      @update-quantity="updateCartItemQuantity"
      @checkout="goToCart"
      ref="miniCartRef"
    />
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCoffee } from '@/api/coffee'
import { addToCart as addCart, getUserCart, deleteCartItem, updateCartItemQuantity as updateQuantityApi } from '@/api/cart'
import CoffeeCard from '@/components/CoffeeCard.vue'
import MiniCart from '@/components/MiniCart.vue'

export default {
  name: 'MenuView',
  components: {
    CoffeeCard,
    MiniCart
  },
  setup() {
    const router = useRouter()
    const coffees = ref([])
    const activeCategory = ref('经典意式')
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const selectedOptions = ref({})
    const searchKeyword = ref('')
    const miniCartItems = ref([])
    const miniCartTotal = ref(0)
    const miniCartItemCount = ref(0)
    
    const cursorRef = ref(null)
    const headerRef = ref(null)
    const miniCartRef = ref(null)
    const contentInnerRef = ref(null)

    const categories = ['经典意式', '风味拿铁', '风味美式', '奶咖', '燕麦系列', '单品豆SOE', '其他']

    onMounted(() => {
      loadCoffees()
      checkLoginStatus()
      loadMiniCart()
      initCustomCursor()
      initScrollObserver()
    })

    onUnmounted(() => {
      window.removeEventListener('mousemove', handleMouseMove)
      if (scrollObserver) {
        scrollObserver.disconnect()
      }
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

    // 滚动监听和联动功能
    let scrollObserver = null

    const initScrollObserver = () => {
      nextTick(() => {
        const categorySections = document.querySelectorAll('.category-section')
        
        if (categorySections.length === 0) return

        // 创建 Intersection Observer 来监听分类区域的可见性
        scrollObserver = new IntersectionObserver(
          (entries) => {
            entries.forEach((entry) => {
              if (entry.isIntersecting) {
                // 获取当前可见的分类名称
                const categoryId = entry.target.id
                const categoryName = categoryId.replace('category-', '')
                
                // 更新左侧导航的激活状态
                activeCategory.value = categoryName
                
                // 滚动左侧导航到对应的分类项
                scrollCategoryToActive(categoryName)
              }
            })
          },
          {
            rootMargin: '-20% 0px -60% 0px', // 调整触发区域
            threshold: 0.1
          }
        )

        // 监听所有分类区域
        categorySections.forEach((section) => {
          scrollObserver.observe(section)
        })
      })
    }

    const scrollCategoryToActive = (categoryName) => {
      const categoryItem = document.querySelector(`[data-category="${categoryName}"]`)
      if (categoryItem) {
        const sidebar = document.querySelector('.category-sidebar')
        const sidebarInner = document.querySelector('.sidebar-inner')
        
        if (sidebar && sidebarInner && categoryItem) {
          const itemRect = categoryItem.getBoundingClientRect()
          const sidebarRect = sidebar.getBoundingClientRect()
          const sidebarInnerRect = sidebarInner.getBoundingClientRect()
          
          // 计算滚动位置
          const itemOffset = categoryItem.offsetTop
          const sidebarHeight = sidebarRect.height
          const itemHeight = itemRect.height
          
          // 确保分类项在可视区域内
          const targetScroll = itemOffset - (sidebarHeight / 2) + (itemHeight / 2)
          
          sidebar.scrollTo({
            top: targetScroll,
            behavior: 'smooth'
          })
        }
      }
    }

    const checkLoginStatus = () => {
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
      }
    }

    const loadCoffees = async () => {
      try {
        const response = await getAllCoffee()
        if (response.code === 200) {
          coffees.value = response.data
          coffees.value.forEach(coffee => {
            if (!selectedOptions.value[coffee.id]) {
              selectedOptions.value[coffee.id] = {
                quantity: 1,
                sweet: 1,
                temperature: 1
              }
            }
          })
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载咖啡列表失败:', error)
        ElMessage.error('加载咖啡列表失败')
      }
    }

    const getCategoryIcon = (category) => {
      const icons = {
        '经典意式': '☕',
        '风味拿铁': '🥛',
        '风味美式': '💧',
        '奶咖': '🐄',
        '燕麦系列': '🌾',
        '单品豆SOE': '🫘',
        '其他': '✨'
      }
      return icons[category] || '☕'
    }

    const getCategoryDescription = (category) => {
      const descriptions = {
        '经典意式': '纯粹的咖啡本味 · 意式经典',
        '风味拿铁': '丝滑奶泡 · 风味交融',
        '风味美式': '清爽纯净 · 果香四溢',
        '奶咖': '浓郁奶香 · 温柔治愈',
        '燕麦系列': '植物基选择 · 轻盈健康',
        '单品豆SOE': '单一产地 · 风味独特',
        '其他': '更多惊喜 · 等你发现'
      }
      return descriptions[category] || ''
    }

    const getCategoryCount = (category) => {
      return coffees.value.filter(c => c.category === category).length
    }

    const getCoffeesByCategory = (category) => {
      return coffees.value.filter(coffee => coffee.category === category)
    }

    const getCoffeesBySearch = () => {
      if (!searchKeyword.value) return []
      const keyword = searchKeyword.value.toLowerCase()
      return coffees.value.filter(coffee => 
        coffee.name.toLowerCase().includes(keyword) ||
        (coffee.description && coffee.description.toLowerCase().includes(keyword))
      )
    }

    const onCategoryChange = (category) => {
      activeCategory.value = category
      searchKeyword.value = ''
      setTimeout(() => {
        const element = document.getElementById('category-' + category)
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      }, 100)
    }

    const onSearchChange = () => {
      activeCategory.value = ''
    }

    const clearSearch = () => {
      searchKeyword.value = ''
      activeCategory.value = categories[0]
      setTimeout(() => {
        const element = document.getElementById('category-' + categories[0])
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      }, 100)
    }

    const promptLogin = () => {
      ElMessageBox.confirm('您还未登录，是否前往登录？', '提示', {
        confirmButtonText: '前往登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        router.push('/login')
      }).catch(() => {})
    }

    const addToCart = async ({ coffee, options }) => {
      if (!isLoggedIn.value) {
        promptLogin()
        return
      }

      try {
        const cartItem = {
          userId: userInfo.value.id,
          coffeeId: coffee.id,
          coffeeName: coffee.name,
          coffeeImage: coffee.coffeeImage,
          price: coffee.price,
          quantity: options.quantity,
          sweet: options.sweet,
          temperature: options.temperature
        }

        const response = await addCart(cartItem)
        
        if (response.code === 200) {
          ElMessage.success(`「${coffee.name}」已加入购物车`)
          await loadMiniCart()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('添加到购物车失败:', error)
        ElMessage.error('添加到购物车失败')
      }
    }

    const loadMiniCart = async () => {
      if (!isLoggedIn.value || !userInfo.value.id) return

      try {
        const response = await getUserCart(userInfo.value.id)
        if (response.code === 200) {
          miniCartItems.value = response.data
          miniCartItemCount.value = response.data.reduce((sum, item) => sum + item.quantity, 0)
          miniCartTotal.value = response.data.reduce((sum, item) => sum + (item.price * item.quantity), 0)
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
      }
    }

    const removeCartItem = async (itemId) => {
      try {
        const response = await deleteCartItem(itemId)
        if (response.code === 200) {
          ElMessage.success('已从购物车移除')
          await loadMiniCart()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('移除失败:', error)
        ElMessage.error('移除失败')
      }
    }

    const updateCartItemQuantity = async (data) => {
      try {
        const { itemId, quantity } = data
        const response = await updateQuantityApi(itemId, quantity)
        if (response.code === 200) {
          // 成功后重新加载迷你购物车数据
          await loadMiniCart()
        } else {
          ElMessage.error(response.message)
          await loadMiniCart()
        }
      } catch (error) {
        console.error('更新购物车数量失败:', error)
        ElMessage.error('更新失败')
        await loadMiniCart()
      }
    }

    const goToCart = () => {
      router.push('/cart')
    }

    return {
      coffees,
      categories,
      activeCategory,
      isLoggedIn,
      userInfo,
      selectedOptions,
      searchKeyword,
      miniCartItems,
      miniCartTotal,
      miniCartItemCount,
      cursorRef,
      headerRef,
      miniCartRef,
      getCategoryIcon,
      getCategoryDescription,
      getCategoryCount,
      getCoffeesByCategory,
      getCoffeesBySearch,
      onCategoryChange,
      onSearchChange,
      clearSearch,
      promptLogin,
      addToCart,
      loadMiniCart,
      removeCartItem,
      updateCartItemQuantity,
      goToCart
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.menu-page {
  --espresso: #1E1B1A;
  --dark-roast: #3A241A;
  --warm-brown: #5C3A2A;
  --crema: #FDF8F2;
  --cream: #FFF5E6;
  --golden: #D4A373;
  --terracotta: #C8795C;
  
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

/* ===== 主内容区域 ===== */
.menu-main {
  padding-top: 84px;
  min-height: 100vh;
}

.menu-layout {
  display: flex;
  max-width: 1600px;
  margin: 0 auto;
}

/* ===== 左侧边栏 ===== */
.category-sidebar {
  width: 280px;
  flex-shrink: 0;
  position: fixed;
  top: 84px;
  left: 0;
  height: calc(100vh - 84px);
  overflow-y: auto;
  padding: 24px 16px;
  border-right: 1px solid rgba(212, 163, 115, 0.15);
  background: rgba(253, 248, 242, 0.85);
  backdrop-filter: blur(8px);
  z-index: 90;
}

.sidebar-inner {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* 搜索框 */
.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(212, 163, 115, 0.25);
  border-radius: 40px;
  padding: 4px 16px;
  transition: all 0.3s ease;
}

.search-wrapper:focus-within {
  border-color: var(--golden);
  box-shadow: 0 0 0 3px rgba(212, 163, 115, 0.1);
  background: rgba(255, 255, 255, 0.8);
}

.search-icon {
  width: 20px;
  height: 20px;
  color: var(--warm-brown);
  opacity: 0.6;
  margin-right: 8px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 12px 0;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--espresso);
  outline: none;
}

.search-input::placeholder {
  color: var(--warm-brown);
  opacity: 0.5;
}

.clear-search {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--warm-brown);
  opacity: 0.5;
  cursor: pointer;
  transition: all 0.2s ease;
}

.clear-search:hover {
  opacity: 1;
  background: rgba(212, 163, 115, 0.1);
}

/* 分类导航 */
.category-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-item:hover {
  background: rgba(212, 163, 115, 0.08);
}

.category-item.active {
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.12), rgba(200, 121, 92, 0.06));
}

.category-icon {
  font-size: 20px;
  margin-right: 12px;
}

.category-name {
  flex: 1;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--dark-roast);
}

.category-count {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  color: var(--terracotta);
  background: rgba(200, 121, 92, 0.1);
  padding: 2px 8px;
  border-radius: 20px;
}

.category-indicator {
  position: absolute;
  left: 0;
  width: 3px;
  height: 0;
  background: linear-gradient(180deg, var(--golden), var(--terracotta));
  border-radius: 0 3px 3px 0;
  transition: height 0.3s ease;
}

.category-item.active .category-indicator {
  height: 60%;
}

/* 侧边栏引用 */
.sidebar-quote {
  margin-top: auto;
  padding: 24px 16px;
  text-align: center;
  border-top: 1px solid rgba(212, 163, 115, 0.2);
}

.quote-text {
  font-family: 'Playfair Display', serif;
  font-size: 16px;
  font-style: italic;
  color: var(--warm-brown);
  margin: 0 0 8px 0;
}

.quote-author {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 0;
}

/* ===== 右侧内容区域 ===== */
.coffee-content {
  flex: 1;
  min-width: 0;
  padding: 24px 32px;
  margin-left: 280px;
}

.content-inner {
  position: relative;
  max-width: 1200px;
}

/* 装饰线条 */
.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.1;
  pointer-events: none;
}

.decorative-line--1 {
  top: 0;
  right: -100px;
  width: 300px;
  height: 1px;
  transform: rotate(-15deg);
}

.decorative-line--2 {
  bottom: 100px;
  left: -80px;
  width: 250px;
  height: 1px;
  transform: rotate(-10deg);
}

/* 区块标题 */
.section-header {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: 'Playfair Display', serif;
  font-size: 36px;
  font-weight: 400;
  color: var(--espresso);
  margin: 0 0 8px 0;
}

.title-icon {
  font-size: 32px;
}

.section-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  letter-spacing: 2px;
  text-transform: uppercase;
  margin: 0;
}

/* 咖啡网格 */
.coffee-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 28px;
  margin-bottom: 48px;
}

.coffee-card-wrapper {
  opacity: 0;
  transform: translateY(30px);
  animation: cardFadeIn 0.6s ease forwards;
  animation-delay: calc(var(--card-index) * 0.08s);
}

/* 无结果状态 */
.no-results {
  text-align: center;
  padding: 80px 40px;
}

.no-results-illustration {
  margin-bottom: 24px;
}

.illustration-icon {
  font-size: 64px;
  opacity: 0.5;
}

.no-results-title {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  color: var(--espresso);
  margin: 0 0 8px 0;
}

.no-results-desc {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  margin-bottom: 24px;
}

.clear-search-btn {
  padding: 10px 24px;
  background: transparent;
  border: 1.5px solid var(--golden);
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--dark-roast);
  cursor: pointer;
  transition: all 0.3s ease;
}

.clear-search-btn:hover {
  background: var(--dark-roast);
  border-color: var(--dark-roast);
  color: var(--crema);
}

/* ===== 动画 ===== */
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

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .menu-layout {
    flex-direction: column;
  }
  
  .category-sidebar {
    width: 100%;
    position: static;
    height: auto;
    border-right: none;
    border-bottom: 1px solid rgba(212, 163, 115, 0.15);
    padding: 16px;
  }
  
  .coffee-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 16px 20px;
  }
  
  .coffee-content {
    padding: 16px;
  }
  
  .section-title {
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
}
</style>