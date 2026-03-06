import request from '@/utils/request'

/**
 * 咖啡相关API
 */

// 获取所有上架的咖啡
export const getAllCoffee = () => {
  return request.get('/api/coffee/list')
}

// 根据ID获取咖啡详情
export const getCoffeeById = (id) => {
  return request.get(`/api/coffee/${id}`)
}

// 根据分类获取咖啡
export const getCoffeeByCategory = (category) => {
  return request.get(`/api/coffee/category/${category}`)
}

// 获取推荐的咖啡
export const getRecommendedCoffee = () => {
  return request.get('/api/coffee/recommended')
}