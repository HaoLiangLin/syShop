<template>
  <div class="container">
    <van-nav-bar left-arrow right-text="前往登录" :fixed="true" :placeholder="true" @click-left="onClickLeft" @click-right="onClickRight"/>
    <div class="registerFrom">
      <div class="title">注册</div>
      <div class="form">
        <div class="input1">
          <label>用户名</label>
          <input class="username" type="text" v-model="username" placeholder="请输入注册用户名">
        </div>
        <div class="input2">
          <label>手机号</label>
          <input class="phone" type="text" v-model="phone" placeholder="请输入注册手机号">
        </div>
        <div class="input3">
          <label>验证码</label><br/>
          <input class="code" type="text" v-model="code" placeholder="请输入验证码">
          <van-button plain type="primary" @click="sendCode" v-if="!isSend">{{codeMsg}}</van-button>
          <van-button plain type="primary" disabled v-if="isSend">{{codeMsg}}</van-button>
        </div>
        <div class="input4">
          <label>密码</label>
          <input class="password" type="text" v-model="password" placeholder="请输入密码">
        </div>
      </div>
    </div>
    <div class="registerBtn">
      <van-button type="info" @click="onRegister">立即注册</van-button>
    </div>
    <div class="agreement">
        <van-checkbox v-model="agreement" icon-size="14px" checked-color="#7FDBFF">
          <template #default>
            <span class="agreementText">
              已阅读并同意以下协议:
              <span class="text1"> 《网上商城平台服务协议》 </span>
              <span class="text1"> 《隐私权政策》 </span>
            </span>
          </template>
        </van-checkbox>
      </div>
  </div>
</template>

<script>
import { sendRegisterCode, register } from '@/api/login.js'
export default {
  name: 'Register-Content',
  data() {
    return {
      username: '',
      password: '',
      phone: '',
      code: '',
      codeMsg: '获取验证码',
      isSend: false,
      isLogin: false,
      agreement: false
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onClickRight() {
      this.$router.replace('/login')
    },
    sendCode() {
      if (!this.phone) {
        this.$notify({ type: 'warning', message: '手机号不能为空' })
        return
      }

      this.isSend = true
      let time = 30
      const msg = '秒后重试'

      var interval = setInterval(() => {
        this.codeMsg = time + msg
        time--
        if (time < 0) {
          clearInterval(interval)
          this.isSend = false
          this.codeMsg = '获取验证码'
        }
      }, 1000)

      sendRegisterCode(this.phone)
        .then(res => {
          const data = res.data
          if (!data.success) {
            this.$notify({ type: 'warning', message: data.message })
            clearInterval(interval)
            this.isSend = false
            this.codeMsg = '获取验证码'
          } else {
            console.log(`验证码：${data.data}`)
          }
        })
        .catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
    },
    onRegister() {
      if (!this.username) {
        this.$toast('注册用户名不能为空')
        return
      }
      if (!this.phone) {
        this.$toast('注册手机号不能为空')
        return
      }
      if (!this.password) {
        this.$toast('密码不能为空')
        return
      }
      if (!this.code) {
        this.$toast('验证码不能为空')
        return
      }
      if (!this.agreement) {
        this.$toast('请先阅读并同意协议')
        return
      }
      register(this.username, this.phone, this.code, this.password)
        .then(res => {
          const data = res.data
          if (!data.success) {
            this.$notify({ type: 'warning', message: data.message })
          } else {
            this.$toast.success(data.message)
          }
        })
        .catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  padding: 10px;
  height: 90vh;

  /deep/.van-nav-bar{
    i,span{
      color: black;
    }
  }
  .registerFrom{
    margin: 0 auto;
    margin-top: 70px;
    padding: 15px 18px;
    background-color: rgba(255, 255, 255, 0.3);
    border-radius: 5px;
    box-shadow: 1px 1px 5px 1px rgba(0, 0, 0, 0.4);
    .title{
      font-size: 20px;
      color: #fff;
      margin-bottom: 30px;
      text-align: center;
    }
    .form{
      label{
        font-size: 16px;
        color: #909090;
      }
      input {
        width: 90%;
        height: 25px;
        background-color: transparent;
        border: transparent;
      }
      .input1,.input2 {
        text-align: left;
        width: 100%;
        position: relative;
        border-bottom: 1px solid #c3c3c3;
        margin-bottom: 20px;

        .username,.phone {
          width: 100%;
        }
      }
      .input3{
        text-align: left;
        width: 100%;
        position: relative;
        border-bottom: 1px solid #c3c3c3;
        margin-bottom: 20px;

        .code{
          width: 60%;
        }
        /deep/.van-button{
          background-color: rgba(255, 255, 255, 0.4);
          position: absolute;
          width: 40%;
          right: 0;
          top: 0px;
        }
      }
      .input4 {
        text-align: left;
        width: 100%;
        position: relative;
        border-bottom: 1px solid #c3c3c3;
        margin-bottom: 10px;

        .password {
          width: 100%;
        }
      }
    }
  }
  .registerBtn{
    margin-top: 30px;
    height: 50px;
    width: 100%;

    /deep/.van-button{
      width: 100%;

      background-image: linear-gradient(to right, #00d2ff 0%, #3a7bd5  51%, #00d2ff  100%);
      padding: 15px 45px;
      text-align: center;
      text-transform: uppercase;
      transition: 0.5s;
      background-size: 200% auto;
      color: white;
      border-radius: 10px;
      display: block;

      &:hover {
        background-position: right center; /* change the direction of the change here */
        color: #fff;
        text-decoration: none;
      }
    }
  }
  .agreement{
    margin-top: 30px;
    .agreementText {
        font-size: 12px;
        color: DarkGray;
        .text1 {
          font-size: 12px;
          color: black;
        }
      }
  }
}
</style>
