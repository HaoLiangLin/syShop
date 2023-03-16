import request from '@/utils/request.js'

// 查询全部分类
export function findSelectCategory() {
  return request.get('/goodsCategory/select')
}

// 上传或修改商品分类图标
export function uploadOrUpdateGoodsCateIcon(id, flie) {
  return request.post(
    `/goodsCategory/uploadOrUpdateIcon/${id}`,
    { flie }
  )
}

// 新增商品分类
export function saveGoodsCategory(name, fid, remark) {
  return request.post('/goodsCategory/save', {
    name, fid, remark
  })
}

// 修改商品分类
export function updateGoodsCategory(id, name, fid, remark) {
  return request.put('/goodsCategory/update', {
    id, name, fid, remark
  })
}

// 删除商品分类
export function deleteGoodsCategory(id) {
  return request.delete(`/goodsCategory/delete/${id}`)
}

// #######################商品分类结束线#####################################

// 分页查询商品
export function queryGoodsPage(page, size, id, name, cid, province, city, district, recommend, warrantyTime, refundTime, changerTime, status) {
  return request.post(`/goods/list/${page}/${size}`, { id, name, cid, province, city, district, recommend, warrantyTime, refundTime, changerTime, status })
}

// 根据id查询商品
export function queryGoodsById(id) {
  return request.get(`/goods/find/${id}`)
}

// 新增商品
export function saveGoods(name, cid, province, city, district, address, postage, warrantyTime, refundTime, changerTime) {
  return request.post('/goods/save', {
    name, cid, province, city, district, address, postage, warrantyTime, refundTime, changerTime
  })
}

// 修改商品
export function updateGoods(id, name, cid, province, city, district, address, postage, recommend, warrantyTime, refundTime, changerTime, status) {
  return request.put('/goods/update', {
    id, name, cid, province, city, district, address, postage, recommend, warrantyTime, refundTime, changerTime, status
  })
}

// 上传或修改商品封面
export function uploadOrUpdateGoodsImages(id, files) {
  // 创建一个空的FormData对象
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file.raw)
  })
  return request.post(
    `/goods/uploadOrUpdate/images/${id}`,
    {
      files: formData.getAll('files')
    },
    {
      headers: { 'Content-Type': 'multipart/form-data' },
      processData: false
    }
  )
}

// 删除商品封面
export function removeImages(id) {
  return request.delete(`/goods/remove/images/${id}`)
}

// 删除商品
export function deleteGoods(id) {
  return request.delete(`/goods/delete/${id}`)
}

// ###############################商品结束线####################################

// 上传或修改商品属性图片
export function uploadOrUpdateGoodsItemIcon(id, file) {
  return request.post(
    `/goodsItem/uploadOrUpdate/icon/${id}`,
    { file }
  )
}

// 新增商品属性
export function saveGoodsItem(gid, color, size, combo, edition, price, discount, stock, remark) {
  return request.post('/goodsItem/save', {
    gid, color, size, combo, edition, price, discount, stock, remark
  })
}

// 删除商品属性
export function deleteGoodsItem(id) {
  return request.delete(`/goodsItem/delete/${id}`)
}

// 修改商品属性
export function updateGoodsItem(id, gid, color, size, combo, edition, price, discount, stock, remark, status) {
  return request.put(`/goodsItem/update/${id}`, {
    gid, color, size, combo, edition, price, discount, stock, remark, status
  })
}

// 查询商品属性
export function queryGoodsItem(gid, color, combo, size, edition, priceSort, stockSort, salesSort, status) {
  return request.post(`/goodsItem/all/${gid}`, {
    gid, color, combo, size, edition, priceSort, stockSort, salesSort, status
  })
}

// 分页查询商品属性
export function queryGoodsItemForPage(page, number, gid, color, combo, size, edition, priceSort, stockSort, salesSort, status) {
  return request.post(`/goodsItem/list/${page}/${number}`, {
    gid, color, combo, size, edition, priceSort, stockSort, salesSort, status
  })
}

export default {
  findSelectCategory,
  uploadOrUpdateGoodsCateIcon,
  saveGoodsCategory,
  updateGoodsCategory,
  deleteGoodsCategory,
  queryGoodsPage,
  queryGoodsById,
  saveGoods,
  updateGoods,
  uploadOrUpdateGoodsImages,
  removeImages,
  deleteGoods,
  uploadOrUpdateGoodsItemIcon,
  saveGoodsItem,
  deleteGoodsItem,
  updateGoodsItem,
  queryGoodsItem,
  queryGoodsItemForPage
}
