<template>
  <div class="login-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 装饰性元素 -->
      <div class="decorative-elements">
        <div class="decorative-circle decorative-circle--1"></div>
        <div class="decorative-circle decorative-circle--2"></div>
        <div class="decorative-circle decorative-circle--3"></div>
        <div class="decorative-line decorative-line--1"></div>
        <div class="decorative-line decorative-line--2"></div>
      </div>

      <!-- 登录卡片 -->
      <div class="login-card">
        <!-- 品牌区域 -->
        <div class="brand-section">
          <div class="brand-icon">☕</div>
          <h1 class="brand-title">咖啡工坊</h1>
          <p class="brand-subtitle">Caffè Artigianale</p>
          <div class="brand-decoration">
            <span class="decoration-dot"></span>
            <span class="decoration-line"></span>
            <span class="decoration-dot"></span>
          </div>
        </div>

        <!-- 登录表单 -->
        <div class="form-section">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录您的账户，继续品味咖啡时光</p>

          <form @submit.prevent="handleLogin" class="login-form">
            <!-- 手机号输入框 -->
            <div class="form-group" :class="{ 'has-error': errors.phone }">
              <label class="form-label">手机号</label>
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M22 12V17C22 19.2091 20.2091 21 18 21H6C3.79086 21 2 19.2091 2 17V7C2 4.79086 3.79086 3 6 3H12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <path d="M15 3L21 9M21 3L15 9" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M7 9H9M7 13H13M7 17H17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </span>
                <input 
                  type="tel" 
                  v-model="loginForm.phone" 
                  placeholder="请输入手机号" 
                  class="form-input"
                  @blur="validatePhone"
                  @input="errors.phone = ''"
                />
              </div>
              <span class="error-message" v-if="errors.phone">{{ errors.phone }}</span>
            </div>

            <!-- 密码输入框 -->
            <div class="form-group" :class="{ 'has-error': errors.password }">
              <label class="form-label">密码</label>
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M12 15V17M8 11V8C8 5.79086 9.79086 4 12 4C14.2091 4 16 5.79086 16 8V11M6 21H18C19.1046 21 20 20.1046 20 19V13C20 11.8954 19.1046 11 18 11H6C4.89543 11 4 11.8954 4 13V19C4 20.1046 4.89543 21 6 21Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                    <circle cx="12" cy="16" r="1" fill="currentColor"/>
                  </svg>
                </span>
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  v-model="loginForm.password" 
                  placeholder="请输入密码" 
                  class="form-input"
                  @blur="validatePassword"
                  @input="errors.password = ''"
                  @keyup.enter="handleLogin"
                />
                <button 
                  type="button" 
                  class="password-toggle" 
                  @click="showPassword = !showPassword"
                >
                  <svg v-if="showPassword" viewBox="0 0 24 24" fill="none">
                    <path d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z" stroke="currentColor" stroke-width="1.5"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none">
                    <path d="M3 3L21 21M10.584 10.587C9.811 11.022 9.289 11.86 9.289 12.813C9.289 14.315 10.498 15.524 12 15.524C12.953 15.524 13.791 15.002 14.226 14.229M9.887 6.653C10.556 6.241 11.276 6 12 6C19 6 23 12 23 12C23 12 21.222 15.057 18.439 17.434M16.5 9.5C17.394 10.081 18.104 10.964 18.575 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
              <span class="error-message" v-if="errors.password">{{ errors.password }}</span>
            </div>

            <!-- 登录按钮 -->
            <button 
              type="submit" 
              class="login-btn" 
              :class="{ 'is-loading': loading }"
              :disabled="loading"
            >
              <span v-if="!loading">登录</span>
              <span v-else class="loading-dots">
                <span></span><span></span><span></span>
              </span>
              <svg v-if="!loading" viewBox="0 0 24 24" fill="none" class="btn-arrow">
                <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>

            <!-- 注册链接 -->
            <div class="register-link">
              <span class="link-text">还没有账号？</span>
              <button type="button" class="link-btn" @click="$router.push('/register')">
                立即注册
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 返回首页链接 -->
      <div class="back-home">
        <button class="back-home-btn" @click="$router.push('/')">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>返回首页</span>
        </button>
      </div>

      <!-- 装饰性引用 -->
      <div class="footer-quote">
        <p class="quote-text">"咖啡是液体阳光"</p>
        <p class="quote-author">— 意大利谚语</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const showPassword = ref(false)
    const cursorRef = ref(null)

    const loginForm = reactive({
      phone: '',
      password: ''
    })

    const errors = reactive({
      phone: '',
      password: ''
    })

    onMounted(() => {
      initCustomCursor()
    })

    onUnmounted(() => {
      window.removeEventListener('mousemove', handleMouseMove)
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

    const validatePhone = () => {
      if (!loginForm.phone) {
        errors.phone = '请输入手机号'
        return false
      } else if (!/^1[3-9]\d{9}$/.test(loginForm.phone)) {
        errors.phone = '请输入正确的手机号'
        return false
      }
      errors.phone = ''
      return true
    }

    const validatePassword = () => {
      if (!loginForm.password) {
        errors.password = '请输入密码'
        return false
      } else if (loginForm.password.length < 6 || loginForm.password.length > 20) {
        errors.password = '密码长度在6到20个字符'
        return false
      }
      errors.password = ''
      return true
    }

    const validateForm = () => {
      const isPhoneValid = validatePhone()
      const isPasswordValid = validatePassword()
      return isPhoneValid && isPasswordValid
    }

    const handleLogin = async () => {
      if (!validateForm()) return

      try {
        loading.value = true

        const response = await login(loginForm)
        
        if (response.code === 200) {
          localStorage.setItem('userInfo', JSON.stringify(response.data))
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }

    return {
      loginForm,
      errors,
      loading,
      showPassword,
      cursorRef,
      validatePhone,
      validatePassword,
      handleLogin
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.login-page {
  --espresso: #1E1B1A;
  --dark-roast: #3A241A;
  --warm-brown: #5C3A2A;
  --crema: #FDF8F2;
  --cream: #FFF5E6;
  --golden: #D4A373;
  --honey: #E8B86D;
  --terracotta: #C8795C;
  --sage: #7D9B7A;
  
  min-height: 100vh;
  background-color: var(--crema);
  position: relative;
  overflow-x: hidden;
  font-family: 'DM Sans', 'Quicksand', sans-serif;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ===== 背景纹理 ===== */
.background-texture {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-radial-gradient(circle at 20% 30%, rgba(212, 163, 115, 0.03) 0px, transparent 2px),
    repeating-linear-gradient(45deg, rgba(58, 36, 26, 0.02) 0px, rgba(58, 36, 26, 0.02) 1px, transparent 1px, transparent 8px);
  pointer-events: none;
  z-index: 0;
}

.background-gradient {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 80% 20%, rgba(232, 184, 109, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 10% 80%, rgba(125, 155, 122, 0.06) 0%, transparent 50%);
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
  width: 100%;
  max-width: 1200px;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ===== 装饰元素 ===== */
.decorative-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.decorative-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.decorative-circle--1 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--golden), transparent);
  top: -100px;
  left: -100px;
}

.decorative-circle--2 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, var(--terracotta), transparent);
  bottom: -50px;
  right: -50px;
}

.decorative-circle--3 {
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, var(--sage), transparent);
  top: 50%;
  right: 10%;
  opacity: 0.05;
}

.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.15;
}

.decorative-line--1 {
  top: 20%;
  left: 5%;
  width: 200px;
  height: 1px;
  transform: rotate(-25deg);
}

.decorative-line--2 {
  bottom: 30%;
  right: 8%;
  width: 250px;
  height: 1px;
  transform: rotate(15deg);
}

/* ===== 登录卡片 ===== */
.login-card {
  position: relative;
  z-index: 2;
  display: flex;
  width: 900px;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 40px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(58, 36, 26, 0.12);
  opacity: 0;
  animation: cardReveal 0.8s ease forwards;
}

/* ===== 品牌区域 ===== */
.brand-section {
  flex: 1;
  padding: 48px 40px;
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.08), rgba(200, 121, 92, 0.04));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  border-right: 1px solid rgba(212, 163, 115, 0.15);
}

.brand-icon {
  font-size: 72px;
  margin-bottom: 16px;
  animation: steamFloat 3s ease-in-out infinite;
}

.brand-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 42px;
  font-weight: 600;
  color: var(--dark-roast);
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.brand-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  letter-spacing: 4px;
  text-transform: uppercase;
  margin: 0 0 24px 0;
}

.brand-decoration {
  display: flex;
  align-items: center;
  gap: 12px;
}

.decoration-dot {
  width: 6px;
  height: 6px;
  background: var(--golden);
  border-radius: 50%;
}

.decoration-line {
  width: 60px;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
}

/* ===== 表单区域 ===== */
.form-section {
  flex: 1;
  padding: 48px 40px;
  background: rgba(255, 255, 255, 0.3);
}

.form-title {
  font-family: 'Playfair Display', serif;
  font-size: 32px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  opacity: 0.8;
  margin: 0 0 32px 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--warm-brown);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  display: flex;
  align-items: center;
  color: var(--warm-brown);
  opacity: 0.5;
  transition: opacity 0.3s ease;
}

.input-wrapper:focus-within .input-icon {
  opacity: 1;
  color: var(--golden);
}

.input-icon svg {
  width: 20px;
  height: 20px;
}

.form-input {
  width: 100%;
  padding: 16px 48px 16px 52px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--espresso);
  outline: none;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: var(--golden);
  box-shadow: 0 0 0 4px rgba(212, 163, 115, 0.1);
  background: white;
}

.form-input::placeholder {
  color: var(--warm-brown);
  opacity: 0.4;
}

.has-error .form-input {
  border-color: var(--terracotta);
}

.password-toggle {
  position: absolute;
  right: 12px;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--warm-brown);
  opacity: 0.5;
  cursor: pointer;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  opacity: 1;
  background: rgba(212, 163, 115, 0.1);
}

.password-toggle svg {
  width: 20px;
  height: 20px;
}

.error-message {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--terracotta);
  padding-left: 8px;
}

.login-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 32px;
  margin-top: 8px;
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  border: none;
  border-radius: 40px;
  color: var(--crema);
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 24px rgba(58, 36, 26, 0.2);
}

.login-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(58, 36, 26, 0.25);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-arrow {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.login-btn:hover:not(:disabled) .btn-arrow {
  transform: translateX(4px);
}

.register-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
}

.link-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  opacity: 0.7;
}

.link-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: transparent;
  border: none;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: var(--terracotta);
  cursor: pointer;
  transition: all 0.3s ease;
}

.link-btn:hover {
  color: var(--dark-roast);
}

.link-btn svg {
  width: 16px;
  height: 16px;
  transition: transform 0.3s ease;
}

.link-btn:hover svg {
  transform: translateX(3px);
}

.loading-dots {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: var(--crema);
  border-radius: 50%;
  animation: dotPulse 1.4s ease-in-out infinite;
}

.loading-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.loading-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

/* ===== 返回首页 ===== */
.back-home {
  position: relative;
  z-index: 2;
  margin-top: 32px;
}

.back-home-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-home-btn:hover {
  background: rgba(255, 255, 255, 0.8);
  border-color: var(--golden);
  color: var(--dark-roast);
}

.back-home-btn svg {
  width: 18px;
  height: 18px;
}

/* ===== 底部引用 ===== */
.footer-quote {
  position: relative;
  z-index: 2;
  margin-top: 40px;
  text-align: center;
}

.quote-text {
  font-family: 'Playfair Display', serif;
  font-size: 16px;
  font-style: italic;
  color: var(--warm-brown);
  margin: 0 0 4px 0;
  opacity: 0.6;
}

.quote-author {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  opacity: 0.4;
  margin: 0;
}

/* ===== 动画 ===== */
@keyframes cardReveal {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes steamFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

@keyframes dotPulse {
  0%, 60%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  30% {
    opacity: 1;
    transform: scale(1.3);
  }
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 100%;
  }

  .brand-section {
    padding: 40px 30px;
    border-right: none;
    border-bottom: 1px solid rgba(212, 163, 115, 0.15);
  }

  .brand-title {
    font-size: 32px;
  }

  .form-section {
    padding: 40px 30px;
  }

  .form-title {
    font-size: 28px;
  }

  .page-container {
    padding: 24px 16px;
  }
}

@media (max-width: 480px) {
  .brand-section {
    padding: 32px 20px;
  }

  .form-section {
    padding: 32px 20px;
  }

  .brand-icon {
    font-size: 56px;
  }

  .brand-title {
    font-size: 28px;
  }

  .form-title {
    font-size: 24px;
  }

  .form-input {
    padding: 14px 44px 14px 48px;
  }

  .footer-quote {
    margin-top: 24px;
  }
}
</style>