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
      <el-input
        placeholder="请输入登录密钥"
        v-model="loginKey"
        prefix-icon="el-icon-key"
        show-password>
      </el-input>
      <el-button type="primary" :disabled="username == '' || password == '' || loginKey== ''" @click="onLogin">登 录</el-button>
      <span @click="loginKeyVisible = true">获取登录密钥</span>
      <el-dialog
        title="获取登录密钥"
        :visible.sync="loginKeyVisible"
        width="30%">
        <el-input
          placeholder="请输入用户"
          v-model="usernameKey"
          prefix-icon="el-icon-user-solid"
          clearable>
        </el-input>
        <el-input
          placeholder="请输入密码"
          v-model="passwordKey"
          prefix-icon="el-icon-lock"
          show-password>
        </el-input>
        <div class="btn">
          <el-button @click="loginKeyVisible = false">取 消</el-button>
          <el-button type="primary" @click="onLoginKey">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { login, getLoginKey } from '@/api/user.js'
export default {
  name: 'Login-Card',
  data() {
    return {
      username: '',
      password: '',
      usernameKey: '',
      passwordKey: '',
      loginKey: '',
      loginKeyVisible: false
    }
  },
  methods: {
    onLogin() {
      sessionStorage.removeItem('authorization')
      login(this.username, this.password, this.loginKey).then(res => {
        const data = res.data
        if (data.code === 20011) {
          const jwt = data.data
          sessionStorage.setItem('authorization', jwt)
          this.$router.replace('/home')
          this.$message({
            message: data.message,
            type: 'success'
          })
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
    },
    onLoginKey() {
      getLoginKey(this.usernameKey, this.passwordKey).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.loginKey = data.data
          this.$message({
            message: data.message,
            type: 'success'
          })
          this.loginKeyVisible = false
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
  height: 350px;
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
    span{
      color: #b2b2b2;
      float: right;
      font-size: 14px;
      padding: 5px 0;
      padding-right: 10px;

      &:hover{
        cursor: pointer;
      }
    }
    /deep/.el-dialog{
      .btn{
        text-align: center;
        .el-button{
          width: 40%;
        }
      }
    }
  }
}
</style>
