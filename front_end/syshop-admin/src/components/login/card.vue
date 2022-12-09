<template>
  <div class="container">
    <div class="title">
      水院商城后台
    </div>
    <div class="form">
      <el-input
        placeholder="请输入用户"
        v-model="username"
        prefix-icon="el-icon-user-solid"
        clearable>
      </el-input>
      <el-input
        placeholder="请输入密码"
        v-model="password"
        prefix-icon="el-icon-lock"
        show-password>
      </el-input>
      <el-button type="primary" :disabled="username == '' || password == ''" @click="onLogin">登 录</el-button>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/user.js'
export default {
  name: 'Login-Card',
  data() {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    onLogin() {
      login(this.username, this.password).then(res => {
        const data = res.data
        if (data.success) {
          const jwt = data.data
          sessionStorage.setItem('authorization', jwt)
          this.$router.replace('/home')
        } else {
          this.$message({
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  width: 500px;
  height: 300px;
  margin: 0 auto;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 5px;
  box-shadow: 1px 1px 5px 1px rgba(0, 0, 0, 0.4);
  padding: 10px;
  .title{
    text-align: center;
    font-size: 22px;
    color: #fff;
    padding: 10px 0;
  }
  .form{
    padding: 20px;
    /deep/.el-input{
      margin-bottom: 30px;
    }
    /deep/.el-button{
      margin-top: 10px;
      width: 100%;
    }
  }
}
</style>
