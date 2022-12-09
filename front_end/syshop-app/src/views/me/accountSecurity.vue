<template>
  <div class="containerx">
    <van-nav-bar title="账户安全" left-arrow @click-left="onClickLeft" :fixed="true" :placeholder="true" />
    <van-cell-group>
      <van-cell border title="修改绑定手机号" label="若手机号更换请尽快进行修改" @click="goAuth('updatePhone')" :value="phone" is-link center/>
      <van-cell border title="修改登录密码" label="建议您定期更改密码以保护账户安全" @click="goAuth('updatePwd')" is-link center/>
    </van-cell-group>
  </div>
</template>

<script>
import { me } from '@/api/user.js'
export default {
  name: 'AccountSecurity',
  data() {
    return {
      phone: ''
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    goAuth(url) {
      this.$toast.loading({
        message: '加载中...',
        forbidClick: true
      })
      setTimeout(() => {
        this.$router.push(`/${url}`)
      }, 1500)
    }
  },
  created() {
    me().then(res => {
      const data = res.data
      if (data.success) {
        const array = data.data.phone.slice()
        for (let i = 0; i < array.length; i++) {
          if (i > 2 && i < 7) {
            this.phone += '*'
            continue
          }
          this.phone += array[i]
        }
      }
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>

</style>
