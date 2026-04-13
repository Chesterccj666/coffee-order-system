import request from '@/utils/request'

/**
 * 订单相关API
 */

// 创建订单
export const createOrder = (data) => {
  return request.post('/api/order/create', data)
}

// 获取用户订单列表
export const getUserOrders = (userId) => {
  return request.get(`/api/order/list/${userId}`)
}

// 根据用户ID和状态获取订单
export const getUserOrdersByStatus = (userId, status) => {
  return request.get(`/api/order/user/${userId}/status/${status}`)
}

// 获取订单详情
export const getOrderById = (id) => {
  return request.get(`/api/order/${id}`)
}

// 更新订单状态
export const updateOrderStatus = (id, status) => {
  return request.put('/api/order/updateStatus', null, {
    params: {
      id,
      status
    }
  })
}

// 根据订单号获取订单
export const getOrderByOrderNo = (orderNo) => {
  return request.get(`/api/order/byOrderNo/${orderNo}`)
}

// 根据状态获取订单
export const getOrdersByStatus = (status) => {
  return request.get(`/api/order/byStatus/${status}`)
}

// 获取所有订单（按状态，0表示所有状态）
export const getAllOrdersByStatus = (status) => {
  return request.get(`/api/order/allOrders/${status}`)
}

// 获取订单详情
export const getOrderDetail = (id) => {
  return request.get(`/api/order/detail/${id}`)
}

// 取消订单
export const cancelOrder = (orderId) => {
  return request.put(`/api/order/cancel/${orderId}`)
}