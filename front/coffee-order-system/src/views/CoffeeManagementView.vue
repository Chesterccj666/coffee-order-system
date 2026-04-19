<template>
  <div class="coffee-management-container">
    <el-container>
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo" @click="$router.push('/')">☕ 咖啡点单系统 - 咖啡管理</h1>
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
        <!-- 操作按钮 -->
        <div class="operation-buttons">
          <el-button type="primary" @click="showAddDialog = true">添加咖啡</el-button>
          <el-button @click="loadCoffeeList">刷新列表</el-button>
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索咖啡名称" 
            style="width: 200px; margin-left: 20px;" 
            clearable
            @input="onSearchChange"
          />
        </div>

        <!-- 咖啡列表 -->
        <el-table 
          :data="coffeeList" 
          stripe 
          style="width: 100%"
          v-loading="loading">
          <el-table-column prop="name" width="150">
            <template #header>
              <span class="table-header-text">咖啡名称</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" width="100">
            <template #header>
              <span class="table-header-text">价格</span>
            </template>
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column label="图片" width="100">
            <template #header>
              <span class="table-header-text">图片</span>
            </template>
            <template #default="{ row }">
              <el-image 
                :src="getImageUrl(row.coffeeImage)" 
                style="width: 50px; height: 50px;"
                fit="cover"
                :preview-src-list="[getImageUrl(row.coffeeImage)]"
                preview-teleported
              ></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="description" show-overflow-tooltip>
            <template #header>
              <span class="table-header-text">描述</span>
            </template>
          </el-table-column>
          <el-table-column prop="category" width="180">
              <template #header>
                <div class="filter-header-inline">
                  <span class="table-header-text">类别</span>
                  <el-select 
                    v-model="selectedCategory" 
                    placeholder="全部" 
                    size="small"
                    clearable
                    style="margin-left: 10px; width: 100px;"
                    @change="onCategoryChange"
                  >
                    <el-option label="全部" value=""></el-option>
                    <el-option label="经典意式" value="经典意式"></el-option>
                    <el-option label="风味拿铁" value="风味拿铁"></el-option>
                    <el-option label="风味美式" value="风味美式"></el-option>
                    <el-option label="奶咖" value="奶咖"></el-option>
                    <el-option label="燕麦系列" value="燕麦系列"></el-option>
                    <el-option label="单品豆SOE" value="单品豆SOE"></el-option>
                    <el-option label="其他" value="其他"></el-option>
                  </el-select>
                </div>
              </template>
            </el-table-column>
          <el-table-column label="库存" width="120">
            <template #header>
              <div class="sort-header-inline">
                <span class="table-header-text">库存</span>
                <el-button 
                  size="medium" 
                  :type="stockSortOrder === 'asc' || stockSortOrder === 'desc' ? 'primary' : 'default'"
                  @click="toggleStockSort"
                >
                  {{ stockSortOrder === 'asc' ? '升序' : stockSortOrder === 'desc' ? '降序' : '排序' }}
                </el-button>
              </div>
            </template>
            <template #default="{ row }">
              <span>{{ row.stock }}</span>
            </template>
          </el-table-column>
          <el-table-column width="100">
            <template #header>
              <span class="table-header-text">状态</span>
            </template>
            <template #default="{ row }">
              <el-tag :type="row.status === '1' ? 'success' : 'danger'">
                {{ row.status === '1' ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column width="100">
            <template #header>
              <span class="table-header-text">推荐</span>
            </template>
            <template #default="{ row }">
              <el-tag :type="row.recommend === '1' ? 'warning' : 'info'">
                {{ row.recommend === '1' ? '推荐' : '不推荐' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="销量" width="120">
            <template #header>
              <div class="sort-header-inline">
                <span class="table-header-text">销量</span>
                <el-button 
                  size="medium" 
                  :type="salesSortOrder === 'asc' || salesSortOrder === 'desc' ? 'primary' : 'default'"
                  @click="toggleSalesSort"
                >
                  {{ salesSortOrder === 'asc' ? '升序' : salesSortOrder === 'desc' ? '降序' : '排序' }}
                </el-button>
              </div>
            </template>
            <template #default="{ row }">
              <span>{{ row.sales }}</span>
            </template>
          </el-table-column>
          <el-table-column width="320">
            <template #header>
              <span class="table-header-text">操作</span>
            </template>
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="editCoffee(row)">编辑</el-button>
              <el-button size="small" :type="row.status === '1' ? 'info' : 'success'" @click="toggleStatus(row)">
                {{ row.status === '1' ? '下架' : '上架' }}
              </el-button>
              <el-button size="small" :type="row.recommend === '1' ? 'info' : 'warning'" @click="toggleRecommend(row)" class="fixed-width-btn">
                {{ row.recommend === '1' ? '取消推荐' : '推荐' }}
              </el-button>
              <el-button size="small" type="danger" @click="deleteCoffee(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 添加咖啡对话框 -->
        <el-dialog 
          v-model="showAddDialog" 
          title="添加咖啡" 
          width="600px"
          :before-close="handleCloseAddDialog"
        >
          <el-form :model="addForm" :rules="formRules" ref="addFormRef" label-width="80px">
            <el-form-item label="咖啡名称" prop="name">
              <el-input v-model="addForm.name" placeholder="请输入咖啡名称" />
            </el-form-item>
            <el-form-item label="价格" prop="price">
              <el-input v-model="addForm.price" placeholder="请输入价格" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input 
                v-model="addForm.description" 
                type="textarea" 
                :rows="3"
                placeholder="请输入咖啡描述"
              />
            </el-form-item>
            <el-form-item label="类别" prop="category">
              <el-select v-model="addForm.category" placeholder="请选择类别" style="width: 100%">
                <el-option label="经典意式" value="经典意式"></el-option>
                <el-option label="风味拿铁" value="风味拿铁"></el-option>
                <el-option label="风味美式" value="风味美式"></el-option>
                <el-option label="奶咖" value="奶咖"></el-option>
                <el-option label="燕麦系列" value="燕麦系列"></el-option>
                <el-option label="单品豆SOE" value="单品豆SOE"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="addForm.stock" :min="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="addForm.status">
                <el-radio label="1">上架</el-radio>
                <el-radio label="2">下架</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="推荐" prop="recommend">
              <el-radio-group v-model="addForm.recommend">
                <el-radio label="1">推荐</el-radio>
                <el-radio label="2">不推荐</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="图片" prop="image">
              <el-upload
                class="avatar-uploader"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleImageChange"
              >
                <img v-if="addForm.imagePreview" :src="addForm.imagePreview" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="handleCloseAddDialog">取消</el-button>
              <el-button type="primary" @click="handleAddCoffee">确定</el-button>
            </span>
          </template>
        </el-dialog>

        <!-- 编辑咖啡对话框 -->
        <el-dialog 
          v-model="showEditDialog" 
          title="编辑咖啡" 
          width="600px"
          :before-close="handleCloseEditDialog"
        >
          <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="80px">
            <el-form-item label="ID">
              <el-input v-model.number="editForm.id" disabled />
            </el-form-item>
            <el-form-item label="咖啡名称" prop="name">
              <el-input v-model="editForm.name" placeholder="请输入咖啡名称" />
            </el-form-item>
            <el-form-item label="价格" prop="price">
              <el-input v-model="editForm.price" placeholder="请输入价格" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input 
                v-model="editForm.description" 
                type="textarea" 
                :rows="3"
                placeholder="请输入咖啡描述"
              />
            </el-form-item>
            <el-form-item label="类别" prop="category">
              <el-select v-model="editForm.category" placeholder="请选择类别" style="width: 100%">
                <el-option label="经典意式" value="经典意式"></el-option>
                <el-option label="风味拿铁" value="风味拿铁"></el-option>
                <el-option label="风味美式" value="风味美式"></el-option>
                <el-option label="奶咖" value="奶咖"></el-option>
                <el-option label="燕麦系列" value="燕麦系列"></el-option>
                <el-option label="单品豆SOE" value="单品豆SOE"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="editForm.stock" :min="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="图片">
              <el-upload
                class="avatar-uploader"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleEditImageChange"
              >
                <img v-if="editForm.imagePreview" :src="editForm.imagePreview" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="handleCloseEditDialog">取消</el-button>
              <el-button type="primary" @click="handleEditCoffee">确定</el-button>
            </span>
          </template>
        </el-dialog>
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
import { Plus } from '@element-plus/icons-vue'
import { getAllCoffeeForAdmin, addCoffee, updateCoffee, deleteCoffee as deleteCoffeeApi, setRecommend, setStatus } from '@/api/admin'

export default {
  name: 'CoffeeManagementView',
  components: {
    Plus
  },
  setup() {
    const coffeeList = ref([])
    const originalCoffeeList = ref([]) // 保存原始列表用于排序
    const loading = ref(false)
    const showAddDialog = ref(false)
    const showEditDialog = ref(false)
    const addFormRef = ref()
    const editFormRef = ref()
    const stockSortOrder = ref('') // '', 'asc', 'desc'
    const salesSortOrder = ref('') // '', 'asc', 'desc'
    const searchKeyword = ref('') // 搜索关键词
    const selectedCategory = ref('') // 选定的类别
    
    // 登录状态相关
    const isLoggedIn = ref(false)
    const userInfo = ref({})

    // 添加咖啡表单
    const addForm = ref({
      name: '',
      price: '',
      description: '',
      category: '',
      stock: 0,
      status: '1',
      recommend: '2',
      image: null,
      imagePreview: ''
    })

    // 编辑咖啡表单
    const editForm = ref({
      id: null,
      name: '',
      price: '',
      description: '',
      category: '',
      stock: 0,
      status: '1',
      recommend: '2',
      imagePreview: ''
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入咖啡名称', trigger: 'blur' },
        { min: 1, max: 20, message: '咖啡名称长度在1到20个字符', trigger: 'blur' }
      ],
      price: [
        { required: true, message: '请输入价格', trigger: 'blur' },
        { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的价格格式', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入咖啡描述', trigger: 'blur' }
      ],
      category: [
        { required: true, message: '请选择类别', trigger: 'change' }
      ],
      stock: [
        { required: true, message: '请输入库存', trigger: 'blur' }
      ]
    }

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadCoffeeList()
      }
    })

    const checkLoginStatus = () => {
      // 从本地存储获取用户信息
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        // 检查是否为管理员
        if (userInfo.value.role !== 3) {
          ElMessage.error('您没有权限访问此页面')
          // 重定向到首页
          window.location.href = '/'
        }
      } else {
        ElMessage.warning('请先登录')
        window.location.href = '/login'
      }
    }

    const loadCoffeeList = async () => {
      try {
        loading.value = true
        const response = await getAllCoffeeForAdmin()
        if (response.code === 200) {
          originalCoffeeList.value = response.data
          // 应用当前的过滤和排序
          applyCurrentSort()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('加载咖啡列表失败:', error)
        ElMessage.error('加载咖啡列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleImageChange = (file) => {
      // 预览选择的图片
      const reader = new FileReader()
      reader.onload = (e) => {
        addForm.value.imagePreview = e.target.result
      }
      reader.readAsDataURL(file.raw)
      
      // 保存原始文件，用于后续上传
      addForm.value.image = file.raw
    }

    const handleEditImageChange = (file) => {
      // 预览选择的图片
      const reader = new FileReader()
      reader.onload = (e) => {
        editForm.value.imagePreview = e.target.result
      }
      reader.readAsDataURL(file.raw)
      
      // 保存原始文件，用于后续上传
      editForm.value.image = file.raw
    }

    const handleAddCoffee = async () => {
      try {
        const valid = await addFormRef.value.validate()
        if (!valid) return

        if (!addForm.value.image) {
          ElMessage.error('请上传咖啡图片')
          return
        }

        // 创建FormData对象来处理文件上传
        const formData = new FormData()
        formData.append('name', addForm.value.name)
        formData.append('price', addForm.value.price)
        formData.append('description', addForm.value.description)
        formData.append('category', addForm.value.category)
        formData.append('stock', addForm.value.stock.toString())
        formData.append('status', addForm.value.status)
        formData.append('recommend', addForm.value.recommend)
        formData.append('image', addForm.value.image)

        const response = await addCoffee(formData)
        
        if (response.code === 200) {
          ElMessage.success('添加成功')
          showAddDialog.value = false
          resetAddForm()
          loadCoffeeList()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error('添加失败')
      }
    }

    const handleEditCoffee = async () => {
      try {
        const valid = await editFormRef.value.validate()
        if (!valid) return

        // 创建FormData对象来处理文件上传
        const formData = new FormData()
        formData.append('id', editForm.value.id.toString())
        formData.append('name', editForm.value.name)
        formData.append('price', editForm.value.price)
        formData.append('description', editForm.value.description)
        formData.append('category', editForm.value.category)
        formData.append('stock', editForm.value.stock.toString())
        formData.append('status', editForm.value.status)
        formData.append('recommend', editForm.value.recommend)
        
        // 如果选择了新图片，则添加到formData中
        if (editForm.value.image) {
          formData.append('image', editForm.value.image)
        }

        const response = await updateCoffee(formData)
        
        if (response.code === 200) {
          ElMessage.success('更新成功')
          showEditDialog.value = false
          loadCoffeeList()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败')
      }
    }

    const editCoffee = (row) => {
      // 复制行数据到编辑表单
      editForm.value = {
        id: row.id,
        name: row.name,
        price: row.price,
        description: row.description,
        category: row.category,
        stock: row.stock,
        status: row.status,
        recommend: row.recommend,
        imagePreview: getImageUrl(row.coffeeImage)
      }
      showEditDialog.value = true
    }

    const toggleStatus = async (row) => {
      try {
        const newStatus = row.status === '1' ? '2' : '1'
        const response = await setStatus(row.id, newStatus)
        
        if (response.code === 200) {
          ElMessage.success(`${row.status === '1' ? '下架' : '上架'}成功`)
          // 更新本地数据
          row.status = newStatus
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('更新状态失败:', error)
        ElMessage.error('更新状态失败')
      }
    }

    const toggleRecommend = async (row) => {
      try {
        const newRecommend = row.recommend === '1' ? '2' : '1'
        const response = await setRecommend(row.id, newRecommend)
        
        if (response.code === 200) {
          ElMessage.success(`${row.recommend === '1' ? '取消推荐' : '推荐'}成功`)
          // 更新本地数据
          row.recommend = newRecommend
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('更新推荐状态失败:', error)
        ElMessage.error('更新推荐状态失败')
      }
    }

    const deleteCoffee = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个咖啡吗？删除后不可恢复！', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await deleteCoffeeApi(id)
        
        if (response.code === 200) {
          ElMessage.success('删除成功')
          loadCoffeeList()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      }
    }

    const handleCloseAddDialog = () => {
      showAddDialog.value = false
      resetAddForm()
    }

    const handleCloseEditDialog = () => {
      showEditDialog.value = false
      editForm.value.image = null // 清空图片文件
    }

    const resetAddForm = () => {
      addForm.value = {
        name: '',
        price: '',
        description: '',
        category: '',
        stock: 0,
        status: '1',
        recommend: '2',
        image: null,
        imagePreview: ''
      }
      if (addFormRef.value) {
        addFormRef.value.clearValidate()
      }
    }

    const getImageUrl = (url) => {
      if (!url) return url;
      // 确保路径以斜杠开头，处理相对路径
      let imageUrl = url;
      if (!imageUrl.startsWith('/')) {
        imageUrl = '/' + imageUrl;
      }
      // 添加时间戳参数以防止浏览器缓存
      return imageUrl + '?t=' + Date.now();
    }

    // 切换库存排序
    const toggleStockSort = () => {
      // 按照无排序 -> 升序 -> 降序 -> 无排序 的顺序循环切换
      if (stockSortOrder.value === '') {
        stockSortOrder.value = 'asc'
      } else if (stockSortOrder.value === 'asc') {
        stockSortOrder.value = 'desc'
      } else if (stockSortOrder.value === 'desc') {
        stockSortOrder.value = ''
      }
      // 如果按库存排序，则清空销量排序状态
      if (stockSortOrder.value) {
        salesSortOrder.value = ''
      } else {
        // 如果切换回无排序状态，也清空销量排序
        if (!stockSortOrder.value) {
          salesSortOrder.value = ''
        }
      }
      applyCurrentSort()
    }

    // 切换销量排序
    const toggleSalesSort = () => {
      // 按照无排序 -> 升序 -> 降序 -> 无排序 的顺序循环切换
      if (salesSortOrder.value === '') {
        salesSortOrder.value = 'asc'
      } else if (salesSortOrder.value === 'asc') {
        salesSortOrder.value = 'desc'
      } else if (salesSortOrder.value === 'desc') {
        salesSortOrder.value = ''
      }
      // 如果按销量排序，则清空库存排序状态
      if (salesSortOrder.value) {
        stockSortOrder.value = ''
      } else {
        // 如果切换回无排序状态，也清空库存排序
        if (!salesSortOrder.value) {
          stockSortOrder.value = ''
        }
      }
      applyCurrentSort()
    }

    // 监听搜索关键词变化
    const onSearchChange = () => {
      if (searchKeyword.value) {
        // 如果搜索框有内容，清空类别筛选
        selectedCategory.value = ''
      }
      applyCurrentSort()
    }

    // 监听类别变化
    const onCategoryChange = () => {
      if (selectedCategory.value) {
        // 如果选择了类别，清空搜索框
        searchKeyword.value = ''
      }
      applyCurrentSort()
    }
    
    // 过滤数据
    const filterData = (list) => {
      let filteredList = [...list]
      
      // 按搜索关键词过滤（只匹配咖啡名称）
      if (searchKeyword.value) {
        filteredList = filteredList.filter(item => 
          item.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
        )
      }
      // 按类别筛选（只有在没有搜索关键词时才应用）
      else if (selectedCategory.value) {
        filteredList = filteredList.filter(item => 
          item.category === selectedCategory.value
        )
      }
      
      return filteredList
    }

    // 应用当前排序
    const applyCurrentSort = () => {
      if (!originalCoffeeList.value || originalCoffeeList.value.length === 0) return

      let sortedList = [...originalCoffeeList.value]
      
      // 先应用过滤条件
      sortedList = filterData(sortedList)

      if (stockSortOrder.value) {
        sortedList.sort((a, b) => {
          if (stockSortOrder.value === 'asc') {
            return a.stock - b.stock
          } else {
            return b.stock - a.stock
          }
        })
      } else if (salesSortOrder.value) {
        sortedList.sort((a, b) => {
          if (salesSortOrder.value === 'asc') {
            return a.sales - b.sales
          } else {
            return b.sales - a.sales
          }
        })
      }
      coffeeList.value = sortedList
    }

    // 清除排序
    const clearSort = () => {
      stockSortOrder.value = ''
      salesSortOrder.value = ''
      if (originalCoffeeList.value) {
        coffeeList.value = [...originalCoffeeList.value]
      }
    }

    return {
      coffeeList,
      loading,
      showAddDialog,
      showEditDialog,
      addFormRef,
      editFormRef,
      addForm,
      editForm,
      formRules,
      loadCoffeeList,
      handleImageChange,
      handleEditImageChange,
      handleAddCoffee,
      handleEditCoffee,
      editCoffee,
      toggleStatus,
      toggleRecommend,
      deleteCoffee,
      handleCloseAddDialog,
      handleCloseEditDialog,
      getImageUrl,
      // 排序相关
      stockSortOrder,
      salesSortOrder,
      toggleStockSort,
      toggleSalesSort,
      applyCurrentSort,
      clearSort,
      // 搜索和筛选相关
      searchKeyword,
      selectedCategory,
      filterData,
      onSearchChange,
      onCategoryChange,
      // 登录状态相关
      isLoggedIn,
      userInfo
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

.sort-header-inline {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.sort-header-inline .el-button {
  padding: 4px 8px;
  font-size: 12px;
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

.operation-buttons {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.filter-header-inline {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.filter-header-inline .el-select {
  margin-left: 10px;
}

.table-header-text {
  font-size: 16px; /* 您可以修改这个值来控制字体大小 */
}

.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}

.image-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.footer {
  background-color: #333;
  color: white;
  text-align: center;
  line-height: 60px;
}

/* 美化表格操作列按钮 */
.el-table .cell .el-button {
  margin-right: 6px;
  margin-bottom: 3px;
}

.fixed-width-btn {
  min-width: 80px;
  text-align: center;
}
</style>