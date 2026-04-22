<template>
  <div class="register-page">
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

      <!-- 注册卡片 -->
      <div class="register-card">
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
          <p class="brand-slogan">加入我们 · 开启咖啡之旅</p>
        </div>

        <!-- 注册表单区域 -->
        <div class="form-section">
          <h2 class="form-title">创建账户</h2>
          <p class="form-subtitle">填写信息，注册成为用户</p>

          <!-- 头像上传 -->
          <div class="avatar-section">
            <div class="avatar-upload" @click="triggerFileInput">
              <input 
                type="file" 
                ref="fileInputRef" 
                accept="image/*" 
                style="display: none" 
                @change="handleAvatarChange"
              />
              <div class="avatar-wrapper">
                <img v-if="avatarUrl" :src="avatarUrl" class="avatar-image" alt="头像" />
                <div v-else class="avatar-placeholder">
                  <span class="placeholder-icon">📷</span>
                </div>
                <div class="avatar-overlay">
                  <span class="overlay-icon">{{ avatarUrl ? '🔄' : '📷' }}</span>
                </div>
              </div>
            </div>
            <p class="avatar-hint">点击上传头像（可选）</p>
          </div>

          <form @submit.prevent="handleRegister" class="register-form">
            <!-- 用户名 -->
            <div class="form-group" :class="{ 'has-error': errors.username }">
              <div class="input-wrapper">
                <span class="input-icon">👤</span>
                <input 
                  type="text" 
                  v-model="registerForm.username" 
                  placeholder="用户名" 
                  class="form-input"
                  @blur="validateUsername"
                  @input="errors.username = ''"
                />
              </div>
              <span class="error-message" v-if="errors.username">{{ errors.username }}</span>
            </div>

            <!-- 手机号 -->
            <div class="form-group" :class="{ 'has-error': errors.phone }">
              <div class="input-wrapper">
                <span class="input-icon">📱</span>
                <input 
                  type="tel" 
                  v-model="registerForm.phone" 
                  placeholder="手机号" 
                  class="form-input"
                  @blur="validatePhone"
                  @input="errors.phone = ''"
                />
              </div>
              <span class="error-message" v-if="errors.phone">{{ errors.phone }}</span>
            </div>

            <!-- 密码 -->
            <div class="form-group" :class="{ 'has-error': errors.password }">
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  v-model="registerForm.password" 
                  placeholder="密码（6-20位）" 
                  class="form-input"
                  @blur="validatePassword"
                  @input="errors.password = ''"
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

            <!-- 确认密码 -->
            <div class="form-group" :class="{ 'has-error': errors.confirmPassword }">
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <input 
                  :type="showConfirmPassword ? 'text' : 'password'" 
                  v-model="registerForm.confirmPassword" 
                  placeholder="确认密码" 
                  class="form-input"
                  @blur="validateConfirmPassword"
                  @input="errors.confirmPassword = ''"
                />
                <button 
                  type="button" 
                  class="password-toggle" 
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  <svg v-if="showConfirmPassword" viewBox="0 0 24 24" fill="none">
                    <path d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z" stroke="currentColor" stroke-width="1.5"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none">
                    <path d="M3 3L21 21M10.584 10.587C9.811 11.022 9.289 11.86 9.289 12.813C9.289 14.315 10.498 15.524 12 15.524C12.953 15.524 13.791 15.002 14.226 14.229M9.887 6.653C10.556 6.241 11.276 6 12 6C19 6 23 12 23 12C23 12 21.222 15.057 18.439 17.434M16.5 9.5C17.394 10.081 18.104 10.964 18.575 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
              <span class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</span>
            </div>

            <!-- 性别 -->
            <div class="form-group">
              <label class="form-label">性别</label>
              <div class="gender-options">
                <div 
                  class="gender-option" 
                  :class="{ active: registerForm.gender === '男' }"
                  @click="registerForm.gender = '男'"
                >
                  <span class="gender-icon">♂️</span>
                  <span class="gender-text">男</span>
                </div>
                <div 
                  class="gender-option" 
                  :class="{ active: registerForm.gender === '女' }"
                  @click="registerForm.gender = '女'"
                >
                  <span class="gender-icon">♀️</span>
                  <span class="gender-text">女</span>
                </div>
              </div>
            </div>

            <!-- 角色 -->
            <div class="form-group">
              <label class="form-label">注册身份</label>
              <div class="role-options">
                <div 
                  class="role-option" 
                  :class="{ active: registerForm.role === 1 }"
                  @click="registerForm.role = 1"
                >
                  <span class="role-icon">👤</span>
                  <span class="role-text">顾客</span>
                  <span class="role-desc">浏览菜单 · 下单购买</span>
                </div>
                <div 
                  class="role-option" 
                  :class="{ active: registerForm.role === 2 }"
                  @click="registerForm.role = 2"
                >
                  <span class="role-icon">🛎️</span>
                  <span class="role-text">店员</span>
                  <span class="role-desc">处理订单 · 服务顾客</span>
                </div>
              </div>
              <span class="error-message" v-if="errors.role">{{ errors.role }}</span>
            </div>

            <!-- 注册按钮 -->
            <button 
              type="submit" 
              class="register-btn" 
              :class="{ 'is-loading': loading }"
              :disabled="loading"
            >
              <span v-if="!loading">注册</span>
              <span v-else class="loading-dots">
                <span></span><span></span><span></span>
              </span>
              <svg v-if="!loading" viewBox="0 0 24 24" fill="none" class="btn-arrow">
                <path d="M5 12H19M19 12L13 6M19 12L13 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>

            <!-- 登录链接 -->
            <div class="login-link">
              <span class="link-text">已有账号？</span>
              <button type="button" class="link-btn" @click="$router.push('/login')">
                立即登录
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
        <p class="quote-text">"每一杯咖啡 · 都是独特的故事"</p>
        <p class="quote-author">— 咖啡工坊</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const avatarUrl = ref('')
    const selectedFile = ref(null)
    const showPassword = ref(false)
    const showConfirmPassword = ref(false)
    const fileInputRef = ref(null)
    const cursorRef = ref(null)

    const registerForm = reactive({
      username: '',
      phone: '',
      password: '',
      confirmPassword: '',
      gender: '男',
      role: 1
    })

    const errors = reactive({
      username: '',
      phone: '',
      password: '',
      confirmPassword: '',
      role: ''
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

    const triggerFileInput = () => {
      fileInputRef.value?.click()
    }

    const handleAvatarChange = (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      if (!file.type.startsWith('image/')) {
        ElMessage.error('请选择图片文件')
        return
      }
      
      const reader = new FileReader()
      reader.onload = (e) => {
        avatarUrl.value = e.target.result
      }
      reader.readAsDataURL(file)
      selectedFile.value = file
    }

    const validateUsername = () => {
      if (!registerForm.username) {
        errors.username = '请输入用户名'
        return false
      } else if (registerForm.username.length < 2 || registerForm.username.length > 20) {
        errors.username = '用户名长度在2到20个字符'
        return false
      }
      errors.username = ''
      return true
    }

    const validatePhone = () => {
      if (!registerForm.phone) {
        errors.phone = '请输入手机号'
        return false
      } else if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
        errors.phone = '请输入正确的手机号'
        return false
      }
      errors.phone = ''
      return true
    }

    const validatePassword = () => {
      if (!registerForm.password) {
        errors.password = '请输入密码'
        return false
      } else if (registerForm.password.length < 6 || registerForm.password.length > 20) {
        errors.password = '密码长度在6到20个字符'
        return false
      }
      errors.password = ''
      return true
    }

    const validateConfirmPassword = () => {
      if (!registerForm.confirmPassword) {
        errors.confirmPassword = '请再次输入密码'
        return false
      } else if (registerForm.confirmPassword !== registerForm.password) {
        errors.confirmPassword = '两次输入的密码不一致'
        return false
      }
      errors.confirmPassword = ''
      return true
    }

    const validateRole = () => {
      if (!registerForm.role) {
        errors.role = '请选择注册身份'
        return false
      }
      errors.role = ''
      return true
    }

    const validateForm = () => {
      const isUsernameValid = validateUsername()
      const isPhoneValid = validatePhone()
      const isPasswordValid = validatePassword()
      const isConfirmPasswordValid = validateConfirmPassword()
      const isRoleValid = validateRole()
      return isUsernameValid && isPhoneValid && isPasswordValid && isConfirmPasswordValid && isRoleValid
    }

    const handleRegister = async () => {
      if (!validateForm()) return

      try {
        loading.value = true

        const formData = new FormData()
        formData.append('username', registerForm.username)
        formData.append('phone', registerForm.phone)
        formData.append('password', registerForm.password)
        formData.append('gender', registerForm.gender)
        formData.append('role', registerForm.role)
        
        if (selectedFile.value) {
          formData.append('headImage', selectedFile.value)
        }

        const response = await register(formData)
        
        if (response.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(response.message || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error(error.response?.data?.message || '注册失败')
      } finally {
        loading.value = false
      }
    }

    return {
      registerForm,
      errors,
      loading,
      avatarUrl,
      cursorRef,
      fileInputRef,
      showPassword,
      showConfirmPassword,
      triggerFileInput,
      handleAvatarChange,
      validateUsername,
      validatePhone,
      validatePassword,
      validateConfirmPassword,
      handleRegister
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.register-page {
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
  overflow: hidden;
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
  max-width: 1100px;
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

/* ===== 注册卡片 ===== */
.register-card {
  position: relative;
  z-index: 2;
  display: flex;
  width: 950px;
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
  padding: 48px 32px;
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.08), rgba(200, 121, 92, 0.04));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  border-right: 1px solid rgba(212, 163, 115, 0.15);
}

.brand-icon {
  font-size: 64px;
  margin-bottom: 12px;
  animation: steamFloat 3s ease-in-out infinite;
}

.brand-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 38px;
  font-weight: 600;
  color: var(--dark-roast);
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.brand-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  letter-spacing: 4px;
  text-transform: uppercase;
  margin: 0 0 20px 0;
}

.brand-decoration {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
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

.brand-slogan {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 0;
}

/* ===== 表单区域 ===== */
.form-section {
  flex: 1.2;
  padding: 40px 36px;
  background: rgba(255, 255, 255, 0.3);
}

.form-title {
  font-family: 'Playfair Display', serif;
  font-size: 28px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0 0 4px 0;
}

.form-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.8;
  margin: 0 0 24px 0;
}

/* 头像上传 */
.avatar-section {
  text-align: center;
  margin-bottom: 24px;
}

.avatar-upload {
  cursor: pointer;
  display: inline-block;
}

.avatar-wrapper {
  position: relative;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(58, 36, 26, 0.12);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: scale(1.02);
  box-shadow: 0 10px 28px rgba(58, 36, 26, 0.18);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--cream), var(--crema));
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed rgba(212, 163, 115, 0.4);
  border-radius: 50%;
}

.placeholder-icon {
  font-size: 32px;
  opacity: 0.5;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(30, 27, 26, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.overlay-icon {
  font-size: 24px;
  color: var(--crema);
}

.avatar-hint {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  opacity: 0.6;
  margin: 8px 0 0 0;
}

/* 表单样式 */
.register-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-label {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--warm-brown);
  margin-left: 4px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  font-size: 16px;
  opacity: 0.6;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 14px 44px 14px 46px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 18px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--espresso);
  outline: none;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: var(--golden);
  box-shadow: 0 0 0 3px rgba(212, 163, 115, 0.1);
  background: white;
}

.form-input::placeholder {
  color: var(--warm-brown);
  opacity: 0.4;
}

.has-error .form-input {
  border-color: var(--terracotta);
}

.error-message {
  font-family: 'DM Sans', sans-serif;
  font-size: 11px;
  color: var(--terracotta);
  padding-left: 8px;
}

.password-toggle {
  position: absolute;
  right: 10px;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 10px;
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
  width: 18px;
  height: 18px;
}

/* 性别选择 */
.gender-options {
  display: flex;
  gap: 12px;
}

.gender-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.gender-option:hover {
  background: rgba(212, 163, 115, 0.08);
  border-color: var(--golden);
}

.gender-option.active {
  background: var(--dark-roast);
  border-color: var(--dark-roast);
}

.gender-option.active .gender-text {
  color: var(--crema);
}

.gender-icon {
  font-size: 18px;
}

.gender-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--warm-brown);
  transition: color 0.3s ease;
}

/* 角色选择 */
.role-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.role-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.role-option:hover {
  background: rgba(212, 163, 115, 0.08);
  border-color: var(--golden);
}

.role-option.active {
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.12), rgba(200, 121, 92, 0.06));
  border-color: var(--golden);
}

.role-icon {
  font-size: 22px;
}

.role-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 600;
  color: var(--espresso);
}

.role-desc {
  margin-left: auto;
  font-family: 'DM Sans', sans-serif;
  font-size: 11px;
  color: var(--warm-brown);
  opacity: 0.7;
}

/* 注册按钮 */
.register-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 15px 28px;
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
  box-shadow: 0 6px 20px rgba(58, 36, 26, 0.18);
}

.register-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(58, 36, 26, 0.22);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-arrow {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.register-btn:hover:not(:disabled) .btn-arrow {
  transform: translateX(4px);
}

/* 登录链接 */
.login-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 12px;
}

.link-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
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
  font-size: 13px;
  font-weight: 600;
  color: var(--terracotta);
  cursor: pointer;
  transition: all 0.3s ease;
}

.link-btn:hover {
  color: var(--dark-roast);
}

.link-btn svg {
  width: 14px;
  height: 14px;
  transition: transform 0.3s ease;
}

.link-btn:hover svg {
  transform: translateX(3px);
}

/* 加载动画 */
.loading-dots {
  display: flex;
  gap: 5px;
  justify-content: center;
}

.loading-dots span {
  width: 7px;
  height: 7px;
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
  margin-top: 28px;
}

.back-home-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
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
  width: 16px;
  height: 16px;
}

/* ===== 底部引用 ===== */
.footer-quote {
  position: relative;
  z-index: 2;
  margin-top: 32px;
  text-align: center;
}

.quote-text {
  font-family: 'Playfair Display', serif;
  font-size: 15px;
  font-style: italic;
  color: var(--warm-brown);
  margin: 0 0 4px 0;
  opacity: 0.6;
}

.quote-author {
  font-family: 'DM Sans', sans-serif;
  font-size: 11px;
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
  .register-card {
    flex-direction: column;
    width: 100%;
  }

  .brand-section {
    padding: 36px 24px;
    border-right: none;
    border-bottom: 1px solid rgba(212, 163, 115, 0.15);
  }

  .brand-title {
    font-size: 32px;
  }

  .form-section {
    padding: 36px 24px;
  }

  .form-title {
    font-size: 24px;
  }

  .page-container {
    padding: 20px 16px;
  }

  .role-option {
    padding: 12px 14px;
  }

  .role-desc {
    display: none;
  }
}

@media (max-width: 480px) {
  .brand-section {
    padding: 28px 16px;
  }

  .form-section {
    padding: 28px 16px;
  }

  .brand-icon {
    font-size: 52px;
  }

  .brand-title {
    font-size: 28px;
  }

  .gender-options {
    flex-direction: column;
  }

  .footer-quote {
    margin-top: 20px;
  }
}
</style>