import request from '@/utils/request.js'

// 提交订单
export function submitOrder(aid, items, remarks) {
  return request.post('/orders/submit', {
    aid,
    items,
    remarks
  })
}

// 修改订单
export function updateOrder(id, aid, remarks) {
  return request.put(`/orders/update/${id}`, {
    aid,
    remarks
  })
}

// 取消订单
export function cancelOrder(id) {
  return request.delete(`/orders/cancel/${id}`)
}

// 支付订单
export function paymentOrder(id) {
  return request.put(`/orders/payment/${id}`)
}

// 完成订单
export function complete(id) {
  return request.put(`/orders/complete/${id}`)
}

// 删除订单
export function delOrder(id) {
  return request.delete(`/orders/delete/${id}`)
}

// 查询订单
export function getOrder(id) {
  return request.get(`/orders/query/${id}`)
}

// 查询全部订单
export function allOrder() {
  return request.get('/orders/query/all')
}

// 查询用户待付款订单
export function unpaidOrder() {
  return request.get('/orders/query/unpaid')
}

// 查询用户待发货订单
export function beShippedOrder() {
  return request.get('/orders/query/beShipped')
}

// 查询用户待收货订单
export function undeliveredOrder() {
  return request.get('/orders/query/undelivered')
}

// 查询用户已完成订单
export function completeOrder() {
  return request.get('/orders/query/completed')
}

// 查询订单属性
export function queryOrderItemById(id) {
  return request.get(`/orderItem/query/${id}`)
}

// 上传订单评价图片
export function uploadImages(orderItemId, files) {
  return request.post(`/evaluation/uploadImages/${orderItemId}`, files, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 新增订单评价
export function saveEvaluation(orderItemId, stars, images, content) {
  return request.post('/evaluation/save', { orderItemId, stars, images, content })
}

// 删除订单评价
export function delEvaluation(id) {
  return request.delete(`/evaluation/delete/${id}`)
}

// 根据id查询订单评价
export function queryEvaluation(id) {
  return request.get(`/evaluation/query/${id}`)
}

export default {
  submitOrder,
  updateOrder,
  cancelOrder,
  paymentOrder,
  complete,
  delOrder,
  getOrder,
  allOrder,
  unpaidOrder,
  beShippedOrder,
  undeliveredOrder,
  completeOrder,
  queryOrderItemById,
  uploadImages,
  saveEvaluation,
  delEvaluation,
  queryEvaluation
}
