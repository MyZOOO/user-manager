import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // 后端API地址
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 添加 Bearer 前缀，遵循 HTTP 认证标准
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码为200，说明接口请求成功
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      // 未授权 - 只在token完全无效时才退出登录
      // 如果是权限不足，应该抛出错误让调用方处理，而不是自动退出
      ElMessage.error(res.message || '未授权')
      return Promise.reject(new Error(res.message || '未授权'))
    } else {
      // 其他错误
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.error('响应错误：', error)
    if (error.response) {
      if (error.response.status === 401) {
        // HTTP 401 - token 无效或已过期
        ElMessage.error('未授权，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/#/login'
      } else if (error.response.status === 403) {
        // HTTP 403 - 权限不足（这是正常的，不应该自动退出）
        ElMessage.error(error.response.data?.message || '权限不足')
      } else {
        ElMessage.error(error.response.data?.message || '服务器错误')
      }
    } else {
      ElMessage.error('网络错误，请检查您的网络连接')
    }
    return Promise.reject(error)
  }
)

export default service
