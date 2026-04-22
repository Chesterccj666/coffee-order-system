import request from '@/utils/request'

/**
 * 管理员相关API
 */

// 获取所有咖啡（包括下架的）
export const getAllCoffeeForAdmin = () => {
  return request.get('/api/coffee/admin/list')
}

// 添加咖啡
export const addCoffee = (formData) => {
  return request.post('/api/coffee/admin/add', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新咖啡
export const updateCoffee = (formData) => {
  return request.put('/api/coffee/admin/update', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除咖啡
export const deleteCoffee = (id) => {
  return request.delete(`/api/coffee/admin/delete/${id}`)
}

// 设置推荐状态
export const setRecommend = (id, recommend) => {
  return request.put(`/api/coffee/admin/set-recommend/${id}/${recommend}`)
}

// 设置上下架状态
export const setStatus = (id, status) => {
  return request.put(`/api/coffee/admin/set-status/${id}/${status}`)
}

// 获取销量最高的咖啡
export const getTopSellingCoffee = (limit) => {
  return request.get(`/api/coffee/admin/top-selling/${limit}`)
}

// 获取所有咖啡类别
export const getAllCoffeeCategories = () => {
  return request.get('/api/coffee/admin/categories')
}

// 获取按类别统计的销售数据
export const getCategorySalesStats = () => {
  return request.get('/api/coffee/admin/sales-stats')
}

// 获取总销售额
export const getTotalSalesAmount = () => {
  return request.get('/api/coffee/admin/total-sales')
}

// 获取历史总销量
export const getTotalCoffeesSold = () => {
  return request.get('/api/coffee/admin/total-coffees-sold')
}

// 获取过去七天的每日销售额
export const getDailySalesForLastWeek = () => {
  return request.get('/api/coffee/admin/daily-sales-last-week')
}

// 获取过去七天的每日销量
export const getDailyQuantityForLastWeek = () => {
  return request.get('/api/coffee/admin/daily-quantity-last-week')
}