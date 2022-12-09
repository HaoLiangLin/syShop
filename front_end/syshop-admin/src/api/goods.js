import request from '@/utils/request.js'

// 分页查询商品一级分类
export function queryGoodsCateFirthList(page, size) {
  return request.get(`/goodsCategory/list/${page}/${size}`)
}

// 查询商品一级分类
export function queryGoodsCategoryFirst() {
  return request.get('/goodsCategory/query/one')
}

// 查询商品分类子分类
export function queryGoodsCategoryByCid(id) {
  return request.get(`/goodsCategory/query/child/${id}`)
}

// 查询全部分类
export function findSelectCategory() {
  return request.get('/goodsCategory/select')
}

// 上传商品分类图标
export function uploadGoodsCateIcon(flie) {
  return request.post('/goodsCategory/uploadIcon', {
    flie
  })
}

// 新增商品分类
export function saveGoodsCategory(name, icon, fid, remark) {
  return request.post('/goodsCategory/save', {
    name, icon, fid, remark
  })
}

// 修改商品分类
export function updateGoodsCategory(id, name, icon, fid, remark) {
  return request.put('/goodsCategory/update', {
    id, name, icon, fid, remark
  })
}

// 删除商品分类
export function deleteGoodsCategory(id) {
  return request.delete(`/goodsCategory/delete/${id}`)
}

// 分页查询商品
export function queryGoodsPage(page, size, id, cid, province, city, district, recommend, warrantyTime, refundTime, changerTime, status) {
  return request.post(`/goods/list/${page}/${size}`, { id, cid, province, city, district, recommend, warrantyTime, refundTime, changerTime, status })
}

// 根据id查询商品
export function queryGoodsById(id) {
  return request.get('/goods/find', {
    params: {
      id
    }
  })
}

// 新增商品
export function saveGoods(name, images, cid, province, city, district, address, postage, warrantyTime, refundTime, changerTime) {
  return request.post('/goods/save', {
    name, images, cid, province, city, district, address, postage, warrantyTime, refundTime, changerTime
  })
}

// 删除商品
export function deleteGoods(id) {
  return request.delete(`/goods/delete/${id}`)
}

// 修改商品
export function updateGoods(id, name, images, cid, province, city, district, address, postage, recommend, warrantyTime, refundTime, changerTime, status) {
  return request.put('/goods/update', {
    id, name, images, cid, province, city, district, address, postage, recommend, warrantyTime, refundTime, changerTime, status
  })
}

// 上传商品属性图片
export function uploadGoodsItemIcon(file) {
  return request.post('/goodsItem/uploadIcon', {
    file
  })
}

// 新增商品属性
export function saveGoodsItem(gid, icon, color, size, combo, edition, price, discount, stock, remark) {
  return request.post('/goodsItem/save', {
    gid, icon, color, size, combo, edition, price, discount, stock, remark
  })
}

// 删除商品属性
export function deleteGoodsItem(id) {
  return request.delete(`/goodsItem/delete/${id}`)
}

// 修改商品属性
export function updateGoodsItem(id, gid, color, icon, size, combo, edition, price, discount, stock, remark, status) {
  return request.put(`/goodsItem/update/${id}`, {
    gid, color, icon, size, combo, edition, price, discount, stock, remark, status
  })
}

// 查询商品属性
export function queryGoodsItem(gid, color, combo, size, edition, priceSort, stockSort, salesSort, status) {
  return request.post(`/goodsItem/all/${gid}`, {
    gid, color, combo, size, edition, priceSort, stockSort, salesSort, status
  })
}

export default {
  queryGoodsCateFirthList,
  queryGoodsCategoryFirst,
  queryGoodsCategoryByCid,
  findSelectCategory,
  uploadGoodsCateIcon,
  saveGoodsCategory,
  updateGoodsCategory,
  deleteGoodsCategory,
  queryGoodsPage,
  queryGoodsById,
  saveGoods,
  deleteGoods,
  updateGoods,
  uploadGoodsItemIcon,
  saveGoodsItem,
  deleteGoodsItem,
  updateGoodsItem,
  queryGoodsItem
}
