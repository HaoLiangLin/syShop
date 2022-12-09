<template>
  <div class="container">
    <div class="icon">
      <van-image width="70" height="70" round :src="setIcon(icon)" />
    </div>
    <div class="accountInfo" @click="onUserInfo" v-if="jwt">
      <div class="nickname">{{nickname}}</div>
      <div class="account">用户名: {{username}}</div>
    </div>
    <div class="accountInfo" v-else>
      <div class="nickname" style="padding-top: 20px">
        <span @click="onLogin">登录</span>/<span @click="onRegister">注册</span>
      </div>
    </div>
    <div class="signIn" v-if="jwt">
      <van-button round type="info" @click="onSignIn" :disabled="isSignIn">{{isSignIn ? '今日已签到' : '签到送积分'}}</van-button>
    </div>
  </div>
</template>

<script>
import { signInToday, me } from '@/api/user.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Me-User',
  data() {
    return {
      icon: '',
      nickname: '水院学子',
      username: 'gdldlzyjsxy',
      isSignIn: false,
      jwt: localStorage.getItem('authorization')
    }
  },
  methods: {
    onLogin() {
      this.$router.push('/login')
    },
    onRegister() {
      this.$router.push('/register')
    },
    setIcon(icon) {
      return getImage(icon)
    },
    onUserInfo() {
      this.$router.push('/userInfo')
    },
    onSignIn() {
      this.$router.push('/purse')
    }
  },
  created() {
    me()
      .then(res => {
        const result = res.data.success
        const data = res.data.data
        if (result) {
          this.icon = data.icon
          this.username = data.username
          this.nickname = data.nickname
        } else {
          const jwt = localStorage.getItem('authorization')
          if (jwt) {
            this.$notify({ type: 'warning', message: res.data.message })
            this.jwt = null
          }
          localStorage.removeItem('authorization')
        }
      })
      .catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    signInToday().then(res => {
      const data = res.data
      if (data.success) {
        this.isSignIn = true
      }
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  padding: 0 10px;
  padding-top: 10px;
  height: 74px;
  display: flex;
  background-color: transparent;
  padding-bottom: 10px;
  .icon {
    flex: 2;
  }
  .accountInfo {
    flex: 5;
    margin-left: 15px;
    div {
      line-height: 30px;
    }
    .nickname {
      width: 180px;
      padding-top: 14px;
      font-size: 18px;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
      span{
        padding: 0 5px;
        font-size: 18px;
      }
    }
    .account {
      width: 180px;
      font-size: 14px;
      color: #686868;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
    }
    .login {
      line-height: 70px;
      font-size: 18px;
    }
  }
  .signIn{
    flex: 3;
    line-height: 85px;
    /deep/.van-button{
      width: 95px;
      height: 28px;
      padding: 0 10px;

      border: transparent;

      background-image: linear-gradient(to right, #F7971E 0%, #FFD200  51%, #F7971E  100%);
      text-align: center;
      text-transform: uppercase;
      transition: 0.5s;
      background-size: 200% auto;
      color: white;
      box-shadow: 0 0 20px #eee;
      border-radius: 10px;

      &:hover {
        background-position: right center; /* change the direction of the change here */
        color: #fff;
        text-decoration: none;
      }
    }
  }
}
</style>
