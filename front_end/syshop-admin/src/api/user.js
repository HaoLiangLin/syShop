import request from '@/utils/request.js'
import qs from 'qs'

// 登录
export function login(username, password) {
  return request.post('/users/loginEnd/1',
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
export function queryUserListPage(page, size, id, username, phone, status) {
  return request.post(`/users/list/${page}/${size}`, {
    id, username, phone, status
  })
}

// 根据条件查询用户
export function queryUser(id, username, phone, status) {
  return request.post('/users/query', {
    id, username, phone, status
  })
}

// 修改用户
export function updateUser(id, phone, nickname, status) {
  return request.put('/users/update', {
    id, phone, nickname, status
  })
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/users/delete/${id}`)
}

// 批量删除用户
export function deteteUsers(ids) {
  return request.delete('/users/deletes', {
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

// 分页查询用户信息(可包含条件)
export function queryUserInfoListPage(page, size, gender, level) {
  return request.post(`/userInfo/list/${page}/${size}`, {
    gender, level
  })
}

// 查询用户信息
export function queryUserInfo(id, level) {
  return request.post('/userInfo/query', {
    id, level
  })
}

// 修改用户信息
export function updateUserInfo(id, fullName, gender, age, email, birthday, qq, level) {
  return request.put('/userinfo/updates', {
    id, fullName, gender, age, email, birthday, qq, level
  })
}

export default {
  queryUserListPage,
  queryUser,
  updateUser,
  deleteUser,
  deteteUsers,
  queryUserInfoListPage,
  queryUserInfo,
  updateUserInfo
}
