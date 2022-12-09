import request from '@/utils/request.js'

// 用户注册：发送验证码
export function sendRegisterCode(phone) {
  return request.get('users/register/code', { params: { phone } })
}

// 用户注册
export function register(username, phone, code, password) {
  return request.post('/users/register',
    {
      username,
      phone,
      code,
      password
    },
    'json',
    'appliaction/json'
  )
}

// 用户名密码登录
export function login(username, password) {
  return request.post('users/login',
    {
      username,
      password,
      loginType: 0
    },
    'json',
    'appliaction/json'
  )
}

// 发送验证码
export function sendCode(phone) {
  return request.get('users/code', { params: { phone } })
}

// 手机号验证码登录
export function phoneLogin(phone, code) {
  return request.post('users/login',
    {
      phone,
      code,
      loginType: 1
    },
    'json',
    'appliaction/json'
  )
}

// 退出登录
export function logout() {
  return request.delete('/users/logout')
}

// 忘记密码：发送验证码
export function sendForgetPasswordCode(phone) {
  return request.get('/users/codePassword', {
    params: {
      phone
    }
  })
}

// 忘记密码： 验证验证码
export function checkForgetPasswordCode(phone, code) {
  return request.get('/users/check/codePassword', {
    params: {
      phone,
      code
    }
  })
}

// 忘记密码：修改密码
export function updatePassword(phone, newPassword) {
  return request.put('/users/updatePassword',
    {
      phone,
      newPassword
    },
    'json',
    'appliaction/json'
  )
}

export default {
  sendRegisterCode,
  register,
  login,
  sendCode,
  phoneLogin,
  logout,
  sendForgetPasswordCode,
  checkForgetPasswordCode,
  updatePassword
}
