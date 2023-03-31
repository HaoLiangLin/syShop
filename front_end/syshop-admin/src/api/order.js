import request from '@/utils/request.js'

// 查询订单
export function queryOrder(page, size, id, uid, phone, isPay, logisticsStatus, status, timeSort, priceSort) {
  return request.post(`/orders/query/${page}/${size}`, {
    id, uid, phone, isPay, logisticsStatus, status, timeSort, priceSort
  })
}

// 修改订单
export function updateOrder(id, remarks, name, phone, province, city, district, address) {
  return request.put('/orders/update', {
    id, remarks, name, phone, province, city, district, address
  })
}

/**
 * 修改订单状态
 * @param {*} orderId 订单号
 * @param {*} logisticsStatus 0：未发货，1：待收货，2：已收货，3：待换货，4：已换货，5：待退货，6：已退货
 * @param {*} status 0：未完成，1：已完成，2：待退款，3：已退款
 * @returns function
 */
export function updateOrderStatus(orderId, logisticsStatus, status) {
  return request.put(`/orders/update/status/${orderId}`, {
    logisticsStatus, status
  })
}

// 订单统计
export function orderCount(startDate, endDate) {
  return request.get(`/orders/count/${startDate}/${endDate}`)
}

export default {
  queryOrder,
  updateOrder,
  updateOrderStatus,
  orderCount
}
