import request from '@/utils/request.js'

// 查询订单
export function queryOrder(page, size, id, uid, isPay, logisticsStatus, status, timeSort, priceSort) {
  return request.post(`/orders/query/${page}/${size}`, {
    id, uid, isPay, logisticsStatus, status, timeSort, priceSort
  })
}

// 修改订单
export function updateOrder(id, logisticsStatus, status, remarks, name, phone, province, city, district, address) {
  return request.put('/orders/update', {
    id, logisticsStatus, status, remarks, name, phone, province, city, district, address
  })
}

// 订单统计
export function orderCount(startDate, endDate) {
  return request.get(`/orders/count/${startDate}/${endDate}`)
}

export default {
  queryOrder,
  updateOrder,
  orderCount
}
