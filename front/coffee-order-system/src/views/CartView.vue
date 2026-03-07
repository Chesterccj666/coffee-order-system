<template>
  <div class="cart-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统</h1>
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
        <h2 class="page-title">购物车</h2>
        
        <div v-if="cartItems.length === 0" class="empty-cart">
          <el-empty description="购物车为空" />
          <el-button type="primary" @click="$router.push('/menu')">去添加商品</el-button>
        </div>
        
        <div v-else>
          <el-table :data="cartItems" stripe style="width: 100%">
            <el-table-column prop="coffeeImage" label="图片" width="100">
              <template #default="{ row }">
                <img :src="row.coffeeImage" class="table-image" alt="咖啡图片" />
              </template>
            </el-table-column>
            <el-table-column prop="coffeeName" label="商品名称" width="200" />
            <el-table-column label="甜度" width="120">
              <template #default="{ row }">
                <span>{{ getSugarText(row.sweet) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="温度" width="120">
              <template #default="{ row }">
                <span>{{ getTempText(row.temperature) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="100">
              <template #default="{ row }">
                ¥{{ row.price }}
              </template>
            </el-table-column>
            <el-table-column label="数量" width="150">
              <template #default="{ row }">
                <el-input-number 
                  v-model="row.quantity" 
                  :min="1" 
                  :max="10" 
                  size="small"
                  @change="updateQuantity(row.id, row.quantity)"
                />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="100">
              <template #default="{ row }">
                <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="removeFromCart(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="cart-summary">
            <div class="total">
              <span>总计：</span>
              <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <el-button type="primary" size="large" @click="checkout">去结算</el-button>
          </div>
        </div>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <p>&copy; 2026 咖啡点单系统. 版权所有.</p>
      </el-footer>
    </el-container>

    <!-- 结算对话框 -->
    <el-dialog v-model="showCheckoutDialog" title="确认订单" width="500px">
      <div class="checkout-content">
        <p>收货人：{{ userInfo.username }}</p>
        <p>联系电话：{{ userInfo.phone }}</p>
        <p>备注：<el-input v-model="orderRemark" placeholder="请输入备注信息" /></p>
        <p class="total-amount">订单总额：¥{{ totalAmount.toFixed(2) }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCheckoutDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmOrder">确认下单</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadCartItems()
      }
    })

    const checkLoginStatus = () => {
      // 从本地存储获取用户信息
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
      } else {
        ElMessage.warning('请先登录')
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
          ElMessage.success('数量更新成功')
        } else {
          ElMessage.error(response.message)
          // 重新加载购物车以恢复原值
          loadCartItems()
        }
      } catch (error) {
        console.error('更新数量失败:', error)
        ElMessage.error('更新数量失败')
        // 重新加载购物车以恢复原值
        loadCartItems()
      }
    }

    const removeFromCart = async (id) => {
      try {
        const response = await deleteCartItem(id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          loadCartItems() // 重新加载购物车
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }

    const totalAmount = computed(() => {
      return cartItems.value.reduce((sum, item) => {
        return sum + (item.price * item.quantity)
      }, 0)
    })

    const getSugarText = (sugarValue) => {
      const sugarMap = {
        1: '正常糖',
        2: '少糖',
        3: '不加糖'
      }
      return sugarMap[sugarValue] || '未知'
    }

    const getTempText = (tempValue) => {
      const tempMap = {
        1: '烫',
        2: '温热',
        3: '少冰',
        4: '正常冰'
      }
      return tempMap[tempValue] || '未知'
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
        // 获取购物车ID列表
        const cartIds = cartItems.value.map(item => item.id)
        
        // 准备订单数据
        const orderData = {
          userId: userInfo.value.id,
          remark: orderRemark.value,
          cartIds: cartIds
        }

        const response = await createOrder(orderData)
        
        if (response.code === 200) {
          ElMessage.success('订单创建成功')
          showCheckoutDialog.value = false
          orderRemark.value = ''
          loadCartItems() // 重新加载购物车
          router.push('/orders') // 跳转到订单页面
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
      updateQuantity,
      removeFromCart,
      getSugarText,
      getTempText,
      checkout,
      confirmOrder
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

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.empty-cart {
  text-align: center;
  padding: 40px;
}

.table-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 5px;
}

.subtotal {
  font-weight: bold;
  color: #e74c3c;
}

.cart-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total {
  font-size: 18px;
  font-weight: bold;
}

.total-price {
  color: #e74c3c;
  font-size: 24px;
}

.checkout-content p {
  margin: 10px 0;
}

.total-amount {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}
</style>