<template>
  <div class="coffee-card" :class="{ 'out-of-stock': isOutOfStock }" :id="`coffee-${coffee.id}`">
    <div class="card-image-wrapper">
      <img :src="coffee.coffeeImage" :alt="coffee.name" class="card-image" />
      <div class="image-overlay"></div>
      <div v-if="isOutOfStock" class="stock-badge sold-out">售罄</div>
      <div v-else-if="isLowStock" class="stock-badge low-stock">仅剩 {{ coffee.stock }} 件</div>
    </div>
    
    <div class="card-content">
      <div class="card-header">
        <h3 class="coffee-name">{{ coffee.name }}</h3>
        <span class="coffee-price">¥{{ coffee.price }}</span>
      </div>
      
      <p class="coffee-description">{{ coffee.description }}</p>
      
      <div class="card-options">
        <div class="option-row">
          <span class="option-label">甜度</span>
          <div class="option-buttons">
            <button 
              v-for="val in [1, 2, 3]" 
              :key="val"
              class="option-btn"
              :class="{ active: localOptions.sweet === val }"
              @click="localOptions.sweet = val"
            >
              {{ getSugarText(val) }}
            </button>
          </div>
        </div>
        
        <div class="option-row">
          <span class="option-label">温度</span>
          <div class="option-buttons">
            <button 
              v-for="val in [1, 2, 3, 4]" 
              :key="val"
              class="option-btn"
              :class="{ active: localOptions.temperature === val }"
              @click="localOptions.temperature = val"
            >
              <span class="temp-icon">{{ getTempIcon(val) }}</span>
              {{ getTempText(val) }}
            </button>
          </div>
        </div>
      </div>
      
      <div class="card-footer">
        <div class="quantity-control">
          <button 
            class="qty-btn"
            @click="decrementQuantity"
            :disabled="localOptions.quantity <= 1"
          >−</button>
          <span class="qty-value">{{ localOptions.quantity }}</span>
          <button 
            class="qty-btn"
            @click="incrementQuantity"
            :disabled="localOptions.quantity >= coffee.stock"
          >+</button>
        </div>
        
        <button 
          class="add-to-cart-btn"
          :class="{ disabled: isOutOfStock }"
          :disabled="isOutOfStock"
          @click="handleAddToCart"
        >
          <span class="btn-price">¥{{ totalPrice }}</span>
          <span class="btn-text">{{ isOutOfStock ? '已售罄' : '加入购物车' }}</span>
          <svg viewBox="0 0 24 24" fill="none" class="btn-icon">
            <path d="M3 3H5L5.4 5M7 13H17L21 5H5.4M7 13L5.4 5M7 13L4.7 15.3C4.3 15.7 4.6 16.5 5.1 16.5H17M17 13V19C17 19.5 16.5 20 16 20H8C7.5 20 7 19.5 7 19V13M17 13H7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'CoffeeCard',
  props: {
    coffee: {
      type: Object,
      required: true
    },
    selectedOptions: {
      type: Object,
      default: () => ({})
    },
    isLoggedIn: {
      type: Boolean,
      default: false
    }
  },
  emits: ['add-to-cart', 'login-required'],
  setup(props, { emit }) {
    const localOptions = ref({
      quantity: props.selectedOptions?.quantity || 1,
      sweet: props.selectedOptions?.sweet || 1,
      temperature: props.selectedOptions?.temperature || 1
    })

    watch(() => props.selectedOptions, (newVal) => {
      if (newVal) {
        localOptions.value = { 
          quantity: newVal.quantity || 1,
          sweet: newVal.sweet || 1,
          temperature: newVal.temperature || 1
        }
      }
    }, { deep: true })

    const getSugarText = (val) => {
      const map = { 1: '正常糖', 2: '少糖', 3: '不加糖' }
      return map[val] || '正常糖'
    }

    const getTempText = (val) => {
      const map = { 1: '烫', 2: '温热', 3: '少冰', 4: '正常冰' }
      return map[val] || '烫'
    }

    const getTempIcon = (val) => {
      const map = { 1: '🔥', 2: '☀️', 3: '❄️', 4: '🧊' }
      return map[val] || '☕'
    }

    const decrementQuantity = () => {
      if (localOptions.value.quantity > 1) {
        localOptions.value.quantity--
      }
    }

    const incrementQuantity = () => {
      if (localOptions.value.quantity < props.coffee.stock) {
        localOptions.value.quantity++
      }
    }

    const handleAddToCart = () => {
      if (!props.isLoggedIn) {
        emit('login-required')
        return
      }
      emit('add-to-cart', {
        coffee: props.coffee,
        options: { ...localOptions.value }
      })
    }

    const isOutOfStock = computed(() => props.coffee.stock <= 0)
    const isLowStock = computed(() => props.coffee.stock > 0 && props.coffee.stock <= 5)
    const totalPrice = computed(() => (props.coffee.price * localOptions.value.quantity).toFixed(2))

    return {
      localOptions,
      getSugarText,
      getTempText,
      getTempIcon,
      decrementQuantity,
      incrementQuantity,
      handleAddToCart,
      isOutOfStock,
      isLowStock,
      totalPrice
    }
  }
}
</script>

<style scoped>
/* ===== 高亮效果 ===== */
.coffee-card.highlighted {
  animation: highlightPulse 1s ease-in-out;
  box-shadow: 0 0 20px rgba(212, 163, 115, 0.5);
  border: 2px solid var(--golden);
}

@keyframes highlightPulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 rgba(212, 163, 115, 0.4);
  }
  50% {
    transform: scale(1.02);
    box-shadow: 0 0 20px rgba(212, 163, 115, 0.8);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 rgba(212, 163, 115, 0.4);
  }
}

.coffee-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 24px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 8px 30px rgba(58, 36, 26, 0.08);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.coffee-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(58, 36, 26, 0.12);
  border-color: rgba(212, 163, 115, 0.35);
  background: rgba(255, 255, 255, 0.8);
}

.coffee-card.out-of-stock {
  opacity: 0.7;
}

.card-image-wrapper {
  position: relative;
  height: 300px;
  overflow: hidden;
  flex-shrink: 0;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.coffee-card:hover .card-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 50%, rgba(30, 27, 26, 0.3));
}

.stock-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 6px 12px;
  border-radius: 30px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(4px);
  z-index: 2;
}

.stock-badge.sold-out {
  background: rgba(200, 121, 92, 0.9);
  color: #FDF8F2;
}

.stock-badge.low-stock {
  background: rgba(232, 184, 109, 0.9);
  color: #3A241A;
}

.card-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 8px;
}

.coffee-name {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 20px;
  font-weight: 600;
  color: #1E1B1A;
  margin: 0;
}

.coffee-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 18px;
  font-weight: 600;
  color: #C8795C;
}

.coffee-description {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: #5C3A2A;
  margin: 0 0 16px 0;
  line-height: 1.5;
  opacity: 0.8;
}

.card-options {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 20px;
}

.option-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #5C3A2A;
  opacity: 0.7;
}

.option-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.option-btn {
  padding: 6px 12px;
  border: 1px solid rgba(212, 163, 115, 0.3);
  border-radius: 30px;
  background: transparent;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: #5C3A2A;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.option-btn:hover {
  background: rgba(212, 163, 115, 0.1);
  border-color: #D4A373;
}

.option-btn.active {
  background: #3A241A;
  border-color: #3A241A;
  color: #FDF8F2;
}

.temp-icon {
  font-size: 10px;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: auto;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #FFF5E6;
  padding: 4px;
  border-radius: 30px;
  border: 1px solid rgba(212, 163, 115, 0.2);
}

.qty-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 50%;
  font-size: 18px;
  font-weight: 300;
  color: #3A241A;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-btn:hover:not(:disabled) {
  background: #D4A373;
  color: #FDF8F2;
}

.qty-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: #1E1B1A;
  min-width: 24px;
  text-align: center;
}

.add-to-cart-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #3A241A, #5C3A2A);
  border: none;
  border-radius: 40px;
  color: #FDF8F2;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(58, 36, 26, 0.15);
}

.add-to-cart-btn:hover:not(.disabled) {
  background: linear-gradient(135deg, #1E1B1A, #3A241A);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(58, 36, 26, 0.2);
}

.add-to-cart-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-price {
  font-weight: 600;
}

.btn-icon {
  width: 18px;
  height: 18px;
}
</style>