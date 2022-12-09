import { baseURL } from '@/utils/setting.js'

// 获取图片
export function getImage(name) {
  return `${baseURL}/resources/image?name=${name}`
}

// 上传商品分类图标
export function uploadGoodsCategoryIcon() {
  return `${baseURL}/goodsCategory/uploadIcon`
}

// 上传商品分类图标
export function uploadGoodIcon() {
  return `${baseURL}/goods/uploadImages`
}

// 上传商品属性图标
export function uploadGoodsItemIcon() {
  return `${baseURL}/goodsItem/uploadIcon`
}

export default {
  getImage,
  uploadGoodsCategoryIcon,
  uploadGoodIcon,
  uploadGoodsItemIcon
}
