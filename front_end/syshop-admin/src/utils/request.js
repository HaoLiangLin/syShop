import axios from 'axios'
import { baseURL } from '@/utils/setting.js'

const request = axios.create({
  baseURL,
  timeout: 5000
})

request.interceptors.request.use(config => {
  // 为请求头添加token字段为服务器返回的token
  config.headers.authorization = sessionStorage.getItem('authorization')
  return config
})

export default request
