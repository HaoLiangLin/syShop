<template>
  <div class="container">
    <van-tabbar :fixed="true" :border="false" :route="true" active-color="red" v-model="active">
      <van-tabbar-item icon="home-o" :to="{name:'home'}">主页</van-tabbar-item>
      <van-tabbar-item icon="apps-o" :to="{name: 'category'}">分类</van-tabbar-item>
      <van-tabbar-item icon="shopping-cart-o" :to="{name: 'shoppingCart'}">购物车</van-tabbar-item>
      <van-tabbar-item icon="user-o" :to="{name:'me'}">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
export default {
  name: 'Tabbar',
  data() {
    return {
      active: 0
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  margin-top: 50px;
}
</style>
