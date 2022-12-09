import request from '@/utils/request.js'

// 查询一级商品分类
export function queryGoodsCategoryFirst() {
  return request.get('/goodsCategory/query/one')
}

// 查询商品分类的子分类
export function queryGoodsCategoryByCid(id) {
  return request.get(`/goodsCategory/query/child/${id}`)
}

// 根据id查询商品
export function queryGoodsById(id) {
  return request.get(`goods/query/${id}`)
}

// 分页查询商品
export function queryGoodsPage(page, size, name, cid, saleSort, recommend, isNew, priceSort) {
  return request.post(`/goods/query/${page}/${size}`, { name, cid, saleSort, recommend, isNew, priceSort })
}

// 查询商品属性
export function queryGoodsItemByGid(gid) {
  return request.get(`/goodsItem/query/${gid}`)
}

// 根据条件查询商品属性
export function queryGoodsItem(gid, color, size, combo, edition) {
  return request.post('/goodsItem/query', { gid, color, size, combo, edition })
}

// 查询评价
export function queryEvaluation(goodsId, sort) {
  return request.get('/evaluation/query', {
    params: {
      goodsId,
      sort
    }
  })
}

// 评价点赞
export function evaluationLiked(id) {
  return request.put(`/evaluation/liked/${id}`)
}

// 新增评价评论
export function saveComments(evaluationId, fid, content) {
  return request.post('/comments/save', { evaluationId, fid, content })
}

// 删除评价评论
export function delComments(id) {
  return request.delete(`/comments/delete/${id}`)
}

// 查询评价评论
export function queryComment(id) {
  return request.get('/comments/query', {
    params: { id }
  })
}

// 评价评论点赞
export function commentsLiked(id) {
  return request.put(`/comments/liked/${id}`)
}

export default {
  queryGoodsCategoryFirst,
  queryGoodsCategoryByCid,
  queryGoodsById,
  queryGoodsPage,
  queryGoodsItemByGid,
  queryGoodsItem,
  queryEvaluation,
  evaluationLiked,
  saveComments,
  delComments,
  queryComment,
  commentsLiked
}
