<template>
  <div class="container">
    <van-nav-bar
      title="设置新密码"
      left-arrow
      @click-left="onClickLeft"
    />
    <van-field v-model="password" label="新密码" />
    <van-button type="primary" block @click="onCommit">确定</van-button>
  </div>
</template>

<script>
import { updatePassword } from '@/api/login.js'
export default {
  name: 'ForgetPwd-UpdatePwd',
  data() {
    return {
      phone: '',
      password: ''
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onCommit() {
      updatePassword(this.phone, this.password).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
          return
        }
        this.$toast.success(data.message)
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    }
  },
  created() {
    this.phone = this.$route.params.phone
  }
}
</script>

<style lang="less" scoped>
.container{
  /deep/.van-button{
    width: 90%;
    margin: 0 auto;
    margin-top: 30px;
  }
}
</style>
