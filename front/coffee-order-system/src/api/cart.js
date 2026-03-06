import request from '@/utils/request'

/**
 * 购物车相关API
 */

// 获取用户购物车列表
export const getUserCart = (userId) => {
  return request.get(`/api/cart/list/${userId}`)
}

// 添加商品到购物车
export const addToCart = (data) => {
  return request.post('/api/cart/add', data)
}

// 更新购物车项数量
export const updateCartItemQuantity = (id, quantity) => {
  return request.put('/api/cart/updateQuantity', null, {
    params: {
      id,
      quantity
    }
  })
}

// 删除购物车项
export const deleteCartItem = (id) => {
  return request.delete(`/api/cart/delete/${id}`)
}