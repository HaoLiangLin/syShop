import { baseURL } from '@/utils/setting.js'

// 获取图片
export function getImage(name) {
  return `${baseURL}/resources/image?name=${name}`
}

export default {
  getImage
}
