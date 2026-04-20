<template>
  <div class="menu-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统 - 菜单</h1>
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
        <el-container>
          <!-- 左侧分类导航 -->
          <el-aside width="200px" class="category-aside">
            <div class="search-box">
              <el-input 
                v-model="searchKeyword" 
                placeholder="搜索咖啡名称" 
                clearable
                @input="onSearchChange"
              />
            </div>
            <el-menu
              :default-active="activeCategory"
              class="category-menu"
              @select="onCategoryChange"
            >
              <el-menu-item index="经典意式">经典意式</el-menu-item>
              <el-menu-item index="风味拿铁">风味拿铁</el-menu-item>
              <el-menu-item index="风味美式">风味美式</el-menu-item>
              <el-menu-item index="奶咖">奶咖</el-menu-item>
              <el-menu-item index="燕麦系列">燕麦系列</el-menu-item>
              <el-menu-item index="单品豆SOE">单品豆SOE</el-menu-item>
              <el-menu-item index="其他">其他</el-menu-item>
            </el-menu>
          </el-aside>

          <!-- 右侧咖啡列表 -->
          <el-main class="coffee-main">
            <!-- 按类别展示咖啡或按搜索结果展示 -->
            <div v-if="searchKeyword">
              <!-- 搜索结果展示 -->
              <div class="category-section">
                <h2 class="category-title">搜索结果</h2>
                <div v-for="coffee in getCoffeesBySearch()" :key="coffee.id">
                  <el-card class="horizontal-coffee-card">
                    <div class="horizontal-card-content">
                      <div class="coffee-image-container">
                        <img :src="coffee.coffeeImage" class="horizontal-coffee-image" alt="咖啡图片" />
                      </div>
                      <div class="horizontal-coffee-info">
                        <div class="info-and-controls-container">
                          <div class="left-side">
                            <h3 class="coffee-name">{{ coffee.name }}</h3>
                            <p class="description">{{ coffee.description }}</p>
                            <p class="stock">剩余: {{ coffee.stock }}</p>
                          </div>
                          <div class="right-side">
                            <div class="customization-options">
                              <div class="option-group">
                                <span class="option-label">甜度:</span>
                                <div class="option-controls">
                                  <el-radio-group v-model="selectedOptions[coffee.id].sweet" size="small">
                                    <el-radio-button :label="1">正常糖</el-radio-button>
                                    <el-radio-button :label="2">少糖</el-radio-button>
                                    <el-radio-button :label="3">不加糖</el-radio-button>
                                  </el-radio-group>
                                </div>
                              </div>
                              <div class="option-group">
                                <span class="option-label">温度:</span>
                                <div class="option-controls">
                                  <el-radio-group v-model="selectedOptions[coffee.id].temperature" size="small">
                                    <el-radio-button :label="1">烫</el-radio-button>
                                    <el-radio-button :label="2">温热</el-radio-button>
                                    <el-radio-button :label="3">少冰</el-radio-button>
                                    <el-radio-button :label="4">正常冰</el-radio-button>
                                  </el-radio-group>
                                </div>
                              </div>
                            </div>
                            <div class="add-to-cart">
                              <el-input-number 
                                v-model="selectedOptions[coffee.id].quantity" 
                                :min="1" 
                                :max="coffee.stock"
                                size="small"
                              />
                              <el-button 
                                type="primary" 
                                @click="addToCart(coffee)"
                                :disabled="coffee.stock <= 0"
                              >
                                ¥{{ (coffee.price * selectedOptions[coffee.id].quantity).toFixed(2) }} 加入购物车
                              </el-button>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-card>
                </div>
                <div v-if="getCoffeesBySearch().length === 0" class="no-results">
                   <p>未找到匹配的咖啡</p>
                 </div>
              </div>
            </div>
            <div v-else>
              <!-- 按类别展示咖啡 -->
              <div v-for="category in ['经典意式', '风味拿铁', '风味美式', '奶咖', '燕麦系列', '单品豆SOE', '其他']" :key="category">
                <div v-if="getCoffeesByCategory(category).length > 0" class="category-section" :id="'category-' + category">
                  <h2 class="category-title">{{ category }}</h2>
                  <div v-for="coffee in getCoffeesByCategory(category)" :key="coffee.id">
                    <el-card class="horizontal-coffee-card">
                      <div class="horizontal-card-content">
                        <div class="coffee-image-container">
                          <img :src="coffee.coffeeImage" class="horizontal-coffee-image" alt="咖啡图片" />
                        </div>
                        <div class="horizontal-coffee-info">
                          <div class="info-and-controls-container">
                            <div class="left-side">
                              <h3 class="coffee-name">{{ coffee.name }}</h3>
                              <p class="description">{{ coffee.description }}</p>
                              <p class="stock">剩余: {{ coffee.stock }}</p>
                            </div>
                            <div class="right-side">
                            <div class="customization-options">
                              <div class="option-group">
                                <div class="option-controls">
                                  <el-radio-group v-model="selectedOptions[coffee.id].sweet" size="medium">
                                    <el-radio-button :label="1">正常糖</el-radio-button>
                                    <el-radio-button :label="2">少糖</el-radio-button>
                                    <el-radio-button :label="3">不加糖</el-radio-button>
                                  </el-radio-group>
                                </div>
                              </div>
                              <div class="option-group">
                                <div class="option-controls">
                                  <el-radio-group v-model="selectedOptions[coffee.id].temperature" size="medium">
                                    <el-radio-button :label="1">烫</el-radio-button>
                                    <el-radio-button :label="2">温热</el-radio-button>
                                    <el-radio-button :label="3">少冰</el-radio-button>
                                    <el-radio-button :label="4">正常冰</el-radio-button>
                                  </el-radio-group>
                                </div>
                              </div>
                            </div>
                            <div class="add-to-cart">
                              <el-input-number 
                                v-model="selectedOptions[coffee.id].quantity" 
                                :min="1" 
                                :max="coffee.stock"
                                size="medium"
                              />
                              <el-button 
                                type="primary" 
                                @click="addToCart(coffee)"
                                :disabled="coffee.stock <= 0"
                                size="large"
                                
                              >
                                ¥{{ (coffee.price * selectedOptions[coffee.id].quantity).toFixed(2) }} 加入购物车
                              </el-button>
                            </div>
                          </div>
                          </div>
                        </div>
                      </div>
                    </el-card>
                  </div>
                </div>
              </div>
            </div>
          </el-main>
        </el-container>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </el-footer>
    </el-container>

    <!-- 固定在底部的小购物车组件 -->
    <div class="mini-cart" v-if="isLoggedIn && userInfo.role === 1">
      <el-popover
        placement="top"
        title="购物车"
        width="400"
        trigger="click"
        v-model:visible="showMiniCartPopover"
      >
        <div class="mini-cart-content">
          <div v-if="miniCartItems.length === 0" class="empty-cart">
            购物车为空
          </div>
          <div v-else class="cart-items">
            <div 
              class="cart-item" 
              v-for="item in miniCartItems" 
              :key="item.id"
            >
              <img :src="item.coffeeImage" class="item-image" alt="咖啡图片" />
              <div class="item-info">
                <div class="item-name">{{ item.coffeeName }}</div>
                <div class="item-specs">
                  <span class="spec">{{ getSugarText(item.sweet) }}</span>
                  <span class="spec">{{ getTempText(item.temperature) }}</span>
                </div>
              </div>
              <div class="item-quantity">
                <el-button 
                  @click="decreaseQuantity(item)"
                  :disabled="item.quantity <= 1"
                  size="small"
                  circle
                >
                  -
                </el-button>
                <span class="quantity-value">{{ item.quantity }}</span>
                <el-button 
                  @click="increaseQuantity(item)"
                  size="small"
                  circle
                >
                  +
                </el-button>
              </div>
              <div class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              <el-button 
                class="delete-item-btn" 
                type="danger" 
                size="small" 
                circle
                @click="removeCartItem(item.id)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="cart-actions">
            <div class="cart-total" v-if="miniCartItems.length > 0">
              合计：¥{{ miniCartTotal.toFixed(2) }}
            </div>
            <el-button v-if="miniCartItems.length > 0" type="primary" size="medium" @click="$router.push('/cart')">去支付</el-button>
          </div>
        </div>
        <template #reference>
          <el-badge :value="miniCartItemCount" class="item" :hidden="miniCartItemCount === 0">
            <el-button type="primary" circle class="mini-cart-btn">
              <el-icon><ShoppingCart /></el-icon>
            </el-button>
          </el-badge>
        </template>
      </el-popover>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCoffee } from '@/api/coffee'
import { addToCart as addCart, getUserCart, deleteCartItem, updateCartItemQuantity } from '@/api/cart'
import { ShoppingCart, Delete } from '@element-plus/icons-vue'

export default {
  name: 'MenuView',
  setup() {
    const router = useRouter()
    const coffees = ref([])
    const activeCategory = ref('all')
    const isLoggedIn = ref(false)
    const userInfo = ref({})

    const selectedOptions = ref({})
    const searchKeyword = ref('')
    const showMiniCartPopover = ref(false)
    const miniCartItems = ref([])
    const miniCartTotal = ref(0)
    const miniCartItemCount = ref(0)

    onMounted(() => {
      loadCoffees()
      checkLoginStatus()
      loadMiniCart()
    })

    const checkLoginStatus = () => {
      // 从本地存储获取用户信息
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
          // 初始化选项
          coffees.value.forEach(coffee => {
            if (!selectedOptions.value[coffee.id]) {
              selectedOptions.value[coffee.id] = {
                quantity: 1,
                sweet: 1, // 默认正常糖
                temperature: 1 // 默认烫
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

    const onCategoryChange = (category) => {
      activeCategory.value = category
      // 清空搜索关键词
      searchKeyword.value = ''
      // 滚动到对应分类的位置
      setTimeout(() => {
        const element = document.getElementById('category-' + category)
        if (element) {
          element.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      }, 100)
    }

    const onSearchChange = () => {
      // 当进行搜索时，清空分类选择
      activeCategory.value = ''
    }

    const getCoffeesByCategory = (category) => {
      return coffees.value.filter(coffee => coffee.category === category)
    }

    const filterCoffees = (coffeesList) => {
      let filtered = coffeesList
      
      // 如果有搜索关键词，按名称进行模糊匹配
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        filtered = filtered.filter(coffee => 
          coffee.name.toLowerCase().includes(keyword)
        )
      }
      
      return filtered
    }

    const getCoffeesBySearch = () => {
      return filterCoffees(coffees.value)
    }

    const addToCart = async (coffee) => {
      if (!isLoggedIn.value) {
        ElMessageBox.confirm('您还未登录，是否前往登录？', '提示', {
          confirmButtonText: '前往登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          router.push('/login')
        }).catch(() => {
          // 用户取消登录
          return
        })
      }

      try {
        const cartItem = {
          userId: userInfo.value.id,
          coffeeId: coffee.id,
          coffeeName: coffee.name,
          coffeeImage: coffee.coffeeImage,
          price: coffee.price,
          quantity: selectedOptions.value[coffee.id].quantity,
          sweet: selectedOptions.value[coffee.id].sweet,
          temperature: selectedOptions.value[coffee.id].temperature
        }

        const response = await addCart(cartItem)
        
        if (response.code === 200) {
          ElMessage.success(`已将 ${coffee.name} 添加到购物车！`)
          // 更新小购物车
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
      if (!isLoggedIn.value || !userInfo.value.id) {
        return
      }

      try {
        const response = await getUserCart(userInfo.value.id)
        if (response.code === 200) {
          miniCartItems.value = response.data.map(item => ({
            ...item,
            totalPrice: item.price * item.quantity
          }))
          
          // 计算购物车总数量
          miniCartItemCount.value = response.data.reduce((sum, item) => sum + item.quantity, 0)
          
          // 计算购物车总金额
          miniCartTotal.value = response.data.reduce((sum, item) => sum + (item.price * item.quantity), 0)
        } else {
          console.error('加载购物车失败:', response.message)
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
      }
    }

    const closeMiniCart = () => {
      showMiniCartPopover.value = false
    }

    // 获取甜度文本
    const getSugarText = (sweetValue) => {
      switch(sweetValue) {
        case 1: return '正常糖'
        case 2: return '少糖'
        case 3: return '不加糖'
        default: return '正常糖'
      }
    }

    // 获取温度文本
    const getTempText = (tempValue) => {
      switch(tempValue) {
        case 1: return '烫'
        case 2: return '温热'
        case 3: return '少冰'
        case 4: return '正常冰'
        default: return '烫'
      }
    }

    const removeCartItem = async (itemId) => {
      try {
        const response = await deleteCartItem(itemId)
        if (response.code === 200) {
          ElMessage.success('已从购物车中移除')
          // 重新加载购物车
          await loadMiniCart()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('移除购物车项目失败:', error)
        ElMessage.error('移除购物车项目失败')
      }
    }

    const decreaseQuantity = async (item) => {
      if (item.quantity <= 1) {
        return // 最少为1，不能再减少
      }
      
      try {
        // 这里需要调用更新购物车数量的API
        // 需要先查找对应的购物车项ID
        const response = await getUserCart(userInfo.value.id)
        if (response.code === 200) {
          const cartItem = response.data.find(cartItem => 
            cartItem.coffeeId === item.coffeeId && 
            cartItem.sweet === item.sweet && 
            cartItem.temperature === item.temperature
          )
          
          if (cartItem) {
            // 调用更新数量的API，数量减1
            const updateResponse = await updateCartItemQuantity(cartItem.id, item.quantity - 1)
            if (updateResponse.code === 200) {
              // 重新加载购物车
              await loadMiniCart()
            } else {
              ElMessage.error(updateResponse.message)
            }
          }
        }
      } catch (error) {
        console.error('更新购物车数量失败:', error)
        ElMessage.error('更新购物车数量失败')
      }
    }

    const increaseQuantity = async (item) => {
      try {
        // 查找对应的购物车项ID
        const response = await getUserCart(userInfo.value.id)
        if (response.code === 200) {
          const cartItem = response.data.find(cartItem => 
            cartItem.coffeeId === item.coffeeId && 
            cartItem.sweet === item.sweet && 
            cartItem.temperature === item.temperature
          )
          
          if (cartItem) {
            // 调用更新数量的API，数量加1
            const updateResponse = await updateCartItemQuantity(cartItem.id, item.quantity + 1)
            if (updateResponse.code === 200) {
              // 重新加载购物车
              await loadMiniCart()
            } else {
              ElMessage.error(updateResponse.message)
            }
          }
        }
      } catch (error) {
        console.error('更新购物车数量失败:', error)
        ElMessage.error('更新购物车数量失败')
      }
    }



    return {
      coffees,
      activeCategory,
      isLoggedIn,
      userInfo,
      selectedOptions,
      searchKeyword,
      showMiniCartPopover,
      miniCartItems,
      miniCartTotal,
      miniCartItemCount,
      onCategoryChange,
      onSearchChange,
      addToCart,
      loadMiniCart,
      closeMiniCart,
      removeCartItem,
      decreaseQuantity,
      increaseQuantity,
      getSugarText,
      getTempText,
      getCoffeesByCategory,
      getCoffeesBySearch
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
  margin-top: 20px;
}

.category-tabs {
  margin-bottom: 20px;
}

.category-aside {
  background-color: #f5f5f5;
  padding: 20px 0;
  max-height: calc(100vh - 160px); /* 限制最大高度 */
  position: fixed;
  left: 0;
  top: 138px; /* 调整以避开头部 */
  z-index: 100;
  overflow-y: auto;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  border-right: 1px solid #eee;
  width: 200px;
}

.search-box {
  padding: 0 12px 12px;
}

.category-menu {
  border-right: none;
  background-color: transparent;
}

.category-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 16px;
  color: #666;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.category-menu .el-menu-item:hover {
  background-color: #f5f5f5;
  color: #333;
}

.category-menu .el-menu-item.is-active {
  background-color: #8bcbed;
  color: white;
  font-weight: bold;
}

.coffee-main {
  padding: 20px;
  margin-left: 220px; /* 为左侧固定导航留出空间 */
}

.category-section {
  margin-bottom: 40px;
}

.category-title {
  font-size: 26px;
  color: #333;
  border-bottom: 3px solid #e0dedd;
  padding-bottom: 12px;
  margin-bottom: 25px;
  font-weight: bold;
}

.horizontal-coffee-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 260px;
}

.horizontal-coffee-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.horizontal-card-content {
  display: flex;
  align-items: center;
  min-height: 180px;
}

.coffee-image-container {
  flex: 0 0 160px;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.horizontal-coffee-image {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-top: 2px;
}

.horizontal-coffee-info {
  flex: 1;
  padding: 20px;
  display: flex;
  min-height: 160px;
}

.info-and-controls-container {
  display: flex;
  width: 100%;
  gap: 20px;
}

.left-side {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.right-side {
  flex: 0 0 300px; /* 右侧固定宽度以容纳控件 */
  display: flex;
  flex-direction: column;
  gap: 15px;
  position: relative;
  padding-top: 35px; /* 为浮动的价格留出空间 */
  padding-right: 10px; /* 避免价格与右边框重叠 */
}

.coffee-name {
  margin: 0;
  font-size: 25px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.description {
  color: #666;
  font-size: 16px;
  margin: 0 0 10px 0;
  flex: 1;
}

.stock {
  color: #27ae60;
  font-size: 16px;
  margin: 0 0 15px 0;
}

.customization-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.option-label {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.option-controls {
  flex: 1;
}

.add-to-cart {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-top: auto; /* 将按钮推到底部 */
}

.add-to-cart .el-input-number {
  width: 120px;
}

.add-to-cart .el-button {
  font-size: 16px;
  padding: 10px 20px;
  width: 200px; /* 固定按钮宽度 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.footer {
  background-color: #f5f5f5;
  text-align: center;
  padding: 20px;
  color: #666;
  margin-top: auto;
}

/* 小购物车样式 */
.mini-cart {
  position: fixed;
  bottom: 10px;
  left: 10px;
  z-index: 1001;
}

.mini-cart-btn {
  width: 60px;
  height: 60px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.mini-cart-content {
  max-height: 320px;
  overflow-y: auto;
}

.empty-cart {
  text-align: center;
  padding: 20px;
  color: #999;
}

.cart-items {
  margin-bottom: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  margin-right: 10px;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-specs {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #666;
}

.spec {
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 0;
  margin: 0 10px;
  color: #666;
  min-width: 80px;
  text-align: center;
}

.quantity-value {
  min-width: 20px;
  text-align: center;
  font-weight: 1000;
}

.item-price {
  font-weight: 500;
  color: #e74c3c;
  min-width: 60px;
  text-align: right;
}

.delete-item-btn {
  margin-left: 10px;
  width: 24px;
  height: 24px;
  padding: 5px;
}

.cart-total {
  font-weight: bold;
  padding: 10px 0;
  border-top: 1px solid #eee;
  text-align: right;
  color: #e74c3c;
}

.cart-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
  margin-top: auto; /* 将按钮推到底部 */
  position: sticky;
  bottom: 0;
  background: white;
  padding: 15px 0 0 0;
}



.customization-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.option-label {
  font-size: 16px;
  color: #555;
  white-space: nowrap;
  width: 60px;
}

.option-controls {
  flex: 1;
}

.add-to-cart {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-top: auto; /* 将按钮推到底部 */
}

.add-to-cart .el-input-number {
  width: 120px;
}

.add-to-cart .el-button {
  font-size: 16px;
  padding: 10px 20px;
  width: 200px; /* 固定按钮宽度 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coffee-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.coffee-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.15);
}

.coffee-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.coffee-info {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.coffee-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: bold;
}

.coffee-info p {
  margin: 5px 0;
  font-size: 14px;
}

.description {
  color: #666;
  flex-grow: 1;
}

.price {
  color: #e74c3c;
  font-weight: bold;
  font-size: 16px;
}

.stock {
  color: #27ae60;
}

.option-group {
  margin-bottom: 8px;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}
</style>