<template>
  <div class="home">
    <div class="home-container">
      <!-- 头部 -->
      <div class="header">
        <div class="header-content">
          <h1 class="logo">☕ 咖啡点单系统</h1>
          <div class="nav-links">
            <el-button type="text" @click="$router.push('/menu')">菜单</el-button>
            <el-button type="text" @click="$router.push('/cart')">购物车</el-button>
            <el-button type="text" @click="$router.push('/orders')">订单</el-button>
            <el-button v-if="!isLoggedIn" type="text" @click="$router.push('/login')">登录</el-button>
            <el-button v-else type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
          </div>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <div class="main-content">
        <div class="welcome-section">
          <h2>欢迎来到我们的咖啡世界</h2>
          <p>精心调制的每一杯咖啡，为您带来温暖与舒适</p>
          <el-button type="primary" size="large" @click="$router.push('/menu')">开始点单</el-button>
        </div>

        <!-- 推荐咖啡展示 -->
        <div class="recommended-section">
          <h3>推荐咖啡</h3>
          <div class="coffee-display-container">
            <div class="coffee-carousel-section">
              <el-carousel 
                height="200px"
                :autoplay="true"
                :interval="3000" 
                indicator-position="outside"
                @change="handleCarouselChange"
                ref="carouselRef"
              >
                <el-carousel-item v-for="(coffee, index) in limitedRecommendedCoffees" :key="coffee.id">
                  <div class="carousel-coffee-item" @click="$router.push(`/menu#${coffee.id}`)">
                    <img :src="coffee.coffeeImage" class="carousel-coffee-image" alt="咖啡图片" />
                  </div>
                </el-carousel-item>
              </el-carousel>
            </div>
            <div class="coffee-info-section">
              <div class="coffee-info-card" v-if="currentCoffee">
                <h4>{{ currentCoffee.name }}</h4>
                <p class="price">¥{{ currentCoffee.price }}</p>
                <p class="description">{{ currentCoffee.description }}</p>
                <el-button type="primary" @click="$router.push(`/menu#${currentCoffee.id}`)">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部 -->
      <div class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'HomeView',
  setup() {
    const recommendedCoffees = ref([])
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const currentCoffee = ref(null)
    const carouselRef = ref(null)

    // 限制推荐咖啡数量最多为8种
    const limitedRecommendedCoffees = computed(() => {
      return recommendedCoffees.value.slice(0, 8)
    })

    onMounted(() => {
      loadRecommendedCoffees()
      checkLoginStatus()
    })

    const loadRecommendedCoffees = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/coffee/recommended')
        if (response.data.code === 200) {
          recommendedCoffees.value = response.data.data
          // 初始化当前显示的咖啡信息
          if (limitedRecommendedCoffees.value.length > 0) {
            currentCoffee.value = limitedRecommendedCoffees.value[0]
          }
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('加载推荐咖啡失败:', error)
        ElMessage.error('加载推荐咖啡失败')
      }
    }

    const handleCarouselChange = (index) => {
      // 当轮播图切换时，更新右侧的咖啡信息
      currentCoffee.value = limitedRecommendedCoffees.value[index]
    }

    const checkLoginStatus = () => {
      // 从本地存储获取用户信息
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
      }
    }

    return {
      recommendedCoffees,
      limitedRecommendedCoffees,
      isLoggedIn,
      userInfo,
      currentCoffee,
      carouselRef,
      handleCarouselChange
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  padding: 20px 0;
}

.home-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  margin: 0;
  color: #8B4513;
  font-size: 24px;
}

.nav-links .el-button {
  margin-left: 20px;
  font-size: 16px;
}

.main-content {
  padding: 30px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-section h2 {
  color: #333;
  font-size: 32px;
  margin-bottom: 15px;
}

.welcome-section p {
  color: #666;
  font-size: 18px;
  margin-bottom: 25px;
}

.recommended-section {
  margin-top: 30px;
}

.recommended-section h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.coffee-display-container {
  display: flex;
  gap: 30px;
  align-items: center;
}

.coffee-carousel-section {
  flex: 1;
  max-width: 50%;
}

.coffee-info-section {
  flex: 1;
  max-width: 50%;
}

.coffee-info-card {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.coffee-info-card h4 {
  font-size: 24px;
  color: #333;
  margin-top: 0;
  margin-bottom: 15px;
}

.coffee-info-card .price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
  margin: 10px 0;
}

.coffee-info-card .description {
  color: #666;
  line-height: 1.6;
  margin: 15px 0;
  flex-grow: 1;
}

.carousel-coffee-item {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
}

.carousel-coffee-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.carousel-coffee-item:hover .carousel-coffee-image {
  transform: scale(1.05);
}

.el-carousel__item {
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .coffee-display-container {
    flex-direction: column;
  }
  
  .coffee-carousel-section,
  .coffee-info-section {
    max-width: 100%;
  }
}

.footer {
  text-align: center;
  padding: 20px;
  color: #999;
  border-top: 1px solid #eee;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .home-container {
    margin: 0 10px;
    border-radius: 8px;
  }
  
  .header-content {
    flex-direction: column;
    height: auto;
    gap: 10px;
  }
  
  .nav-links .el-button {
    margin-left: 0;
    margin-top: 5px;
  }
  
  .main-content {
    padding: 20px;
  }
  
  .welcome-section h2 {
    font-size: 24px;
  }
  
  .welcome-section p {
    font-size: 16px;
  }
}
</style>