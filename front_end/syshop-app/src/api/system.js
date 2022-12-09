import request from '@/utils/request.js'

// 查询公告
export function queryNotices(id, noticeCategoryId, timeSort) {
  return request.post('/notices/query', { id, noticeCategoryId, timeSort })
}

// 查询充值套餐
export function queryRechargeCombo() {
  return request.get('/rechargeCombo/query')
}

// 查询活动
export function quertEvents() {
  return request.get('/events/query')
}

// 查询活动商品
export function queryEventsGoods(page, size, id) {
  return request.get(`/eventsGoods/query/${page}/${size}/${id}`)
}
