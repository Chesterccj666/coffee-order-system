<template>
  <div class="coffee-management-page">
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
              <span class="logo-sub">咖啡管理</span>
            </span>
          </div>
          
          <nav class="nav-menu">
            <router-link to="/admin/coffee" class="nav-link active">
              <span class="nav-text">咖啡管理</span>
              <span class="nav-underline"></span>
            </router-link>
            <router-link to="/admin/statistics" class="nav-link">
              <span class="nav-text">数据统计</span>
              <span class="nav-underline"></span>
            </router-link>
            <router-link to="/profile" class="nav-link nav-link--user">
              <span class="user-avatar">{{ userInfo.username?.charAt(0) }}</span>
              <span class="nav-text">{{ userInfo.username }}</span>
              <span class="nav-underline"></span>
            </router-link>
          </nav>
        </div>
      </header>

      <!-- 主内容区域 -->
      <main class="management-main">
        <div class="management-wrapper">
          <!-- 装饰性线条 -->
          <div class="decorative-line decorative-line--1"></div>
          <div class="decorative-line decorative-line--2"></div>

          <!-- 页面标题 -->
          <div class="page-header">
            <h1 class="page-title">
              <span class="title-word title-word--1">咖啡</span>
              <span class="title-word title-word--2">管理</span>
            </h1>
            <p class="page-subtitle">管理商品 · 掌控全局</p>
          </div>

          <!-- 统计卡片 -->
          <div class="stats-grid">
            <div class="stat-card stat-card--total">
              <div class="stat-icon">☕</div>
              <div class="stat-content">
                <span class="stat-value">{{ coffeeList.length }}</span>
                <span class="stat-label">咖啡总数</span>
              </div>
              <div class="stat-decoration"></div>
            </div>
            <div class="stat-card stat-card--active">
              <div class="stat-icon">✅</div>
              <div class="stat-content">
                <span class="stat-value">{{ activeCount }}</span>
                <span class="stat-label">上架中</span>
              </div>
              <div class="stat-decoration"></div>
            </div>
            <div class="stat-card stat-card--recommend" @click="toggleRecommendFilter" :class="{ 'active': isFilteringRecommend }" title="点击查看所有已推荐的咖啡">
              <div class="stat-icon">⭐</div>
              <div class="stat-content">
                <span class="stat-value">{{ recommendCount }}</span>
                <span class="stat-label">推荐中</span>
              </div>
              <div class="stat-decoration"></div>
              <div class="stat-tooltip">点击查看所有已推荐的咖啡</div>
            </div>
          </div>

          <!-- 操作栏 -->
          <div class="action-bar">
            <div class="action-left">
              <button class="btn-primary" @click="openAddDialog">
                <span class="btn-icon">＋</span>
                <span>添加咖啡</span>
              </button>
              <button class="btn-secondary" @click="loadCoffeeList">
                <span class="btn-icon">🔄</span>
                <span>刷新列表</span>
              </button>
            </div>
            
            <div class="action-right">
              <!-- 自定义搜索框 -->
              <div class="search-box">
                <span class="search-icon">🔍</span>
                <input 
                  type="text"
                  v-model="searchKeyword" 
                  placeholder="搜索咖啡名称..." 
                  class="search-input"
                  @input="onSearchChange"
                />
                <button 
                  v-if="searchKeyword" 
                  class="search-clear"
                  @click="clearSearch"
                >
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- 数据表格 -->
          <div class="table-wrapper" :class="{ 'is-loading': loading }">
            <div v-if="loading" class="table-loader">
              <div class="loader-spinner"></div>
              <span>加载中...</span>
            </div>
            
            <table class="data-table" v-else>
              <thead>
                <tr>
                  <th class="col-name">咖啡名称</th>
                  <th class="col-price">价格</th>
                  <th class="col-image">图片</th>
                  <th class="col-desc">描述</th>
                  <th class="col-category">
                    <div class="th-with-filter">
                      <span>类别</span>
                      <!-- 自定义类别筛选下拉框 -->
                      <div class="category-filter" ref="categoryFilterRef">
                        <button class="filter-trigger" @click="toggleCategoryDropdown">
                          <span>{{ getCategoryDisplayText(selectedCategory) }}</span>
                          <svg viewBox="0 0 24 24" fill="none" :class="{ rotated: categoryDropdownOpen }">
                            <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                          </svg>
                        </button>
                        <transition name="dropdown-fade">
                          <div class="filter-dropdown" v-show="categoryDropdownOpen">
                            <div 
                              v-for="cat in categoryOptions" 
                              :key="cat.value"
                              class="dropdown-item"
                              :class="{ active: selectedCategory === cat.value }"
                              @click="selectCategory(cat.value)"
                            >
                              <span class="item-icon">{{ cat.icon }}</span>
                              <span class="item-label">{{ cat.label }}</span>
                              <span v-if="selectedCategory === cat.value" class="item-check">✓</span>
                            </div>
                          </div>
                        </transition>
                      </div>
                    </div>
                  </th>
                  <th class="col-stock">
                    <div class="th-with-sort">
                      <span>库存</span>
                      <button class="sort-btn" :class="{ active: stockSortOrder }" @click="toggleStockSort">
                        <span class="sort-icon">{{ getSortIcon(stockSortOrder) }}</span>
                      </button>
                    </div>
                  </th>
                  <th class="col-status">状态</th>
                  <th class="col-recommend">推荐</th>
                  <th class="col-sales">
                    <div class="th-with-sort">
                      <span>销量</span>
                      <button class="sort-btn" :class="{ active: salesSortOrder }" @click="toggleSalesSort">
                        <span class="sort-icon">{{ getSortIcon(salesSortOrder) }}</span>
                      </button>
                    </div>
                  </th>
                  <th class="col-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(coffee, index) in filteredCoffeeList" :key="coffee.id" :style="{ '--row-index': index }">
                  <td class="col-name">
                    <span class="coffee-name">{{ coffee.name }}</span>
                  </td>
                  <td class="col-price">
                    <span class="coffee-price">¥{{ coffee.price }}</span>
                  </td>
                  <td class="col-image">
                    <div class="image-preview" @click="previewImage(getImageUrl(coffee.coffeeImage))">
                      <img :src="getImageUrl(coffee.coffeeImage)" :alt="coffee.name" />
                      <div class="image-overlay">
                        <span>🔍</span>
                      </div>
                    </div>
                  </td>
                  <td class="col-desc">
                    <p class="coffee-desc">{{ coffee.description }}</p>
                  </td>
                  <td class="col-category">
                    <span class="category-tag">{{ coffee.category }}</span>
                  </td>
                  <td class="col-stock">
                    <span class="stock-value" :class="{ 'low-stock': coffee.stock <= 5 }">
                      {{ coffee.stock }}
                    </span>
                  </td>
                  <td class="col-status">
                    <span class="status-badge" :class="{ active: coffee.status === '1' }">
                      <span class="status-dot"></span>
                      {{ coffee.status === '1' ? '上架' : '下架' }}
                    </span>
                  </td>
                  <td class="col-recommend">
                    <span class="recommend-badge" :class="{ active: coffee.recommend === '1' }">
                      <span class="recommend-star">{{ coffee.recommend === '1' ? '★' : '☆' }}</span>
                      {{ coffee.recommend === '1' ? '已推荐' : '未推荐' }}
                    </span>
                  </td>
                  <td class="col-sales">
                    <span class="sales-value">{{ coffee.sales || 0 }}</span>
                  </td>
                  <td class="col-actions">
                    <div class="action-buttons">
                      <button class="action-btn edit-btn" @click="editCoffee(coffee)" title="编辑">
                        <svg viewBox="0 0 24 24" fill="none">
                          <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                          <path d="M18.5 2.5C19.3284 1.67157 20.6716 1.67157 21.5 2.5C22.3284 3.32843 22.3284 4.67157 21.5 5.5L12 15L8 16L9 12L18.5 2.5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                      </button>
                      <button 
                        class="action-btn toggle-status-btn" 
                        :class="{ active: coffee.status === '1' }"
                        @click="toggleStatus(coffee)" 
                        :title="coffee.status === '1' ? '下架' : '上架'"
                      >
                        <svg v-if="coffee.status === '1'" viewBox="0 0 24 24" fill="none">
                          <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                        </svg>
                        <svg v-else viewBox="0 0 24 24" fill="none">
                          <path d="M5 12H19M12 5V19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                        </svg>
                      </button>
                      <button 
                        class="action-btn recommend-btn" 
                        :class="{ active: coffee.recommend === '1' }"
                        @click="toggleRecommend(coffee)" 
                        :title="coffee.recommend === '1' ? '取消推荐' : '推荐'"
                      >
                        <svg viewBox="0 0 24 24" fill="none">
                          <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/>
                        </svg>
                      </button>
                      <button class="action-btn delete-btn" @click="deleteCoffee(coffee.id)" title="删除">
                        <svg viewBox="0 0 24 24" fill="none">
                          <path d="M4 7H20M10 11V17M14 11V17M5 7L6 19C6 19.5304 6.21071 20.0391 6.58579 20.4142C6.96086 20.7893 7.46957 21 8 21H16C16.5304 21 17.0391 20.7893 17.4142 20.4142C17.7893 20.0391 18 19.5304 18 19L19 7M9 7V4C9 3.73478 9.10536 3.48043 9.29289 3.29289C9.48043 3.10536 9.73478 3 10 3H14C14.2652 3 14.5196 3.10536 14.7071 3.29289C14.8946 3.48043 15 3.73478 15 4V7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="filteredCoffeeList.length === 0">
                  <td colspan="10" class="empty-row">
                    <div class="empty-state">
                      <span class="empty-icon">☕</span>
                      <p class="empty-text">暂无咖啡数据</p>
                      <button class="btn-primary" @click="openAddDialog">添加第一杯咖啡</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>

    <!-- 图片预览模态框 -->
    <transition name="modal-fade">
      <div class="image-modal" v-if="showImagePreview" @click="closeImagePreview">
        <div class="modal-backdrop"></div>
        <div class="modal-content">
          <img :src="previewImageUrl" alt="预览" />
          <button class="modal-close" @click="closeImagePreview">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>
    </transition>

    <!-- 添加/编辑咖啡侧滑抽屉 -->
    <transition name="drawer-fade">
      <div class="drawer-overlay" v-if="showFormDrawer" @click="closeFormDrawer"></div>
    </transition>
    <transition name="drawer-slide">
      <div class="form-drawer" v-if="showFormDrawer" :class="{ 'is-editing': isEditing }">
        <div class="drawer-header">
          <h2 class="drawer-title">{{ isEditing ? '编辑咖啡' : '添加咖啡' }}</h2>
          <p class="drawer-subtitle">{{ isEditing ? '修改咖啡信息' : '填写信息添加新咖啡' }}</p>
          <button class="drawer-close" @click="closeFormDrawer">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
        
        <div class="drawer-body">
          <div class="form-group" v-if="isEditing">
            <label class="form-label">咖啡 ID</label>
            <input type="text" class="form-input" v-model="formData.id" disabled />
          </div>
          
          <div class="form-group">
            <label class="form-label">咖啡名称 <span class="required">*</span></label>
            <input 
              type="text" 
              class="form-input" 
              v-model="formData.name" 
              placeholder="请输入咖啡名称"
              :class="{ 'is-error': errors.name }"
            />
            <span class="error-message" v-if="errors.name">{{ errors.name }}</span>
          </div>
          
          <div class="form-group">
            <label class="form-label">价格 <span class="required">*</span></label>
            <div class="price-input-wrapper">
              <span class="price-prefix">¥</span>
              <input 
                type="text" 
                class="form-input price-input" 
                v-model="formData.price" 
                placeholder="请输入价格"
                :class="{ 'is-error': errors.price }"
              />
            </div>
            <span class="error-message" v-if="errors.price">{{ errors.price }}</span>
          </div>
          
          <div class="form-group">
            <label class="form-label">描述 <span class="required">*</span></label>
            <textarea 
              class="form-textarea" 
              v-model="formData.description" 
              placeholder="请输入咖啡描述"
              rows="3"
              :class="{ 'is-error': errors.description }"
            ></textarea>
            <span class="error-message" v-if="errors.description">{{ errors.description }}</span>
          </div>
          
          <div class="form-row">
            <div class="form-group half">
              <label class="form-label">类别 <span class="required">*</span></label>
              <div class="custom-select" ref="formCategorySelectRef">
                <button class="select-trigger" @click="toggleFormCategoryDropdown">
                  <span>{{ getCategoryDisplayText(formData.category) || '请选择类别' }}</span>
                  <svg viewBox="0 0 24 24" fill="none" :class="{ rotated: formCategoryDropdownOpen }">
                    <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </button>
                <transition name="dropdown-fade">
                  <div class="select-dropdown" v-show="formCategoryDropdownOpen">
                    <div 
                      v-for="cat in categoryOptions.filter(c => c.value !== '')" 
                      :key="cat.value"
                      class="dropdown-item"
                      :class="{ active: formData.category === cat.value }"
                      @click="selectFormCategory(cat.value)"
                    >
                      <span class="item-icon">{{ cat.icon }}</span>
                      <span class="item-label">{{ cat.label }}</span>
                    </div>
                  </div>
                </transition>
              </div>
              <span class="error-message" v-if="errors.category">{{ errors.category }}</span>
            </div>
            
            <div class="form-group half">
              <label class="form-label">库存 <span class="required">*</span></label>
              <div class="number-input">
                <button class="number-btn" @click="formData.stock = Math.max(0, formData.stock - 1)">−</button>
                <input type="number" class="form-input number-value" v-model="formData.stock" min="0" />
                <button class="number-btn" @click="formData.stock++">+</button>
              </div>
              <span class="error-message" v-if="errors.stock">{{ errors.stock }}</span>
            </div>
          </div>
          
          <div class="form-row" v-if="!isEditing">
            <div class="form-group half">
              <label class="form-label">状态</label>
              <div class="toggle-switch">
                <button 
                  class="toggle-option" 
                  :class="{ active: formData.status === '1' }"
                  @click="formData.status = '1'"
                >上架</button>
                <button 
                  class="toggle-option" 
                  :class="{ active: formData.status === '2' }"
                  @click="formData.status = '2'"
                >下架</button>
              </div>
            </div>
            
            <div class="form-group half">
              <label class="form-label">推荐</label>
              <div class="toggle-switch">
                <button 
                  class="toggle-option" 
                  :class="{ active: formData.recommend === '1' }"
                  @click="formData.recommend = '1'"
                >推荐</button>
                <button 
                  class="toggle-option" 
                  :class="{ active: formData.recommend === '2' }"
                  @click="formData.recommend = '2'"
                >不推荐</button>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">咖啡图片 <span class="required" v-if="!isEditing || !formData.imagePreview">*</span></label>
            <div class="image-uploader" @click="triggerFileInput">
              <input 
                type="file" 
                ref="fileInputRef" 
                accept="image/*" 
                style="display: none" 
                @change="handleFileChange"
              />
              <div v-if="formData.imagePreview" class="uploaded-image">
                <img :src="formData.imagePreview" alt="预览" />
                <div class="image-hover">
                  <span>更换图片</span>
                </div>
              </div>
              <div v-else class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">点击上传图片</span>
                <span class="upload-hint">支持 JPG、PNG 格式</span>
              </div>
            </div>
            <span class="error-message" v-if="errors.image">{{ errors.image }}</span>
          </div>
        </div>
        
        <div class="drawer-footer">
          <button class="btn-secondary" @click="closeFormDrawer">取消</button>
          <button class="btn-primary" @click="submitForm" :disabled="isSubmitting">
            <span v-if="!isSubmitting">{{ isEditing ? '保存更改' : '添加咖啡' }}</span>
            <span v-else class="loading-dots">
              <span></span><span></span><span></span>
            </span>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCoffeeForAdmin, addCoffee, updateCoffee, deleteCoffee as deleteCoffeeApi, setRecommend, setStatus } from '@/api/admin'

export default {
  name: 'CoffeeManagementView',
  setup() {
    const coffeeList = ref([])
    const originalCoffeeList = ref([])
    const loading = ref(false)
    const showImagePreview = ref(false)
    const previewImageUrl = ref('')
    const showFormDrawer = ref(false)
    const isEditing = ref(false)
    const isSubmitting = ref(false)
    const fileInputRef = ref(null)
    
    const stockSortOrder = ref('')
    const salesSortOrder = ref('')
    const searchKeyword = ref('')
    const selectedCategory = ref('')
    const categoryDropdownOpen = ref(false)
    const formCategoryDropdownOpen = ref(false)
    const categoryFilterRef = ref(null)
    const formCategorySelectRef = ref(null)
    
    // 过滤状态
    const isFilteringRecommend = ref(false)
    
    const cursorRef = ref(null)
    const headerRef = ref(null)
    
    const isLoggedIn = ref(false)
    const userInfo = ref({})
    
    const formData = reactive({
      id: null,
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
    
    const errors = reactive({
      name: '',
      price: '',
      description: '',
      category: '',
      stock: '',
      image: ''
    })

    const categoryOptions = [
      { value: '', label: '全部类别', icon: '📋' },
      { value: '经典意式', label: '经典意式', icon: '☕' },
      { value: '风味拿铁', label: '风味拿铁', icon: '🥛' },
      { value: '风味美式', label: '风味美式', icon: '💧' },
      { value: '奶咖', label: '奶咖', icon: '🐄' },
      { value: '燕麦系列', label: '燕麦系列', icon: '🌾' },
      { value: '单品豆SOE', label: '单品豆SOE', icon: '🫘' },
      { value: '其他', label: '其他', icon: '✨' }
    ]

    const activeCount = computed(() => coffeeList.value.filter(c => c.status === '1').length)
    const recommendCount = computed(() => coffeeList.value.filter(c => c.recommend === '1').length)

    // 过滤后的咖啡列表
    const filteredCoffeeList = computed(() => {
      let result = [...coffeeList.value]
      
      // 应用推荐过滤
      if (isFilteringRecommend.value) {
        result = result.filter(c => c.recommend === '1')
      }
      
      return result
    })

    onMounted(() => {
      checkLoginStatus()
      if (isLoggedIn.value) {
        loadCoffeeList()
      }
      initCustomCursor()
      document.addEventListener('click', handleClickOutside)
    })

    onUnmounted(() => {
      window.removeEventListener('mousemove', handleMouseMove)
      document.removeEventListener('click', handleClickOutside)
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

    const handleClickOutside = (event) => {
      if (categoryFilterRef.value && !categoryFilterRef.value.contains(event.target)) {
        categoryDropdownOpen.value = false
      }
      if (formCategorySelectRef.value && !formCategorySelectRef.value.contains(event.target)) {
        formCategoryDropdownOpen.value = false
      }
    }

    const checkLoginStatus = () => {
      const storedUser = localStorage.getItem('userInfo')
      if (storedUser) {
        userInfo.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        if (userInfo.value.role !== 3) {
          ElMessage.error('您没有权限访问此页面')
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

    // 切换推荐过滤
    const toggleRecommendFilter = () => {
      isFilteringRecommend.value = !isFilteringRecommend.value
    }

    const getCategoryDisplayText = (value) => {
      if (!value) return '全部类别'
      const option = categoryOptions.find(opt => opt.value === value)
      return option ? option.label : '全部类别'
    }

    const toggleCategoryDropdown = () => {
      categoryDropdownOpen.value = !categoryDropdownOpen.value
    }

    const toggleFormCategoryDropdown = () => {
      formCategoryDropdownOpen.value = !formCategoryDropdownOpen.value
    }

    const selectCategory = (value) => {
      selectedCategory.value = value
      categoryDropdownOpen.value = false
      onCategoryChange()
    }

    const selectFormCategory = (value) => {
      formData.category = value
      formCategoryDropdownOpen.value = false
      errors.category = ''
    }

    const clearSearch = () => {
      searchKeyword.value = ''
      onSearchChange()
    }

    const getSortIcon = (order) => {
      if (order === 'asc') return '↑'
      if (order === 'desc') return '↓'
      return '↕'
    }

    const toggleStockSort = () => {
      if (stockSortOrder.value === '') stockSortOrder.value = 'asc'
      else if (stockSortOrder.value === 'asc') stockSortOrder.value = 'desc'
      else stockSortOrder.value = ''
      if (stockSortOrder.value) salesSortOrder.value = ''
      applyCurrentSort()
    }

    const toggleSalesSort = () => {
      if (salesSortOrder.value === '') salesSortOrder.value = 'asc'
      else if (salesSortOrder.value === 'asc') salesSortOrder.value = 'desc'
      else salesSortOrder.value = ''
      if (salesSortOrder.value) stockSortOrder.value = ''
      applyCurrentSort()
    }

    const onSearchChange = () => {
      if (searchKeyword.value) selectedCategory.value = ''
      applyCurrentSort()
    }

    const onCategoryChange = () => {
      if (selectedCategory.value) searchKeyword.value = ''
      applyCurrentSort()
    }

    const filterData = (list) => {
      let filteredList = [...list]
      if (searchKeyword.value) {
        filteredList = filteredList.filter(item => 
          item.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
        )
      } else if (selectedCategory.value) {
        filteredList = filteredList.filter(item => item.category === selectedCategory.value)
      }
      return filteredList
    }

    const applyCurrentSort = () => {
      if (!originalCoffeeList.value.length) return
      let sortedList = [...originalCoffeeList.value]
      sortedList = filterData(sortedList)
      if (stockSortOrder.value) {
        sortedList.sort((a, b) => stockSortOrder.value === 'asc' ? a.stock - b.stock : b.stock - a.stock)
      } else if (salesSortOrder.value) {
        sortedList.sort((a, b) => salesSortOrder.value === 'asc' ? a.sales - b.sales : b.sales - a.sales)
      }
      coffeeList.value = sortedList
    }

    const previewImage = (url) => {
      previewImageUrl.value = url
      showImagePreview.value = true
    }

    const closeImagePreview = () => {
      showImagePreview.value = false
      previewImageUrl.value = ''
    }

    const openAddDialog = () => {
      isEditing.value = false
      resetFormData()
      showFormDrawer.value = true
    }

    const editCoffee = (row) => {
      isEditing.value = true
      formData.id = row.id
      formData.name = row.name
      formData.price = row.price
      formData.description = row.description
      formData.category = row.category
      formData.stock = row.stock
      formData.status = row.status
      formData.recommend = row.recommend
      formData.imagePreview = getImageUrl(row.coffeeImage)
      formData.image = null
      showFormDrawer.value = true
      clearErrors()
    }

    const closeFormDrawer = () => {
      showFormDrawer.value = false
      resetFormData()
      clearErrors()
    }

    const resetFormData = () => {
      formData.id = null
      formData.name = ''
      formData.price = ''
      formData.description = ''
      formData.category = ''
      formData.stock = 0
      formData.status = '1'
      formData.recommend = '2'
      formData.image = null
      formData.imagePreview = ''
    }

    const clearErrors = () => {
      Object.keys(errors).forEach(key => errors[key] = '')
    }

    const triggerFileInput = () => {
      fileInputRef.value?.click()
    }

    const handleFileChange = (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      if (!file.type.startsWith('image/')) {
        ElMessage.error('请选择图片文件')
        return
      }
      
      const reader = new FileReader()
      reader.onload = (e) => {
        formData.imagePreview = e.target.result
      }
      reader.readAsDataURL(file)
      formData.image = file
      errors.image = ''
    }

    const validateForm = () => {
      clearErrors()
      let isValid = true
      
      if (!formData.name.trim()) {
        errors.name = '请输入咖啡名称'
        isValid = false
      } else if (formData.name.length > 20) {
        errors.name = '名称不能超过20个字符'
        isValid = false
      }
      
      if (!formData.price) {
        errors.price = '请输入价格'
        isValid = false
      } else if (!/^\d+(\.\d{1,2})?$/.test(formData.price)) {
        errors.price = '请输入正确的价格格式'
        isValid = false
      }
      
      if (!formData.description.trim()) {
        errors.description = '请输入咖啡描述'
        isValid = false
      }
      
      if (!formData.category) {
        errors.category = '请选择类别'
        isValid = false
      }
      
      if (formData.stock < 0) {
        errors.stock = '库存不能为负数'
        isValid = false
      }
      
      if (!isEditing.value && !formData.image) {
        errors.image = '请上传咖啡图片'
        isValid = false
      }
      
      return isValid
    }

    const submitForm = async () => {
      if (!validateForm()) return
      
      isSubmitting.value = true
      
      try {
        const submitFormData = new FormData()
        if (isEditing.value) submitFormData.append('id', formData.id.toString())
        submitFormData.append('name', formData.name)
        submitFormData.append('price', formData.price)
        submitFormData.append('description', formData.description)
        submitFormData.append('category', formData.category)
        submitFormData.append('stock', formData.stock.toString())
        submitFormData.append('status', formData.status)
        submitFormData.append('recommend', formData.recommend)
        if (formData.image) submitFormData.append('image', formData.image)
        
        const response = isEditing.value 
          ? await updateCoffee(submitFormData)
          : await addCoffee(submitFormData)
        
        if (response.code === 200) {
          ElMessage.success(isEditing.value ? '更新成功' : '添加成功')
          closeFormDrawer()
          loadCoffeeList()
        } else {
          ElMessage.error(response.message)
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      } finally {
        isSubmitting.value = false
      }
    }

    const toggleStatus = async (row) => {
      try {
        const newStatus = row.status === '1' ? '2' : '1'
        const response = await setStatus(row.id, newStatus)
        if (response.code === 200) {
          ElMessage.success(`${row.status === '1' ? '下架' : '上架'}成功`)
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
          row.recommend = newRecommend
        } else if (response.code === 500 && response.message.includes('推荐的咖啡数量已达上限')) {
          ElMessage.error('推荐的咖啡数量已达上限（8个）')
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

    const getImageUrl = (url) => {
      if (!url) return ''
      let imageUrl = url
      if (!imageUrl.startsWith('/')) imageUrl = '/' + imageUrl
      return imageUrl + '?t=' + Date.now()
    }

    return {
      coffeeList,
      filteredCoffeeList,
      loading,
      showImagePreview,
      previewImageUrl,
      showFormDrawer,
      isEditing,
      isSubmitting,
      fileInputRef,
      stockSortOrder,
      salesSortOrder,
      searchKeyword,
      selectedCategory,
      categoryDropdownOpen,
      formCategoryDropdownOpen,
      categoryFilterRef,
      formCategorySelectRef,
      cursorRef,
      headerRef,
      isLoggedIn,
      userInfo,
      formData,
      errors,
      categoryOptions,
      activeCount,
      recommendCount,
      isFilteringRecommend,
      loadCoffeeList,
      getCategoryDisplayText,
      toggleCategoryDropdown,
      toggleFormCategoryDropdown,
      selectCategory,
      selectFormCategory,
      clearSearch,
      getSortIcon,
      toggleStockSort,
      toggleSalesSort,
      onSearchChange,
      onCategoryChange,
      previewImage,
      closeImagePreview,
      openAddDialog,
      editCoffee,
      closeFormDrawer,
      triggerFileInput,
      handleFileChange,
      submitForm,
      toggleStatus,
      toggleRecommend,
      deleteCoffee,
      getImageUrl,
      toggleRecommendFilter
    }
  }
}
</script>

<style scoped>
/* ===== CSS Variables ===== */
.coffee-management-page {
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
  background: radial-gradient(circle at 80% 20%, rgba(232, 184, 109, 0.05) 0%, transparent 50%),
              radial-gradient(circle at 10% 80%, rgba(125, 155, 122, 0.04) 0%, transparent 50%);
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

.logo-area:hover { transform: scale(1.02); }
.logo-icon { font-size: 32px; animation: steamFloat 3s ease-in-out infinite; }
.logo-text { display: flex; flex-direction: column; }
.logo-main { font-family: 'Playfair Display', serif; font-size: 24px; font-weight: 600; color: var(--dark-roast); }
.logo-sub { font-family: 'DM Sans', sans-serif; font-size: 14px; color: var(--warm-brown); letter-spacing: 2px; }

.nav-menu { display: flex; align-items: center; gap: 32px; }
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
.nav-link:hover { color: var(--terracotta); }
.nav-link.active { color: var(--terracotta); }
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
.nav-link.active .nav-underline { width: 100%; }
.nav-link--user { gap: 6px; }
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
}

/* ===== 主内容区域 ===== */
.management-main { padding-top: 120px; padding-bottom: 60px; min-height: 100vh; }
.management-wrapper { max-width: 1500px; margin: 0 auto; padding: 0 40px; position: relative; }

/* ===== 装饰线条 ===== */
.decorative-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, var(--golden), transparent);
  opacity: 0.1;
  pointer-events: none;
}
.decorative-line--1 { top: 20px; right: -50px; width: 250px; height: 1px; transform: rotate(-15deg); }
.decorative-line--2 { bottom: 100px; left: -80px; width: 300px; height: 1px; transform: rotate(-10deg); }

/* ===== 页面标题 ===== */
.page-header { margin-bottom: 32px; }
.page-title {
  font-family: 'Playfair Display', serif;
  font-size: 42px;
  font-weight: 400;
  margin: 0 0 8px 0;
  display: flex;
  gap: 16px;
}
.title-word { display: inline-block; opacity: 0; animation: titleReveal 0.6s ease forwards; }
.title-word--1 { color: var(--espresso); font-style: italic; animation-delay: 0.1s; }
.title-word--2 { color: var(--dark-roast); font-weight: 600; animation-delay: 0.3s; }
.page-subtitle {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  letter-spacing: 3px;
  opacity: 0;
  animation: fadeIn 0.6s ease 0.5s forwards;
}

/* ===== 统计卡片 ===== */
.stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; margin-bottom: 32px; }
.stat-card {
  position: relative;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 24px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  overflow: hidden;
  transition: all 0.3s ease;
}
.stat-card:hover { 
  transform: translateY(-4px); 
  box-shadow: 0 12px 30px rgba(58, 36, 26, 0.1);
  border-color: rgba(212, 163, 115, 0.4);
  background: rgba(255, 255, 255, 0.8);
}

/* 推荐卡片交互样式 */
.stat-card--recommend {
  cursor: pointer;
}
.stat-card--recommend.active {
  background: rgba(212, 163, 115, 0.1);
  border-color: var(--golden);
  box-shadow: 0 8px 20px rgba(212, 163, 115, 0.25);
}
.stat-tooltip {
  position: absolute;
  bottom: -40px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  white-space: nowrap;
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
  z-index: 10;
}
.stat-card--recommend:hover .stat-tooltip {
  bottom: -30px;
  opacity: 1;
}
.stat-card--recommend:hover::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 6px solid rgba(0, 0, 0, 0.8);
  opacity: 1;
  transition: all 0.3s ease;
}
.stat-card--recommend:not(:hover)::after {
  opacity: 0;
}
.stat-icon { font-size: 42px; opacity: 0.8; }
.stat-content { display: flex; flex-direction: column; }
.stat-value { font-family: 'Playfair Display', serif; font-size: 42px; font-weight: 600; line-height: 1.1; }
.stat-label { font-family: 'DM Sans', sans-serif; font-size: 14px; color: var(--warm-brown); opacity: 0.7; }
.stat-decoration {
  position: absolute;
  bottom: -30px;
  right: -30px;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(212, 163, 115, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}
.stat-card--total .stat-value { color: var(--dark-roast); }
.stat-card--active .stat-value { color: var(--sage); }
.stat-card--recommend .stat-value { color: var(--honey); }

/* ===== 操作栏 ===== */
.action-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.action-left { display: flex; gap: 12px; }
.btn-primary, .btn-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 40px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}
.btn-primary {
  background: linear-gradient(135deg, var(--dark-roast), var(--warm-brown));
  color: var(--crema);
  box-shadow: 0 6px 16px rgba(58, 36, 26, 0.15);
}
.btn-primary:hover {
  background: linear-gradient(135deg, var(--espresso), var(--dark-roast));
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(58, 36, 26, 0.2);
}
.btn-secondary {
  background: transparent;
  border: 1.5px solid rgba(212, 163, 115, 0.4);
  color: var(--warm-brown);
}
.btn-secondary:hover { background: rgba(212, 163, 115, 0.08); border-color: var(--golden); }
.btn-icon { font-size: 18px; }

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 40px;
  padding: 6px 16px;
  min-width: 280px;
}
.search-icon { font-size: 16px; opacity: 0.5; margin-right: 8px; }
.search-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 10px 0;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--espresso);
  outline: none;
}
.search-input::placeholder { color: var(--warm-brown); opacity: 0.5; }
.search-clear {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--warm-brown);
  opacity: 0.5;
  cursor: pointer;
  transition: all 0.2s ease;
}
.search-clear:hover { opacity: 1; background: rgba(212, 163, 115, 0.1); }
.search-clear svg { width: 16px; height: 16px; }

/* ===== 表格 ===== */
.table-wrapper {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(212, 163, 115, 0.15);
  border-radius: 24px;
  overflow: hidden;
  position: relative;
  min-height: 400px;
}
.table-loader {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  background: rgba(253, 248, 242, 0.8);
  z-index: 10;
}
.loader-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(212, 163, 115, 0.2);
  border-top-color: var(--golden);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
.data-table { width: 100%; border-collapse: collapse; }
.data-table th {
  background: rgba(212, 163, 115, 0.06);
  padding: 18px 16px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--warm-brown);
  text-align: left;
  border-bottom: 1px solid rgba(212, 163, 115, 0.2);
  white-space: nowrap;
}
.data-table td {
  padding: 16px;
  border-bottom: 1px solid rgba(212, 163, 115, 0.08);
  color: var(--espresso);
}
.data-table tbody tr {
  transition: all 0.3s ease;
  opacity: 0;
  animation: rowFadeIn 0.4s ease forwards;
  animation-delay: calc(var(--row-index) * 0.03s);
}
.data-table tbody tr:hover { background: rgba(212, 163, 115, 0.04); }

.th-with-filter, .th-with-sort { display: flex; align-items: center; gap: 12px; }
.category-filter { position: relative; }
.filter-trigger {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 20px;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--warm-brown);
  cursor: pointer;
  transition: all 0.2s ease;
}
.filter-trigger:hover { border-color: var(--golden); background: rgba(255, 255, 255, 0.8); }
.filter-trigger svg { width: 14px; height: 14px; transition: transform 0.3s ease; }
.filter-trigger svg.rotated { transform: rotate(180deg); }
.filter-dropdown, .select-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  min-width: 180px;
  background: rgba(253, 248, 242, 0.98);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(212, 163, 115, 0.2);
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(58, 36, 26, 0.12);
  overflow: hidden;
  z-index: 200;
}
.dropdown-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.dropdown-item:hover { background: rgba(212, 163, 115, 0.08); }
.dropdown-item.active { background: linear-gradient(135deg, rgba(212, 163, 115, 0.12), rgba(200, 121, 92, 0.06)); }
.item-icon { font-size: 16px; margin-right: 10px; }
.item-label { flex: 1; font-family: 'DM Sans', sans-serif; font-size: 13px; color: var(--warm-brown); }
.item-check { font-size: 14px; color: var(--terracotta); }

.sort-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--warm-brown);
  opacity: 0.5;
  transition: all 0.2s ease;
}
.sort-btn:hover, .sort-btn.active { opacity: 1; background: rgba(212, 163, 115, 0.1); }
.sort-icon { font-size: 14px; }

.coffee-name { font-weight: 500; }
.coffee-price { font-weight: 600; color: var(--terracotta); }
.image-preview {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  box-shadow: 0 4px 10px rgba(58, 36, 26, 0.1);
}
.image-preview img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s ease; }
.image-preview:hover img { transform: scale(1.1); }
.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(30, 27, 26, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
  font-size: 18px;
}
.image-preview:hover .image-overlay { opacity: 1; }
.coffee-desc {
  max-width: 250px;
  margin: 0;
  font-size: 13px;
  color: var(--warm-brown);
  opacity: 0.8;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.category-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(212, 163, 115, 0.1);
  border-radius: 20px;
  font-size: 12px;
  color: var(--warm-brown);
}
.stock-value { font-weight: 500; }
.stock-value.low-stock { color: var(--terracotta); }
.status-badge, .recommend-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
.status-badge { background: rgba(200, 121, 92, 0.1); color: var(--terracotta); }
.status-badge.active { background: rgba(125, 155, 122, 0.12); color: #5C7A5A; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--terracotta); }
.status-badge.active .status-dot { background: var(--sage); animation: pulseDot 2s infinite; }
.recommend-badge { background: rgba(212, 163, 115, 0.08); color: var(--warm-brown); }
.recommend-badge.active { background: rgba(232, 184, 109, 0.12); color: #B8860B; }
.recommend-star { font-size: 12px; }
.sales-value { font-weight: 500; }

.action-buttons { display: flex; gap: 6px; }
.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--warm-brown);
}
.action-btn:hover { background: rgba(212, 163, 115, 0.1); }
.action-btn svg { width: 18px; height: 18px; }
.edit-btn:hover { color: var(--golden); }
.toggle-status-btn:hover { color: var(--sage); }
.toggle-status-btn.active:hover { color: var(--terracotta); }
.recommend-btn:hover { color: var(--honey); }
.recommend-btn.active:hover { color: var(--warm-brown); }
.delete-btn:hover { color: var(--terracotta); background: rgba(200, 121, 92, 0.1); }

.empty-row { text-align: center; }
.empty-state { padding: 60px 40px; }
.empty-icon { font-size: 56px; opacity: 0.3; display: block; margin-bottom: 16px; }
.empty-text { font-family: 'DM Sans', sans-serif; color: var(--warm-brown); margin-bottom: 24px; }

/* ===== 图片预览模态框 ===== */
.image-modal { position: fixed; top: 0; left: 0; right: 0; bottom: 0; z-index: 1000; display: flex; align-items: center; justify-content: center; }
.modal-backdrop { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(30, 27, 26, 0.8); backdrop-filter: blur(8px); }
.modal-content { position: relative; z-index: 1; max-width: 90vw; max-height: 90vh; }
.modal-content img { max-width: 100%; max-height: 90vh; border-radius: 24px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3); }
.modal-close {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 48px;
  height: 48px;
  border: none;
  background: var(--crema);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--dark-roast);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}
.modal-close:hover { transform: scale(1.1); background: var(--terracotta); color: var(--crema); }
.modal-close svg { width: 24px; height: 24px; }

/* ===== 表单抽屉 ===== */
.drawer-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(30, 27, 26, 0.5); backdrop-filter: blur(4px); z-index: 500; }
.form-drawer {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 520px;
  background: var(--crema);
  box-shadow: -10px 0 40px rgba(58, 36, 26, 0.15);
  z-index: 501;
  display: flex;
  flex-direction: column;
  border-left: 1px solid rgba(212, 163, 115, 0.2);
}
.drawer-header { padding: 32px 32px 20px; border-bottom: 1px solid rgba(212, 163, 115, 0.15); position: relative; }
.drawer-title { font-family: 'Playfair Display', serif; font-size: 28px; font-weight: 500; color: var(--espresso); margin: 0 0 4px 0; }
.drawer-subtitle { font-family: 'DM Sans', sans-serif; font-size: 14px; color: var(--warm-brown); opacity: 0.7; margin: 0; }
.drawer-close {
  position: absolute;
  top: 28px;
  right: 28px;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--warm-brown);
  transition: all 0.2s ease;
}
.drawer-close:hover { background: rgba(212, 163, 115, 0.1); color: var(--terracotta); }
.drawer-close svg { width: 20px; height: 20px; }
.drawer-body { flex: 1; overflow-y: auto; padding: 28px 32px; }
.drawer-footer {
  padding: 24px 32px;
  border-top: 1px solid rgba(212, 163, 115, 0.15);
  display: flex;
  gap: 16px;
  justify-content: flex-end;
}

.form-group { margin-bottom: 24px; }
.form-row { display: flex; gap: 20px; }
.form-group.half { flex: 1; }
.form-label {
  display: block;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--warm-brown);
  margin-bottom: 8px;
}
.required { color: var(--terracotta); margin-left: 2px; }
.form-input, .form-textarea {
  width: 100%;
  padding: 14px 16px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 16px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--espresso);
  outline: none;
  transition: all 0.3s ease;
}
.form-input:focus, .form-textarea:focus { border-color: var(--golden); box-shadow: 0 0 0 3px rgba(212, 163, 115, 0.1); }
.form-input.is-error, .form-textarea.is-error { border-color: var(--terracotta); }
.error-message { display: block; font-family: 'DM Sans', sans-serif; font-size: 12px; color: var(--terracotta); margin-top: 6px; }
.price-input-wrapper { position: relative; }
.price-prefix {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--warm-brown);
}
.price-input { padding-left: 36px; }

.custom-select { position: relative; }
.select-trigger {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: var(--cream);
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 16px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--espresso);
  cursor: pointer;
  transition: all 0.3s ease;
}
.select-trigger:hover { border-color: var(--golden); }
.select-trigger svg { width: 18px; height: 18px; color: var(--golden); transition: transform 0.3s ease; }
.select-trigger svg.rotated { transform: rotate(180deg); }
.select-dropdown { width: 100%; top: calc(100% + 8px); }

.number-input { display: flex; align-items: center; gap: 4px; background: var(--cream); border: 1.5px solid rgba(212, 163, 115, 0.2); border-radius: 16px; padding: 4px; }
.number-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 12px;
  font-size: 20px;
  color: var(--warm-brown);
  cursor: pointer;
  transition: all 0.2s ease;
}
.number-btn:hover { background: rgba(212, 163, 115, 0.1); color: var(--terracotta); }
.number-value { border: none; background: transparent; text-align: center; font-weight: 500; padding: 0; }

.toggle-switch { display: flex; gap: 8px; }
.toggle-option {
  flex: 1;
  padding: 12px 16px;
  border: 1.5px solid rgba(212, 163, 115, 0.2);
  border-radius: 30px;
  background: transparent;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--warm-brown);
  cursor: pointer;
  transition: all 0.3s ease;
}
.toggle-option.active { background: var(--dark-roast); border-color: var(--dark-roast); color: var(--crema); }

.image-uploader { cursor: pointer; }
.uploaded-image {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  border-radius: 20px;
  overflow: hidden;
  border: 1.5px solid rgba(212, 163, 115, 0.2);
}
.uploaded-image img { width: 100%; height: 100%; object-fit: cover; }
.image-hover {
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
  color: white;
  font-family: 'DM Sans', sans-serif;
  font-weight: 500;
}
.uploaded-image:hover .image-hover { opacity: 1; }
.upload-placeholder {
  width: 100%;
  aspect-ratio: 1;
  border: 2px dashed rgba(212, 163, 115, 0.3);
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
}
.upload-placeholder:hover { border-color: var(--golden); background: rgba(212, 163, 115, 0.03); }
.upload-icon { font-size: 32px; opacity: 0.6; }
.upload-text { font-family: 'DM Sans', sans-serif; font-size: 14px; color: var(--warm-brown); }
.upload-hint { font-family: 'DM Sans', sans-serif; font-size: 11px; color: var(--warm-brown); opacity: 0.5; }

.loading-dots { display: flex; gap: 4px; justify-content: center; }
.loading-dots span { width: 6px; height: 6px; background: var(--crema); border-radius: 50%; animation: dotPulse 1.4s ease-in-out infinite; }
.loading-dots span:nth-child(2) { animation-delay: 0.2s; }
.loading-dots span:nth-child(3) { animation-delay: 0.4s; }

/* ===== 动画 ===== */
@keyframes steamFloat { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-3px); } }
@keyframes titleReveal { 0% { opacity: 0; transform: translateY(30px); } 100% { opacity: 1; transform: translateY(0); } }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes rowFadeIn { to { opacity: 1; } }
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes pulseDot { 0%, 100% { opacity: 1; transform: scale(1); } 50% { opacity: 0.5; transform: scale(1.2); } }
@keyframes dotPulse { 0%, 60%, 100% { opacity: 0.4; transform: scale(1); } 30% { opacity: 1; transform: scale(1.3); } }

.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.drawer-fade-enter-active, .drawer-fade-leave-active { transition: opacity 0.3s ease; }
.drawer-fade-enter-from, .drawer-fade-leave-to { opacity: 0; }
.drawer-slide-enter-active, .drawer-slide-leave-active { transition: transform 0.35s ease; }
.drawer-slide-enter-from, .drawer-slide-leave-to { transform: translateX(100%); }
.dropdown-fade-enter-active { transition: all 0.2s ease-out; }
.dropdown-fade-leave-active { transition: all 0.15s ease-in; }
.dropdown-fade-enter-from, .dropdown-fade-leave-to { opacity: 0; transform: translateY(-8px); }

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: 1fr; }
  .action-bar { flex-direction: column; gap: 16px; align-items: stretch; }
  .search-box { min-width: 100%; }
}
@media (max-width: 768px) {
  .app-header { padding: 16px 20px; }
  .management-wrapper { padding: 0 20px; }
  .form-drawer { width: 100%; }
  .form-row { flex-direction: column; gap: 0; }
}
</style>