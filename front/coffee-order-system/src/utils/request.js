import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 后端接口地址
  //baseURL: 'http://10.112.73.21:8080', // 后端接口地址（局域网内访问）
  timeout: 60000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 对于FormData请求，移除Content-Type，让浏览器自动设置
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    }
    // 可以在这里添加token等
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  },
)

export default request