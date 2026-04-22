<template>
  <div class="profile-page">
    <!-- 背景纹理层 -->
    <div class="background-texture"></div>
    <div class="background-gradient"></div>

    <!-- 自定义光标 -->
    <div class="custom-cursor" ref="cursorRef"></div>

    <div class="page-container">
      <!-- 头部导航 - 完全照搬 MenuView.vue 的设计 -->
      <header class="app-header" ref="headerRef">
        <div class="header-inner">
          <div class="logo-area" @click="$router.push('/')">
            <span class="logo-icon">☕</span>
            <span class="logo-text">
              <span class="logo-main">咖啡工坊</span>
              <span class="logo-sub">个人中心</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <!-- 顾客功能 -->
            <template v-if="isLoggedIn && userInfo.role === 1">
              <router-link to="/menu" class="nav-link">
                <span class="nav-text">菜单</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/cart" class="nav-link">
                <span class="nav-text">购物车</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/orders" class="nav-link">
                <span class="nav-text">我的订单</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <!-- 店员功能 -->
            <template v-if="isLoggedIn && userInfo.role === 2">
              <router-link to="/staff" class="nav-link">
                <span class="nav-text">待处理订单</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/staff/all-orders" class="nav-link">
                <span class="nav-text">全部订单</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <!-- 管理员功能 -->
            <template v-if="isLoggedIn && userInfo.role === 3">
              <router-link to="/admin/coffee" class="nav-link">
                <span class="nav-text">咖啡管理</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/admin/statistics" class="nav-link">
                <span class="nav-text">数据统计</span>
                <span class="nav-underline"></span>
              </router-link>
              <router-link to="/admin/add-admin" class="nav-link">
                <span class="nav-text">添加管理员</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            
            <!-- 公有功能 -->
            <template v-if="!isLoggedIn">
              <router-link to="/login" class="nav-link nav-link--highlight">
                <span class="nav-text">登录</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
            <template v-else>
              <router-link to="/profile" class="nav-link nav-link--user active">
                <span class="user-avatar">{{ userInfo.username?.charAt(0) }}</span>
                <span class="nav-text">{{ userInfo.username }}</span>
                <span class="nav-underline"></span>
              </router-link>
            </template>
          </nav>
        </div>
      </header>

      <!-- 主内容区域 -->
      <main class="profile-main">
        <div class="profile-wrapper">
          <!-- 装饰性线条 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>

          <!-- 页面标题 -->
          <div class="page-header">
            <h1 class="page-title">
              <span class="title-word title-word--1">个人</span>
              <span class="title-word title-word--2">中心</span>
            </h1>
            <p class="page-subtitle">管理您的账户信息</p>
          </div>

          <!-- 个人信息卡片 -->
          <div class="profile-card">
            <div class="profile-card-inner">
              <!-- 左侧：头像与基本信息 -->
              <div class="profile-sidebar">
                <div class="avatar-section">
                  <el-upload
                    class="avatar-uploader"
                    action="#"
                    :show-file-list="false"
                    :on-change="handleAvatarChange"
                    :auto-upload="false"
                  >
                    <div class="avatar-wrapper">
                      <img v-if="form.headImage" :src="getImageUrl(form.headImage)" class="avatar" alt="头像" />
                      <div v-else class="avatar-placeholder">
                        <span class="placeholder-icon">📷</span>
                        <span class="placeholder-text">点击上传头像</span>
                      </div>
                      <div class="avatar-overlay">
                        <span class="overlay-icon">📷</span>
                      </div>
                    </div>
                  </el-upload>
                  
                  <h2 class="profile-username">{{ form.username }}</h2>
                  
                  <div class="role-badge" :class="`role-${form.role}`">
                    <span class="role-icon">{{ getRoleIcon(form.role) }}</span>
                    <span class="role-text">{{ getRoleText(form.role) }}</span>
                  </div>
                  
                  <div class="profile-meta">
                    <div class="meta-item">
                      <span class="meta-icon">📞</span>
                      <span class="meta-value">{{ form.phone || '未填写' }}</span>
                    </div>
                    <div class="meta-item">
                      <span class="meta-icon">👤</span>
                      <span class="meta-value">{{ form.gender || '未设置' }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 右侧：编辑表单 -->
              <div class="profile-form-section">
                <div class="form-header">
                  <h3 class="form-title">编辑资料</h3>
                  <p class="form-desc">更新您的个人信息</p>
                </div>

                <el-form :model="form" :rules="rules" ref="profileFormRef" label-position="top" class="profile-form">
                  <div class="form-row">
                    <el-form-item label="用户名" prop="username">
                      <el-input 
                        v-model="form.username" 
                        placeholder="请输入用户名" 
                        class="custom-input"
                      />
                    </el-form-item>
                  </div>

                  <div class="form-row">
                    <el-form-item label="手机号" prop="phone">
                      <el-input 
                        v-model="form.phone" 
                        placeholder="请输入手机号" 
                        class="custom-input"
                      />
                    </el-form-item>
                  </div>

                  <div class="form-row">
                    <el-form-item label="性别" prop="gender">
                      <div class="gender-options">
                        <div 
                          class="gender-option"
                          :class="{ active: form.gender === '男' }"
                          @click="form.gender = '男'"
                        >
                          <span class="gender-icon">♂️</span>
                          <span class="gender-text">男</span>
                        </div>
                        <div 
                          class="gender-option"
                          :class="{ active: form.gender === '女' }"
                          @click="form.gender = '女'"
                        >
                          <span class="gender-icon">♀️</span>
                          <span class="gender-text">女</span>
                        </div>
                      </div>
                    </el-form-item>
                  </div>
                </el-form>

                <div class="form-actions">
                  <button class="save-btn" @click="updateProfile">
                    <span>保存更改</span>
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M5 13L9 17L19 7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                  <button class="logout-btn" @click="logout">
                    <span>退出登录</span>
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      <path d="M16 17L21 12L16 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                </div>

                <div class="password-section">
                  <button class="password-toggle-btn" @click="showPasswordChange">
                    <span class="toggle-icon">{{ showPasswordChangeCard ? '🔒' : '🔐' }}</span>
                    <span>{{ showPasswordChangeCard ? '收起' : '修改密码' }}</span>
                    <svg viewBox="0 0 24 24" fill="none" class="toggle-arrow" :class="{ rotated: showPasswordChangeCard }">
                      <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 密码修改卡片 -->
          <transition name="slide-fade">
            <div v-if="showPasswordChangeCard" class="password-card">
              <div class="password-card-inner">
                <div class="password-header">
                  <h3 class="password-title">修改密码</h3>
                  <p class="password-desc">请填写以下信息以更新密码</p>
                </div>

                <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-position="top" class="password-form">
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input 
                      v-model="passwordForm.oldPassword" 
                      type="password" 
                      placeholder="请输入原密码" 
                      class="custom-input"
                      show-password
                    />
                  </el-form-item>

                  <el-form-item label="新密码" prop="newPassword">
                    <el-input 
                      v-model="passwordForm.newPassword" 
                      type="password" 
                      placeholder="请输入新密码（6-20位）" 
                      class="custom-input"
                      show-password
                    />
                  </el-form-item>

                  <el-form-item label="确认新密码" prop="confirmNewPassword">
                    <el-input 
                      v-model="passwordForm.confirmNewPassword" 
                      type="password" 
                      placeholder="请再次输入新密码" 
                      class="custom-input"
                      show-password
                    />
                  </el-form-item>
                </el-form>

                <div class="password-actions">
                  <button class="cancel-btn" @click="showPasswordChangeCard = false">
                    <span>取消</span>
                  </button>
                  <button class="confirm-btn" @click="changePassword">
                    <span>确认修改</span>
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M5 13L9 17L19 7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserInfo, updateUserInfo, changePassword as apiChangePassword } from '@/api/user'

export default {
  name: 'ProfileView',
  setup() {
    const router = useRouter()
    const profileFormRef = ref()
    const passwordFormRef = ref()
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    const cursorRef = ref(null)
    const headerRef = ref(null)
    
    const form = ref({
      id: null,
      username: '',
      phone: '',
      gender: '男',
      headImage: '',
      role: 1
    })

    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '用户名长度在2到20个字符', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ]
    }
    
    const passwordForm = ref({
      oldPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    })
    
    const showPasswordChangeCard = ref(false)
    
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== passwordForm.value.newPassword) {
        callback(new Error('两次输入的新密码不一致'))
      } else {
        callback()
      }
    }
    
    const passwordRules = ref({
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
      ],
      confirmNewPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    })

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadUserProfile()
      }
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
      } else {
        ElMessage.warning('请先登录')
        router.push('/login')
      }
    }

    const loadUserProfile = async () => {
      try {
        const response = await getUserInfo(userInfo.value.id)
        if (response.code === 200) {
          const userData = response.data
          form.value = {
            id: userData.id,
            username: userData.username,
            phone: userData.phone,
            gender: userData.gender || '男',
            headImage: userData.headImage,
            role: userData.role
          }
          localStorage.setItem('userInfo', JSON.stringify(userData))
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        ElMessage.error('加载用户信息失败')
      }
    }

    const getRoleIcon = (role) => {
      const icons = { 1: '👤', 2: '🛎️', 3: '⚙️' }
      return icons[role] || '👤'
    }

    const getRoleText = (role) => {
      const texts = { 1: '顾客', 2: '店员', 3: '管理员' }
      return texts[role] || '用户'
    }

    const handleAvatarChange = (file) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        form.value.headImage = e.target.result
      }
      reader.readAsDataURL(file.raw)
      form.value.avatarFile = file.raw
    }

    const updateProfile = async () => {
      try {
        const valid = await profileFormRef.value.validate()
        if (!valid) return

        const formData = new FormData()
        formData.append('id', form.value.id)
        formData.append('username', form.value.username)
        formData.append('phone', form.value.phone)
        formData.append('gender', form.value.gender)
        formData.append('role', form.value.role)
        
        if (form.value.avatarFile) {
          formData.append('headImage', form.value.avatarFile)
        }

        const response = await updateUserInfo(formData)
        
        if (response.code === 200) {
          ElMessage.success('资料更新成功')
          const updatedUserInfo = {
            ...userInfo.value,
            username: form.value.username,
            phone: form.value.phone,
            gender: form.value.gender,
            headImage: response.data.headImage,
            role: form.value.role
          }
          localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
          loadUserProfile()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败')
      }
    }

    const logout = async () => {
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        localStorage.removeItem('userInfo')
        ElMessage.success('已退出登录')
        router.push('/')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退出登录失败:', error)
        }
      }
    }

    const getImageUrl = (url) => {
      if (!url) return url
      return url + '?t=' + Date.now()
    }

    const showPasswordChange = () => {
      showPasswordChangeCard.value = !showPasswordChangeCard.value
      if (!showPasswordChangeCard.value) {
        passwordForm.value = {
          oldPassword: '',
          newPassword: '',
          confirmNewPassword: ''
        }
        passwordFormRef.value?.clearValidate()
      }
    }
    
    const changePassword = async () => {
      try {
        const valid = await passwordFormRef.value.validate()
        if (!valid) return

        if (passwordForm.value.newPassword !== passwordForm.value.confirmNewPassword) {
          ElMessage.error('两次输入的新密码不一致')
          return
        }

        const changePasswordData = {
          userId: form.value.id,
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        }

        const response = await apiChangePassword(changePasswordData)

        if (response.code === 200) {
          ElMessage.success('密码修改成功')
          passwordForm.value = {
            oldPassword: '',
            newPassword: '',
            confirmNewPassword: ''
          }
          showPasswordChangeCard.value = false
        } else {
          ElMessage.error(response.message || '密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error(error.message || '修改密码失败')
      }
    }

    return {
      profileFormRef,
      passwordFormRef,
      isLoggedIn,
      userInfo,
      form,
      passwordForm,
      rules,
      passwordRules,
      cursorRef,
      headerRef,
      showPasswordChangeCard,
      getRoleIcon,
      getRoleText,
      handleAvatarChange,
      updateProfile,
      changePassword,
      showPasswordChange,
      logout,
      getImageUrl
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.profile-page {
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
  max-width: 1400px;
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
.profile-main {
  padding-top: 120px;
  padding-bottom: 60px;
  min-height: 100vh;
}

.profile-wrapper {
  max-width: 1000px;
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
  margin-bottom: 48px;
}

.page-title {
  font-family: 'Playfair Display', 'Cormorant Garamond', serif;
  font-size: 48px;
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
  letter-spacing: 3px;
  text-transform: uppercase;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.5s forwards;
}

/* ===== 个人信息卡片 ===== */
.profile-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(58, 36, 26, 0.08);
  margin-bottom: 32px;
}

.profile-card-inner {
  display: flex;
  min-height: 480px;
}

/* 左侧边栏 */
.profile-sidebar {
  width: 320px;
  background: linear-gradient(135deg, rgba(212, 163, 115, 0.08), rgba(200, 121, 92, 0.04));
  padding: 40px 24px;
  border-right: 1px solid rgba(212, 163, 115, 0.15);
}

.avatar-section {
  text-align: center;
}

.avatar-uploader {
  display: block;
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
  width: 160px;
  height: 160px;
  margin: 0 auto;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(58, 36, 26, 0.15);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--cream), var(--crema));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 2px dashed rgba(212, 163, 115, 0.5);
  border-radius: 50%;
}

.placeholder-icon {
  font-size: 32px;
  opacity: 0.6;
}

.placeholder-text {
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
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

.avatar-wrapper:hover .avatar {
  transform: scale(1.05);
}

.overlay-icon {
  font-size: 28px;
  color: var(--crema);
}

.profile-username {
  font-family: 'Playfair Display', serif;
  font-size: 26px;
  font-weight: 600;
  color: var(--espresso);
  margin: 16px 0 12px 0;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 28px;
}

.role-1 {
  background: rgba(212, 163, 115, 0.15);
  color: var(--warm-brown);
}

.role-2 {
  background: rgba(125, 155, 122, 0.12);
  color: var(--sage);
}

.role-3 {
  background: rgba(200, 121, 92, 0.12);
  color: var(--terracotta);
}

.profile-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid rgba(212, 163, 115, 0.2);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
}

.meta-icon {
  font-size: 18px;
  opacity: 0.7;
}

/* 右侧表单区域 */
.profile-form-section {
  flex: 1;
  padding: 40px;
}

.form-header {
  margin-bottom: 32px;
}

.form-title {
  font-family: 'Playfair Display', serif;
  font-size: 24px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0 0 4px 0;
}

.form-desc {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 0;
}

.profile-form {
  margin-bottom: 32px;
}

.form-row {
  margin-bottom: 20px;
}

:deep(.custom-input .el-input__wrapper) {
  background: var(--cream);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 16px;
  box-shadow: none;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input__wrapper:hover) {
  border-color: var(--golden);
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  border-color: var(--golden);
  box-shadow: 0 0 0 3px rgba(212, 163, 115, 0.1);
}

:deep(.custom-input .el-input__inner) {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--espresso);
}

:deep(.custom-input .el-input__inner::placeholder) {
  color: var(--warm-brown);
  opacity: 0.5;
}

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
  border-radius: 16px;
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

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.save-btn,
.logout-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 24px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.save-btn {
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  color: var(--crema);
  box-shadow: 0 6px 20px rgba(58, 36, 26, 0.15);
}

.save-btn:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.2);
}

.logout-btn {
  background: transparent;
  border: 1.5px solid rgba(200, 121, 92, 0.4);
  color: var(--terracotta);
}

.logout-btn:hover {
  background: rgba(200, 121, 92, 0.08);
  border-color: var(--terracotta);
}

.save-btn svg,
.logout-btn svg {
  width: 18px;
  height: 18px;
}

/* 密码切换按钮 */
.password-section {
  text-align: center;
}

.password-toggle-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  background: transparent;
  border: 1.5px solid rgba(212, 163, 115, 0.3);
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--warm-brown);
  cursor: pointer;
  transition: all 0.3s ease;
}

.password-toggle-btn:hover {
  background: rgba(212, 163, 115, 0.08);
  border-color: var(--golden);
}

.toggle-icon {
  font-size: 16px;
}

.toggle-arrow {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.toggle-arrow.rotated {
  transform: rotate(180deg);
}

/* 密码卡片 */
.password-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(58, 36, 26, 0.08);
}

.password-card-inner {
  padding: 36px;
}

.password-header {
  text-align: center;
  margin-bottom: 32px;
}

.password-title {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 500;
  color: var(--espresso);
  margin: 0 0 4px 0;
}

.password-desc {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.7;
  margin: 0;
}

.password-form {
  max-width: 400px;
  margin: 0 auto;
}

.password-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 32px;
}

.cancel-btn,
.confirm-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 32px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.cancel-btn {
  background: transparent;
  border: 1.5px solid rgba(212, 163, 115, 0.4);
  color: var(--warm-brown);
}

.cancel-btn:hover {
  background: rgba(212, 163, 115, 0.08);
  border-color: var(--golden);
}

.confirm-btn {
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  color: var(--crema);
  box-shadow: 0 6px 20px rgba(58, 36, 26, 0.15);
}

.confirm-btn:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(58, 36, 26, 0.2);
}

.confirm-btn svg {
  width: 18px;
  height: 18px;
}

/* 动画 */
.slide-fade-enter-active {
  transition: all 0.4s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s ease-in;
}

.slide-fade-enter-from {
  transform: translateY(-20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

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

@keyframes steamFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .profile-card-inner {
    flex-direction: column;
  }
  
  .profile-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid rgba(212, 163, 115, 0.15);
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 16px 20px;
  }
  
  .profile-wrapper {
    padding: 0 20px;
  }
  
  .page-title {
    font-size: 36px;
  }
  
  .profile-form-section {
    padding: 30px 24px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .password-actions {
    flex-direction: column;
  }
  
  .cancel-btn,
  .confirm-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .logo-main {
    font-size: 18px;
  }
  
  .nav-menu {
    gap: 16px;
  }
  
  .gender-options {
    flex-direction: column;
  }
}
</style>