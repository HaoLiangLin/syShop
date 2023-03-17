import { baseURL } from '@/utils/setting.js'

// 获取图片
export function getImage(name) {
  return `${baseURL}/resources/image?name=${name}`
}

// 上传或修改用户头像
export function uploadOrUpdateUserIcon(userId) {
  return `${baseURL}/users/uploadOrUpdate/icon/${userId}`
}

// 上传或修改商品分类图标
export function uploadOrUpdateGoodsCategoryIcon(id) {
  return `${baseURL}/goodsCategory/uploadOrUpdateIcon/${id}`
}

// 上传商品属性图标
export function uploadOrUpdateGoodsItemIcon(id) {
  return `${baseURL}/goodsItem/uploadOrUpdate/icon/${id}`
}

export default {
  getImage,
  uploadOrUpdateUserIcon,
  uploadOrUpdateGoodsCategoryIcon,
  uploadOrUpdateGoodsItemIcon
}
