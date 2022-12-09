<template>
  <div class="containerx">
    <van-nav-bar title="修改名字" right-text="确定" left-arrow @click-left="onClickLeft" @click-right="onClickRight" :fixed="true" :placeholder="true" />
    <div class="content">
      <input class="name" type="text" v-model="name" @input="clearEnver" placeholder="请输入名字">
      <span class="clear" v-show="clearStatus">
        <van-icon name="clear" @click="clearClick" />
      </span>
    </div>
    <div class="text">
      <span>大声地告诉这个世界，你的名字</span>
    </div>
  </div>
</template>

<script>
import { updateUserInfo } from '@/api/user.js'
export default {
  name: 'Setting-UpdateUame',
  data() {
    return {
      name: '',
      clearStatus: false
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onClickRight() {
      updateUserInfo(this.name, null, null, null, null, null).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
        } else {
          this.$notify({ type: 'success', message: '名字修改成功' })
          this.$router.back()
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    clearEnver() {
      this.clearStatus = false
      if (this.name !== '') {
        this.clearStatus = true
      }
    },
    clearClick() {
      this.name = ''
      this.clearStatus = false
    }
  },
  created() {
    this.name = this.$route.params.name
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
    .name {
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
