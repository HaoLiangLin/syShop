<template>
  <div class="container">
    <van-nav-bar left-arrow :right-text="loginType == 1 ? '登录' : '手机登录'" :fixed="true" :placeholder="true" @click-left="onClickLeft" @click-right="onClickRight"/>
    <div class="loginFrom">
      <div class="title">{{loginType == 0 ? '登录' : '手机登录'}}</div>
      <div class="form">
        <div class="input1" v-if="loginType == 0">
          <input class="username" type="text" v-model="username" @input="clearEnver" placeholder="请输入用户名">
          <span class="clear" v-show="clearStatus">
            <van-icon name="clear" @click="clearClick" />
          </span>
        </div>
        <div class="input2" v-if="loginType == 0">
          <input class="password" :type="passwordType" v-model="password" @input="eyeEnver" placeholder="请输入密码">
          <span class="eye" v-show="eyeStatus">
            <van-icon v-if="eye" name="closed-eye" @click="showClick" />
            <van-icon v-if="!eye" name="eye-o" @click="closeClick" />
          </span>
        </div>
        <div class="input3" v-if="loginType == 1">
          <input class="phone" type="text" v-model="phone" @input="clearEnver2" placeholder="请输入手机号">
          <span class="clear" v-show="clearStatus">
            <van-icon name="clear" @click="clearClick" />
          </span>
        </div>
        <div class="input4" v-if="loginType == 1">
          <input class="code" type="text" v-model="code" placeholder="请输入验证码">
          <van-button plain type="primary" @click="sendCode" v-if="!isSend">{{codeMsg}}</van-button>
          <van-button plain type="primary" disabled v-if="isSend">{{codeMsg}}</van-button>
        </div>
        <div class="forgetPwd">
          <span class="register" @click="onRegister">注册账号</span>
          <span class="not" @click="onForgetPwd" v-if="loginType == 0">找回密码</span>
        </div>
      </div>
    </div>
    <div class="loginBtn">
      <div class="rememeMe">
        <van-checkbox v-model="isRememe" checked-color="#FFA500">记住我</van-checkbox>
      </div>
      <div class="btn">
        <van-button type="primary" @click="onLogin" v-if="!isLogin">登录</van-button>
        <van-button type="primary" disabled v-if="isLogin">登录</van-button>
      </div>
    </div>
    <div class="loginType">
      <van-divider
        :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }"
      >
        更多方式登录
      </van-divider>
      <div class="type">
        <van-icon name="wechat" size="42px" color="rgba(0, 0, 0, 0.5)"/>
      </div>
    </div>
  </div>
</template>

<script>
import { login, sendCode, phoneLogin } from '@/api/login.js'
export default {
  name: 'Login-Content',
  data() {
    return {
      username: '',
      password: '',
      passwordType: 'password',
      phone: '',
      code: '',
      loginType: 0,
      codeMsg: '获取验证码',
      isSend: false,
      isLogin: false,
      clearStatus: false,
      eyeStatus: false,
      eye: true,
      isRememe: false
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onClickRight() {
      if (this.loginType === 1) {
        this.loginType = 0
      } else {
        this.loginType = 1
      }
    },
    clearEnver() {
      this.clearStatus = false
      if (this.username !== '' && this.username !== ' ') {
        this.clearStatus = true
      }
    },
    clearEnver2() {
      this.clearStatus = false
      if (this.phone !== '' && this.phone !== ' ') {
        this.clearStatus = true
      }
    },
    clearClick() {
      this.username = ''
      this.password = ''
      this.phone = ''
      this.clearStatus = false
      this.eyeStatus = false
    },
    eyeEnver() {
      this.eyeStatus = false
      if (this.password !== '' && this.password !== ' ') {
        this.eyeStatus = true
      }
    },
    showClick() {
      this.eye = false
      this.passwordType = 'text'
    },
    closeClick() {
      this.eye = true
      this.passwordType = 'password'
    },
    onRegister() {
      this.$router.push('/register')
    },
    onForgetPwd() {
      this.$router.push('/forgetPwd')
    },
    sendCode() {
      if (!this.phone) {
        this.$toast('手机号不能为空')
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

      sendCode(this.phone)
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
    onLogin() {
      this.isLogin = true
      if (this.loginType === 0) {
        login(this.username, this.password)
          .then(res => {
            const data = res.data
            if (!data.success) {
              this.$notify({ type: 'warning', message: data.message })
              return
            }
            localStorage.setItem('authorization', data.data)
            this.$notify({ type: 'success', message: data.message })
            this.$router.replace('/home')
          })
      } else {
        phoneLogin(this.phone, this.code)
          .then(res => {
            const data = res.data
            if (!data.success) {
              this.$notify({ type: 'warning', message: data.message })
              return
            }
            localStorage.setItem('authorization', data.data)
            this.$notify({ type: 'success', message: data.message })
            this.$router.replace('/home')
          })
      }
      this.isLogin = false
    }
  },
  watch: {
    phone(newVal, oldVal) {
      const rex = /^[0-9]*$/
      if (!rex.test(newVal)) {
        this.phone = oldVal
      }
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
  .loginFrom{
    margin: 0 auto;
    margin-top: 70px;
    padding: 15px 18px;
    // height: 35vh;
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
      input {
        width: 90%;
        height: 25px;
        background-color: transparent;
        border: transparent;
      }
      .input1,.input3 {
        text-align: left;
        width: 100%;
        position: relative;
        border-bottom: 1px solid #c3c3c3;
        margin-bottom: 30px;

        .username,.phone {
          width: 90%;
        }
        .clear {
          width: 10%;
          position: absolute;
          right: 0;
          top: 5px;
          /deep/.van-icon {
            color: #9c9c9c;
            font-size: 20px;
          }
        }
      }
      .input2 {
        text-align: left;
        width: 100%;
        position: relative;
        border-bottom: 1px solid #c3c3c3;
        margin-bottom: 20px;

        .password {
          width: 90%;
        }
        .eye {
          width: 10%;
          position: absolute;
          right: 0;
          top: 5px;
          /deep/.van-icon {
            font-size: 20px;
          }
        }
      }
      .input4{
        text-align: left;
        width: 100%;
        position: relative;
        border-bottom: 1px solid #c3c3c3;
        margin-bottom: 30px;

        .code{
          width: 60%;
        }
        /deep/.van-button{
          background-color: rgba(255, 255, 255, 0.4);
          position: absolute;
          width: 40%;
          right: 0;
          top: -16px;
        }
      }
      .forgetPwd {
        width: 100%;
        height: 18px;
        font-size: 14px;
        margin-bottom: 10px;
        .register {
          color: green;
          float: left;
        }
        .not {
          color: #5b5b5b;
          float: right;
        }
      }
    }
  }
  .loginBtn{
    margin-top: 20px;
    height: 50px;
    display: flex;
    align-items: center;

    .rememeMe{
      flex: 1;
      /deep/.van-checkbox__label{
        color: #6f6f6f;
        size: 16px;
      }
    }
    .btn{
      flex: 3;
      /deep/.van-button{
        background-image: linear-gradient(to right, #1CD8D2 0%, #93EDC7  51%, #1CD8D2  100%);
        margin: 10px;
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
        width: 180px;
        float: right;
      }
    }
  }
  .loginType{
    margin-top: 80px;
    .type{
      display: flex;
      justify-content: center;
    }
  }
}
</style>
