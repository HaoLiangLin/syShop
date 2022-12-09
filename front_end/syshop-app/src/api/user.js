import request from '@/utils/request.js'
import qs from 'qs'

// 用户签到
export function signIn() {
  return request.post('/users/signIn')
}

// 查询签到
export function signInToday() {
  return request.get('/users/signIn/today')
}

// 查询连续签到天数
export function signInCount() {
  return request.get('/users/signIn/count')
}

// 查询个人用户
export function me() {
  return request.get('/users/me')
}

// 修改密码
export function updatePassword(newPassword, oldPassword) {
  return request.put('/users/setPassword',
    {
      newPassword,
      oldPassword
    }
  )
}

// 用户头像上传
export function uploadIcon(file) {
  return request.post('/users/uploadIcon', file)
}

// 修改用户头像
export function updateIcon(icon) {
  return request.put('/users/updateIcon', { icon })
}

// 修改用户昵称
export function updateNickName(nickname) {
  return request.put('/users/updateNickName', { nickname })
}

// 修改用户账号
export function updateUsername(username) {
  return request.put('/users/updateUsername', { username })
}

// 查询用户是否修改过用户名
export function checkIsNotUpdateUsername() {
  return request.get('/users/isNotUpdateUsername')
}

// 修改手机号：获取验证码
export function sendUpdatePhoneCode(phone) {
  return request.get('/users/codePhone',
    {
      params: {
        phone
      }
    }
  )
}

// 修改手机号
export function updatePhone(phone, code) {
  return request.put(`/users/updatePhone/${phone}/${code}`)
}

// 查询用户个人信息
export function userInfoMe() {
  return request.get('/userInfo/me')
}

// 修改个人信息
export function updateUserInfo(fullName, gender, age, email, birthday, qq) {
  return request.put('/userInfo/update', { fullName, gender, age, email, birthday, qq })
}

// 查询收货地址
export function queryAddress() {
  return request.get('/address/list')
}

// 查询默认收货地址
export function queryDefaultAddress() {
  return request.get('/address/default')
}

// 根据id查询收货地址
export function queryAddressById(id) {
  return request.get(`/address/query/${id}`)
}

// 新增收货地址
export function saveAddress(name, phone, province, city, district, address, isDefault) {
  return request.post('/address/save', { name, phone, province, city, district, address, isDefault })
}

// 修改收货地址
export function updateAddress(id, uid, name, phone, province, city, district, address, isDefault) {
  return request.put('/address/update', { id, uid, name, phone, province, city, district, address, isDefault })
}

// 删除收货地址
export function deleteAddress(id) {
  return request.delete(`/address/delete/${id}`)
}

// 批量删除收货地址
export function deleteAddresses(ids) {
  return request.delete('/address/deletes',
    {
      params: {
        ids,
        type: 1
      },
      paramsSerializer: {
        serialize(params) {
          return qs.stringify(params, { indices: false })
        }
      },
      'Content-Type': 'application/json;charset=UTF-8'
    }
  )
}

// 查询用户钱包
export function queryAccount() {
  return request.get('/account/query')
}

// 钱包充值
export function recharge(rechargeComboId, recharge) {
  return request.put('/account/recharge', { rechargeComboId, recharge })
}

// 查询账单
export function queryBill(year, month, day) {
  return request.post('/account/bill', { year, month, day })
}

// 新增购物车
export function saveShoppingCart(gid, quantity) {
  return request.post('/shoppingCart/save', {
    gid,
    quantity
  })
}

// 删除购物车
export function deleteShoppingCart(id) {
  return request.delete(`/shoppingCart/delete/${id}`)
}

// 批量删除购物车
export function bulkDeleteShoppingCart(ids) {
  return request.delete('/shoppingCart/deletes', {
    params: {
      ids,
      type: 1
    },
    paramsSerializer: {
      serialize(params) {
        return qs.stringify(params, { indices: false })
      }
    },
    'Content-Type': 'application/json;charset=UTF-8'
  })
}

// 清空购物车
export function emptyShoppingCart() {
  return request.delete('/shoppingCart/empty')
}

// 修改购物车
export function updateShoppingCart(id, goodsItemId, quantity) {
  return request.put('/shoppingCart/update', { id, goodsItemId, quantity })
}

// 根据id查询购物车
export function queryShoppingCartById(id) {
  return request.get(`/shoppingCart/query/${id}`)
}

// 查询全部购物车
export function queryShoppingCart() {
  return request.get('/shoppingCart/query')
}

// 新增收藏
export function saveCollection(gid) {
  return request.post(`/collection/save/${gid}`)
}

// 取消收藏
export function deleteCollection(gid) {
  return request.delete(`/collection/delete/${gid}`)
}

// 查询收藏
export function queryCollection() {
  return request.get('/collection/query')
}

// 根据商品id查询是否收藏
export function queryCollectionById(gid) {
  return request.get(`/collection/query/${gid}`)
}

export default {
  signIn,
  signInToday,
  signInCount,
  me,
  updatePassword,
  uploadIcon,
  updateIcon,
  updateNickName,
  updateUsername,
  checkIsNotUpdateUsername,
  sendUpdatePhoneCode,
  updatePhone,
  userInfoMe,
  updateUserInfo,
  queryAddress,
  queryDefaultAddress,
  queryAddressById,
  saveAddress,
  updateAddress,
  deleteAddress,
  deleteAddresses,
  queryAccount,
  recharge,
  queryBill,
  saveShoppingCart,
  deleteShoppingCart,
  bulkDeleteShoppingCart,
  emptyShoppingCart,
  updateShoppingCart,
  queryShoppingCartById,
  queryShoppingCart,
  saveCollection,
  deleteCollection,
  queryCollection,
  queryCollectionById
}
