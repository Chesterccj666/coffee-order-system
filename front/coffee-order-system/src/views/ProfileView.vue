<template>
  <div class="profile-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统 - 个人中心</h1>
          <div class="nav-links">
              <!-- 顾客功能 -->
              <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/menu')">菜单</el-button>
              <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/cart')">购物车</el-button>
              <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/orders')">我的订单</el-button>
              <!-- 店员功能 -->
              <el-button v-if="isLoggedIn && userInfo.role === 2" type="text" @click="$router.push('/staff')">待处理订单</el-button>
              <el-button v-if="isLoggedIn && userInfo.role === 2" type="text" @click="$router.push('/staff/all-orders')">全部订单</el-button>
              <!-- 管理员功能 -->
              <el-button v-if="isLoggedIn && userInfo.role === 3" type="text" @click="$router.push('/admin/coffee')">咖啡管理</el-button>
              <el-button v-if="isLoggedIn && userInfo.role === 3" type="text" @click="$router.push('/admin/statistics')">数据统计</el-button>
              <!-- 公有功能 -->
              <el-button v-if="!isLoggedIn" type="text" @click="$router.push('/login')">登录</el-button>
              <el-button v-else type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
            </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <div class="profile-wrapper">
          <!-- 个人信息卡片 -->
          <div class="profile-card-container">
            <el-card class="profile-card">
              <div class="avatar-section">
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :show-file-list="false"
                  :on-change="handleAvatarChange"
                  :auto-upload="false"
                >
                  <img v-if="form.headImage" :src="getImageUrl(form.headImage)" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <h2 class="username">{{ form.username }}</h2>
                <el-tag 
                  :type="form.role === 1 ? 'primary' : form.role === 2 ? 'success' : 'warning'" 
                  class="user-role-tag"
                >
                  {{ form.role === 1 ? '顾客' : form.role === 2 ? '店员' : '管理员' }}
                </el-tag>
              </div>
              
              <!-- 用户统计信息 -->
              <!-- <div class="user-stats">
                <div class="stat-item">
                  <div class="stat-value">0</div>
                  <div class="stat-label">总订单</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">¥0.00</div>
                  <div class="stat-label">消费金额</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">0</div>
                  <div class="stat-label">收藏数</div>
                </div>
              </div> -->
            </el-card>
          </div>

          <!-- 编辑表单 -->
          <div class="edit-form-container">
            <el-card class="edit-form">
              <div class="form-header">
                <h3>编辑资料</h3>
              </div>
              <el-form :model="form" :rules="rules" ref="profileFormRef" label-width="100px">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="form.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="form.gender">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateProfile" class="save-btn">保存更改</el-button>
                  <el-button @click="logout" class="logout-btn">退出登录</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </div>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserInfo, updateUserInfo } from '@/api/user'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'ProfileView',
  components: {
    Plus
  },
  setup() {
    const router = useRouter()
    const profileFormRef = ref()
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    
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
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 2, max: 20, message: '姓名长度在2到20个字符', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ]
    }

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadUserProfile()
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

    const loadUserProfile = async () => {
      try {
        const response = await getUserInfo(userInfo.value.id)
        if (response.code === 200) {
          const userData = response.data
          form.value = {
            id: userData.id,
            username: userData.username,
            phone: userData.phone,
            gender: userData.gender,
            headImage: userData.headImage,
            role: userData.role
          }
          // 同步更新本地存储的用户信息
          localStorage.setItem('userInfo', JSON.stringify(userData))
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        ElMessage.error('加载用户信息失败')
      }
    }

    const handleAvatarChange = (file) => {
      // 预览选择的图片
      const reader = new FileReader()
      reader.onload = (e) => {
        form.value.headImage = e.target.result
      }
      reader.readAsDataURL(file.raw)
      
      // 保存原始文件，用于后续上传
      form.value.avatarFile = file.raw
    }

    const updateProfile = async () => {
      try {
        const valid = await profileFormRef.value.validate()
        if (!valid) return

        // 创建FormData对象来处理文件上传
        const formData = new FormData()
        formData.append('id', form.value.id)
        formData.append('username', form.value.username)
        formData.append('phone', form.value.phone)
        formData.append('gender', form.value.gender)
        formData.append('role', form.value.role) // 添加角色信息
        
        // 如果有新选择的头像文件，则上传
        if (form.value.avatarFile) {
          formData.append('headImage', form.value.avatarFile)
        }

        const response = await updateUserInfo(formData)
        
        if (response.code === 200) {
          ElMessage.success('更新成功')
          // 更新本地存储的用户信息
          const updatedUserInfo = {
            ...userInfo.value,
            username: form.value.username,
            phone: form.value.phone,
            gender: form.value.gender,
            headImage: response.data.headImage, // 使用服务器返回的实际路径
            role: form.value.role
          }
          localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
          
          // 重新加载用户信息以更新头像
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

        // 清除本地存储的用户信息
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
      if (!url) return url;
      // 添加时间戳参数以防止浏览器缓存
      return url + '?t=' + Date.now();
    }

    return {
      profileFormRef,
      isLoggedIn,
      userInfo,
      form,
      rules,
      handleAvatarChange,
      updateProfile,
      logout,
      getImageUrl
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
  padding: 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
  min-height: calc(100vh - 140px);
}

.profile-wrapper {
  display: flex;
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card-container {
  flex: 0 0 320px;
}

.edit-form-container {
  flex: 1;
}

.profile-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background: linear-gradient(135deg, #ffffff 0%, #fefefe 100%);
}

.profile-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.avatar-section {
  padding: 40px 20px 30px;
  background: linear-gradient(135deg, #ffffff 0%, #ffffff 100%);
  color: white;
  text-align: center;
}

.avatar-uploader {
  text-align: center;
  margin-bottom: 20px;
}

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.avatar-uploader-icon {
  width: 120px;
  height: 120px;
  border: 2px dashed rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 32px;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  transition: all 0.3s ease;
}

.avatar-uploader-icon:hover {
  border-color: rgba(255, 255, 255, 0.8);
  color: white;
}

.username {
  margin: 15px 0 10px;
  font-size: 24px;
  font-weight: 600;
  color: white;
}

.user-role-tag {
  font-size: 14px;
  padding: 6px 16px;
  font-weight: 500;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  background-color: #f9f9f9;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #999;
}

.form-header {
  margin-bottom: 25px;
}

.form-header h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.form-desc {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.edit-form {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  background: white;
  padding: 30px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.edit-form:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.edit-form h3 {
  margin-bottom: 10px;
}

.save-btn {
  background: linear-gradient(135deg, #409EFF 0%, #409EFF 100%);
  border: none;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
}

.save-btn:hover {
  background: linear-gradient(135deg, #7a3a0f 0%, #8b4826 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.3);
}

.logout-btn {
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  margin-left: 15px !important;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .profile-wrapper {
    flex-direction: column;
  }
  
  .profile-card-container {
    flex: 0 0 auto;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px 15px;
  }
  
  .profile-wrapper {
    flex-direction: column;
    gap: 20px;
  }
  
  .profile-card-container, .edit-form-container {
    width: 100%;
  }
}
</style>