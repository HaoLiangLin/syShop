<template>
  <div class="container">
    <van-nav-bar title="修改绑定手机号" left-arrow @click-left="onClickLeft" :fixed="true" :placeholder="true" />
    <van-cell-group>
      <van-field v-model="phone" type="tel" label="手机号" placeholder="请输入手机号码" />
      <van-field
        v-model="code"
        center
        clearable
        label="验证码"
        placeholder="请输入验证码"
      >
        <template #button>
          <van-button size="small" type="primary" @click="sendCode" v-if="!isSend">{{codeMsg}}</van-button>
          <van-button size="small" disabled type="primary" v-if="isSend">{{codeMsg}}</van-button>
        </template>
      </van-field>
    </van-cell-group>
    <div class="btn">
      <van-button type="primary" round @click="onUpdate" :disabled="code == ''">确认修改</van-button>
    </div>
  </div>
</template>

<script>
import { sendUpdatePhoneCode, updatePhone } from '@/api/user.js'
export default {
  name: 'UpdatePhone',
  data() {
    return {
      phone: '',
      code: '',
      codeMsg: '获取验证码',
      isSend: false
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    sendCode() {
      if (!this.phone) {
        this.$toast('手机号不能为空')
        return
      }
      this.code = ''
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

      sendUpdatePhoneCode(this.phone).then(res => {
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
    },
    onUpdate() {
      if (this.code) {
        updatePhone(this.phone, this.code).then(res => {
          const toast = this.$toast.loading({
            message: '请稍候...',
            forbidClick: true
          })
          setTimeout(() => {
            toast.clear()
            const data = res.data
            if (!data.success) {
              this.$notify({ type: 'warning', message: data.message })
            } else {
              this.$notify({ type: 'success', message: data.message })
              this.$router.back()
            }
          }, 1500)
        }).catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
      }
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  text-align: center;
  .btn{
    /deep/.van-button {
      margin-top: 20px;
      width: 90%;
    }
  }
}
</style>
