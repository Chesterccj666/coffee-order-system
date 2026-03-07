<template>
  <div class="index-container">
    <!-- 顾客角色 - 显示顾客内容 -->
    <HomeView v-if="isLoggedIn && userInfo.role === 1" />
    
    <!-- 店员角色 - 显示店员首页 -->
    <HomeView v-if="isLoggedIn && userInfo.role === 2" />

    <!-- 管理员角色 - 显示管理员首页 -->
    <HomeView v-if="isLoggedIn && userInfo.role === 3" />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import HomeView from '@/views/HomeView.vue'

export default {
  name: 'IndexView',
  components: {
    HomeView
  },
  setup() {
    const isLoggedIn = ref(false)
    const userInfo = ref({})

    onMounted(() => {
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

    return {
      isLoggedIn,
      userInfo
    }
  }
}
</script>

<style scoped>
.index-container {
  min-height: 100vh;
}
</style>