<template>
  <div class="container">
    <van-nav-bar
      title="手机验证"
      left-arrow
      @click-left="onClickLeft"
    />
    <van-field v-model="phone" type="tel" label="手机号" placeholder="请输入手机号"/>
    <van-field
      v-model="code"
      center
      clearable
      label="验证码"
      placeholder="请输入验证码"
    >
      <template #button>
        <van-button size="small" @click="sendCode" type="primary" v-if="!isSend">{{codeMsg}}</van-button>
        <van-button size="small" disabled type="primary" v-if="isSend">{{codeMsg}}</van-button>
      </template>
    </van-field>
    <div class="checkBox">
      <div class="check">
        <span class="tipsText" v-show="tipsTextStatus">{{ tipsText }}</span>
        <van-slider v-model="value" @change="onChange" @input="onInput" :disabled="checkBtnStatus" bar-height="41px" inactive-color="#d9d9d9" active-color="LimeGreen">
          <template #button>
            <van-button :loading="checkBtnLoading" type="default">
              <van-icon :name="checkBtnIcon" size="18px" />
            </van-button>
          </template>
        </van-slider>
      </div>
    </div>
  </div>
</template>

<script>
import { sendForgetPasswordCode, checkForgetPasswordCode } from '@/api/login.js'
export default {
  name: 'ForgetPwd-CheckPhone',
  data() {
    return {
      phone: '',
      code: '',
      value: 0,
      codeMsg: '获取验证码',
      isSend: false,
      // 请求是否成功
      requestStatus: false,
      // 提示文字显示与否
      tipsTextStatus: true,
      // 提示文字
      tipsText: '向右滑动验证',
      // 滑块状态
      checkBtnStatus: false,
      // 滑块加载状态开启与否
      checkBtnLoading: false,
      // 滑块显示图标
      checkBtnIcon: 'arrow'
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

      sendForgetPasswordCode(this.phone)
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
    onChange() {
      if (this.value <= 99) {
        this.tipsTextStatus = true
        this.value = 0
      } else {
        this.value = 100
        // 发送请求
        if (this.phone && this.code) {
          checkForgetPasswordCode(this.phone, this.code)
            .then(res => {
              const data = res.data
              if (data.success) {
                this.requestStatus = true
              }
            }).catch(err => {
              this.$toast(err.message)
            })
        }
        this.checkBtnStatus = true
        this.checkBtnLoading = true
        this.tipsTextStatus = true
        this.tipsText = '请稍候...'
        this.onCheck()
      }
    },
    onCheck() {
      setTimeout(() => {
        this.checkBtnStatus = false
        this.checkBtnLoading = false
        // 成功跳转
        if (this.requestStatus) {
          this.checkBtnIcon = 'success'
          this.tipsText = '验证通过'
          this.$router.push(`/forgetPwd/updatePwd/${this.phone}`)
          return
        }
        this.value = 0
        this.tipsTextStatus = true
        this.tipsText = '向右滑动验证'
        this.checkBtnIcon = 'arrow'
        // 失败提示
        this.$toast('验证未通过')
      }, 2000)
    },
    onInput() {
      if (this.value < 99) {
        this.tipsTextStatus = false
        return
      }
      this.tipsTextStatus = true
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  .checkBox {
    height: 40px;
    background-color: #efefef;
    padding: 15px 30px;

    .check {
      position: relative;
      /deep/.van-slider,
      .van-slider__bar {
        border-radius: 0;
      }
      /deep/.van-slider__button-wrapper {
        z-index: 2;
      }
      .tipsText {
        color: #fff;
        font-size: 14px;
        position: absolute;
        z-index: 1;
        top: 9px;
        left: 34%;
      }
    }
  }
}
</style>
