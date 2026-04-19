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


  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCoffee, getCoffeeByCategory } from '@/api/coffee'
import { addToCart as addCart } from '@/api/cart'

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

    onMounted(() => {
      loadCoffees()
      checkLoginStatus()
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
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('添加到购物车失败:', error)
        ElMessage.error('添加到购物车失败')
      }
    }



    return {
      coffees,
      activeCategory,
      isLoggedIn,
      userInfo,
      selectedOptions,
      searchKeyword,
      onCategoryChange,
      onSearchChange,
      addToCart,
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