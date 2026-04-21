<template>
  <div class="mini-cart-wrapper">
    <el-popover
      placement="top"
      width="420"
      trigger="click"
      v-model:visible="visible"
      popper-class="mini-cart-popover"
    >
      <template #reference>
        <div class="mini-cart-trigger">
          <el-badge :value="itemCount" :hidden="itemCount === 0" class="cart-badge">
            <button class="mini-cart-btn">
              <svg viewBox="0 0 24 24" fill="none" class="cart-icon">
                <path d="M3 3H5L5.4 5M7 13H17L21 5H5.4M7 13L5.4 5M7 13L4.7 15.3C4.3 15.7 4.6 16.5 5.1 16.5H17M17 13V19C17 19.5 16.5 20 16 20H8C7.5 20 7 19.5 7 19V13M17 13H7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </button>
          </el-badge>
        </div>
      </template>
      
      <div class="mini-cart-content">
        <div class="cart-header">
          <h4 class="cart-title">购物车</h4>
          <span class="item-count">{{ itemCount }} 件商品</span>
        </div>
        
        <div v-if="items.length === 0" class="empty-cart">
          <span class="empty-icon">🛒</span>
          <p>购物车空空如也</p>
          <p class="empty-hint">去挑选一杯心仪的咖啡吧</p>
        </div>
        
        <div v-else class="cart-items-list">
          <div v-for="item in items" :key="item.id" class="cart-item">
            <img :src="item.coffeeImage" :alt="item.coffeeName" class="item-image" />
            <div class="item-details">
              <span class="item-name">{{ item.coffeeName }}</span>
              <div class="item-specs">
                <span class="spec-tag">{{ getSugarText(item.sweet) }}</span>
                <span class="spec-tag">{{ getTempText(item.temperature) }}</span>
              </div>
            </div>
            <div class="item-actions">
              <!-- 数量控制组件 -->
              <div class="quantity-control">
                <button 
                  class="qty-btn qty-decrement"
                  @click="decrementQuantity(item)"
                  :disabled="item.quantity <= 1"
                >
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
                <span class="qty-value">{{ item.quantity }}</span>
                <button 
                  class="qty-btn qty-increment"
                  @click="incrementQuantity(item)"
                >
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
              
              <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              <button class="remove-item-btn" @click="handleRemove(item.id)">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
        
        <div v-if="items.length > 0" class="cart-footer">
          <div class="cart-total">
            <span class="total-label">合计</span>
            <span class="total-value">¥{{ total.toFixed(2) }}</span>
          </div>
          <button class="checkout-btn" @click="handleCheckout">
            <span>去结算</span>
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'MiniCart',
  props: {
    items: {
      type: Array,
      default: () => []
    },
    total: {
      type: Number,
      default: 0
    },
    itemCount: {
      type: Number,
      default: 0
    }
  },
  emits: ['remove', 'checkout', 'update-quantity'],
  setup(props, { emit }) {
    const visible = ref(false)
    
    const getSugarText = (val) => {
      const map = { 1: '正常糖', 2: '少糖', 3: '不加糖' }
      return map[val] || '正常糖'
    }

    const getTempText = (val) => {
      const map = { 1: '烫', 2: '温热', 3: '少冰', 4: '正常冰' }
      return map[val] || '烫'
    }

    const handleRemove = (id) => {
      emit('remove', id)
    }

    const handleCheckout = () => {
      visible.value = false
      emit('checkout')
    }

    const closePopover = () => {
      visible.value = false
    }

    const handleQuantityChange = (itemId, newQuantity) => {
      if (newQuantity < 1) return
      emit('update-quantity', { itemId, quantity: newQuantity })
    }

    const incrementQuantity = (item) => {
      handleQuantityChange(item.id, item.quantity + 1)
    }

    const decrementQuantity = (item) => {
      if (item.quantity > 1) {
        handleQuantityChange(item.id, item.quantity - 1)
      }
    }

    return {
      visible,
      getSugarText,
      getTempText,
      handleRemove,
      handleCheckout,
      closePopover,
      incrementQuantity,
      decrementQuantity
    }
  }
}
</script>

<style scoped>
.mini-cart-wrapper {
  position: fixed;
  bottom: 30px;
  left: 30px;
  z-index: 1000;
}

.mini-cart-trigger {
  position: relative;
}

.mini-cart-btn {
  width: 60px;
  height: 60px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #3A241A, #5C3A2A);
  box-shadow: 0 8px 24px rgba(58, 36, 26, 0.25);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  color: #FDF8F2;
}

.mini-cart-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 12px 32px rgba(58, 36, 26, 0.3);
}

.cart-icon {
  width: 28px;
  height: 28px;
}

.cart-badge :deep(.el-badge__content) {
  background: #C8795C;
  border: 2px solid #FDF8F2;
}
</style>

<style>
.mini-cart-popover {
  border-radius: 24px !important;
  overflow: hidden;
  border: 1px solid rgba(212, 163, 115, 0.2) !important;
  box-shadow: 0 20px 40px rgba(58, 36, 26, 0.15) !important;
  padding: 0 !important;
}

.mini-cart-popover .el-popover__title {
  display: none;
}

.mini-cart-content {
  background: #FDF8F2;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 20px 20px 16px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.2);
}

.cart-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 20px;
  font-weight: 500;
  color: #1E1B1A;
  margin: 0;
}

.item-count {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: #5C3A2A;
}

.empty-cart {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.4;
  display: block;
  margin-bottom: 16px;
}

.empty-cart p {
  margin: 8px 0;
  color: #5C3A2A;
  font-family: 'DM Sans', sans-serif;
}

.empty-hint {
  font-size: 13px;
  opacity: 0.7;
}

.cart-items-list {
  max-height: 320px;
  overflow-y: auto;
  padding: 8px 16px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid rgba(212, 163, 115, 0.1);
}

.cart-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  object-fit: cover;
  margin-right: 14px;
}

.item-details {
  flex: 1;
}

.item-name {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #1E1B1A;
  margin-bottom: 6px;
  display: block;
}

.item-specs {
  display: flex;
  gap: 6px;
}

.spec-tag {
  font-family: 'DM Sans', sans-serif;
  font-size: 11px;
  background: rgba(212, 163, 115, 0.1);
  padding: 2px 8px;
  border-radius: 20px;
  color: #5C3A2A;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 数量控制组件样式 */
.quantity-control {
  display: flex;
  align-items: center;
  background: rgba(212, 163, 115, 0.08);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid rgba(212, 163, 115, 0.2);
}

.qty-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #5C3A2A;
}

.qty-btn:hover:not(:disabled) {
  background: rgba(212, 163, 115, 0.15);
}

.qty-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-btn svg {
  width: 14px;
  height: 14px;
}

.qty-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #1E1B1A;
  min-width: 24px;
  text-align: center;
  padding: 0 4px;
}

.item-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 600;
  color: #C8795C;
}

.remove-item-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #B0A090;
  cursor: pointer;
  transition: all 0.2s ease;
}

.remove-item-btn:hover {
  background: rgba(200, 121, 92, 0.1);
  color: #C8795C;
}

.remove-item-btn svg {
  width: 16px;
  height: 16px;
}

.cart-footer {
  padding: 20px;
  border-top: 1px solid rgba(212, 163, 115, 0.2);
}

.cart-total {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.total-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: #5C3A2A;
}

.total-value {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 26px;
  font-weight: 600;
  color: #C8795C;
}

.checkout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 24px;
  background: linear-gradient(135deg, #3A241A, #5C3A2A);
  border: none;
  border-radius: 40px;
  color: #FDF8F2;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.checkout-btn:hover {
  background: linear-gradient(135deg, #1E1B1A, #3A241A);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(58, 36, 26, 0.2);
}

.checkout-btn svg {
  width: 18px;
  height: 18px;
}

.cart-items-list::-webkit-scrollbar {
  width: 4px;
}

.cart-items-list::-webkit-scrollbar-track {
  background: rgba(212, 163, 115, 0.1);
  border-radius: 10px;
}

.cart-items-list::-webkit-scrollbar-thumb {
  background: #D4A373;
  border-radius: 10px;
}
</style>