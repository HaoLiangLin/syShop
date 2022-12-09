import request from '@/utils/request.js'
import qs from 'qs'

// 分页查询一级公告类型
export function queryNoticeCategoryFirstPage(page, size) {
  return request.get(`/noticeCategory/list/${page}/${size}`)
}

// 查询公告类型子类
export function queryNoticeCategoryChild(id) {
  return request.get(`/noticeCategory/query/child/${id}`)
}

// 新增公告类型
export function saveNoticeCategory(name, fid, remark) {
  return request.post('/noticeCategory/save', {
    name, fid, remark
  })
}

// 删除公告类型
export function deleteNoticeCategory(id) {
  return request.delete(`/noticeCategory/delete/${id}`)
}

// 修改公告类型
export function updateNoticeCategory(id, name, fid, remark) {
  return request.put('/noticeCategory/update', {
    id, name, fid, remark
  })
}

// 分页查询公告
export function queryNoticeListPage(page, size, id, noticeCategoryId, timeSort) {
  return request.post(`/notices/query/${page}/${size}`, {
    id, noticeCategoryId, timeSort
  })
}

// 发布公告
export function saveNotice(title, cid, content) {
  return request.post('/notices/save', {
    title, cid, content
  })
}

// 删除公告
export function deleteNotice(id) {
  return request.delete(`/notices/delete/${id}`)
}

// 修改公告
export function updateNotice(id, title, cid, content) {
  return request.put(`/notices/update/${id}`, {
    title, cid, content
  })
}

// 查询活动
export function queryEvents() {
  return request.get('/events/query')
}

// 上传活动图片
export function uploadEventsIcon(file) {
  return request.post('/events/uploadIcon', {
    file
  })
}

// 新增活动
export function saveEvents(name, icon, startTime, deadline, remark) {
  return request.post('/events/save', {
    name, icon, startTime, deadline, remark
  })
}

// 删除活动
export function deleteEvents(id) {
  return request.delete(`/events/delete/${id}`)
}

// 修改活动
export function updateEvents(id, name, icon, startTime, deadline, remark) {
  return request.put(`/events/update/${id}`, {
    name, icon, startTime, deadline, remark
  })
}

// 查询活动商品
export function queryEventsGoods(page, size, id) {
  return request.get(`/eventsGoods/query/${page}/${size}/${id}`)
}

// 新增活动商品
export function saveEventsGoods(id, ids) {
  return request.post(`/eventsGoods/save/${id}`, {
    params: {
      ids,
      type: 1
    },
    paramsSerializer: {
      serialize(params) {
        return qs.stringify(params, { indices: false })
      }
    }
  })
}

// 删除活动商品
export function deleteEventsGoods(eventsId, goodsId) {
  return request.delete(`/eventsGoods/delete/${eventsId}/${goodsId}`)
}

// 查询充值套餐
export function queryRechargeCombo() {
  return request.get('/rechargeCombo/query')
}

// 新增充值套餐
export function saveRechargeCombo(name, price, points, discount) {
  return request.post('/rechargeCombo/save', {
    name, price, points, discount
  })
}

// 删除充值套餐
export function deleteRechargeCombo(id) {
  return request.delete(`/rechargeCombo/delete/${id}`)
}

// 修改充值套餐
export function updateRechargeCombo(id, name, price, points, discount) {
  return request.put('/rechargeCombo/update', {
    id, name, price, points, discount
  })
}

export default {
  queryNoticeCategoryFirstPage,
  queryNoticeCategoryChild,
  saveNoticeCategory,
  deleteNoticeCategory,
  updateNoticeCategory,
  queryNoticeListPage,
  saveNotice,
  deleteNotice,
  queryEvents,
  uploadEventsIcon,
  saveEvents,
  deleteEvents,
  updateEvents,
  queryEventsGoods,
  saveEventsGoods,
  deleteEventsGoods,
  queryRechargeCombo,
  saveRechargeCombo
}
