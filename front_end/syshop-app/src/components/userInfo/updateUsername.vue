<template>
  <div class="containerx">
    <van-nav-bar title="修改用户名" right-text="确定" left-arrow @click-left="onClickLeft" @click-right="onClickRight" :fixed="true" :placeholder="true" />
    <div class="content">
      <input class="username" type="text" v-model="username" @input="clearEnver" placeholder="请输入用户名">
      <span class="clear" v-show="clearStatus">
        <van-icon name="clear" @click="clearClick" />
      </span>
    </div>
    <div class="text">
      <span>{{info}}</span>
    </div>
  </div>
</template>

<script>
import { checkIsNotUpdateUsername, updateUsername } from '@/api/user.js'
export default {
  name: 'Setting-UpdateUsername',
  data() {
    return {
      username: '',
      clearStatus: false,
      info: '当前账号只剩1次修改用户名的机会了,要考虑清楚再确定哦'
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onClickRight() {
      updateUsername(this.username).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
        } else {
          this.$notify({ type: 'success', message: data.data })
          this.$router.replace('/me')
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    clearEnver() {
      this.clearStatus = false
      if (this.username !== '') {
        this.clearStatus = true
      }
    },
    clearClick() {
      this.username = ''
      this.clearStatus = false
    }
  },
  created() {
    this.username = this.$route.params.username
    checkIsNotUpdateUsername().then(res => {
      const data = res.data
      if (!data.success) {
        this.info = '你已经修改过用户名啦！无法再修改了哦'
      }
    }).catch(err => {
      this.notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>
.containerx {
  width: 100%;
  input {
    width: 90%;
    height: 30px;
    border: transparent;
    background-color: #efefef;
  }
  .content {
    padding: 0 15px;
    position: relative;
    border-bottom: 1px solid #e3e3e3;
    margin-bottom: 5px;
    .username {
      width: 90%;
    }
    .clear {
      width: 10%;
      position: absolute;
      right: 0;
      top: 5px;
      /deep/.van-icon {
        color: #c3c3c3;
        font-size: 20px;
      }
    }
  }
  .text {
    padding: 0 15px;
    span {
      font-size: 12px;
      color: #c8c8c8;
    }
  }
}
</style>
