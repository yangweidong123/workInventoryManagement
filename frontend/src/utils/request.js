import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const data = error.response.data
      if (error.response.status === 401) {
        Message.error('登录已过期，请重新登录')
        localStorage.clear()
        router.push('/login')
      } else if (data && data.message) {
        Message.error(data.message)
      } else {
        Message.error(error.message || '网络错误')
      }
    } else {
      Message.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
