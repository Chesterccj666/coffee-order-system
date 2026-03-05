<template>
  <div class="home">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
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
      </el-header>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <div class="welcome-section">
          <h2>欢迎来到我们的咖啡世界</h2>
          <p>精心调制的每一杯咖啡，为您带来温暖与舒适</p>
          <el-button type="primary" size="large" @click="$router.push('/menu')">开始点单</el-button>
        </div>

        <!-- 推荐咖啡展示 -->
        <div class="recommended-section">
          <h3>推荐咖啡</h3>
          <el-row :gutter="20">
            <el-col :span="6" v-for="coffee in recommendedCoffees" :key="coffee.id">
              <el-card class="coffee-card" @click="$router.push(`/menu#${coffee.id}`)">
                <img :src="coffee.coffeeImage" class="coffee-image" alt="咖啡图片" />
                <div class="coffee-info">
                  <h4>{{ coffee.name }}</h4>
                  <p class="price">¥{{ coffee.price }}</p>
                  <p class="description">{{ coffee.description.substring(0, 30) }}...</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'HomeView',
  setup() {
    const recommendedCoffees = ref([])
    const isLoggedIn = ref(false)
    const userInfo = ref({})

    onMounted(() => {
      loadRecommendedCoffees()
      checkLoginStatus()
    })

    const loadRecommendedCoffees = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/coffee/recommended')
        if (response.data.code === 200) {
          recommendedCoffees.value = response.data.data
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('加载推荐咖啡失败:', error)
        ElMessage.error('加载推荐咖啡失败')
      }
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
      isLoggedIn,
      userInfo
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
}

.nav-links .el-button {
  margin-left: 20px;
  font-size: 16px;
}

.main-content {
  padding: 20px;
  background-color: #f5f5f5;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 10px;
}

.welcome-section h2 {
  font-size: 32px;
  color: #333;
  margin-bottom: 15px;
}

.welcome-section p {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}

.recommended-section {
  margin-top: 30px;
}

.recommended-section h3 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.coffee-card {
  cursor: pointer;
  transition: transform 0.3s ease;
  margin-bottom: 20px;
}

.coffee-card:hover {
  transform: translateY(-5px);
}

.coffee-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 5px 5px 0 0;
}

.coffee-info {
  padding: 15px;
}

.coffee-info h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
}

.price {
  font-size: 18px;
  color: #e74c3c;
  font-weight: bold;
  margin: 5px 0;
}

.description {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}
</style>