<template>
  <div class="container">
    <van-nav-bar title="修改登录密码" left-arrow @click-left="onClickLeft" :fixed="true" :placeholder="true" />
    <van-cell-group>
      <van-field v-model="oldPwd" label="旧密码" placeholder="请输入旧密码" />
    </van-cell-group>
    <van-cell-group>
      <van-field v-model="newPwd" label="新密码" placeholder="请输入新密码" />
    </van-cell-group>
    <van-cell-group>
      <van-field v-model="newPwd2" label="确认密码" placeholder="确认新密码" />
    </van-cell-group>
    <van-button type="primary" round @click="onUpdate">确认修改</van-button>
  </div>
</template>

<script>
import { updatePassword } from '@/api/user.js'
export default {
  name: 'UpdatePassword',
  data() {
    return {
      oldPwd: '',
      newPwd: '',
      newPwd2: ''
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onUpdate() {
      if (this.newPwd === this.newPwd2) {
        updatePassword(this.newPwd, this.oldPwd).then(res => {
          const data = res.data
          if (!data.success) {
            this.$notify({ type: 'warning', message: data.message })
          } else {
            this.$toast.success(data.message)
            this.$router.replace('/me')
          }
        })
        return
      }
      this.$toast('请确保密码一致')
    }
  }
}
</script>

<style lang="less" scoped>
.container {
  text-align: center;
  /deep/.van-button {
    margin-top: 20px;
    width: 90%;
  }
}
</style>
