<template>
  <div class="add-admin-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">添加管理员</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <template v-if="isLoggedIn && userInfo.role === 3">
              <router-link to="/admin/coffee" class="nav-link">
                <span class="nav-text">咖啡管理</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/admin/statistics" class="nav-link">
                <span class="nav-text">数据统计</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/admin/add-admin" class="nav-link active">
                <span class="nav-text">添加管理员</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <template v-if="!isLoggedIn">
              <router-link to="/login" class="nav-link nav-link--highlight">
                <span class="nav-text">登录</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            <template v-else>
              <router-link to="/profile" class="nav-link nav-link--user">
                <span class="user-avatar">{{ userInfo.username?.charAt(0) }}</span>
                <span class="nav-text">{{ userInfo.username }}</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
          </nav>
        </div>
      </header>

      <!-- 主内容区域 -->
      <main class="add-admin-main">
        <div class="add-admin-wrapper">
          <!-- 装饰性线条 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>

          <!-- 页面标题 -->
          <div class="page-header">
            <h1 class="page-title">
              <span class="title-word title-word--1">添加</span>
              <span class="title-word title-word--2">管理员</span>
            </h1>
            <p class="page-subtitle">创建新的管理员账户 · 赋予管理权限</p>
          </div>

          <!-- 表单卡片 -->
          <div class="form-card">
            <!-- 头像上传区域 -->
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

            <!-- 表单区域 -->
            <form @submit.prevent="handleAddAdmin" class="admin-form">
              <!-- 用户名 -->
              <div class="form-group" :class="{ 'has-error': errors.username }">
                <label class="form-label">
                  用户名
                  <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                  <span class="input-icon">👤</span>
                  <input 
                    type="text" 
                    v-model="adminForm.username" 
                    placeholder="请输入用户名" 
                    class="form-input"
                    @blur="validateUsername"
                    @input="errors.username = ''"
                  />
                </div>
                <span class="error-message" v-if="errors.username">{{ errors.username }}</span>
              </div>

              <!-- 手机号 -->
              <div class="form-group" :class="{ 'has-error': errors.phone }">
                <label class="form-label">
                  手机号
                  <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                  <span class="input-icon">📱</span>
                  <input 
                    type="tel" 
                    v-model="adminForm.phone" 
                    placeholder="请输入手机号" 
                    class="form-input"
                    @blur="validatePhone"
                    @input="errors.phone = ''"
                  />
                </div>
                <span class="error-message" v-if="errors.phone">{{ errors.phone }}</span>
              </div>

              <!-- 密码 -->
              <div class="form-group" :class="{ 'has-error': errors.password }">
                <label class="form-label">
                  密码
                  <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                  <span class="input-icon">🔒</span>
                  <input 
                    :type="showPassword ? 'text' : 'password'" 
                    v-model="adminForm.password" 
                    placeholder="请输入密码（6-20位）" 
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
                <label class="form-label">
                  确认密码
                  <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                  <span class="input-icon">🔒</span>
                  <input 
                    :type="showConfirmPassword ? 'text' : 'password'" 
                    v-model="adminForm.confirmPassword" 
                    placeholder="请再次输入密码" 
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
                    :class="{ active: adminForm.gender === '男' }"
                    @click="adminForm.gender = '男'"
                  >
                    <span class="gender-icon">♂️</span>
                    <span class="gender-text">男</span>
                  </div>
                  <div 
                    class="gender-option" 
                    :class="{ active: adminForm.gender === '女' }"
                    @click="adminForm.gender = '女'"
                  >
                    <span class="gender-icon">♀️</span>
                    <span class="gender-text">女</span>
                  </div>
                </div>
              </div>

              <!-- 角色（只读展示） -->
              <div class="form-group">
                <label class="form-label">角色</label>
                <div class="role-display">
                  <span class="role-icon">⚙️</span>
                  <span class="role-text">管理员</span>
                </div>
              </div>

              <!-- 提交按钮 -->
              <div class="form-actions">
                <button 
                  type="submit" 
                  class="submit-btn" 
                  :class="{ 'is-loading': loading }"
                  :disabled="loading"
                >
                  <span v-if="!loading">添加管理员</span>
                  <span v-else class="loading-dots">
                    <span></span><span></span><span></span>
                  </span>
                  <svg v-if="!loading" viewBox="0 0 24 24" fill="none" class="btn-arrow">
                    <path d="M12 4V20M20 12H4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </form>
          </div>
        </div>
      </main>

      <!-- 底部 -->
      <footer class="app-footer">
        <div class="footer-content">
          <div class="footer-brand">
            <span class="footer-logo">☕</span>
            <span class="footer-name">咖啡工坊</span>
          </div>
          <p class="footer-copyright">© 2026 咖啡点单系统 · 版权所有</p>
          <p class="footer-tagline">每一杯咖啡 · 都是独特的故事</p>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'

export default {
  name: 'AddAdminView',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const avatarUrl = ref('')
    const selectedFile = ref(null)
    const showPassword = ref(false)
    const showConfirmPassword = ref(false)
    const fileInputRef = ref(null)
    
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const cursorRef = ref(null)
    const headerRef = ref(null)

    const adminForm = reactive({
      username: '',
      phone: '',
      password: '',
      confirmPassword: '',
      gender: '男',
      role: 3
    })

    const errors = reactive({
      username: '',
      phone: '',
      password: '',
      confirmPassword: ''
    })

    onMounted(() => {
      checkLoginStatus()
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

    const checkLoginStatus = () => {
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        if (userInfo.value.role !== 3) {
          ElMessage.error('无权限访问此页面')
          router.push('/')
        }
      } else {
        ElMessage.error('请先登录')
        router.push('/login')
      }
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
      if (!adminForm.username) {
        errors.username = '请输入用户名'
        return false
      } else if (adminForm.username.length < 2 || adminForm.username.length > 20) {
        errors.username = '用户名长度在2到20个字符'
        return false
      }
      errors.username = ''
      return true
    }

    const validatePhone = () => {
      if (!adminForm.phone) {
        errors.phone = '请输入手机号'
        return false
      } else if (!/^1[3-9]\d{9}$/.test(adminForm.phone)) {
        errors.phone = '请输入正确的手机号'
        return false
      }
      errors.phone = ''
      return true
    }

    const validatePassword = () => {
      if (!adminForm.password) {
        errors.password = '请输入密码'
        return false
      } else if (adminForm.password.length < 6 || adminForm.password.length > 20) {
        errors.password = '密码长度在6到20个字符'
        return false
      }
      errors.password = ''
      return true
    }

    const validateConfirmPassword = () => {
      if (!adminForm.confirmPassword) {
        errors.confirmPassword = '请再次输入密码'
        return false
      } else if (adminForm.confirmPassword !== adminForm.password) {
        errors.confirmPassword = '两次输入的密码不一致'
        return false
      }
      errors.confirmPassword = ''
      return true
    }

    const validateForm = () => {
      const isUsernameValid = validateUsername()
      const isPhoneValid = validatePhone()
      const isPasswordValid = validatePassword()
      const isConfirmPasswordValid = validateConfirmPassword()
      return isUsernameValid && isPhoneValid && isPasswordValid && isConfirmPasswordValid
    }

    const resetForm = () => {
      adminForm.username = ''
      adminForm.phone = ''
      adminForm.password = ''
      adminForm.confirmPassword = ''
      adminForm.gender = '男'
      avatarUrl.value = ''
      selectedFile.value = null
      Object.keys(errors).forEach(key => errors[key] = '')
    }

    const handleAddAdmin = async () => {
      if (!validateForm()) return

      try {
        loading.value = true

        const formData = new FormData()
        formData.append('username', adminForm.username)
        formData.append('phone', adminForm.phone)
        formData.append('password', adminForm.password)
        formData.append('gender', adminForm.gender)
        formData.append('role', adminForm.role)
        
        if (selectedFile.value) {
          formData.append('headImage', selectedFile.value)
        }

        const response = await register(formData)
        
        if (response.code === 200) {
          ElMessage.success('添加管理员成功')
          resetForm()
        } else {
          ElMessage.error(response.message || '添加失败')
        }
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error(error.response?.data?.message || '添加失败')
      } finally {
        loading.value = false
      }
    }

    return {
      adminForm,
      errors,
      loading,
      avatarUrl,
      isLoggedIn,
      userInfo,
      cursorRef,
      headerRef,
      fileInputRef,
      showPassword,
      showConfirmPassword,
      triggerFileInput,
      handleAvatarChange,
      validateUsername,
      validatePhone,
      validatePassword,
      validateConfirmPassword,
      handleAddAdmin
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.add-admin-page {
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
}

/* ===== 背景纹理 ===== */
.background-texture {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    repeating-radial-gradient(circle at 20% 30%, rgba(212, 163, 115, 0.02) 0px, transparent 2px),
    repeating-linear-gradient(45deg, rgba(58, 36, 26, 0.015) 0px, rgba(58, 36, 26, 0.015) 1px, transparent 1px, transparent 8px);
  pointer-events: none;
  z-index: 0;
}

.background-gradient {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 80% 20%, rgba(232, 184, 109, 0.06) 0%, transparent 50%),
              radial-gradient(circle at 10% 80%, rgba(125, 155, 122, 0.05) 0%, transparent 50%);
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
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* ===== 头部导航 ===== */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 20px 40px;
  background: rgba(253, 248, 242, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(212, 163, 115, 0.15);
}

.header-inner {
  max-width: 1600px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.logo-area:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 32px;
  animation: steamFloat 3s ease-in-out infinite;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-main {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 24px;
  font-weight: 600;
  color: var(--dark-roast);
  letter-spacing: -0.5px;
  line-height: 1.2;
}

.logo-sub {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 400;
  color: var(--warm-brown);
  letter-spacing: 2px;
  text-transform: uppercase;
}

/* ===== 导航菜单 ===== */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-link {
  position: relative;
  text-decoration: none;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--dark-roast);
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: var(--terracotta);
}

.nav-link.active {
  color: var(--terracotta);
}

.nav-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--golden), var(--terracotta));
  transition: width 0.3s ease;
}

.nav-link:hover .nav-underline,
.nav-link.active .nav-underline {
  width: 100%;
}

.nav-link--user {
  gap: 6px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--golden), var(--terracotta));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--crema);
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
}

/* ===== 主内容区域 ===== */
.add-admin-main {
  flex: 1;
  padding-top: 120px;
  padding-bottom: 40px;
}

.add-admin-wrapper {
  max-width: 700px;
  margin: 0 auto;
  padding: 0 40px;
  position: relative;
}

/* ===== 装饰线条 ===== */
.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.1;
  pointer-events: none;
}

.decorative-line--1 {
  top: 20px;
  right: -50px;
  width: 250px;
  height: 1px;
  transform: rotate(-15deg);
}

.decorative-line--2 {
  bottom: 100px;
  left: -80px;
  width: 300px;
  height: 1px;
  transform: rotate(-10deg);
}

/* ===== 页面标题 ===== */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 42px;
  font-weight: 400;
  line-height: 1.1;
  margin: 0 0 12px 0;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.title-word {
  display: inline-block;
  opacity: 0;
  animation: titleReveal 0.6s ease forwards;
}

.title-word--1 {
  color: var(--espresso);
  font-style: italic;
  animation-delay: 0.1s;
}

.title-word--2 {
  color: var(--dark-roast);
  font-weight: 600;
  animation-delay: 0.3s;
}

.page-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--warm-brown);
  letter-spacing: 2px;
  text-transform: uppercase;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.5s forwards;
}

/* ===== 表单卡片 ===== */
.form-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 32px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(58, 36, 26, 0.08);
  opacity: 0;
  animation: cardReveal 0.6s ease 0.3s forwards;
}

/* 头像上传区域 */
.avatar-section {
  text-align: center;
  margin-bottom: 36px;
}

.avatar-upload {
  cursor: pointer;
  display: inline-block;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(58, 36, 26, 0.15);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: scale(1.02);
  box-shadow: 0 12px 32px rgba(58, 36, 26, 0.2);
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
  font-size: 40px;
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
  font-size: 28px;
  color: var(--crema);
}

.avatar-hint {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 12px 0 0 0;
}

/* 表单样式 */
.admin-form {
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

.required {
  color: var(--terracotta);
  margin-left: 2px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
  opacity: 0.6;
  pointer-events: none;
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

.error-message {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--terracotta);
  padding-left: 8px;
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

/* 性别选择 */
.gender-options {
  display: flex;
  gap: 16px;
}

.gender-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 20px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
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
  font-size: 20px;
}

.gender-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--warm-brown);
  transition: color 0.3s ease;
}

/* 角色展示 */
.role-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
}

.role-icon {
  font-size: 20px;
}

.role-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  color: var(--espresso);
}

.role-badge {
  margin-left: auto;
  padding: 4px 12px;
  background: rgba(212, 163, 115, 0.15);
  border-radius: 30px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
}

/* 提交按钮 */
.form-actions {
  margin-top: 16px;
}

.submit-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 32px;
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  border: none;
  border-radius: 50px;
  color: var(--crema);
  font-family: 'DM Sans', sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 24px rgba(58, 36, 26, 0.2);
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(58, 36, 26, 0.25);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-arrow {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.submit-btn:hover:not(:disabled) .btn-arrow {
  transform: rotate(90deg);
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

/* ===== 底部 ===== */
.app-footer {
  padding: 32px 40px;
  background: rgba(253, 248, 242, 0.5);
  border-top: 1px solid rgba(212, 163, 115, 0.15);
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}

.footer-logo {
  font-size: 24px;
}

.footer-name {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  color: var(--dark-roast);
}

.footer-copyright {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 0 0 4px 0;
}

.footer-tagline {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  opacity: 0.5;
  letter-spacing: 2px;
  margin: 0;
}

/* ===== 动画 ===== */
@keyframes titleReveal {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes cardReveal {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes steamFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
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
  .app-header {
    padding: 16px 20px;
  }
  
  .add-admin-wrapper {
    padding: 0 20px;
  }
  
  .form-card {
    padding: 30px 24px;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .nav-menu {
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .logo-main {
    font-size: 18px;
  }
  
  .gender-options {
    flex-direction: column;
  }
  
  .form-card {
    padding: 24px 16px;
  }
}
</style>