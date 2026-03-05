<template>
  <div class="register-container">
    <el-card class="register-form">
      <div class="avatar-upload">
        <el-upload
          class="avatar-uploader"
          :show-file-list="false"
          :on-change="handleAvatarChange"
          :auto-upload="false"
          accept=".jpg,.jpeg,.png,.gif"
        >
          <img v-if="avatarUrl" :src="avatarUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <p>点击上传头像</p>
      </div>
      
      <h2 class="title">用户注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="80px">
        <el-form-item label="姓名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            placeholder="请输入手机号" 
            prefix-icon="Iphone"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio :label="1">顾客</el-radio>
            <el-radio :label="2">店员</el-radio>
            <el-radio :label="3">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            :loading="loading" 
            class="register-btn"
          >
            注册
          </el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<el-button type="text" @click="$router.push('/login')">立即登录</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage, ElUpload } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'RegisterView',
  components: {
    Plus
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const registerFormRef = ref()
    const avatarUrl = ref('')
    const selectedFile = ref(null)

    const registerForm = reactive({
      username: '',
      phone: '',
      password: '',
      confirmPassword: '',
      gender: '男',
      role: 1  // 默认为顾客
    })

    // 自定义验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    const rules = {
      username: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 2, max: 20, message: '姓名长度在2到20个字符', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ],
      role: [
        { required: true, message: '请选择角色', trigger: 'change' }
      ]
    }

    const handleAvatarChange = (file) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        avatarUrl.value = e.target.result
      }
      reader.readAsDataURL(file.raw)
      selectedFile.value = file.raw
    }

    const handleRegister = async () => {
      try {
        const valid = await registerFormRef.value.validate()
        if (!valid) return

        loading.value = true

        // 创建FormData对象用于上传头像
        const formData = new FormData()
        formData.append('username', registerForm.username)
        formData.append('phone', registerForm.phone)
        formData.append('password', registerForm.password)
        formData.append('gender', registerForm.gender)
        formData.append('role', registerForm.role)
        
        // 如果选择了头像，则添加到formData
        if (selectedFile.value) {
          formData.append('headImage', selectedFile.value)
        }

        const response = await axios.post('http://localhost:8080/api/user/register', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (response.data.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(response.data.message || '注册失败')
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
      rules,
      registerFormRef,
      loading,
      avatarUrl,
      handleAvatarChange,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.register-form {
  width: 450px;
  padding: 30px;
}

.avatar-upload {
  text-align: center;
  margin-bottom: 20px;
}

.avatar-uploader {
  display: inline-block;
  margin-bottom: 10px;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.register-btn {
  width: 100%;
  margin-top: 10px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
}
</style>