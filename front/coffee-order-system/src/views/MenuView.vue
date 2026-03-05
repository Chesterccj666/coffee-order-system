<template>
  <div class="menu-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统</h1>
          <div class="nav-links">
            <el-button type="text" @click="$router.push('/menu')">菜单</el-button>
            <el-button type="text" @click="$router.push('/cart')">购物车</el-button>
            <el-button type="text" @click="$router.push('/orders')">订单</el-button>
            <el-button v-if="!isLoggedIn" type="text" @click="$router.push('/login')">登录</el-button>
            <el-button v-else type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <!-- 分类导航 -->
        <el-tabs v-model="activeCategory" @tab-change="onCategoryChange" class="category-tabs">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="美式" name="美式"></el-tab-pane>
          <el-tab-pane label="意式" name="意式"></el-tab-pane>
          <el-tab-pane label="花式" name="花式"></el-tab-pane>
        </el-tabs>

        <!-- 咖啡列表 -->
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="coffee in filteredCoffees" :key="coffee.id">
            <el-card class="coffee-card">
              <img :src="coffee.coffeeImage" class="coffee-image" alt="咖啡图片" />
              <div class="coffee-info">
                <h3>{{ coffee.name }}</h3>
                <p class="description">{{ coffee.description }}</p>
                <p class="price">¥{{ coffee.price }}</p>
                <p class="stock">剩余数量: {{ coffee.stock }}</p>
                <div class="customization-options">
                  <div class="option-group">
                    <span>甜度:</span>
                    <el-radio-group v-model="selectedOptions[coffee.id].sweet" size="small">
                      <el-radio-button :label="1">正常糖</el-radio-button>
                      <el-radio-button :label="2">少糖</el-radio-button>
                      <el-radio-button :label="3">不加糖</el-radio-button>
                    </el-radio-group>
                  </div>
                  <div class="option-group">
                    <span>温度:</span>
                    <el-radio-group v-model="selectedOptions[coffee.id].temperature" size="small">
                      <el-radio-button :label="1">烫</el-radio-button>
                      <el-radio-button :label="2">温热</el-radio-button>
                      <el-radio-button :label="3">少冰</el-radio-button>
                      <el-radio-button :label="4">正常冰</el-radio-button>
                    </el-radio-group>
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
                    size="small" 
                    @click="addToCart(coffee)"
                    :disabled="coffee.stock <= 0"
                  >
                    加入购物车
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </el-footer>
    </el-container>

    <!-- 添加到购物车成功提示对话框 -->
    <el-dialog v-model="showAddCartSuccess" title="添加成功" width="30%">
      <p>已将 {{ addToCartCoffeeName }} 添加到购物车！</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddCartSuccess = false">继续浏览</el-button>
          <el-button type="primary" @click="goToCart">去购物车</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'MenuView',
  setup() {
    const coffees = ref([])
    const activeCategory = ref('all')
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const showAddCartSuccess = ref(false)
    const addToCartCoffeeName = ref('')
    const selectedOptions = ref({})

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
        const response = await axios.get('http://localhost:8080/api/coffee/list')
        if (response.data.code === 200) {
          coffees.value = response.data.data
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
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('加载咖啡列表失败:', error)
        ElMessage.error('加载咖啡列表失败')
      }
    }

    const onCategoryChange = async (category) => {
      try {
        let response
        if (category === 'all') {
          response = await axios.get('http://localhost:8080/api/coffee/list')
        } else {
          response = await axios.get(`http://localhost:8080/api/coffee/category/${category}`)
        }
        
        if (response.data.code === 200) {
          coffees.value = response.data.data
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
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('加载咖啡列表失败:', error)
        ElMessage.error('加载咖啡列表失败')
      }
    }

    const filteredCoffees = computed(() => {
      if (activeCategory.value === 'all') {
        return coffees.value
      }
      return coffees.value.filter(coffee => coffee.category === activeCategory.value)
    })

    const addToCart = async (coffee) => {
      if (!isLoggedIn.value) {
        ElMessageBox.confirm('您还未登录，是否前往登录？', '提示', {
          confirmButtonText: '前往登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 跳转到登录页面
          $router.push('/login')
        })
        return
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

        const response = await axios.post('http://localhost:8080/api/cart/add', cartItem)
        
        if (response.data.code === 200) {
          addToCartCoffeeName.value = coffee.name
          showAddCartSuccess.value = true
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('添加到购物车失败:', error)
        ElMessage.error('添加到购物车失败')
      }
    }

    const goToCart = () => {
      showAddCartSuccess.value = false
      $router.push('/cart')
    }

    return {
      coffees,
      activeCategory,
      isLoggedIn,
      userInfo,
      showAddCartSuccess,
      addToCartCoffeeName,
      selectedOptions,
      filteredCoffees,
      onCategoryChange,
      addToCart,
      goToCart
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

.coffee-card {
  margin-bottom: 10px; /* 减小卡片自身的下边距 */
  margin-left: 8px;
  margin-right: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 12px; /* 增加圆角 */
  overflow: hidden; /* 确保内容也遵循圆角 */
}

/* 添加行间距控制 */
.el-row :deep(.el-col) {
  margin-bottom: 25px; /* 控制每行之间的间距 */
}

.coffee-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 12px 12px 0 0; /* 与容器圆角匹配 */
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
  color: #333;
}

.description {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
  flex-grow: 1;
}

.price {
  font-size: 20px;
  color: #e74c3c;
  font-weight: bold;
  margin: 5px 0;
}

.stock {
  color: #999;
  font-size: 14px;
  margin: 5px 0;
}

.customization-options {
  margin: 10px 0;
}

.option-group {
  margin-bottom: 8px;
}

.option-group span {
  display: inline-block;
  width: 40px;
  font-size: 12px;
  color: #666;
  margin-right: 5px;
}

.add-to-cart {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.add-to-cart .el-input-number {
  width: 100px;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}
</style>