<template>
  <div class="container">
    我的
    <div class="func">
      <span>
        <van-icon @click="onSetting" name="setting-o" size="19px" />
      </span>
      <span>
        <van-icon @click="onUndeveloped" name="chat-o" size="19px" />
      </span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Me-Header',
  methods: {
    onSetting() {
      this.$router.push('/setting')
    },
    onUndeveloped() {
      this.$toast('功能未开发')
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  height: 6vh;
  line-height: 6vh;
  text-align: center;
  font-size: 16px;
  position: relative;

  background-color: transparent;
  letter-spacing: 2px;

  .func{
    position: absolute;
    top: 2px;
    right: 0;
    span{
      margin: 10px;
      right: 0;
    }
  }
}
</style>
