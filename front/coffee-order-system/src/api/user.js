import request from '@/utils/request'

/**
 * 用户相关API
 */

// 用户登录
export const login = (data) => {
  return request.post('/api/user/login', data)
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export const getUserInfo = (id) => {
  return request.get(`/api/user/${id}`)
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request({
    url: '/api/user/update',
    method: 'put',
    data
  })
}