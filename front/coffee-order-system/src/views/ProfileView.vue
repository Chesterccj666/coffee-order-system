<template>
  <div class="profile-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统</h1>
          <div class="nav-links">
              <!-- 顾客功能 -->
              <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/menu')">菜单</el-button>
              <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/cart')">购物车</el-button>
              <el-button v-if="isLoggedIn && userInfo.role === 1" type="text" @click="$router.push('/orders')">我的订单</el-button>
              <!-- 店员功能 -->
              <el-button v-if="isLoggedIn && userInfo.role === 2" type="text" @click="$router.push('/staff')">待处理订单</el-button>
              <el-button v-if="isLoggedIn && userInfo.role === 2" type="text" @click="$router.push('/staff/all-orders')">全部订单</el-button>
              <el-button v-if="!isLoggedIn" type="text" @click="$router.push('/login')">登录</el-button>
              <el-button v-else type="text" @click="$router.push('/profile')">{{ userInfo.username }}</el-button>
            </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <h2 class="page-title">个人中心</h2>
        
        <el-row :gutter="20">
          <!-- 个人信息卡片 -->
          <el-col :md="8">
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
                <h3>{{ form.username }}</h3>
                <p class="user-role">{{ form.role === 1 ? '顾客' : form.role === 2 ? '店员' : '管理员' }}</p>
              </div>
            </el-card>
          </el-col>

          <!-- 编辑表单 -->
          <el-col :md="16">
            <el-card class="edit-form">
              <h3>编辑资料</h3>
              <el-form :model="form" :rules="rules" ref="profileFormRef" label-width="80px">
                <el-form-item label="姓名" prop="username">
                  <el-input v-model="form.username" placeholder="请输入姓名" />
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
                  <el-button type="primary" @click="updateProfile">保存更改</el-button>
                  <el-button @click="logout">退出登录</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
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
import axios from 'axios'
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
        const response = await axios.get(`http://localhost:8080/api/user/${userInfo.value.id}`)
        if (response.data.code === 200) {
          const userData = response.data.data
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
          ElMessage.error(response.data.message)
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

        const response = await axios.put('http://localhost:8080/api/user/update', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (response.data.code === 200) {
          ElMessage.success('更新成功')
          // 更新本地存储的用户信息
          const updatedUserInfo = {
            ...userInfo.value,
            username: form.value.username,
            phone: form.value.phone,
            gender: form.value.gender,
            headImage: response.data.data.headImage, // 使用服务器返回的实际路径
            role: form.value.role
          }
          localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
          
          // 重新加载用户信息以更新头像
          loadUserProfile()
        } else {
          ElMessage.error(response.data.message)
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
  padding: 20px;
  background-color: #f5f5f5;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 20px 0;
}

.avatar-uploader {
  text-align: center;
  margin-bottom: 15px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader-icon {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 28px;
  color: #8c939d;
  text-align: center;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
}

.user-role {
  color: #999;
  margin-top: 10px;
}

.edit-form {
  padding: 20px;
}

.edit-form h3 {
  margin-bottom: 20px;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}
</style>