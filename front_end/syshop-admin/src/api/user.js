import request from '@/utils/request.js'

// 获取登录令牌
export function getLoginKey(username, password) {
  return request.post('/users/loginToken',
    {
      username,
      password
    }
  )
}

// 管理员登录
export function login(username, password, adminKey) {
  return request.post(`/users/adminLogin/${adminKey}`,
    {
      username,
      password,
      loginType: 0
    },
    'json',
    'appliaction/json'
  )
}

// 退出登录
export function logout() {
  return request.delete('/users/logout')
}

// 查询个人用户
export function me() {
  return request.get('/users/me')
}

// 分页查询用户(可包含条件)
export function findUserListForPage(page, size, id, username, phone, status, createTimeSort, lastLoginSort) {
  return request.post(`/users/find/${page}/${size}`, {
    id, username, phone, status, createTimeSort, lastLoginSort
  })
}

// 分页查询系统用户(可包含条件)
export function findSystemUserListForPage(page, size, id, username, phone, status, createTimeSort, lastLoginSort) {
  return request.post(`/users/findSystem/${page}/${size}`, {
    id, username, phone, status, createTimeSort, lastLoginSort
  })
}

// 根据用户ID查询用户信息
export function queryUserById(userId) {
  return request.get(`/userInfos/query/${userId}`)
}

// 修改用户
export function updateUser(id, phone, nickname, status) {
  return request.put('/users/update', {
    id, phone, nickname, status
  })
}

// 修改系统用户
export function updateSystemUser(id, phone, nickname, status) {
  return request.put('/users/updateSystem', {
    id, phone, nickname, status
  })
}

// 强制用户下线
export function coerceLogout(userId, token) {
  return request.post('/users/coerceLogout', {
    userId,
    token
  })
}

// 禁用用户
export function blockUpUser(userId, blockUpTime) {
  return request.Post(`/users/blockUp/${userId}/${blockUpTime}`)
}

// 取消禁用
export function cancelBlockUpUser(userId) {
  return request.post(`/users/blockUp/cancel/${userId}`)
}

// 注册用户统计
export function registerCount(startDate, endDate) {
  return request.get(`/users/registerCount/${startDate}/${endDate}`)
}

// 用户访问量统计
export function userPvCount(startDate, endDate) {
  return request.get(`/users/pvCount/${startDate}/${endDate}`)
}

// 查询用户信息
export function queryUserInfo(userId) {
  return request.get(`/userInfo/query/${userId}`)
}

// 修改用户信息
export function updateUserInfo(id, fullName, gender, age, email, birthday, qq, level) {
  return request.put('/userinfo/updates', {
    id, fullName, gender, age, email, birthday, qq, level
  })
}

export default {
  findUserListForPage,
  findSystemUserListForPage,
  queryUserById,
  updateUser,
  updateSystemUser,
  coerceLogout,
  blockUpUser,
  cancelBlockUpUser,
  registerCount,
  userPvCount,
  queryUserInfo,
  updateUserInfo
}
